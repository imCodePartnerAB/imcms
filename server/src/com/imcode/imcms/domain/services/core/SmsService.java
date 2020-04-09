package com.imcode.imcms.domain.services.core;

import imcode.server.Imcms;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Properties;

public class SmsService {
    public static final String SMS_GATEWAY_USERNAME = "sms.gateway.username";
    public static final String SMS_GATEWAY_PASSWORD = "sms.gateway.password";
    public static final String SMS_GATEWAY_URL = "sms.gateway.url";
    public static final String SMS_GATEWAY_ORIGIN_ADDRESS = "sms.gateway.originAddress";
    public static final String SMS_GATEWAY_DEFAULT_COUNTRY_CODE = "sms.gateway.defaultCountryCode";

    private static final Log log = LogFactory.getLog(SmsService.class);

    private static SmsService instance = null;
    private final String username;
    private final String password;
    private final String gatewayURL;
    private final String originAddress;
    private String defaultCountryCode;

    private SmsService() {
        Properties systemProperties = Imcms.getServerProperties();

        username = systemProperties.getProperty(SMS_GATEWAY_USERNAME, "");
        password = systemProperties.getProperty(SMS_GATEWAY_PASSWORD, "");
        gatewayURL = systemProperties.getProperty(SMS_GATEWAY_URL, "");
        originAddress = systemProperties.getProperty(SMS_GATEWAY_ORIGIN_ADDRESS, "");
        // Default country code for sweden
        defaultCountryCode = systemProperties.getProperty(SMS_GATEWAY_DEFAULT_COUNTRY_CODE, "46");
        if (defaultCountryCode.isEmpty()) {
            defaultCountryCode = "46";
        }
    }

    public static SmsService getInstance() {
        if (null == instance) {
            instance = new SmsService();
        }
        return instance;
    }

    public boolean sendSms(String message, String recipient) {
        try {
            URIBuilder smsRequestBuilder = new URIBuilder(gatewayURL);

            recipient = recipient.replaceAll("\\D", "");
            if (recipient.startsWith("0")) {
                recipient = recipient.replaceFirst("0", defaultCountryCode);
            }

            smsRequestBuilder.addParameter("destination", recipient);
            smsRequestBuilder.addParameter("username", username);
            smsRequestBuilder.addParameter("password", password);
            smsRequestBuilder.addParameter("message", message);

            addOriginAddressIfPresent(smsRequestBuilder);

            HttpGet getRequest = new HttpGet(smsRequestBuilder.build());
            HttpClient client = HttpClientBuilder.create().build();

            HttpResponse response = client.execute(getRequest);
            int responseCode = response.getStatusLine().getStatusCode();

            //0 seems like custom response code
            if (responseCode == HttpStatus.SC_OK || responseCode == 0) {
                return true;
            } else {
                log.error("Wrong response code: " + response.getStatusLine() + "body" + IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
            }

            return false;
        } catch (IOException | URISyntaxException e) {
            log.error(e);
            return false;
        }
    }

    private void addOriginAddressIfPresent(URIBuilder smsRequestBuilder) throws UnsupportedEncodingException {
        if (!originAddress.isEmpty()) {
            int originAddressType;

            if (originAddress.matches("[^a-z]+")) {
                if (originAddress.length() == 5) {
                    originAddressType = 0;
                } else if (originAddress.length() > 5) {
                    originAddressType = 2;
                } else {
                    originAddressType = 1;
                }
            } else {
                originAddressType = 1;
            }

            smsRequestBuilder.addParameter("originatortype", String.valueOf(originAddressType));
            smsRequestBuilder.addParameter("originator", URLEncoder.encode(originAddress, "UTF-8"));
        }
    }
}
