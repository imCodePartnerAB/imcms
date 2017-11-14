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
                function createRestrictedCheckboxesDependingOnIndex(permissionSetName) {
                    return mapCheckboxesFromAttributesArray([{
                        name: "edit_text_" + permissionSetName,
                        text: "Edit text"
                    }, {
                        name: "edit_menu_" + permissionSetName,
                        text: "Edit menu"
                    }, {
                        name: "edit_image_" + permissionSetName,
                        text: "Edit image"
                    }, {
                        name: "edit_loop_" + permissionSetName,
                        text: "Edit loop"
                    }, {
                        name: "edit_doc_info_" + permissionSetName,
                        text: "Edit doc info"
                    }]);
                }

                function mapCheckboxesFromAttributesArray(attributesArr) {
                    return attributesArr.map(function (attributes) {
                        return components.checkboxes.imcmsCheckbox("<div>", attributes);
                    });
                }

                var restrictedCheckboxes1 = createRestrictedCheckboxesDependingOnIndex("RESTRICTED_1");
                var $restrictedRole1Rights = components.checkboxes.checkboxContainer("<div>",
                    restrictedCheckboxes1,
                    {title: "Restricted 1"}
                );
                $restrictedRole1Rights.modifiers = ["float-l", "col-3"];

                var restrictedCheckboxes2 = createRestrictedCheckboxesDependingOnIndex("RESTRICTED_2");
                var $restrictedRole2Rights = components.checkboxes.checkboxContainer("<div>",
                    restrictedCheckboxes2,
                    {title: "Restricted 2"}
                );
                $restrictedRole2Rights.modifiers = ["float-l", "col-3"];

                tabData.restrictedCheckboxes = restrictedCheckboxes1.concat(restrictedCheckboxes2);

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

                Object.keys(document.restrictedPermissions).forEach(function (permissionName) {
                    var permission = document.restrictedPermissions[permissionName];

                    var edit_text = "edit_text",
                        edit_menu = "edit_menu",
                        edit_image = "edit_image",
                        edit_loop = "edit_loop",
                        edit_doc_info = "edit_doc_info";

                    restrictedCheckboxes[edit_text + "_" + permissionName].setChecked(permission[edit_text]);
                    restrictedCheckboxes[edit_menu + "_" + permissionName].setChecked(permission[edit_menu]);
                    restrictedCheckboxes[edit_image + "_" + permissionName].setChecked(permission[edit_image]);
                    restrictedCheckboxes[edit_loop + "_" + permissionName].setChecked(permission[edit_loop]);
                    restrictedCheckboxes[edit_doc_info + "_" + permissionName].setChecked(permission[edit_doc_info]);
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
