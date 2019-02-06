/**
 * @author Victor Pavlenko from Ubrainians for imCode
 * 15.01.19
 */
define(
    'imcms-category-types-editor',
    [
        'imcms-bem-builder', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms-category-types-rest-api',
        'jquery', 'imcms-modal-window-builder'
    ],
    function (BEM, components, texts, typesRestApi, $, modal) {

        texts = texts.superAdmin.categories;

        let $typeNameRow;
        let $inherited;
        let $imageArchive;
        let $singleSelect;
        let $multiSelect;
        let errorMsg;
        let $categoryTypeSaveButtons;

        function buildTypeNameRow() {
            $typeNameRow = components.texts.textBox('<div>', {
                text: texts.sections.createCategoryType.name
            });

            return $typeNameRow;
        }

        function buildErrorBlock() {
            errorMsg = components.texts.errorText("<div>", texts.duplicateErrorName, {style: 'display: none;'});
            return errorMsg;
        }

        function buildInheriteNewDocsCheckBox() {
            return $inherited = components.checkboxes.imcmsCheckbox('<div>', {
                text: texts.sections.createCategoryType.inherited,
            });
        }

        function buildImageArchiveCheckBox() {
            return $imageArchive = components.checkboxes.imcmsCheckbox('<div>', {
                text: texts.sections.createCategoryType.imageArchive,
            });
        }

        function buildSingleSelectRadioButton() {
            return $singleSelect = components.radios.imcmsRadio('<div>', {
                text: texts.sections.createCategoryType.singleSelect,
            });
        }

        function buildMultiSelectRadioButton() {
            return $multiSelect = components.radios.imcmsRadio('<div>', {
                text: texts.sections.createCategoryType.multiSelect,
            });
        }

        function onCancelChanges($categoryTypeElement, categoryType) {

            getOnDiscardChanges(() => {
                onCategoryTypeView = onCategoryTypeSimpleView;
                currentCategoryType = categoryType;
                $categoryTypeItem = $categoryTypeElement;
                prepareCategoryTypeView();
            }).call();
        }

        function onEditCategoryType() {
            onCategoryTypeView = onCancelChanges;
            $categoryTypeSaveButtons.slideDown();
        }

        function onDeleteCategoryType() {
            modal.buildModalWindow('delete?', confirmed => {
                if (!confirmed) return;

                typesRestApi.remove(currentCategoryType).done(() => {
                    $categoryTypeItem.remove();
                    currentCategoryType = null;
                    onEditDelegate = onSimpleEdit;
                    //$container.slideUp();
                })
            });
        }


        function onSaveCategoryType() {
            let name = $typeNameRow.getValue();
            let inherited = $inherited.isChecked();
            let imageArchive = $imageArchive.isChecked();

            let currentCtgTypeToSave = {
                id: currentCategoryType.id,
                name: name,
                inherited: inherited,
                imageArchive: imageArchive
            };

            if (currentCtgTypeToSave.id) {
                typesRestApi.replace(currentCtgTypeToSave).done(savedCategoryType => {
                    currentCategoryType = savedCategoryType;
                    $categoryTypeItem.find('type-create-block__field-name').text(currentCategoryType.name);
                    $inherited.find('type-create-block__inherited').setCheckedValue(currentCategoryType.inherited);
                    $imageArchive.find('type-create-block__imageArchive').setCheckedValue(currentCategoryType.imageArchive);
                    onCategoryTypeView = onCategoryTypeSimpleView;
                    prepareCategoryTypeView();
                }).fail(() => {
                    errorMsg.css('display', 'inline-block').slideDown();
                });
            } else {
                typesRestApi.create(currentCtgTypeToSave).done(function (categoryType) {
                    $categoryTypeItem = categoryType;

                    onCategoryTypeView = onCategoryTypeSimpleView;
                    prepareCategoryTypeView();
                }).fail(function () {
                    errorMsg.css('display', 'inline-block').slideDown();
                });
            }
        }

        function getOnDiscardChanges(onConfirm) {
            return () => {
                modal.buildModalWindow(texts.warnCancelMessage, confirmed => {
                    if (!confirmed) return;
                    onConfirm.call();
                });
            }
        }

        function buildCategoryTypeSaveCancelButtons() {
            return $categoryTypeSaveButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.saveButton({
                    text: texts.saveButton,
                    click: onSaveCategoryType
                }),
                components.buttons.negativeButton({
                    text: texts.sections.removeCategoryType.removeButton,
                    click: onDeleteCategoryType
                })
            ]);
        }


        function prepareCategoryTypeView() {
            onEditDelegate = onSimpleEdit;

            $typeNameRow.setValue(currentCategoryType.name);
            $singleSelect.setChecked(currentCategoryType.singleSelect);
            $multiSelect.setChecked(currentCategoryType.multiSelect);
            $inherited.setChecked(currentCategoryType.inherited);
            $imageArchive.setChecked(currentCategoryType.imageArchive);

            $categoryTypeSaveButtons.slideDown('fast');

            $container.css('display', 'inline-block');
            errorMsg.css('display', 'none').slideUp();
        }

        function onCategoryTypeSimpleView($categoryTypeRowElement, categoryType) {
            if (currentCategoryType && currentCategoryType.id === categoryType.id) return;
            currentCategoryType = categoryType;
            $categoryTypeItem = $categoryTypeRowElement;

            prepareCategoryTypeView();
        }

        var $container;
        var $categoryTypeItem;
        var currentCategoryType;
        var onCategoryTypeView = onCategoryTypeSimpleView;

        function buildCreateCategoryTypeContainer() {
            return $container || ($container = new BEM({
                block: 'type-create-block',
                elements: {
                    'title-row': $('<div>', {text: texts.sections.createCategoryType.title}),
                    'field-name': buildTypeNameRow(),
                    'single-select': buildSingleSelectRadioButton(),
                    'multi-select': buildMultiSelectRadioButton(),
                    'inherited': buildInheriteNewDocsCheckBox(),
                    'imageArchive': buildImageArchiveCheckBox(),
                    'error-row': buildErrorBlock(),
                    'ctg-type-button-save': buildCategoryTypeSaveCancelButtons()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'}));
        }

        function viewCategoryType($categoryTypeRow, categoryType) {
            $container.slideDown();
            onCategoryTypeView($categoryTypeRow, categoryType);
        }

        var onEditDelegate = onSimpleEdit;

        function onSimpleEdit($categoryTypeRow, categoryType) {
            viewCategoryType($categoryTypeRow, categoryType);
            onEditCategoryType();
        }

        function editCategoryType($categoryTypeRow, categoryType) {
            onEditDelegate($categoryTypeRow, categoryType);
            onEditDelegate = () => {
            }
        }

        let categoryTypeEditor = {
            buildCategoryTypeCreateContainer: buildCreateCategoryTypeContainer,
            editCategoryType: editCategoryType,
            viewCategoryType: viewCategoryType,
            onDeleteCategoryType: onDeleteCategoryType,
        };

        return categoryTypeEditor;
    }
);




