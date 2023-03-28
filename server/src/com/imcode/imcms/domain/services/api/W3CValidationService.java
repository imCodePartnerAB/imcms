package com.imcode.imcms.domain.services.api;

import imcode.server.Imcms;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class W3CValidationService {

	private static final String CONTENT_WRAPPER = "<!DOCTYPE html>\n"
			+ "<html lang=\"en\">\n"
			+ "  <head>\n"
			+ "    <meta charset=\"UTF-8\">\n"
			+ "    <title>ImCMS HTML validation</title>\n"
			+ "    <meta name=\"description\" content=\"ImCMS HTML validation\">\n"
			+ "  </head>\n"
			+ "  <body>%s</body>\n"
			+ "</html>";

	public static boolean isAvailable() {
		if (!Boolean.parseBoolean(Imcms.getServerProperties().getProperty("w3c.validation"))) return false;

		try (final CloseableHttpClient client = HttpClients.createMinimal()) {
			final HttpHead httpHead = new HttpHead("https://validator.w3.org/nu/");

			return client.execute(httpHead)
					.getStatusLine()
					.getStatusCode() == HttpServletResponse.SC_OK;
		} catch (IOException e) {
			log.error(e);
			return false;
		}
	}

	public static JSONObject validate(String htmlToValidate, boolean showResults) {
		JSONObject jsonObject = new JSONObject();
		JSONParser jsonParser = new JSONParser();

		try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
			final HttpPost httpPost = new HttpPost("https://validator.w3.org/nu/?out=json");
			final StringEntity entity = new StringEntity(String.format(CONTENT_WRAPPER, convertIsoToUtf8(htmlToValidate)));

			httpPost.setEntity(entity);
			httpPost.setHeader("Content-Type", "text/html; charset=UTF-8");

			final CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			final JSONObject jsonResponse = (JSONObject) jsonParser.parse(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8));

			httpResponse.close();

			final List<JSONObject> warnings = new ArrayList<>();
			final List<JSONObject> errors = new ArrayList<>();

			final JSONArray messages = (JSONArray) jsonResponse.get("messages");
			for (Object message : messages) {
				final JSONObject jsonMessage = (JSONObject) message;

				if (jsonMessage.get("type").equals("info")) {
					warnings.add(jsonMessage);
				}

				if (jsonMessage.get("type").equals("error")) {
					errors.add(jsonMessage);
				}
			}

			if (!showResults) {
				jsonResponse.clear();
			}

			jsonObject.put("isOk", true);
			jsonObject.put("messages", messages);
			jsonObject.put("jsonResponse", jsonResponse);
			jsonObject.put("isValid", messages.isEmpty());
			jsonObject.put("warnings", warnings);
			jsonObject.put("errors", errors);
			jsonObject.put("error", "");
		} catch (Exception ex) {
			jsonObject.put("isOk", false);
			jsonObject.put("isValid", false);
			jsonObject.put("error", ex.getMessage());
			log.error(ex);
		}

		return jsonObject;
	}

	private static String convertUtf8ToIso(String string) {
		try {
			return new String(string.getBytes(Imcms.UTF_8_ENCODING), Imcms.ISO_8859_1_ENCODING);
		} catch (Exception e) {
			return string;
		}
	}

	private static String convertIsoToUtf8(String string) {
		try {
			return new String(string.getBytes(Imcms.ISO_8859_1_ENCODING), Imcms.UTF_8_ENCODING);
		} catch (Exception e) {
			return string;
		}
	}
}
