/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
define("imcms-admin-panel-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-page-info-builder", "imcms-document-editor-builder",
        "jquery", "imcms", "imcms-events", "imcms-top-panel-visibility-initiator",
        "imcms-i18n-texts", "imcms-admin-panel-settings-builder", "imcms-modal-window-builder"
    ],
    function (BEM, componentsBuilder, pageInfoBuilder, documentEditorBuilder, $, imcms, events,
              panelVisibility, texts, panelSettings, modal) {

        let $panelContainer, $panel;

        const panelBlock = "imcms-panel";
        const panelItemClass = BEM.buildClass(panelBlock, "item");
        const panelItemHasNewerVersionClass = BEM.buildClass(panelBlock, "item", "has-newer-version");

        let textStatus = texts.status;
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
            const panelButtonsBEM = new BEM({
                block: panelBlock,
                elements: {
                    "items": "",
                    "item": panelItemClass
                }
            });

            function buildPanelButton(buttonData) {
                const attributes = {
                    html: buttonData.content,
                    title: buttonData.title,
                    href: buttonData.href,
                    target: buttonData.target,
                    click: buttonData.onClick,
                    style: buttonData.style
                };

                if (opts && opts.active === buttonData.name) {
                    attributes["class"] = BEM.buildClass(panelBlock, "item", "active");
                }

                return panelButtonsBEM.buildBlockElement("item", buttonData.tag, attributes, buttonData.modifiers);
            }

            const isPreviewVersion = imcms.isPreviewMode && new URLSearchParams(window.location.search).get("version-no");

            const editContentDisplayProperty = imcms.editOptions.isEditContent && !isPreviewVersion ? "" : "display:none";
            const editDocInfoDisplayProperty = imcms.editOptions.isEditDocInfo ? "" : "display:none";
            const publishDisplayProperty = (imcms.accessToPublishCurrentDoc || imcms.isSuperAdmin) && !isPreviewVersion ? "" : "display:none";
            const documentDisplayProperty = (imcms.isSuperAdmin || imcms.accessToDocumentEditor) && !isPreviewVersion ? "" : "display:none";
            const adminDisplayProperty = (imcms.isSuperAdmin || imcms.accessToAdminPages) && !isPreviewVersion ? "" : "display:none";
            const logoutDisplayProperty = !isPreviewVersion ? "" : "display:none"

            const versionedContentModifiers = imcms.isVersioningAllowed ? [] : ["versioning-off"];
            const publishVersionButtonModifiers = (imcms.isVersioningAllowed && imcms.document.hasNewerVersion)
                ? ["has-newer-version"] : [];
            const buttons = [
                {
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
                    style: publishDisplayProperty
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
                    style: documentDisplayProperty
                }, {
                    name: 'admin',
                    tag: "<a>",
                    href: imcms.contextPath + "/api/admin/manager",
                    target: '_blank',
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
                    modifiers: ["logout"],
                    style: logoutDisplayProperty
                }, {
                    name: 'settings',
                    tag: '<div>',
                    onClick: panelSettings.onSettingsClicked,
                    title: texts.settingsTitle,
                    modifiers: ["settings"]
                }
            ].map(buildPanelButton);

            const $buttonsWrapper = $("<ul>").append(buttons);

            return panelButtonsBEM.buildBlock("<div>", [{"items": $buttonsWrapper}]);
        }

        function buildFlags() {
            return componentsBuilder.flags.flagsContainer((language) => ["<div>", {
                text: language.code,
                click: componentsBuilder.flags.onFlagClickReloadWithLangParam
            }]);
        }

        function createAdminPanel(opts) {
            const adminPanelBEM = new BEM({
                block: "imcms-admin-panel",
                elements: {
                    "item": "",
                    "logo": "",
                    "title": "",
                    "meta": ""
                }
            });

            const $logo = adminPanelBEM.buildBlockElement("logo", "<a>", {href: ""}); // fixme: link to start doc?
            const $logoItem = $("<div>").append($logo);

            const $title = adminPanelBEM.buildBlockElement("title", "<div>", {text: imcms.version});
            const $titleItem = $("<div>").append($title);

            const $meta = adminPanelBEM.buildBlockElement("meta", "<div>", {
                text: imcms.document.id,
                title: imcms.document.alias
            });
            const $metaItem = $("<div>").append($meta);

            if(imcms.isInWasteBasket){
                const $trash = componentsBuilder.controls.trash().attr("title", textStatus.tooltip.wasteBasket);
                $metaItem.append($trash);
            }

            const $flagsItem = buildFlags();
            const $buttonsContainer = buildPanelButtons(opts);

            const adminPanelElements$ = [
                $logoItem,
                $titleItem,
                $flagsItem,
                $metaItem,
                $buttonsContainer
            ];

            const panelAttributes = {
                id: "imcms-admin-panel",
                style: "top: -92px;"
            };
            return $panel = adminPanelBEM.buildBlock("<div>", adminPanelElements$, panelAttributes, "item");
        }

        function highlightPublishButton() {
            const panelItemPublishClassSelector = BEM.buildClassSelector(panelBlock, "item", "publish-of");

            $panelContainer.find(panelItemPublishClassSelector).addClass(panelItemHasNewerVersionClass);
        }

        let isPanelBuilt = false;
        let onPanelBuiltCallbacks = [];

        return {
            buildPanel: opts => {
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
        };
    }
);
