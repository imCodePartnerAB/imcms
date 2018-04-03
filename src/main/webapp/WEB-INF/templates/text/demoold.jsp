<%@ page import="imcode.server.Imcms,
                 imcode.server.ImcmsConstants" pageEncoding="UTF-8" %>
<%@ page import="org.apache.oro.text.perl.Perl5Util" %>

<%@taglib prefix="imcms" uri="imcms" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<imcms:variables/>
<%

    // TODO: Add support for imCMS versions > 5

    String documentationUrl = "@documentation-host@";

    Perl5Util re = new Perl5Util();

    if (re.match("/^(.*\\/)(\\d)(\\.\\d).*/", documentationUrl)) {
        try {
            int majorVersion = Integer.parseInt(re.group(2));
            if (majorVersion > 5) {
                majorVersion = 5;
            }
            documentationUrl = re.group(1) + majorVersion + re.group(3);
        } catch (Exception ignore) {
        }
    }

    request.setAttribute("documentationUrl", documentationUrl);

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>

    <title><c:out value="${document.headline}"/> - Powered by imCMS from imCode Partner AB</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/imcms_demo.css.jsp"/>
    <c:choose>
        <c:when test="<%=Imcms.getUser().isDefaultUser()%>">
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
            <link rel="stylesheet" type="text/css"
                  href="${pageContext.request.contextPath}/imcms/css/imcms_demo.css.jsp"/>
            <style type="text/css">
                .pages, .tabs {
                    padding: 10px 10px 0 10px;
                    width: 300px;
                    height: 50px;
                    overflow: hidden;
                }

                .pages {
                    padding: 0 10px 10px 10px;
                    height: 340px;
                }

                .tabs .tab {
                    width: 130px;
                    height: 30px;
                    padding: 10px;
                    font-size: 30px;
                    float: left;
                    line-height: 30px;
                }

                .pages .page {
                    padding: 10px;
                    height: 320px;
                    display: none;
                }

                .pages .page[selected],
                .tabs .tab[selected] {
                    -webkit-box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.75);
                    -moz-box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.75);
                    box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.75);
                    display: block;
                }

                .pages .field {
                    clear: both;
                    padding-top: 10px;
                }

                .page .field label,
                .page .field label {
                    display: block;
                    margin-bottom: 2px;
                }

                .page .field select,
                .page .field input,
                .page .field input {
                    background: #f0f0f0;
                    border: none;
                    padding: 5px 10px;
                    width: 260px;
                    outline: none;
                    height: 20px;
                    resize: none;
                }

                .page .field .positive {
                    background: #649b00;
                    border: none;
                    color: #fff;
                    cursor: pointer;
                    line-height: 30px;
                    display: inline-block;
                    padding: 0 20px;
                    height: 30px;
                }

                .page .field .positive:hover {
                    background: #6eaf00;
                }
            </style>
        </c:when>
    </c:choose>
</head>
<body style="margin:10px; background-color:#eee;">


<table border="0" cellspacing="0" cellpadding="5" align="center"
       style="height:100%; background-color:#fff; border: 1px solid #ccc; border-width: 1px 2px 2px 1px; border-color: #ccc #000 #000 #ccc;">
    <tr>
        <td valign="top">
            <table border="0" cellspacing="0" cellpadding="0" width="760">
                <tr>
                    <td colspan="5"><imcms:include url="${documentationUrl}/1054?template=imcmsDemoTop"/></td>
                </tr>
                <tr>
                    <td colspan="5" height="15">&nbsp;</td>
                </tr>
                <tr valign="top">
                    <td width="200"></td>

                    <td width="15">&nbsp;</td>

                    <td width="385">
                        <%
                            String lang = ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE;

                            // Refactor
                            String queryString = request.getQueryString();
                            StringBuffer baseURL = request.getRequestURL();

                            if (queryString == null) {
                                baseURL.append("?" + lang + "=");
                            } else {
                                // TODO 18n: refactor
                                queryString = queryString.replaceFirst("&?" + lang + "=..", "");
                                baseURL.append("?" + queryString + "&amp;" + lang + "=");
                            }

                            pageContext.setAttribute("baseURL", baseURL);

                        %>
                        <c:choose>
                            <c:when test="<%=!Imcms.getUser().isDefaultUser()%>">
                                <imcms:logout>
                                    Logout
                                </imcms:logout>
                            </c:when>
                            <c:otherwise>
                                <div class="tabs">
                                    <div class="tab" selected data-item="1">Sing In</div>
                                    <div class="tab" data-item="2">Sing Up</div>
                                </div>
                                <div class="pages">
                                    <div class="page" data-item="1" selected>
                                        <span>Demo Login form</span>
                                        <imcms:login>
                                            <div class="field">
                                                <label>Login</label>
                                                <imcms:loginname
                                                        attributes="class='asasdgasdf' data-lol='asasdfs' placeholder='Enter your login'"/>
                                            </div>
                                            <div class="field">
                                                <label>Password</label>
                                                <imcms:loginpassword/>
                                            </div>
                                            <input type="hidden" name="login" value="login"/>

                                            <div class="field">
                                                <button class="positive" type="submit">Login</button>
                                            </div>
                                        </imcms:login>
                                    </div>
                                    <div class="page" data-item="2">
                                        <span>Demo registration form</span>
                                        <imcms:registration>
                                            <div class="field">
                                                <label>Login</label>
                                                <imcms:registrationlogin/>
                                            </div>
                                            <div class="field">
                                                <label>Name</label>
                                                <imcms:registrationname/>
                                            </div>
                                            <div class="field">
                                                <label>Second Name</label>
                                                <imcms:registrationsurname/>
                                            </div>
                                            <div class="field">
                                                <label>Password</label>
                                                <imcms:registrationpassword1/>
                                            </div>
                                            <div class="field">
                                                <label>Repeat password</label>
                                                <imcms:registrationpassword2/>
                                            </div>
                                            <div class="field">
                                                <button class="positive" type="submit">Register</button>
                                            </div>
                                        </imcms:registration>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <br/>
                        <a href="${baseURL}en"><img
                                src="${pageContext.request.contextPath}/imcms/eng/images/admin/flags_iso_639_1/en.gif"
                                alt="" style="border:0;"/></a>
                        <a href="${baseURL}sv"><img
                                src="${pageContext.request.contextPath}/imcms/swe/images/admin/flags_iso_639_1/sv.gif"
                                alt="" style="border:0;"/></a>

                        <imcms:text no="1" label="Text (html)" formats="text,html" rows="2" pre='<h1/>' post='<h1/>'/>
                        <imcms:text no='2' label='<br/>Text' pre='<div class="text">' post='</div>'/>
                        <div>
                            <span>Demo menu</span>
                            <imcms:menu index='1' document="1001" label='<br/><br/>Meny (punktlista)'>
                                <ul>
                                    <imcms:menuloop>
                                        <imcms:menuitem>
                                            <li style="padding-bottom:5px; color:green;"><imcms:menuitemlink><c:out
                                                    value="${menuitem.document.headline}"/></imcms:menuitemlink>
                                                <imcms:menuloop>
                                                    <imcms:menuitem>
                                                        <div style="padding-bottom:5px; color:green;">
                                                            <imcms:menuitemlink><c:out
                                                                    value="${menuitem.document.headline}"/></imcms:menuitemlink>
                                                        </div>
                                                    </imcms:menuitem>
                                                </imcms:menuloop>
                                            </li>
                                        </imcms:menuitem>
                                    </imcms:menuloop>
                                </ul>
                            </imcms:menu>
                        </div>
                        <imcms:include url="${documentationUrl}/1054?template=imcmsDemoContent"
                                       pre='<div style="margin: 10px 0; padding: 10px 0; border: 1px solid #ccc; border-width: 1px 0;">'
                                       post='</div>'/>
                        <imcms:image no='3' label='Bild' pre='<br/><br/>' post='<br/>'/><br/>
                        <imcms:include no='1' label='Dynamisk inkludering 1'/>


                        <imcms:contentLoop no="1" label="Loop (main)" pre='<div class="loop">' post='</div>'>
                            <imcms:text no='2' label='<br/>Text' pre='<div class="text">' post='</div>'/>
                            <imcms:image no="33"/>
                        </imcms:contentLoop>


                        <imcms:contentLoop no="100" label="Loop (secondary)" pre='<div class="loop">' post='</div>'>
                            <imcms:text no='100' label='<br/>Text' pre='<div class="text">' post='</div>'/>
                            <imcms:image no='3' label='Bild' pre='<br/><br/>' post='<br/>'/><br/>
                        </imcms:contentLoop>

                    </td>

                    <td width="10">&nbsp;</td>

                    <td width="150"><imcms:include url="${documentationUrl}/1054?template=imcmsDemoRight"/></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td align="center" valign="bottom">&nbsp;<br/><imcms:admin/>
            <imcms:include url="${documentationUrl}/1054?template=imcmsDemoBottom"/>
        </td>
    </tr>
</table>

<br/>
<imcms:search searchRequest="" skip="0" take="2">
    <imcms:searchitem>
        <div>
                ${searchItem.foundDocument.alias}
                ${searchItem.foundDocument.language.name}
        </div>
    </imcms:searchitem>
    <imcms:pager>
        <div>
            <a href="${firstPagerItem.link}">${firstPagerItem.pageNumber+1}</a>
        </div>
        <imcms:pageritem>
            <c:choose>
                <c:when test="${pagerItem.showed}">
                    <div>
                        <a href="${pagerItem.link}">${pagerItem.pageNumber+1}</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${pagerItem.link}">${pagerItem.pageNumber+1}</a>
                </c:otherwise>
            </c:choose>
        </imcms:pageritem>
        <div>
            <a href="${lastPagerItem.link}">${lastPagerItem.pageNumber+1}</a>
        </div>
    </imcms:pager>
</imcms:search>
<script type="application/javascript">
    $(".tab").click(function () {
        var
                $this = $(this),
                tabId = $this.attr("data-item");

        $(".tab").removeAttr("selected");
        $(".page").removeAttr("selected");

        $(this).attr("selected", "");
        $(".page[data-item=" + tabId + "]").attr("selected", "");
    });
</script>
</body>
</html>