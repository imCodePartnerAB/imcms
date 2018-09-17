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

        function flagOnClick() {
            const languageCode = $(this).text();

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

        function createToolbarPanel(itemsToDisplay) {
            const toolbarBEM = new BEM({
                block: "imcms-editor-toolbar-panel",
                elements: {
                    "id": "",
                    "index": "",
                    "label": ""
                }
            });

            let toolbarElements = [];
            itemsToDisplay.forEach(function (item) {
                let $itemContainer;

                switch (item.type) {
                    case 'language':
                        $itemContainer = buildFlags();
                        break;
                    default:
                        const $item = toolbarBEM.buildBlockElement(item.type, "<div>", {
                            text: item.text,
                            title: item.title,
                        });
                        $itemContainer = $("<div>").append($item);
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
            buildPanel: function (itemsToDisplay) {
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
