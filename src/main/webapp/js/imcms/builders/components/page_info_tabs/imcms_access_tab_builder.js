Imcms.define("imcms-access-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-roles-rest-api", "imcms-uuid-generator", "jquery",
        "imcms-i18n-texts", "imcms-page-info-tab"
    ],
    function (BEM, components, rolesRestApi, uuidGenerator, $, texts, PageInfoTab) {

        texts = texts.pageInfo.access;

        var storedRoleIdsPerRoles;
        var storedRoles;

        var rolesBEM = new BEM({
            block: "imcms-access-role",
            elements: {
                "head": "",
                "title": "imcms-title",
                "body": "",
                "row": "",
                "column-title": "imcms-title",
                "column": "imcms-radio",
                "button": "imcms-button"
            }
        });

        function storeRoles(roles) {
            storedRoles = roles;
            storedRoleIdsPerRoles = {};

            roles.forEach(function (role) {
                storedRoleIdsPerRoles[role.id] = role;
            });
        }

        function mapRoleOnSelectOption(role) {
            return {
                text: role.name,
                "data-value": role.id
            }
        }

        function generateRoleRow(role, $addRoleSelect) {
            function buildRole(roleName, role, radioName) {
                return rolesBEM.makeBlockElement("column", components.radios.imcmsRadio("<div>", {
                    name: radioName,
                    value: roleName,
                    checked: roleName === role.permission ? "checked" : undefined
                }));
            }

            function displayAddRoleSelectAndBtn($addRoleSelect) {
                $addRoleSelect.css("display", "block");
                var $addRoleBtn = $addRoleSelect.next();
                $addRoleBtn.css("display", "block");
            }

            function hideRolesField($rolesBody) {
                var $rolesField = $rolesBody.parent().parent();
                $rolesField.css("display", "none");
            }

            var $roleTitle = rolesBEM.buildBlockElement("column-title", "<div>", mapRoleOnSelectOption(role)),
                radioName = uuidGenerator.generateUUID(),
                $roleView = buildRole("VIEW", role, radioName),
                $roleEdit = buildRole("EDIT", role, radioName),
                $roleRestricted1 = buildRole("RESTRICTED_1", role, radioName),
                $roleRestricted2 = buildRole("RESTRICTED_2", role, radioName),
                $row = rolesBEM.buildBlockElement("row", "<div>", {"data-role-id": role.id}),
                onDeleteRoleClick = function () {
                    components.selects.addOptionsToSelect([mapRoleOnSelectOption(role)], $addRoleSelect);

                    if ($addRoleSelect.hasOptions()) {
                        displayAddRoleSelectAndBtn($addRoleSelect);
                    }

                    var $rolesBody = $row.parent();

                    $row.remove();

                    var tableContainsRows = $rolesBody.find("[data-role-id]").length;
                    if (!tableContainsRows) {
                        hideRolesField($rolesBody);
                    }
                },
                $deleteRoleButton = rolesBEM.makeBlockElement(
                    "button", components.buttons.closeButton({click: onDeleteRoleClick})
                )
            ;

            return $row.append([
                $roleTitle,
                $roleView,
                $roleEdit,
                $roleRestricted1,
                $roleRestricted2,
                $deleteRoleButton
            ]);
        }

        var tabData = {};

        var AccessTab = function (name) {
            PageInfoTab.call(this, name);
        };

        AccessTab.prototype = Object.create(PageInfoTab.prototype);

        AccessTab.prototype.isDocumentTypeSupported = function () {
            return true; // all supported
        };
        AccessTab.prototype.tabElementsFactory = function (index, docId) {
            var $addRoleSelect = components.selects.imcmsSelect("<div>");

            if (!docId) {
                function mapRoles(roles) {
                    var rolesDataMapped = roles.map(mapRoleOnSelectOption);
                    components.selects.addOptionsToSelect(rolesDataMapped, $addRoleSelect);
                }

                storedRoles ? mapRoles(storedRoles) : rolesRestApi.read(null).done(function (roles) {
                    storeRoles(roles);
                    mapRoles(roles);
                });
            }

            var $titleRole = rolesBEM.buildBlockElement("title", "<div>", {text: texts.role}),
                $titleView = rolesBEM.buildBlockElement("title", "<div>", {text: texts.view}),
                $titleEdit = rolesBEM.buildBlockElement("title", "<div>", {text: texts.edit}),
                $titleRestricted1 = rolesBEM.buildBlockElement("title", "<div>", {text: texts.restricted_1}),
                $titleRestricted2 = rolesBEM.buildBlockElement("title", "<div>", {text: texts.restricted_2}),
                $rolesHead = $("<div>", {
                    html: [$titleRole, $titleView, $titleEdit, $titleRestricted1, $titleRestricted2]
                }),
                $rolesBody = $("<div>"),
                $rolesTable = rolesBEM.buildBlock("<div>", [
                    {"head": $rolesHead},
                    {"body": $rolesBody}
                ]),
                $rolesField = new BEM({
                    block: "imcms-field",
                    elements: {
                        "access-role": $rolesTable
                    }
                }).buildBlockStructure("<div>")
            ;

            tabData.$addRoleSelect = $addRoleSelect;
            tabData.$rolesBody = $rolesBody;
            tabData.$rolesField = $rolesField.css("display", "none");

            var $addRoleButton = components.buttons.neutralButton({
                    text: texts.addRole,
                    click: function () {
                        var id = $addRoleSelect.getSelectedValue();
                        var role = {
                            id: id,
                            name: $addRoleSelect.selectedText()
                        };

                        var $row = generateRoleRow(role, $addRoleSelect);
                        $row.find(":radio")
                            .first()
                            .prop("checked", "checked");

                        $rolesBody.append($row);
                        $addRoleSelect.deleteOption(id);

                        if (!$addRoleSelect.hasOptions()) {
                            $addRoleSelect.css("display", "none");
                            $addRoleButton.css("display", "none");
                        } else {
                            $addRoleSelect.selectFirst();
                        }

                        $rolesField.css("display", "block");
                    }
                }),

                $addRoleInnerBlock = new BEM({
                    block: "imcms-access-addrole",
                    elements: {
                        "select": $addRoleSelect,
                        "button": $addRoleButton
                    }
                }).buildBlockStructure("<div>"),

                $addRoleContainer = new BEM({
                    block: "imcms-field",
                    elements: {
                        "access-role": $addRoleInnerBlock
                    }
                }).buildBlockStructure("<div>")
            ;

            return [$rolesField, $addRoleContainer];
        };
        AccessTab.prototype.fillTabDataFromDocument = function (document) {
            function documentContainsRole(document, role) {
                return document.roleIdToPermission[role.id];
            }

            function buildRolesRows(roles) {
                var rolesDataMapped = roles.filter(function (role) {
                    return !documentContainsRole(document, role);
                }).map(mapRoleOnSelectOption);

                tabData.$addRoleSelect.clearSelect();

                var addRoleDisplay = "none",
                    $addRoleBtn = tabData.$addRoleSelect.next();

                if (rolesDataMapped.length) {
                    addRoleDisplay = "block";
                    components.selects.addOptionsToSelect(rolesDataMapped, tabData.$addRoleSelect);
                }

                tabData.$addRoleSelect.css("display", addRoleDisplay);
                $addRoleBtn.css("display", addRoleDisplay);

                var $roles = Object.keys(document.roleIdToPermission).map(function (roleId) {
                    var role = storedRoleIdsPerRoles[roleId];
                    role.permission = document.roleIdToPermission[roleId];

                    return generateRoleRow(role, tabData.$addRoleSelect);
                });

                if ($roles.length) {
                    tabData.$rolesBody.prepend($roles);
                    tabData.$rolesField.css("display", "block");
                }
            }

            (storedRoles) ? buildRolesRows(storedRoles) : rolesRestApi.read(null).done(function (roles) {
                storeRoles(roles);
                buildRolesRows(roles);
            });
        };
        AccessTab.prototype.saveData = function (documentDTO) {
            documentDTO.roleIdToPermission = {};

            tabData.$rolesBody.find("[data-role-id]")
                .toArray()
                .forEach(function (roleRow) {
                    var $roleRow = $(roleRow);
                    var radios$ = $roleRow.find(".imcms-radio")
                        .map(function () {
                            return $(this);
                        })
                        .toArray();

                    var permission = components.radios.group.apply(components.radios, radios$).getCheckedValue();
                    var id = $roleRow.data("roleId");

                    documentDTO.roleIdToPermission[id] = permission;
                });

            return documentDTO;
        };
        AccessTab.prototype.clearTabData = function () {
            tabData.$rolesBody.empty();
            tabData.$rolesField.css("display", "none");
        };

        return new AccessTab(texts.name);
    }
);
