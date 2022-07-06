package org.bahmni.module.hipfeedintegration.services;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bahmni.module.hipfeedintegration.atomfeed.worker.EncounterFeedWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.newCareContext;
import static org.bahmni.module.hipfeedintegration.atomfeed.client.Constants.smsNotify;
import static org.bahmni.module.hipfeedintegration.services.OpenMRSService.getURLPrefix;

@Component
public class HipService {

    private static final Logger logger = LoggerFactory.getLogger(EncounterFeedWorker.class);


    public String callNewContext(String body) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = createRequest(getURLPrefix() + newCareContext,body);
        CloseableHttpResponse response = client.execute(httpPost);

        String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
        logger.warn("Calling new carecontext, statusCode: " + statusCode);
        client.close();

        return statusCode;
    }

    public String smsNotify(String body) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = createRequest(getURLPrefix() + smsNotify,body);
        CloseableHttpResponse response = client.execute(httpPost);

        String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
        logger.warn("sms notify , statusCode: " + statusCode);
        client.close();

        return statusCode;
    }



    private HttpPost createRequest(String uri, String requestBody) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(uri);
        StringEntity entity = new StringEntity(requestBody, "application/json", "UTF-8");
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("CORRELATION_ID", null);
        return httpPost;
    }
}
