package de.hahn.blog.trainstopstatus.view.beans;

import java.util.Map;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.TaskFlowContext;
import oracle.adf.controller.TaskFlowTrainModel;
import oracle.adf.controller.TaskFlowTrainStopModel;
import oracle.adf.controller.ViewPortContext;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.context.AdfFacesContext;


public class TrainStopBean {
    private static ADFLogger logger = ADFLogger.createADFLogger(TrainStopBean.class);

    public TrainStopBean() {
    }


    /**
     * This getter is used to layz init the status of train stops depending on the current stop, 
     * all previous stops are set to status visited
     * @return empty string as the component is hidden in the ui.
     */
    public String getInitStatus(){
        // get taskflow parameter from pageFlowScope
        // AdfFacesContext
        AdfFacesContext adfFacesCtx = AdfFacesContext.getCurrentInstance();
        // get the PageFlowScope Params
        Map<String, Object> scopePageFlowScopeVar = adfFacesCtx.getPageFlowScope();
        Integer i = (Integer)scopePageFlowScopeVar.get("startview");
        if (i == null || i <= 0) {
            logger.info("Information: train stops already initialized");
            return "";
        }
        scopePageFlowScopeVar.put("startview",new Integer(0));
        
        logger.info("Information: getInitStatus called!");
        ControllerContext controllerContext = ControllerContext.getInstance();
        ViewPortContext currentViewPortCtx = controllerContext.getCurrentViewPort();
        TaskFlowContext taskFlowCtx = currentViewPortCtx.getTaskFlowContext();
        TaskFlowTrainModel taskFlowTrainModel = taskFlowCtx.getTaskFlowTrainModel();
                
        // get the stop from the map
        TaskFlowTrainStopModel currentStop = taskFlowTrainModel.getCurrentStop();
        logger.info("Information:" + currentStop);

        // mark all stops before this one as visiited
        TaskFlowTrainStopModel prevStop = taskFlowTrainModel.getPreviousStop(currentStop);
        // get all previous stops and mark them visited
        // if prevStop is null it's the first stop!
        while (prevStop != null) {
            //CAUTATION Using adfinternal classes should be avoided!
            oracle.adfinternal.controller.train.TrainStopModel ts = (oracle.adfinternal.controller.train.TrainStopModel) prevStop;
            // set the stop to visited
            ts.setVisited(true);
            prevStop = taskFlowTrainModel.getPreviousStop(prevStop);
        }
    
        // always return an empty string
        return "";
    }
}
