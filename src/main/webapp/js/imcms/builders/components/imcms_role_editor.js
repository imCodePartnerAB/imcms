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

        var $roleViewButtons;
        var $roleEditButtons;

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

        function onEditRole() {
            $roleViewButtons.slideToggle('fast');
            $roleEditButtons.slideToggle('fast');
        }

        function onDeleteRole() {
            // todo: implement
        }

        function buildRoleViewButtons() {
            return $roleViewButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.positiveButton({
                    text: 'Edit role',
                    click: onEditRole
                }),
                components.buttons.negativeButton({
                    text: 'Delete role',
                    click: onDeleteRole
                })
            ]);
        }

        function onSaveRole() {
            // todo: implement
        }

        function onCancelChanges() {
            $roleEditButtons.slideToggle('fast');
            $roleViewButtons.slideToggle('fast');
        }

        function buildRoleEditButtons() {
            return $roleEditButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.saveButton({
                    text: 'Save changes',
                    click: onSaveRole
                }),
                components.buttons.negativeButton({
                    text: 'Cancel',
                    click: onCancelChanges
                })
            ], {
                style: 'display: none;'
            });
        }

        var $container;

        return {
            buildContainer: function () {
                return $container || ($container = new BEM({
                    block: 'roles-editor',
                    elements: {
                        'role-name-row': buildRoleNameRow(),
                        'role-permissions': buildRolePermissions(),
                        'role-view-buttons': buildRoleViewButtons(),
                        'role-edit-buttons': buildRoleEditButtons()
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
