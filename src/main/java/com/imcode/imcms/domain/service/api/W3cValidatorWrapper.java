package com.imcode.imcms.domain.service.api;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.domain.dto.ValidationData;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class W3cValidatorWrapper {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String SCHEMA_URL = "https://validator.w3.org/nu/?out=json";
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

	public ValidationData validateText(String content) {
		try {
			final CloseableHttpClient client = HttpClients.createDefault();
			final HttpPost httpPost = new HttpPost(SCHEMA_URL);
			final StringEntity entity = new StringEntity(content);
			httpPost.setEntity(entity);
			httpPost.setHeader("Content-Type", CONTENT_TYPE);

			final CloseableHttpResponse response = client.execute(httpPost);

			return new ObjectMapper().readValue(response.getEntity().getContent(), ValidationData.class);
		} catch (JsonMappingException e) {
			logger.error("Error during deserializing JSON value: ", e);
			throw new RuntimeException("Error during deserializing JSON value: ", e);
		} catch (IOException e) {
			logger.error("Error during sending/receiving: ", e);
			throw new RuntimeException("Error during sending/receiving: ", e);
		}
	}
}