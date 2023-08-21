define(
    "imcms-permissions-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-document-types", "imcms-document-permission-types", "imcms-i18n-texts", "imcms-page-info-tab"
    ],
    function (BEM, components, docTypes, docPermissionTypes, texts, PageInfoTab) {

        texts = texts.pageInfo.permissions;

        function getDefaultPermissions() {
            return [{
                permission: docPermissionTypes.RESTRICTED_1
            }, {
                permission: docPermissionTypes.RESTRICTED_2
            }];
        }

        const editText = "editText",
            editMenu = "editMenu",
            editImage = "editImage",
            editLoop = "editLoop",
            editDocInfo = "editDocInfo";

        const exactPermissions = [
            editText,
            editMenu,
            editImage,
            editLoop,
            editDocInfo
        ];

        const tabData = {};

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
            return attributesArr.map(attributes => components.checkboxes.imcmsCheckbox("<div>", attributes));
        }

        function prettifyPermissionName(permissionName) {
            return permissionName.charAt(0).toUpperCase() + permissionName.replace("_", " ").toLowerCase().slice(1);
        }

        const permissionsWrapperBEM = new BEM({
            block: "imcms-field",
            elements: {
                "item": ""
            }
        });

        const PermissionsTab = function (name, docType) {
            PageInfoTab.apply(this, arguments);
        };

        PermissionsTab.prototype = Object.create(PageInfoTab.prototype);

        PermissionsTab.prototype.tabElementsFactory = () => [tabData.$permissionsWrapper = permissionsWrapperBEM.buildBlockStructure("<div>")];
        PermissionsTab.prototype.fillTabDataFromDocument = document => {
            tabData.restrictedCheckboxes$ = [];

            if (!document.restrictedPermissions || !document.restrictedPermissions.length) {
                document.restrictedPermissions = getDefaultPermissions();
            }

            document.restrictedPermissions.forEach(restrictedPermission => {
                const permissionName = restrictedPermission.permission;

                const restrictedCheckboxes$ = createRestrictedCheckboxes(permissionName);
                const $restrictedRoleRights = components.checkboxes.checkboxContainer(
                    "<div>", restrictedCheckboxes$, {title: prettifyPermissionName(permissionName)}
                );

                permissionsWrapperBEM.makeBlockElement("item", $restrictedRoleRights, ["float-l", "col-3"]);
                tabData.$permissionsWrapper.append($restrictedRoleRights);

                const restrictedCheckboxesPerName = {};

                restrictedCheckboxes$.forEach($restrictedPermCheckbox => {
                    tabData.restrictedCheckboxes$.push($restrictedPermCheckbox);
                    restrictedCheckboxesPerName[$restrictedPermCheckbox.find("input").prop("name")] = $restrictedPermCheckbox;
                });

                exactPermissions.forEach(exactPermName => {
                    restrictedCheckboxesPerName[exactPermName + "_" + permissionName].setChecked(restrictedPermission[exactPermName]);
                });
            });
        };
        PermissionsTab.prototype.saveData = function (documentDTO) {
            if (!this.isDocumentTypeSupported(documentDTO.type)) {
                return documentDTO;
            }

            const restrictedCheckboxes = {};

            tabData.restrictedCheckboxes$.forEach($restrictedPermCheckbox => {
                restrictedCheckboxes[$restrictedPermCheckbox.find("input").prop("name")] = $restrictedPermCheckbox;
            });

            documentDTO.restrictedPermissions.forEach(restrictedPermission => {
                const permissionName = restrictedPermission.permission;

                exactPermissions.forEach(exactPermName => {
                    restrictedPermission[exactPermName] = restrictedCheckboxes[exactPermName + "_" + permissionName].isChecked();
                });
            });

            return documentDTO;
        };
        PermissionsTab.prototype.clearTabData = () => {
            tabData.$permissionsWrapper.empty();
        };

        PermissionsTab.prototype.getDocLink = () => texts.documentationLink;

        return new PermissionsTab(texts.name, docTypes.TEXT);
    }
);
