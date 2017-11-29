/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 25.07.17.
 */
Imcms.define("imcms-selects-builder",
    ["imcms-bem-builder", "imcms-primitives-builder", "imcms-buttons-builder", "jquery"],
    function (BEM, primitives, buttons, $) {

        var SELECT__CLASS = "imcms-select",
            SELECT__CLASS_$ = "." + SELECT__CLASS,
            DROP_DOWN_LIST__CLASS = "imcms-drop-down-list",
            DROP_DOWN_LIST__CLASS_$ = "." + DROP_DOWN_LIST__CLASS,
            DROP_DOWN_LIST__ACTIVE__CLASS = "imcms-select__drop-down-list--active",
            SELECT__DROP_DOWN_LIST__CLASS_$ = ".imcms-select__drop-down-list",
            DROP_DOWN_LIST__ITEMS__CLASS_$ = ".imcms-drop-down-list__items",
            DROP_DOWN_LIST__ITEM__CLASS_$ = ".imcms-drop-down-list__item"
        ;

        var fieldBEM = new BEM({
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
            $(this).closest(SELECT__CLASS_$)
                .find(DROP_DOWN_LIST__CLASS_$)
                .toggleClass(DROP_DOWN_LIST__ACTIVE__CLASS)
                .children(DROP_DOWN_LIST__ITEMS__CLASS_$)
                .find(DROP_DOWN_LIST__ITEM__CLASS_$)
                .click(onOptionSelected);
        }

        function onOptionSelected() {
            var $this = $(this),
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

            return itemValue;
        }

        function mapOptionsToItemsArr(options, dropDownListBEM) {
            return options.map(function (option) {
                return dropDownListBEM.buildBlockElement("item", "<div>", option);
            });
        }

        function addOptionsToExistingDropDown(options, $select, dropDownListBEM) {
            return $select.find(DROP_DOWN_LIST__ITEMS__CLASS_$)
                .append(mapOptionsToItemsArr(options, dropDownListBEM))
                .end()
                .selectFirst();
        }

        function buildSelectOptions(options, dropDownListBEM) {
            var $itemsContainer = dropDownListBEM.buildElement("items", "<div>")
                .append(mapOptionsToItemsArr(options, dropDownListBEM)),

                $button = dropDownListBEM.makeBlockElement("button", buttons.dropDownButton()),
                $selectedValue = dropDownListBEM.buildBlockElement("select-item-value", "<span>", {
                    text: (options[0] && options[0].text) || "None"
                }),
                $selectItem = dropDownListBEM.buildElement("select-item", "<div>", {click: toggleSelect})
                    .append($selectedValue, $button),

                $dropDownList = dropDownListBEM.buildBlock("<div>", [
                    {"select-item": $selectItem},
                    {"items": $itemsContainer}
                ]);

            return selectBEM.makeBlockElement("drop-down-list", $dropDownList);
        }

        function createSelectValue($resultImcmsSelect, $selectedValInput) {
            return function (value) {
                var $selectCandidate = $resultImcmsSelect.find("[data-value='" + value + "']");

                if ($selectCandidate.length) {
                    onOptionSelected.call($selectCandidate);
                    $selectedValInput.val(value);
                    return $resultImcmsSelect;

                } else {
                    console.error("Value '" + value + "' for select doesn't exist");
                    console.error($resultImcmsSelect[0]);
                }
            }
        }

        function createSelectFirst($resultImcmsSelect) {
            return function () {
                var $selectCandidate = $resultImcmsSelect.find(DROP_DOWN_LIST__ITEMS__CLASS_$)
                    .find(DROP_DOWN_LIST__ITEM__CLASS_$).first();

                if ($selectCandidate.length) {
                    onOptionSelected.call($selectCandidate);
                    return $resultImcmsSelect;

                } else {
                    console.error("Select is empty, nothing to choose");
                    console.error($resultImcmsSelect[0]);
                }
            }
        }

        function createGetSelect($select) {
            return function () {
                return $select;
            }
        }

        function createGetSelectedValue($input) {
            return function () {
                return $input.val();
            }
        }

        function createSelectedText($input) {
            return function () {
                return $input.data("content");
            }
        }

        function createClearSelect($resultImcmsSelect, $input) {
            return function () {
                $input.val("");
                $input.removeProp("data-content");
                return $resultImcmsSelect.find(DROP_DOWN_LIST__CLASS_$).detach();
            }
        }

        function createDeleteOption($resultImcmsSelect) {
            return function (optionValue) {
                return $resultImcmsSelect.find("[data-value='" + optionValue + "']").detach();

            }
        }

        function createHasOptions($resultImcmsSelect) {
            return function () {
                return $resultImcmsSelect.find("[data-value]").length > 0;
            }
        }

        return {
            imcmsSelect: function (tag, attributes, options) {
                attributes = attributes || {};

                var blockElements = [];

                if (attributes.text) {
                    var $label = primitives.imcmsLabel(attributes.id, attributes.text, {click: toggleSelect});
                    blockElements = [{"label": $label}];
                }

                var $selectElements = [];

                if (attributes.emptySelect && options) {
                    options.unshift({
                        text: "None",
                        "data-value": null
                    });
                }

                if (options && options.length) {
                    $selectElements.push(buildSelectOptions(options, dropDownListBEM));
                }

                var $selectedValInput = $("<input>", {
                    type: "hidden",
                    id: attributes.id,
                    name: attributes.name
                });

                $selectElements.push($selectedValInput);

                var $resultImcmsSelect = selectBEM.buildBlock("<div>", blockElements,
                    (attributes["class"] ? {"class": attributes["class"]} : {})
                ).append($selectElements);

                $resultImcmsSelect.selectValue = createSelectValue($resultImcmsSelect, $selectedValInput);
                $resultImcmsSelect.selectFirst = createSelectFirst($resultImcmsSelect);
                $resultImcmsSelect.getSelectedValue = createGetSelectedValue($selectedValInput);
                $resultImcmsSelect.selectedText = createSelectedText($selectedValInput);
                $resultImcmsSelect.clearSelect = createClearSelect($resultImcmsSelect, $selectedValInput);
                $resultImcmsSelect.deleteOption = createDeleteOption($resultImcmsSelect);
                $resultImcmsSelect.hasOptions = createHasOptions($resultImcmsSelect);

                return $resultImcmsSelect;
            },
            addOptionsToSelect: function (options, $select) {
                var selectContainsDropDownList = $select.find(SELECT__DROP_DOWN_LIST__CLASS_$).length;

                return selectContainsDropDownList
                    ? addOptionsToExistingDropDown(options, $select, dropDownListBEM)
                    : $select.append(buildSelectOptions(options, dropDownListBEM)).selectFirst();
            },
            selectContainer: function (tag, attributes, options) {
                var clas = (attributes && attributes["class"]) || "";

                if (clas) {
                    delete attributes["class"];
                }

                var $select = this.imcmsSelect("<div>", attributes, options),
                    resultContainer = fieldBEM.buildBlock("<div>", [$select], (clas ? {"class": clas} : {}), "select");

                resultContainer.getSelect = createGetSelect($select);

                return resultContainer;
            }
        }
    }
);
