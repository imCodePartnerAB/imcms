define("imcms-categories-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-categories-rest-api", "imcms-i18n-texts", "jquery",
        "imcms-page-info-tab", "imcms-modal-window-builder"
    ],
    function (BEM, components, categoriesRestApi, texts, $, PageInfoTab, modal) {

        texts = texts.pageInfo.categories;

        const tabData = {};

        function isDocumentContainsCategory(document, category) {
            if (!document) {
                return false;
            }

            const docCategoriesIds = document.categories.map(category => category.id);

            return docCategoriesIds.indexOf(category.id) !== -1;
        }

        function createMultiSelectCategoryType(categoryType, document, selectWareHouse) {

            const categoryTypeAsCheckboxGroup = categoryType.categories.map(category => {
                const $imcmsCheckbox = components.checkboxes.imcmsCheckbox("<div>", {
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
            const mappedCategoriesForSelectContainer = categoryType.categories.map(category => ({
                text: category.name,
                "data-value": category.id
            }));

            const $selectContainer = components.selects.selectContainer("<div>", {
                id: "category-type-" + categoryType.id,
                text: categoryType.name,
                emptySelect: true
            }, mappedCategoriesForSelectContainer);

            const $imcmsSelect = $selectContainer.getSelect();
            selectWareHouse.push($imcmsSelect);

            var category = categoryType.categories.filter(category => isDocumentContainsCategory(document, category))[0];

            category && $imcmsSelect.selectValue(category.id);

            return $selectContainer;
        }

        function buildCategoryTypes(categoryTypes, document) {
            const categoriesBlockElements = [];

            categoryTypes.forEach(categoryType => {
                const $categoryType = (categoryType.multiSelect)
                    ? createMultiSelectCategoryType(categoryType, document, tabData.multiSelects$)
                    : createSingleSelectCategoryType(categoryType, document, tabData.singleSelects$);

                (categoryType.visible) ? categoriesBlockElements.push($categoryType) : categoriesBlockElements;
            });

            tabData.$categoriesContainer.append(categoriesBlockElements);
        }

        function extractCategoryTypes(categories) {
            const categoryTypeIdsPerType = {};

            categories.forEach(category => {
                const categoryTypeId = category.type.id;

                if (!categoryTypeIdsPerType[categoryTypeId]) {
                    categoryTypeIdsPerType[categoryTypeId] = category.type;
                    categoryTypeIdsPerType[categoryTypeId].categories = [];
                }

                delete category.type;
                categoryTypeIdsPerType[categoryTypeId].categories.push(category);
            });

            const categoryTypes = [];

            for (var categoryType in categoryTypeIdsPerType) {
                categoryTypes.push(categoryTypeIdsPerType[categoryType]);
            }

            return categoryTypes;
        }

        function buildCategoriesContainer() {
            return tabData.$categoriesContainer = $('<div>');
        }

        const CategoriesTab = function (name) {
            PageInfoTab.call(this, name);
        };

        CategoriesTab.prototype = Object.create(PageInfoTab.prototype);

        CategoriesTab.prototype.isDocumentTypeSupported = () => {
            return true; // all supported
        };
        CategoriesTab.prototype.tabElementsFactory = function (index, docId) {
            const tabElements = [buildCategoriesContainer()];
            return tabElements;
        };
        CategoriesTab.prototype.fillTabDataFromDocument = document => {
            tabData.multiSelects$ = [];
            tabData.singleSelects$ = [];

            categoriesRestApi.read(null)
                .done(categories => {
                    const categoryTypes = extractCategoryTypes(categories);
                    buildCategoryTypes(categoryTypes, document);
                })
                .fail(() => modal.buildErrorWindow(texts.error.loadFailed));
        };
        CategoriesTab.prototype.saveData = documentDTO => {
            const multiSelectCategories = tabData.multiSelects$
                .filter($categoryCheckbox => $categoryCheckbox.isChecked())
                .map($categoryCheckbox => ({
                    id: $categoryCheckbox.getValue()
                }));

            const singleSelectCategories = tabData.singleSelects$
                .map($categorySelect => ({
                    id: $categorySelect.getSelectedValue()
                }))
                .filter(category => category.id);

            documentDTO.categories = multiSelectCategories.concat(singleSelectCategories);

            return documentDTO;
        };
        CategoriesTab.prototype.clearTabData = () => {
            tabData.$categoriesContainer.empty();
        };

        CategoriesTab.prototype.getDocLink = () => texts.documentationLink;

        return new CategoriesTab(texts.name);
    }
);
