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
        var $getPasswordByEmail;
        var $accessToAdminPages;
        var $useImagesInImageArchive;
        var $changeImagesInImageArchive;

        function buildRoleNameRow() {
            $roleNameRow = components.texts.textBox('<div>', {text: texts.roleName});
            $roleNameRow.$input.attr('disabled', 'disabled');
            return $roleNameRow;
        }

        function buildRolePermissions() {
            function createCheckboxWithText(text) {
                return components.checkboxes.imcmsCheckbox("<div>", {
                    disabled: 'disabled',
                    text: text
                });
            }

            var permissionCheckboxes$ = [
                $getPasswordByEmail = createCheckboxWithText('Get password by email'),
                $accessToAdminPages = createCheckboxWithText('Access to admin pages'),
                $useImagesInImageArchive = createCheckboxWithText('Use images in image archive'),
                $changeImagesInImageArchive = createCheckboxWithText('Change images in image archive')
            ];
            return components.checkboxes.checkboxContainerField(
                '<div>', permissionCheckboxes$, {title: 'Role permissions'}
            );
        }

        var $container;

        return {
            buildContainer: function () {
                return $container || ($container = new BEM({
                    block: 'roles-editor',
                    elements: {
                        'role-name-row': buildRoleNameRow(),
                        'role-permissions': buildRolePermissions(),
                        'role-edit': '',
                        'role-save': '',
                        'role-cancel': '',
                        'role-delete': ''
                    }
                }).buildBlockStructure('<div>', {style: 'display: none;'}));
            },
            viewRole: function (role) {
                $roleNameRow.setValue(role.name);

                $getPasswordByEmail.setChecked(role.permissions.getPasswordByEmail);
                $accessToAdminPages.setChecked(role.permissions.accessToAdminPages);
                $useImagesInImageArchive.setChecked(role.permissions.useImagesInImageArchive);
                $changeImagesInImageArchive.setChecked(role.permissions.changeImagesInImageArchive);

                $container.css('display', 'inline-block')
            }
        }
    }
);
