<%@ page contentType="text/css" pageEncoding="UTF-8" %>
<%@ page import="com.imcode.imcms.addon.imagearchive.tag.ImageArchiveTag"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
@import url('${pageContext.request.contextPath}/css/image_archive_default_style.css.jsp');
<% if(session.getAttribute(ImageArchiveTag.CSS_INCLUDE_OVERRIDES_FILE_NAME) != null) { %>
@import url('<%=session.getAttribute(ImageArchiveTag.CSS_INCLUDE_OVERRIDES_FILE_NAME)%>');
<% } %>