Imcms.define("imcms-permissions-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder",
        "imcms-page-info-tabs-linker"
    ],
    function (BEM, components, tabContentBuilder) {

        var RESTRICTED_1 = "RESTRICTED_1";
        var RESTRICTED_2 = "RESTRICTED_2";

        var restrictedPermissions = [RESTRICTED_1, RESTRICTED_2];

        var edit_text = "edit_text",
            edit_menu = "edit_menu",
            edit_image = "edit_image",
            edit_loop = "edit_loop",
            edit_doc_info = "edit_doc_info";

        var exactPermissions = [
            edit_text,
            edit_menu,
            edit_image,
            edit_loop,
            edit_doc_info
        ];

        var tabData = {};
    
        return {
            name: "permissions",
            buildTab: function (index) {
                function createRestrictedCheckboxesDependingOnIndex(permissionSetName) {
                    return mapCheckboxesFromAttributesArray([{
                        name: edit_text + "_" + permissionSetName,
                        text: "Edit text"
                    }, {
                        name: edit_menu + "_" + permissionSetName,
                        text: "Edit menu"
                    }, {
                        name: edit_image + "_" + permissionSetName,
                        text: "Edit image"
                    }, {
                        name: edit_loop + "_" + permissionSetName,
                        text: "Edit loop"
                    }, {
                        name: edit_doc_info + "_" + permissionSetName,
                        text: "Edit doc info"
                    }]);
                }

                function mapCheckboxesFromAttributesArray(attributesArr) {
                    return attributesArr.map(function (attributes) {
                        return components.checkboxes.imcmsCheckbox("<div>", attributes);
                    });
                }

                var restrictedCheckboxes1 = createRestrictedCheckboxesDependingOnIndex(RESTRICTED_1);
                var $restrictedRole1Rights = components.checkboxes.checkboxContainer("<div>",
                    restrictedCheckboxes1,
                    {title: "Restricted 1"}
                );
                $restrictedRole1Rights.modifiers = ["float-l", "col-3"];

                var restrictedCheckboxes2 = createRestrictedCheckboxesDependingOnIndex(RESTRICTED_2);
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

                return tabContentBuilder.buildFormBlock([$permissionsWrapper], index);
            },
            fillTabDataFromDocument: function (document) {
                var restrictedCheckboxes = {};

                tabData.restrictedCheckboxes.forEach(function ($restrictedPermCheckbox) {
                    restrictedCheckboxes[$restrictedPermCheckbox.find("input").prop("name")] = $restrictedPermCheckbox;
                });

                document.restrictedPermissions = document.restrictedPermissions || {};

                Object.keys(document.restrictedPermissions).forEach(function (permissionName) {
                    var permission = document.restrictedPermissions[permissionName];

                    exactPermissions.forEach(function (exactPermName) {
                        restrictedCheckboxes[exactPermName + "_" + permissionName].setChecked(permission[exactPermName]);
                    });
                });
            },
            saveData: function (documentDTO) {
                var restrictedCheckboxes = {};

                tabData.restrictedCheckboxes.forEach(function ($restrictedPermCheckbox) {
                    restrictedCheckboxes[$restrictedPermCheckbox.find("input").prop("name")] = $restrictedPermCheckbox;
                });

                restrictedPermissions.forEach(function (permName) {
                    documentDTO.restrictedPermissions[permName] = documentDTO.restrictedPermissions[permName] || {};
                    var permission = documentDTO.restrictedPermissions[permName];

                    exactPermissions.forEach(function (exactPermName) {
                        permission[exactPermName] = restrictedCheckboxes[exactPermName + "_" + permName].isChecked();
                    });
                });

                return documentDTO;
            },
            clearTabData: function () {
                tabData.restrictedCheckboxes.forEach(function (checkbox) {
                    checkbox.setChecked(false);
                });
            }
        };
    }
);
