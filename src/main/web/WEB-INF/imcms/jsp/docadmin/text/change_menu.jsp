<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:url var="vaadin_bootstrap_js_url" value='/VAADIN/vaadinBootstrap.js'/>

<!DOCTYPE html>
<html>
<head>
    <title>Edit menu</title>
</head>

<body style="background: #d0ffd0;">
<h1>Edit Menu HTML page</h1>

<!-- Loads the Vaadin widget set, etc. -->
<script type="text/javascript"
        src="${vaadin_bootstrap_js_url}">
</script>

<!-- Vaadin/GWT requires an invisible history frame. It is needed for page/fragment history in the browser. -->
<iframe tabindex="-1" id="__gwt_historyFrame"
        style="position: absolute; width: 0; height: 0; border: 0; overflow: hidden"
        src="javascript:false"></iframe>

 <!-- Embedded menu app-->
<div style="width: 600px; height: 700px; border: 2px solid green;" id="menu-editor" class="v-app">
    <!-- Optional placeholder for the loading indicator -->
    <div class="v-app-loading"></div>

    <!-- Alternative fallback text -->
    <noscript>
        You have to enable javascript in your browser to use an application built with Vaadin.
    </noscript>
</div>

<script type="text/javascript">
//<![CDATA[
if (!window.vaadin) {
    alert("Failed to load Vaadin bootstrap JavaScript: ${vaadin_bootstrap_js_url}");
}

/* The UI Configuration */
vaadin.initApplication("menu-editor", {
    "browserDetailsUrl": "<c:url value='/imcms/docadmin/menu?docId=${requestScope.docId}&menuNo=${requestScope.menuNo}'/>",
    "serviceUrl": "<c:url value='/imcms/docadmin/menu/'/>",
    "widgetset": "com.imcode.imcms.AppWidgetSet",
    "theme": "imcms",
    "versionInfo": {"vaadinVersion": "7.0.0"},
    "vaadinDir": "<c:url value='/VAADIN/'/>",
    "heartbeatInterval": 300,
    "debug": true,
    "standalone": false,
    "authErrMsg": {
        "message": "Take note of any unsaved data, " +
                "and <u>click here<\/u> to continue.",
        "caption": "Authentication problem"
    },
    "comErrMsg": {
        "message": "Take note of any unsaved data, " +
                "and <u>click here<\/u> to continue.",
        "caption": "Communication problem"
    },
    "sessExpMsg": {
        "message": "Take note of any unsaved data, " +
                "and <u>click here<\/u> to continue.",
        "caption": "Session Expired"
    }
});
//]] >
</script>

</body>
</html>

