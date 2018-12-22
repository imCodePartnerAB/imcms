/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-ip-access-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-ip-rules-rest-api',
        'imcms-rule-editor', 'imcms-rule-to-row-transformer', 'imcms', 'imcms-bem-builder', 'jquery', 'imcms-field-wrapper'
    ],
    function (SuperAdminTab, texts, components, rulesApi, ruleEditor, ruleToRow, imcms, BEM, $, fieldWrapper) {

        texts = texts.superAdmin.ipAccess;

        const ruleLoader = {
            rules: false,
            callbacks: [],
            whenRulesLoaded: function (callback) {
                this.rules ? callback(this.rules) : this.callbacks.push(callback);
            },
            runCallbacks: function (rules) {
                this.rules = rules;

                this.callbacks.forEach((callback) => {
                    callback(rules);
                })
            }
        };

        rulesApi.read().success(rules => {
            ruleLoader.runCallbacks(rules);
        });

        let $rulesContainer;

        function buildTabTitle() {
            return fieldWrapper.wrap(components.texts.titleText('<div>', texts.title));
        }

        function onCreateNewRule() {
            $rulesContainer.find('.rules-table__rule-row--active')
                .removeClass('rules-table__rule-row--active');

            ruleEditor.editRule($('<div>'), {
                id: null,
                isEnabled: false,
                isRestricted: false,
                ipRange: '',
                roleId: null,
                userId: null,
            });
        }

        function buildCreateNewRuleButton() {
            return fieldWrapper.wrap(components.buttons.positiveButton({
                text: texts.createNewRule,
                click: onCreateNewRule
            }));
        }

        function buildRulesContainer() {
            $rulesContainer = $('<div>', {
                'class': 'rules-table'
            });

            ruleLoader.whenRulesLoaded(rules => {
                $rulesContainer.append(prepareTitleRow());
                $rulesContainer.append(rules.map(rule => ruleToRow.transform(rule, ruleEditor)));
            });

            return fieldWrapper.wrap([ruleEditor.buildContainer(), $rulesContainer]);
        }


        function prepareTitleRow() {
            var $titleRow = new BEM({
                block: 'rule-title-row',
                elements: {
                    'rule-enabled': $('<div>', {text: texts.fields.enabled}),
                    'rule-restricted': $('<div>', {text: texts.fields.restricted}),
                    'rule-ip-range': $('<div>', {text: texts.fields.ipRange}),
                    'rule-role': $('<div>', {text: texts.fields.role}),
                    'rule-user': $('<div>', {text: texts.fields.user}),
                    'rule-actions': $('<div>', {})
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-title'
            });
            return $titleRow;
        }

        function buildTitleRow() {
            let $titleRow = new BEM({
                block: 'title-profile-row',
                elements: {
                    'name': $('<div>', {text: texts.titleTextName}),
                    'doc-name': $('<div>', {text: texts.titleTextDocName})
                }
            }).buildBlockStructure('<div>', {
                'class': 'table-title'
            });
            return $titleRow;
        }

        return new SuperAdminTab(texts.name, [
            buildTabTitle(),
            buildCreateNewRuleButton(),
            buildRulesContainer()
        ]);
    }
);
