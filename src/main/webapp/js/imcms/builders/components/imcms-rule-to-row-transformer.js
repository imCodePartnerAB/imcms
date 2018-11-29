/**
 * @author Dmytro Zemlianslyi from Ubrainians for imCode
 * 29.10.18
 */
define('imcms-rule-to-row-transformer', ['imcms-bem-builder', 'jquery'], function (BEM, $) {

    var rulesTableBEM = new BEM({
        block: 'rules-table',
        elements: {
            'rule-row': ''
        }
    });

    function getOnRuleClicked(rule, ruleEditor) {
        return function () {
            var $this = $(this);

            if ($this.hasClass('rules-table__rule-row--active')) return;

            ruleEditor.viewRule($this, rule);
        }
    }

    return {
        transform: function (rule, ruleEditor) {
            return rulesTableBEM.makeBlockElement('rule-row', $('<div>', {
                id: 'rule-id-' + rule.id,
                text: rule.ipRange,
                click: getOnRuleClicked(rule, ruleEditor)
            }))
        }
    };
});
