define(
    'imcms-link-validator-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder',
        'imcms-field-wrapper', 'imcms-bem-builder', 'jquery', 'imcms-link-validator-rest-api'],
    function (SuperAdminTab, texts, components, fieldWrapper, BEM, $, linksValidatorRestApi) {

        texts = texts.superAdmin.linkValidator;

        let $inputStartId;
        let $inputEndId;
        let $filterOnlyBrokenList;
        let $searchResultContainer;

        function showOnlyBrokenLinks() {

        }

        function buildTitleText() {
            return fieldWrapper.wrap(components.texts.titleText('<div>', texts.name))
        }

        function buildFilterOnlyBrokenLinks() {
            return $filterOnlyBrokenList = components.checkboxes.imcmsCheckbox('<div>', {
                text: texts.titleOnlyBrokenLinks,
                click: showOnlyBrokenLinks
            });
        }



        function buildLinksContainer() {
            let $linkContainer = $('<div>', {
                'class': 'table-links',
                style: 'display: none;'
            });

            return fieldWrapper.wrap([$linkContainer]);
        }

        function buildContainerInputIdsValidation() {

            function buildFieldStartId() {
                return $inputStartId = components.texts.textNumber('<div>', {
                    placeholder: '1001',
                    text: texts.titleStartId,
                });
            }

            function buildFieldEndId() {
                return $inputEndId = components.texts.textNumber('<div>', {
                    placeholder: '1001',
                    text: texts.titleEndId,
                });
            }

            function buildButtonValidation() {
                let $button = components.buttons.positiveButton({
                    text: texts.buttonValidation,
                    click: listValidationLinks
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'imcms-field-ids-block', // change this name if need!! =)
                elements: {
                    'links-field-start': buildFieldStartId(),
                    'links-field-end': buildFieldEndId(),
                    'button-validation': buildButtonValidation()
                }
            }).buildBlockStructure('<div>');
        }

        return new SuperAdminTab(texts.name, [
            buildTitleText(),
            buildFilterOnlyBrokenLinks(),
            buildContainerInputIdsValidation(),
            $searchResultContainer = buildLinksContainer()
        ]);
    }
);
