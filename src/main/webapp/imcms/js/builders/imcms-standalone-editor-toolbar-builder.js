/**
 * Created by Dmytro Zemlianskyi from Ubrainians for imCode
 * 14.09.18.
 */
define("imcms-standalone-editor-toolbar-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "jquery"
    ],
    function (BEM, componentsBuilder, $) {

        let $panelContainer, $panel;

        function buildFlags() {
            return componentsBuilder.flags.flagsContainer(language => ["<div>", {
                text: language.code,
                click: componentsBuilder.flags.onFlagClickReloadWithLangParam
            }]);
        }

        function createToolbarPanel(itemsToDisplay) {
            const toolbarBEM = new BEM({
                block: "imcms-editor-toolbar-panel",
                elements: {
                    "id": "",
                    "index": "",
                    "label": "",
                    "placeholder": ""
                }
            });

            let toolbarElements = [];
            itemsToDisplay.forEach(item => {
                                                                                                                                                                                                                                                                                                                                        let $itemContainer = $("<div>");

                switch (item.type) {
                    case 'language':
                        $itemContainer = buildFlags();
                        break;
                    case 'close':
                        let onClose;

                        if (item.link &&
                            ((item.showIfSeparate && window.opener) || (!item.showIfSeparate && !window.opener))) {

                            onClose = () => window.location.replace(`/api/redirect?returnUrl=${item.link}`);
                        } else if (window.opener) {
                            onClose = () => window.close();
                        } else break;

                        $itemContainer = $("<div>")
                            .append(componentsBuilder.buttons.closeButton().click(onClose))
                            .addClass("imcms-editor-toolbar-panel--close-button");
                        break;
                    default:
                        $itemContainer.append(
                            toolbarBEM.buildBlockElement(item.type, "<div>", {
                                text: item.text,
                                title: item.title,
                            })
                        );
                        break;
                }

                toolbarElements.push($itemContainer);
            });

            const panelAttributes = {
                id: "imcms-editor-toolbar-panel",
            };
            return $panel = toolbarBEM.buildBlock("<div>", toolbarElements, panelAttributes, "item");

        }

        return {
            buildPanel: itemsToDisplay => {
                if ($panelContainer) {
                    return;
                }

                $panelContainer = $("<div>", {
                    "id": "imcms-editor-toolbar",
                    "class": "imcms-editor-toolbar",
                    html: createToolbarPanel(itemsToDisplay)
                });

                $("body")
                    .addClass("standalone-editor-body")
                    .prepend($panelContainer);
            }
        }
    }
);
