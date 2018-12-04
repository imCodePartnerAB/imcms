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
                $rulesContainer.append(rules.map(rule => ruleToRow.transform(rule, ruleEditor)));
            });

            return fieldWrapper.wrap([ruleEditor.buildContainer(),$rulesContainer]);
        }


        //Regex to validate ipv4/6 format
        //    ((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))


        return new SuperAdminTab(texts.name, [
            buildTabTitle(),
            buildCreateNewRuleButton(),
            buildRulesContainer()
        ]);
    }
);
