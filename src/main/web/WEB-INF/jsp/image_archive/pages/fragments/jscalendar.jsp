<%@ page import="com.imcode.imcms.api.*,imcode.server.Imcms" %>
<% String calLangIso2 = Language.getLanguageByISO639_1(Imcms.getUser().getDocGetterCallback().languages().selected().getCode()).getIsoCode639_2(); %>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/imcms/jscalendar/calendar.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/imcms/jscalendar/lang/calendar-<%= calLangIso2 %>.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/imcms/jscalendar/calendar-setup.js"></script>
