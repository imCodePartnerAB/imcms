<%@ page import="com.imcode.imcms.api.*" %>
<% String calLangIso2 = Language.getLanguageByISO639_1(I18nSupport.getCurrentLanguage().getCode()).getIsoCode639_2(); %>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/imcms/jscalendar/calendar.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/imcms/jscalendar/lang/calendar-<%= calLangIso2 %>.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/imcms/jscalendar/calendar-setup.js"></script>
