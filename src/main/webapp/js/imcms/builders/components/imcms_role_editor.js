/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 25.07.18
 */
Imcms.define(
    'imcms-role-editor',
    ['imcms-bem-builder', 'imcms-components-builder', 'imcms-i18n-texts'],
    function (BEM, components, texts) {

        texts = texts.superAdmin.roles;

        var $roleNameRow;

        function buildRoleNameRow() {
            $roleNameRow = components.texts.textBox('<div>', {text: texts.roleName});
            $roleNameRow.$input.attr('disabled', 'disabled');
            return $roleNameRow;
        }

        var $container;

        return {
            buildContainer: function () {
                return $container || ($container = new BEM({
                    block: 'roles-editor',
                    elements: {
                        'role-name-row': buildRoleNameRow(),
                        'role-permissions': '',
                        'role-edit': '',
                        'role-save': '',
                        'role-cancel': '',
                        'role-delete': ''
                    }
                }).buildBlockStructure('<div>', {style: 'display: none;'}));
            },
            viewRole: function (role) {
                $roleNameRow.setValue(role.name);

                $container.css('display', 'inline-block')
            }
        }
    }
);
