<%@ page import="imcode.server.AuthenticationMethodConfiguration" %>
<%@ page import="java.util.Map" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page import="imcode.util.Utility" %>
<%@ page import="imcode.server.user.UserDomainObject" %>
<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessage" %>
<%@ page import="com.imcode.imcms.servlet.VerifyUser" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="vel" uri="imcmsvelocity" %>
<vel:velocity>
<%
    String fullServerName = (request.isSecure() ? "https://" : "http://") + request.getServerName() + ":" + request.getServerPort();
    Map<String, AuthenticationMethodConfiguration> loginConfiguration = Imcms.getAuthenticationConfiguration();
    UserDomainObject user = Utility.getLoggedOnUser(request);
%>
<html>
<head>
    <title><? templates/login/index.html/1 ?></title>


    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp">
    <script src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/scripts/imcms_admin.js.jsp"
            type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'name')">
#gui_outer_start()
#gui_head( "<? templates/login/index.html/2 ?>" )
<table border="0" cellspacing="0" cellpadding="0" width="700">
    <form action="">
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><input type="button" class="imcmsFormBtn" style="width:100px"
                                   value="<? templates/login/index.html/2001 ?>"
                                   onClick="top.location='<%= request.getContextPath() %>/servlet/StartDoc';"></td>
                        <td>&nbsp;</td>
                        <td><input type="button" class="imcmsFormBtn" style="width:115px"
                                   value="<? templates/login/index.html/2002 ?>"
                                   onClick="top.location='<%= request.getContextPath() %>/servlet/PasswordReset';">
                        </td>
                        <td>&nbsp;</td>
                        <td><input type="button" value="<? templates/login/index.html/2003 ?>"
                                   title="<? templates/login/index.html/2004 ?>" class="imcmsFormBtn"
                                   onClick="openHelpW('LogIn')"></td>
                    </tr>
                </table>
            </td>
        </tr>
    </form>
</table>
#gui_mid()
<div id="imcms-login-container">
    <% if (loginConfiguration.size() > 1) {
        if (loginConfiguration.containsKey("cgi")
                && loginConfiguration.containsKey("loginPassword")
                && loginConfiguration.get("loginPassword").getOrder() < loginConfiguration.get("cgi").getOrder()) { %>
    <div id="imcms-login-container-tabs">
        <div class="imcms-tab imcms-tab-active" id="imcms-default-tab"><? templates/login/index.html/2008 ?></div>
        <div class="imcms-tab" id="imcms-bankid-tab"><? templates/login/index.html/2009 ?></div>
        <div style="clear:both"></div>
    </div>
    <% } else {%>
    <div id="imcms-login-container-tabs">
        <div class="imcms-tab imcms-tab-active" id="imcms-bankid-tab"><? templates/login/index.html/2009 ?></div>
        <div class="imcms-tab" id="imcms-default-tab"><? templates/login/index.html/2008 ?></div>
        <div style="clear:both"></div>
    </div>
    <% } %>

    <script>
        $(document).ready(function () {
            $(".imcms-tab-active").click();
        });
    </script>
    <% } else if (loginConfiguration.size() == 1) {
        if (loginConfiguration.containsKey("cgi")) {%>
    <div class="imcms-tab imcms-tab-active" id="imcms-bankid-tab"><? templates/login/index.html/2009 ?></div>
    <% } else {%>
    <div class="imcms-tab imcms-tab-active" id="imcms-default-tab"><? templates/login/index.html/2008 ?></div>
    <%
        }%>

    <script>
        $(document).ready(function () {
            $('#imcms-bankid-login-tab').css({height: 500});
            $(".imcms-tab-active").click();
            $(".imcms-tab-active").remove();
        });
    </script>
    <%
        }
    %>

    <div class="imcms-typed-form-container" id="imcms-bankid-login-tab" style="display:none">
        <iframe>

        </iframe>
    </div>
    <div class="imcms-typed-form-container" id="imcms-default-login-tab">

        <table border="0" cellspacing="0" cellpadding="2" width="700">
            <tr>
                <td colspan="2" nowrap><span class="imcmsAdmText">
	<% LocalizedMessage error = (LocalizedMessage) request.getAttribute("error");
        if (null != error) {
    %><p><b><%= error.toLocalizedString(request) %>
                </b></p><%
                    }
                %>
                    <? templates/login/index.html/4 ?>
                    <img alt=""
                         src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/1x1.gif"
                         width="1" height="5"><? templates/login/index.html/1001 ?></span></td>
            </tr>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <table border="0" cellspacing="0" cellpadding="1">
                        <form action="<%= request.getContextPath() %>/servlet/VerifyUser" method="post">
                            <%
                                String next_meta = request.getParameter(VerifyUser.REQUEST_PARAMETER__NEXT_META);
                                String next_url = request.getParameter(VerifyUser.REQUEST_PARAMETER__NEXT_URL);
                                if (null != next_meta) { %>
                            <input type="hidden" name="<%= VerifyUser.REQUEST_PARAMETER__NEXT_META %>"
                                   value="<%=StringEscapeUtils.escapeHtml(next_meta)%>">
                            <%} else if (null != next_url) { %>
                            <input type="hidden" name="<%= VerifyUser.REQUEST_PARAMETER__NEXT_URL %>"
                                   value="<%=StringEscapeUtils.escapeHtml(next_url)%>">
                            <%}%>
                            <tr>
                                <td><span class="imcmsAdmText"><? templates/login/index.html/5 ?></span></td>
                                <td>&nbsp;</td>
                                <td><input type="text" name="<%= VerifyUser.REQUEST_PARAMETER__USERNAME %>"
                                           size="15"
                                           style="width:180px"></td>
                            </tr>
                            <tr>
                                <td><span class="imcmsAdmText"><? templates/login/index.html/6 ?></span></td>
                                <td>&nbsp;</td>
                                <td><input type="password" name="<%= VerifyUser.REQUEST_PARAMETER__PASSWORD %>"
                                           size="15" style="width:180px"></td>
                            </tr>
                            <tr>
                                <td colspan="3">&nbsp;</td>
                            </tr>
                            <tr>
                                <td colspan="2">&nbsp;</td>
                                <td>
                                    <table border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td><input class="imcmsFormBtn" type="submit"
                                                       value="<? templates/login/index.html/2005 ?>"
                                                       style="width:80px">
                                            </td>
                                            <td>&nbsp;</td>
                                            <td><input class="imcmsFormBtn" type="submit"
                                                       name="<%= VerifyUser.REQUEST_PARAMETER__EDIT_USER %>"
                                                       value="<? templates/login/index.html/2006 ?>"
                                                       style="width:80px">
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </form>
                    </table>
                </td>
            </tr>
        </table>
    </div>
    <script>
        $(".imcms-tab").click(function (e) {
            switch ($(this).attr('id')) {
                case "imcms-default-tab":
                {
                    $("iframe").attr("src", "");
                    $("#imcms-bankid-login-tab").animate({
                        height: 200
                    }, 500, function () {
                        $("#imcms-bankid-tab").removeClass("imcms-tab-active");
                        $("#imcms-bankid-login-tab").css({display: 'none'});
                        $("#imcms-default-login-tab").css({display: 'block'});
                        $("#imcms-default-tab").addClass("imcms-tab-active");
                    });

                }
                    break;
                case "imcms-bankid-tab":
                {
                    e.preventDefault();

                    $("#imcms-bankid-login-tab").css({display: 'block'});
                    $("#imcms-bankid-tab").addClass("imcms-tab-active");
                    $("#imcms-bankid-login-tab").animate({
                        height: 500
                    }, 500, function () {
                        $("iframe").css({height: 500});
                        $("iframe").attr("src", "<%= fullServerName+request.getContextPath()+"/VerifyUserViaBankId" %>");
                    });
                    $("#imcms-default-login-tab").css({display: 'none'});
                    $("#imcms-default-tab").removeClass("imcms-tab-active");
                    $("iframe").load(function (e) {
                        try {
                            if ($(this).get(0).contentWindow.location.href == "<%= fullServerName+request.getContextPath()+ "/StartDoc" %>") {
                                $(this).css({display: 'none'});
                                window.location.replace("<%= fullServerName+request.getContextPath()+"/servlet/StartDoc" %>");
                            }
                        }
                        catch (e) {
                        }
                    });
                }
                    break;
            }

        });
    </script>
</div>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>
