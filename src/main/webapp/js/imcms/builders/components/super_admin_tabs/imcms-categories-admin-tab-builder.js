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

        let createContainer;
        let currentCtgType;
        let editorContainer;
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
                            imageArchive: ctgType.imageArchive,

                        });

                        if (values) {
                            $('.categories-block').remove();
                            $('.category-types-block').append(buildCategoriesContainer(values));
                            categoriesList.slideDown();
                            if (categoryCreateContainer) {
                                editorContainer.css('display', 'none').slideUp();
                            } else {
                                editorContainer.slideDown();
                            }
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

        function buildDropDownListCategories(id) {

            let onCategorySelected = function (value) {
                categoriesRestApi.getById(value).done(function (category) {
                    currentCategory = category;
                    typeEditor.editCategoryType($('<div>'), {
                        id: currentCategory.id,
                        name: currentCategory.name,
                        description: currentCategory.description,
                        icon: currentCategory.icon,
                        type: currentCtgType,
                    });
                })
            };

            let categorySelect = components.selects.selectContainer('<div>', {
                id: "category-filter",
                name: "category-filter",
                emptySelect: true,
                text: texts.chooseCategory,
                onSelected: onCategorySelected
            });

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

        function buildViewCtgTypesContainer() {
            return buildShowCategoryType = new BEM({
                block: 'category-types-block',
                elements: {
                    'categories-types': buildDropDownListCategoriesTypes()
                }
            }).buildBlockStructure('<div>');
        }

        function onCreateNewCategoryType() {
            createContainer.css('display', 'inline-block').slideDown();
            typeEditor.viewCategoryType($('<div>'), {
                id: null,
                name: '',
                singleSelect: null,
                multiSelect: null,
                inherited: null,
                imageArchive: null

            });
        }

        function showCtgTypeCreateContainer() {
            createContainer = typeEditor.buildCategoryTypeCreateContainer();
            return createContainer;
        }


        function buildEditorCtgTypeButtonsContainer() {

            function onEditCurrentCtgType() {

            }

            function buildCategoryTypeEditButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.editButtonName,
                    click: onEditCurrentCtgType
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            function onRemoveCtgType() {

                typesRestApi.remove(currentCtgType)
                    .done(() => {
                        currentCtgType = null;
                        editorContainer.slideUp();
                        createContainer.slideUp();
                    })
                    .fail(() => modal.buildErrorWindow(texts.categoryType.error.removeFailed));
            }

            function buildCategoryTypeRemoveButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.removeButtonName,
                    click: onRemoveCtgType
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return editorContainer = new BEM({
                block: 'upgrade-block',
                elements: {
                    'edit-button': buildCategoryTypeEditButton(),
                    'remove-button': buildCategoryTypeRemoveButton()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'});
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

        function buildEditorCategoryButtonsContainer() {

        }

        let $categoryNameRow;
        let categoryDescription;
        let categoryUrlIcon;
        let categorySaveButton;
        let categoryEditButtons;

        function onSaveCategory() {

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

            function buildCategoryIconRow() {
                categoryUrlIcon = components.texts.textBox('<div>', {
                    text: texts.sections.createCategory.icon
                });

                return categoryUrlIcon;
            }

            function buildSaveAndCancelContainer() {
                return categorySaveButton = components.buttons.buttonsContainer('<div>', [
                    components.buttons.saveButton({
                        text: texts.saveButton,
                        style: 'display: none;',
                        click: onSaveCategory
                    }),
                    components.buttons.negativeButton({
                        text: 'remove',
                        style: 'display: none;',
                        click: function () {

                        }
                    })
                ]);
            }

            function buildEditAndCancelButtons() {
                return categoryEditButtons = components.buttons.buttonsContainer('<div>', [
                    components.buttons.saveButton({
                        text: 'Edit',
                        click: function () {
                            categoryEditButtons.css('display', 'none').slideUp();
                            categorySaveButton.css('display', 'inline-block').slideDown();
                        }
                    }),
                    components.buttons.negativeButton({
                        text: texts.cancelButton,
                        click: getOnWarnCancel(() => {
                            //onCategoryTypeView = onCategoryTypeSimpleView;

                            if (currentCategory) {
                                // prepareCategoryTypeView();
                                // $container.slideUp();

                            } else {
                                // currentCategory = null;
                                // $container.slideUp();
                            }
                        })
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
                    'list-category-types': buildDropDownListCategoriesTypes(),
                    'edit-cancel-buttons': buildEditAndCancelButtons(),
                    'save-cancel-buttons': buildSaveAndCancelContainer()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'});
        }

        function buildCategoryEditButtonsContainer() {

            function openCreateContainer() {
                createContainer.css('display', 'none');
                editorContainer.css('display', 'none'); //todo fix it need add all in one block BEM, (edit,save, create buttons and fields)
                return categoryCreateContainer.css('display', 'inline-block').slideDown();
            }

            function buildCategoryCreateButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.createButtonName,
                    click: openCreateContainer
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            function buildCategoryEditButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.editButtonName,
                    click: () => {

                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }


            function buildCategoryRemoveButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.removeButtonName,
                    click: () => {

                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }


            function buildCategoryViewButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.viewButtonName,
                    click: () => {

                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'category-buttons-block',
                elements: {
                    'title': $('<div>', {text: texts.titleCategory}),
                    'create': buildCategoryCreateButton(),
                    'edit': buildCategoryEditButton(),
                    'remove': buildCategoryRemoveButton(),
                    'view': buildCategoryViewButton()
                }
            }).buildBlockStructure('<div>');
        }

        return new SuperAdminTab(texts.name, [
            buildCategoryTypeButtonsContainer(),
            buildCategoryEditButtonsContainer(),
            buildViewCtgTypesContainer(),
            showCtgTypeCreateContainer(),
            buildEditorCtgTypeButtonsContainer(),
            buildCategoryCreateContainer()
        ]);
    }
);
