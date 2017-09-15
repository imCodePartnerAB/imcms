Imcms.define("imcms-categories-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-category-types-rest-api",
        "imcms-page-info-tabs-linker"
    ],
    function (BEM, components, categoriesTypesRestApi, linker) {

        return {
            name: "categories",
            data: {},
            buildTab: function (index, docId) {
                this.data.$categoriesBlock = linker.buildFormBlock([], index);
                docId || this.fillTabDataFromDocument();
                return this.data.$categoriesBlock;
            },
            fillTabDataFromDocument: function (document) {
                this.data.$categoriesBlock && this.data.$categoriesBlock.empty();
                var categoriesBlockElements = [],
                    parentContext = this;

                categoriesTypesRestApi.read(null)
                    .done(function (categoriesTypes) {
                        categoriesTypes.forEach(function (categoryType) {
                            var $categoryType = (categoryType.multi_select)
                                ? createMultiSelectCategoryType(categoryType, document)
                                : createSingleSelectCategoryType(categoryType, document);

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

                function createMultiSelectCategoryType(categoryType, document) {

                    var categoryTypeAsCheckboxGroup = categoryType.categories.map(function (category) {
                        return components.checkboxes
                            .imcmsCheckbox("<div>", {
                                name: "category-type-" + categoryType.id,
                                value: category.id,
                                text: category.name
                            })
                            .setChecked(isDocumentContainsCategory(document, category));
                    });

                    return components.checkboxes.checkboxContainerField("<div>",
                        categoryTypeAsCheckboxGroup,
                        {title: categoryType.name}
                    );
                }

                function createSingleSelectCategoryType(categoryType, document) {
                    var mappedCategoriesForSelectContainer = categoryType.categories.map(function (category) {
                        return {
                            text: category.name,
                            "data-value": category.id
                        };
                    });

                    var $selectContainer = components.selects.selectContainer("<div>", {
                        id: "category-type-" + categoryType.id,
                        text: categoryType.name
                    }, mappedCategoriesForSelectContainer);

                    var category = categoryType.categories.filter(function (category) {
                        return isDocumentContainsCategory(document, category);
                    })[0];

                    category && $selectContainer.getSelect().selectValue(category.id);

                    return $selectContainer;
                }
            },
            clearTabData: function () {
                this.fillTabDataFromDocument();
            }
        };
    }
);
