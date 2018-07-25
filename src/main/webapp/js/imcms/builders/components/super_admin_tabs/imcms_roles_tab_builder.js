/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-roles-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-roles-rest-api',
        'imcms-bem-builder', 'jquery'
    ],
    function (SuperAdminTab, texts, components, rolesRestApi, BEM, $) {

        texts = texts.superAdmin.roles;

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
            // todo: implement
        }

        function buildCreateNewRoleButton() {
            return wrapInImcmsField(components.buttons.positiveButton({
                text: texts.createNewRole,
                click: onCreateNewRole
            }));
        }

        var rolesTableBEM = new BEM({
            block: 'roles-table',
            elements: {
                'role-row': ''
            }
        });

        function buildRolesContainer() {
            var $rolesContainer = rolesTableBEM.buildBlock('<div>');

            function getOnRoleClicked(role) {
                return function () {
                    // todo: implement
                }
            }

            function roleToRow(role) {
                return rolesTableBEM.buildElement('role-row', '<div>', {
                    id: 'role-id-' + role.id,
                    text: role.name,
                    click: getOnRoleClicked(role)
                })
            }

            rolesRestApi.read().success(function (roles) {
                $rolesContainer.append(roles.map(roleToRow))
            });

            return wrapInImcmsField($rolesContainer);
        }

        return new SuperAdminTab(texts.name, [
            buildTabTitle(),
            buildCreateNewRoleButton(),
            buildRolesContainer()
        ]);
    }
);
