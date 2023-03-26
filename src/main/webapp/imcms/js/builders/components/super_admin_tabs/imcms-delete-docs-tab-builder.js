/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-delete-docs-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-bem-builder',
        'imcms-modal-window-builder', 'imcms-documents-rest-api', 'imcms-document-basket-rest-api', 'imcms', 'jquery'
    ],
    function (SuperAdminTab, texts, components, BEM, modal, docRestApi, docBasketRestApi, imcms, $) {

        texts = texts.superAdmin.deleteDocs;

        let $inputBlock;

        let $inputBasket;
        let docsInTable = [];
        let classBasketElementsContainer = 'imcms-doc-basket-table-elements';

        function onDeleteClicked() {
            $inputBlock.find('.imcms-error-msg').slideUp();

            modal.buildModalWindow(texts.deleteConfirmation, confirm => {
                if (!confirm) return;

                docRestApi.remove($inputBlock.getInput().val())
                    .done(() => {
                        $inputBlock.getInput().val('');
                    })
                    .fail((response) => {
                        let errorText = response.responseText ? texts.error.protectedDoc : texts.error.missedDoc;
                        $inputBlock.find('.imcms-error-msg').text(errorText).slideDown();
                    });
            });
        }

        function buildDeleteDocsBlock() {
            return new BEM({
                block: 'delete-document-row',
                elements: {
                    'input': $inputBlock = components.texts.textNumber('<div>', {
                        placeholder: '1001',
                        text: texts.title,
                        error: ''
                    }),
                    'confirm': components.buttons.negativeButton({
                        text: texts.deleteDocButton,
                        click: onDeleteClicked
                    })
                }
            }).buildBlockStructure('<div>')
        }

        function onPutClicked(){
            $inputBasket.find('.imcms-error-msg').slideUp();

            let docId = $inputBasket.getInput().val();
            docBasketRestApi.create(docId).done((doc) => {
                $('.'+classBasketElementsContainer).prepend(buildBasketTableInfoRow(doc));
            }).fail(() => {
                $inputBasket.find('.imcms-error-msg').text(texts.basket.error.putFailed).slideDown();
            })
        }

        function onRestoreClicked() {
            let ids = docsInTable.filter(docInTable => docInTable.checkbox.isChecked())
                .map(docInTable => docInTable.id);

            docBasketRestApi.restoreByIds(ids)
                .done(() => {
                    deleteFromTable(ids);
                })
                .fail(() => {
                    modal.buildErrorWindow(texts.basket.error.restoreFailed);
                });
        }

        function onDeleteByIdsClicked() {
            let ids = docsInTable.filter(docInTable => docInTable.checkbox.isChecked())
                .map(docInTable => docInTable.id);

            docRestApi.removeByIds(ids)
                .done(() => {
                    deleteFromTable(ids);
                })
                .fail((response) => {
                    let errorText;
                    if (response.responseText) {
                        let ids = response.responseText.replace("[", "").replace("]", "");
                        errorText = texts.error.removeProtectedDocumentFailed + ": " + ids;
                    } else {
                        errorText = texts.error.removeDocumentFailed;
                    }
                    modal.buildErrorWindow(errorText);
                });
        }

        function deleteFromTable(ids){
            ids.forEach(id => $(`[data-id='${id}']`).remove());
        }

        function prepareBasketTableTitleRow(){
            return new BEM({
                block: 'imcms-doc-basket-title-row',
                elements: {
                    'meta-id': $('<div>', {text: texts.basket.metaId}),
                    'headline': $('<div>', {text: texts.basket.headline}),
                    'user-login': $('<div>', {text: texts.basket.userLogin}),
                    'added-date': $('<div>', {text: texts.basket.addedDate}),
                    'checkbox': $('<div>')  //stub for page markup
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-title'
            });
        }

        function buildBasketTableInfoRow(doc){
            let headline = doc.commonContents[0].headline;
            doc.commonContents.forEach(c => {
                if(c.language.code === imcms.userLanguage)
                    headline = c.headline
            });

            let $checkbox = components.checkboxes.imcmsCheckbox('<div>');

            docsInTable.push({
                id: doc.id,
                checkbox: $checkbox
            });

            return new BEM({
                block: 'imcms-doc-basket-info-row',
                elements: {
                    'meta-id': $('<div>', {
                        text: doc.id
                    }),
                    'headline': $('<div>', {
                        text: headline
                    }),
                    'user-login': $('<div>', {
                        text: doc.documentWasteBasket.addedBy.login
                    }),
                    'added-date': $('<div>', {
                        text: doc.documentWasteBasket.addedDatetime
                    }),
                    'checkbox': $checkbox
                }
            }).buildBlockStructure('<div>', {
                'data-id': doc.id
            });
        }

        function buildBasketTable() {
            let $tableContainer = $('<div>', {
                'class': 'imcms-doc-basket-table'
            });

            $tableContainer.append(prepareBasketTableTitleRow());

            let $basketElementsContainer = $('<div>', {
                'class': classBasketElementsContainer
            });

            docBasketRestApi.read()
                .done(docs => {
                    docs.sort((a,b) => {
                        return Date.parse(b.documentWasteBasket.addedDatetime) - Date.parse(a.documentWasteBasket.addedDatetime);
                    }).forEach(doc =>
                        $basketElementsContainer.append(buildBasketTableInfoRow(doc)));
                        $tableContainer.append($basketElementsContainer);
                })
                .fail(() => modal.buildErrorWindow(texts.basket.error.readFailed));

            return $tableContainer;
        }

        function buildBasketTableButtons() {
            return new BEM({
                block: 'imcms-doc-basket-buttons',
                elements: {
                    'delete': components.buttons.errorButton({
                        text: texts.basket.deleteButton,
                        click: onDeleteByIdsClicked
                    }),
                    'restore': components.buttons.positiveButton({
                        text: texts.basket.restoreButton,
                        click: onRestoreClicked
                    })
                }
            }).buildBlockStructure('<div>');
        }
        
        function buildBasketTableBlock(){
            return new BEM({
                block: 'imcms-doc-basket-table-block',
                elements: {
                    'table': buildBasketTable(),
                    'buttons': buildBasketTableButtons()
                }
            }).buildBlockStructure('<div>');
        }

        function buildBasketInput(){
            $inputBasket = components.texts.textNumber('<div>', {
                placeholder: '1001',
                text: texts.basket.input,
                error: ''
            });

            let $confirm = components.buttons.negativeButton({
                text: texts.basket.putButton,
                "class": "imcms-doc-basket-block__put-button",
                click: onPutClicked
            });

            return $('<div>', {
                html: [$inputBasket, $confirm]
            });
        }

        function buildBasketBlock(){
            return new BEM({
                block: 'imcms-doc-basket-block',
                elements: {
                    'put-row': buildBasketInput(),
                    'title': components.texts.titleText('<div>', texts.basket.title),
                    'table': buildBasketTableBlock()
                }
            }).buildBlockStructure('<div>');
        }

        return new SuperAdminTab(texts.name, [
            buildDeleteDocsBlock(),
            buildBasketBlock()
        ]);
    }
);
