/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-categories-admin-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-field-wrapper',
        'imcms-bem-builder', 'jquery', 'imcms-category-types-rest-api', 'imcms-categories-rest-api',
        'imcms-category-types-editor', 'imcms-modal-window-builder'],
    function (SuperAdminTab, texts, components, fieldWrapper, BEM, $, typesRestApi, categoriesRestApi, typeEditor,
              modal) {

        texts = texts.superAdmin.categories;

        let createContainer;
        let currentCtgType;
        let editorContainer;
        let categoryCreateContainer;
        let currentCategory;

        function buildViewCategoriesTypes() {

            var onSelected = function (value) {
                typesRestApi.getById(value).done(function (ctgType) {
                    currentCtgType = ctgType;
                    let edit = typeEditor.editCategoryType($('<div>'), {
                        id: currentCtgType.id,
                        name: currentCtgType.name,
                        singleSelect: currentCtgType.singleSelect,
                        multiSelect: currentCtgType.multiSelect,
                        inherited: currentCtgType.inherited,
                        imageArchive: currentCtgType.imageArchive

                    });
                    editorContainer.slideDown();
                    return edit;
                })
            };

            let categoryTypeSelect = components.selects.selectContainer('<div>', {
                id: "types-filter",
                name: "types-filter",
                emptySelect: true,
                text: texts.chooseType,
                onSelected: onSelected
            });


            typesRestApi.read().done(function (ctgTypes) {
                let categoriesTypesDataMapped = ctgTypes.map(function (categoryType) {
                    return {
                        text: categoryType.name,
                        'data-value': categoryType.id
                    }
                });

                components.selects.addOptionsToSelect(categoriesTypesDataMapped, categoryTypeSelect.getSelect(), onSelected);
            });

            return categoryTypeSelect;

        }

        let view;
        function buildViewContainer() {
            return view = new BEM({
                block: 'shows-block',
                elements: {
                    'view': buildViewCategoriesTypes()
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

                typesRestApi.remove(currentCtgType).done(function () {
                    currentCtgType = null;
                    editorContainer.slideUp();
                    createContainer.slideUp();
                });
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
                    'create': buildCategoryTypeCreateButton(),
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
            return function () {
                modal.buildModalWindow(texts.warnCancelMessage, function (confirmed) {
                    if (!confirmed) return;
                    onConfirm.call();
                })
            }
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
                        click: getOnWarnCancel(function () {
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
                    'list-category-types': buildViewCategoriesTypes(),
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
                    click: function () {

                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }


            function buildCategoryRemoveButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.removeButtonName,
                    click: function () {

                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }


            function buildCategoryViewButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.viewButtonName,
                    click: function () {

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
            buildViewContainer(),
            showCtgTypeCreateContainer(),
            buildEditorCtgTypeButtonsContainer(),
            buildCategoryCreateContainer()
        ]);
    }
);
