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

        // fixme: toggle select also binds onClick EACH TIME
        function toggleSelect(onSelected) {
            var $select = $(this).closest(SELECT__CLASS_$);

            if ($select.is('[disabled]')) return;

            $select.find(DROP_DOWN_LIST__CLASS_$)
                .toggleClass(DROP_DOWN_LIST__ACTIVE__CLASS)
                .children(DROP_DOWN_LIST__ITEMS__CLASS_$)
                .find(DROP_DOWN_LIST__ITEM__CLASS_$)
                .click(function () {
                    onOptionSelected.call(this, onSelected)
                });
        }

        function onOptionSelected(onSelected) {

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

            onSelected && onSelected.call && onSelected(value);

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

        function buildSelectOptions(options, dropDownListBEM, onSelected) {
            var $itemsContainer = dropDownListBEM.buildElement("items", "<div>").append(
                mapOptionsToItemsArr(options, dropDownListBEM)
            );

            var $button = dropDownListBEM.makeBlockElement("button", buttons.dropDownButton());

            var $selectedValue = dropDownListBEM.buildBlockElement("select-item-value", "<span>", {
                text: (options[0] && options[0].text) || "None"
            });
            var $selectItem = dropDownListBEM.buildElement("select-item", "<div>", {
                click: function () {
                    toggleSelect.call(this, onSelected);
                }
            }).append($selectedValue, $button);

            var $dropDownList = dropDownListBEM.buildBlock("<div>", [
                {"select-item": $selectItem},
                {"items": $itemsContainer}
            ]);

            return selectBEM.makeBlockElement("drop-down-list", $dropDownList);
        }

        function bindSelectValue($resultImcmsSelect, $selectedValInput) {
            return function (value) {
                var $selectCandidate = $resultImcmsSelect.find("[data-value='" + value + "']");

                if ($selectCandidate.length) {
                    onOptionSelected.call($selectCandidate, $resultImcmsSelect.onSelected);
                    $selectedValInput.val(value);
                    return $resultImcmsSelect;

                } else {
                    console.log("%c Value '" + value + "' for select doesn't exist", "color: red;");
                    console.log($resultImcmsSelect[0]);
                }
            }
        }

        function bindSelectFirst($resultImcmsSelect) {
            return function () {
                var $selectCandidate = $resultImcmsSelect.find(DROP_DOWN_LIST__ITEMS__CLASS_$)
                    .find(DROP_DOWN_LIST__ITEM__CLASS_$).first();

                if ($selectCandidate.length) {
                    onOptionSelected.call($selectCandidate, $resultImcmsSelect.onSelected);
                    return $resultImcmsSelect;

                } else {
                    console.log("%c Select is empty, nothing to choose", "color: red;");
                    console.log($resultImcmsSelect[0]);
                }
            }
        }

        function bindGetSelect($select) {
            return function () {
                return $select;
            }
        }

        function bindGetSelectedValue($input) {
            return function () {
                return $input.val();
            }
        }

        function bindSelectedText($input) {
            return function () {
                return $input.data("content");
            }
        }

        function bindClearSelect($resultImcmsSelect, $input) {
            return function () {
                $input.val("");
                $input.removeProp("data-content");
                return $resultImcmsSelect.find(DROP_DOWN_LIST__CLASS_$).detach();
            }
        }

        function bindDeleteOption($resultImcmsSelect) {
            return function (optionValue) {
                return $resultImcmsSelect.find("[data-value='" + optionValue + "']").detach();

            }
        }

        function bindHasOptions($resultImcmsSelect) {
            return function () {
                return $resultImcmsSelect.find("[data-value]").length > 0;
            }
        }

        function bindApi($select, $selectedValInput) {
            $select.selectValue = bindSelectValue($select, $selectedValInput);
            $select.selectFirst = bindSelectFirst($select);
            $select.getSelectedValue = bindGetSelectedValue($selectedValInput);
            $select.selectedText = bindSelectedText($selectedValInput);
            $select.clearSelect = bindClearSelect($select, $selectedValInput);
            $select.deleteOption = bindDeleteOption($select);
            $select.hasOptions = bindHasOptions($select);
        }

        return {
            imcmsSelect: function (tag, attributes, options) {
                attributes = attributes || {};
                options = options || [];

                var blockElements = [];

                if (attributes.text) {
                    var $label = primitives.imcmsLabel(attributes.id, attributes.text, {
                        click: function () {
                            toggleSelect.call(this, attributes.onSelected);
                        }
                    });
                    blockElements = [{"label": $label}];
                }

                var $selectElements = [];

                if (attributes.emptySelect) {
                    options.unshift({
                        text: "None",
                        "data-value": null
                    });
                }

                if (options && options.length) {
                    $selectElements.push(buildSelectOptions(options, dropDownListBEM, attributes.onSelected));
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

                bindApi($resultImcmsSelect, $selectedValInput);

                return $resultImcmsSelect;
            },
            makeImcmsSelect: function ($existingSelect) {
                $existingSelect.find('.imcms-drop-down-list__select-item').click(toggleSelect);
                bindApi($existingSelect, $existingSelect.find('input[type=hidden]'));

                return $existingSelect;
            },
            addOptionsToSelect: function (options, $select, onSelected) {
                var selectContainsDropDownList = $select.find(SELECT__DROP_DOWN_LIST__CLASS_$).length;

                return selectContainsDropDownList
                    ? addOptionsToExistingDropDown(options, $select, dropDownListBEM)
                    : $select.append(buildSelectOptions(options, dropDownListBEM, onSelected)).selectFirst();
            },
            selectContainer: function (tag, attributes, options) {
                var clas = (attributes && attributes["class"]) || "";

                if (clas) {
                    delete attributes["class"];
                }

                var $select = this.imcmsSelect("<div>", attributes, options),
                    resultContainer = fieldBEM.buildBlock("<div>", [$select], (clas ? {"class": clas} : {}), "select");

                resultContainer.getSelect = bindGetSelect($select);

                return resultContainer;
            }
        }
    }
);
