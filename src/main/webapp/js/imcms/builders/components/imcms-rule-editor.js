/**
 * @author Dmytro Zemlianslyi from Ubrainians for imCode
 * 29.10.18
 */
define(
    'imcms-rule-editor',
    [
        'imcms-bem-builder', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms-modal-window-builder',
        'imcms-ip-rules-rest-api', 'imcms-rule-to-row-transformer'
    ],
    function (BEM, components, texts, confirmationBuilder, rulesAPI, ruleToRow) {

        texts = texts.superAdmin.ipAccess;

        var $ruleNameRow;

        var $ruleViewButtons;
        var $ruleEditButtons;

        function buildRuleNameRow() {
            $ruleNameRow = components.texts.textBox('<div>', {text: texts.ruleName});
            $ruleNameRow.$input.attr('disabled', 'disabled');
            return $ruleNameRow;
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

            $ruleNameRow.$input.removeAttr('disabled').focus();
        }

        function onDeleteRule() {
            confirmationBuilder.buildModalWindow(texts.deleteConfirm, function (confirmed) {
                if (!confirmed) return;

                rulesAPI.remove(currentRule).success(function () {
                    $ruleRow.remove();
                    currentRule = null;
                    onEditDelegate = onSimpleEdit;
                    $container.slideUp();
                })
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
            var name = $ruleNameRow.getValue();

            if (!name) {
                $ruleNameRow.$input.focus();
                return;
            }

            var saveMe = {
                id: currentRule.id,
                isEnabled: false,
                isRestricted: false,
                ipRange: null,
                roleId: null,
                userId: null
            };

            if (saveMe.id) {
                rulesAPI.update(saveMe).success(function (savedRule) {
                    // todo: maybe there is better way to reassign fields' values, not object itself
                    currentRule.id = savedRule.id;
                    $ruleRow.text(currentRule.name = savedRule.name);
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
                    if (!confirmed) return;
                    onConfirm.call();
                });
            }
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

            $ruleNameRow.$input.attr('disabled', 'disabled');
            $ruleNameRow.setValue(currentRule.name);

            $container.css('display', 'inline-block');
        }

        function onRuleSimpleView($ruleRowElement, rule) {
            if (currentRule && currentRule.id === rule.id) return;
            currentRule = rule;
            $ruleRow = $ruleRowElement;

            prepareRuleView();
        }

        var $container;
        var currentRule;
        var $ruleRow;
        var onRuleView = onRuleSimpleView;

        function buildContainer() {
            return $container || ($container = new BEM({
                block: 'rules-editor',
                elements: {
                    'rule-name-row': buildRuleNameRow(),
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

        var onEditDelegate = onSimpleEdit;

        function editRule($ruleRow, rule) {
            onEditDelegate($ruleRow, rule);
            onEditDelegate = function () {
            }
        }

        var ruleEditor = {
            buildContainer: buildContainer,
            viewRule: viewRule,
            editRule: editRule
        };

        return ruleEditor;
    }
);
