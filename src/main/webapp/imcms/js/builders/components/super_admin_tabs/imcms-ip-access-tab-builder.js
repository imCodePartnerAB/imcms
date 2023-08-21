/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-ip-access-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-ip-rules-rest-api',
        'imcms-rule-editor', 'imcms-rule-to-row-transformer', 'imcms', 'imcms-bem-builder', 'jquery', 'imcms-field-wrapper',
        "imcms-modal-window-builder"
    ],
    function (SuperAdminTab, texts, components, rulesApi, ruleEditor, ruleToRow, imcms, BEM, $, fieldWrapper, modal) {

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
                });
            }
        };

        runCallbacksWithReadRulesWhenRuleEditorLoaded();

        function runCallbacksWithReadRulesWhenRuleEditorLoaded() {
            let loadedData = ruleEditor.getLoadedData();
            $.when(loadedData.rolesReadAjax, loadedData.usersReadAjax).then(runCallbacksWithReadRules);
        }

        function runCallbacksWithReadRules() {
            rulesApi.read()
                .done(rules => {
                    ruleLoader.runCallbacks(rules);
                })
                .fail(() => modal.buildErrorWindow(texts.error.loadFailed))
        }

        let $ruleElementsContainer;

        function buildTabTitle() {
            return fieldWrapper.wrap(components.texts.titleText('<div>', texts.title));
        }

        function onCreateNewRule() {
            $ruleElementsContainer.find('.rules-table-elements__rule-row--active')
                .removeClass('rules-table-elements__rule-row--active');

            ruleEditor.editRule($('<div>'), {
                id: null,
                enabled: false,
                restricted: false,
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
            const $rulesContainer = $('<div>', {
                'class': 'rules-table'
            });

            $ruleElementsContainer = $('<div>', {
                'class': 'rules-table-elements'
            });

            ruleLoader.whenRulesLoaded(rules => {
                $ruleElementsContainer.append(rules.map(rule => ruleToRow.transform(rule, ruleEditor)));
                $rulesContainer.append(prepareTitleRow());
                $rulesContainer.append($ruleElementsContainer);
            });

            return fieldWrapper.wrap([ruleEditor.buildContainer(), $rulesContainer]);
        }


        function prepareTitleRow() {
            const $titleRow = new BEM({
                block: 'rule-title-row',
                elements: {
                    'rule-enabled': $('<div>', {text: texts.fields.enabled}),
                    'rule-restricted': $('<div>', {text: texts.fields.restricted}),
                    'rule-ip-range': $('<div>', {text: texts.fields.ipRange}),
                    'rule-user': $('<div>', {text: texts.fields.user}),
                    'rule-role': $('<div>', {text: texts.fields.role}),
                    'rule-actions': $('<div>', {})
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-title'
            });
            return $titleRow;
        }

        const IpAccessAdminTab = function (name, tabElements) {
            SuperAdminTab.call(this, name, tabElements);
        };

        IpAccessAdminTab.prototype = Object.create(SuperAdminTab.prototype);

        IpAccessAdminTab.prototype.getDocLink = () => texts.documentationLink;

        return new IpAccessAdminTab(texts.name, [
            buildTabTitle(),
            buildCreateNewRuleButton(),
            buildRulesContainer()
        ]);
    }
);
