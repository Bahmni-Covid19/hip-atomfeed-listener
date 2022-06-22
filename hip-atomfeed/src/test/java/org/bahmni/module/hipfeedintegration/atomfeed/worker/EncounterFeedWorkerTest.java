package org.bahmni.module.hipfeedintegration.atomfeed.worker;

import org.bahmni.module.hipfeedintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.hipfeedintegration.atomfeed.builders.OpenMRSConceptBuilder;
import org.bahmni.module.hipfeedintegration.atomfeed.builders.OpenMRSConceptNameBuilder;
import org.bahmni.module.hipfeedintegration.atomfeed.builders.OpenMRSEncounterBuilder;
import org.bahmni.module.hipfeedintegration.atomfeed.builders.OpenMRSObsBuilder;
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
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterBuilder().withEncounterType("Reg").build();

        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(hipFeedIntegrationService, times(0)).processEncounter(openMRSEncounter);
    }

    @Test
    public void shouldProcessEncounterIfAtLeastOneObservationIsNotInIgnoredList() throws Exception {
        String content = "/openmrs/encounter/uuid1";

        OpenMRSConcept concept1 = new OpenMRSConceptBuilder().withName(new OpenMRSConceptNameBuilder().withName("concept in ignored list").build()).build();
        OpenMRSConcept concept2 = new OpenMRSConceptBuilder().withName(new OpenMRSConceptNameBuilder().withName("concept not in ignored list").build()).build();


        List<OpenMRSObs> observations = new ArrayList<OpenMRSObs>();
        observations.add(new OpenMRSObsBuilder().withConcept(concept1).withGroupMembers(new ArrayList<OpenMRSObs>()).build());
        observations.add(new OpenMRSObsBuilder().withConcept(concept2).withGroupMembers(new ArrayList<OpenMRSObs>()).build());

        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterBuilder().withObservation(observations).build();

        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(hipFeedIntegrationService, times(1)).processEncounter(openMRSEncounter);
    }

    @Test
    public void shouldProcessEncounterIfAtLeastOneFormFieldInObservationFormIsNotInIgnoredList() throws Exception {
        String content = "/openmrs/encounter/uuid1";

        OpenMRSConcept concept = new OpenMRSConceptBuilder().withName(new OpenMRSConceptNameBuilder().withName("concept not in ignored list").build()).build();
        OpenMRSConcept formField1 = new OpenMRSConceptBuilder().withName(new OpenMRSConceptNameBuilder().withName("formField not in ignored list").build()).build();
        OpenMRSConcept formField2 = new OpenMRSConceptBuilder().withName(new OpenMRSConceptNameBuilder().withName("formField in ignored list").build()).build();

        List<OpenMRSObs> observations = new ArrayList<OpenMRSObs>();
        List<OpenMRSObs> formFields = new ArrayList<OpenMRSObs>();
        formFields.add(new OpenMRSObsBuilder().withConcept(formField1).withGroupMembers(new ArrayList<OpenMRSObs>()).build());
        formFields.add(new OpenMRSObsBuilder().withConcept(formField2).withGroupMembers(new ArrayList<OpenMRSObs>()).build());
        observations.add(new OpenMRSObsBuilder().withConcept(concept).withGroupMembers(formFields).build());

        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterBuilder().withObservation(observations).build();

        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(hipFeedIntegrationService, times(1)).processEncounter(openMRSEncounter);
    }

    @Test
    public void shouldNotProcessEncounterIfAllFormFieldInObservationFormIsInIgnoredListButConceptsIsNotInIgnoredList() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSConcept concept = new OpenMRSConceptBuilder().withName(new OpenMRSConceptNameBuilder().withName("concept not in ignored list").build()).build();
        OpenMRSConcept formField1 = new OpenMRSConceptBuilder().withName(new OpenMRSConceptNameBuilder().withName("formField in ignored list").build()).build();
        OpenMRSConcept formField2 = new OpenMRSConceptBuilder().withName(new OpenMRSConceptNameBuilder().withName("formField in ignored list").build()).build();

        List<OpenMRSObs> observations = new ArrayList<OpenMRSObs>();
        List<OpenMRSObs> formFields = new ArrayList<OpenMRSObs>();
        formFields.add(new OpenMRSObsBuilder().withConcept(formField1).withGroupMembers(new ArrayList<OpenMRSObs>()).build());
        formFields.add(new OpenMRSObsBuilder().withConcept(formField2).withGroupMembers(new ArrayList<OpenMRSObs>()).build());
        observations.add(new OpenMRSObsBuilder().withConcept(concept).withGroupMembers(formFields).build());

        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterBuilder().withObservation(observations).build();

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
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterBuilder().withTestOrder(order).build();

        encounterFeedWorker.process(new Event("event id", content, "Bed-Assignment"));

        verify(hipFeedIntegrationService, times(0)).processEncounter(openMRSEncounter);
    }
}