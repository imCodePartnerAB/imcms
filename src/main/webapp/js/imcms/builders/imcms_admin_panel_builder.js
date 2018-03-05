/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
Imcms.define("imcms-admin-panel-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-page-info-builder", "imcms-document-editor-builder",
        "jquery", "imcms", "imcms-events", "imcms-languages-rest-api", "imcms-i18n-texts"
    ],
    function (BEM, componentsBuilder, pageInfoBuilder, documentEditorBuilder, $, imcms, events, languagesRestApi,
              texts) {
        var $panel;

        texts = texts.panel;

        var panelSensitivePixels = 15;

        var isPanelEnabled = true; // by default

        function publishDoc() {
            events.trigger("imcms-publish-new-version-current-doc");
        }

        function showPageInfo() {
            pageInfoBuilder.build(imcms.document.id);
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
                    click: buttonData.onClick,
                    style: [buttonData.style]
                };

                if (opts && opts.active === buttonData.name) {
                    attributes["class"] = "imcms-menu__item--active";
                }

                return panelButtonsBEM.buildBlockElement("item", buttonData.tag, attributes, buttonData.modifiers);
            }

            var editContentDisplayProperty = imcms.editOptions.isEditContent ? "" : "display:none";
            var editDocInfoDisplayProperty = imcms.editOptions.isEditDocInfo ? "" : "display:none";
            var adminDisplayProperty = imcms.isAdmin ? "" : "display:none";

            var versionedContentModifiers = imcms.isVersioningAllowed ? [] : ["versioning-off"],
                publishVersionButtonModifiers = imcms.isVersioningAllowed && imcms.document.hasNewerVersion
                    ? ["has-newer-version"] : [],
                buttons = [
                    {
                        name: 'public',
                        tag: '<a>',
                        href: imcms.contextPath + '/' + imcms.document.id,
                        content: texts.public,
                        modifiers: ["public"],
                        style: editContentDisplayProperty
                    }, {
                        name: 'edit',
                        tag: '<a>',
                        href: imcms.contextPath + "/servlet/AdminDoc?meta_id=" + imcms.document.id,
                        content: texts.edit,
                        modifiers: ["edit"],
                        style: editContentDisplayProperty
                    }, {
                        name: 'preview',
                        tag: "<a>",
                        href: imcms.contextPath + '/api/viewDoc/' + imcms.document.id + "?working-preview=true",
                        content: texts.preview,
                        modifiers: ["preview"].concat(versionedContentModifiers),
                        style: editContentDisplayProperty
                    }, {
                        name: 'publish_offline',
                        tag: "<li>",
                        content: texts.publish,
                        onClick: publishDoc,
                        modifiers: ["publish-of"].concat(versionedContentModifiers, publishVersionButtonModifiers),
                        style: editContentDisplayProperty
                    }, {
                        name: 'page_info',
                        tag: "<li>",
                        content: texts.pageInfo,
                        onClick: showPageInfo,
                        modifiers: ["page-info"],
                        style: editDocInfoDisplayProperty
                    }, {
                        name: 'document',
                        tag: "<li>",
                        content: texts.document,
                        onClick: initDocumentEditor,
                        modifiers: ["document"],
                        style: adminDisplayProperty
                    }, {
                        name: 'admin',
                        tag: "<a>",
                        href: imcms.contextPath + "/servlet/AdminManager",
                        content: texts.admin,
                        modifiers: ["admin"],
                        style: adminDisplayProperty
                    }, {
                        name: 'logout',
                        tag: "<a>",
                        href: imcms.contextPath + "/servlet/LogOut",
                        content: componentsBuilder.buttons.positiveButton({
                            text: texts.logout
                        }),
                        modifiers: ["logout"]
                    }
                ].map(buildPanelButton);

            var $buttonsWrapper = $("<ul>").append(buttons);

            return panelButtonsBEM.buildBlock("<div>", [{"items": $buttonsWrapper}]);
        }

        function flagOnClick() {
            var languageCode = $(this).text();

            if (languageCode !== imcms.language.code) {
                languagesRestApi.update({code: languageCode}).done(function () {
                    location.reload(true);
                });
            }
        }

        function buildFlags() {
            return componentsBuilder.flags.flagsContainer(function (language) {
                return ["<div>", {
                    text: language.code,
                    click: flagOnClick
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

            var $title = adminPanelBEM.buildBlockElement("title", "<div>", {text: imcms.version});
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
                if (isPanelEnabled && (event.clientY >= 0) && (event.clientY <= panelSensitivePixels)) {
                    if ($(window).scrollTop() === 0) {
                        $body.css({"top": "90px"})
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
                $("body").css({"top": "0px"});
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

        function highlightPublishButton() {
            $panel.find(".imcms-menu__item--publish-of").addClass("imcms-menu__item--has-newer-version");
        }

        return {
            buildPanel: function (opts) {
                if ($panel) {
                    return;
                }

                events.on("enable admin panel", function () {
                    isPanelEnabled = true;
                });
                events.on("disable admin panel", function () {
                    isPanelEnabled = false;
                });

                $panel = $("<div>", {
                    "class": "imcms-admin",
                    html: createAdminPanel(opts)
                });

                setShowPanelRule();
                setHidePanelRule();
                $("body").prepend($panel);

                events.on("imcms-version-modified", highlightPublishButton)
            }
        }
    }
);
