<%@ page pageEncoding="UTF-8" %>
<%--
    Provides all necessary admin functionality. Put to <head> tag and do not forget to set parameter ${flags}
    Created by Serhii from Ubrainians for Imcode
    on 26.08.16.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<imcms:variables/>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<base href="${contextPath}">

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jqueryui-touch-punch/0.2.3/jquery.ui.touch-punch.min.js"></script>
<script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.14.0/jquery.validate.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/speakingurl/5.0.1/speakingurl.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/js-cookie/2.1.1/js.cookie.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jstree/3.3.2/jstree.min.js"></script>

<script type="text/javascript" src="${contextPath}/imcms/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/ckeditor/lang/sv.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/ckeditor/lang/en.js"></script>

<link rel="stylesheet"
      href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.min.css"/>
<link rel="stylesheet" href="${contextPath}/imcms/jqtree/jqtree.css">
<link rel="stylesheet" href="${contextPath}/imcms/prism/prism.css">
<link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_merged.css.jsp"/>
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jstree/3.3.2/themes/default/style.min.css" />

<script type="text/javascript"
        src="${contextPath}/imcms/scripts/imcms.js.jsp?meta_id=${document.id}&amp;language=${user.language.isoCode639_2}&amp;flags=${flags}"></script>
<script type="text/javascript" src="${contextPath}/application.min.js"></script>
