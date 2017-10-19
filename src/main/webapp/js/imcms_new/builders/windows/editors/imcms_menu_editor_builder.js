/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 10.08.17.
 */
Imcms.define("imcms-menu-editor-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-document-editor-builder", "imcms-modal-window-builder",
        "imcms-window-builder", "imcms-menus-rest-api", "imcms-controls-builder", "imcms-page-info-builder", "jquery"
    ],
    function (BEM, components, documentEditorBuilder, imcmsModalWindow, WindowBuilder, menusRestApi,
              controls, pageInfoBuilder, $) {

        var $title, $menuElementsContainer, $documentsContainer;
        var docId, menuId;

        function saveAndClose() {
            // fixme: just closing now, should be save and close
            menuWindowBuilder.closeWindow();
        }

        function buildHead() {
            var $head = menuWindowBuilder.buildHead("menu editor");
            $title = $head.find(".imcms-title");

            return $head;
        }

        function buildBody() {
            return new BEM({
                block: "imcms-menu-editor-body",
                elements: {
                    "left-side": $menuElementsContainer = $("<div>", {"class": "imcms-left-side"}),
                    "right-side": $documentsContainer = $("<div>", {"class": "imcms-right-side"})
                }
            }).buildBlockStructure("<div>");
        }

        function buildFooter() {
            var $saveAndClose = components.buttons.saveButton({
                text: "Save and close",
                click: saveAndClose
            });

            return menuWindowBuilder.buildFooter([$saveAndClose]);
        }

        function buildMenuEditorContent(menuElementsTree) {
            function removeMenuItemFromEditor(currentMenuItem) {
                var submenuItem = currentMenuItem.parent().find(".imcms-menu-items"),
                    parentMenuItem = currentMenuItem.closest(".imcms-menu-items"),
                    currentMenuItemWrap = parentMenuItem.parent();

                submenuItem.remove();
                currentMenuItem.remove();
                parentMenuItem.remove();

                if (currentMenuItemWrap.children().length === 1) {
                    currentMenuItemWrap.find(".imcms-menu-item__btn").remove();
                }
            }

            function removeMenuItem() {
                var currentMenuItem = $(this).closest(".imcms-menu-item"),
                    currentMenuItemName = currentMenuItem.find(".imcms-menu-item__info").text();

                var question = "Do you want to remove menu item \"" + currentMenuItemName + "\"?";
                imcmsModalWindow.buildModalWindow(question, function (answer) {
                    if (!answer) {
                        return;
                    }

                    removeMenuItemFromEditor(currentMenuItem)
                });
            }

            function buildMenuItemControls(menuItemDocId) {
                var $controlMove = controls.move();
                var $controlRemove = controls.remove(function () {
                    removeMenuItem.call(this, menuItemDocId);
                });
                var $controlEdit = controls.edit(function () {
                    pageInfoBuilder.build(menuItemDocId);
                });

                return controls.buildControlsBlock("<div>", [
                    $controlMove,
                    $controlRemove,
                    $controlEdit
                ]);
            }

            function showHideSubmenu() {
                var $btn = $(this),
                    level = $btn.parents(".imcms-menu-items").attr("data-menu-items-lvl")
                ;

                level = parseInt(level) + 1;
                $btn.parents(".imcms-menu-items")
                    .find(".imcms-menu-items[data-menu-items-lvl=" + level + "]")
                    .each(function () {
                        $(this).slideToggle()
                    });
                $btn.toggleClass("imcms-menu-item-btn--open");
            }

            function buildMenuItems(menuElementTree) {
                var elements = {};

                if (menuElementTree.children.length) {
                    elements.btn = $("<div>", {click: showHideSubmenu});
                }

                elements.info = components.texts.titleText("<div>", menuElementTree.documentId + " - " + menuElementTree.title);
                elements.controls = buildMenuItemControls(menuElementTree.documentId);

                return new BEM({
                    block: "imcms-menu-item",
                    elements: elements
                }).buildBlockStructure("<div>");
            }

            function buildMenuItemTree(menuElementTree, level) {
                menuElementTree.children = menuElementTree.children || [];

                var treeBlock = new BEM({
                    block: "imcms-menu-items",
                    elements: {
                        "menu-item": buildMenuItems(menuElementTree)
                    }
                }).buildBlockStructure("<div>", {"data-menu-items-lvl": level});

                ++level;

                var $childElements = menuElementTree.children.map(function (childElement) {
                    return buildMenuItemTree(childElement, level).addClass("imcms-submenu-items--close");
                });

                return treeBlock.append($childElements);
            }

            function buildMenuElements(menuElementsTree) {
                var $menuItems = menuElementsTree.map(function (menuElementTree) {
                    return buildMenuItemTree(menuElementTree, 1);
                });

                return new BEM({
                    block: "imcms-menu-items-tree",
                    elements: {
                        "menu-items": $menuItems
                    }
                }).buildBlockStructure("<div>");
            }

            function buildMenuTitlesRow() {
                var $idColumnHead = $("<div>", {
                    "class": "imcms-grid-coll-2",
                    text: "id"
                });
                var $titleColumnHead = $("<div>", {
                    "class": "imcms-grid-coll-2",
                    text: "Title"
                });

                return new BEM({
                    block: "imcms-menu-list-titles",
                    elements: {
                        "title": [$idColumnHead, $titleColumnHead]
                    }
                }).buildBlockStructure("<div>");
            }


            return new BEM({
                block: "imcms-menu-list",
                elements: {
                    "titles": buildMenuTitlesRow(),
                    "items": buildMenuElements(menuElementsTree)
                }
            }).buildBlockStructure("<div>");
        }

        function fillEditorContent(menuElementsTree) {
            var $menuElementsTree = buildMenuEditorContent(menuElementsTree);
            $menuElementsContainer.append($menuElementsTree);

            var $documentEditor = documentEditorBuilder.buildBody();
            $documentsContainer.append($documentEditor);
            documentEditorBuilder.loadDocumentEditorContent.applyAsync([{moveEnable: true}]);
        }

        function loadMenuEditorContent(opts) {
            addHeadData(opts);
            menusRestApi.read(opts).done(fillEditorContent);
        }

        function addHeadData(opts) {
            $title.append(": " + opts.docId + "-" + opts.menuId);
        }

        function buildMenuEditor(opts) {
            docId = opts.docId;
            menuId = opts.menuId;

            return new BEM({
                block: "imcms-menu-editor",
                elements: {
                    "head": buildHead(),
                    "body": buildBody(),
                    "footer": buildFooter()
                }
            }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
        }

        function clearData() {
            $title.text("Menu Editor");
            $menuElementsContainer.add($documentsContainer).empty();
        }

        var menuWindowBuilder = new WindowBuilder({
            factory: buildMenuEditor,
            loadDataStrategy: loadMenuEditorContent,
            clearDataStrategy: clearData
        });

        return {
            build: function (opts) {
                menuWindowBuilder.buildWindow.applyAsync(arguments, menuWindowBuilder);
            }
        };
    }
);
