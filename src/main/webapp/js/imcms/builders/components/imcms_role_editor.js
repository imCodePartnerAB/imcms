/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 25.07.18
 */
Imcms.define(
    'imcms-role-editor',
    ['imcms-bem-builder', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms-modal-window-builder', 'imcms-roles-rest-api'],
    function (BEM, components, texts, confirmationBuilder, rolesRestAPI) {

        texts = texts.superAdmin.roles;

        var $roleNameRow;

        var $getPasswordByEmail;
        var $accessToAdminPages;
        var $useImagesInImageArchive;
        var $changeImagesInImageArchive;

        var permissionCheckboxes$;

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

            permissionCheckboxes$ = [
                $getPasswordByEmail = createCheckboxWithText('Get password by email'),
                $accessToAdminPages = createCheckboxWithText('Access to admin pages'),
                $useImagesInImageArchive = createCheckboxWithText('Use images in image archive'),
                $changeImagesInImageArchive = createCheckboxWithText('Change images in image archive')
            ];

            return components.checkboxes.checkboxContainerField(
                '<div>', permissionCheckboxes$, {title: 'Role permissions'}
            );
        }

        function slideToggle(elements$) {
            elements$.forEach(function ($element) {
                $element.slideToggle('fast');
            })
        }

        function onEditRole() {
            slideToggle([
                $roleViewButtons,
                $roleEditButtons
            ]);

            $roleNameRow.$input.removeAttr('disabled').focus();

            permissionCheckboxes$.forEach(function ($checkbox) {
                $checkbox.$input.removeAttr('disabled');
            })
        }

        function onDeleteRole() {
            confirmationBuilder.buildModalWindow('Do you really want to delete this role?', function (confirmed) {
                if (!confirmed) return;

                rolesRestAPI.remove().success(function () {

                })
            });
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
            confirmationBuilder.buildModalWindow('Discard changes?', function (confirmed) {
                if (!confirmed) return;

                slideToggle([$roleEditButtons, $roleViewButtons]);
                $roleNameRow.$input.attr('disabled', 'disabled');
                viewRole(currentRole);
            });
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
        var currentRole;

        function buildContainer() {
            return $container || ($container = new BEM({
                block: 'roles-editor',
                elements: {
                    'role-name-row': buildRoleNameRow(),
                    'role-permissions': buildRolePermissions(),
                    'role-view-buttons': buildRoleViewButtons(),
                    'role-edit-buttons': buildRoleEditButtons()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'}));
        }

        function viewRole(role) {
            currentRole = role;

            $roleNameRow.setValue(role.name);

            $getPasswordByEmail.setChecked(role.permissions.getPasswordByEmail);
            $accessToAdminPages.setChecked(role.permissions.accessToAdminPages);
            $useImagesInImageArchive.setChecked(role.permissions.useImagesInImageArchive);
            $changeImagesInImageArchive.setChecked(role.permissions.changeImagesInImageArchive);

            $container.css('display', 'inline-block')
        }

        return {
            buildContainer: buildContainer,
            viewRole: viewRole
        }
    }
);
