define(
    'imcms-all-data-tab-builder',
    ["imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery",
        "imcms-page-info-tab", 'imcms', 'imcms-all-data-document-rest-api', 'imcms-modal-window-builder'],
    function (BEM, components, texts, $, PageInfoTab, imcms, allDataDocumentRestApi, modal) {

        const localization = texts.pageInfo.allData;

        let $documentTextDataContainer = $('<div>', {
            'class': 'imcms-all-data-text-container'
        });
        let $documentMenuDataContainer = $('<div>', {
            'class': 'imcms-all-data-menu-container'
        });
        let $documentCategoryDataContainer = $('<div>', {
            'class': 'imcms-all-data-category-container'
        });
        let $documentLoopDataContainer = $('<div>', {
            'class': 'imcms-all-data-loop-container'
        });
        let $documentImageDataContainer = $('<div>', {
            'class': 'imcms-all-data-image-container'
        });

        function buildDataList(docId) {

	        allDataDocumentRestApi.getAllDataDocument(docId)
		        .fail(() => {
			        if (docId !== null)
				        modal.buildErrorWindow(localization.errorGettingData)
		        })
		      .always((dataDocumentDTO) => {
			    buildTextList(dataDocumentDTO.textsDTO);
			    buildMenuList(dataDocumentDTO.menusDTO);
			    buildCategoryList(dataDocumentDTO.categoriesDTO);
				buildLoopList(dataDocumentDTO.loopDataDTO);
			    buildImageList(dataDocumentDTO.imagesDTO);
		     });

	        function buildTextList(textsData) {
		        $documentTextDataContainer.append(buildTextTitle(localization.text.title));

                if (textsData === undefined || textsData === null || textsData.length === 0){
                    $documentTextDataContainer.append(buildTextInfo(localization.noData));
                }else {
                    let $frameContainer = $('<div>', {
                        'class': 'all-data-text'
                    });

                    $frameContainer.append(
                        textsData.map(textDTO => {
                            return buildTextContentContainerBEM(textDTO);
                        })
                    );
                    $documentTextDataContainer.append($frameContainer);
                }
                $documentTextDataContainer.append($('<hr/>'));
            }

            function buildTextContentContainerBEM(textDTO){
                return new BEM({
                    block: 'imcms-text-data',
                    elements: {
                        'info': $('<div>', {
                            html: [
                                buildTextProperty(localization.index, textDTO.index),
                                buildTextProperty(localization.text.access, textDTO.htmlFilteringPolicy),
                                buildTextProperty(localization.text.type, textDTO.type),
                                buildTextProperty(localization.language, textDTO.langCode),
                            ]
                        }),
                        'content': $('<div>', {
                            html: [
                                buildInput(textDTO.text),
                                buildEditButton('text', textDTO.index, textDTO.loopEntryRef, textDTO.langCode)
                            ]
                        })
                    }
                }).buildBlockStructure('<div>')
            }

            function buildMenuList(menusData){

                $documentMenuDataContainer.append(buildTextTitle(localization.menu.title));

                if (menusData === undefined || menusData === null || menusData.length === 0){
                    $documentMenuDataContainer.append(buildTextInfo(localization.noData));
                }else {

                    let $frameContainer = $('<div>', {
                        'class': 'all-data-menu'
                    });
                    menusData = sortData(menusData, 'menuIndex');

                    $frameContainer.append(
                        menusData.map(menuDTO => {
                            return buildMenuContentContainerBEM(menuDTO.menuIndex, menuDTO.menuItems.length);
                        })
                    );
                    $documentMenuDataContainer.append($frameContainer);
                }
                $documentMenuDataContainer.append($('<hr/>'));
            }

            function buildMenuContentContainerBEM(index, countElements){
                return new BEM({
                    block: 'imcms-menu-data',
                    elements: {
                        'info': $('<div>', {
                            html: [buildTextProperty(localization.index, index),
                                buildTextProperty(localization.menu.countElements, countElements)]
                        }),
                        'button': buildEditButton('menu', index, null, null)
                    }
                }).buildBlockStructure('<div>');
            }

            function buildCategoryList(categoriesData){

                $documentCategoryDataContainer.append(buildTextTitle(localization.category.title));

                if (categoriesData === undefined || categoriesData === null || categoriesData.length === 0){
                    $documentCategoryDataContainer.append(buildTextInfo(localization.noData))
                }else {

                    let $frameContainer = $('<div>', {
                        'class': 'all-data-category'
                    });

                    $frameContainer.append(
                        categoriesData.map(categoryDTO => {
                            return buildCategoryContentContainerBEM(categoryDTO.type.id, categoryDTO.type.visible, categoryDTO.type.name,
                                                                categoryDTO.id, categoryDTO.name)
                        })
                    );
                    $documentCategoryDataContainer.append($frameContainer);
                }
                $documentCategoryDataContainer.append($('<hr/>'));
            }

            function buildCategoryContentContainerBEM(typeId, typeVisible, typeName, categoryId, categoryName){
                return new BEM({
                    block: 'imcms-category-data',
                    elements: {
                        'typeId': buildTextProperty(localization.id, typeId),
                        'visible': buildTextProperty(localization.category.visible, typeVisible),
                        'typeName': buildTextInfo(typeName),
                        'dash': buildTextInfo('—\t'),
                        'categoryId': buildTextProperty(localization.id, categoryId),
                        'categoryName': buildTextInfo(categoryName)
                    }
                }).buildBlockStructure('<div>');
            }

            function buildLoopList(loopsContentData){
                let loopsData = loopsContentData.loopsDTO;

                if (loopsData === undefined || loopsData === null || loopsData.length === 0){
                    $documentLoopDataContainer.append(buildTextTitle(localization.loop.title));
                    $documentLoopDataContainer.append(buildTextInfo(localization.noData));
                }else {
                    loopsData = sortData(loopsData, "index");

                    let $frameContainer = $('<div>', {
                        'class': 'all-data-loop'
                    });

                    $frameContainer.append(
                        loopsData.map(loopDTO => {
                            return buildLoopContentContainerBEM(loopDTO, loopsContentData);
                        })
                    );
                    $documentLoopDataContainer.append($frameContainer);
                }
                $documentLoopDataContainer.append($('<hr/>'));
            }

            function buildLoopContentContainerBEM(loopDTO, loopContentDTO){
                let $titleContainer = $('<div>', {
                    'class': 'all-data-loop-title'
                });
                $titleContainer.append(buildTextTitle(localization.loop.titleSingle));
                $titleContainer.append(buildTextProperty(localization.index, loopDTO.index));
                $titleContainer.append(buildTextProperty(localization.loop.countElements, loopDTO.entries.length));
                $titleContainer.append(buildEditButton('loop', loopDTO.index, null, null));

                return new BEM({
                    block: 'imcms-loop-data',
                    elements: {
                        'title': $titleContainer,
                        'text': buildTextContentContainerInLoopBEM(loopDTO.index, loopContentDTO.textsDTO),
                        'image': buildImageContentContainerInLoopBEM(loopDTO.index, loopContentDTO.imagesDTO)
                    }
                }).buildBlockStructure('<div>');
            }

            function buildTextContentContainerInLoopBEM(loopIndex, textListDTO){
                let $textContainer = $('<div>', {
                    'class': 'imcms-text-data'
                });

                let textList = [];
                textListDTO.forEach(textDTO => {
                    if(textDTO.loopEntryRef.loopIndex === loopIndex) textList.push(textDTO);
                })

                $textContainer.append(
                    textList.map(textDTO => {
                        return buildTextContentContainerBEM(textDTO);
                    })
                );

                return $textContainer;
            }

            function buildImageContentContainerInLoopBEM(loopIndex, imageListDTO){
                let $imageContainer = $('<div>', {
                    'class': 'all-data-image'
                });

                let imageList = [];
                imageListDTO.forEach(imageDTO => {
                    if(imageDTO.loopEntryRef.loopIndex === loopIndex) imageList.push(imageDTO);
                })

                $imageContainer.append(
                    imageList.map(imageDTO => {
                        return buildImageContentContainerBEM(imageDTO.index, imageDTO.allLanguages, imageDTO.langCode, imageDTO.path, imageDTO.loopEntryRef);
                    })
                );

                return $imageContainer;
            }

            function buildImageList(imagesData){
                $documentImageDataContainer.append(buildTextTitle(localization.image.title));

                if (imagesData === undefined || imagesData === null || imagesData.length === 0) {
                    $documentImageDataContainer.append(buildTextInfo(localization.noData));
                }else {
                    let $frameContainer = $('<div>', {
                        'class': 'all-data-image'
                    });

                    $frameContainer.append(
                        imagesData.map(imageDTO => {
                            return buildImageContentContainerBEM(imageDTO.index, imageDTO.allLanguages, imageDTO.langCode, imageDTO.path, imageDTO.loopEntryRef);
                        })
                    );
                    $documentImageDataContainer.append($frameContainer);
                }
                $documentImageDataContainer.append($('<hr/>'));
            }

            function buildImageContentContainerBEM(index, allLanguages, language, path, loopEntryRef){
                return new BEM({
                    block: 'imcms-image-data',
                    elements: {
                        'info': $('<div>', {
                            html: [buildTextProperty(localization.index, index),
                                buildTextProperty(localization.image.allLanguages, allLanguages),
                                buildTextProperty(localization.language, language),
                                buildTextProperty(localization.image.path, path)]
                        }),
                        'button': buildEditButton('image', index, loopEntryRef, language)
                    }
                }).buildBlockStructure('<div>');
            }

            function buildEditButton(nameItem, index, loopEntryRef, lang) {
                const linkToEditor = buildLinkToEditor(nameItem, index, loopEntryRef, lang);
                return components.buttons.positiveButton( {
                    text: localization.edit,
                    click: () => {
                        window.open(linkToEditor,'_blank');
                    }
                }).addClass('all-data-edit-button');
            }

            function buildLinkToEditor(nameItem, index, loopEntryRef, lang){
                let indexInLink = '&index=' + index;
                let loopIndexInLink = '';
                let loopEntryIndexInLink = '';
                let langInLink = '';
                if(loopEntryRef !== undefined && loopEntryRef !== null){
                    loopIndexInLink = '&loop-index=' + loopEntryRef.loopIndex;
                    loopEntryIndexInLink = '&loop-entry-index=' + loopEntryRef.loopEntryIndex;
                }
                if(lang !== undefined && lang !== null){
                    langInLink = '&lang=' + lang;
                }

                return '/api/admin/' + nameItem + '?meta-id=' + docId + indexInLink +
                    loopIndexInLink + loopEntryIndexInLink + langInLink;
            }

            function buildTextProperty(name, value){
                return components.texts.infoText('<div>', name + ': ' + value).addClass('info-text')
            }

            function buildTextInfo(value){
                return components.texts.infoText('<div>', value).addClass('info-text')
            }

            function buildTextTitle(value){
                return components.texts.titleText('<div>', value).addClass('imcms-all-data-title');
            }

            function buildInput(value) {
                let $testInput = components.texts.textArea('<div>', {
                    placeholder: value,
                    readonly: true
                });
                $testInput.addClass('text-content');
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
                    'categories' : $documentCategoryDataContainer,
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

	    AllDataTab.prototype.tabElementsFactory = (index, docId) => [
		    buildDataList(docId)
	    ];

	    AllDataTab.prototype.clearTabData = () => {
		    $documentTextDataContainer.empty();
		    $documentMenuDataContainer.empty();
		    $documentCategoryDataContainer.empty();
		    $documentLoopDataContainer.empty();
		    $documentImageDataContainer.empty();
	    };

        AllDataTab.prototype.getDocLink = () => localization.documentationLink;

        return new AllDataTab(localization.name);
        }
    );