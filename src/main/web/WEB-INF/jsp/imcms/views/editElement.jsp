<%@ page pageEncoding="UTF-8" %>
<%--
    Page for editing one specialized text/image/menu, without whole document content.
    Created by Serhii from Ubrainians for Imcode
    on 26.08.16.
--%>
<%@taglib prefix="imcms" uri="imcms" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<imcms:variables/>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>ImCMS single edit mode</title>
    <meta charset="utf-8"/>
    <jsp:include page="/WEB-INF/templates/${user.language.isoCode639_2}/admin/textdoc/imcms_hard_jquery.html"/>
    <jsp:include page="/WEB-INF/jsp/imcms/imcms_admin_headtag.jsp">
        <jsp:param name="flags" value="${flags}"/>
    </jsp:include>
    <link rel="stylesheet" href="${contextPath}/imcms/css/template/demo.css"/>
    <script src="${contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_edit_element.js"></script>
</head>
<body>
<div class="container">
    <section class="content">
        <div class="wrapper">
            <c:choose>
                <c:when test="${textNo ne null}">
                    <imcms:text no="${textNo}" document="${document.id}"/>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${imageNo ne null}">
                            <imcms:image no="${imageNo}" document="${document.id}"/>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${menuNo ne null}">
                                    <imcms:menu no='${menuNo}' docId="${document.id}">
                                        <ul>
                                            <imcms:menuloop>
                                                <imcms:menuitem>
                                                    <li>
                                                        <imcms:menuitemlink>
                                                            ${menuitem.document.headline}
                                                        </imcms:menuitemlink>
                                                        <!-- sub menu definition -->
                                                        <imcms:menuloop>
                                                            <imcms:menuitem>
                                                                <div>
                                                                    <imcms:menuitemlink>
                                                                        ${menuitem.document.headline}
                                                                    </imcms:menuitemlink>
                                                                </div>
                                                            </imcms:menuitem>
                                                        </imcms:menuloop>
                                                    </li>
                                                </imcms:menuitem>
                                            </imcms:menuloop>
                                        </ul>
                                    </imcms:menu>
                                </c:when>
                                <c:otherwise>
                                    <h3><p>You should to set at least one parameter:</p>
                                        <ul>
                                            <li>textNo</li>
                                            <li>imageNo</li>
                                            <li>menuNo</li>
                                        </ul>
                                    </h3>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</div>
</body>
</html>
