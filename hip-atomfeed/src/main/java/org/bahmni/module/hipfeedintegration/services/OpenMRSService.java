package org.bahmni.module.hipfeedintegration.services;

import org.bahmni.module.hipfeedintegration.atomfeed.client.ConnectionDetails;
import org.bahmni.module.hipfeedintegration.atomfeed.client.WebClientFactory;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.globalProperty.OpenMRSProperties;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.hipfeedintegration.atomfeed.mappers.OpenMRSEncounterMapper;
import org.bahmni.module.hipfeedintegration.atomfeed.mappers.OpenMRSPatientMapper;
import org.bahmni.module.hipfeedintegration.atomfeed.mappers.OpenMRSPropertiesMapper;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class OpenMRSService {

    String patientRestUrl = "/openmrs/ws/rest/v1/patient/";
    String newCareContextUrl = "/openmrs/ws/rest/v1/hip/careContext/new?patientUuid=";
    String globalPropertyurl = "/openmrs/ws/rest/v1/systemsetting/";

    public OpenMRSEncounter getEncounter(String encounterUrl) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String encounterJSON = webClient.get(URI.create(urlPrefix + encounterUrl));
        return new OpenMRSEncounterMapper(ObjectMapperRepository.objectMapper).map(encounterJSON);
    }

    public OpenMRSPatient getPatient(String patientUuid) throws IOException, ParseException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String patientJSON = webClient.get(URI.create(urlPrefix + patientRestUrl + patientUuid+"?v=full"));
        return new OpenMRSPatientMapper().map(patientJSON);
    }
    public OpenMRSPatient getCareContext(String patientUuid) throws IOException, ParseException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String careContextJSON = webClient.get(URI.create(urlPrefix + newCareContextUrl + patientUuid));
        return new OpenMRSPatientMapper().mapCareContext(careContextJSON);
    }
    public List<String> getValueFromGlobalProperty(String property) throws IOException {
            HttpClient webClient = WebClientFactory.getClient();
            String urlPrefix = getURLPrefix();
            String Json = webClient.get(URI.create(urlPrefix + globalPropertyurl + property));

            OpenMRSProperties openMRSProperties = new OpenMRSPropertiesMapper().map(Json);
            ArrayList<String> encounters = openMRSProperties.getValue() != null ? new ArrayList<String>(Arrays.asList(openMRSProperties.getValue().trim().split("\\s*,\\s*"))) : new ArrayList<String>();
            return encounters;
    }
    static String getURLPrefix() {
        org.bahmni.webclients.ConnectionDetails connectionDetails = ConnectionDetails.get();
        String authenticationURI = connectionDetails.getAuthUrl();

        URL openMRSAuthURL;
        try {
            openMRSAuthURL = new URL(authenticationURI);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Is not a valid URI - " + authenticationURI);
        }
        return String.format("%s://%s", openMRSAuthURL.getProtocol(), openMRSAuthURL.getAuthority());
    }

}
