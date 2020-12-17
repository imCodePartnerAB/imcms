/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 25.07.18
 */
define(
    'imcms-role-editor',
    [
        'imcms-bem-builder', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms-modal-window-builder',
        'imcms-roles-rest-api', 'imcms-role-to-row-transformer', 'imcms-document-editor-builder', "jquery"
    ],
    function (BEM, components, texts, modal, rolesRestAPI, roleToRow, documentEditorBuilder, $) {

        texts = texts.superAdmin.roles;

        let $roleNameRow;

        let $getPasswordByEmail;
        let $accessToAdminPages;
        let $useImagesInImageArchive;
        let $changeImagesInImageArchive;
        let $accessToDocumentEditor;

        let permissionCheckboxes$;

        let $roleViewButtons;
        let $roleEditButtons;

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
                $getPasswordByEmail = createCheckboxWithText(texts.permissions.getPasswordByEmail),
                $accessToAdminPages = createCheckboxWithText(texts.permissions.accessToAdminPages),
                $useImagesInImageArchive = createCheckboxWithText(texts.permissions.useImagesInImageArchive),
                $changeImagesInImageArchive = createCheckboxWithText(texts.permissions.changeImagesInImageArchive),
                $accessToDocumentEditor = createCheckboxWithText(texts.permissions.accessToDocumentEditor)
            ];

            return components.checkboxes.checkboxContainerField(
                '<div>', permissionCheckboxes$, {title: texts.permissions.title}
            );
        }

        function onCancelChanges($roleRowElement, role) {
            getOnDiscardChanges(() => {
                onRoleView = onRoleSimpleView;
                currentRole = role;
                $roleRow = $roleRowElement;
                prepareRoleView();
            }).call();
        }

        function onEditRole() {
            onRoleView = onCancelChanges;

            $roleViewButtons.slideUp();
            $roleEditButtons.slideDown();

            $roleNameRow.$input.removeAttr('disabled').focus();

            permissionCheckboxes$.forEach($checkbox => {
                $checkbox.$input.removeAttr('disabled');
            });
        }

        function onDeleteRole() {
            modal.buildModalWindow(texts.deleteConfirm, confirmed => {
                if (!confirmed) return;

                rolesRestAPI.remove(currentRole)
                    .done(() => {
                        $roleRow.remove();
                        currentRole = null;
                        onEditDelegate = onSimpleEdit;
                        $container.slideUp();
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.removeFailed));
            });
        }

        function buildRoleViewButtons() {
            return $roleViewButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.errorButton({
                    text: texts.deleteRole,
                    click: onDeleteRole
                }),
                buildLinkDocumentEditor(),
                components.buttons.positiveButton({
                    text: texts.editRole,
                    click: onEditRole
                }),
            ]);
        }

        function onSaveRole() {
            let name = $roleNameRow.getValue();

            if (!name) {
                $roleNameRow.$input.focus();
                return;
            }

            const saveMe = {
                id: currentRole.id,
                name: name,
                permissions: {
                    getPasswordByEmail: permissionCheckboxes$[0].isChecked(),
                    accessToAdminPages: permissionCheckboxes$[1].isChecked(),
                    useImagesInImageArchive: permissionCheckboxes$[2].isChecked(),
                    changeImagesInImageArchive: permissionCheckboxes$[3].isChecked()
                }
            };

            if (saveMe.id) {
                rolesRestAPI.update(saveMe)
                    .done(savedRole => {
                        // todo: maybe there is better way to reassign fields' values, not object itself
                        currentRole.id = savedRole.id;
                        $roleRow.text(currentRole.name = savedRole.name);
                        currentRole.permissions.getPasswordByEmail = savedRole.permissions.getPasswordByEmail;
                        currentRole.permissions.accessToAdminPages = savedRole.permissions.accessToAdminPages;
                        currentRole.permissions.useImagesInImageArchive = savedRole.permissions.useImagesInImageArchive;
                        currentRole.permissions.changeImagesInImageArchive = savedRole.permissions.changeImagesInImageArchive;

                        onRoleView = onRoleSimpleView;
                        prepareRoleView();
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.updateFailed));
            } else {
                rolesRestAPI.create(saveMe)
                    .done(role => {
                        $roleRow = roleToRow.transform((currentRole = role), roleEditor);
                        $container.parent().find('.roles-table').append($roleRow);

                        onRoleView = onRoleSimpleView;
                        prepareRoleView();
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.createFailed));
            }
        }

        function getOnDiscardChanges(onConfirm) {
            return () => {
                modal.buildModalWindow(texts.discardChangesMessage, confirmed => {
                    if (!confirmed) return;
                    onConfirm.call();
                });
            };
        }

        function buildRoleEditButtons() {
            return $roleEditButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.negativeButton({
                    text: texts.cancel,
                    click: getOnDiscardChanges(() => {
                        onRoleView = onRoleSimpleView;

                        if (currentRole.id) {
                            prepareRoleView();

                        } else {
                            currentRole = null;
                            onEditDelegate = onSimpleEdit;
                            $container.slideUp();
                        }
                    })
                }),
                components.buttons.saveButton({
                    text: texts.saveChanges,
                    click: onSaveRole
                }),
            ], {
                style: 'display: none;'
            });
        }

        function prepareRoleView() {
            onEditDelegate = onSimpleEdit;

            $roleRow.parent()
                .find('.roles-table__role-row--active')
                .removeClass('roles-table__role-row--active');

            $roleRow.addClass('roles-table__role-row--active');

            $roleEditButtons.slideUp('fast');
            $roleViewButtons.slideDown('fast');

            $roleNameRow.$input.attr('disabled', 'disabled');
            $roleNameRow.setValue(currentRole.name);

            const permissions = [
                currentRole.permissions.getPasswordByEmail,
                currentRole.permissions.accessToAdminPages,
                currentRole.permissions.useImagesInImageArchive,
                currentRole.permissions.changeImagesInImageArchive
            ];

            permissionCheckboxes$.forEach(($checkbox, i) => {
                $checkbox.$input.attr('disabled', 'disabled');
                $checkbox.setChecked(permissions[i]);
            });

            $container.css('display', 'inline-block');
        }

        function onRoleSimpleView($roleRowElement, role) {
            if (currentRole && currentRole.id === role.id) return;
            currentRole = role;
            $roleRow = $roleRowElement;

            prepareRoleView();
        }

        var $container;
        var currentRole;
        var $roleRow;
        var onRoleView = onRoleSimpleView;

        function buildLinkDocumentEditor() {

            if (currentRole === null) return;


            return components.buttons.positive('<div>', {
                text: texts.documentEditor,
                title: texts.documentEditor,
                click: function () {
                    $('.imcms-info-page').css({'display': 'none'});
                    documentEditorBuilder.build(currentRole.id);
                }
            })
        }

        function buildContainer() {
            return $container || ($container = new BEM({
                block: 'roles-editor',
                elements: {
                    'role-name-row': buildRoleNameRow(),
                    'role-permissions': buildRolePermissions(),
                    'role-view-buttons': buildRoleViewButtons(),
                    'role-edit-buttons': buildRoleEditButtons(),
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'}));
        }

        function viewRole($roleRow, role) {
            $container.slideDown();
            onRoleView($roleRow, role);
        }

        function onSimpleEdit($roleRow, role) {
            viewRole($roleRow, role);
            onEditRole();
        }

        var onEditDelegate = onSimpleEdit;

        function editRole($roleRow, role) {
            onEditDelegate($roleRow, role);
            onEditDelegate = () => {
            };
        }

        var roleEditor = {
            buildContainer: buildContainer,
            viewRole: viewRole,
            editRole: editRole
        };

        return roleEditor;
    }
);
