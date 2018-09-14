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

        function createToolbarPanel() {
            const adminPanelBEM = new BEM({
                block: "imcms-editor-toolbar-panel",
                elements: {
                    "id": "",
                    "no": "",
                    "label": ""
                }
            });

            const $tag = $(".imcms-editor-area");

            const $id = adminPanelBEM.buildBlockElement("id", "<div>", {
                text: $tag.attr("data-doc-id"),
                title: "docId"
            });
            const $idItem = $("<div>").append($id);


            const $no = adminPanelBEM.buildBlockElement("no", "<div>", {
                text: $tag.attr("data-index"),
                title: "data index"
            });
            const $noItem = $("<div>").append($no);

            const $flagsItem = buildFlags();
            // var $buttonsContainer = buildPanelButtons(opts);

            const adminPanelElements$ = [
                $idItem,
                $noItem,
                $flagsItem,
                // $buttonsContainer
            ];

            const panelAttributes = {
                id: "imcms-editor-toolbar-panel",
            };
            return $panel = adminPanelBEM.buildBlock("<div>", adminPanelElements$, panelAttributes, "item");

        }

        return {
            buildPanel: () => {
                if ($panelContainer) {
                    return;
                }

                $panelContainer = $("<div>", {
                    "id": "imcms-editor-toolbar",
                    "class": "imcms-editor-toolbar",
                    html: createToolbarPanel()
                });

                console.log(imcms);

                $("body")
                    .addClass("standalone-editor-body")
                    .prepend($panelContainer);

            }
        }
    }
);
