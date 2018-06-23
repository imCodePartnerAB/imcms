Imcms.define("imcms-categories-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-categories-rest-api", "imcms-i18n-texts", "jquery",
        "imcms-page-info-tab"
    ],
    function (BEM, components, categoriesRestApi, texts, $, PageInfoTab) {

        texts = texts.pageInfo.categories;

        var tabData = {};

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

        function buildCategoryTypes(categoryTypes, document) {
            var categoriesBlockElements = [];

            categoryTypes.forEach(function (categoryType) {
                var $categoryType = (categoryType.multiSelect)
                    ? createMultiSelectCategoryType(categoryType, document, tabData.multiSelects$)
                    : createSingleSelectCategoryType(categoryType, document, tabData.singleSelects$);

                categoriesBlockElements.push($categoryType);
            });

            tabData.$categoriesContainer.append(categoriesBlockElements);
        }

        function extractCategoryTypes(categories) {
            var categoryTypeIdsPerType = {};

            categories.forEach(function (category) {
                var categoryTypeId = category.type.id;

                if (!categoryTypeIdsPerType[categoryTypeId]) {
                    categoryTypeIdsPerType[categoryTypeId] = category.type;
                    categoryTypeIdsPerType[categoryTypeId].categories = [];
                }

                delete category.type;
                categoryTypeIdsPerType[categoryTypeId].categories.push(category);
            });

            var categoryTypes = [];

            for (var categoryType in categoryTypeIdsPerType) {
                categoryTypes.push(categoryTypeIdsPerType[categoryType]);
            }

            return categoryTypes;
        }

        function buildCategoriesContainer() {
            return tabData.$categoriesContainer = $('<div>');
        }

        var CategoriesTab = function (name) {
            PageInfoTab.call(this, name);
        };

        CategoriesTab.prototype = Object.create(PageInfoTab.prototype);

        CategoriesTab.prototype.isDocumentTypeSupported = function () {
            return true; // all supported
        };
        CategoriesTab.prototype.tabElementsFactory = function (index, docId) {
            var tabElements = [buildCategoriesContainer()];
            docId || this.fillTabDataFromDocument(); // when creating new doc without id yet, categories still should be loaded
            return tabElements;
        };
        CategoriesTab.prototype.fillTabDataFromDocument = function (document) {
            tabData.multiSelects$ = [];
            tabData.singleSelects$ = [];

            categoriesRestApi.read(null).done(function (categories) {
                var categoryTypes = extractCategoryTypes(categories);
                buildCategoryTypes(categoryTypes, document);
            });
        };
        CategoriesTab.prototype.saveData = function (documentDTO) {
            var multiSelectCategories = tabData.multiSelects$
                .filter(function ($categoryCheckbox) {
                    return $categoryCheckbox.isChecked();
                })
                .map(function ($categoryCheckbox) {
                    return {
                        id: $categoryCheckbox.getValue()
                    };
                });

            var singleSelectCategories = tabData.singleSelects$
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
        };
        CategoriesTab.prototype.clearTabData = function () {
            tabData.$categoriesContainer.empty();
        };

        return new CategoriesTab(texts.name);
    }
);
