<%@ page contentType="text/javascript" pageEncoding="UTF-8" %>

<%@ page import="imcode.server.user.UserDomainObject" %>
<%@ page import="imcode.util.Utility" %>
<%@ page import="imcode.server.parser.ParserParameters" %>
<%@ page import="imcode.server.document.textdocument.TextDocumentDomainObject" %>
<%@ page import="imcode.util.Html" %>
<%@ page import="imcode.server.parser.TextDocumentParser" %>
<%@ page import="imcode.server.Imcms" %>

<%--<%--%>
<%--try {--%>
<%--UserDomainObject user = Utility.getLoggedOnUser(request);--%>
<%--ParserParameters parserParameters = ParserParameters.fromRequest(request);--%>
<%--if (parserParameters.getFlags() >= 0 && parserParameters.isAdminButtonsVisible()) {--%>
<%--TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();--%>
<%--String adminButtons = Html.getAdminButtons(user, document, request, response);--%>
<%--out.print(adminButtons);--%>
<%--out.print(TextDocumentParser.createChangeTemplateUi(parserParameters.isTemplateMode(), user, document, Imcms.getServices()));--%>
<%--}--%>
<%--} catch (Exception e) {--%>
<%--throw new JspException(e);--%>
<%--}--%>
<%--%>--%>

$(document).ready(function() {
    // write
});

