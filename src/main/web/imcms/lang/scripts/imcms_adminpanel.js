/**
 * Created by Shadowgun on 08.04.2015.
 */
Imcms.Admin = {};
Imcms.Admin.Panel = {
    init: function () {
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

        if (!$adminPanel.length) {
            return;
        }

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
        window.pageInfo = (function () {
            Imcms.Editors.Document.getDocument(Imcms.document.meta, function (data) {
                var viewer = new Imcms.Document.Viewer({
                    data: data,
                    type: Imcms.document.type,
                    loader: Imcms.Editors.Document,
                    target: $("body")[0],
                    onApply: function () {
                        Imcms.Editors.Document.update(viewer.serialize(), Imcms.BackgroundWorker.createTask({
                            showProcessWindow: true,
                            refreshPage: true
                        }));
                    }
                });
            })
        });
    },
    docs: function () {
        Imcms.Editors.Document.show();
    }
};