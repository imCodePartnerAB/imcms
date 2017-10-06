/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.08.17
 */
Imcms.define("imcms-loop-editor-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-loop-rest-api", "imcms-window-builder",
        "imcms-controls-builder", "jquery"
    ],
    function (BEM, components, loopREST, WindowBuilder, controls, $) {
        var $title, $body, $listItems;

        var modifiers = {
            ID: ["col-1", "id"],
            CONTENT: ["col-10", "content"],
            CONTROLS: ["col-1", "control"]
        };

        var currentLoop;

        var LOOP_ITEM_CLASS = "imcms-loop-item";

        var itemsBEM = new BEM({
            block: "imcms-loop-items",
            elements: {
                "item": LOOP_ITEM_CLASS
            }
        });

        var bodyBEM = new BEM({
            block: "imcms-loop-editor-body",
            elements: {
                "list": "imcms-loop-list"
            }
        });

        function buildEditor() {
            function getMaxLoopItemID() {
                return $listItems.children()
                    .toArray()
                    .map(function (listItem) {
                        var loopItemIdClass = BEM.buildClass(LOOP_ITEM_CLASS, "info", modifiers.ID[1]);
                        return +($(listItem).find("." + loopItemIdClass).text());
                    })
                    .sort(function (a, b) {
                        return (a - b);
                    })
                    .pop();
            }

            function onCreateNewClicked() {
                var newLoopEntry = {
                    no: getMaxLoopItemID() + 1,
                    content: "",
                    enabled: true
                };

                $listItems.append(itemsBEM.makeBlockElement("item", buildItem(newLoopEntry)));
            }

            function getLoopData() {
                currentLoop.entries = $listItems.children()
                    .toArray()
                    .map(function (listItem) {
                        var $listItem = $(listItem);

                        var loopItemIdClass = BEM.buildClassSelector(LOOP_ITEM_CLASS, "info", modifiers.ID[1]);
                        var entryIndex = +($listItem.find(loopItemIdClass).text());

                        var loopItemControlsClass = BEM.buildClassSelector(LOOP_ITEM_CLASS, "info", modifiers.CONTROLS[1]);
                        var isEnabled = $listItem.find(loopItemControlsClass).find("input").is(":checked");

                        return {
                            index: entryIndex,
                            enabled: isEnabled
                        };
                    });

                return currentLoop;
            }

            function onSaveAndCloseClicked() {
                var loopElement = getLoopData();

                loopREST.create(loopElement)
                    .success(loopWindowBuilder.closeWindow.bind(loopWindowBuilder))
                    .error(console.error.bind(console));
            }

            var $head = loopWindowBuilder.buildHead("Loop Editor");
            $title = $head.find(".imcms-title");

            var $footer = loopWindowBuilder.buildFooter([
                components.buttons.positiveButton({
                    text: "Create new",
                    click: onCreateNewClicked
                }),
                components.buttons.saveButton({
                    text: "Save and close",
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
            var $id = $("<div>", {text: "id"});
            $id.modifiers = modifiers.ID;

            var $content = $("<div>", {text: "text content"});
            $content.modifiers = modifiers.CONTENT;

            var $isEnabled = $("<div>", {text: "is enabled"});
            $isEnabled.modifiers = modifiers.CONTROLS;

            return new BEM({
                block: "imcms-loop-list-titles",
                elements: {
                    "title": [$id, $content, $isEnabled]
                }
            }).buildBlockStructure("<div>");
        }

        function buildControls() {
            var $remove = controls.remove(function () {
                var $item = $remove.parents("." + LOOP_ITEM_CLASS);
                $item.detach();
            });

            return controls.buildControlsBlock("<div>", [$remove]);
        }

        function buildItem(loopEntry) {
            var $no = components.texts.titleText("<div>", loopEntry.index);
            $no.modifiers = modifiers.ID;

            // todo: get content from the page!
            var $content = components.texts.titleText("<div>", loopEntry.content);
            $content.modifiers = modifiers.CONTENT;

            var $isEnabled = components.checkboxes.imcmsCheckbox("<div>", {
                name: "isEnabled" + loopEntry.no,
                checked: loopEntry.enabled ? "checked" : undefined
            });
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
            var blockElements = loop.entries.map(function (entry) {
                return {"item": buildItem(entry)};
            });

            return itemsBEM.buildBlock("<div>", blockElements);
        }

        function buildLoopList(loop) {
            return new BEM({
                block: "imcms-loop-list",
                elements: {
                    "titles": buildTitles(),
                    "items": $listItems = buildItems(loop)
                }
            }).buildBlockStructure("<div>");
        }

        function buildData(loop) {
            currentLoop = loop;

            addHeadData(loop);

            var $list = bodyBEM.makeBlockElement("list", buildLoopList(loop));
            $body.append($list);
        }

        function addHeadData(loop) {
            $title.append(": " + loop.docId + "-" + loop.index);
        }

        function clearData() {
            $title.text("Loop Editor");
            $body.empty();
        }

        function loadData(opts) {
            loopREST.read(opts).done(buildData);
        }

        var loopWindowBuilder = new WindowBuilder({
            factory: buildEditor,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData
        });

        return {
            build: function (opts) {
                loopWindowBuilder.buildWindow.applyAsync(arguments, loopWindowBuilder);
            }
        }
    }
);
