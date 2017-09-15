Imcms.define("imcms-permissions-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder",
        "imcms-page-info-tabs-linker"
    ],
    function (BEM, components, linker) {

        var tabData = {};
    
        return {
            name: "permissions",
            buildTab: function (index) {
                function createRestrictedCheckboxesDependingOnIndex(index) {
                    return mapCheckboxesFromAttributesArray([{
                        name: "edit_text" + index,
                        text: "Edit text"
                    }, {
                        name: "edit_menu" + index,
                        text: "Edit menu"
                    }, {
                        name: "edit_image" + index,
                        text: "Edit image"
                    }, {
                        name: "edit_loop" + index,
                        text: "Edit loop"
                    }, {
                        name: "edit_doc_info" + index,
                        text: "Edit doc info"
                    }]);
                }

                function mapCheckboxesFromAttributesArray(attributesArr) {
                    return attributesArr.map(function (attributes) {
                        return components.checkboxes.imcmsCheckbox("<div>", attributes);
                    });
                }

                var restrictedCheckboxes0 = createRestrictedCheckboxesDependingOnIndex(0);
                var $restrictedRole1Rights = components.checkboxes.checkboxContainer("<div>",
                    restrictedCheckboxes0,
                    {title: "Restricted 1"}
                );
                $restrictedRole1Rights.modifiers = ["float-l", "col-3"];

                var restrictedCheckboxes1 = createRestrictedCheckboxesDependingOnIndex(1);
                var $restrictedRole2Rights = components.checkboxes.checkboxContainer("<div>",
                    restrictedCheckboxes1,
                    {title: "Restricted 2"}
                );
                $restrictedRole2Rights.modifiers = ["float-l", "col-3"];

                tabData.restrictedCheckboxes = restrictedCheckboxes0.concat(restrictedCheckboxes1);

                var $permissionsWrapper = new BEM({
                    block: "imcms-field",
                    elements: {
                        "item": [$restrictedRole1Rights, $restrictedRole2Rights]
                    }
                }).buildBlockStructure("<div>");

                return linker.buildFormBlock([$permissionsWrapper], index);
            },
            fillTabDataFromDocument: function (document) {
                var restrictedCheckboxes = {};

                tabData.restrictedCheckboxes.forEach(function (permission) {
                    restrictedCheckboxes[permission.find("input").prop("name")] = permission;
                });

                document.permissions.forEach(function (permission, index) {
                    var edit_text = "edit_text",
                        edit_menu = "edit_menu",
                        edit_image = "edit_image",
                        edit_loop = "edit_loop",
                        edit_doc_info = "edit_doc_info";

                    restrictedCheckboxes[edit_text + index].setChecked(permission[edit_text]);
                    restrictedCheckboxes[edit_menu + index].setChecked(permission[edit_menu]);
                    restrictedCheckboxes[edit_image + index].setChecked(permission[edit_image]);
                    restrictedCheckboxes[edit_loop + index].setChecked(permission[edit_loop]);
                    restrictedCheckboxes[edit_doc_info + index].setChecked(permission[edit_doc_info]);
                });
            },
            clearTabData: function () {
                tabData.restrictedCheckboxes.forEach(function (checkbox) {
                    checkbox.setChecked(false);
                });
            }
        };
    }
);
