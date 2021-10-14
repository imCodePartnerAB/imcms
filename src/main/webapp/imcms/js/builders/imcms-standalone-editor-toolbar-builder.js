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
            const urlParams = new URLSearchParams(window.location.search);
            const languageParamName = 'lang';
            const languageCode = $(this).text();

            if (urlParams.has(languageParamName)) {
                urlParams.delete(languageParamName);
            }
            urlParams.append(languageParamName, languageCode);

            location.href = location.origin + location.pathname + '?' + urlParams.toString();
        }

        function buildFlags() {
            return componentsBuilder.flags.flagsContainer(language => ["<div>", {
                text: language.code,
                click: flagOnClick
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
                        if(window.opener != null){
                            $itemContainer = $("<div>")
                                .append(componentsBuilder.buttons.closeButton().click(() => window.close()))
                                .addClass("imcms-editor-toolbar-panel--close-button");
                        }
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
