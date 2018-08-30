/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
define("imcms-admin-panel-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-page-info-builder", "imcms-document-editor-builder",
        "jquery", "imcms", "imcms-events", "imcms-languages-rest-api", "imcms-top-panel-visibility-initiator",
        "imcms-i18n-texts", "imcms-admin-panel-settings-builder"
    ],
    function (BEM, componentsBuilder, pageInfoBuilder, documentEditorBuilder, $, imcms, events, languagesRestApi,
              panelVisibility, texts, panelSettings) {

        var $panelContainer, $panel;

        var panelBlock = "imcms-panel";
        var panelItemClass = BEM.buildClass(panelBlock, "item");
        var panelItemHasNewerVersionClass = BEM.buildClass(panelBlock, "item", "has-newer-version");

        texts = texts.panel;

        function publishDoc() {
            $(this).hasClass(panelItemHasNewerVersionClass) && events.trigger("imcms-publish-new-version-current-doc");
        }

        function showPageInfo() {
            pageInfoBuilder.build(imcms.document.id);
        }

        function initDocumentEditor() {
            documentEditorBuilder.build();
        }

        function buildPanelButtons(opts) {
            var panelButtonsBEM = new BEM({
                block: panelBlock,
                elements: {
                    "items": "",
                    "item": panelItemClass
                }
            });

            function buildPanelButton(buttonData) {
                var attributes = {
                    html: buttonData.content,
                    title: buttonData.title,
                    href: buttonData.href,
                    click: buttonData.onClick,
                    style: buttonData.style
                };

                if (opts && opts.active === buttonData.name) {
                    attributes["class"] = BEM.buildClass(panelBlock, "item", "active");
                }

                return panelButtonsBEM.buildBlockElement("item", buttonData.tag, attributes, buttonData.modifiers);
            }

            var editContentDisplayProperty = imcms.editOptions.isEditContent ? "" : "display:none";
            var editDocInfoDisplayProperty = imcms.editOptions.isEditDocInfo ? "" : "display:none";
            var adminDisplayProperty = imcms.isAdmin ? "" : "display:none";

            var versionedContentModifiers = imcms.isVersioningAllowed ? [] : ["versioning-off"];
            var publishVersionButtonModifiers = (imcms.isVersioningAllowed && imcms.document.hasNewerVersion)
                ? ["has-newer-version"] : [];
            var buttons = [
                {
                    name: 'settings',
                    tag: '<div>',
                    onClick: panelSettings.onSettingsClicked,
                    content: texts.settings,
                    title: texts.settingsTitle,
                    modifiers: ["settings"]
                }, {
                    name: 'public',
                    tag: '<a>',
                    href: imcms.contextPath + '/' + imcms.document.id,
                    content: texts["public"],
                    title: texts.publicTitle,
                    modifiers: ["public"],
                    style: editContentDisplayProperty
                }, {
                    name: 'edit',
                    tag: '<a>',
                    href: imcms.contextPath + "/servlet/AdminDoc?meta_id=" + imcms.document.id,
                    content: texts.edit,
                    title: texts.editTitle,
                    modifiers: ["edit"],
                    style: editContentDisplayProperty
                }, {
                    name: 'preview',
                    tag: "<a>",
                    href: imcms.contextPath + '/api/viewDoc/' + imcms.document.id + "?working-preview=true",
                    content: texts.preview,
                    title: texts.previewTitle,
                    modifiers: ["preview"].concat(versionedContentModifiers),
                    style: editContentDisplayProperty
                }, {
                    name: 'publish_offline',
                    tag: "<li>",
                    content: texts.publish,
                    title: texts.publishTitle,
                    onClick: publishDoc,
                    modifiers: ["publish-of"].concat(versionedContentModifiers, publishVersionButtonModifiers),
                    style: adminDisplayProperty
                }, {
                    name: 'page_info',
                    tag: "<li>",
                    content: texts.pageInfo,
                    title: texts.pageInfoTitle,
                    onClick: showPageInfo,
                    modifiers: ["page-info"],
                    style: editDocInfoDisplayProperty
                }, {
                    name: 'document',
                    tag: "<li>",
                    content: texts.document,
                    title: texts.documentTitle,
                    onClick: initDocumentEditor,
                    modifiers: ["document"],
                    style: adminDisplayProperty
                }, {
                    name: 'admin',
                    tag: "<a>",
                    href: imcms.contextPath + "/servlet/AdminManager",
                    content: texts.admin,
                    title: texts.adminTitle,
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
                languagesRestApi.replace({code: languageCode}).done(function () {
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

            var adminPanelElements$ = [
                $logoItem,
                $titleItem,
                $flagsItem,
                $buttonsContainer
            ];

            var panelAttributes = {
                id: "imcms-admin-panel",
                style: "top: -92px;"
            };
            return $panel = adminPanelBEM.buildBlock("<div>", adminPanelElements$, panelAttributes, "item");
        }

        function highlightPublishButton() {
            var panelItemPublishClassSelector = BEM.buildClassSelector(panelBlock, "item", "publish-of");

            $panelContainer.find(panelItemPublishClassSelector).addClass(panelItemHasNewerVersionClass);
        }

        var isPanelBuilt = false;
        var onPanelBuiltCallbacks = [];

        return {
            buildPanel: function (opts) {
                if ($panelContainer) {
                    return;
                }

                $panelContainer = $("<div>", {
                    "id": "imcms-admin",
                    "class": "imcms-admin",
                    html: createAdminPanel(opts)
                });

                panelVisibility.setShowHidePanelRules($panel);

                $("body").prepend($panelContainer);

                panelSettings.applyCurrentSettings();

                events.on("imcms-version-modified", highlightPublishButton);
                isPanelBuilt = true;

                onPanelBuiltCallbacks.forEach(function (callMe) {
                    setTimeout(callMe);
                });

                onPanelBuiltCallbacks = [];
            },
            callOnPanelBuilt: function (callOnPanelBuilt) {
                if (!callOnPanelBuilt || !callOnPanelBuilt.call) return;

                if (isPanelBuilt) {
                    setTimeout(callOnPanelBuilt);
                    return;
                }

                onPanelBuiltCallbacks.push(callOnPanelBuilt);
            }
        }
    }
);
