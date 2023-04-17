package org.bahmni.module.hipfeedintegration.atomfeed.client;

import org.bahmni.module.hipfeedintegration.atomfeed.worker.EncounterFeedWorker;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class AtomFeedClientFactory {

    private final Logger logger = LoggerFactory.getLogger(AtomFeedClientFactory.class);
    private static final String OPENMRS_ENCOUNTER_URL = "OPENMRS_ENCOUNTER_FEED_URL";

    @Autowired
    private AtomFeedHibernateTransactionManager transactionManager;

    public FeedClient get(EncounterFeedWorker encounterFeedWorker) {
        logger.info("encounterFeedWorker........." + encounterFeedWorker.toString());
        HttpClient authenticatedWebClient = WebClientFactory.getClient();
        logger.info("authenticatedWebClient........." + authenticatedWebClient.toString());
        org.bahmni.webclients.ConnectionDetails connectionDetails = ConnectionDetails.get();
        logger.info("connectionDetails........." + connectionDetails.toString());
        String authUri = connectionDetails.getAuthUrl();
        logger.info("authUri........." + authUri);
        ClientCookies cookies = getCookies(authenticatedWebClient, authUri);
        logger.info("cookies........." + cookies.size());

        return getFeedClient(AtomFeedProperties.getInstance(), encounterFeedWorker, cookies);
    }

    private FeedClient getFeedClient(AtomFeedProperties atomFeedProperties,
                                        EventWorker eventWorker, ClientCookies cookies) {
        String uri = atomFeedProperties.getProperty(OPENMRS_ENCOUNTER_URL);
        logger.info("uri........." + uri);
        try {

            org.ict4h.atomfeed.client.AtomFeedProperties atomFeedClientProperties = createAtomFeedClientProperties(atomFeedProperties);
            logger.info("atomFeedClientProperties........." + atomFeedClientProperties);
            AllFeeds allFeeds = new AllFeeds(atomFeedClientProperties, cookies);
            logger.info("allFeeds........." + allFeeds.toString());
            AllMarkersJdbcImpl allMarkers = new AllMarkersJdbcImpl(transactionManager);
            logger.info("allMarkers........." + allMarkers.toString());
            AllFailedEventsJdbcImpl allFailedEvents = new AllFailedEventsJdbcImpl(transactionManager);
            logger.info("allFailedEvents........." + allFailedEvents.toString());

            return new AtomFeedClient(allFeeds, allMarkers, allFailedEvents,
                    atomFeedClientProperties, transactionManager, new URI(uri), eventWorker);
            
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Is not a valid URI - %s", uri));
        }
    }

    private org.ict4h.atomfeed.client.AtomFeedProperties createAtomFeedClientProperties(AtomFeedProperties atomFeedProperties) {
        org.ict4h.atomfeed.client.AtomFeedProperties feedProperties = new org.ict4h.atomfeed.client.AtomFeedProperties();
        feedProperties.setConnectTimeout(Integer.parseInt(atomFeedProperties.getFeedConnectionTimeout()));
        feedProperties.setReadTimeout(Integer.parseInt(atomFeedProperties.getFeedReplyTimeout()));
        feedProperties.setMaxFailedEvents(Integer.parseInt(atomFeedProperties.getMaxFailedEvents()));
        feedProperties.setFailedEventMaxRetry(Integer.parseInt(atomFeedProperties.getFailedEventMaxRetry()));
        feedProperties.setControlsEventProcessing(true);
        feedProperties.setHandleRedirection(Boolean.parseBoolean(atomFeedProperties.getHandleRedirection()));
        return feedProperties;
    }

    private ClientCookies getCookies(HttpClient authenticatedWebClient, String urlString) {
        try {
            return authenticatedWebClient.getCookies(new URI(urlString));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Is not a valid URI - " + urlString);
        }
    }
}
