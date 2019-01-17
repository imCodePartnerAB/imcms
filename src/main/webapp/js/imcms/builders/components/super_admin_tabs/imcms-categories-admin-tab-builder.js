/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-categories-admin-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-field-wrapper',
        'imcms-bem-builder', 'jquery', 'imcms-category-types-rest-api', 'imcms-categories-rest-api',
        'imcms-category-types-editor'],
    function (SuperAdminTab, texts, components, fieldWrapper, BEM, $, typesRestApi, categoriesRestApi, typeEditor) {

        texts = texts.superAdmin.categories;

        let $categoryTypeSelect;
        let createContainer;
        let currentCtgType;

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
            }).buildBlockStructure('<div>', {style: 'display: none;'});
        }

        function onCreateNewCategoryType() {
            view.css('display', 'none').slideUp('fast');
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

        function buildCategoryTypeButtonsContainer() {

            function buildCategoryTypeCreateButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.createButtonName,
                    click: onCreateNewCategoryType
                });
                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            function showCategoryTypes() {
                createContainer.css('display', 'none').slideUp('fast');
                return view.css('display', 'inline-block').slideDown();
            }

            function buildCategoryTypeEditButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.editButtonName,
                    click: showCategoryTypes
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            function showCtgTypeRemoveContainer() {

            }

            function buildCategoryTypeRemoveButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.removeButtonName,
                    click: showCtgTypeRemoveContainer
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'type-buttons-block',
                elements: {
                    'title': $('<div>', {text: texts.titleCategoryType}),
                    'create': buildCategoryTypeCreateButton(),
                    'edit': buildCategoryTypeEditButton(),
                    'remove': buildCategoryTypeRemoveButton()
                }
            }).buildBlockStructure('<div>');

        }

        function buildCategoryButtonsContainer() {

            function buildCategoryCreateButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.createButtonName,
                    click: function () {

                    }
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
            showCtgTypeCreateContainer(),
            buildViewContainer()
        ]);
    }
);
