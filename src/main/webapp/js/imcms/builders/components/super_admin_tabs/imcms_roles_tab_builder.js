/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-roles-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-roles-rest-api',
        'imcms-bem-builder', 'imcms-role-editor', 'jquery', 'imcms-role-to-row-transformer'
    ],
    function (SuperAdminTab, texts, components, rolesRestApi, BEM, roleEditor, $, roleToRow) {

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

        return new SuperAdminTab(texts.name, [
            buildTabTitle(),
            buildCreateNewRoleButton(),
            buildRolesContainer()
        ]);
    }
);
