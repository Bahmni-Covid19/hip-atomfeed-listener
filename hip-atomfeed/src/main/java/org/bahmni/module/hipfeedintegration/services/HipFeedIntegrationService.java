package org.bahmni.module.hipfeedintegration.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


@Component
public class HipFeedIntegrationService {

    @Autowired
    private OpenMRSService openMRSService;

    @Autowired
    private HipService hipService;

    private Map<String, OpenMRSPatient> careContextCache = new HashMap<>();

    public void processEncounter(OpenMRSEncounter openMRSEncounter) throws IOException, ParseException {
        OpenMRSPatient patientCareContext = new OpenMRSPatient();
        if(!careContextCache.containsKey(openMRSEncounter.getVisitUuid())) {
            patientCareContext = openMRSService.getCareContext(openMRSEncounter.getPatientUuid(), openMRSEncounter.getVisitUuid());
            careContextCache.put(openMRSEncounter.getVisitUuid(), patientCareContext);
        }
        callNewContext(patientCareContext);
        callSmsNotify(patientCareContext);
    }

    private void callNewContext(OpenMRSPatient patientCareContext) throws IOException {
        if(patientCareContext != null && !patientCareContext.getHealthId().equals("null") && patientCareContext.getHealthId() != null) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(patientCareContext.getCareContexts());
            String jsonInputString = "{\"patientReferenceNumber\": \"" + patientCareContext.getPatientReferenceNumber() +
                    "\",\n \"patientName\":\"" + patientCareContext.getGivenName() +
                    "\",\n\"careContexts\" : " + jsonString +
                    ",\n\"healthId\" : \"" + patientCareContext.getHealthId() + "\"}";
            hipService.callNewContext(jsonInputString);
        }
    }


    private void callSmsNotify(OpenMRSPatient patientCareContext) throws IOException {
        if(patientCareContext != null && patientCareContext.getPhoneNumber() != null && !patientCareContext.getPhoneNumber().equals(" ")) {
            String jsonInputString = "{\"phoneNo\": \"" + patientCareContext.getPhoneNumber() + "\",\n \"receiverName\":\"" + patientCareContext.getGivenName() + "\",\n\"careContextInfo\" : \"" + patientCareContext.getCareContextInfo() + "\"}";

            hipService.smsNotify(jsonInputString);
        }
    }

}
