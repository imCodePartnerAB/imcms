Imcms.define("imcms-access-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-roles-rest-api", "imcms-page-info-tab-form-builder",
        "imcms-uuid-generator", "jquery"
    ],
    function (BEM, components, rolesRestApi, tabContentBuilder, uuidGenerator, $) {

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

            var $roleTitle = rolesBEM.buildBlockElement("column-title", "<div>", mapRoleOnSelectOption(role)),
                radioName = uuidGenerator.generateUUID(),
                $roleView = buildRole("VIEW", role, radioName),
                $roleEdit = buildRole("EDIT", role, radioName),
                $roleRestricted1 = buildRole("RESTRICTED_1", role, radioName),
                $roleRestricted2 = buildRole("RESTRICTED_2", role, radioName),
                $row = rolesBEM.buildBlockElement("row", "<div>", {"data-role-id": role.id}),
                $deleteRoleButton = rolesBEM.makeBlockElement("button", components.buttons.closeButton({
                    click: function () {
                        components.selects.addOptionsToSelect([mapRoleOnSelectOption(role)], $addRoleSelect);

                        if ($addRoleSelect.hasOptions()) {
                            displayAddRoleSelectAndBtn($addRoleSelect);
                        }

                        var $rolesBody = $row.parent();

                        $row.detach();

                        var tableContainsRows = $rolesBody.find("[data-role-id]").length;
                        if (!tableContainsRows) {
                            hideRolesField($rolesBody);
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
                    }
                }))
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

        return {
            name: "access",
            buildTab: function (index, docId) {
                var $addRoleSelect = components.selects.imcmsSelect("<div>");

                var $addRoleButton = components.buttons.neutralButton({
                        text: "Add role",
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
                    }).buildBlockStructure("<div>"),

                    $accessBlock = tabContentBuilder.buildFormBlock([$addRoleContainer], index)
                ;

                if (!docId) {
                    rolesRestApi.read(null).done(function (roles) {
                        var rolesDataMapped = roles.map(mapRoleOnSelectOption);
                        components.selects.addOptionsToSelect(rolesDataMapped, $addRoleSelect);
                    });
                }

                var $titleRole = rolesBEM.buildBlockElement("title", "<div>", {text: "role"}),
                    $titleView = rolesBEM.buildBlockElement("title", "<div>", {text: "view"}),
                    $titleEdit = rolesBEM.buildBlockElement("title", "<div>", {text: "edit"}),
                    $titleRestricted1 = rolesBEM.buildBlockElement("title", "<div>", {text: "restricted 1"}),
                    $titleRestricted2 = rolesBEM.buildBlockElement("title", "<div>", {text: "restricted 2"}),
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

                $accessBlock.prepend($rolesField);

                return $accessBlock;
            },

            fillTabDataFromDocument: function (document) {

                var $addRoleSelect = tabData.$addRoleSelect;
                var $rolesBody = tabData.$rolesBody;

                var roles = createRolesRows($addRoleSelect, document);
                if (roles.length) {
                    $rolesBody.prepend(roles);
                    tabData.$rolesField.css("display", "block");
                }

                function createRolesRows($addRoleSelect, document) {

                    function documentContainsRole(document, role) {
                        return document.roles.some(function (docRole) {
                            return role.id === docRole.id;
                        });
                    }

                    rolesRestApi.read(null).done(function (roles) {
                        var rolesDataMapped = roles.filter(function (role) {
                            return !documentContainsRole(document, role);
                        }).map(mapRoleOnSelectOption);

                        $addRoleSelect.clearSelect();

                        var addRoleDisplay = "none",
                            $addRoleBtn = $addRoleSelect.next();

                        if (rolesDataMapped.length) {
                            addRoleDisplay = "block";
                            components.selects.addOptionsToSelect(rolesDataMapped, $addRoleSelect);
                        }

                        $addRoleSelect.css("display", addRoleDisplay);
                        $addRoleBtn.css("display", addRoleDisplay);
                    });

                    return document.roles.map(function (docRole) {
                        return generateRoleRow(docRole, $addRoleSelect);
                    });
                }
            },

            saveData: function (documentDTO) {
                documentDTO.roles = tabData.$rolesBody.find("[data-role-id]")
                    .toArray()
                    .map(function (roleRow) {
                        var $roleRow = $(roleRow);

                        var radios$ = $roleRow.find(".imcms-radio")
                            .map(function () {
                                return $(this);
                            })
                            .toArray();

                        var permission = components.radios.group.apply(components.radios, radios$).getCheckedValue();

                        return {
                            id: $roleRow.data("roleId"),
                            name: $roleRow.text(),
                            permission: permission
                        };
                    });

                return documentDTO;
            },

            clearTabData: function () {
                tabData.$rolesBody.empty();
                tabData.$rolesField.css("display", "none");
            }
        };
    }
);
