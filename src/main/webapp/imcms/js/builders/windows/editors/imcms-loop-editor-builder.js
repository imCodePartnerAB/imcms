/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.08.17
 */
define("imcms-loop-editor-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-loops-rest-api", "imcms-window-builder", "jquery",
        "imcms-events", "imcms-i18n-texts", "imcms-modal-window-builder", "imcms-jquery-element-reload", "imcms-images-rest-api",
	    'imcms-texts-rest-api', 'imcms-cookies', 'imcms'
    ],
    function (BEM, components, loopREST, WindowBuilder, $, events, texts, modal, reloadElement, imagesRestApi, textsRestApi, cookies, imcms) {
        let $title, $body, $listItems;

        texts = texts.editors.loop;

        const modifiers = {
            ID: ["id"],
	        IMAGE_CONTAINER: ["image-container"],
	        IMAGE: ["image"],
            CONTENT: ["content"],
            CONTROLS: ["control"]
        };

	    const MISSING_ICON_IMAGE = "/imcms/images/icon_missing_image.png";

        let currentLoop;

        const LOOP_ITEM_CLASS = "imcms-loop-item";

        const itemsBEM = new BEM({
            block: "imcms-document-list__items imcms-loop-items-list",
            elements: {
                "item": LOOP_ITEM_CLASS
            }
        }, );

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

                    const loopItemIdClass = BEM.buildClassSelector(LOOP_ITEM_CLASS, "info", modifiers.ID[0]);
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

		function onResetSorting() {
			const defaultSortedListItems = $listItems.children()
				.toArray()
				.sort((listItem1, listItem2) => {
					const $listItem1 = $(listItem1);
					const $listItem2 = $(listItem2);

					const loopItemIdClass = BEM.buildClassSelector(LOOP_ITEM_CLASS, "info", modifiers.ID[0]);

					const entryIndex1 = +($listItem1.find(loopItemIdClass).text());
					const entryIndex2 = +($listItem2.find(loopItemIdClass).text());

					return entryIndex1 - entryIndex2;
				});
			$listItems.append(defaultSortedListItems)
		}

	    function enableDragging() {
		    const container = document.querySelector(".imcms-loop-list__items")
		    $listItems.children().toArray().forEach(entry => {
			    $(entry).off('dragstart dragend drag').on({
				    'dragstart': function () {
					    $(this).addClass('imcms-loop-items-list__item-dragging')
				    },
				    'dragend': function () {
					    $(this).removeClass('imcms-loop-items-list__item-dragging')
				    },
				    'drag': enableAutoScrollOnDrag
			    })
		    })
		    $(container).off('dragover').on('dragover', e => {
			    e.preventDefault()
			    const afterElement = getDragAfterElement(container, e.clientY)
			    const draggable = document.querySelector('.imcms-loop-items-list__item-dragging')
			    if (afterElement == null) {
				    container.appendChild(draggable)
			    } else {
				    container.insertBefore(draggable, afterElement)
			    }
		    })
	    }

	    function getDragAfterElement(container, y) {
		    const draggableElements = [...container.querySelectorAll('.imcms-loop-items-list__item:not(.imcms-loop-items-list__item-dragging)')]

		    return draggableElements.reduce((closest, child) => {
			    const box = child.getBoundingClientRect()
			    const offset = y - box.top - box.height / 2
			    if (offset < 0 && offset > closest.offset) {
				    return {offset: offset, element: child}
			    } else {
				    return closest
			    }
		    }, {offset: Number.NEGATIVE_INFINITY}).element
	    }

	    function enableAutoScrollOnDrag(event) {
		    if(event.originalEvent.clientY <= 150)
			    autoScroll(-5);
		    if(event.originalEvent.clientY >= ($listItems.height() - 30))
			    autoScroll(5);
	    }

	    function autoScroll(step) {
		    const scrollY = $listItems.scrollTop();
		    $listItems.scrollTop(scrollY + step);
	    }

        function buildEditor(opts) {
			addHeadData(opts);

            function getMaxLoopItemID() {
                return $listItems.children()
                    .toArray()
                    .map(listItem => {
                        const loopItemIdClass = BEM.buildClass(LOOP_ITEM_CLASS, "info", modifiers.ID[0]);
                        return +($(listItem).find("." + loopItemIdClass).text());
                    })
                    .sort((a, b) => (a - b))
                    .pop() || 0;
            }

            function onCreateNewClicked() {
                const newLoopEntry = {
                    index: getMaxLoopItemID() + 1,
	                image: MISSING_ICON_IMAGE,
	                content: "",
                    enabled: true
                };

                $listItems.append(itemsBEM.makeBlockElement("item", buildItem(newLoopEntry)));
				enableDragging()
            }

            const $head = loopWindowBuilder.buildHead(`${texts.title} - ${texts.page} ${opts.docId}, ${texts.loopTitle} ${opts.index} - ${texts.teaser} : `);
            $head.find(".imcms-title").append($title);

            const $footer = WindowBuilder.buildFooter([
                components.buttons.positiveButton({
                    text: texts.createNew,
                    click: onCreateNewClicked
                }),
                components.buttons.saveButton({
                    text: texts.saveAndClose,
                    click: onSaveAndCloseClicked
                }),
	            components.buttons.negativeButton({
		            text: texts.resetSorting,
		            click: onResetSorting
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
            });
            $id.modifiers = modifiers.ID;

	        const $imageContainer = $("<div>", {
		        text: texts.image,
	        });
	        $imageContainer.modifiers = modifiers.IMAGE_CONTAINER;

            const $content = $("<div>", {
                text: texts.content,
            });
            $content.modifiers = modifiers.CONTENT;

            const $isEnabled = $("<div>", {
                text: texts.isEnabled,
            });
            $isEnabled.modifiers = modifiers.CONTROLS;

            return new BEM({
                block: "imcms-loop-list-titles",
                elements: {
                    "title": [$id, $imageContainer, $content, $isEnabled]
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
            });
            $no.modifiers = modifiers.ID;

			const $imageContainer= $("<div>").attr({
		        src: loopEntry.image,
			});
	        $imageContainer.modifiers = modifiers.IMAGE_CONTAINER;
			const $image = $("<img>").attr({
		        src: loopEntry.image,
		        class: "imcms-loop-item__info--image",
				draggable: false
			})
	        $image.modifiers = modifiers.IMAGE;

	        $imageContainer.append($image)

            const $content = components.texts.titleText("<div>", loopEntry.content);
            $content.modifiers = modifiers.CONTENT;

            const $isEnabled = $("<div>").append(components.checkboxes.imcmsCheckbox("<div>", {
                name: "isEnabled" + loopEntry.no,
                checked: loopEntry.enabled ? "checked" : undefined,
            }));
            $isEnabled.modifiers = modifiers.CONTROLS;

            return new BEM({
                block: LOOP_ITEM_CLASS,
                elements: {
                    "info": [$no, $imageContainer, $content, $isEnabled],
                    "controls": buildControls()
                }
            }).buildBlockStructure("<div>", {draggable: true});
        }

        function buildItems(loop) {
            const blockElements = loop.entries.map(entry => ({"item": buildItem(entry)}));

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

            const $list = bodyBEM.makeBlockElement("list", buildLoopList(loop));
            $body.append($list);

	        enableDragging()
        }

	    function displayImages(images) {
		    $listItems.children()
			    .toArray()
			    .map(listItem => {
				    const $listItem = $(listItem);

				    const loopItemIdClass = BEM.buildClassSelector(LOOP_ITEM_CLASS, "info", modifiers.ID[0]);
				    const entryIndex = +($listItem.find(loopItemIdClass).text());

					const loopItemImageClass=BEM.buildClassSelector(LOOP_ITEM_CLASS,"info", modifiers.IMAGE[0]);

				    const entryImage = $listItem.find(loopItemImageClass)[0];

				    let imageToShow = images.find(image => entryIndex === image.loopEntryRef.loopEntryIndex);

				    entryImage.src = imageToShow ? imcms.imagesPath +"?path="+ imageToShow.generatedFilePath : MISSING_ICON_IMAGE;
			    })
	    }

		function displayTextsContent(texts) {
			$listItems.children()
				.toArray()
				.map(listItem => {
					const $listItem = $(listItem);

					const loopItemIdClass = BEM.buildClassSelector(LOOP_ITEM_CLASS, "info", modifiers.ID[0]);
					const entryIndex = +($listItem.find(loopItemIdClass).text());

					const loopItemContentClass = BEM.buildClassSelector(LOOP_ITEM_CLASS, "info", modifiers.CONTENT[0]);
					const entryContent = $listItem.find(loopItemContentClass)[0];

					let textToDisplay = texts.find(text => text.loopEntryRef.loopEntryIndex === entryIndex);
					entryContent.append(textToDisplay ? textToDisplay.text : "");
				})
		}

	    function addHeadData(opts) {
		    $title = $('<a>');

		    let linkData = imcms.contextPath + "/api/admin/loop?meta-id="
			    + opts.docId
			    + "&index=" + opts.index;

		    $title.text(linkData).css({
			    'text-transform': 'lowercase',
			    'color': '#fff2f9',
			    'overflow':'hidden',
			    'white-space':'nowrap',
				'text-overflow':'ellipsis'
		    });

		    $title.attr('href', linkData)
	    }

        function clearData() {
            events.trigger("loop editor closed");
            $title.text("Loop Editor");
            $body.empty();
        }

	    function loadData(opts) {
		    const requestDTO = {
			    docId: opts.docId,
			    index: opts.index,
			    langCode: cookies.getCookie("userLanguage"),
		    }
		    requestDTO["loopEntryRef.loopIndex"] = opts.index;

		    loopREST.read(opts).done(loop => {
			    buildData(loop);
			    imagesRestApi.getLoopImages(requestDTO).done(displayImages).fail(() => modal.buildErrorWindow(texts.error.loadFailed));
			    textsRestApi.getLoopTexts(requestDTO).done(displayTextsContent).fail(() => modal.buildErrorWindow(texts.error.loadFailed));
		    }).fail(() => modal.buildErrorWindow(texts.error.loadFailed));
        }

        const loopWindowBuilder = new WindowBuilder({
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
