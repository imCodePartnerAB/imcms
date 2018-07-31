/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-roles-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-roles-rest-api', 'imcms',
        'imcms-bem-builder', 'imcms-role-editor', 'jquery', 'imcms-role-to-row-transformer', 'imcms-authentication'
    ],
    function (SuperAdminTab, texts, components, rolesRestApi, imcms, BEM, roleEditor, $, roleToRow, auth) {

        texts = texts.superAdmin.roles;

        var $rolesContainer;

        function wrapInImcmsField($wrapMe) {
            return $('<div>', {
                'class': 'imcms-field',
                html: $wrapMe
            })
        }

        function buildTabTitle() {
            return wrapInImcmsField(components.texts.titleText('<div>', texts.title))
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
            return wrapInImcmsField(components.buttons.positiveButton({
                text: texts.createNewRole,
                click: onCreateNewRole
            }));
        }

        function buildRolesContainer() {
            $rolesContainer = $('<div>', {
                'class': 'roles-table'
            });

            rolesRestApi.read().success(function (roles) {
                $rolesContainer.append(roles.map(function (role) {
                    return roleToRow.transform(role, roleEditor)
                }))
            });

            return wrapInImcmsField([$rolesContainer, roleEditor.buildContainer()]);
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
                        'roles': ''
                    }
                });

                var providers$ = providers.map(function (provider) {

                    var $title = components.texts.titleText('<div>', provider.providerName).append($('<img>', {
                        'class': 'auth-provider-icon',
                        src: imcms.contextPath + provider.iconPath
                    }));
                    var $roles = $('<div>');
                    var $providerBlock = providerBEM.buildBlock('<div>', [
                        {'title': $title},
                        {'roles': $roles}
                    ]);
                    return externalRolesBEM.makeBlockElement('auth-provider', $providerBlock);
                });

                $externalRolesContainer.append(providers$);
            });

            return wrapInImcmsField($externalRolesContainer);
        }

        return new SuperAdminTab(texts.name, [
            buildTabTitle(),
            buildCreateNewRoleButton(),
            buildRolesContainer(),
            buildExternalRolesContainer()
        ]);
    }
);
