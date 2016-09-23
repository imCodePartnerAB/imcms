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
<c:set var="language" value="${user.language.isoCode639_2}"/>

<base href="${contextPath}">

<script src="${contextPath}/js/jquery.nearest.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.js"></script>
<script type="text/javascript" src="${contextPath}/js/jquery.ui.touch-punch.min.js"></script>
<script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.14.0/jquery.validate.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/speakingurl/5.0.1/speakingurl.min.js"></script>
<script src="${contextPath}/js/js.cookie.js"></script>
<script src="${contextPath}/js/jquery.cookie.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/ckeditor/lang/sv.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/ckeditor/lang/en.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/jsformbuilder/JSFormBuilder.js"></script>

<script src="${contextPath}/imcms/prism/prism.js"></script>
<script src="${contextPath}/imcms/jqtree/tree.jquery.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jstree/3.3.2/jstree.min.js"></script>

<script src="${contextPath}/imcms/js/admin/jquery.i18n.properties.min.js"></script>

<link rel="stylesheet"
      href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css"/>
<link rel="stylesheet" href="${contextPath}/imcms/jqtree/jqtree.css">
<link rel="stylesheet" href="${contextPath}/imcms/prism/prism.css">

<link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_merged.css.jsp"/>
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jstree/3.3.2/themes/default/style.min.css" />

<script src="${contextPath}/imcms/${language}/scripts/imcms_linker.js"></script>

<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_base.js"></script>
<script type="text/javascript"
        src="${contextPath}/imcms/${language}/scripts/imcms_bootstrap.js.jsp?meta_id=${document.id}&amp;language=${language}&amp;flags=${flags}"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_adminpanel.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_logger.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_utils.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_addons.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_ckeditor_extension.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_events.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_loop.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_menu.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_text.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_language.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_template.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_category.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_user.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_document.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_role.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_permission.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_file.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_folder.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_content.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_image.js"></script>
<script type="text/javascript" src="${contextPath}/imcms/${language}/scripts/imcms_backgroundworker.js"></script>


