
package org.bahmni.module.pacsintegration.atomfeed.worker;

import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSObs;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.bahmni.module.pacsintegration.services.PacsIntegrationService;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

import static org.bahmni.module.pacsintegration.atomfeed.client.Constants.OPENMRS_PROPERTY_CONCEPTS_TO_BE_IGNORED;
import static org.bahmni.module.pacsintegration.atomfeed.client.Constants.OPENMRS_PROPERTY_ENCOUNTERS_TO_BE_IGNORED;

@Component
public class EncounterFeedWorker implements EventWorker {

    private static final Logger logger = LoggerFactory.getLogger(EncounterFeedWorker.class);
    public static List encounterToBeIgnored;
    public static List conceptsToBeIgnored;

    @Autowired
    private PacsIntegrationService pacsIntegrationService;

    @Autowired
    private OpenMRSService openMRSService;

    public EncounterFeedWorker() {
    }

    @Override
    public void process(Event event) {
        String bedAssignment = "Bed-Assignment";
        try {
            if (event.getTitle() == null || !event.getTitle().equals(bedAssignment)) {
                encounterToBeIgnored = (encounterToBeIgnored != null) ? encounterToBeIgnored : openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_ENCOUNTERS_TO_BE_IGNORED);
                conceptsToBeIgnored = (conceptsToBeIgnored != null) ? conceptsToBeIgnored : openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_CONCEPTS_TO_BE_IGNORED);
                logger.warn("Getting encounter data...");
                String encounterUri = event.getContent();
                OpenMRSEncounter encounter = openMRSService.getEncounter(encounterUri);
                if(!checkIfEncounterIgnored(encounter)){
                    pacsIntegrationService.processEncounter(encounter);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process encounter", e);
            throw new RuntimeException("Failed to process encounter", e);
        }
    }

    private boolean checkIfEncounterIgnored(OpenMRSEncounter encounter){
        if(encounterToBeIgnored.contains(encounter.getEncounterType()))
            return true;
        List<OpenMRSObs> observations = encounter.getObservations();
        boolean ignored = false;
        for (OpenMRSObs obs: observations) {
            if(conceptsToBeIgnored.contains(obs.getConcept().getName().getName()))
                ignored = true;
            else
                return false;
        }
        return ignored;
    }

    @Override
    public void cleanUp(Event event) {
    }
}
