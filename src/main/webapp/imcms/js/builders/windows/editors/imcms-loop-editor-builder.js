/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.08.17
 */
define("imcms-loop-editor-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-loops-rest-api", "imcms-window-builder", "jquery",
        "imcms-events", "imcms-i18n-texts", "imcms-modal-window-builder", "imcms-jquery-element-reload"
    ],
    function (BEM, components, loopREST, WindowBuilder, $, events, texts, modal, reloadElement) {
        let $title, $body, $listItems;

        texts = texts.editors.loop;

        const modifiers = {
            ID: ["id"],
            CONTENT: ["content"],
            CONTROLS: ["control"]
        };

        let currentLoop;

        const LOOP_ITEM_CLASS = "imcms-document-item";

        const itemsBEM = new BEM({
            block: "imcms-document-items-list",
            elements: {
                "document-item": LOOP_ITEM_CLASS
            }
        });

        const bodyBEM = new BEM({
            block: "imcms-loop-editor-body",
            elements: {
                "list": "imcms-loop-list"
            }
        });

        function getLoopData() {
            currentLoop.entries = $listItems.children()
                .toArray()
                .map(listItem => {
                    const $listItem = $(listItem);

                    const loopItemIdClass = BEM.buildClassSelector(LOOP_ITEM_CLASS, "info", modifiers.ID[1]);
                    const entryIndex = +($listItem.find(loopItemIdClass).text());

                    const loopItemControlsClass = BEM.buildClassSelector(LOOP_ITEM_CLASS, "info", modifiers.CONTROLS[1]);
                    const isEnabled = $listItem.find(loopItemControlsClass).find("input").is(":checked");

                    return {
                        index: entryIndex,
                        enabled: isEnabled
                    };
                });

            return currentLoop;
        }

        function onLoopSaved() {
            reloadElement($tag, () => {
                // TODO: It doesn't work with import in arguments
                const editorsInit = require("imcms-editors-initializer");
                editorsInit.initEditors();
            });
            loopWindowBuilder.closeWindow();
            events.trigger("imcms-version-modified");
        }

        function onSaveAndCloseClicked() {
            const loopElement = getLoopData();
            loopREST.create(loopElement)
                .done(onLoopSaved)
                .fail(() => modal.buildErrorWindow(texts.error.createFailed));
        }

        function buildEditor() {
            function getMaxLoopItemID() {
                return $listItems.children()
                    .toArray()
                    .map(listItem => {
                        const loopItemIdClass = BEM.buildClass(LOOP_ITEM_CLASS, "info", modifiers.ID[1]);
                        return +($(listItem).find("." + loopItemIdClass).text());
                    })
                    .sort((a, b) => (a - b))
                    .pop() || 0;
            }

            function onCreateNewClicked() {
                const newLoopEntry = {
                    index: getMaxLoopItemID() + 1,
                    content: "",
                    enabled: true
                };

                $listItems.append(itemsBEM.makeBlockElement("item", buildItem(newLoopEntry)));
            }

            const $head = loopWindowBuilder.buildHead(texts.title);
            $title = $head.find(".imcms-title");

            const $footer = WindowBuilder.buildFooter([
                components.buttons.positiveButton({
                    text: texts.createNew,
                    click: onCreateNewClicked
                }),
                components.buttons.saveButton({
                    text: texts.saveAndClose,
                    click: onSaveAndCloseClicked
                })
            ]);

            return new BEM({
                block: "imcms-loop-editor",
                elements: {
                    "head": $head,
                    "body": $body = bodyBEM.buildBlock("<div>"),
                    "footer": $footer
                }
            }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
        }

        function buildTitles() {
            const $id = $("<div>", {
                text: texts.id,
                class: "imcms-grid-col-18",
            });
            $id.modifiers = modifiers.ID;

            const $content = $("<div>", {
                text: texts.content,
                class: "imcms-flex--flex-1",
            });
            $content.modifiers = modifiers.CONTENT;

            const $isEnabled = $("<div>", {
                text: texts.isEnabled,
                class: "imcms-grid-col-1",
            });
            $isEnabled.modifiers = modifiers.CONTROLS;

            return new BEM({
                block: "imcms-document-list-titles",
                elements: {
                    "title": [$id, $content, $isEnabled]
                }
            }).buildBlockStructure("<div>");
        }

        function buildControls() {
            const $remove = components.controls.remove(() => {
                $remove.parents("." + LOOP_ITEM_CLASS).remove();
            });

            return components.controls.buildControlsBlock("<div>", [$remove]);
        }

        function buildItem(loopEntry) {
            const $no = components.texts.titleText("<div>", loopEntry.index, {
                class: "imcms-grid-col-18",
            });
            $no.modifiers = modifiers.ID;

            // todo: get content from the page!
            const $content = components.texts.titleText("<div>", loopEntry.content, {
                class: "imcms-flex--flex-1",
            });
            $content.modifiers = modifiers.CONTENT;

            const $isEnabled = $("<div>").append(components.checkboxes.imcmsCheckbox("<div>", {
                name: "isEnabled" + loopEntry.no,
                checked: loopEntry.enabled ? "checked" : undefined,
            }));
            $isEnabled.addClass("imcms-grid-col-1 imcms-flex--d-flex imcms-flex--justify-content-center");
            $isEnabled.modifiers = modifiers.CONTROLS;

            return new BEM({
                block: LOOP_ITEM_CLASS,
                elements: {
                    "info": [$no, $content, $isEnabled],
                    "controls": buildControls()
                }
            }).buildBlockStructure("<div>");
        }

        function buildItems(loop) {
            const blockElements = loop.entries.map(entry => ({"item": buildItem(entry)}));

            return itemsBEM.buildBlock("<div>", blockElements);
        }

        function buildLoopList(loop) {
            return new BEM({
                block: "imcms-document-list",
                elements: {
                    "titles": buildTitles(),
                    "items": $listItems = buildItems(loop)
                }
            }).buildBlockStructure("<div>");
        }

        function buildData(loop) {
            currentLoop = loop;

            addHeadData(loop);

            const $list = bodyBEM.makeBlockElement("list", buildLoopList(loop));
            $body.append($list);
        }

        function addHeadData(loop) {
            $title.append(": " + loop.docId + "-" + loop.index);
        }

        function clearData() {
            events.trigger("loop editor closed");
            $title.text("Loop Editor");
            $body.empty();
        }

        function loadData(opts) {
            loopREST.read(opts)
                .done(buildData)
                .fail(() => modal.buildErrorWindow(texts.error.loadFailed));
        }

        var loopWindowBuilder = new WindowBuilder({
            factory: buildEditor,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData,
            onEscKeyPressed: "close",
            onEnterKeyPressed: onSaveAndCloseClicked
        });

        let $tag;

        return {
            setTag: function ($editedTag) {
                $tag = $editedTag;
                return this;
            },
            build: function (opts) {
                loopWindowBuilder.buildWindow.apply(loopWindowBuilder, arguments);
            }
        };
    }
);
