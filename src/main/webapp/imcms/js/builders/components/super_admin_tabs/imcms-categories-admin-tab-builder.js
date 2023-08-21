/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-categories-admin-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-field-wrapper',
        'imcms-bem-builder', 'jquery', 'imcms-category-types-rest-api', 'imcms-categories-rest-api',
        'imcms-modal-window-builder'
    ],
    function (SuperAdminTab, texts, components, fieldWrapper, BEM, $, typesRestApi, categoriesRestApi,
              modal) {

        texts = texts.superAdmin.categories;

        let $typeNameRow;
        let $isInherited;
        let $isVisible;
        let $isSingleSelect;
        let $isMultiSelect;
        let $categoryTypeSaveButtons;
        let valueRadios;
        let radioButtonsGroup;
        let categoryCreateContainer;
        let categoryTypeSelected;
        let currentCategoryType;

        function buildTypeNameRow() {
            $typeNameRow = components.texts.textAreaField('<div>', {
                text: texts.sections.createCategoryType.name
            });
            return $typeNameRow;
        }

        function buildCategoryTypeProperty() {
            let valuesCheckBox = [
                $isInherited = components.checkboxes.imcmsCheckbox("<div>", {
                    text: texts.sections.createCategoryType.inherited
                }),
                $isVisible = components.checkboxes.imcmsCheckbox("<div>", {
                    text: texts.sections.createCategoryType.visible
                })
            ];

            return components.checkboxes.checkboxContainer('<div>', valuesCheckBox, {});
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
                            inherited: $isInherited.setChecked(ctgType.inherited),
                            visible: $isVisible.setChecked(ctgType.visible)
                        };

                        categoryCreateContainer.slideUp();

                        $categoryTypeCreateContainer.slideDown();

                        if (id) buildAndShowDropDownListCategories(id);

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
                text: texts.sections.createCategoryType.chooseType,
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
                .fail(() => modal.buildErrorWindow(texts.error.categoryType.loadFailed));

            return categoryTypeSelect;
        }


        let buildShowCategoryType;
        const categoryTypeBem = new BEM({
            block: 'category-admin',
        });

        function buildDropListCtgTypesContainer() {
            return buildShowCategoryType = categoryTypeBem.buildBlock('<div>', [{
                'category-type': buildCategoryTypeEditorContainer()
            }]);
        }

        function buildCategoryTypeEditorContainer() {
            return new BEM({
                block: 'category-type-editor',
                elements: {
                    'type-container': buildCategoryTypeContainer(),
                    'create-container': buildCreateCategoryTypeContainer()
                }
            }).buildBlockStructure('<div>');
        }

        function buildCategoryTypeContainer() {
            return new BEM({
                block: 'category-type',
                elements: {
                    'type-drop-list': buildDropDownListCategoriesTypes(),
                    'type-create-button': buildCategoryTypeButtonContainer(),
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
                inherited: false,
                visible: true
            };

            $typeNameRow.setValue(createCategoryType.name);
            $isSingleSelect.setChecked(createCategoryType.singleSelect);
            $isMultiSelect.setChecked(createCategoryType.multiSelect);
            $isInherited.setChecked(createCategoryType.inherited);
            $isVisible.setChecked(createCategoryType.visible);

            $categoryTypeSaveButtons.find('.imcms-button--error').hide();
            hideCategoriesContainer();
            $categoryTypeCreateContainer.slideDown();

            return createCategoryType;
        }

        function hideCategoriesContainer() {
            if (categoryCreateContainer) categoryCreateContainer.slideUp();
            if (categoriesList) categoriesList.slideUp();
            if (categoryCreateBtnContainer) categoryCreateBtnContainer.slideUp();
        }

        function buildCategoryTypeButtonContainer() {
            let $button = components.buttons.positiveButton({
                text: texts.createButtonName,
                click: onCreateNewCategoryType
            });
            return components.buttons.buttonsContainer('<div>', [$button]);
        }

        function onDeleteCategoryType() {
            modal.buildModalWindow(texts.confirmDelete, confirmed => {
                if (!confirmed) return;

                typesRestApi.remove(currentCategoryType)
                    .done(() => afterDeleteCategoryType(currentCategoryType.id))
                    .fail(() => modal.buildConfirmWindow(texts.error.categoryType.removeFailed,
                        () => deleteForceCategoryType(currentCategoryType)));
            });
        }

        function deleteForceCategoryType(currentCategoryType){
            typesRestApi.removeForce(currentCategoryType)
                .done(() => afterDeleteCategoryType(currentCategoryType.id))
                .fail(() => modal.buildErrorWindow(texts.error.categoryType.removeForceFailed));
        }

        function afterDeleteCategoryType(currentCategoryTypeId){
            categoryTypeSelected.deleteOption(currentCategoryTypeId);
            currentCategoryType = null;
            categoryTypeSelected.selectFirst();
            hideCategoriesContainer();
            $categoryTypeCreateContainer.slideUp();
        }

        function onSaveCategoryType() {
            let checkValue = radioButtonsGroup.getCheckedValue();

            let name = $typeNameRow.getValue();
            let inherited = $isInherited.isChecked();
            let visible = $isVisible.isChecked();

            if (!name) {
                modal.buildErrorWindow(texts.error.invalidName);
                return;
            }

            let currentCtgTypeToSave = {
                id: (currentCategoryType) ? currentCategoryType.id : null,
                name: name,
                singleSelect: (checkValue === 'single-select'),
                multiSelect: (checkValue === 'multi-select'),
                inherited: inherited,
                visible: visible
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
                        categoryTypeSelected.selectValue(savedCategoryType.id);
                    })
                    .fail(() => {
                        modal.buildErrorWindow(texts.error.invalidName);
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
                        categoryTypeSelected.selectLast();

                        buildAndShowDropDownListCategories();
                    })
                    .fail(() => {
                        modal.buildErrorWindow(texts.error.invalidName);
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
                components.buttons.errorButton({
                    text: texts.removeButtonName,
                    click: onDeleteCategoryType
                }),
                components.buttons.negativeButton({
                    text: texts.cancelButton,
                    click: onWarnCancel(() => {
                        $categoryTypeCreateContainer.slideUp();
                    })
                }),
                components.buttons.saveButton({
                    text: texts.saveButton,
                    click: onSaveCategoryType
                }),
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
                    'ctg-type-edit-button': buildCategoryTypeEditButtons()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'}));
        }

        let categorySelected;
        let currentCategory;
        let categoriesList;


        function onCategorySelected() {
            return id => {
                categoriesRestApi.getById(id).done(function (category) {
                    currentCategory = category;
                    let categoryObj = {
                        name: categoryNameRow.setValue(currentCategory.name),
                        description: categoryDescription.setValue(currentCategory.description),
                        type: currentCategoryType
                    };

                    if($(window).width() > 1000) $categoryTypeCreateContainer.slideUp();

                    categorySaveButtons.find('.imcms-button--error').show();
                    categoryCreateContainer.slideDown();

                    return categoryObj;

                }).fail(() => modal.buildErrorWindow(texts.error.category.loadFailed));
            };
        }

        function buildAndShowDropDownListCategories(id){
            buildShowCategoryType.find('.category-editor').remove();
            buildShowCategoryType.append(categoryTypeBem.makeBlockElement('category', buildCategoriesSelection(id)));
            categoriesList.slideDown();
            $categoryTypeSaveButtons.find('.imcms-button--error').slideDown();
            categoryCreateBtnContainer.slideDown();
        }

        function buildDropDownListCategories(id) {
            let categorySelect = components.selects.selectContainer('<div>', {
                id: "category-filter",
                name: "category-filter",
                emptySelect: false,
                text: texts.sections.createCategory.chooseCategory,
                onSelected: onCategorySelected
            });

            categorySelected = categorySelect.getSelect();

            if (id) {
                categoriesRestApi.getCategoriesByCategoryTypeId(id).done(function (categories) {
                    let categoriesDataMapped = categories.map(function (category) {
                        return {
                            text: category.name,
                            'data-value': category.id
                        }
                    });
                    components.selects.addOptionsToSelect(categoriesDataMapped, categorySelect.getSelect(), onCategorySelected());
                });
            }else{
                components.selects.addOptionsToSelect([], categorySelect.getSelect(), onCategorySelected());
            }

            return categorySelect;
        }

        function buildCategoriesContainer(id) {
            return categoriesList = new BEM({
                block: 'categories',
                elements: {
                    'select-categories': buildDropDownListCategories(id),
                    'category-create-button': buildCategoryCreateButtonContainer()
                }
            }).buildBlockStructure('<div>');
        }

        function buildCategoriesSelection(id) {
            return new BEM({
                block: 'category-editor',
                elements: {
                    'select-container': buildCategoriesContainer(id),
                    'create-container': buildCategoryCreateContainer()
                }
            }).buildBlockStructure('<div>');
        }

        let categoryNameRow;
        let categoryDescription;
        let categorySaveButtons;

        function onSaveCategory() {
            let name = categoryNameRow.getValue();
            let description = categoryDescription.getValue();

            let currentCategoryToSave = {
                id: (currentCategory) ? currentCategory.id : null,
                name: name ? name : '',
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

                        categorySelected.find(`[data-value='${savedCategory.id}']`).remove();

                        components.selects.addOptionsToSelect(categoryDataMapped, categorySelected, onCategorySelected());
                        categorySelected.selectLast();

                    })
                    .fail(() => {
                        modal.buildErrorWindow(texts.error.invalidName);
                    });
            } else {
                categoriesRestApi.create(currentCategoryToSave)
                    .done(category => {
                        currentCategory = category;

                        let categoryDataMapped = [{
                            text: category.name,
                            'data-value': category.id
                        }];

                        components.selects.addOptionsToSelect(categoryDataMapped, categorySelected, onCategorySelected());
                        categorySelected.selectLast();

                        categorySaveButtons.find('.imcms-button--error').slideDown();

                    })
                    .fail(() => {
                        modal.buildErrorWindow(texts.error.invalidName);
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
                categoryNameRow = components.texts.textAreaField('<div>', {
                    text: texts.sections.createCategory.name
                });
                return categoryNameRow;
            }

            function buildCategoryDescriptionTextField() {
                categoryDescription = components.texts.textAreaField('<div>', {
                    text: texts.sections.createCategory.description
                });
                return categoryDescription;
            }

            function onRemoveCategory() {
                modal.buildModalWindow(texts.confirmDelete, confirmed => {
                    if (!confirmed) return;

                    categoriesRestApi.remove(currentCategory)
                        .done(() => afterRemoveCategory(currentCategory.id))
                        .fail(() => modal.buildConfirmWindow(texts.error.category.removeFailed,
                            () => removeForceCategory(currentCategory)));
                });
            }

            function removeForceCategory(currentCategory){
                categoriesRestApi.removeForce(currentCategory)
                    .done(() => afterRemoveCategory(currentCategory.id))
                    .fail(() => modal.buildErrorWindow(texts.error.category.removeForceFailed));
            }

            function afterRemoveCategory(currentCategoryId){
                categorySelected.deleteOption(currentCategoryId);
                currentCategory = null;
                categorySelected.selectFirst();
                categoryCreateContainer.slideUp();
            }

            function buildEditCategoryButtonContainer() {
                return categorySaveButtons = components.buttons.buttonsContainer('<div>', [
                    components.buttons.errorButton({
                        text: texts.removeButtonName,
                        click: onRemoveCategory
                    }),
                    components.buttons.negativeButton({
                        text: texts.cancelButton,
                        click: getOnWarnCancel(() => {
                            categoryCreateContainer.slideUp();
                        })
                    }),
                    components.buttons.saveButton({
                        text: texts.saveButton,
                        click: onSaveCategory
                    }),
                ])
            }

            return categoryCreateContainer = new BEM({
                block: 'category-create-block',
                elements: {
                    'row-name': buildCategoryNameRow(),
                    'row-description': buildCategoryDescriptionTextField(),
                    'edit-buttons': buildEditCategoryButtonContainer()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'});
        }

        function onCategoryCreate() {
            currentCategory = null;
            let createCategory = {
                id: null,
                name: '',
                description: ''
            };
            categoryNameRow.setValue(createCategory.name);
            categoryDescription.setValue(createCategory.description);
            $categoryTypeCreateContainer.slideUp();
            categorySaveButtons.find('.imcms-button--error').hide();
            categoryCreateContainer.slideDown();

            return createCategory;
        }

        function buildCategoryCreateButton() {
            let $button = components.buttons.positiveButton({
                text: texts.createButtonName,
                click: onCategoryCreate
            });

            return components.buttons.buttonsContainer('<div>', [$button]);
        }

        let categoryCreateBtnContainer;

        function buildCategoryCreateButtonContainer() {

            return categoryCreateBtnContainer = new BEM({
                block: 'create-button-block',
                elements: {
                    'create': buildCategoryCreateButton(),
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'});
        }

        const CategoriesAdminTab = function (name, tabElements) {
            SuperAdminTab.call(this, name, tabElements);
        };

        CategoriesAdminTab.prototype = Object.create(SuperAdminTab.prototype);

        CategoriesAdminTab.prototype.getDocLink = () => texts.documentationLink;

        return new CategoriesAdminTab(texts.name, [
            buildDropListCtgTypesContainer(),
            buildCategoryCreateContainer()
        ]);
    }
);
