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

                    let infoRowAttributes = {
                        id: 'link-id-' + validationLink.id,
                    };

                    return new BEM({
                        block: "link-info-row",
                        elements: {
                            'link-page-alias-': $('<div>', {
                                text: validationLink.alias
                            }),
                            'link-status-': $('<div>', {
                                text: validationLink.documentStatus
                            }),
                            'link-type-': $('<div>', {
                                text: validationLink.type
                            }),
                            // 'link-admin-': $('<div>', {
                            //     text: validationLink.admin
                            // }),
                            // 'link-ref-': $('<div>', {
                            //     text: validationLink.documentStatus
                            // }),
                            'link-name-': $('<div>', {
                                text: validationLink.title
                            }),
                            'link-host-found-': $('<div>', {
                                text: (validationLink.hostFound) ? "V" : components.controls.remove(),
                            }),
                            'link-host-reachable-': $('<div>', {
                                text: (validationLink.hostReachable) ? "V" : components.controls.remove(),
                            }),
                            'link-page-found-': $('<div>', {
                                text: (validationLink.pageFound) ? "V" : components.controls.remove(),
                            }),
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                },
                prepareTitleRow: function () {
                    let titleRow = new BEM({
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
                },
                addRowsToList: function (linkRows$) {
                    this.$searchResultContainer.css('display', 'block').append(linkRows$);
                    return this;
                },
                appendLinks: function (validationLinks) {
                    this.prepareTitleRow()
                        .addRowsToList(validationLinks.map(this.linkToRow));
                }
            };

            function listValidationLinks() {
                var filterLinks = {
                    filterBrokenLinks: $filterOnlyBrokenList.isChecked(),
                    startDocumentId: $inputStartId.getInput().val(),
                    endDocumentId: $inputEndId.getInput().val()
                };
                var tableBuilder = new linkListBuilder($searchResultContainer).clearList();
                linksValidatorRestApi.validate(filterLinks).done(tableBuilder.linkAppender);
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
