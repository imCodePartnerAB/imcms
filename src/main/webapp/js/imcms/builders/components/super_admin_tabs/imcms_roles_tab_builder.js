/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-roles-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-roles-rest-api', 'imcms',
        'imcms-bem-builder', 'imcms-role-editor', 'jquery', 'imcms-role-to-row-transformer', 'imcms-authentication',
        'imcms-azure-roles-rest-api', 'imcms-external-to-local-roles-links-rest-api', 'imcms-field-wrapper'
    ],
    function (
        SuperAdminTab, texts, components, rolesRestApi, imcms, BEM, roleEditor, $, roleToRow, auth, azureRoles,
        externalToLocalRolesLinks, fieldWrapper
    ) {

        texts = texts.superAdmin.roles;

        var roleLoader = {
            roles: false,
            callbacks: [],
            whenRolesLoaded: function (callback) {
                (this.roles) ? callback(this.roles) : this.callbacks.push(callback)
            },
            runCallbacks: function (roles) {
                this.roles = roles;

                this.callbacks.forEach(function (callback) {
                    callback(roles)
                })
            }
        };

        rolesRestApi.read().success(function (roles) {
            roleLoader.runCallbacks(roles);
        });

        var $rolesContainer;

        function buildTabTitle() {
            return fieldWrapper.wrap(components.texts.titleText('<div>', texts.title))
        }

        function onCreateNewRole() {
            $rolesContainer.find('.roles-table__role-row--active')
                .removeClass('roles-table__role-row--active');

            roleEditor.editRole($('<div>'), {
                id: null,
                name: '',
                permissions: {
                    getPasswordByEmail: false,
                    accessToAdminPages: false,
                    useImagesInImageArchive: false,
                    changeImagesInImageArchive: false
                }
            });
        }

        function buildCreateNewRoleButton() {
            return fieldWrapper.wrap(components.buttons.positiveButton({
                text: texts.createNewRole,
                click: onCreateNewRole
            }));
        }

        function buildRolesContainer() {
            $rolesContainer = $('<div>', {
                'class': 'roles-table'
            });

            roleLoader.whenRolesLoaded(function (roles) {
                $rolesContainer.append(roles.map(function (role) {
                    return roleToRow.transform(role, roleEditor)
                }))
            });

            return fieldWrapper.wrap([$rolesContainer, roleEditor.buildContainer()]);
        }

        function buildExternalRolesContainer() {
            var externalRolesBEM = new BEM({
                block: 'external-roles',
                elements: {
                    'auth-provider': ''
                }
            });

            var $externalRolesContainer = externalRolesBEM.buildBlock('<div>', [], {style: 'display: none;'});

            auth.getAuthProviders().success(function (providers) {
                if (!providers || !providers.length) return;

                $externalRolesContainer.css('display', 'block');

                var providerBEM = new BEM({
                    block: 'external-provider',
                    elements: {
                        'title': '',
                        'text': '',
                        'roles': ''
                    }
                });

                var externalRolesRowBEM = new BEM({
                    block: 'external-role',
                    elements: {
                        'name': '',
                        'linked-local-roles': '',
                        'controls': ''
                    }
                });

                var providers$ = providers.map(function (provider) {
                    var $roles = $('<div>');

                    azureRoles.read().success(function (externalRoles) {
                        var roles$ = externalRoles.map(function (externalRole) {
                            var $externalRoleName = $('<div>', {
                                text: externalRole.displayName
                            });

                            var $selectWrapper = $('<div>');
                            var $controlsWrapper = $('<div>');

                            var $row = externalRolesRowBEM.buildBlock(
                                '<div>',
                                [
                                    {'name': $externalRoleName},
                                    {'linked-local-roles': $selectWrapper},
                                    {'controls': $controlsWrapper}
                                ],
                                {'class': 'imcms-field'}
                            );

                            var requestData = {
                                id: externalRole.id,
                                providerId: externalRole.providerId
                            }; // only these two are required for request

                            externalToLocalRolesLinks.read(requestData).success(function (linkedRoles) {
                                roleLoader.whenRolesLoaded(function (roles) {

                                    var $rolesSelect;

                                    var $saveButton = components.buttons.saveButton({
                                        text: texts.save,
                                        style: 'display: none;',
                                        click: function () {
                                            var selectedRolesId = $rolesSelect.getSelectedValues();
                                            var request = {
                                                externalRole: requestData,
                                                localRolesId: selectedRolesId
                                            };
                                            externalToLocalRolesLinks.replace(request).success(function () {
                                                linkedRoles = selectedRolesId.map(function (selectedRoleId) {
                                                    return {id: selectedRoleId}
                                                });
                                                $saveButton.add($cancelButton).css('display', 'none');
                                            });
                                        }
                                    });

                                    var $cancelButton = components.buttons.negativeButton({
                                        text: texts.cancel,
                                        style: 'display: none;',
                                        click: function () {
                                            $saveButton.add($cancelButton).css('display', 'none');
                                            $selectWrapper.empty().append(buildRolesSelect());
                                        }
                                    });

                                    function buildRolesSelect() {
                                        var rolesDataMapped = roles.map(function (role) {
                                            var attributes = {
                                                text: role.name,
                                                value: role.id,
                                                change: function () {
                                                    $saveButton.add($cancelButton).css('display', 'inline-block');
                                                }
                                            };

                                            for (var i = 0; i < linkedRoles.length; i++) {
                                                var linkedRole = linkedRoles[i];

                                                if (linkedRole.id === role.id) {
                                                    attributes.checked = 'checked';
                                                    break;
                                                }
                                            }

                                            return attributes
                                        });

                                        return $rolesSelect = components.selects.multipleSelect(
                                            '<div>', {}, rolesDataMapped
                                        );
                                    }

                                    $selectWrapper.append(buildRolesSelect());

                                    $controlsWrapper.append([$saveButton, $cancelButton])
                                });
                            });

                            return $row;
                        });

                        $roles.append(roles$);
                    });

                    var $title = components.texts.titleText('<div>', provider.providerName).append($('<img>', {
                        'class': 'auth-provider-icon',
                        src: imcms.contextPath + provider.iconPath
                    }));
                    var $text = $('<div>', {
                        'class': 'imcms-field',
                        text: texts.externalRolesInfo
                    });
                    var $providerBlock = providerBEM.buildBlock('<div>', [
                        {'title': $title},
                        {'text': $text},
                        {'roles': $roles}
                    ]);
                    return externalRolesBEM.makeBlockElement('auth-provider', $providerBlock);
                });

                $externalRolesContainer.append(providers$);
            });

            return fieldWrapper.wrap($externalRolesContainer);
        }

        return new SuperAdminTab(texts.name, [
            buildTabTitle(),
            buildCreateNewRoleButton(),
            buildRolesContainer(),
            buildExternalRolesContainer()
        ]);
    }
);
