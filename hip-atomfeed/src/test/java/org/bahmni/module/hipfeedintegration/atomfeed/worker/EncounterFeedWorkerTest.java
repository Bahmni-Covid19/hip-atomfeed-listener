package org.bahmni.module.hipfeedintegration.atomfeed.worker;

import org.bahmni.module.hipfeedintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSConceptName;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSObs;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.hipfeedintegration.services.OpenMRSService;
import org.bahmni.module.hipfeedintegration.services.HipFeedIntegrationService;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.OPENMRS_PROPERTY_CONCEPTS_TO_BE_IGNORED;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.OPENMRS_PROPERTY_ENCOUNTERS_TO_BE_IGNORED;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.OPENMRS_PROPERTY_FORM_FIELDS_TO_BE_IGNORED;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EncounterFeedWorkerTest extends OpenMRSMapperBaseTest {

    @Mock
    private HipFeedIntegrationService hipFeedIntegrationService;

    @Mock
    private OpenMRSService openMRSService;

    @InjectMocks
    private EncounterFeedWorker encounterFeedWorker = new EncounterFeedWorker();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_ENCOUNTERS_TO_BE_IGNORED)).thenReturn(new ArrayList<String>(Arrays.asList("Reg")));
        when(openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_CONCEPTS_TO_BE_IGNORED)).thenReturn(new ArrayList<String>(Arrays.asList("concept in ignored list")));
        when(openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_FORM_FIELDS_TO_BE_IGNORED)).thenReturn(new ArrayList<String>(Arrays.asList("formField in ignored list")));

    }

    @Test
    public void shouldGetEncounterDataFromTheEventContentAndSaveIt() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSOrder order = new OpenMRSOrder();
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.addTestOrder(order);
        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(hipFeedIntegrationService, times(1)).processEncounter(openMRSEncounter);
    }

    @Test
    public void shouldNotProcessEncounterIfItIsInIgnoredList() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setEncounterType("Reg");
        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(hipFeedIntegrationService, times(0)).processEncounter(openMRSEncounter);
    }

    @Test
    public void shouldProcessEncounterIfAtLeastOneObservationIsNotInIgnoredList() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        OpenMRSConcept concept1 = new OpenMRSConcept("conceptUuid2",new OpenMRSConceptName("concept in ignored list"),false);
        OpenMRSConcept concept2 = new OpenMRSConcept("conceptUuid1",new OpenMRSConceptName("concept not in ignored list"),false);

        List<OpenMRSObs> observations = new ArrayList<OpenMRSObs>();
        observations.add(new OpenMRSObs("uuid123",concept1, new ArrayList<OpenMRSObs>()));
        observations.add(new OpenMRSObs("uuid162",concept2, new ArrayList<OpenMRSObs>()));

        openMRSEncounter.setObservations(observations);

        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(hipFeedIntegrationService, times(1)).processEncounter(openMRSEncounter);
    }

    @Test
    public void shouldProcessEncounterIfAtLeastOneFormFieldInObservationFormIsNotInIgnoredList() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        OpenMRSConcept concept = new OpenMRSConcept("conceptUuid1",new OpenMRSConceptName("concept not in ignored list"),false);
        OpenMRSConcept formField1 = new OpenMRSConcept("conceptUuid2",new OpenMRSConceptName("formField not in ignored list"),false);
        OpenMRSConcept formField2 = new OpenMRSConcept("conceptUuid3",new OpenMRSConceptName("formField in ignored list"),false);

        List<OpenMRSObs> observations = new ArrayList<OpenMRSObs>();
        List<OpenMRSObs> formFields = new ArrayList<OpenMRSObs>();
        formFields.add(new OpenMRSObs("uuid",formField1,new ArrayList<OpenMRSObs>()));
        formFields.add(new OpenMRSObs("uuid",formField2,new ArrayList<OpenMRSObs>()));

        observations.add(new OpenMRSObs("uuid123",concept, formFields));

        openMRSEncounter.setObservations(observations);

        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(hipFeedIntegrationService, times(1)).processEncounter(openMRSEncounter);
    }

    @Test
    public void shouldNotProcessEncounterIfAllFormFieldInObservationFormIsInIgnoredListButConceptsIsNotInIgnoredList() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        OpenMRSConcept concept = new OpenMRSConcept("conceptUuid1",new OpenMRSConceptName("concept not in ignored list"),false);
        OpenMRSConcept formField1 = new OpenMRSConcept("conceptUuid2",new OpenMRSConceptName("formField in ignored list"),false);
        OpenMRSConcept formField2 = new OpenMRSConcept("conceptUuid3",new OpenMRSConceptName("formField in ignored list"),false);

        List<OpenMRSObs> observations = new ArrayList<OpenMRSObs>();
        List<OpenMRSObs> formFields = new ArrayList<OpenMRSObs>();
        formFields.add(new OpenMRSObs("uuid",formField1,new ArrayList<OpenMRSObs>()));
        formFields.add(new OpenMRSObs("uuid",formField2,new ArrayList<OpenMRSObs>()));

        observations.add(new OpenMRSObs("uuid123",concept, formFields));

        openMRSEncounter.setObservations(observations);

        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(hipFeedIntegrationService, times(0)).processEncounter(openMRSEncounter);
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

        verify(hipFeedIntegrationService, times(0)).processEncounter(openMRSEncounter);
    }
}