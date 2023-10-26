package com.imcode.imcms.api;

import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class SmsService {
	private final Logger logger = LogManager.getLogger(SmsService.class);
	private final String gatewayUrl;
	private final String gatewayUsername;
	private final String gatewayPassword;
	private final String originator;
	private final long validityTime;
	private final String deliveryTime;


	public SmsService(@Value("${sms.gateway.url}") String gatewayUrl,
	                  @Value("${sms.gateway.username}") String gatewayUsername,
	                  @Value("${sms.gateway.password}") String gatewayPassword,
	                  @Value("${sms.gateway.originator}") String originator,
	                  @Value("${sms.gateway.validity.time}") int validityTime,
	                  @Value("${sms.gateway.delivery.time}") String deliveryTime) {
		this.gatewayUrl = gatewayUrl;
		this.gatewayUsername = gatewayUsername;
		this.gatewayPassword = gatewayPassword;
		this.originator = originator;
		this.validityTime = validityTime;
		this.deliveryTime = deliveryTime;
	}

	public boolean sendSms(String message, String recipient) {
		try {

			if (StringUtils.isAnyEmpty(message, recipient)) {
				logger.error(String.format("Message or recipient cannot be empty: message = %s, recipient = %s", message, recipient));
				throw new IllegalArgumentException(String.format("Message or recipient cannot be empty: message = %s, recipient = %s", message, recipient));
			}

			final HttpClient client = HttpClient.newHttpClient();
			final HttpRequest request = HttpRequest.newBuilder(buildUri(message, recipient)).build();
			final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != HttpStatus.SC_OK) {
				logger.error(String.format("Unsuccessful response code: %d, response body: %s", response.statusCode(), response.body()));
				return false;
			}

			return isMessageSent(response.body());
		} catch (IOException | InterruptedException e) {
			logger.error(String.format("Something happened during request: %s", e));
			throw new RuntimeException(String.format("Something happened during request: %s", e));
		}
	}

	private URI buildUri(String message, String recipient) {
		try {
			final URIBuilder smsRequest = new URIBuilder(gatewayUrl);

			smsRequest.addParameter("destination", formatPhoneNumber(recipient));
			smsRequest.addParameter("username", gatewayUsername);
			smsRequest.addParameter("password", gatewayPassword);
			smsRequest.addParameter("message", message);

			addOriginator(smsRequest);

			if (!deliveryTime.isEmpty()) {
				smsRequest.addParameter("deliverytime", deliveryTime);
			}

			if (validityTime != -1) {
				smsRequest.addParameter("validitytime", String.valueOf(validityTime));
			}

			return smsRequest.build();
		} catch (URISyntaxException e) {
			logger.error("Uri did not build due to illegal arguments", e);
			throw new IllegalArgumentException("Uri did not build due to illegal arguments", e);
		}
	}

	private void addOriginator(URIBuilder smsRequest) {
		if (!originator.isEmpty()) {
			int originAddressType;

			if (originator.matches("[^a-z]+")) {
				if (originator.length() == 5) {
					originAddressType = 0;
				} else if (originator.length() > 5) {
					originAddressType = 2;
				} else {
					originAddressType = 1;
				}
			} else {
				originAddressType = 1;
			}

			smsRequest.addParameter("originatortype", String.valueOf(originAddressType));
			smsRequest.addParameter("originator", URLEncoder.encode(originator, StandardCharsets.UTF_8));
		}
	}

	private boolean isMessageSent(String xml) {
		final boolean messageNotSent = Optional.ofNullable(Utility.xmlStringToDocument(xml))
				.map(Document::getDocumentElement)
				.map(element -> isTagNotEmpty(element, "responsemessage") || isTagNotEmpty(element, "errormessage"))
				.orElse(true);

		if (messageNotSent) {
			logger.error(String.format("Message not sent due to some mistakes in parameters: %s", xml));
			return false;
		}

		return true;
	}

	private boolean isTagNotEmpty(Element element, String tagName) {
		return Optional.ofNullable(element.getElementsByTagName(tagName).item(0))
				.filter(node -> node.getTextContent() != null && !node.getTextContent().isBlank())
				.isPresent();
	}

	private String formatPhoneNumber(String recipient) {
		return recipient.startsWith("+") ? recipient.substring(recipient.indexOf("+") + 1) : recipient;
	}
}
