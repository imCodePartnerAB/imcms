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
                        let edit = typeEditor.editCategoryType($('<div>'), {
                            id: ctgType.id,
                            name: ctgType.name,
                            singleSelect: ctgType.singleSelect,
                            multiSelect: ctgType.multiSelect,
                            inherited: ctgType.inherited,
                            imageArchive: ctgType.imageArchive,

                        });

                        buildDropDownListCategories(values);

                        editorContainer.slideDown();
                        categoriesList.slideDown();

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

        let buildCategory;

        function buildDropDownListCategories(v) {

            let onCategorySelected = function (value) {
                categoriesRestApi.getById(value).done(function (category) {
                    currentCategory = category;
                    let edit = typeEditor.editCategoryType($('<div>'), {
                        id: currentCategory.id,
                        name: currentCategory.name,
                        description: currentCategory.description,
                        icon: currentCategory.icon,
                        type: currentCtgType,
                    });

                    return edit;
                })
            };

            let categorySelect = components.selects.selectContainer('<div>', {
                id: "category-filter",
                name: "category-filter",
                emptySelect: true,
                text: texts.chooseCategory,
                onSelected: onCategorySelected
            });
            categoriesRestApi.getCategoriesByCategoryTypeId(v).done(function (categories) {

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


        function buildCategoriesContainer() {

            return new BEM({
                block: 'categories-block',
                elements: {
                    'categories': buildDropDownListCategories()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'});
        }


        let buildShowCategoryType;

        function buildViewCtgTypesContainer() {
            return buildShowCategoryType = new BEM({
                block: 'shows-block',
                elements: {
                    'view': buildDropDownListCategoriesTypes()
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
                        click: onSaveCategory
                    }),
                    components.buttons.negativeButton({
                        text: texts.cancelButton,
                        click: getOnWarnCancel(() => {
                            //onCategoryTypeView = onCategoryTypeSimpleView;

                            if (currentCategory.id) {
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
                    'save-cancel-buttons': buildSaveAndCancelContainer()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'});
        }

        function buildCategoryButtonsContainer() {

            function openCreateContainer() {
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
            buildCategoryButtonsContainer(),
            buildViewCtgTypesContainer(),
            showCtgTypeCreateContainer(),
            buildEditorCtgTypeButtonsContainer(),
            categoriesList = buildCategoriesContainer(),
            buildCategoryCreateContainer()
        ]);
    }
);
