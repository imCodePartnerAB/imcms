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

        texts = texts.superAdmin.tabCategories;

        let $typeNameRow, $isInherited, $isSingleSelect, $isMultiSelect, errorMsg, $categoryTypeSaveButtons,
            $categoryTypeEditButtons, valueRadios, radioButtonsGroup;

        function buildTypeNameRow() {
            $typeNameRow = components.texts.textBox('<div>', {
                text: texts.sections.createCategoryType.name
            });
            //$typeNameRow.$input.attr('disabled', 'disabled');
            return $typeNameRow;
        }

        function buildErrorBlock() {
            errorMsg = components.texts.errorText("<div>", texts.duplicateErrorName, {style: 'display: none;'});
            return errorMsg;
        }


        function buildCategoryTypeProperty() {

            return $isInherited = components.checkboxes.imcmsCheckbox("<div>", {
                text: texts.sections.createCategoryType.inherited
            })
        }

        function buildCategoryTypeSelectionModes() {

            valueRadios = [
                $isSingleSelect = components.radios.imcmsRadio("<div>", {
                    text: texts.sections.createCategoryType.singleSelect,
                    name: 'select',
                    value: 'single-select',
                }),
                $isMultiSelect = components.radios.imcmsRadio("<div>", {
                    text: texts.sections.createCategoryType.multiSelect,
                    name: 'select',
                    value: 'multi-select',
                }),
            ];

            radioButtonsGroup = components.radios.group($isSingleSelect, $isMultiSelect);


            return components.radios.radioContainer(
                '<div>', valueRadios, {}
            );
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

            $categoryTypeEditButtons.slideUp();
            $categoryTypeSaveButtons.slideDown();

            // $typeNameRow.$input.removeAttr('disabled').focus();
            //
            // valueCheckboxes$.forEach($checkbox => {
            //     $checkbox.$input.removeAttr('disabled');
            // });
            //
            // valueRadios.forEach($radios => {
            //     $radios.$input.removeAttr('disabled');
            // });
        }

        function onDeleteCategoryType() {
            modal.buildModalWindow('delete?', confirmed => {
                if (!confirmed) return;

                typesRestApi.remove(currentCategoryType)
                    .done(() => {
                        $categoryTypeItem.remove();
                        currentCategoryType = null;
                        onEditDelegate = onSimpleEdit;
                        $container.slideUp();
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.removeFailed));
            });
        }


        function onSaveCategoryType() {
            let ckeckValue = radioButtonsGroup.getCheckedValue();

            let name = $typeNameRow.getValue();
            let inherited = $isInherited.isChecked();

            if (!name) {
                $typeNameRow.$input.focus();
                return;
            }

            let currentCtgTypeToSave = {
                id: currentCategoryType.id,
                name: name,
                singleSelect: (ckeckValue === 'single-select'), // foreach
                multiSelect: (ckeckValue === 'multi-select'),
                inherited: inherited,
            };

            if (currentCtgTypeToSave.id) {
                typesRestApi.replace(currentCtgTypeToSave)
                    .done(savedCategoryType => {
                        currentCategoryType.id = savedCategoryType.id;
                        currentCategoryType.name = savedCategoryType.name;
                        currentCategoryType.singleSelect = savedCategoryType.singleSelect;
                        currentCategoryType.multiSelect = savedCategoryType.multiSelect;
                        currentCategoryType.inherited = savedCategoryType.inherited;

                        onCategoryTypeView = onCategoryTypeSimpleView;
                        prepareCategoryTypeView();
                    })
                    .fail(() => {
                        errorMsg.css('display', 'inline-block').slideDown();
                    });
            } else {
                typesRestApi.create(currentCtgTypeToSave)
                    .done(function (categoryType) {
                        $categoryTypeItem = categoryType;

                        onCategoryTypeView = onCategoryTypeSimpleView;
                        prepareCategoryTypeView();
                    })
                    .fail(() => {
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
            };
        }

        function buildCategoryTypeSaveButtons() {
            return $categoryTypeSaveButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.saveButton({
                    text: texts.saveButton,
                    click: onSaveCategoryType
                }),
                components.buttons.negativeButton({
                    text: texts.cancelButton,
                    click: getOnDiscardChanges(() => {
                        $categoryTypeSaveButtons.slideUp();
                        $categoryTypeEditButtons.slideDown();
                    })
                })
            ], {
                style: 'display: none;'
            });
        }

        function buildCategoryTypeEditButtons() {
            return $categoryTypeEditButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.positiveButton({
                    text: texts.editButtonName,
                    click: onEditCategoryType
                }),
                components.buttons.negativeButton({
                    text: texts.removeButtonName,
                    click: onDeleteCategoryType
                })
            ]);
        }


        function prepareCategoryTypeView() {
            onEditDelegate = onSimpleEdit;

            $typeNameRow.setValue(currentCategoryType.name);
            $isSingleSelect.setChecked((currentCategoryType.multiSelect === false));
            $isMultiSelect.setChecked(currentCategoryType.multiSelect);
            $isInherited.setChecked(currentCategoryType.inherited);

            $categoryTypeSaveButtons.slideDown();

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
                    'selection-modes': buildCategoryTypeSelectionModes(),
                    'properties': buildCategoryTypeProperty(),
                    'error-row': buildErrorBlock(),
                    'ctg-type-view-button': buildCategoryTypeEditButtons(),
                    'ctg-type-edit-button': buildCategoryTypeSaveButtons()
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
            };
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




