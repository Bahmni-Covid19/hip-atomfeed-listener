package org.bahmni.module.hipfeedintegration.atomfeed.jobs;

import org.bahmni.module.hipfeedintegration.atomfeed.client.AtomFeedClientFactory;
import org.bahmni.module.hipfeedintegration.atomfeed.worker.EncounterFeedWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.quartz.DisallowConcurrentExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@DisallowConcurrentExecution
@Component("openMRSEncounterFeedJob")
@ConditionalOnExpression("'${enable.scheduling}'=='true'")
public class EncounterFeedJob implements FeedJob {
    private final Logger logger = LoggerFactory.getLogger(EncounterFeedJob.class);
    private FeedClient atomFeedClient;
    private EncounterFeedWorker encounterFeedWorker;
    private AtomFeedClientFactory atomFeedClientFactory;

    @Autowired
    public EncounterFeedJob(EncounterFeedWorker encounterFeedWorker, AtomFeedClientFactory atomFeedClientFactory) {
        this.encounterFeedWorker = encounterFeedWorker;
        this.atomFeedClientFactory = atomFeedClientFactory;
    }

    public EncounterFeedJob() {
    }

    @Override
    public void process() {
        if(atomFeedClient == null){
            atomFeedClient = atomFeedClientFactory.get(encounterFeedWorker);
        }
        logger.info("atomFeedClient........." + atomFeedClient.toString());
        logger.info("Processing feed...");
        atomFeedClient.processEvents();
        logger.info("Completed processing feed...");
    }
}
