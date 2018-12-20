define(
    'imcms-link-validator-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder',
        'imcms-field-wrapper', 'imcms-bem-builder', 'jquery', 'imcms-link-validator-rest-api'],
    function (SuperAdminTab, texts, components, fieldWrapper, BEM, $, linksValidatorRestApi) {

        texts = texts.superAdmin.linkValidator;

        let $startIdInput;
        let $endIdInput;
        let $filterBrokenLinksCheckbox;
        let $resultContainer;

        function showOnlyBrokenLinks() {
        }
        function buildTitleText() {
            return fieldWrapper.wrap(components.texts.titleText('<div>', texts.name))
        }

        function buildFilterOnlyBrokenLinks() {
            return $filterBrokenLinksCheckbox = components.checkboxes.imcmsCheckbox('<div>', {
                text: texts.titleOnlyBrokenLinks,
                click: showOnlyBrokenLinks
            });
        }

        function buildLinkParamsContainer() {

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
                this.$searchResultContainer = $searchResultContainer; // rename search
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
                            'link-admin': $('<a>', {
                                text: validationLink.editLink.metaId +
                                    validationLink.editLink.title +
                                    validationLink.editLink.index,
                                href: validationLink.editLink.metaId +
                                    validationLink.editLink.title +
                                    validationLink.editLink.index
                            }),
                            'link-name': $('<a>', {
                                text: validationLink.url,
                                href: validationLink.url
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
                        block: 'link-title-row',
                        elements: {
                            'page-alias': $('<div>', {text: texts.linkInfoRow.pageAlias}),
                            'status': $('<div>', {text: texts.linkInfoRow.status}),
                            'type': $('<div>', {text: texts.linkInfoRow.type}),
                            'admin': $('<div>', {text: texts.linkInfoRow.admin}),
                            'link': $('<div>', {text: texts.linkInfoRow.link}),
                            'host-found': $('<div>', {text: texts.linkInfoRow.hostFound}),
                            'host-reachable': $('<div>', {text: texts.linkInfoRow.hostReachable}),
                            'page-found': $('<div>', {text: texts.linkInfoRow.pageFound})
                        }
                    }).buildBlockStructure('<div>', {
                        'class': 'link-title'
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
                    filterBrokenLinks: $filterBrokenLinksCheckbox.isChecked(),
                    startDocumentId: $startIdInput.getInput().val(),
                    endDocumentId: $endIdInput.getInput().val()
                };
                var tableBuilder = new linkListBuilder($resultContainer).clearList();
                linksValidatorRestApi.validate(linksValidationParams).done(tableBuilder.linkAppender);
            }

            function buildValidationButton() {
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
                    'button-validation': buildValidationButton()
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
            buildLinkParamsContainer(),
            $resultContainer = buildLinksContainer()
        ]);
    }
);
