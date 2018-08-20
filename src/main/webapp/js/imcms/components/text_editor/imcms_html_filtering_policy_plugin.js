/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.08.18
 */
Imcms.define(
    'imcms-html-filtering-policy-plugin',
    [
        'imcms-text-editor-toolbar-button-builder', 'imcms-html-filtering-policies', 'imcms-bem-builder', 'jquery',
        'imcms-text-editor-utils'
    ],
    function (toolbarButtonBuilder, filteringPolicies, BEM, $, textUtils) {

        var title = 'HTML content filtering policy'; // todo: localize!!!11

        var policyToName = {}; // todo: localize!!1
        policyToName[filteringPolicies.restricted] = 'Restricted';
        policyToName[filteringPolicies.relaxed] = 'Relaxed';
        policyToName[filteringPolicies.allowAll] = 'Everything is allowed';

        var policyToTitle = {};  // todo: localize!!1
        policyToTitle[filteringPolicies.restricted] = 'Illegal tags (head, script, embed, style) will be removed with content, not allowed tags (html, body, doctype) will be removed but content kept. Not allowed attributes (class, style, etc.) are removed.';
        policyToTitle[filteringPolicies.relaxed] = 'Illegal tags (head, script, embed, style) will be removed with content, not allowed tags (html, body, doctype) will be removed but content kept. All attributes are allowed.';
        policyToTitle[filteringPolicies.allowAll] = 'Everything is allowed';

        function getOnClick(editor, $btn) {
            var $textEditor = $(editor.$());

            return function (e) {
                var $target = $(e.target);

                if ($target.hasClass('settings-section__setting') && $target.parents('.filtering-policies').length) {
                    return;
                }

                var $policies = buildPoliciesSelect($textEditor);

                $btn.append($policies);

                setTimeout(function () {
                    $(document).one('click', function (e) {
                        e.preventDefault();
                        e.stopPropagation();

                        var $target = $(e.target);

                        if ($target.hasClass('settings-section__setting') && $target.parents('.filtering-policies').length) {
                            var policy = $target.attr('data-policy');

                            $textEditor.attr('data-html-filtering-policy', policy)
                                .data('htmlFilteringPolicy', policy);


                            textUtils.saveContent(editor);
                        }

                        $policies.remove();

                        return false;
                    })
                });
            }
        }

        function buildPoliciesSelect($textEditor) {

            var currentPolicy = $textEditor.attr('data-html-filtering-policy');

            var elements = Object.keys(filteringPolicies).map(function (key) {
                var policyName = filteringPolicies[key];

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
                        getOnClick(editor, $(this.$el)).apply(this, arguments)
                    }
                });
            },
            buildHtmlFilteringPolicyButton: function (editor) {
                var $btn;

                return $btn = toolbarButtonBuilder.buildButton(
                    'html-filtering-policy-button', title, function () {
                        getOnClick(editor, $btn).apply(this, arguments)
                    }
                )
            }
        }
    }
);
