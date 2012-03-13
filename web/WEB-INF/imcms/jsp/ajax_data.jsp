<%@ page

	import="com.imcode.imcms.api.*,
	        java.sql.Connection,
	        java.sql.PreparedStatement,
	        java.sql.ResultSet,
	        java.text.SimpleDateFormat,
	        java.util.Date,
	        org.apache.commons.lang.StringUtils, org.json.simple.JSONObject, imcode.server.ImcmsServices, imcode.server.Imcms, imcode.server.user.UserDomainObject, imcode.util.Utility, com.imcode.imcms.mapping.DocumentMapper, imcode.server.document.textdocument.TextDocumentDomainObject, imcode.server.document.TextDocumentPermissionSetDomainObject, imcode.server.document.textdocument.TextDomainObject, imcode.server.document.NoPermissionToEditDocumentException, imcode.util.ShouldHaveCheckedPermissionsEarlierException, imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException, imcode.server.document.ConcurrentDocumentModificationException, com.imcode.imcms.mapping.DocumentSaveException, imcode.util.ShouldNotBeThrownException, org.apache.commons.lang.StringEscapeUtils, java.net.URLEncoder"

	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"

%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%

int truncateLength = 70 ;

SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm") ;

String action = StringUtils.defaultString(request.getParameter("action")) ;
String value  = StringUtils.defaultString(request.getParameter("value")) ;

if ("getHelpTextInlineEditing".equals(action)) {
	
	boolean isSwe = false ;
	try {
		isSwe =	ContentManagementSystem.fromRequest(request).getCurrentUser().getLanguage().getIsoCode639_2().equals("swe");
	} catch (Exception e) {}
	
	if (isSwe) { %>
	<h3>Redigera på stället</h3>
	<ul>
		<li>...</li>
	</ul><%
	} else { %>
	<h3>Edit in place!</h3>
	<ul>
		<li>...</li>
	</ul><%
	}
	return ;
	
} else if ("saveText".equals(action)) {
	
	JSONObject jsonObject = new JSONObject() ;
	
	int meta_id = Integer.parseInt(request.getParameter("meta_id")) ;
	int txt_no  = Integer.parseInt(request.getParameter("txt_no")) ;
	int format  = Integer.parseInt(request.getParameter("format")) ;
	String text = request.getParameter("text") ;
	boolean doLog = (null != request.getParameter("do_log")) ;
	text = text
		.replace(StringEscapeUtils.escapeHtml("<?imcms:contextpath?>"), "<?imcms:contextpath?>")
		.replace(URLEncoder.encode("<?imcms:contextpath?>", Imcms.ISO_8859_1_ENCODING), "<?imcms:contextpath?>")
		.replace(URLEncoder.encode("<?imcms:contextpath?>", Imcms.UTF_8_ENCODING), "<?imcms:contextpath?>")
		.replace("%3C?imcms:contextpath?%3E", "<?imcms:contextpath?>") ;
	
	ImcmsServices imcref = Imcms.getServices() ;
	UserDomainObject user = Utility.getLoggedOnUser(request) ;
	DocumentMapper documentMapper = imcref.getDocumentMapper();
	TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument(meta_id) ;
	TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor(document) ;
	
	if (permissionSet.getEditTexts()) {
		TextDomainObject textDO = new TextDomainObject(text, format) ;
		document.setText(txt_no, textDO) ;
		document.addModifiedTextIndex(txt_no, true) ;
		
		try {
			documentMapper.saveDocument(document, user) ;
			if (doLog) {
				imcref.updateMainLog("Text " + txt_no + " in [" + meta_id + "] modified by user: [" + user.getFullName() + "]") ;
			}
			jsonObject.put("isSaved", true) ;
			jsonObject.put("error", "") ;
		} catch (NoPermissionToEditDocumentException e ) {
			jsonObject.put("isSaved", false) ;
			jsonObject.put("error", e.getMessage()) ;
		} catch ( NoPermissionToAddDocumentToMenuException e ) {
			jsonObject.put("isSaved", false) ;
			jsonObject.put("error", e.getMessage()) ;
		} catch (DocumentSaveException e) {
			jsonObject.put("isSaved", false) ;
			jsonObject.put("error", e.getMessage()) ;
		}
	} else {
		jsonObject.put("isSaved", false) ;
		jsonObject.put("error", "No permission to save!") ;
	}
	out.print(jsonObject) ;
	
	return ;
	
} else if ("getCompleteHtmlForW3cValidation".equals(action)) {
	
	%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="sv" lang="sv">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Validation</title>
</head>
<body>
<%= value %>
</body>
</html><%
	return ;
	
} else if ("linkEditAutoCompleteSearch".equals(action)) {
	
	String cp = request.getContextPath() ;
	
	int truncateLengthPath = truncateLength - cp.length() ;
	
	boolean isStartsWithSearch = "".equals(StringUtils.defaultString(request.getParameter("isStartsWithSearch"))) ;
	int limit = Integer.parseInt(request.getParameter("limit")) ;
	
	String query  = StringUtils.defaultString(request.getParameter("q"))
					.toLowerCase().replaceFirst(cp + "\\/", "") ;
	
	//out.print("query: " + query) ;
	int iCount = 0 ;
	try {
		ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest(request) ;
		DatabaseService databaseService = imcmsSystem.getDatabaseService() ;
		Connection connection = null ;
		PreparedStatement preparedStatement = null ;
		ResultSet resultSet = null ;
		try {
			String sql =
					"SELECT m.meta_id, (SELECT p.value FROM document_properties p WHERE p.meta_id = m.meta_id AND p.key_name = 'imcms.document.alias'), m.meta_headline, t.text, m.date_created, m.date_modified, m.status\n" +
					"FROM meta m\n" +
					"RIGHT JOIN texts t ON m.meta_id = t.meta_id\n" +
					"WHERE\n" +
					"  t.name = ? AND\n" +
					"  (CONVERT(m.meta_id, CHAR(8)) LIKE ? OR\n" +
					"  m.meta_headline LIKE ? OR\n" +
					"  t.text LIKE ? OR\n" +
					"  m.meta_id IN (\n" +
					"    SELECT p.meta_id FROM document_properties p WHERE p.key_name = 'imcms.document.alias' AND p.value LIKE ?)\n" +
					"  )\n" +
					"LIMIT " + limit ;
			connection = databaseService.getConnection() ;
			preparedStatement = connection.prepareStatement(sql) ;
			preparedStatement.setInt(1, 1) ;
			preparedStatement.setString(2, query + '%') ;
			preparedStatement.setString(3, isStartsWithSearch ? query + '%' : '%' + query + '%') ;
			preparedStatement.setString(4, isStartsWithSearch ? query + '%' : '%' + query + '%') ;
			preparedStatement.setString(5, isStartsWithSearch ? query + '%' : '%' + query + '%') ;
			resultSet = preparedStatement.executeQuery() ;
			while (resultSet.next()) {
				String metaId   = resultSet.getInt(1) + "" ;
				String alias    = StringUtils.defaultString(resultSet.getString(2)) ;
				String headline = StringUtils.defaultString(resultSet.getString(3)) ;
				String text1    = StringUtils.defaultString(resultSet.getString(4)) ;
				Date dateCr     = resultSet.getTimestamp(5) ;
				Date dateMo     = resultSet.getTimestamp(6) ;
				int status      = resultSet.getInt(7) ;
				boolean isNew   = (Document.PublicationStatus.NEW.toString().equals(status + "")) ;
				String path     = (!"".equals(alias)) ? alias : metaId ;
				String pathVis  = StringUtils.abbreviate(path, truncateLengthPath) ;
				boolean matchMetaId   = metaId.startsWith(query) ;
				boolean matchAlias    = (isStartsWithSearch) ? alias.toLowerCase().startsWith(query) : alias.toLowerCase().contains(query) ;
				boolean matchHeadline = (isStartsWithSearch) ? headline.replaceAll("<[^>]+?>", "").toLowerCase().startsWith(query) : headline.toLowerCase().contains(query) ;
				boolean matchText1    = (isStartsWithSearch) ? text1.replaceAll("<[^>]+?>", "").toLowerCase().startsWith(query)    : text1.toLowerCase().contains(query) ;
				if (matchMetaId) {
					metaId = metaId.replaceFirst("^(" + query + ")", "<b>$1</b>") ;
					pathVis   = pathVis.replaceFirst("^(" + query + ")", "<b>$1</b>") ;
				}
				if (matchAlias) {
					pathVis = pathVis.replaceAll("(" + query + ")", "<b>$1</b>") ;
				}
				headline = StringUtils.abbreviate(headline.replaceAll("<[^>]+?>", "").replaceAll("\\s+", " "), truncateLength) ;
				text1  = StringUtils.abbreviate(text1.replaceAll("<[^>]+?>", "").replaceAll("\\s+", " "), truncateLength) ;
				if (matchHeadline) {
					headline = headline.replaceAll("(" + query + ")", "<b>$1</b>") ;
				} else if (matchText1) {
					text1  = text1.replaceAll("(" + query + ")", "<b>$1</b>") ;
				}
				headline += " (headline)" ;
				text1 += " (text 1)" ;
				
				String textToShow = (matchHeadline || !matchText1) ? headline : text1 ;
				
				String tableStart = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"2\">" ;
				String rowStart   = "<tr valign=\"top\"><td class=\"imcmsAdmDim\" style=\"font-weight:bold; padding-right:10px;\">" ;
				String rowMid     = "</td><td style=\"white-space:pre-wrap; font-size:11px;\">" ;
				String rowEnd     = "</td></tr>" ;
				String tableEnd   = "</table>" ;
				
				out.print("\n") ;
				%><!-- <%= path %> --><%
				%><div id="valueShow<%= iCount %>" style="display:block;"><%
					%><%= tableStart %><%
					%><%= rowStart %>Id:<%=   rowMid %><%= metaId             %><%= rowEnd %><%
					%><%= rowStart %>Href:<%= rowMid %><%= cp + "/" + pathVis %><%= rowEnd %><%
					%><%= rowStart %>Text:<%= rowMid %><%= textToShow         %><%= rowEnd %><%
					%><%= tableEnd %><%
				%></div><%
				%><div id="valueDefault<%= iCount %>" style="display:none;"><%
					%><%= tableStart %><%
					%><%= rowStart %>Id:<%=   rowMid %><%= metaId             %><%= rowEnd %><%
					%><%= rowStart %>Href:<%= rowMid %><%= cp + "/" + pathVis %><%= rowEnd %><%
					%><%= rowStart %>Text:<%= rowMid %><%= textToShow         %><%= rowEnd %><%
					%><%= tableEnd %><%
				%></div><%
				%><div id="valueHover<%= iCount %>" style="display:none;"><%
					%><%= tableStart %><%
					%><%= rowStart %>Id:<%=   rowMid %><%= metaId %><%
						%> - <fmt:message key="global/created"/>: <b><%= df.format(dateCr) %></b><%
						%>, <fmt:message key="global/modified"/>: <b><%= df.format(dateMo) %></b><%
						%><%= isNew ? " - <span style=\"color:#0b0; font-weight:bold; font-style:italic;\">" : "" %><%
						if (isNew) {
							%><fmt:message key="global/new"/>!<%
						} %><%= isNew ? "</span>" : "" %><%= rowEnd %><%
					%><%= rowStart %>Href:<%= rowMid %><%= cp + "/" + pathVis %><%= rowEnd %><%
					%><%= rowStart %>Text:<%= rowMid %><%= textToShow         %><%= rowEnd %><%
					%><%= tableEnd %><%
				%></div><%
				iCount++ ;
			}
		} catch(Exception e) {
			out.print("\nerror1: " + e.getMessage()) ;
		} finally {
			try {
				if (null != resultSet) resultSet.close() ;
				if (null != preparedStatement) preparedStatement.close() ;
				if (null != connection) connection.close() ;
			} catch(Exception ex) {}
		}
	} catch (Exception ex) {
		out.print("\nerror2: " + ex.getMessage()) ;
	}
	
	return ;
} %>