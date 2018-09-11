define(
    "imcms-permissions-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-document-types", "imcms-i18n-texts", "imcms-page-info-tab"
    ],
    function (BEM, components, docTypes, texts, PageInfoTab) {

        texts = texts.pageInfo.permissions;

        function getDefaultPermissions() {
            return [{
                permission: "RESTRICTED_1"
            }, {
                permission: "RESTRICTED_2"
            }];
        }

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

        var PermissionsTab = function (name, docType) {
            PageInfoTab.apply(this, arguments);
        };

        PermissionsTab.prototype = Object.create(PageInfoTab.prototype);

        PermissionsTab.prototype.tabElementsFactory = function () {
            return [tabData.$permissionsWrapper = permissionsWrapperBEM.buildBlockStructure("<div>")];
        };
        PermissionsTab.prototype.fillTabDataFromDocument = function (document) {
            tabData.restrictedCheckboxes$ = [];

            if (!document.restrictedPermissions || !document.restrictedPermissions.length) {
                document.restrictedPermissions = getDefaultPermissions();
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
        };
        PermissionsTab.prototype.saveData = function (documentDTO) {
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
        };
        PermissionsTab.prototype.clearTabData = function () {
            tabData.$permissionsWrapper.empty();
        };

        return new PermissionsTab(texts.name, docTypes.TEXT);
    }
);
