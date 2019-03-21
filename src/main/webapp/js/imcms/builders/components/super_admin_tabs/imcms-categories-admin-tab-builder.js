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

        let categoryCreateContainer;
        let currentCategory;
        let categoriesList;
        let categoryTypeSelected;

        function buildDropDownListCategoriesTypes() {
            const onCategoryTypeSelected = values => {
                typesRestApi.getById(values)
                    .done(ctgType => {
                        currentCategoryType = ctgType;
                        let categoryTypeObj = {
                            name: $typeNameRow.setValue(ctgType.name),
                            singleSelect: $isSingleSelect.setChecked((ctgType.multiSelect === false)),
                            multiSelect: $isMultiSelect.setChecked(ctgType.multiSelect),
                            inherited: $isInherited.setChecked(ctgType.inherited)
                        };

                        categoryCreateContainer.css('display', 'none').slideUp();

                        $typeContainer.slideDown();

                        if (values) {
                            $('.categories-block').remove();
                            $('.category-types-block').append(buildCategoriesContainer(values));
                            categoriesList.slideDown();
                            categoryCreateBtnContainer.slideDown();
                        }

                        return categoryTypeObj
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.categoryType.loadFailed));
            };

            let categoryTypeSelect = components.selects.selectContainer('<div>', {
                id: "types-id",
                name: "types-name",
                emptySelect: true,
                text: texts.chooseType,
                onSelected: onCategoryTypeSelected
            });

            categoryTypeSelected = categoryTypeSelect.getSelect();

            typesRestApi.read()
                .done(ctgTypes => {
                    let categoriesTypesDataMapped = ctgTypes.map(categoryType => ({
                        text: categoryType.name,
                        'data-value': categoryType.id
                    }));

                    components.selects.addOptionsToSelect(categoriesTypesDataMapped, categoryTypeSelect.getSelect(), onCategoryTypeSelected);
                })
                .fail(() => modal.buildErrorWindow(texts.folderNotEmptyMessage));

            return categoryTypeSelect;
        }


        let categorySelected;
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
            categoryCreateContainer.css('display', 'none').slideUp();
            return $typeContainer.slideDown();
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


        let $typeNameRow, $isInherited, $isSingleSelect, $isMultiSelect, errorMsg, $categoryTypeSaveButtons,
            $categoryTypeEditButtons, valueRadios, radioButtonsGroup;

        function buildTypeNameRow() {
            $typeNameRow = components.texts.textBox('<div>', {
                text: texts.sections.createCategoryType.name
            });
            return $typeNameRow;
        }

        function buildErrorMsgBlock() {
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

        function onDeleteCategoryType() {
            modal.buildModalWindow('delete?', confirmed => {
                if (!confirmed) return;

                typesRestApi.remove(currentCategoryType)
                    .done(() => {

                        categoryTypeSelected.find("[data-value='" + currentCategoryType.id + "']").remove();
                        currentCategoryType = null;
                        $typeContainer.slideUp();
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
                        currentCategoryType.id = savedCategoryType.id;
                        currentCategoryType.name = savedCategoryType.name;
                        currentCategoryType.singleSelect = savedCategoryType.singleSelect;
                        currentCategoryType.multiSelect = savedCategoryType.multiSelect;
                        currentCategoryType.inherited = savedCategoryType.inherited;

                        let categoriesTypesDataMapped = [{
                            text: savedCategoryType.name,
                            'data-value': savedCategoryType.id
                        }];


                        categoryTypeSelected.find("[data-value='" + savedCategoryType.id + "']").remove();
                        components.selects.addOptionsToSelect(categoriesTypesDataMapped, categoryTypeSelected, function () {
                        });

                    })
                    .fail(() => {
                        errorMsg.css('display', 'inline-block').slideDown();
                    });
            } else {
                typesRestApi.create(currentCtgTypeToSave)
                    .done(function (categoryType) {
                        currentCategoryType = categoryType;

                        let categoriesTypesDataMapped = [{
                            text: categoryType.name,
                            'data-value': categoryType.id
                        }];

                        components.selects.addOptionsToSelect(categoriesTypesDataMapped, categoryTypeSelected, function () {
                        });
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

        function buildCategoryTypeSaveButtons() {
            return $categoryTypeSaveButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.saveButton({
                    text: texts.saveButton,
                    click: onSaveCategoryType
                }),
                components.buttons.negativeButton({
                    text: texts.cancelButton,
                    click: onWarnCancel(() => {
                        $categoryTypeSaveButtons.slideUp();
                        $categoryTypeEditButtons.slideDown();

                        //$typeNameRow.$input.attr('disabled', 'disabled');
                    })
                })
            ], {
                style: 'display: none;'
            });
        }

        function buildCategoryTypeEditButtons() {
            return $categoryTypeEditButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.positiveButton({
                    text: texts.editButtonName,
                    click: function () {
                        $categoryTypeEditButtons.slideUp();
                        $categoryTypeSaveButtons.slideDown();
                    }
                }),
                components.buttons.negativeButton({
                    text: texts.removeButtonName,
                    click: onDeleteCategoryType
                })
            ]);
        }

        var $typeContainer;
        var currentCategoryType;

        function buildCreateCategoryTypeContainer() {

            return $typeContainer || ($typeContainer = new BEM({
                block: 'type-create-block',
                elements: {
                    'title-row': $('<div>', {text: texts.sections.createCategoryType.title}),
                    'field-name': buildTypeNameRow(),
                    'selection-modes': buildCategoryTypeSelectionModes(),
                    'properties': buildCategoryTypeProperty(),
                    'error-row': buildErrorMsgBlock(),
                    'ctg-type-view-button': buildCategoryTypeEditButtons(),
                    'ctg-type-edit-button': buildCategoryTypeSaveButtons()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'}));
        }

        function buildDropDownListCategories(id) {
            let onCategorySelected = function (value) {
                categoriesRestApi.getById(value).done(function (category) {
                    currentCategory = category;
                    let categoryObj = {
                        name: $categoryNameRow.setValue(currentCategory.name),
                        description: categoryDescription.setValue(currentCategory.description),
                        type: currentCategoryType
                    };

                    $typeContainer.css('display', 'none').slideUp();

                    categoryCreateContainer.slideDown();

                    return categoryObj;

                }).fail(() => modal.buildErrorWindow(texts.error.category.loadFailed));
            };

            let categorySelect = components.selects.selectContainer('<div>', {
                id: "category-filter",
                name: "category-filter",
                emptySelect: true,
                text: texts.chooseCategory,
                onSelected: onCategorySelected
            });

            categorySelected = categorySelect.getSelect();

            categoriesRestApi.getCategoriesByCategoryTypeId(id).done(function (categories) {

                let categoriesDataMapped = categories.map(function (category) {
                    return {
                        text: category.name,
                        'data-value': category.id
                    }
                });

                components.selects.addOptionsToSelect(categoriesDataMapped, categorySelect.getSelect(), onCategorySelected);

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
        let categoryEditButtons;
        let errorDuplicateMessage$;

        function buildErrorBlock() {
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
                        currentCategory.id = savedCategory.id;
                        currentCategory.name = savedCategory.name;
                        currentCategory.description = savedCategory.description;
                        currentCategory.type = savedCategory.type;

                        let categoryDataMapped = [{
                            text: savedCategory.name,
                            'data-value': savedCategory.id
                        }];


                        categorySelected.find("[data-value='" + savedCategory.id + "']").remove();

                        components.selects.addOptionsToSelect(categoryDataMapped, categorySelected, function () {
                        });

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

                        components.selects.addOptionsToSelect(categoryDataMapped, categorySelected, function () {
                        });

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
                // $categoryNameRow.$input.attr('disabled', 'disabled');
                return $categoryNameRow;
            }

            function buildCategoryDescriptionTextField() {
                categoryDescription = components.texts.textAreaField('<div>', {
                    text: texts.sections.createCategory.description
                });

                //categoryDescription.$input.attr('disabled', 'disabled');
                return categoryDescription;
            }

            function onRemoveCategory() {
                modal.buildModalWindow('delete?', confirmed => {
                    if (!confirmed) return;

                    categoriesRestApi.remove(currentCategory)
                        .done(() => {

                            categorySelected.find("[data-value='" + currentCategory.id + "']").remove();
                            currentCategory = null;
                            categoryCreateContainer.slideUp();
                        })
                        .fail(() => modal.buildErrorWindow(texts.error.removeFailed));
                });
            }

            function buildSaveAndCancelContainer() {
                return categorySaveButtons = components.buttons.buttonsContainer('<div>', [
                    components.buttons.saveButton({
                        text: texts.saveButton,
                        click: onSaveCategory
                    }),
                    components.buttons.negativeButton({
                        text: texts.cancelButton,
                        click: getOnWarnCancel(() => {
                            categorySaveButtons.slideUp();
                            categoryEditButtons.slideDown();
                        })
                    })
                ], {
                    style: 'display: none;'
                })
            }

            function buildCategoryViewButtons() {
                return categoryEditButtons = components.buttons.buttonsContainer('<div>', [
                    components.buttons.positiveButton({
                        text: texts.editButtonName,
                        click: function () {
                            categoryEditButtons.css('display', 'none').slideUp();
                            categorySaveButtons.css('display', 'inline-block').slideDown();
                        }
                    }),
                    components.buttons.negativeButton({
                        text: texts.removeButtonName,
                        click: onRemoveCategory
                    })
                ]);
            }

            return categoryCreateContainer = new BEM({
                block: 'category-create-block',
                elements: {
                    'title-create': '',
                    'row-name': buildCategoryNameRow(),
                    'row-description': buildCategoryDescriptionTextField(),
                    'error-duplicate': buildErrorBlock(),
                    'edit-cancel-buttons': buildCategoryViewButtons(),
                    'save-cancel-buttons': buildSaveAndCancelContainer()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'});
        }

        function onShowCategoryCreateContainer() {
            if (currentCategory) {
                $categoryNameRow.setValue('');
                categoryDescription.setValue('');
            }
            $typeContainer.css('display', 'none').slideUp();
            return categoryCreateContainer.slideDown();
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
