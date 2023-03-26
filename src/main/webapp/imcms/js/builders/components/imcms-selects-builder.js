/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 25.07.17.
 */
const logger = require('imcms-logger');

define("imcms-selects-builder",
    ["imcms-bem-builder", "imcms-primitives-builder", "imcms-buttons-builder", "jquery", "imcms-checkboxes-builder", "imcms-i18n-texts"],
    function (BEM, primitives, buttons, $, checkboxesBuilder, texts) {

        const SELECT__CLASS = "imcms-select",
            SELECT__CLASS_$ = "." + SELECT__CLASS,
            DROP_DOWN_LIST__CLASS = "imcms-drop-down-list",
            DROP_DOWN_LIST__CLASS_$ = "." + DROP_DOWN_LIST__CLASS,
            DROP_DOWN_LIST__ACTIVE__CLASS = "imcms-select__drop-down-list--active",
            SELECT__DROP_DOWN_LIST__CLASS_$ = ".imcms-select__drop-down-list",
            DROP_DOWN_LIST__ITEMS__CLASS_$ = ".imcms-drop-down-list__items",
            DROP_DOWN_LIST__ITEM__CLASS_$ = ".imcms-drop-down-list__item"
        ;

        const fieldBEM = new BEM({
                block: "imcms-field",
                elements: {
                    "select": SELECT__CLASS
                }
            }),
            selectBEM = new BEM({
                block: SELECT__CLASS,
                elements: {
                    "drop-down-list": DROP_DOWN_LIST__CLASS
                }
            }),
            dropDownListBEM = new BEM({
                block: DROP_DOWN_LIST__CLASS,
                elements: {
                    "select-item": "",
                    "items": "",
                    "item": "",
                    "checkbox": "",
                    "select-item-value": "",
                    "button": "imcms-button"
                }
            })
        ;

        function closeSelect(e) {
            if (!$(e.target).parents(SELECT__CLASS_$).length) {
                $(SELECT__DROP_DOWN_LIST__CLASS_$).removeClass(DROP_DOWN_LIST__ACTIVE__CLASS);
                e.stopPropagation();
            }
        }

        $(document).click(closeSelect);

        function toggleSelect() {
            const $select = $(this).closest(SELECT__CLASS_$);

            if ($select.is('[disabled]')) return;

            $select.find(DROP_DOWN_LIST__CLASS_$).toggleClass(DROP_DOWN_LIST__ACTIVE__CLASS)
        }

        function onOptionSelected(onSelected) {

            const $this = $(this),
                content = $this.text(),
                value = $this.data("value"),
                $select = $this.closest(SELECT__DROP_DOWN_LIST__CLASS_$),
                itemValue = $select.find(".imcms-drop-down-list__select-item-value").html(content)
            ;

            // todo: implement labeling selected item by [selected] attribute

            $select.removeClass(DROP_DOWN_LIST__ACTIVE__CLASS)
                .parent()
                .find("input")
                .data("content", content)
                .val(value);

            onSelected && onSelected.call && onSelected(value);

            return itemValue;
        }

        function mapMultiSelectOptionsToItemsArr(options) {
            return options.map(option => dropDownListBEM.makeBlockElement(
                "item", checkboxesBuilder.imcmsCheckbox("<div>", option)
            ));
        }

        function mapOptionsToItemsArr(options, onSelected) {
            return options.map(function (option) {
                option.click = function () {
                    onOptionSelected.call(this, onSelected)
                };
                return dropDownListBEM.buildBlockElement("item", "<div>", option);
            });
        }

        function addOptionsToExistingDropDown(options, $select, onSelected) {
            return $select.find(DROP_DOWN_LIST__ITEMS__CLASS_$)
                .append(mapOptionsToItemsArr(options, onSelected))
                .end()
                .selectFirst();
        }

        function addMultiSelectOptionsToExistingDropDown(options, $select) {
            return $select.find(DROP_DOWN_LIST__ITEMS__CLASS_$)
                .append(mapMultiSelectOptionsToItemsArr(options));
        }

        function bindGetSelectedValues($select) {
            return function () {
                return $select.find("input:checked")
                    .map(function () {
                        return $(this).val();
                    })
                    .toArray();
            }
        }

        function buildMultiSelectOptions(options) {
            const $itemsContainer = dropDownListBEM.buildElement("items", "<div>").append(
                mapMultiSelectOptionsToItemsArr(options)
            );

            const $button = dropDownListBEM.makeBlockElement("button", buttons.dropDownButton());

            const $selectedValue = dropDownListBEM.buildBlockElement("select-item-value", "<span>", {
                text: "Choose values"
            });
            const $selectItem = dropDownListBEM.buildElement("select-item", "<div>", {click: toggleSelect})
                .append($selectedValue, $button);

            const $dropDownList = dropDownListBEM.buildBlock("<div>", [
                {"select-item": $selectItem},
                {"items": $itemsContainer}
            ]);

            return selectBEM.makeBlockElement("drop-down-list", $dropDownList);
        }

        function buildSelectOptions(options, onSelected, styleAttributes) {
            const $itemsContainer = dropDownListBEM.buildElement("items", "<div>").append(
                mapOptionsToItemsArr(options, onSelected)
            );
            if (styleAttributes) {
                styleAttributes.forEach(style => {
                    $itemsContainer.css(style)
                });
            }

            const $button = dropDownListBEM.makeBlockElement("button", buttons.dropDownButton());

            const $selectedValue = dropDownListBEM.buildBlockElement("select-item-value", "<span>", {
                text: (options[0] && options[0].text) || texts.none
            });
            const $selectItem = dropDownListBEM.buildElement("select-item", "<div>", {click: toggleSelect})
                .append($selectedValue, $button);

            const $dropDownList = dropDownListBEM.buildBlock("<div>", [
                {"select-item": $selectItem},
                {"items": $itemsContainer}
            ]);

            return selectBEM.makeBlockElement("drop-down-list", $dropDownList);
        }

        function bindSelectValue($resultImcmsSelect, $selectedValInput) {
            return value => {
                const $selectCandidate = $resultImcmsSelect.find("[data-value='" + value + "']");

                if ($selectCandidate.length) {
                    onOptionSelected.call($selectCandidate, $resultImcmsSelect.onSelected);
                    $selectedValInput && $selectedValInput.val(value);
                    return $resultImcmsSelect;

                } else {
                    logger.log("%c Value '" + value + "' for select doesn't exist", "color: red;");
                    logger.log($resultImcmsSelect[0]);
                }
            }
        }

        function bindSelectFirst($resultImcmsSelect) {
            return () => {
                const $selectCandidate = $resultImcmsSelect.find(DROP_DOWN_LIST__ITEMS__CLASS_$)
                    .find(DROP_DOWN_LIST__ITEM__CLASS_$).first();

                selectItem($selectCandidate, $resultImcmsSelect);

            }
        }

        function bindSelectLast($resultImcmsSelect) {
            return () => {
                const $selectCandidate = $resultImcmsSelect.find(DROP_DOWN_LIST__ITEMS__CLASS_$)
                    .find(DROP_DOWN_LIST__ITEM__CLASS_$).last();

                selectItem($selectCandidate, $resultImcmsSelect);
            }
        }

        function selectItem($selectCandidate, $resultImcmsSelect) {
            if ($selectCandidate.length) {
                onOptionSelected.call($selectCandidate, $resultImcmsSelect.onSelected);
                return $resultImcmsSelect;

            } else {
                $resultImcmsSelect.find(".imcms-drop-down-list__select-item-value").html(texts.none)
                logger.log("%c Select is empty, nothing to choose", "color: red;");
                logger.log($resultImcmsSelect[0]);
            }
        }

        function bindGetSelect($select) {
            return () => $select;
        }

        function bindGetSelectedValue($input) {
            return () => $input.val();
        }

        function bindSelectedText($input) {
            return () => $input.data("content");
        }

        function bindClearSelect($resultImcmsSelect, $input) {
            return () => {
                $input.val("");
                $input.removeProp("data-content");
                return $resultImcmsSelect.find(DROP_DOWN_LIST__CLASS_$).remove();
            };
        }

        function bindDeleteOption($resultImcmsSelect) {
            return optionValue => $resultImcmsSelect.find("[data-value='" + optionValue + "']").remove();
        }

        function bindHasOptions($resultImcmsSelect) {
            return () => $resultImcmsSelect.find("[data-value]").length > 0;
        }

        function bindApi($select, $selectedValInput) {
            $select.selectValue = bindSelectValue($select, $selectedValInput);
            $select.selectFirst = bindSelectFirst($select);
            $select.selectLast = bindSelectLast($select);
            $select.getSelectedValue = bindGetSelectedValue($selectedValInput);
            $select.selectedText = bindSelectedText($selectedValInput);
            $select.clearSelect = bindClearSelect($select, $selectedValInput);
            $select.deleteOption = bindDeleteOption($select);
            $select.hasOptions = bindHasOptions($select);
        }

        function buildSelectLabel(attributes) {
            return primitives.imcmsLabel(attributes.id, attributes.text, {click: toggleSelect});
        }

        return {
            multipleSelect: (tag, attributes, options) => {
                attributes = attributes || {};
                options = options || [];

                let blockElements = [];

                if (attributes.text) {
                    blockElements = [{"label": buildSelectLabel(attributes)}];
                }

	            if (attributes.click) {
		            options.forEach(option => option.click = attributes.click);
				}

				if (attributes.change){
					options.forEach(option => option.change = attributes.change);
				}

                const $selectElements = [];

                if (options && options.length) {
                    $selectElements.push(buildMultiSelectOptions(options));
                }

                const $select = selectBEM.buildBlock(
                    "<div>", blockElements, (attributes["class"] ? {"class": attributes["class"]} : {})
                ).append($selectElements);

                $select.selectValue = bindSelectValue($select);
                $select.getSelectedValues = bindGetSelectedValues($select);

                return $select;
            },
            imcmsSelect: (tag, attributes, options) => {
                attributes = attributes || {};
                options = options || [];

                let blockElements = [];

                if (attributes.text) {
                    blockElements = [{"label": buildSelectLabel(attributes)}];
                }

                const $selectElements = [];

                if (attributes.emptySelect) {
                    let emptySelectText = attributes.emptySelectText ? attributes.emptySelectText : texts.none;

                    options.unshift({
                        text: emptySelectText,
                        "data-value": null
                    });
                }

                if (options && options.length) {
                    $selectElements.push(buildSelectOptions(options, attributes.onSelected, attributes.style));
                }

                const $selectedValInput = $("<input>", {
                    type: "hidden",
                    id: attributes.id,
                    name: attributes.name
                });

                $selectElements.push($selectedValInput);

                const $resultImcmsSelect = selectBEM.buildBlock(
                    "<div>", blockElements, (attributes["class"] ? {"class": attributes["class"]} : {})
                ).append($selectElements);

                bindApi($resultImcmsSelect, $selectedValInput);

                return $resultImcmsSelect;
            },
            makeImcmsSelect: function ($existingSelect) {
                $existingSelect.find('.imcms-drop-down-list__select-item').click(toggleSelect);
                $existingSelect.find(DROP_DOWN_LIST__ITEM__CLASS_$).each(function () {
                    $(this).click(onOptionSelected)
                });

                bindApi($existingSelect, $existingSelect.find('input[type=hidden]'));

                return $existingSelect;
            },
            addOptionsToSelect: (options, $select, onSelected) => {
                const selectContainsDropDownList = $select.find(SELECT__DROP_DOWN_LIST__CLASS_$).length;

                return selectContainsDropDownList
                    ? addOptionsToExistingDropDown(options, $select, onSelected)
                    : $select.append(buildSelectOptions(options, onSelected)).selectFirst();
            },
            addOptionsToMultiSelect: (options, $select) => {
                const selectContainsDropDownList = $select.find(SELECT__DROP_DOWN_LIST__CLASS_$).length;

                return selectContainsDropDownList
                    ? addMultiSelectOptionsToExistingDropDown(options, $select)
                    : $select.append(buildMultiSelectOptions(options));
            },
            selectContainer: function (tag, attributes, options) {
                const clas = (attributes && attributes["class"]) || "";

                if (clas) {
                    delete attributes["class"];
                }

                const $select = this.imcmsSelect("<div>", attributes, options),
                    resultContainer = fieldBEM.buildBlock("<div>", [$select], (clas ? {"class": clas} : {}), "select");

                resultContainer.getSelect = bindGetSelect($select);

                return resultContainer;
            }
        }
    }
);
