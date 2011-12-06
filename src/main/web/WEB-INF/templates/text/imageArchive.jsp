<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="imcode.server.ImcmsConstants" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page import="com.imcode.imcms.servlet.VerifyUser" %>
<%@ page import="com.imcode.imcms.api.TextDocumentViewing" %>
<%@ page import="com.imcode.imcms.api.Document" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>
        <imcms:text no="1" label="Page title" mode="read"/>
    </title>

    <link href="${contextPath}/css/standalone_style.css.jsp" rel="stylesheet" type="text/css"/>
    <link type="text/css" rel="stylesheet" href="${contextPath}/css/jquery.qtip.css"/>
    
    <script type="text/javascript" src="${contextPath}/js/jquery-1.6.4.min.js"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.ba-resize.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            var iframe = $('#imageArchive');

            $(iframe).load(function () {
                iframe.css({ width : iframe.contents().width()});

                var iframe_content = $(this).contents().find('body');
                iframe_content.resize(function() {
                    var outerHeight = $(this).outerHeight();
                    if(outerHeight != 0) {
                        iframe.css({ height: outerHeight });
                    }
                });

                iframe_content.resize();

            });
        });
    </script>
</head>
<body>
<div class="printHidden" style="width:980px; margin: 0 auto; text-align:center;"
     title="Dubbelklicka för att dölja adminpaneler" ondblclick="this.style.display='none';">
    <imcms:include path='/WEB-INF/jsp/admin/inc_adminlinks.jsp'/>
    <imcms:admin/>
    <imcms:text no="1" label="<br/>Page title<br/>" mode="write" rows="1" formats="text"/>
    <div style="clear:both"></div>
</div>
    <div style="margin:20px auto 0 auto;border:1px solid #ccc;padding: 20px;width:1074px" class="clearfix">
        <div class="clearfix">
            <div style='float:left;height:40px;padding:5px;'>
                <imcms:text no="2" label="<br/>Text above<br/>"/>
            </div>
            <%
                String lang = ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE;

                // Refactor
                String queryString = request.getQueryString();
                if (queryString != null && queryString.contains("toArchiveSearchPage")) {
                    queryString = queryString.replace("toArchiveSearchPage", "");
                }
                StringBuffer baseURL = request.getRequestURL();

                if (queryString == null) {
                    baseURL.append("?").append(lang).append("=");
                } else {
                    // TODO 18n: refactor
                    queryString = queryString.replaceFirst("&?" + lang + "=..", "");
                    baseURL.append("?").append(queryString).append("&amp;").append(lang).append("=");
                }

                pageContext.setAttribute("baseURL", baseURL);
            %>
            <div style="float:right;">
                <div>
                    <%
                        String logInUrl = "login";
                        String logOutUrl = "web/archive/logOut";
                        TextDocumentViewing viewing = TextDocumentViewing.fromRequest(request);
                        if (viewing != null) {
                            Document thisDoc = viewing.getTextDocument();
                            if (thisDoc != null) {
                                logInUrl += "?" + VerifyUser.REQUEST_PARAMETER__NEXT_META + "=" + thisDoc.getId();
                                logOutUrl += "?redirectTo=" + request.getContextPath() + "/" + thisDoc.getId();
                            }
                        }
                        boolean loggedIn = !Imcms.getUser().isDefaultUser();
                        pageContext.setAttribute("userLoggedIn", loggedIn);
                        pageContext.setAttribute("logInUrl", logInUrl);
                        pageContext.setAttribute("logOutUrl", logOutUrl);
                    %>
                    <imcms:text no="100" label="<br/>Log in<br/>" rows="1" formats="text" mode="write"/>
                    <imcms:text no="101" label="<br/>Log out<br/>" rows="1" formats="text" mode="write"/>
                    <a style="font: 11px Verdana, Geneva, sans-serif;text-decoration:none;"
                       href="${contextPath}/${userLoggedIn ? logOutUrl : logInUrl}">
                        <c:choose>
                            <c:when test="${userLoggedIn}">
                                <imcms:text no="101" label="<br/>Log out<br/>" rows="1" formats="text" mode="read"/>
                            </c:when>
                            <c:otherwise>
                                <imcms:text no="100" label="<br/>Log in<br/>" rows="1" formats="text" mode="read"/>
                            </c:otherwise>
                        </c:choose>
                    </a>
                </div>
                <div style="text-align:right;" id="externalLanguageSwitch">
                    <a href="${baseURL}en"><img
                            src="${pageContext.request.contextPath}/imcms/images/icons/flags_iso_639_1/en.gif" alt=""
                            style="border:0;margin-right:4px;"/></a>
                    <a href="${baseURL}sv"><img
                            src="${pageContext.request.contextPath}/imcms/images/icons/flags_iso_639_1/sv.gif" alt=""
                            style="border:0;"/></a>
                </div>
            </div>
        </div>
        <div class="clearfix">
            <div style="float:left;">
                <div id="leftmenu">
                    <jsp:include page="/WEB-INF/jsp/inc_leftmenu.jsp"/>
                </div>
                <imcms:text no="3" label="<br/>Text below<br/>" pre="<div style='width:172px;padding:5px;'>"
                            post="</div>"/>
            </div>
            <div style="float:right;">
                <imcms:imageArchive styleClass="imageArchive">
                    <link href="${contextPath}/css/tag_image_archive.css.jsp" rel="stylesheet" type="text/css"/>
                </imcms:imageArchive>
            </div>
        </div>
    </div>
</body>
</html>