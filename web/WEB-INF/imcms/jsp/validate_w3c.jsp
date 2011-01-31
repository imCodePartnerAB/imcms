<%@ page
	
	import="imcode.util.ClientHttpRequest,
	        imcode.server.Imcms, org.json.simple.JSONObject, java.io.InputStream, java.io.IOException"
    
  contentType="text/html;charset=UTF-8"
  pageEncoding="UTF-8"
  
%><%!

private static String convertUtf8ToIso(String string) {
	try {
		return new String(string.getBytes(Imcms.UTF_8_ENCODING), Imcms.ISO_8859_1_ENCODING) ;
	} catch (Exception e) {
		return string ;
	}
}
private static String convertIsoToUtf8(String string) {
	try {
		return new String(string.getBytes(Imcms.ISO_8859_1_ENCODING), Imcms.UTF_8_ENCODING) ;
	} catch (Exception e) {
		return string ;
	}
}

%><%

JSONObject jsonObject = new JSONObject() ;

try {
	String htmlToValidate = request.getParameter("htmlToValidate") ;
	boolean showResults   = "true".equals(request.getParameter("showResults")) ;


	ClientHttpRequest clientHttpRequest = new ClientHttpRequest("http://validator.w3.org/check") ;
	clientHttpRequest.setParameter("fragment", htmlToValidate) ;
	clientHttpRequest.setParameter("group", "0") ;
	clientHttpRequest.setParameter("prefill", "0") ;
	clientHttpRequest.setParameter("doctype", "Inline") ;
	clientHttpRequest.setParameter("ss", "1") ;
	InputStream isResponse = clientHttpRequest.post() ;
	String textResponse = "" ;

	try {
		if (null != isResponse) {
			int c;
			StringBuffer buf = new StringBuffer() ;
			while ((c = isResponse.read()) >= 0) {
				buf.append((char)c) ;
			}
			textResponse = buf.toString() ;
		}
	} catch (Exception e) {}

	//out.print("simulatedForm returned &quot;" + textResponse + "&quot;") ;

	textResponse = textResponse
					.replaceAll("<a[^>]+?>&#x2709;<\\/a>", "") ;
	textResponse = convertIsoToUtf8(textResponse.trim()) ;

	boolean isValid   = textResponse.toLowerCase().contains("[valid]") ;
	boolean isInValid = textResponse.toLowerCase().contains("[invalid]") ;

	if (!showResults) {
		textResponse = "" ;
	}

	jsonObject.put("isOk", isValid || isInValid) ;
	jsonObject.put("isValid", isValid) ;
	jsonObject.put("getHtml", textResponse) ;
	jsonObject.put("error", "") ;
} catch (Exception ex) {
	jsonObject.put("isOk", false) ;
	jsonObject.put("isValid", false) ;
	jsonObject.put("getHtml", "") ;
	jsonObject.put("error", ex.getMessage()) ;
}
out.print(jsonObject) ;

%>