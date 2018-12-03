/**
 * @author Dmytro Zemlianslyi from Ubrainians for imCode
 * 29.10.18
 */
define('imcms-rule-to-row-transformer',
    ['imcms-bem-builder', 'jquery', 'imcms-components-builder'],
    function (BEM, $, components) {

        function getOnRuleClicked(rule, ruleEditor) {
            return function () {
                var $this = $(this);

                if ($this.hasClass('rule-row--active')) {
                    return;
                }
                ruleEditor.viewRule($this, rule);
            };
        }

        return {
            transform: function (rule, ruleEditor) {

                let ruleRowAttributes = {
                    id: `rule-id-${rule.id}`,
                    click: getOnRuleClicked(rule, ruleEditor)
                };

                return new BEM({
                    block: 'rule-row',
                    elements: {
                        "rule-enabled": components.checkboxes.imcmsCheckbox("<div>", {
                            checked: rule.enabled,
                            disabled: true
                        }),
                        "rule-restricted": components.checkboxes.imcmsCheckbox("<div>", {
                            checked: rule.restricted,
                            disabled: true
                        }),
                        "rule-ip-range": $('<div>', {
                            text: rule.ipRange
                        }),
                        "rule-role": $('<div>', {
                            text: rule.roleId
                        }),
                        "rule-user": $('<div>', {
                            text: rule.userId
                        }),
                        'rule-delete': components.controls.remove(ruleEditor.deleteRule)
                    },
                }).buildBlockStructure('<div>', ruleRowAttributes);
            }
        };
    });
