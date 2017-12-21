/** @namespace document.restrictedPermissions */

Imcms.define("imcms-permissions-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder",
        "imcms-page-info-tab-form-builder"
    ],
    function (BEM, components, tabContentBuilder) {

        var editText = "editText",
            editMenu = "editMenu",
            editImage = "editImage",
            editLoop = "editLoop",
            editDocInfo = "editDocInfo";

        var exactPermissions = [
            editText,
            editMenu,
            editImage,
            editLoop,
            editDocInfo
        ];

        var tabData = {};

        function createRestrictedCheckboxes(permissionSetName) {
            return mapCheckboxesFromAttributesArray([{
                name: editText + "_" + permissionSetName,
                text: "Edit text"
            }, {
                name: editMenu + "_" + permissionSetName,
                text: "Edit menu"
            }, {
                name: editImage + "_" + permissionSetName,
                text: "Edit image"
            }, {
                name: editLoop + "_" + permissionSetName,
                text: "Edit loop"
            }, {
                name: editDocInfo + "_" + permissionSetName,
                text: "Edit doc info"
            }]);
        }

        function mapCheckboxesFromAttributesArray(attributesArr) {
            return attributesArr.map(function (attributes) {
                return components.checkboxes.imcmsCheckbox("<div>", attributes);
            });
        }

        var permissionsWrapperBEM = new BEM({
            block: "imcms-field",
            elements: {
                "item": ""
            }
        });

        return {
            name: "permissions",
            buildTab: function (index) {
                tabData.$permissionsWrapper = permissionsWrapperBEM.buildBlockStructure("<div>");
                return tabContentBuilder.buildFormBlock([tabData.$permissionsWrapper], index);
            },
            fillTabDataFromDocument: function (document) {
                tabData.restrictedCheckboxes$ = [];

                document.restrictedPermissions.forEach(function (restrictedPermission) {
                    var permissionName = restrictedPermission.permission;

                    var restrictedCheckboxes$ = createRestrictedCheckboxes(permissionName);
                    var $restrictedRoleRights = components.checkboxes.checkboxContainer(
                        "<div>", restrictedCheckboxes$, {title: permissionName.replace("_", " ").toLowerCase()}
                    );

                    permissionsWrapperBEM.makeBlockElement("item", $restrictedRoleRights, ["float-l", "col-3"]);
                    tabData.$permissionsWrapper.append($restrictedRoleRights);

                    var restrictedCheckboxesPerName = {};

                    restrictedCheckboxes$.forEach(function ($restrictedPermCheckbox) {
                        tabData.restrictedCheckboxes$.push($restrictedPermCheckbox);
                        restrictedCheckboxesPerName[$restrictedPermCheckbox.find("input").prop("name")] = $restrictedPermCheckbox;
                    });

                    exactPermissions.forEach(function (exactPermName) {
                        restrictedCheckboxesPerName[exactPermName + "_" + permissionName].setChecked(restrictedPermission[exactPermName]);
                    });
                });
            },
            saveData: function (documentDTO) {
                var restrictedCheckboxes = {};

                tabData.restrictedCheckboxes$.forEach(function ($restrictedPermCheckbox) {
                    restrictedCheckboxes[$restrictedPermCheckbox.find("input").prop("name")] = $restrictedPermCheckbox;
                });

                documentDTO.restrictedPermissions.forEach(function (restrictedPermission) {
                    var permissionName = restrictedPermission.permission;

                    exactPermissions.forEach(function (exactPermName) {
                        restrictedPermission[exactPermName] = restrictedCheckboxes[exactPermName + "_" + permissionName].isChecked();
                    });
                });

                return documentDTO;
            },
            clearTabData: function () {
                tabData.$permissionsWrapper.empty();
            }
        };
    }
);
