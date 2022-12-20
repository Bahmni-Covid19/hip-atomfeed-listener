package org.bahmni.module.hipfeedintegration.atomfeed.client;


public class ConnectionDetails {
    private static final String OPENMRS_URL = "OPENMRS_URL";
    private static final String OPENMRS_USERNAME = "OPENMRS_USERNAME";
    private static final String OPENMRS_PASSWORD = "OPENMRS_PASSWORD";
    private static final String OPENMRS_WEBCLIENT_CONNECT_TIMEOUT = "OPENMRS_CONNECTION_TIMEOUT_IN_MILLISECONS";
    private static final String OPENMRS_WEBCLIENT_READ_TIMEOUT = "OPENMRS_REPLY_TIMEOUT_IN_MILLISECONS";

    public static org.bahmni.webclients.ConnectionDetails get() {
        AtomFeedProperties atomFeedProperties = AtomFeedProperties.getInstance();
        return new org.bahmni.webclients.ConnectionDetails(
                atomFeedProperties.getProperty(OPENMRS_URL),
                atomFeedProperties.getProperty(OPENMRS_USERNAME),
                atomFeedProperties.getProperty(OPENMRS_PASSWORD),
                Integer.parseInt(atomFeedProperties.getProperty(OPENMRS_WEBCLIENT_CONNECT_TIMEOUT)),
                Integer.parseInt(atomFeedProperties.getProperty(OPENMRS_WEBCLIENT_READ_TIMEOUT)));
    }
}
