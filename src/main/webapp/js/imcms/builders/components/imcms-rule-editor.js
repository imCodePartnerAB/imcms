/**
 * @author Dmytro Zemlianslyi from Ubrainians for imCode
 * 29.10.18
 */
define(
    'imcms-rule-editor',
    [
        'imcms-bem-builder', 'jquery', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms-modal-window-builder',
        'imcms-ip-rules-rest-api', 'imcms-roles-rest-api', 'imcms-users-rest-api', 'imcms-rule-to-row-transformer'
    ],
    function (BEM, $, components, texts, confirmationBuilder, rulesAPI, rolesRestApi, usersRestApi, ruleToRow) {

        const ruleEditor = {
            buildContainer: buildContainer,
            viewRule: viewRule,
            editRule: editRule,
            deleteRule: onDeleteRule,
            getUserRoles: getRoles,
            getUsers: getUsers
        };
        texts = texts.superAdmin.ipAccess;

        let receivedRoles = [];
        let receivedUsers = [];

        let onEditDelegate;
        let onRuleView;
        let $ruleRow;
        let currentRule;
        let $container;

        let $ruleRange1Row;
        let $ruleRange2Row;
        let $ruleRange1Error;
        let $ruleRange2Error;

        let $enableRuleCheckbox;
        let $restrictRuleCheckbox;
        let $userSelect;
        let $userRoleSelect;

        let $ruleEditButtons;

        initRequiredData();

        function initRequiredData() {
            rolesRestApi.read().success((roles) => {
                receivedRoles = roles;

                //Init select with data
                let rolesDataMapped = receivedRoles.map((role) => ({
                    text: role.name,
                    "data-value": role.id
                }));
                components.selects.addOptionsToSelect(rolesDataMapped, $userRoleSelect);
            });

            usersRestApi.read().success((users) => {
                receivedUsers = users;

                //Init select with data
                let usersDataMapped = receivedUsers.map((user) => ({
                    text: user.login,
                    "data-value": user.id
                }));

                components.selects.addOptionsToSelect(usersDataMapped, $userSelect);
            });
        }

        function buildRuleRange1Row() {
            $ruleRange1Row = components.texts.textBox('<div>', {});
            $ruleRange1Row.$input
                .blur(function (event) {
                    event.target.checkValidity();
                })
                .attr('pattern', '^$|((^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)|(^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$))');
            return $ruleRange1Row;

        }

        function buildRuleRange2Row() {
            $ruleRange2Row = components.texts.textBox('<div>', {});
            $ruleRange2Row.$input
                .blur(function (event) {
                    event.target.checkValidity();
                })
                .attr('pattern', '^$|((^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)|(^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$))');
            return $ruleRange2Row;
        }

        function buildRuleRangeRow() {
            return new BEM({
                block: 'ip-range',
                title: texts.fields.ipRange,
                elements: {
                    'ip-range1': buildRuleRange1Row(),
                    'ip-range-divider': $('<div>', {
                        text: '-'
                    }),
                    'ip-range2': buildRuleRange2Row()
                }
            }).buildBlockStructure("<div>");
        }

        function buildRuleRangeErrorsRow() {
            $ruleRange1Error = components.texts.errorText("<div>", texts.wrongIpError, {style: 'display: none;'});
            $ruleRange2Error = components.texts.errorText("<div>", texts.wrongIpError, {style: 'display: none;'});

            return new BEM({
                block: 'ip-range',
                title: texts.fields.ipRange,
                elements: {
                    'ip-range1-error': $ruleRange1Error,
                    'ip-range2-error': $ruleRange2Error
                }
            }).buildBlockStructure("<div>");
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
            getOnDiscardChanges(() => {
                onRuleView = onRuleSimpleView;
                currentRule = rule;
                $ruleRow = $ruleRowElement;
                prepareRuleView();
            }).call();
        }

        function onEditRule() {
            onRuleView = onCancelChanges;
            $ruleEditButtons.slideDown();
        }

        function onDeleteRule() {
            confirmationBuilder.buildModalWindow(texts.deleteConfirm, confirmed => {
                if (!confirmed) {
                    return;
                }

                rulesAPI.remove(currentRule).success(() => {
                    $ruleRow.remove();
                    currentRule = null;
                    onEditDelegate = onSimpleEdit;
                    $container.slideUp();
                });
            });
        }

        function onSaveRule() {

            if ($ruleRange1Row.$input[0].validity.valid) {
                $ruleRange1Error.slideUp();
            } else {
                $ruleRange1Error.slideDown();
            }
            if ($ruleRange2Row.$input[0].validity.valid) {
                $ruleRange2Error.slideUp();
            } else {
                $ruleRange2Error.slideDown();
            }

            if (!($ruleRange1Row.$input[0].validity.valid && $ruleRange2Row.$input[0].validity.valid)) {
                return;
            } else {
                let ip1 = $ruleRange1Row.getValue();
                let ip2 = $ruleRange2Row.getValue();
                const saveMe = {
                    id: currentRule.id,
                    enabled: $enableRuleCheckbox.isChecked(),
                    restricted: $restrictRuleCheckbox.isChecked(),
                    ipRange: ip2 ? `${ip1}-${ip2}` : ip1,
                    roleId: $userRoleSelect.getSelectedValue(),
                    userId: $userSelect.getSelectedValue()
                };
                if (saveMe.id) {
                    rulesAPI.replace(saveMe).success(savedRule => {
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
                    rulesAPI.create(saveMe).success(rule => {
                        $ruleRow = ruleToRow.transform((currentRule = rule), ruleEditor);
                        $container.parent().find('.rules-table').append($ruleRow);

                        onRuleView = onRuleSimpleView;
                        prepareRuleView();
                    });
                }
            }
        }

        function getOnDiscardChanges(onConfirm) {
            return () => {
                confirmationBuilder.buildModalWindow(texts.discardChangesMessage, confirmed => {
                    if (!confirmed) {
                        return;
                    }
                    onConfirm.call();
                });
            };
        }

        function buildRuleEditButtons() {
            $ruleEditButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.saveButton({
                    text: texts.saveChanges,
                    click: onSaveRule
                }),
                components.buttons.negativeButton({
                    text: texts.cancel,
                    click: getOnDiscardChanges(() => {
                        onRuleView = onRuleSimpleView;
                        if (currentRule.id) {
                            prepareRuleView();
                            $container.slideUp();
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

            return $ruleEditButtons;
        }

        function prepareRuleView() {
            onEditDelegate = onSimpleEdit;

            $ruleRow.parent()
                .find('.rules-table__rule-row--active')
                .removeClass('rules-table__rule-row--active');

            $ruleRow.addClass('rules-table__rule-row--active');

            $ruleEditButtons.slideDown();

            let ipRange = currentRule.ipRange.split('-');
            $ruleRange1Row.setValue(ipRange[0]);
            $ruleRange2Row.setValue(ipRange[1]);
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
                    'rule-range-row': buildRuleRangeRow(),
                    'rule-range-error-row': buildRuleRangeErrorsRow(),
                    'rule-user-row': buildRuleUserRow(),
                    'rule-role-row': buildRuleRoleRow(),
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

        return ruleEditor;
    }
);
