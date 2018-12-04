/**
 * @author Dmytro Zemlianslyi from Ubrainians for imCode
 * 29.10.18
 */
define(
    'imcms-rule-editor',
    [
        'imcms-bem-builder', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms-modal-window-builder',
        'imcms-ip-rules-rest-api', 'imcms-roles-rest-api', 'imcms-users-rest-api', 'imcms-rule-to-row-transformer'
    ],
    function (BEM, components, texts, confirmationBuilder, rulesAPI, rolesRestApi, usersRestApi, ruleToRow) {

        texts = texts.superAdmin.ipAccess;

        let receivedRoles = [];
        let receivedUsers = [];

        let onEditDelegate;
        let onRuleView;
        let $ruleRow;
        let currentRule;
        let $container;

        let $ruleRangeRow;
        let $enableRuleCheckbox;
        let $restrictRuleCheckbox;
        let $userSelect;
        let $userRoleSelect;

        let $ruleViewButtons;
        let $ruleEditButtons;

        initRequiredData();

        function initRequiredData() {
            rolesRestApi.read().success(function (roles) {
                receivedRoles = roles;

                //Init select with data
                let rolesDataMapped = receivedRoles.map(function (role) {
                    return {
                        text: role.name,
                        "data-value": role.id
                    };
                });
                components.selects.addOptionsToSelect(rolesDataMapped, $userRoleSelect);
            });

            usersRestApi.read().success(function (users) {
                receivedUsers = users;

                //Init select with data
                let usersDataMapped = receivedUsers.map(function (user) {
                    return {
                        text: user.login,
                        "data-value": user.id
                    };
                });

                components.selects.addOptionsToSelect(usersDataMapped, $userSelect);
            });
        }

        function buildRuleRangeRow() {
            $ruleRangeRow = components.texts.textBox('<div>',
                {
                    text: texts.fields.ipRange,
                    pattern: '((^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)|(^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$))'
                });
            $ruleRangeRow.$input.attr('disabled', 'disabled');
            return $ruleRangeRow;
        }

        function buildRuleModifiers() {

            function createCheckboxWithText(text) {
                return components.checkboxes.imcmsCheckbox("<div>", {
                    text: text
                });
            }

            let modifierCheckboxes$ = [
                $enableRuleCheckbox = createCheckboxWithText(texts.fields.enabled),
                $restrictRuleCheckbox = createCheckboxWithText(texts.fields.restricted)
            ];

            return components.checkboxes.checkboxContainerField(
                '<div>', modifierCheckboxes$, {}
            );
        }


        function buildRuleUserRow() {
            $userSelect = components.selects.imcmsSelect('<div>', {
                id: 'users',
                name: 'users',
                text: texts.fields.user,
                emptySelect: true
            });

            return $userSelect;
        }

        function buildRuleRoleRow() {
            $userRoleSelect = components.selects.imcmsSelect('<div>', {
                id: 'users-role',
                name: 'users-role',
                text: texts.fields.role,
                emptySelect: true
            });

            return $userRoleSelect;
        }

        function onCancelChanges($ruleRowElement, rule) {
            getOnDiscardChanges(function () {
                onRuleView = onRuleSimpleView;
                currentRule = rule;
                $ruleRow = $ruleRowElement;
                prepareRuleView();
            }).call();
        }

        function onEditRule() {
            onRuleView = onCancelChanges;

            $ruleViewButtons.slideUp();
            $ruleEditButtons.slideDown();

            $ruleRangeRow.$input.removeAttr('disabled').focus();
        }

        function onDeleteRule() {
            confirmationBuilder.buildModalWindow(texts.deleteConfirm, function (confirmed) {
                if (!confirmed) {
                    return;
                }

                rulesAPI.remove(currentRule).success(function () {
                    $ruleRow.remove();
                    currentRule = null;
                    onEditDelegate = onSimpleEdit;
                    $container.slideUp();
                });
            });
        }

        function buildRuleViewButtons() {
            return $ruleViewButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.positiveButton({
                    text: texts.editRule,
                    click: onEditRule
                }),
                components.buttons.negativeButton({
                    text: texts.deleteRule,
                    click: onDeleteRule
                })
            ]);
        }

        function onSaveRule() {
            let name = $ruleRangeRow.getValue();

            if (!name) {
                $ruleRangeRow.$input.focus();
                return;
            }

            const saveMe = {
                id: currentRule.id,
                enabled: $enableRuleCheckbox.isChecked(),
                restricted: $restrictRuleCheckbox.isChecked(),
                ipRange: $ruleRangeRow.getValue(),
                roleId: $userRoleSelect.getSelectedValue(),
                userId: $userSelect.getSelectedValue()
            };

            if (saveMe.id) {
                rulesAPI.replace(saveMe).success(function (savedRule) {
                    currentRule = savedRule;
                    $ruleRow.find('.rule-row__rule-enabled > :input').attr("checked", currentRule.enabled);
                    $ruleRow.find('.rule-row__rule-restricted > :input').attr("checked", currentRule.restricted);
                    $ruleRow.find('.rule-row__rule-ip-range').text(currentRule.ipRange);
                    $ruleRow.find('.rule-row__rule-role').text(receivedRoles[currentRule.roleId].name);
                    $ruleRow.find('.rule-row__rule-user').text(receivedUsers[currentRule.userId].login);

                    onRuleView = onRuleSimpleView;
                    prepareRuleView();
                });
            } else {
                rulesAPI.create(saveMe).success(function (rule) {
                    $ruleRow = ruleToRow.transform((currentRule = rule), ruleEditor);
                    $container.parent().find('.rules-table').append($ruleRow);

                    onRuleView = onRuleSimpleView;
                    prepareRuleView();
                });
            }
        }

        function getOnDiscardChanges(onConfirm) {
            return function () {
                confirmationBuilder.buildModalWindow(texts.discardChangesMessage, function (confirmed) {
                    if (!confirmed) {
                        return;
                    }
                    onConfirm.call();
                });
            };
        }

        function buildRuleEditButtons() {
            return $ruleEditButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.saveButton({
                    text: texts.saveChanges,
                    click: onSaveRule
                }),
                components.buttons.negativeButton({
                    text: texts.cancel,
                    click: getOnDiscardChanges(function () {
                        onRuleView = onRuleSimpleView;

                        if (currentRule.id) {
                            prepareRuleView();

                        } else {
                            currentRule = null;
                            onEditDelegate = onSimpleEdit;
                            $container.slideUp();
                        }
                    })
                })
            ], {
                style: 'display: none;'
            });
        }

        function prepareRuleView() {
            onEditDelegate = onSimpleEdit;

            $ruleRow.parent()
                .find('.rules-table__rule-row--active')
                .removeClass('rules-table__rule-row--active');

            $ruleRow.addClass('rules-table__rule-row--active');

            $ruleEditButtons.slideUp('fast');
            $ruleViewButtons.slideDown('fast');

            $ruleRangeRow.setValue(currentRule.ipRange);
            $enableRuleCheckbox.setChecked(currentRule.enabled);
            $restrictRuleCheckbox.setChecked(currentRule.restricted);
            $userSelect.selectValue(currentRule.userId);
            $userRoleSelect.selectValue(currentRule.roleId);

            $container.css('display', 'inline-block');
        }

        function onRuleSimpleView($ruleRowElement, rule) {
            if (currentRule && currentRule.id === rule.id) {
                return;
            }
            currentRule = rule;
            $ruleRow = $ruleRowElement;

            prepareRuleView();
        }

        onRuleView = onRuleSimpleView;

        function buildContainer() {
            return $container || ($container = new BEM({
                block: 'rules-editor',
                elements: {
                    'rule-modifiers': buildRuleModifiers(),
                    'rule-name-row': buildRuleRangeRow(),
                    'rule-user-row': buildRuleUserRow(),
                    'rule-role-row': buildRuleRoleRow(),
                    'rule-view-buttons': buildRuleViewButtons(),
                    'rule-edit-buttons': buildRuleEditButtons()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'}));
        }

        function viewRule($ruleRow, rule) {
            $container.slideDown();
            onRuleView($ruleRow, rule);
        }

        function onSimpleEdit($ruleRow, rule) {
            viewRule($ruleRow, rule);
            onEditRule();
        }

        onEditDelegate = onSimpleEdit;

        function editRule($ruleRow, rule) {
            onEditDelegate($ruleRow, rule);
            onEditDelegate = () => {
            };
        }

        function getRoles() {
            return receivedRoles;
        }

        function getUsers() {
            return receivedUsers;
        }

        var ruleEditor = {
            buildContainer: buildContainer,
            viewRule: viewRule,
            editRule: editRule,
            deleteRule: onDeleteRule,
            getUserRoles: getRoles,
            getUsers: getUsers
        };

        return ruleEditor;
    }
);
