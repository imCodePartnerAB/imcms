Imcms.define("imcms-categories-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-category-types-rest-api",
        "imcms-page-info-tabs-linker"
    ],
    function (BEM, components, categoriesTypesRestApi, tabContentBuilder) {

        return {
            name: "categories",
            data: {},
            buildTab: function (index, docId) {
                this.data.$categoriesBlock = tabContentBuilder.buildFormBlock([], index);
                docId || this.fillTabDataFromDocument();
                return this.data.$categoriesBlock;
            },
            fillTabDataFromDocument: function (document) {
                this.data.$categoriesBlock && this.data.$categoriesBlock.empty();
                this.data.multiSelects$ = [];
                this.data.singleSelects$ = [];

                var categoriesBlockElements = [],
                    parentContext = this;

                categoriesTypesRestApi.read(null).done(function (categoriesTypes) {
                    categoriesTypes.forEach(function (categoryType) {
                        var $categoryType = (categoryType.multiSelect)
                            ? createMultiSelectCategoryType(categoryType, document, parentContext.data.multiSelects$)
                            : createSingleSelectCategoryType(categoryType, document, parentContext.data.singleSelects$);

                        categoriesBlockElements.push($categoryType);
                    });

                    parentContext.data.$categoriesBlock.append(categoriesBlockElements);
                });

                function isDocumentContainsCategory(document, category) {
                    if (!document) {
                        return false;
                    }

                    var docCategoriesIds = document.categories.map(function (category) {
                        return category.id;
                    });

                    return docCategoriesIds.indexOf(category.id) !== -1;
                }

                function createMultiSelectCategoryType(categoryType, document, selectWareHouse) {

                    var categoryTypeAsCheckboxGroup = categoryType.categories.map(function (category) {
                        var $imcmsCheckbox = components.checkboxes.imcmsCheckbox("<div>", {
                            name: "category-type-" + categoryType.id,
                            value: category.id,
                            text: category.name
                        });

                        selectWareHouse.push($imcmsCheckbox);

                        return $imcmsCheckbox.setChecked(isDocumentContainsCategory(document, category));
                    });

                    return components.checkboxes.checkboxContainerField("<div>",
                        categoryTypeAsCheckboxGroup,
                        {title: categoryType.name}
                    );
                }

                function createSingleSelectCategoryType(categoryType, document, selectWareHouse) {
                    var mappedCategoriesForSelectContainer = categoryType.categories.map(function (category) {
                        return {
                            text: category.name,
                            "data-value": category.id
                        };
                    });

                    var $selectContainer = components.selects.selectContainer("<div>", {
                        id: "category-type-" + categoryType.id,
                        text: categoryType.name,
                        emptySelect: true
                    }, mappedCategoriesForSelectContainer);

                    var $imcmsSelect = $selectContainer.getSelect();
                    selectWareHouse.push($imcmsSelect);

                    var category = categoryType.categories.filter(function (category) {
                        return isDocumentContainsCategory(document, category);
                    })[0];

                    category && $imcmsSelect.selectValue(category.id);

                    return $selectContainer;
                }
            },
            saveData: function (documentDTO) {
                var multiSelectCategories = this.data.multiSelects$
                    .filter(function ($categoryCheckbox) {
                        return $categoryCheckbox.isChecked();
                    })
                    .map(function ($categoryCheckbox) {
                        return {
                            id: $categoryCheckbox.getValue()
                        };
                    });

                var singleSelectCategories = this.data.singleSelects$
                    .map(function ($categorySelect) {
                        return {
                            id: $categorySelect.getSelectedValue()
                        };
                    })
                    .filter(function (category) {
                        return category.id;
                    });

                documentDTO.categories = multiSelectCategories.concat(singleSelectCategories);

                return documentDTO;
            },
            clearTabData: function () {
                this.data.$categoriesBlock && this.data.$categoriesBlock.empty();
            }
        };
    }
);
