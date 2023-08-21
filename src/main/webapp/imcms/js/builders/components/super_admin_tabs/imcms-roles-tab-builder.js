/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-roles-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-roles-rest-api', 'imcms',
        'imcms-bem-builder', 'imcms-role-editor', 'jquery', 'imcms-role-to-row-transformer', 'imcms-authentication',
        'imcms-azure-roles-rest-api', 'imcms-cgi-roles-rest-api', 'imcms-external-to-local-roles-links-rest-api', 'imcms-field-wrapper',
        'imcms-modal-window-builder'
    ],
    function (
        SuperAdminTab, texts, components, rolesRestApi, imcms, BEM, roleEditor, $, roleToRow, auth, azureRoles,
        cgiRoles, externalToLocalRolesLinks, fieldWrapper, modal
    ) {

        texts = texts.superAdmin.roles;

        const roleLoader = {
            roles: false,
            callbacks: [],
            whenRolesLoaded: function (callback) {
                (this.roles) ? callback(this.roles) : this.callbacks.push(callback)
            },
            runCallbacks: function (roles) {
                this.roles = roles;

                this.callbacks.forEach(callback => {
                    callback(roles);
                });
            }
        };

        rolesRestApi.read()
            .done(roles => {
                roleLoader.runCallbacks(roles);
            })
            .fail(() => modal.buildErrorWindow(texts.error.loadFailed));

        let $rolesContainer;

        function buildTabTitle() {
            return fieldWrapper.wrap(components.texts.titleText('<div>', texts.title));
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
                    accessToDocumentEditor: false,
                    publishOwnDocuments: false,
                    publishAllDocuments: false
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

            roleLoader.whenRolesLoaded(roles => {
                $rolesContainer.append(roles.map(role => roleToRow.transform(role, roleEditor)));
            });

            return fieldWrapper.wrap([$rolesContainer, roleEditor.buildContainer()]);
        }

        function buildExternalRolesContainer() {
            const externalRolesBEM = new BEM({
                block: 'external-roles',
                elements: {
                    'auth-provider': ''
                }
            });

            const $externalRolesContainer = externalRolesBEM.buildBlock('<div>', [], {style: 'display: none;'});

            auth.getAuthProviders()
                .done(providers => {
                    if (!providers || !providers.length) return;

                    $externalRolesContainer.css('display', 'block');

                    const providerBEM = new BEM({
                        block: 'external-provider',
                        elements: {
                            'title': '',
                            'text': '',
                            'roles': ''
                        }
                    });

                    const externalRolesRowBEM = new BEM({
                        block: 'external-role',
                        elements: {
                            'name': '',
                            'linked-local-roles': '',
                            'controls': ''
                        }
                    });

                    const providers$ = providers.map(provider => {
	                    const $roles = $('<div>');
	                    const rolesProvider = () => {
		                    switch (provider.providerId) {
			                    case azureRoles.getAuthenticationProviderId():
				                    return azureRoles;
			                    case cgiRoles.getAuthenticationProviderId():
				                    return cgiRoles;
		                    }
	                    }

	                    rolesProvider().read()
                            .done(externalRoles => {
                                const roles$ = externalRoles.map(externalRole => {
                                    const $externalRoleName = $('<div>', {
                                        text: externalRole.displayName
                                    });

                                    const $selectWrapper = $('<div>');
                                    const $controlsWrapper = $('<div>');

                                    const $row = externalRolesRowBEM.buildBlock(
                                        '<div>',
                                        [
                                            {'name': $externalRoleName},
                                            {'linked-local-roles': $selectWrapper},
                                            {'controls': $controlsWrapper}
                                        ],
                                        {'class': 'imcms-field'}
                                    );

                                    const requestData = {
                                        id: externalRole.id,
                                        providerId: externalRole.providerId
                                    }; // only these two are required for request

                                    externalToLocalRolesLinks.read(requestData)
                                        .done(linkedRoles => {
                                            roleLoader.whenRolesLoaded(roles => {

                                                let $rolesSelect;

                                                const $saveButton = components.buttons.saveButton({
                                                    text: texts.save,
                                                    style: 'display: none;',
                                                    click: () => {
                                                        const selectedRolesId = $rolesSelect.getSelectedValues();
                                                        const request = {
                                                            externalRole: requestData,
                                                            localRolesId: selectedRolesId
                                                        };
                                                        externalToLocalRolesLinks.replace(request)
                                                            .done(() => {
                                                                linkedRoles = selectedRolesId.map(selectedRoleId => ({id: selectedRoleId}));
                                                                $saveButton.add($cancelButton).css('display', 'none');
                                                            })
                                                            .fail(() => modal.buildErrorWindow(texts.error.externalRoles.updateFailed));
                                                    }
                                                });

                                                var $cancelButton = components.buttons.negativeButton({
                                                    text: texts.cancel,
                                                    style: 'display: none;',
                                                    click: () => {
                                                        $saveButton.add($cancelButton).css('display', 'none');
                                                        $selectWrapper.empty().append(buildRolesSelect());
                                                    }
                                                });

                                                function buildRolesSelect() {
                                                    const rolesDataMapped = roles.map(role => {
                                                        const attributes = {
                                                            text: role.name,
                                                            value: role.id,
                                                            change: () => {
                                                                $saveButton.add($cancelButton).css('display', 'inline-block');
                                                            }
                                                        };

                                                        for (let i = 0; i < linkedRoles.length; i++) {
                                                            const linkedRole = linkedRoles[i];

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
                                        })
                                        .fail(() => modal.buildErrorWindow(texts.error.externalRoles.loadFailed));

                                    return $row;
                                });

                                $roles.append(roles$);
                            })
                            .fail(() => modal.buildErrorWindow(texts.error.azureRoles.loadFailed));

                        const $title = components.texts.titleText('<div>', provider.providerName).append($('<img>', {
                            'class': 'auth-provider-icon',
                            src: imcms.contextPath + provider.iconPath
                        }));
                        const $text = $('<div>', {
                            'class': 'imcms-field',
                            text: texts.externalRolesInfo
                        });
                        const $providerBlock = providerBEM.buildBlock('<div>', [
                            {'title': $title},
                            {'text': $text},
                            {'roles': $roles}
                        ]);
                        return externalRolesBEM.makeBlockElement('auth-provider', $providerBlock);
                    });

                    $externalRolesContainer.append(providers$);
                })
                .fail(() => modal.buildErrorWindow(texts.error.loadProvidersFailed));

            return fieldWrapper.wrap($externalRolesContainer);
        }

        const RolesAdminTab = function (name, tabElements) {
            SuperAdminTab.call(this, name, tabElements);
        };

        RolesAdminTab.prototype = Object.create(SuperAdminTab.prototype);

        RolesAdminTab.prototype.getDocLink = () => texts.documentationLink;

        return new RolesAdminTab(texts.name, [
            buildTabTitle(),
            buildCreateNewRoleButton(),
            buildRolesContainer(),
            buildExternalRolesContainer()
        ]);
    }
);
