<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:url var="vaadin_bootstrap_js_url" value='/VAADIN/vaadinBootstrap.js'/>

<!-- Loads the Vaadin widget set, etc. -->
<script type="text/javascript"
        src="${vaadin_bootstrap_js_url}">
</script>

<!-- Embedded menu app-->
<div style="width: 20px; height: 20px; border: 2px solid green;" id="vaadinAdminButtons">
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
    vaadin.initApplication("vaadinAdminButtons", {
        "browserDetailsUrl": "<c:url value='/imcms/docadmin/buttons/'/>",
        "serviceUrl": "<c:url value='/imcms/docadmin/buttons/'/>",
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