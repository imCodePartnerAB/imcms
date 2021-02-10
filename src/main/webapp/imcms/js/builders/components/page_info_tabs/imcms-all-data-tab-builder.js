define(
    'imcms-all-data-tab-builder',
    ["imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery",
        "imcms-page-info-tab", 'imcms', 'imcms-all-data-document-rest-api', 'imcms-modal-window-builder'],
    function (BEM, components, texts, $, PageInfoTab, imcms, allDataDocumentRestApi, modal) {

        localization = texts.pageInfo.allData;
        const docId = imcms.document.id;

        let $documentTextDataContainer = $('<div>', {
            'class': 'imcms-all-data-text-container'
        });
        let $documentMenuDataContainer = $('<div>', {
            'class': 'imcms-all-data-menu-container'
        });
        let $documentLoopDataContainer = $('<div>', {
            'class': 'imcms-all-data-loop-container'
        });
        let $documentImageDataContainer = $('<div>', {
            'class': 'imcms-all-data-image-container'
        });

        function buildDataList() {

           allDataDocumentRestApi.getAllDataDocument(docId).done(dataDocumentDTO => {

               buildTextList(dataDocumentDTO.textsDTO);
               buildMenuList(dataDocumentDTO.menusDTO);
               buildLoopList(dataDocumentDTO.loopsDTO);
               buildImageList(dataDocumentDTO.imagesDTO);
           }).fail(() => modal.buildErrorWindow(localization.errorGettingData));
            function buildTextList(textsData) {
                $documentTextDataContainer.append(components.texts.titleText('<div>', localization.text.title).addClass('imcms-all-data-title'));

                if (textsData === undefined || textsData === null || textsData.length === 0){
                    $documentTextDataContainer.append(components.texts.infoText('<div>', localization.noData));
                }else {
                    let $frameContainer = $('<div>', {
                        'class': 'all-data-text'
                    });

                    $frameContainer.append(
                        textsData.map(textDTO => {
                            return new BEM({
                                block: 'imcms-text-data-info',
                                elements: {
                                    'info': $('<div>', {
                                        html: [
                                            buildText(localization.index, textDTO.index).css('width', '140px'),
                                            buildText(localization.insideLoop, isElementInsideLoopEntry(textDTO.loopEntryRef)).css('width', '140px'),
                                            buildText(localization.text.access, textDTO.htmlFilteringPolicy).css('width', '140px'),
                                            buildText(localization.text.type, textDTO.type).css('width', '140px')
                                        ]
                                    }),
                                    'content': $('<div>', {
                                        html: [
                                            buildInput(textDTO.text),
                                            buildEditButton('text', textDTO.index, textDTO.loopEntryRef)
                                        ]
                                    })
                                }
                            }).buildBlockStructure('<div>')
                        })
                    );
                    $documentTextDataContainer.append($frameContainer);
                }
                $documentTextDataContainer.append($('<hr/>'));
            }

            function buildMenuList(menusData){

                $documentMenuDataContainer.append(components.texts.titleText('<div>', localization.menu.title).addClass('imcms-all-data-title'));

                if (menusData === undefined || menusData === null || menusData.length === 0){
                    $documentMenuDataContainer.append(components.texts.infoText('<div>', localization.noData))
                }else {

                    let $frameContainer = $('<div>', {
                        'class': 'all-data-menu'
                    });

                    $frameContainer.append(
                        menusData.map(menuDTO => {
                            const htmlComponents = [
                                buildText(localization.index, menuDTO.menuIndex),
                                buildText(localization.insideLoop, isElementInsideLoopEntry(menuDTO.loopEntryRef)),
                                buildText(localization.menu.countElements, menuDTO.menuItems.length)
                            ];
                            return buildContentContainerBEM('menu', htmlComponents, menuDTO.menuIndex, menuDTO.loopEntryRef);
                        })
                    );
                    $documentMenuDataContainer.append($frameContainer);
                }
                $documentMenuDataContainer.append($('<hr/>'));
            }

            function buildLoopList(loopsData){
                $documentLoopDataContainer.append(components.texts.titleText('<div>', localization.loop.title).addClass('imcms-all-data-title'));

                if (loopsData === undefined || loopsData === null || loopsData.length === 0){
                $documentLoopDataContainer.append(components.texts.infoText('<div>', localization.noData));
                }else {
                    loopsData = sortData(loopsData, "index");

                    let $frameContainer = $('<div>', {
                        'class': 'all-data-loop'
                    });

                    $frameContainer.append(
                        loopsData.map(loopDTO => {
                            const htmlComponents = [
                                buildText(localization.index, loopDTO.index),
                                buildText(localization.loop.countElements, loopDTO.entries.length)
                            ];
                            return buildContentContainerBEM('loop', htmlComponents, loopDTO.index, null);
                        })
                    );
                    $documentLoopDataContainer.append($frameContainer);
                }
                $documentLoopDataContainer.append($('<hr/>'));
            }

            function buildImageList(imagesData){
                $documentImageDataContainer.append(components.texts.titleText('<div>', localization.image.title).addClass('imcms-all-data-title'));

                if (imagesData === undefined || imagesData === null || imagesData.length === 0) {
                    $documentImageDataContainer.append(components.texts.infoText('<div>', localization.noData));
                }else {
                    let $frameContainer = $('<div>', {
                        'class': 'all-data-loop'
                    });

                    $frameContainer.append(
                        imagesData.map(imageDTO => {
                            const htmlComponents = [
                                buildText(localization.index, imageDTO.index),
                                buildText(localization.insideLoop, isElementInsideLoopEntry(imageDTO.loopEntryRef !== null)),
                                buildText(localization.image.allLanguages, imageDTO.allLanguages),
                                buildText(localization.image.path, imageDTO.path)
                            ];
                            return buildContentContainerBEM('image', htmlComponents, imageDTO.index, imageDTO.loopEntryRef);
                        })
                    );
                    $documentImageDataContainer.append($frameContainer);
                }
                $documentImageDataContainer.append($('<hr/>'));
            }

            function buildContentContainerBEM(name, htmlComponents, index, loopEntryRef){
                return new BEM({
                    block: 'imcms-all-data-info',
                    elements: {
                        'content' : $('<div>', {
                            html: htmlComponents
                        }),
                        'button': buildEditButton(name, index, loopEntryRef)
                    }
                }).buildBlockStructure('<div>');
            }

            function buildEditButton(nameItem, index, loopEntryRef) {
                const linkToEditor = buildLinkToEditor(nameItem, index, loopEntryRef);
                return components.buttons.positiveButton( {
                    text: localization.edit,
                    click: () => {
                        window.open(linkToEditor,'_blank');
                    }
                }).addClass('all-data-edit-button');
            }

            function buildLinkToEditor(nameItem, index, loopEntryRef){
                let indexInLink = '&index=' + index;
                let loopIndexInLink = '';
                let loopEntryIndexInLink = '';
                if(loopEntryRef !== undefined && loopEntryRef !== null){
                    loopIndexInLink = '&loop-index=' + loopEntryRef.loopIndex;
                    loopEntryIndexInLink = '&loop-entry-index=' + loopEntryRef.loopEntryIndex;
                }

                return '/api/admin/' + nameItem + '?meta-id=' + docId + indexInLink +
                    loopIndexInLink + loopEntryIndexInLink;
            }

            function isElementInsideLoopEntry(loopEntryRef){
                return loopEntryRef !== null
            }

            function buildText(name, value){
                return components.texts.infoText('<div>', name + ': ' + value).addClass('info-text')
            }

            function buildInput(value) {
                let $testInput = components.texts.textArea('<div>', {
                    placeholder: value,
                    readonly: true
                });
                $testInput.$input.addClass('text-content-input');

                return $testInput;
            }

            function sortData(array, valueName){
                return array.sort(getSortOrder(valueName));
            }

            function getSortOrder(valueName) {
                return function(a, b) {
                    if (a[valueName] > b[valueName]) {
                        return 1;
                    } else if (a[valueName] < b[valueName]) {
                        return -1;
                    }
                    return 0;
                }
            }

            return new BEM({
                block: 'all-data-document',
                elements: {
                    'texts' : $documentTextDataContainer,
                    'menus' : $documentMenuDataContainer,
                    'loops' : $documentLoopDataContainer,
                    'images': $documentImageDataContainer
                }
            }).buildBlockStructure('<div>');
        }

        const AllDataTab = function (name) {
            PageInfoTab.call(this, name);
        };

        AllDataTab.prototype = Object.create(PageInfoTab.prototype);

        AllDataTab.prototype.isDocumentTypeSupported = () => {
            return true; // all supported
        };
        AllDataTab.prototype.fillTabDataFromDocument = document => {

        }

        AllDataTab.prototype.tabElementsFactory = () => [
            buildDataList()
        ];

        AllDataTab.prototype.clearTabData = () => {
            $documentTextDataContainer.empty();
            $documentMenuDataContainer.empty();
            $documentLoopDataContainer.empty();
            $documentImageDataContainer.empty();
        };

        return new AllDataTab(localization.name);
        }

    );