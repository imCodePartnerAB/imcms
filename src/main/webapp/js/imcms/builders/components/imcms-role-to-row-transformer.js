/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.07.18
 */
define('imcms-role-to-row-transformer', ['imcms-bem-builder', 'jquery'], function (BEM, $) {

    const rolesTableBEM = new BEM({
        block: 'roles-table',
        elements: {
            'role-row': ''
        }
    });

    function getOnRoleClicked(role, roleEditor) {
        return function () {
            const $this = $(this);

            if ($this.hasClass('roles-table__role-row--active')) return;

            roleEditor.viewRole($this, role);
        }
    }

    return {
        transform: (role, roleEditor) => rolesTableBEM.makeBlockElement('role-row', $('<div>', {
            id: 'role-id-' + role.id,
            text: role.name,
            click: getOnRoleClicked(role, roleEditor)
        }))
    };
});
