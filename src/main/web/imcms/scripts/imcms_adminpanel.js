(function (Imcms) {
    return Imcms.Admin = {
        Panel: {
            publisherName: "admin-panel-publisher",
            redirectSafely: function (index, element) {
                $(element).click(function (event) {
                    event.preventDefault();
                    var reference = $(this).attr("href");
                    Imcms.CallbackConnector.setCallbackOrCall(
                        Imcms.Admin.Panel.publisherName,
                        Imcms.BackgroundWorker.createTask({
                            redirectURL: reference
                        })
                    );
                });
            },
            init: function () {
                var draggable = false,
                    entered = false,
                    panelWidth = 0,
                    cornerPointDistanceX = 0,
                    cornerPointDistanceY = 0,
                    $draggable = $(".admin-panel-draggable"),
                    $doc = $(document),
                    $adminPanel = $(".admin-panel");

                Imcms.CallbackConnector.createPublisher(Imcms.Admin.Panel.publisherName);
                $(".imcms-panel-safe-redirect").each(this.redirectSafely);

                $adminPanel.css({
                    left: $.cookie("admin-panel-location-left", Number) || 0,
                    top: $.cookie("admin-panel-location-top", Number) || 0,
                    width: ~~($adminPanel.width()) + 1 //from 823.325 to 824 to prevent fails when drag
                });

                $adminPanel.draggable({
                    handle: ".admin-panel-draggable",
                    containment: "window"
                });

                if (!$adminPanel.length) {
                    return;
                }

                panelWidth = $adminPanel.width();
                $adminPanel.css("minWidth", panelWidth);

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
                            windowWidth = $(window).width(),
                            x = e.pageX - cornerPointDistanceX,
                            y = e.pageY - cornerPointDistanceY;

                        x = (x < 0) ? 0 : (x + panelWidth + 10 >= windowWidth) ? windowWidth - panelWidth - 10 : x;
                        y = (y < 0) ? 0 : y;
                        $adminPanel.css({
                            left: x,
                            top: y
                        });
                        $.cookie("admin-panel-location-left", x, {expires: 9999999, path: '/'});
                        $.cookie("admin-panel-location-top", y, {expires: 9999999, path: '/'});
                    }
                });
            },
            pageInfoCounter: false,
            // don't trust your IDE, function is used!
            pageInfo: function () {
                var pageInfoCounter = this.pageInfoCounter;

                if (this.pageInfoCounter === false) {
                    this.pageInfoCounter = true;
                    Imcms.Editors.Document.getDocument(Imcms.document.meta, function (data) {
                        var viewer = new Imcms.Document.Viewer({
                            data: data,
                            type: Imcms.document.type,
                            loader: Imcms.Editors.Document,
                            target: $("body")[0],
                            onApply: function () {
                                pageInfoCounter = false;
                                Imcms.Editors.Document.update(viewer.serialize(), Imcms.BackgroundWorker.createTask({
                                    showProcessWindow: true,
                                    reloadWholePage: true
                                }));
                            },
                            onCancel: function () {
                                pageInfoCounter = false;
                            }
                        });
                    });
                }
            },
            docs: function () {
                Imcms.Editors.Document.show();
            }
        }
    };
})(Imcms);
