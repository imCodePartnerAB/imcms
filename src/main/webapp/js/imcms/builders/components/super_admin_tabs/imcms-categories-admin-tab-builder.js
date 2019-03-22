/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-categories-admin-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-field-wrapper',
        'imcms-bem-builder', 'jquery', 'imcms-category-types-rest-api', 'imcms-categories-rest-api',
        'imcms-category-types-editor', 'imcms-modal-window-builder'
    ],
    function (SuperAdminTab, texts, components, fieldWrapper, BEM, $, typesRestApi, categoriesRestApi, typeEditor,
              modal) {

        texts = texts.superAdmin.categories;

        let $typeNameRow, $isInherited, $isSingleSelect, $isMultiSelect, errorMsg, $categoryTypeSaveButtons,
            valueRadios, radioButtonsGroup, categoryCreateContainer, categoryTypeSelected, currentCategoryType;

        function buildTypeNameRow() {
            $typeNameRow = components.texts.textBox('<div>', {
                text: texts.sections.createCategoryType.name
            });
            return $typeNameRow;
        }

        function buildErrorCtgTypeDuplicateMsgBlock() {
            errorMsg = components.texts.errorText("<div>", texts.duplicateErrorName, {style: 'display: none;'});
            return errorMsg;
        }


        function buildCategoryTypeProperty() {

            return $isInherited = components.checkboxes.imcmsCheckbox("<div>", {
                text: texts.sections.createCategoryType.inherited
            })
        }

        function buildCategoryTypeSelectionModes() {

            valueRadios = [
                $isSingleSelect = components.radios.imcmsRadio("<div>", {
                    text: texts.sections.createCategoryType.singleSelect,
                    name: 'select',
                    value: 'single-select',
                }),
                $isMultiSelect = components.radios.imcmsRadio("<div>", {
                    text: texts.sections.createCategoryType.multiSelect,
                    name: 'select',
                    value: 'multi-select',
                }),
            ];

            radioButtonsGroup = components.radios.group($isSingleSelect, $isMultiSelect);


            return components.radios.radioContainer(
                '<div>', valueRadios, {}
            );
        }

        function buildOnCategoryTypeSelected() {
            return id => {
                typesRestApi.getById(id)
                    .done(ctgType => {
                        currentCategoryType = ctgType;
                        let categoryTypeObj = {
                            name: $typeNameRow.setValue(ctgType.name),
                            singleSelect: $isSingleSelect.setChecked(ctgType.multiSelect === false),
                            multiSelect: $isMultiSelect.setChecked(ctgType.multiSelect),
                            inherited: $isInherited.setChecked(ctgType.inherited)
                        };

                        categoryCreateContainer.slideUp();

                        $categoryTypeCreateContainer.slideDown();

                        if (id) {
                            $('.categories-block').remove();
                            buildShowCategoryType.append(buildCategoriesContainer(id));

                            categoriesList.slideDown();
                            categoryCreateBtnContainer.slideDown();
                        }

                        return categoryTypeObj
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.categoryType.loadFailed));
            };
        }

        function buildDropDownListCategoriesTypes() {
            let categoryTypeSelect = components.selects.selectContainer('<div>', {
                id: "types-id",
                name: "types-name",
                emptySelect: false,
                text: texts.chooseType,
                onSelected: buildOnCategoryTypeSelected
            });

            categoryTypeSelected = categoryTypeSelect.getSelect();

            typesRestApi.read()
                .done(ctgTypes => {
                    let categoriesTypesDataMapped = ctgTypes.map(categoryType => ({
                        text: categoryType.name,
                        'data-value': categoryType.id
                    }));

                    components.selects.addOptionsToSelect(categoriesTypesDataMapped, categoryTypeSelect.getSelect(), buildOnCategoryTypeSelected());
                })
                .fail(() => modal.buildErrorWindow(texts.folderNotEmptyMessage)); // What is the error here ?

            return categoryTypeSelect;
        }


        let buildShowCategoryType;

        function buildDropListCtgTypesContainer() {
            return buildShowCategoryType = new BEM({
                block: 'category-types-block',
                elements: {
                    'categories-types': buildDropDownListCategoriesTypes()
                }
            }).buildBlockStructure('<div>');
        }

        function onCreateNewCategoryType() {
            currentCategoryType = null;
            let createCategoryType = {
                id: null,
                name: '',
                singleSelect: true,
                multiSelect: false,
                inherited: false
            };

            $typeNameRow.setValue(createCategoryType.name);
            $isSingleSelect.setChecked(createCategoryType.singleSelect);
            $isMultiSelect.setChecked(createCategoryType.multiSelect);
            $isInherited.setChecked(createCategoryType.inherited);

            if (categoryCreateContainer) categoryCreateContainer.slideUp();
            if (categoriesList) categoriesList.slideUp();
            categoryCreateBtnContainer.slideUp();
            $categoryTypeCreateContainer.slideDown();

            return createCategoryType;
        }

        function buildCategoryTypeButtonsContainer() {

            function buildCategoryTypeCreateButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.createButtonName,
                    click: onCreateNewCategoryType
                });
                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'type-buttons-block',
                elements: {
                    'title': $('<div>', {text: texts.titleCategoryType}),
                    'create-button': buildCategoryTypeCreateButton(),
                }
            }).buildBlockStructure('<div>');

        }

        function onDeleteCategoryType() {
            modal.buildModalWindow(texts.warnDelete, confirmed => {
                if (!confirmed) return;

                typesRestApi.remove(currentCategoryType)
                    .done(() => {

                        categoryTypeSelected.find("[data-value='" + currentCategoryType.id + "']").remove();
                        currentCategoryType = null;
                        categoryTypeSelected.selectFirst();
                        $categoryTypeCreateContainer.slideUp();
                        categoriesList.slideUp();
                        categoryCreateBtnContainer.slideUp();
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.removeFailed));
            });
        }

        function onSaveCategoryType() {
            let checkValue = radioButtonsGroup.getCheckedValue();

            let name = $typeNameRow.getValue();
            let inherited = $isInherited.isChecked();

            if (!name) {
                $typeNameRow.$input.focus();
                return;
            }

            let currentCtgTypeToSave = {
                id: (currentCategoryType) ? currentCategoryType.id : null,
                name: name,
                singleSelect: (checkValue === 'single-select'),
                multiSelect: (checkValue === 'multi-select'),
                inherited: inherited,
            };

            if (currentCtgTypeToSave.id) {
                typesRestApi.replace(currentCtgTypeToSave)
                    .done(savedCategoryType => {
                        currentCategoryType = savedCategoryType;

                        let categoriesTypesDataMapped = [{
                            text: savedCategoryType.name,
                            'data-value': savedCategoryType.id
                        }];


                        categoryTypeSelected.find(`[data-value='${savedCategoryType.id}']`).remove();
                        components.selects.addOptionsToSelect(categoriesTypesDataMapped, categoryTypeSelected, buildOnCategoryTypeSelected());

                    })
                    .fail(() => {
                        errorMsg.slideDown();
                    });
            } else {
                typesRestApi.create(currentCtgTypeToSave)
                    .done(function (categoryType) {
                        currentCategoryType = categoryType;

                        let categoriesTypesDataMapped = [{
                            text: categoryType.name,
                            'data-value': categoryType.id
                        }];

                        components.selects.addOptionsToSelect(categoriesTypesDataMapped, categoryTypeSelected, buildOnCategoryTypeSelected());
                    })
                    .fail(() => {
                        errorMsg.css('display', 'inline-block').slideDown();
                    });
            }
        }

        function onWarnCancel(onConfirm) {
            return () => {
                modal.buildModalWindow(texts.warnCancelMessage, confirmed => {
                    if (!confirmed) return;
                    onConfirm.call();
                });
            };
        }

        function buildCategoryTypeEditButtons() {
            return $categoryTypeSaveButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.saveButton({
                    text: texts.saveButton,
                    click: onSaveCategoryType
                }),
                components.buttons.negativeButton({
                    text: texts.cancelButton,
                    click: onWarnCancel(() => {
                        $categoryTypeCreateContainer.slideUp();
                    })
                }),
                components.buttons.errorButton({
                    text: texts.removeButtonName,
                    click: onDeleteCategoryType
                })
            ]);
        }

        let $categoryTypeCreateContainer;

        function buildCreateCategoryTypeContainer() {

            return $categoryTypeCreateContainer || ($categoryTypeCreateContainer = new BEM({
                block: 'type-create-block',
                elements: {
                    'field-name': buildTypeNameRow(),
                    'selection-modes': buildCategoryTypeSelectionModes(),
                    'properties': buildCategoryTypeProperty(),
                    'error-row': buildErrorCtgTypeDuplicateMsgBlock(),
                    'ctg-type-edit-button': buildCategoryTypeEditButtons()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'}));
        }

        let categorySelected;
        let currentCategory;
        let categoriesList;


        function buildOnCategorySelected() {
            return value => {
                categoriesRestApi.getById(value).done(function (category) {
                    currentCategory = category;
                    let categoryObj = {
                        name: $categoryNameRow.setValue(currentCategory.name),
                        description: categoryDescription.setValue(currentCategory.description),
                        type: currentCategoryType
                    };

                    $categoryTypeCreateContainer.slideUp();

                    categoryCreateContainer.slideDown();

                    return categoryObj;

                }).fail(() => modal.buildErrorWindow(texts.error.category.loadFailed));
            };
        }

        function buildDropDownListCategories(id) {
            let categorySelect = components.selects.selectContainer('<div>', {
                id: "category-filter",
                name: "category-filter",
                emptySelect: true,
                text: texts.chooseCategory,
                onSelected: buildOnCategorySelected
            });

            categorySelected = categorySelect.getSelect();

            categoriesRestApi.getCategoriesByCategoryTypeId(id).done(function (categories) {

                let categoriesDataMapped = categories.map(function (category) {
                    return {
                        text: category.name,
                        'data-value': category.id
                    }
                });

                components.selects.addOptionsToSelect(categoriesDataMapped, categorySelect.getSelect(), buildOnCategorySelected());

            });

            return categorySelect;
        }

        function buildCategoriesContainer(id) {
            return categoriesList = new BEM({
                block: 'categories-block',
                elements: {
                    'categories': buildDropDownListCategories(id)
                }
            }).buildBlockStructure('<div>');
        }

        let $categoryNameRow;
        let categoryDescription;
        let categorySaveButtons;
        let errorDuplicateMessage$;

        function buildCategoryNameDupblicateErrorBlock() {
            errorDuplicateMessage$ = components.texts.errorText("<div>", texts.duplicateErrorName, {style: 'display: none;'});
            return errorDuplicateMessage$;
        }

        function onSaveCategory() {
            let name = $categoryNameRow.getValue();
            let description = categoryDescription.getValue();

            if (!name) {
                $categoryNameRow.$input.focus();
                categoryDescription.$input.focus();
                return;
            }

            let currentCategoryToSave = {
                id: (currentCategory) ? currentCategory.id : null,
                name: name,
                description: description,
                type: currentCategoryType
            };

            if (currentCategoryToSave.id) {
                categoriesRestApi.replace(currentCategoryToSave)
                    .done(savedCategory => {
                        currentCategory = savedCategory;

                        let categoryDataMapped = [{
                            text: savedCategory.name,
                            'data-value': savedCategory.id
                        }];

                        categorySelected.find("[data-value='" + savedCategory.id + "']").remove();

                        components.selects.addOptionsToSelect(categoryDataMapped, categorySelected, buildOnCategorySelected());

                    })
                    .fail(() => {
                        errorDuplicateMessage$.css('display', 'inline-block').slideDown();
                    });
            } else {
                categoriesRestApi.create(currentCategoryToSave)
                    .done(category => {
                        currentCategory = category;

                        let categoryDataMapped = [{
                            text: category.name,
                            'data-value': category.id
                        }];

                        components.selects.addOptionsToSelect(categoryDataMapped, categorySelected, buildOnCategorySelected());

                        categoryCreateContainer.slideUp();

                    })
                    .fail(() => {
                        errorDuplicateMessage$.css('display', 'inline-block').slideDown();
                    });
            }

        }

        function getOnWarnCancel(onConfirm) {
            return () => {
                modal.buildModalWindow(texts.warnCancelMessage, confirmed => {
                    if (!confirmed) return;
                    onConfirm.call();
                });
            };
        }

        function buildCategoryCreateContainer() {

            function buildCategoryNameRow() {
                $categoryNameRow = components.texts.textBox('<div>', {
                    text: texts.sections.createCategory.name
                });
                return $categoryNameRow;
            }

            function buildCategoryDescriptionTextField() {
                categoryDescription = components.texts.textAreaField('<div>', {
                    text: texts.sections.createCategory.description
                });
                return categoryDescription;
            }

            function onRemoveCategory() {
                modal.buildModalWindow(texts.warnDelete, confirmed => {
                    if (!confirmed) return;

                    categoriesRestApi.remove(currentCategory)
                        .done(() => {

                            categorySelected.find("[data-value='" + currentCategory.id + "']").remove();
                            currentCategory = null;
                            categorySelected.selectFirst();
                            categoryCreateContainer.slideUp();
                        })
                        .fail(() => modal.buildErrorWindow(texts.error.removeFailed));
                });
            }

            function buildEditCategoryButtonContainer() {
                return categorySaveButtons = components.buttons.buttonsContainer('<div>', [
                    components.buttons.saveButton({
                        text: texts.saveButton,
                        click: onSaveCategory
                    }),
                    components.buttons.negativeButton({
                        text: texts.cancelButton,
                        click: getOnWarnCancel(() => {
                            categoryCreateContainer.slideUp();
                        })
                    }),
                    components.buttons.errorButton({
                        text: texts.removeButtonName,
                        click: onRemoveCategory
                    })
                ])
            }

            return categoryCreateContainer = new BEM({
                block: 'category-create-block',
                elements: {
                    'row-name': buildCategoryNameRow(),
                    'row-description': buildCategoryDescriptionTextField(),
                    'error-duplicate': buildCategoryNameDupblicateErrorBlock(),
                    'edit-buttons': buildEditCategoryButtonContainer()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'});
        }

        function onShowCategoryCreateContainer() {
            currentCategory = null;
            let createCategory = {
                id: null,
                name: '',
                description: ''
            };
            $categoryNameRow.setValue(createCategory.name);
            categoryDescription.setValue(createCategory.description);
            $categoryTypeCreateContainer.css('display', 'none').slideUp();
            categoryCreateContainer.slideDown();

            return createCategory;
        }

        function buildCategoryCreateButton() {
            let $button = components.buttons.positiveButton({
                text: texts.createButtonName,
                click: onShowCategoryCreateContainer
            });

            return components.buttons.buttonsContainer('<div>', [$button]);
        }

        let categoryCreateBtnContainer;

        function buildCategoryCreateButtonContainer() {

            return categoryCreateBtnContainer = new BEM({
                block: 'create-button-block',
                elements: {
                    'title': $('<div>', {text: texts.titleCategory}),
                    'create': buildCategoryCreateButton(),
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'});
        }

        return new SuperAdminTab(texts.name, [
            buildCategoryTypeButtonsContainer(),
            buildCategoryCreateButtonContainer(),
            buildDropListCtgTypesContainer(),
            buildCreateCategoryTypeContainer(),
            buildCategoryCreateContainer()
        ]);
    }
);
