/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.08.18
 */
define(
    'imcms-html-filtering-policy-plugin',
    [
        'imcms-text-editor-toolbar-button-builder', 'imcms-html-filtering-policies', 'imcms-bem-builder', 'jquery',
        'imcms-text-editor-utils', 'imcms-i18n-texts', 'imcms-modal-window-builder', 'imcms-components-builder'
    ],
    function (toolbarButtonBuilder, filteringPolicies, BEM, $, textUtils, texts, modal, components) {

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

        const PASTE_CONTAINER_ID = 'pasted_content';

        function getOnClick(editor, $btn) {
            const $textEditor = $(editor.$());

            return e => {
                const $target = $(e.target);

                if ($target.hasClass('settings-section__setting') && $target.parents('.filtering-policies').length) {
                    return;
                }

                const $policies = buildPoliciesSelect();

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


                            const filteringPolicy = $(editor.$()).data().htmlFilteringPolicy;
                            textUtils.filterContent(editor.getContent(), filteringPolicy, textDTO => {
                                $textEditor.html(textDTO.text);
                            });
                        }

                        $policies.remove();

                        return false;
                    });
                });
            };
        }

        function buildPoliciesSelect() {
            const displayedPolicies =[
                filteringPolicies.restricted,
                filteringPolicies.relaxed,
            ];

            const elements = displayedPolicies.map(policyName => ({
                    'policy': $('<div>', {
                        'class': 'settings-section__setting',
                        text: policyToName[policyName],
                        title: policyToTitle[policyName],
                        'data-policy': policyName
                    })
            }));

            return new BEM({
                block: 'filtering-policies',
                elements: elements
            }).buildBlockStructure('<div>')
        }

        function buildPoliciesModal(node) {
            const $node = $(node);

            const $meta = $($node.children()[0]);
            $meta.remove();

            const content = $node.html();

            $node.html(`<span id="${PASTE_CONTAINER_ID}">&nbsp</span>`);

            const buttons = Object.values(filteringPolicies).map(policy => components.buttons.positiveButton({
                text: policyToName[policy],
                click: () => filterAndPasteContent(content, policy),
            }));

            modal.buildModalWindowWithButtonGroup(texts.filterPolicy.chooseFilter, buttons);
        }

        function filterAndPasteContent(content, policy) {
            const $pasteContainer = $(`#${PASTE_CONTAINER_ID}`);

            textUtils.filterContent(content, policy, textDto => {
                $pasteContainer.replaceWith(textDto.text);
            }, () => {
                $pasteContainer.replaceWith(content);
            })
        }

        return {
            pluginName: '/html_filtering_policy',
            buildPoliciesModal,
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
