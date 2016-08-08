<%@ taglib prefix="imcms" uri="imcms" %>
<%@ page

        contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"

%>
<imcms:variables/>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>${document.headline} - Powered by imCMS from imCode Partner AB</title>
    <meta charset="utf-8"/>
    <base href="${pageContext.request.contextPath}">

    <script src="${pageContext.request.contextPath}/js/jquery.nearest.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.js"></script>
    <script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.14.0/jquery.validate.js"></script>
    <script src="${pageContext.request.contextPath}/js/js.cookie.js"></script>

    <script src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_linker.js"></script>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/speakingurl/5.0.1/speakingurl.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.cookie.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/imcms/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="$contextPath/imcms/ckeditor/lang/sv.js"></script>
    <script type="text/javascript" src="$contextPath/imcms/ckeditor/lang/en.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/jsformbuilder/JSFormBuilder.js"></script>
    <script src="${pageContext.request.contextPath}/imcms/prism/prism.js"></script>
    <script src="${pageContext.request.contextPath}/imcms/jqtree/tree.jquery.js"></script>

    <link rel="stylesheet"
          href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/imcms/jqtree/jqtree.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/imcms/prism/prism.css">

    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/admin/base.css "/>--%>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/imcms_doc_admin.css.jsp"/>--%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/imcms_merged.css.jsp"/>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/admin/panel.css "/>--%>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/admin/frame.css "/>--%>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/admin/window.css "/>--%>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/admin/menu.css "/>--%>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/admin/content.css "/>--%>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/admin/document.css "/>--%>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/admin/image.css "/>--%>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/imcms/css/admin/process.css "/>--%>

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_base.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_bootstrap.js.jsp?meta_id=${document.id}&amp;language=${user.language.isoCode639_2}&amp;flags=56565"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_adminpanel.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_logger.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_utils.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_addons.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_loop.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_menu.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_text.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_language.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_template.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_category.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_document.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_role.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_permission.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_file.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_folder.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_content.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_image.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_backgroundworker.js"></script>
</head>
<body>
<imcms:admin/>
</body>
</html>