package de.hahn.blog.trainstopstatus.view.beans;

import java.util.Map;
import java.util.Set;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.TaskFlowContext;
import oracle.adf.controller.TaskFlowTrainModel;
import oracle.adf.controller.TaskFlowTrainStopModel;
import oracle.adf.controller.ViewPortContext;
import oracle.adf.controller.metadata.ActivityId;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.context.AdfFacesContext;


public class TrainStopBean {
    private static ADFLogger logger = ADFLogger.createADFLogger(TrainStopBean.class);

    public TrainStopBean() {
    }

    public void onInitTrain() {
        ControllerContext controllerContext = ControllerContext.getInstance();
        ViewPortContext currentViewPortCtx = controllerContext.getCurrentViewPort();
        TaskFlowContext taskFlowCtx = currentViewPortCtx.getTaskFlowContext();
        TaskFlowTrainModel taskFlowTrainModel = taskFlowCtx.getTaskFlowTrainModel();
        
        //CAUTATION Using adfinternal classes should be avoided!
        oracle.adfinternal.controller.train.TrainModel trainModel = (oracle.adfinternal.controller.train.TrainModel) taskFlowTrainModel;

        // get taskflow parameter from pageFlowScope
        // AdfFacesContext
        AdfFacesContext adfFacesCtx = AdfFacesContext.getCurrentInstance();
        // get the PageFlowScope Params
        Map<String, Object> scopePageFlowScopeVar = adfFacesCtx.getPageFlowScope();
        Integer i = (Integer)scopePageFlowScopeVar.get("startview");
        if (i == null || i < 0) {
            logger.info("Information: No stop set -> Nothing to do");
            return;
        }
        
        // get the trainstop from the map of all stops via the ActivityId
        Map<ActivityId, oracle.adfinternal.controller.train.TrainStopModel> mapTrainStops = trainModel.getTrainStops();
        Set<ActivityId> keySet = mapTrainStops.keySet();
        Object[] array = keySet.toArray();
        if (i > array.length) {
            // if the requested stop is bigger than the available stops in the map do nothing but write an message to the log
            logger.info("Information: no stop with number " + i);;
            return;
        }
        
        // get the stop from the map
        TaskFlowTrainStopModel currentStop = mapTrainStops.get(array[i-1]);
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
    }
}
