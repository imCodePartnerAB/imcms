<%@ page import="com.imcode.imcms.addon.imagearchive.tag.ImageArchiveTag" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <c:set var="contextPath" value="${pageContext.servletContext.contextPath}"/>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <title>${title}</title>
    <meta http-equiv="imagetoolbar" content="no" />
    <link rel="shortcut icon" href="${contextPath}/images/favicon.ico"/>
    <link href="${contextPath}/imcms/css/imcms_admin.css.jsp" rel="stylesheet" type="text/css" />
    <link href="${contextPath}/css/blue/style.css" rel="stylesheet" type="text/css" />
    <link href="${contextPath}/css/jquery.qtip.css" rel="stylesheet" type="text/css" />
    <link href="${contextPath}/css/image_archive.css.jsp" rel="stylesheet" type="text/css" />
    ${css}

    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.tablesorter.min.js"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.uploadify-v2.1.4/jquery.uploadify.v2.1.4.min.js"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.uploadify-v2.1.4/swfobject.js"></script>
    <script type="text/javascript" src="${contextPath}/js/image_archive.js"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.qtip.js"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.tools.min.js"></script>
    ${javascript}


    <c:set var="customCss" value="${sessionScope.CSS_OVERRIDES_FROM_IMAGE_ARCHIVE_TAG}"/>
    <script type="text/javascript">
        var sessionTimeOutMs = ${pageContext.session.maxInactiveInterval * 1000+100};
        var oMessageDiv;

        $(document).ready(function(){

            setTimeout(function(){
                alert('<spring:message code="archive.warning.sessionExpired" htmlEscape="true"/>');
            },  sessionTimeOutMs);

            var customCss = '${customCss}';
            if(self != top && !opener) {
                if(customCss.length == 0) {
                    parent.location.reload();
                } else {
                    $('head').append(customCss);
                }

                var contentWidth = $(document).width();
                var contentHeight = $(document).height();

                $(window.parent.document).find("iframe").each(function(iel, el) {
                  if(el.contentWindow === window) {
                    $(el).css('width',contentWidth+'px').css('height', contentHeight+'px');
                  }
                });
            }
        });
    </script>
</head>
<body>
<form action="/" style="display:none;">
    <input type="hidden" id="contextPath" value="${contextPath}"/>
    <c:url var="dummyUrl" value="<%=session.getId()%>"/>
    <input type="hidden" id="jsessionid" value=";jsessionid=${dummyUrl}" />
</form>