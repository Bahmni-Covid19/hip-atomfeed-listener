package org.bahmni.module.hipfeedintegration.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.hipfeedintegration.atomfeed.worker.EncounterFeedWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;


@Component
public class HipFeedIntegrationService {

    @Autowired
    private OpenMRSService openMRSService;

    @Autowired
    private HipService hipService;

    private static final Logger logger = LoggerFactory.getLogger(EncounterFeedWorker.class);

    public void processEncounter(OpenMRSEncounter openMRSEncounter) throws IOException, ParseException {
        OpenMRSPatient patient = openMRSService.getPatient(openMRSEncounter.getPatientUuid());
        OpenMRSPatient patientCareContext = openMRSService.getCareContext(openMRSEncounter.getPatientUuid());

        if(patientCareContext.getHealthId() != null) callNewContext(patient, patientCareContext);
        callSmsNotify(patient,patientCareContext);

    }

    private void callNewContext(OpenMRSPatient patient, OpenMRSPatient patientCareContext) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(patientCareContext.getCareContexts());

        String jsonInputString = "{\"patientReferenceNumber\": \"" + patientCareContext.getPatientReferenceNumber() +
                "\",\n \"patientName\":\"" + patient.getGivenName() +
                "\",\n\"careContexts\" : " + jsonString +
                ",\n\"healthId\" : \"" + patientCareContext.getHealthId() + "\"}";

       hipService.callNewContext(jsonInputString);
    }


    private void callSmsNotify(OpenMRSPatient patient,OpenMRSPatient patientCareContext) throws IOException {

        String jsonInputString = "{\"phoneNo\": \"" + patient.getPhoneNumber() + "\",\n \"receiverName\":\"" + patient.getGivenName() + "\",\n\"careContextInfo\" : \"" + patientCareContext.getCareContextInfo() + "\"}";

        hipService.smsNotify(jsonInputString);
    }

}
