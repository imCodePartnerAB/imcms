/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.08.18
 */
define(
    'imcms-html-filtering-policy-plugin',
    [
        'imcms-text-editor-toolbar-button-builder', 'imcms-html-filtering-policies', 'imcms-bem-builder', 'jquery',
        'imcms-text-editor-utils', 'imcms-i18n-texts'
    ],
    function (toolbarButtonBuilder, filteringPolicies, BEM, $, textUtils, texts) {

        texts = texts.toolTipText;

        const title = texts.htmlContent;

        const policyToName = {};
        policyToName[filteringPolicies.restricted] = texts.filterPolicy.restricted;
        policyToName[filteringPolicies.relaxed] = texts.filterPolicy.relaxed;
        policyToName[filteringPolicies.allowAll] = texts.filterPolicy.allowedAll;

        const policyToTitle = {};
        policyToTitle[filteringPolicies.restricted] = texts.filterPolicy.titleRestricted;
        policyToTitle[filteringPolicies.relaxed] = texts.filterPolicy.titleRelaxed;
        policyToTitle[filteringPolicies.allowAll] = texts.filterPolicy.titleAllowedAll;

        function getOnClick(editor, $btn) {
            const $textEditor = $(editor.$());

            return e => {
                const $target = $(e.target);

                if ($target.hasClass('settings-section__setting') && $target.parents('.filtering-policies').length) {
                    return;
                }

                const $policies = buildPoliciesSelect($textEditor);

                $btn.append($policies);

                setTimeout(() => {
                    $(document).one('click', e => {
                        e.preventDefault();
                        e.stopPropagation();

                        const $target = $(e.target);

                        if ($target.hasClass('settings-section__setting') && $target.parents('.filtering-policies').length) {
                            const policy = $target.attr('data-policy');

                            $textEditor.attr('data-html-filtering-policy', policy)
                                .data('htmlFilteringPolicy', policy);


                            textUtils.saveContent(editor);
                        }

                        $policies.remove();

                        return false;
                    });
                });
            };
        }

        function buildPoliciesSelect($textEditor) {

            const currentPolicy = $textEditor.attr('data-html-filtering-policy');

            const elements = Object.keys(filteringPolicies).map(key => {
                const policyName = filteringPolicies[key];

                return {
                    'policy': $('<div>', {
                        'class': 'settings-section__setting'
                            + ((currentPolicy === policyName) ? ' settings-section__setting--enabled' : ''),
                        text: policyToName[policyName],
                        title: policyToTitle[policyName],
                        'data-policy': policyName
                    })
                }
            });

            return new BEM({
                block: 'filtering-policies',
                elements: elements
            }).buildBlockStructure('<div>')
        }

        return {
            pluginName: '/html_filtering_policy',
            initHtmlFilteringPolicy: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'html-filtering-policy-icon',
                    tooltip: title,
                    onclick: function () {
                        getOnClick(editor, $(this.$el).parent()).apply(this, arguments)
                    }
                });
            },
            buildHtmlFilteringPolicyButton: function (editor) {
                let $btn;

                return $btn = toolbarButtonBuilder.buildButton(
                    'html-filtering-policy-button', title, function () {
                        getOnClick(editor, $btn).apply(this, arguments);
                    }
                )
            }
        }
    }
);
