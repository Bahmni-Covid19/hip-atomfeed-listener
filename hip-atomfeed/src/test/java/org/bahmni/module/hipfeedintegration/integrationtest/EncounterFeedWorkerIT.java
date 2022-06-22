package org.bahmni.module.hipfeedintegration.integrationtest;

import org.bahmni.module.HipFeedListener;
import org.bahmni.module.hipfeedintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.hipfeedintegration.atomfeed.worker.EncounterFeedWorker;
import org.bahmni.module.hipfeedintegration.services.HipFeedIntegrationService;
import org.bahmni.module.hipfeedintegration.services.HipService;
import org.bahmni.module.hipfeedintegration.services.OpenMRSService;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HipFeedListener.class)
public class EncounterFeedWorkerIT extends OpenMRSMapperBaseTest{

    @InjectMocks
    @Autowired
    EncounterFeedWorker encounterFeedWorker;

    @Mock
    private OpenMRSService openMRSService;

    @Mock
    private HipService hipService;

    @InjectMocks
    @Autowired
    private HipFeedIntegrationService hipFeedIntegrationService;

    String PATIENT_UUID = "105059a8-5226-4b1f-b512-0d3ae685287d";


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_ENCOUNTERS_TO_BE_IGNORED)).thenReturn(new ArrayList<String>(Arrays.asList("Reg")));
        when(openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_CONCEPTS_TO_BE_IGNORED)).thenReturn(new ArrayList<String>(Arrays.asList("concept in ignored list")));
        when(openMRSService.getValueFromGlobalProperty(OPENMRS_PROPERTY_FORM_FIELDS_TO_BE_IGNORED)).thenReturn(new ArrayList<String>(Arrays.asList("formField in ignored list")));
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(openMRSService.getCareContext(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
    }

    @Test
    public void shouldProcessEncounter() throws IOException{

        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setEncounterType("Consultation");
        openMRSEncounter.setPatientUuid(PATIENT_UUID);
        when(openMRSService.getEncounter("/encounter/1")).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", "/encounter/1"));

        verify(hipService,times(1)).callNewContext(anyString());
        verify(hipService,times(1)).smsNotify(anyString());
    }

    @Test
    public void shouldNotProcessEncounter() throws IOException {

        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setEncounterType("Reg");
        openMRSEncounter.setPatientUuid(PATIENT_UUID);
        when(openMRSService.getEncounter("/encounter/1")).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", "/encounter/1"));

        verify(hipService,times(0)).callNewContext(anyString());
        verify(hipService,times(0)).smsNotify(anyString());

    }
}
