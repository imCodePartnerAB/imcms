/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
Imcms.define("imcms-admin-panel-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-page-info-builder", "imcms-document-editor-builder",
        "jquery", "imcms"
    ],
    function (BEM, componentsBuilder, pageInfoBuilder, documentEditorBuilder, $, imcms) {
        var $panel;

        var panelSensitivePixels = 15;

        function getNotImplementedButtonClick(feature) {
            return function (e) {
                e.preventDefault();
                console.log("%c Not implemented feature: " + feature, "color: red;");
            }
        }

        function showPageInfo() {
            pageInfoBuilder.build(Imcms.document.id);
        }

        function initDocumentEditor() {
            documentEditorBuilder.build();
        }

        function buildPanelButtons(opts) {
            var panelButtonsBEM = new BEM({
                block: "imcms-menu",
                elements: {
                    "items": "",
                    "item": "imcms-menu__item"
                }
            });

            function buildPanelButton(buttonData) {
                var attributes = {
                    html: buttonData.content,
                    href: buttonData.href,
                    click: buttonData.onClick
                };

                if (opts && opts.active === buttonData.name) {
                    attributes["class"] = "imcms-menu__item--active";
                }

                return panelButtonsBEM.buildBlockElement("item", buttonData.tag, attributes, buttonData.modifiers);
            }

            var versionedContentModifier = imcms.isVersioningAllowed ? undefined : "versioning-off",
                buttons = [
                {
                    name: 'public',
                    tag: '<a>',
                    href: imcms.contextPath + '/' + imcms.document.id,
                    content: 'public',
                    modifiers: ["public"]
                }, {
                    name: 'edit',
                    tag: '<a>',
                    href: imcms.contextPath + "/servlet/AdminDoc?meta_id=" + imcms.document.id + "&flags=" + imcms.accessFlags,
                    content: 'edit',
                    modifiers: ["edit"]
                }, {
                    name: 'preview',
                        tag: "<a>",
                        href: imcms.contextPath + '/api/viewDoc/' + imcms.document.id + "?working-preview=true",
                    content: "preview",
                        modifiers: ["preview", versionedContentModifier]
                }, {
                    name: 'publish_offline',
                    tag: "<li>",
                    content: "publish offline",
                    onClick: getNotImplementedButtonClick("publish offline version click"),
                        modifiers: ["publish-of", versionedContentModifier]
                }, {
                    name: 'page_info',
                    tag: "<li>",
                    content: "page info",
                    onClick: showPageInfo,
                    modifiers: ["page-info"]
                }, {
                    name: 'document',
                    tag: "<li>",
                    content: "document",
                    onClick: initDocumentEditor,
                    modifiers: ["document"]
                }, {
                    name: 'admin',
                        tag: "<a>",
                        href: imcms.contextPath + "/servlet/AdminManager",
                    content: "admin",
                    modifiers: ["admin"]
                }, {
                    name: 'logout',
                        tag: "<a>",
                        href: imcms.contextPath + "/servlet/LogOut",
                    content: componentsBuilder.buttons.positiveButton({
                        text: "log out"
                    }),
                    modifiers: ["logout"]
                }
            ].map(buildPanelButton);

            var $buttonsWrapper = $("<ul>").append(buttons);

            return panelButtonsBEM.buildBlock("<div>", [{"items": $buttonsWrapper}]);
        }

        function buildFlags() {
            return componentsBuilder.flags.flagsContainer(function (language) {
                return ["<a>", {
                    text: language.code,
                    href: imcms.contextPath + "/api/viewDoc/" + imcms.document.id + "?language-code=" + language.code
                }];
            });
        }

        function createAdminPanel(opts) {
            var adminPanelBEM = new BEM({
                block: "imcms-admin-panel",
                elements: {
                    "item": "",
                    "logo": "",
                    "title": ""
                }
            });

            var $logo = adminPanelBEM.buildBlockElement("logo", "<a>", {href: ""}); // fixme: link to start doc?
            var $logoItem = $("<div>").append($logo);

            var $title = adminPanelBEM.buildBlockElement("title", "<div>", {text: Imcms.version});
            var $titleItem = $("<div>").append($title);

            var $flagsItem = buildFlags();
            var $buttonsContainer = buildPanelButtons(opts);

            return adminPanelBEM.buildBlock("<div>", [
                {"item": $logoItem},
                {"item": $titleItem},
                {"item": $flagsItem},
                {"item": $buttonsContainer}
            ]);
        }

        function setShowPanelRule() {
            var $body = $("body");
            $(document).mousemove(function (event) {
                if ((event.clientY >= 0) && (event.clientY <= panelSensitivePixels)) {
                    if($body.scrollTop() === 0) {
                        $body.animate({"top": "90px"}, 150)
                    } else {
                        $body.css({"padding-top": "0px"})
                    }
                    showPanel();
                }
            });
        }

        function setHidePanelRule() {
            $(document).click(function (event) {
                if ($(event.target).closest(".imcms-admin").length) {
                    return;
                }
                $("body").animate({"top": "0px"}, 150);
                hidePanel();
            });
        }

        function hidePanel() {
            setAdminPanelTop(-90);
        }

        function showPanel() {
            setAdminPanelTop(0);
        }

        function setAdminPanelTop(px) {
            $panel.css({"top": "" + px + "px"});
        }

        return {
            buildPanel: function (opts) {
                if ($panel) {
                    return;
                }

                $panel = $("<div>", {
                    "class": "imcms-admin",
                    html: createAdminPanel(opts)
                });

                setShowPanelRule();
                setHidePanelRule();
                $("body").prepend($panel);
            }
        }
    }
);
