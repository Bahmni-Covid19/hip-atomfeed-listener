package org.bahmni.module.pacsintegration.atomfeed.worker;

import org.bahmni.module.pacsintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptName;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSObs;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.bahmni.module.pacsintegration.services.PacsIntegrationService;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bahmni.module.pacsintegration.atomfeed.client.Constants.OPENMRS_PROPERTY_CONCEPTS_TO_BE_IGNORED;
import static org.bahmni.module.pacsintegration.atomfeed.client.Constants.OPENMRS_PROPERTY_ENCOUNTERS_TO_BE_IGNORED;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EncounterFeedWorkerTest extends OpenMRSMapperBaseTest {

    @Mock
    private PacsIntegrationService pacsIntegrationService;

    @Mock
    private OpenMRSService openMRSService;

    @InjectMocks
    private EncounterFeedWorker encounterFeedWorker = new EncounterFeedWorker();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetEncounterDataFromTheEventContentAndSaveIt() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSOrder order = new OpenMRSOrder();
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.addTestOrder(order);
        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(pacsIntegrationService, times(1)).processEncounter(openMRSEncounter);
    }

    @Test
    public void shouldNotProcessEncounterIfItIsInIgnoredList() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setEncounterType("Reg");
        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);
        when(openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_ENCOUNTERS_TO_BE_IGNORED)).thenReturn(new ArrayList<String>(Arrays.asList("Reg")));

        encounterFeedWorker.process(new Event("event id", content));

        verify(pacsIntegrationService, times(0)).processEncounter(openMRSEncounter);
    }

    @Test
    public void shouldProcessEncounterIfAtLeastOneObservationIsNotInIgnoredList() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        OpenMRSConcept concept1 = new OpenMRSConcept("conceptUuid2",new OpenMRSConceptName("concept in ignored list"),false);
        OpenMRSConcept concept2 = new OpenMRSConcept("conceptUuid1",new OpenMRSConceptName("concept not in ignored list"),false);

        List<OpenMRSObs> observations = new ArrayList<OpenMRSObs>();
        observations.add(new OpenMRSObs("uuid123",concept1));
        observations.add(new OpenMRSObs("uuid162",concept2));

        openMRSEncounter.setObservations(observations);

        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);
        when(openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_ENCOUNTERS_TO_BE_IGNORED)).thenReturn(new ArrayList<String>(Arrays.asList("Reg")));
        when(openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_CONCEPTS_TO_BE_IGNORED)).thenReturn(new ArrayList<String>(Arrays.asList("concept in ignored list")));

        encounterFeedWorker.process(new Event("event id", content));

        verify(pacsIntegrationService, times(1)).processEncounter(openMRSEncounter);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfJsonParseFails() throws Exception {
        String content = "something";
        when(openMRSService.getEncounter(content)).thenThrow(new IOException("Incorrect JSON"));

        encounterFeedWorker.process(new Event("event id", content));
    }

    @Test
    public void shouldFilterOutBedAssignmentEventsBeforeProcessing() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSOrder order = new OpenMRSOrder();
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.addTestOrder(order);

        encounterFeedWorker.process(new Event("event id", content, "Bed-Assignment"));

        verify(pacsIntegrationService, times(0)).processEncounter(openMRSEncounter);
    }
}