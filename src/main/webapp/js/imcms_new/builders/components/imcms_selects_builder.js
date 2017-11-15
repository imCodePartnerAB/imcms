/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 25.07.17.
 */
Imcms.define("imcms-selects-builder",
    ["imcms-bem-builder", "imcms-primitives-builder", "imcms-buttons-builder", "jquery"],
    function (BEM, primitives, buttons, $) {
        var fieldBEM = new BEM({
                block: "imcms-field",
                elements: {
                    "select": "imcms-select"
                }
            }),
            selectBEM = new BEM({
                block: "imcms-select",
                elements: {
                    "drop-down-list": "imcms-drop-down-list"
                }
            }),
            dropDownListBEM = new BEM({
                block: "imcms-drop-down-list",
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
            if (!$(e.target).parents(".imcms-select").length) {
                $(".imcms-select__drop-down-list").removeClass("imcms-select__drop-down-list--active");
                e.stopPropagation();
            }
        }

        $(document).click(closeSelect);

        function toggleSelect() {
            $(this).closest(".imcms-select")
                .find(".imcms-drop-down-list")
                .toggleClass("imcms-select__drop-down-list--active")
                .children(".imcms-drop-down-list__items")
                .find(".imcms-drop-down-list__item")
                .click(onOptionSelected);
        }

        function onOptionSelected() {
            var $this = $(this),
                content = $this.text(),
                value = $this.data("value"),
                $select = $this.closest(".imcms-select__drop-down-list"),
                itemValue = $select.find(".imcms-drop-down-list__select-item-value").html(content)
            ;

            // todo: implement labeling selected item by [selected] attribute

            $select.removeClass("imcms-select__drop-down-list--active")
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
            return $select.find(".imcms-drop-down-list__items")
                .append(mapOptionsToItemsArr(options, dropDownListBEM))
                .end()
                .selectFirst();
        }

        function createSelectOptions(options, dropDownListBEM) {
            var $itemsContainer = dropDownListBEM.buildElement("items", "<div>")
                .append(mapOptionsToItemsArr(options, dropDownListBEM)),

                $button = dropDownListBEM.makeBlockElement("button", buttons.dropDownButton()),
                $selectedValue = dropDownListBEM.buildBlockElement("select-item-value", "<span>", {
                    text: (options[0] && options[0].text) || ""
                }),
                $selectItem = dropDownListBEM.buildElement("select-item", "<div>", {click: toggleSelect})
                    .append($selectedValue, $button),

                $dropDownList = dropDownListBEM.buildBlock("<div>", [
                    {"select-item": $selectItem},
                    {"items": $itemsContainer}
                ]);

            return selectBEM.makeBlockElement("drop-down-list", $dropDownList);
        }

        function apiSelectValue($resultImcmsSelect, $selectedValInput) {
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

        function apiSelectFirst($resultImcmsSelect) {
            return function () {
                var $selectCandidate = $resultImcmsSelect.find(".imcms-drop-down-list__items")
                    .find(".imcms-drop-down-list__item").first();

                if ($selectCandidate.length) {
                    onOptionSelected.call($selectCandidate);
                    return $resultImcmsSelect;

                } else {
                    console.error("Select is empty, nothing to choose");
                    console.error($resultImcmsSelect[0]);
                }
            }
        }

        function apiGetSelect($select) {
            return function () {
                return $select;
            }
        }

        function apiSelectedValue($resultImcmsSelect) {
            return function () {
                return $resultImcmsSelect.parent().find("input").val();
            }
        }

        function apiSelectedText($resultImcmsSelect) {
            return function () {
                return $resultImcmsSelect.parent().find("input").data("content");
            }
        }

        function apiClearSelect($resultImcmsSelect) {
            return function () {
                var $input = $resultImcmsSelect.parent().find("input");
                $input.val("");
                $input.removeProp("data-content");
                return $resultImcmsSelect.find(".imcms-drop-down-list").detach();
            }
        }

        function apiDeleteOption($resultImcmsSelect) {
            return function (optionValue) {
                return $resultImcmsSelect.find("[data-value='" + optionValue + "']").detach();

            }
        }

        function apiHasOptions($resultImcmsSelect) {
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

                if (options && options.length) {
                    $selectElements.push(createSelectOptions(options, dropDownListBEM));
                }

                var $selectedValInput = $("<input>", {
                    type: "hidden",
                    id: attributes.id,
                    name: attributes.name
                }); // todo: implement putting selected value into this input from [data-value] attribute

                $selectElements.push($selectedValInput);

                var $resultImcmsSelect = selectBEM.buildBlock("<div>", blockElements,
                    (attributes["class"] ? {"class": attributes["class"]} : {})
                ).append($selectElements);

                $resultImcmsSelect.selectValue = apiSelectValue($resultImcmsSelect, $selectedValInput);
                $resultImcmsSelect.selectFirst = apiSelectFirst($resultImcmsSelect);
                $resultImcmsSelect.selectedValue = apiSelectedValue($resultImcmsSelect);
                $resultImcmsSelect.selectedText = apiSelectedText($resultImcmsSelect);
                $resultImcmsSelect.clearSelect = apiClearSelect($resultImcmsSelect);
                $resultImcmsSelect.deleteOption = apiDeleteOption($resultImcmsSelect);
                $resultImcmsSelect.hasOptions = apiHasOptions($resultImcmsSelect);

                return $resultImcmsSelect;
            },
            addOptionsToSelect: function (options, $select) {
                var selectContainsDropDownList = $select.find(".imcms-select__drop-down-list").length;

                return selectContainsDropDownList
                    ? addOptionsToExistingDropDown(options, $select, dropDownListBEM)
                    : $select.append(createSelectOptions(options, dropDownListBEM)).selectFirst();
            },
            selectContainer: function (tag, attributes, options) {
                var clas = (attributes && attributes["class"]) || "";

                if (clas) {
                    delete attributes["class"];
                }

                var $select = this.imcmsSelect("<div>", attributes, options),
                    resultContainer = fieldBEM.buildBlock("<div>", [$select], (clas ? {"class": clas} : {}), "select");

                resultContainer.getSelect = apiGetSelect($select);

                return resultContainer;
            }
        }
    }
);
