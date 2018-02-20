/** @namespace document.restrictedPermissions */

Imcms.define(
    "imcms-permissions-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-page-info-tab-form-builder", "imcms-document-types",
        "imcms-i18n-texts"
    ],
    function (BEM, components, tabContentBuilder, docTypes, texts) {

        texts = texts.pageInfo.permissions;

        var defaultPermissions = [{
            permission: "RESTRICTED_1"
        }, {
            permission: "RESTRICTED_2"
        }];

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
                text: texts.editText
            }, {
                name: editMenu + "_" + permissionSetName,
                text: texts.editMenu
            }, {
                name: editImage + "_" + permissionSetName,
                text: texts.editImage
            }, {
                name: editLoop + "_" + permissionSetName,
                text: texts.editLoop
            }, {
                name: editDocInfo + "_" + permissionSetName,
                text: texts.editDocInfo
            }]);
        }

        function mapCheckboxesFromAttributesArray(attributesArr) {
            return attributesArr.map(function (attributes) {
                return components.checkboxes.imcmsCheckbox("<div>", attributes);
            });
        }

        function prettifyPermissionName(permissionName) {
            return permissionName.charAt(0).toUpperCase() + permissionName.replace("_", " ").toLowerCase().slice(1);
        }

        var permissionsWrapperBEM = new BEM({
            block: "imcms-field",
            elements: {
                "item": ""
            }
        });

        return {
            name: texts.name,
            tabIndex: null,
            isDocumentTypeSupported: function (docType) {
                return docType === docTypes.TEXT;
            },
            showTab: function () {
                tabContentBuilder.showTab(this.tabIndex);
            },
            hideTab: function () {
                tabContentBuilder.hideTab(this.tabIndex);
            },
            buildTab: function (index) {
                this.tabIndex = index;
                tabData.$permissionsWrapper = permissionsWrapperBEM.buildBlockStructure("<div>");
                return tabContentBuilder.buildFormBlock([tabData.$permissionsWrapper], index);
            },
            fillTabDataFromDocument: function (document) {
                tabData.restrictedCheckboxes$ = [];

                if (!document.restrictedPermissions || !document.restrictedPermissions.length) {
                    document.restrictedPermissions = defaultPermissions;
                }

                document.restrictedPermissions.forEach(function (restrictedPermission) {
                    var permissionName = restrictedPermission.permission;

                    var restrictedCheckboxes$ = createRestrictedCheckboxes(permissionName);
                    var $restrictedRoleRights = components.checkboxes.checkboxContainer(
                        "<div>", restrictedCheckboxes$, {title: prettifyPermissionName(permissionName)}
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
                if (!this.isDocumentTypeSupported(documentDTO.type)) {
                    return documentDTO;
                }

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
