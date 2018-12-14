define(
    'imcms-link-validator-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder',
        'imcms-field-wrapper', 'imcms-bem-builder', 'jquery', 'imcms-link-validator-rest-api'],
    function (SuperAdminTab, texts, components, fieldWrapper, BEM, $, linksValidatorRestApi) {

        texts = texts.superAdmin.linkValidator;

        let $startIdInput;
        let $endIdInput;
        let $filterBrokenLinks;
        let $resultContainer;

        function showOnlyBrokenLinks() {

        }

        function buildTitleText() {
            return fieldWrapper.wrap(components.texts.titleText('<div>', texts.name))
        }

        function buildFilterOnlyBrokenLinks() {
            return $filterBrokenLinks = components.checkboxes.imcmsCheckbox('<div>', {
                text: texts.titleOnlyBrokenLinks,
                click: showOnlyBrokenLinks
            });
        }

        function buildContainerInputIdsValidation() {

            function buildFieldStartId() {
                return $startIdInput = components.texts.textNumber('<div>', {
                    text: texts.titleStartId,
                });
            }

            function buildFieldEndId() {
                return $endIdInput = components.texts.textNumber('<div>', {
                    text: texts.titleEndId,
                });
            }

            var linkListBuilder = function ($searchResultContainer) {
                this.$searchResultContainer = $searchResultContainer;
                this.linkAppender = this.appendLinks.bind(this);
            };

            linkListBuilder.prototype = {
                isEmpty: true,
                clearList: function () {
                    this.$searchResultContainer.empty();
                    this.isEmpty = true;

                    return this;
                },
                linkToRow: function (validationLink) {
                    return new BEM({
                        block: "link-info-row",
                        elements: {
                            'link-page-alias': $('<div>', {
                                text: validationLink.documentData.alias
                            }),
                            'link-status': $('<div>', {
                                text: validationLink.documentData.documentStatus
                            }),
                            'link-type': $('<div>', {
                                text: validationLink.documentData.type
                            }),
                            'link-admin': $('<div>', {
                                text: validationLink.documentData.title
                            }),
                            'link-name': $('<div>', {
                                text: validationLink.url
                            }),
                            'link-host-found': validationLink.hostFound
                                ? components.controls.check()
                                : components.controls.remove(),
                            'link-host-reachable': validationLink.hostReachable
                                ? components.controls.check()
                                : components.controls.remove(),
                            'link-page-found': validationLink.pageFound
                                ? components.controls.check()
                                : components.controls.remove()
                        }
                    }).buildBlockStructure("<div>");
                },
                prepareTitleRow: function () {
                    let titleRow = new BEM({
                        block: 'table-title-row',
                        elements: {
                            'page-alias': $('<div>', {text: texts.linkInfoRow.pageAlias}),
                            'status': $('<div>', {text: texts.linkInfoRow.status}),
                            'type': $('<div>', {text: texts.linkInfoRow.type}),
                            'admin': $('<div>', {text: texts.linkInfoRow.admin}),
                            //'ref': $('<div>', {text: texts.linkInfoRow.reference}),
                            'link': $('<div>', {text: texts.linkInfoRow.link}),
                            'host-found': $('<div>', {text: texts.linkInfoRow.hostFound}),
                            'host-reachable': $('<div>', {text: texts.linkInfoRow.hostReachable}),
                            'page-found': $('<div>', {text: texts.linkInfoRow.pageFound})
                        }
                    }).buildBlockStructure('<div>', {
                        'class': 'table-title'
                    });
                    this.$searchResultContainer.append(titleRow);
                    return this;
                },
                rowsAddToList: function (linkRows) {
                    this.$searchResultContainer.css('display', 'inline-block').append(linkRows);
                    return this;
                },
                appendLinks: function (validationLinks) {
                    this.prepareTitleRow()
                        .rowsAddToList(validationLinks.map(this.linkToRow));
                }
            };

            function listValidationLinks() {
                var linksValidationParams = {
                    filterBrokenLinks: $filterBrokenLinks.isChecked(),
                    startDocumentId: $startIdInput.getInput().val(),
                    endDocumentId: $endIdInput.getInput().val()
                };
                var tableBuilder = new linkListBuilder($resultContainer).clearList();
                linksValidatorRestApi.validate(linksValidationParams).done(tableBuilder.linkAppender);
            }

            function buildButtonValidation() {
                let $button = components.buttons.positiveButton({
                    text: texts.buttonValidation,
                    click: listValidationLinks
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'link-params-block',
                elements: {
                    'links-field-start': buildFieldStartId(),
                    'links-field-end': buildFieldEndId(),
                    'button-validation': buildButtonValidation()
                }
            }).buildBlockStructure('<div>');
        }

        function buildLinksContainer() {
            return $('<div>', {
                'class': 'table-links',
                style: 'display: none;'
            });
        }

        return new SuperAdminTab(texts.name, [
            buildTitleText(),
            buildFilterOnlyBrokenLinks(),
            buildContainerInputIdsValidation(),
            $resultContainer = buildLinksContainer()
        ]);
    }
);
