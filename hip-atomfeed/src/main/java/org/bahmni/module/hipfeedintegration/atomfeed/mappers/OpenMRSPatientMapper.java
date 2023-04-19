package org.bahmni.module.hipfeedintegration.atomfeed.mappers;

import org.bahmni.module.hipfeedintegration.atomfeed.contract.patient.CareContext;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.hipfeedintegration.atomfeed.worker.EncounterFeedWorker;
import org.bahmni.webclients.ObjectMapperRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.ATTRIBUTES;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.ATTRIBUTE_TYPE;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.BIRTH_DATE;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.CARE_CONTEXTS;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.CARE_CONTEXT_NAME;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.CARE_CONTEXT_REFERENCE;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.DISPLAY;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.FAMILY_NAME;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.GENDER;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.GIVEN_NAME;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.HEALTH_ID;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.IDENTIFIER;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.IDENTIFIERS;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.MIDDLE_NAME;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.MOBILE;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.PATIENT_NAME;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.PATIENT_REFERENCE_NUMBER;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.PERSON;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.PHONE_NUMBER;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.PHONE_NUMBER_ATTRIBUTE;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.PREFERRED_NAME;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.VALUE;


public class OpenMRSPatientMapper {
    private ObjectMapper objectMapper;
    private SimpleDateFormat dateOfBirthFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private Logger logger = LoggerFactory.getLogger(EncounterFeedWorker.class);

    public OpenMRSPatientMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    public OpenMRSPatient map(String patientJSON) throws IOException, ParseException {
        OpenMRSPatient patient = new OpenMRSPatient();
        JsonNode jsonNode = objectMapper.readTree(patientJSON);

        patient.setPatientId(jsonNode.path(IDENTIFIERS).get(0).path(IDENTIFIER).asText());
        patient.setGivenName(jsonNode.path(PERSON).path(PREFERRED_NAME).path(GIVEN_NAME).asText().replaceAll("[\\W&&[^-]]", " "));
        patient.setFamilyName(jsonNode.path(PERSON).path(PREFERRED_NAME).path(FAMILY_NAME).asText().replaceAll("[\\W&&[^-]]", " "));
        patient.setMiddleName(jsonNode.path(PERSON).path(PREFERRED_NAME).path(MIDDLE_NAME).asText().replaceAll("[\\W&&[^-]]", " "));
        patient.setGender(jsonNode.path(PERSON).path(GENDER).asText());
        patient.setBirthDate(dateOfBirthFormat.parse(jsonNode.path(PERSON).path(BIRTH_DATE).asText()));

        JsonNode personAttributes = jsonNode.path(PERSON).path(ATTRIBUTES);
        String phoneNumberAttribute = System.getenv(PHONE_NUMBER) != null ? System.getenv(PHONE_NUMBER) : PHONE_NUMBER_ATTRIBUTE;
        for(JsonNode attributes : personAttributes){
            if(attributes.path(ATTRIBUTE_TYPE).path(DISPLAY).asText().replaceAll("[\\W&&[^-]]", " ").equals(phoneNumberAttribute)){
                patient.setPhoneNumber(attributes.path(VALUE).asText().replaceAll("[\\W&&[^-]]", " "));
            }
        }
        return patient;
    }

    public OpenMRSPatient mapCareContext(String patientJSON) throws IOException, ParseException {
        OpenMRSPatient patient = new OpenMRSPatient();
        JsonNode jsonNode = objectMapper.readTree(patientJSON);

        patient.setHealthId(jsonNode.path(HEALTH_ID).asText());
        patient.setGivenName(jsonNode.path(PATIENT_NAME).asText().replaceAll("[\\W&&[^-]]", " "));
        patient.setPatientReferenceNumber(jsonNode.path(PATIENT_REFERENCE_NUMBER).asText().replaceAll("[\\W&&[^-]]", " "));
        patient.setPhoneNumber(jsonNode.path(MOBILE).asText().replaceAll("[\\W&&[^-]]", " "));
        List<CareContext> careContexts = new ArrayList<CareContext>();
        JsonNode patientCareContexts = jsonNode.path(CARE_CONTEXTS);
        for(JsonNode patientCareContext : patientCareContexts){
            CareContext careContext = new CareContext();
            careContext.setDisplay(patientCareContext.path(CARE_CONTEXT_NAME).asText());
            careContext.setReferenceNumber(patientCareContext.path(CARE_CONTEXT_REFERENCE).asText());
            careContexts.add(careContext);
        }
        patient.setCareContexts(careContexts);

        return patient;
    }

}
