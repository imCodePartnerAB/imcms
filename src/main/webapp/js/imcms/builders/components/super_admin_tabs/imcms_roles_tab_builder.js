/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-roles-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-roles-rest-api',
        'imcms-bem-builder', 'imcms-role-editor', 'jquery'
    ],
    function (SuperAdminTab, texts, components, rolesRestApi, BEM, roleEditor, $) {

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

        function buildRolesContainer() {
            var rolesTableBEM = new BEM({
                block: 'roles-table',
                elements: {
                    'role-row': ''
                }
            });

            var $rolesContainer = rolesTableBEM.buildBlock('<div>');
            var $roleViewEditContainer = roleEditor.buildContainer();

            function getOnRoleClicked(role) {
                return function () {
                    var $this = $(this);

                    if ($this.hasClass('roles-table__role-row--active')) return;

                    roleEditor.viewRole.call($this, role);
                }
            }

            function roleToRow(role) {
                return rolesTableBEM.makeBlockElement('role-row', $('<div>', {
                    id: 'role-id-' + role.id,
                    text: role.name,
                    click: getOnRoleClicked(role)
                }))
            }

            rolesRestApi.read().success(function (roles) {
                $rolesContainer.append(roles.map(roleToRow))
            });

            return wrapInImcmsField([$rolesContainer, $roleViewEditContainer]);
        }

        return new SuperAdminTab(texts.name, [
            buildTabTitle(),
            buildCreateNewRoleButton(),
            buildRolesContainer()
        ]);
    }
);
