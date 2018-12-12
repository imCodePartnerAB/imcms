define(
    'imcms-link-validator-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder',
        'imcms-field-wrapper', 'imcms-bem-builder', 'jquery', 'imcms-link-validator-rest-api'],
    function (SuperAdminTab, texts, components, fieldWrapper, BEM, $, linksValidatorRestApi) {

        texts = texts.superAdmin.linkValidator;

        let fieldStartId;
        let fieldEndId;
        let $includeInactiveCheckbox;

        function buildTitleText() {
            return fieldWrapper.wrap(components.texts.titleText('<div>', texts.name))
        }

        function buildContainerTitleAndFilter() {

            function buildTitleOnlyBrokenLinks() {
                return fieldWrapper.wrap(components.texts.titleText('<div>', texts.titleOnlyBrokenLinks))
            }

            function buildCheckBoxOnlyBrokenLinks() {
                return $includeInactiveCheckbox = components.checkboxes.imcmsCheckbox('<div>', {
                    click: function () {

                    }
                });
            }

            return new BEM({
                block: 'title-filter-block',
                elements: {
                    'links-only-broken-title': buildTitleOnlyBrokenLinks(),
                    'flag-broken-links': buildCheckBoxOnlyBrokenLinks(),
                }
            }).buildBlockStructure('<div>');

        }

        let titleRow;

        function buildTitleTableRow() {
            titleRow = new BEM({
                block: 'table-title-row',
                elements: {
                    'page-alias': $('<div>', {text: texts.linkInfoRow.pageAlias}),
                    'status': $('<div>', {text: texts.linkInfoRow.status}),
                    'type': $('<div>', {text: texts.linkInfoRow.type}),
                    'admin': $('<div>', {text: texts.linkInfoRow.admin}),
                    'reference': $('<div>', {text: texts.linkInfoRow.reference}),
                    'link': $('<div>', {text: texts.linkInfoRow.link}),
                    'host-found': $('<div>', {text: texts.linkInfoRow.hostFound}),
                    'host-reachable': $('<div>', {text: texts.linkInfoRow.hostReachable}),
                    'page-found': $('<div>', {text: texts.linkInfoRow.pageFound})
                }
            }).buildBlockStructure('<div>', {
                'class': 'table-title',

            });
            return titleRow;
        }

        let $linkContainer;

        function buildLinksContainer() {
            $linkContainer = $('<div>', {
                'class': 'table-links',
                style: 'display: none;'
            });

            return fieldWrapper.wrap([$linkContainer]);
        }

        function showLinksTable() {
            return $linkContainer.slideDown('fast').append(buildTitleTableRow())
        }

        function buildContainerInputIdsValidation() {

            function buildTitleStartId() {
                return fieldWrapper.wrap(components.texts.titleText('<div>', texts.titleStartId))
            }

            function buildFieldStartId() {
                var $documentStartIdBox = components.texts.textNumber('<div>', texts.fieldStartId);
                fieldStartId = $documentStartIdBox.$input;
                return $documentStartIdBox;
            }

            function buildTitleEndId() {
                return fieldWrapper.wrap(components.texts.titleText('<div>', texts.titleEndId))
            }


            function buildFieldEndId() {
                var $documentEndIdBox = components.texts.textNumber('<div>', texts.fieldEndId);
                fieldEndId = $documentEndIdBox.$input;
                return $documentEndIdBox;
            }

            function buildButtonValidation() {
                let $button = components.buttons.positiveButton({
                    text: texts.buttonValidation,
                    click: showLinksTable
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'imcms-field-ids-block', // change this name if need!! =)
                elements: {
                    'title-text-start-id': buildTitleStartId(),
                    'links-field-start': buildFieldStartId(),
                    'title-text-end-id': buildTitleEndId(),
                    'links-field-end': buildFieldEndId(),
                    'button-validation': buildButtonValidation()
                }
            }).buildBlockStructure('<div>');
        }

        return new SuperAdminTab(texts.name, [
            buildTitleText(),
            buildContainerTitleAndFilter(),
            buildContainerInputIdsValidation(),
            buildLinksContainer()
        ]);
    }
);
