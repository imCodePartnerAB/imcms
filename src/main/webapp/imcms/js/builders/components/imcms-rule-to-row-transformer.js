/**
 * @author Dmytro Zemlianskyi from Ubrainians for imCode
 * 29.10.18
 */
define('imcms-rule-to-row-transformer',
    ['imcms-bem-builder', 'jquery', 'imcms-components-builder'],
    function (BEM, $, components) {

        function getOnRuleClicked(rule, ruleEditor) {
            return function () {
                let $this = $(this);

                if ($this.hasClass('rule-row--active')) {
                    return;
                }
                ruleEditor.viewRule($this, rule);
            };
        }

        return {
            transform: (rule, ruleEditor) => {
                let roles = ruleEditor.getUserRoles();
                let users = ruleEditor.getUsers();

                let ruleRowAttributes = {
                    id: `rule-id-${rule.id}`,
                    click: getOnRuleClicked(rule, ruleEditor)
                };

                let userLogin = users.find((user) => user.id === rule.userId);
                let roleName = roles.find((role) => role.id === rule.roleId);

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
                            text: roleName ? roleName.name : ''
                        }),
                        "rule-user": $('<div>', {
                            text: userLogin ? userLogin.login : ''
                        }),
                        'rule-delete': components.controls.remove(ruleEditor.deleteRule)
                    },
                }).buildBlockStructure('<div>', ruleRowAttributes);
            }
        };
    });
