<%@ tag import="imcode.server.document.DocumentDomainObject, imcode.util.Html, imcode.util.Utility" %>

<%@ attribute name="document" required="true" type="imcode.server.document.DocumentDomainObject" %>

<%= Html.getStatusIconTemplate((DocumentDomainObject) jspContext.getAttribute("document"), Utility.getLoggedOnUser(request)) %>
