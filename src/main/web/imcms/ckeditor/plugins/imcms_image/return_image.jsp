<%@ page

		import="com.imcode.imcms.servlet.admin.EditImage,
				imcode.server.document.textdocument.ImageDomainObject,
				imcode.util.ImcmsImageUtils,
				org.apache.commons.lang3.StringEscapeUtils,
				org.apache.commons.lang3.StringUtils"

		pageEncoding="UTF-8"

%>
<%

	ImageDomainObject image = EditImage.getImage(request);
	Integer metaId = EditImage.getMetaId(request);

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="sv" lang="sv">
<head>
	<title></title>

	<script type="text/javascript">
		var returnImage = null;
		<%
		if (null != image && StringUtils.isNotBlank(image.getUrlPathRelativeToContextPath())) { %>
		returnImage = {
			src: '<%= StringEscapeUtils.escapeEcmaScript(ImcmsImageUtils.getImageUrl(metaId, image, request.getContextPath(), true)) %>',
			alt: '<%= StringEscapeUtils.escapeEcmaScript(image.getAlternateText()) %>', <%
	if (StringUtils.isNotBlank(image.getAlign())) { %>
			align: '<%= StringEscapeUtils.escapeEcmaScript(image.getAlign()) %>', <%
	} %>
			border: '<%= image.getBorder() %>',
			horiz: '<%= image.getHorizontalSpace() %>',
			vert: '<%= image.getVerticalSpace() %>', <%
	if (0 != image.getWidth()) { %>
			width: '<%= image.getWidth() %>', <%
	}
	if (0 != image.getHeight()) { %>
			height: '<%= image.getHeight() %>', <%
	} %>
			name: '<%= StringEscapeUtils.escapeEcmaScript(image.getName()) %>'
		};
		<%
		} %>
		window.opener.handleImcmsReturnImage(returnImage);
		window.close();
	</script>

</head>
<body>
</body>
</html>
