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

        const policyToName = {}; // todo: localize!!1
        policyToName[filteringPolicies.restricted] = 'Restricted';
        policyToName[filteringPolicies.relaxed] = 'Relaxed';
        policyToName[filteringPolicies.allowAll] = 'Everything is allowed';

        const policyToTitle = {};  // todo: localize!!1
        policyToTitle[filteringPolicies.restricted] = 'Illegal tags (head, script, embed, style) will be removed with content, not allowed tags (html, body, doctype) will be removed but content kept. Not allowed attributes (class, style, etc.) are removed.';
        policyToTitle[filteringPolicies.relaxed] = 'Illegal tags (head, script, embed, style) will be removed with content, not allowed tags (html, body, doctype) will be removed but content kept. All attributes are allowed.';
        policyToTitle[filteringPolicies.allowAll] = 'Everything is allowed';

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
