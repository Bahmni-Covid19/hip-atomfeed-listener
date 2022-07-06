package org.bahmni.module.hipfeedintegration.services;

import org.bahmni.module.hipfeedintegration.atomfeed.builders.OpenMRSEncounterBuilder;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.patient.CareContext;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HipFeedIntegrationServiceTest {

    @Mock
    OpenMRSService openMRSService;

    @Mock
    HipService hipService;

    @Mock
    OpenMRSPatient openMRSPatient;

    @InjectMocks
    HipFeedIntegrationService hipFeedIntegrationService = new HipFeedIntegrationService();

    String PATIENT_UUID = "105059a8-5226-4b1f-b512-0d3ae685287d";

    @Test
    public void shouldCallNewContextApi() throws IOException, ParseException {

        OpenMRSEncounter encounter = buildEncounter();

        when(openMRSService.getCareContext(PATIENT_UUID)).thenReturn(openMRSPatient);
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(openMRSPatient);
        when(openMRSPatient.getCareContextInfo()).thenReturn("OPD");
        when(openMRSPatient.getGivenName()).thenReturn("Patient Name");
        when(openMRSPatient.getHealthId()).thenReturn("abc@sbx");
        when(openMRSPatient.getPatientReferenceNumber()).thenReturn("123");
        when(openMRSPatient.getCareContextInfo()).thenReturn("OPD");

        hipFeedIntegrationService.processEncounter(encounter);

        String object = "{\"patientReferenceNumber\": \"123\",\n" +
                " \"patientName\":\"Patient Name\",\n" +
                "\"careContexts\" : [ ],\n" +
                "\"healthId\" : \"abc@sbx\"}";

        verify(hipService,times(1)).callNewContext(object);
    }

    @Test
    public void shouldCallSmsNotify() throws IOException, ParseException {

        OpenMRSPatient patient = buildPatient();
        OpenMRSEncounter encounter = buildEncounter();

        when(openMRSService.getCareContext(PATIENT_UUID)).thenReturn(patient);
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(patient);

        hipFeedIntegrationService.processEncounter(encounter);

        String object = "{\"phoneNo\": \"9999999999\",\n" +
                " \"receiverName\":\"Patient Name\",\n" +
                "\"careContextInfo\" : \"OPD 1 record\"}";

        verify(hipService,times(1)).smsNotify(object);
    }



    OpenMRSEncounter buildEncounter() {
        return new OpenMRSEncounterBuilder().withPatientUuid(PATIENT_UUID).build();
    }

    OpenMRSPatient buildPatient() {
        OpenMRSPatient openMRSPatient = new OpenMRSPatient();
        openMRSPatient.setGivenName("Patient Name");
        openMRSPatient.setPhoneNumber("9999999999");
        openMRSPatient.setHealthId("abcd@sbx");
        openMRSPatient.setPatientReferenceNumber("123");
        openMRSPatient.setCareContexts(new ArrayList<CareContext>(Arrays.asList(buildCareContext())));
        return openMRSPatient;
    }

    CareContext buildCareContext() {
        CareContext careContext = new CareContext();
        careContext.setDisplay("OPD 1");
        careContext.setReferenceNumber(1);
        careContext.setType("OPD");
        return careContext;
    }
}