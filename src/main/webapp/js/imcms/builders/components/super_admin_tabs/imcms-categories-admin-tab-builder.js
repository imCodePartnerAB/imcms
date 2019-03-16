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

        texts = texts.superAdmin.tabCategories;

        let createTypeContainer;
        let currentCtgType;
        let categoryCreateContainer;
        let currentCategory;
        let categoriesList;

        function buildDropDownListCategoriesTypes() {
            const onCategoryTypeSelected = values => {
                typesRestApi.getById(values)
                    .done(ctgType => {
                        currentCtgType = ctgType;
                        typeEditor.editCategoryType($('<div>'), {
                            id: ctgType.id,
                            name: ctgType.name,
                            singleSelect: ctgType.singleSelect,
                            multiSelect: ctgType.multiSelect,
                            inherited: ctgType.inherited,

                        });

                        if (values) {
                            $('.categories-block').remove();
                            $('.category-types-block').append(buildCategoriesContainer(values));
                            categoriesList.slideDown();
                        }
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
        let categoryTypeSelected;

        function buildDropDownListCategories(id) {
            let onCategorySelected = function (value) {
                categoriesRestApi.getById(value).done(function (category) {
                    currentCategory = category;
                    let categoryObj = {
                        name: $categoryNameRow.setValue(currentCategory.name),
                        description: categoryDescription.setValue(currentCategory.description),
                        type: currentCtgType
                    };

                    createTypeContainer.css('display', 'none').slideUp();

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
            if (currentCtgType) {
                return;
            }
            typeEditor.editCategoryType($('<div>'), {
                id: null,
                name: '',
                singleSelect: true,
                multiSelect: false,
                inherited: false,
            });
        }

        function buildCtgTypeCreateContainer() {
            return createTypeContainer = typeEditor.buildCategoryTypeCreateContainer();
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

        let $categoryNameRow;
        let categoryDescription;
        let categoryUrlIcon;
        let categorySaveButtons;
        let categoryEditButtons;
        let errorDuplicateMessage;

        function buildErrorBlock() {
            errorDuplicateMessage = components.texts.errorText("<div>", texts.duplicateErrorName, {style: 'display: none;'});
            return errorDuplicateMessage;
        }

        function onSaveCategory() {
            let name = $categoryNameRow.getValue();
            let description = categoryDescription.getValue();
            let imageUrl = categoryUrlIcon.getValue();

            if (!name) {
                $categoryNameRow.$input.focus();
                return;
            }
            let currentCategoryToSave = {
                id: currentCategory.id,
                name: name,
                description: description,
                imageUrl: imageUrl,
            };

            if (currentCategoryToSave.id) {
                categoriesRestApi.replace(currentCategoryToSave)
                    .done(savedCategory => {
                        currentCategory = savedCategory;
                        categorySelected.find('category-create-block__row-name').text(currentCategory.name);
                        description.find('category-create-block__row-description').text(currentCategory.description);
                        imageUrl.find('category-create-block__list-category-types').text(currentCategory.imageUrl);
                    })
                    .fail(() => {
                        errorDuplicateMessage.css('display', 'inline-block').slideDown();
                    });
            } else {
                categoriesRestApi.create(currentCategoryToSave)
                    .done(category => {
                        categorySelected = category;
                    })
                    .fail(() => {
                        errorDuplicateMessage.css('display', 'inline-block').slideDown();
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

            function buildCategoryIconRow() {
                categoryUrlIcon = components.texts.textBox('<div>', {
                    text: texts.sections.createCategory.icon
                });

                //categoryUrlIcon.$input.attr('disabled', 'disabled');
                return categoryUrlIcon;
            }

            function onRemoveCategory() {
                modal.buildModalWindow('delete?', confirmed => {
                    if (!confirmed) return;

                    categoriesRestApi.remove(currentCategory)
                        .done(() => {
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
                    'row-url-image': buildCategoryIconRow(),
                    'error-duplicate': buildErrorBlock(),
                    'edit-cancel-buttons': buildCategoryViewButtons(),
                    'save-cancel-buttons': buildSaveAndCancelContainer()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'});
        }

        function buildCategoryCreateButtonContainer() {

            function openCreateContainer() {
                createTypeContainer.css('display', 'none').slideUp();
                return categoryCreateContainer.css('display', 'inline-block').slideDown();
            }

            function buildCategoryCreateButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.createButtonName,
                    click: openCreateContainer
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'create-button-block',
                elements: {
                    'title': $('<div>', {text: texts.titleCategory}),
                    'create': buildCategoryCreateButton(),
                }
            }).buildBlockStructure('<div>');
        }

        return new SuperAdminTab(texts.name, [
            buildCategoryTypeButtonsContainer(),
            buildCategoryCreateButtonContainer(),
            buildDropListCtgTypesContainer(),
            buildCtgTypeCreateContainer(),
            buildCategoryCreateContainer()
        ]);
    }
);
