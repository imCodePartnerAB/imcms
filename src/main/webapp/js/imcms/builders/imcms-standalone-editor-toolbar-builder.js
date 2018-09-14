/**
 * Created by Dmytro Zemlianskyi from Ubrainians for imCode
 * 14.09.18.
 */
define("imcms-standalone-editor-toolbar-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "jquery", "imcms", "imcms-events", "imcms-languages-rest-api",
    ],
    function (BEM, componentsBuilder, $, imcms, events, languagesRestApi) {

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
            const adminPanelBEM = new BEM({
                block: "imcms-editor-toolbar-panel",
                elements: {
                    "id": "",
                    "index": "",
                    "label": ""
                }
            });

            let toolbarElements = [];
            itemsToDisplay.forEach(function (item) {
                const $item = adminPanelBEM.buildBlockElement(item.type, "<div>", {
                    text: item.text,
                    title: item.title,
                });
                const $itemContainer = $("<div>").append($item);
                toolbarElements.push($itemContainer);
            });

            const $flagsItem = buildFlags();
            // var $buttonsContainer = buildPanelButtons(opts);

            toolbarElements.push($flagsItem);
            // toolbarElements.push($buttonsContainer);

            const panelAttributes = {
                id: "imcms-editor-toolbar-panel",
            };
            return $panel = adminPanelBEM.buildBlock("<div>", toolbarElements, panelAttributes, "item");

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

                console.log(imcms);

                $("body")
                    .addClass("standalone-editor-body")
                    .prepend($panelContainer);

            }
        }
    }
);
