<%@ page
        contentType="text/html; charset=UTF-8"
        import="com.imcode.imcms.api.DocumentLanguage"
        %>
<%@ page import="com.imcode.imcms.servlet.Version" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page import="imcode.server.document.DocumentDomainObject" %>
<%@ page import="imcode.server.user.UserDomainObject" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%
    UserDomainObject user = (UserDomainObject) request.getAttribute("user");
    DocumentDomainObject document = (DocumentDomainObject) request.getAttribute("document");
    if (!user.canEdit(document)) return;
    Boolean editMode = request.getParameterMap().containsKey("flags");
    String contextPath = request.getContextPath();
    String imcmsVersion = Version.getImcmsVersion(getServletConfig().getServletContext());
    DocumentLanguage currentLanguage = Imcms.getUser().getDocGetterCallback().getLanguage();
%>
<div class="admin-panel">
    <div class="admin-panel-draggable"></div>
    <div class="admin-panel-content">
        <section id="languages" class="admin-panel-content-section  admin-panel-content-section-language">
            <div class="admin-panel-version"><%=imcmsVersion%>
            </div>
            <div class="admin-panel-language">
                <a href="<%=contextPath%>/servlet/GetDoc?meta_id=<%=document.getId()%>&lang=en"
                   title="English/English (default current)"
                   class="<%=currentLanguage.getCode().equals("en")?"active":""%>">
                    <img src="<%=contextPath%>/images/ic_english.png" alt="" style="border:0;">
                </a>
                <a href="<%=contextPath%>/servlet/GetDoc?meta_id=<%=document.getId()%>&lang=sv"
                   title="Swedish/Svenska"
                   class="<%=currentLanguage.getCode().equals("sv")?"active":""%>">
                    <img src="<%=contextPath%>/images/ic_swedish.png" alt="" style="border:0;">
                </a>
            </div>
        </section>
        <section id="read" data-mode="readonly" class="admin-panel-content-section <%=editMode?"":"active"%>">
            <a href="<%=contextPath%>/<%=StringUtils.defaultString(document.getAlias(), String.valueOf(document.getId()))%>"
               target="_self">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Read-only</span>
                </div>
            </a>
        </section>
        <section id="edit" data-mode="edit" class="admin-panel-content-section <%=editMode?"active":""%>">
            <a href="<%=contextPath%>/servlet/AdminDoc?meta_id=<%=document.getId()%>&flags=65536" target="_self">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Edit</span>
                </div>
            </a>
        </section>
        <div class="admin-panel-content-separator"></div>
        <section id="info" data-mode="info" class="admin-panel-content-section">
            <a href="#" target="_self" onclick="pageInfo(); return false;">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Page info</span>
                </div>
            </a>
        </section>
        <div class="admin-panel-content-separator"></div>
        <section id="admin" data-mode="admin" class="admin-panel-content-section">
            <a href="<%=contextPath%>/servlet/AdminManager" target="_self">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Admin</span>
                </div>
            </a>
        </section>
        <div class="admin-panel-content-separator"></div>
        <section id="logout" data-mode="logout" class="admin-panel-content-section">
            <a href="<%=contextPath%>/servlet/LogOut" target="_self">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Logout</span>
                </div>
            </a>
        </section>
    </div>
</div>
<script>
    (function () {
        var draggable = false,
                entered = false,
                cornerPointDistanceX = 0,
                cornerPointDistanceY = 0,
                $draggable = $(".admin-panel-draggable"),
                $doc = $(document),
                $adminPanel = $(".admin-panel").css({
                    left: $.cookie("admin-panel-location-left", Number) || 0,
                    top: $.cookie("admin-panel-location-top", Number) || 0
                });
        $draggable.on("mouseenter", function () {
            entered = true
        });
        $draggable.on("mouseleave", function () {
            entered = false
        });
        $doc.on("mousedown", function (e) {
            draggable = entered;
            cornerPointDistanceX = e.pageX - $adminPanel.position().left;
            cornerPointDistanceY = e.pageY - $adminPanel.position().top;
            return !draggable;
        });
        $doc.on("mouseup", function () {
            draggable = false;
        });
        $doc.on("mousemove", function (e) {
            if (draggable) {
                var
                        x = e.pageX - cornerPointDistanceX,
                        y = e.pageY - cornerPointDistanceY;
                x = x < 0 ? 0 : x;
                y = y < 0 ? 0 : y;
                $adminPanel.css({
                    left: x,
                    top: y
                });
                $.cookie("admin-panel-location-left", x, {expires: 9999999, path: '/'});
                $.cookie("admin-panel-location-top", y, {expires: 9999999, path: '/'});
            }
        });
    })();
    var pageInfo = (function () {
        Imcms.Editors.Document.getDocument(<%=document.getId()%>, function (data) {
            var viewer = new Imcms.Document.Viewer({
                data: data,
                loader: Imcms.Editors.Document,
                target: $("body")[0],
                onApply: function () {
                    Imcms.Editors.Document.update(JSON.stringify(viewer.serialize()), $.proxy(location.reload, location));
                }
            });
        })
    })
</script>