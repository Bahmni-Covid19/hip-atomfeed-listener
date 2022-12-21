package org.bahmni.module.hipfeedintegration.atomfeed.client;

public class Constants {

    public static final String OPENMRS_PROPERTY_ENCOUNTERS_TO_BE_IGNORED = "abdm.encounterTypesToBeIgnored";
    public static final String OPENMRS_PROPERTY_CONCEPTS_TO_BE_IGNORED = "abdm.conceptsTypesToBeIgnored";
    public static final String OPENMRS_PROPERTY_FORM_FIELDS_TO_BE_IGNORED = "abdm.formFieldsToBeIgnored";
    public static final String IDENTIFIERS = "identifiers";
    public static final String IDENTIFIER = "identifier";
    public static final String PERSON = "person";
    public static final String PREFERRED_NAME = "preferredName";
    public static final String GIVEN_NAME = "givenName";
    public static final String FAMILY_NAME = "familyName";
    public static final String MIDDLE_NAME = "middleName";
    public static final String GENDER = "gender";
    public static final String PHONE_NUMBER_ATTRIBUTE = "phoneNumber";
    public static final String BIRTH_DATE = "birthdate";
    public static final String ATTRIBUTES = "attributes";
    public static final String ATTRIBUTE_TYPE = "attributeType";
    public static final String DISPLAY = "display";
    public static final String VALUE = "value";
    public static final String HEALTH_ID = "healthId";
    public static final String PATIENT_REFERENCE_NUMBER = "patientReferenceNumber";
    public static final String CARE_CONTEXTS = "careContexts";
    public static final String CARE_CONTEXT_NAME = "careContextName";
    public static final String CARE_CONTEXT_TYPE = "careContextType";
    public static final String CARE_CONTEXT_REFERENCE = "careContextReference";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";

    public static final String patientRestUrl = "/openmrs/ws/rest/v1/patient/";
    public static final String newCareContextUrl = "/openmrs/ws/rest/v1/hip/careContext/new?patientUuid=";
    public static final String globalPropertyurl = "/openmrs/ws/rest/v1/systemsetting/";
    public static final String newCareContext = "/v0.5/hip/new-carecontext";
    public static final String smsNotify = "/v0.5/hip/patients/sms/notify";

}
