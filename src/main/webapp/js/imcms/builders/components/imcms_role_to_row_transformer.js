/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.07.18
 */
Imcms.define('imcms-role-to-row-transformer', ['imcms-bem-builder', 'jquery'], function (BEM, $) {

    var rolesTableBEM = new BEM({
        block: 'roles-table',
        elements: {
            'role-row': ''
        }
    });

    function getOnRoleClicked(role, roleEditor) {
        return function () {
            var $this = $(this);

            if ($this.hasClass('roles-table__role-row--active')) return;

            roleEditor.viewRole($this, role);
        }
    }

    return {
        transform: function (role, roleEditor) {
            return rolesTableBEM.makeBlockElement('role-row', $('<div>', {
                id: 'role-id-' + role.id,
                text: role.name,
                click: getOnRoleClicked(role, roleEditor)
            }))
        }
    };
});
