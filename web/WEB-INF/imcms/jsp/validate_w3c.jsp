<%@ page

		import="imcode.server.Imcms,
		        org.json.simple.JSONObject"

		contentType="text/html;charset=UTF-8"
		pageEncoding="UTF-8"

%>
<%@ page import="org.apache.http.impl.client.CloseableHttpClient" %>
<%@ page import="org.apache.http.impl.client.HttpClients" %>
<%@ page import="org.apache.http.client.methods.HttpPost" %>
<%@ page import="org.apache.http.entity.StringEntity" %>
<%@ page import="org.apache.http.client.methods.CloseableHttpResponse" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%!

	private static final String CONTENT_WRAPPER = "<!DOCTYPE html>\n"
			+ "<html lang=\"en\">\n"
			+ "  <head>\n"
			+ "    <meta charset=\"utf-8\">\n"
			+ "    <title>ImCMS HTML validation</title>\n"
			+ "    <meta name=\"description\" content=\"ImCMS HTML validation\">\n"
			+ "  </head>\n"
			+ "  <body>%s</body>\n"
			+ "</html>";

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

%><%

	JSONObject jsonObject = new JSONObject();
	JSONParser jsonParser = new JSONParser();

	try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
		String htmlToValidate = request.getParameter("htmlToValidate");
		boolean showResults = "true".equals(request.getParameter("showResults"));

		final HttpPost httpPost = new HttpPost("https://validator.w3.org/nu/?out=json");
		final StringEntity entity = new StringEntity(String.format(CONTENT_WRAPPER, htmlToValidate));

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
	}
	out.print(jsonObject);
%>
