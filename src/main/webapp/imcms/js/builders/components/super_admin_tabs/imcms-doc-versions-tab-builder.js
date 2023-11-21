const imcms = require("imcms");
define(
    'imcms-doc-versions-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-bem-builder', 'imcms-modal-window-builder',
        'imcms-all-data-document-rest-api', 'imcms-documents-rest-api', 'imcms-document-types',
        'imcms', 'jquery'
    ],
    function (SuperAdminTab, texts, components, BEM, modal,
              allDataDocumentRestApi, docRestApi, docTypes,
              imcms, $) {

        texts = texts.superAdmin.versionControl;

        let $inputDoc;
        let docId;
        let type;

        let $tableContainer;
        let $versionElementsContainer;

        function onFindClicked(){
            $versionElementsContainer.empty();

            $inputDoc.find('.imcms-error-msg').slideUp();

            docId = $inputDoc.getInput().val();

            let requestData = {docId: docId};
            docRestApi.read(requestData).done((document) => {
                type = document.type;
                findAndAppendVersions();
            }).fail(() => {
                $inputDoc.find('.imcms-error-msg').text(texts.error.getDocFailed).slideDown();
            })
        }

        function findAndAppendVersions(){
            allDataDocumentRestApi.getAllVersions(docId).done((docVersions) => {

                docVersions.sort((a,b) => {
                    return b.no - a.no;
                }).forEach(docVersion =>
                    $versionElementsContainer.append(buildVersionTableInfoRow(docVersion)));

            }).fail(() => {
                $inputDoc.find('.imcms-error-msg').text(texts.error.findFailed).slideDown();
            })
        }

        function onResetVersionClicked(versionNo){
            docRestApi.resetVersion(docId, versionNo).done(() => {
                alert(texts.resetSuccess + versionNo);
            }).fail(() => {
                modal.buildErrorWindow(texts.error.resetFailed + versionNo);
            })
        }

        function prepareVersionTableTitleRow(){
            return new BEM({
                block: 'imcms-doc-version-title-row',
                elements: {
                    'version-id': $('<div>', {text: texts.versionId}),
                    'user-login': $('<div>', {text: texts.login}),
                    'public-date': $('<div>', {text: texts.publicationDate}),
                    'review': $('<div>'),  //stub for page markup
                    'reset': $('<div>'),   //stub for page markup
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-title'
            });
        }

        function buildVersionTableInfoRow(docVersion){

            let reviewLink;
            if(type === docTypes.TEXT){
                reviewLink = `/${docId}?working-preview=true&version-no=`;
            }else{
                reviewLink = `/api/admin/page-info?meta-id=${docId}&version-no=`;
            }

            let $review = components.buttons.neutralButton({
                text: texts.reviewButton,
                click: () => window.open(imcms.contextPath + reviewLink + docVersion.no, '_blank')
            });

            let $reset = components.buttons.neutralButton({
                text: texts.resetButton,
                click: () => onResetVersionClicked(docVersion.no)
            });

            return new BEM({
                block: 'imcms-doc-version-info-row',
                elements: {
                    'version-id': $('<div>', {
                        text: docVersion.no
                    }),
                    'user-login': $('<div>', {
                        text: docVersion.createdBy.login
                    }),
                    'public-date': $('<div>', {
                        text: docVersion.createdDt
                    }),
                    'review': $review,
                    'reset': $reset
                }
            }).buildBlockStructure('<div>');
        }

        function buildVersionTable() {
            $tableContainer = $('<div>', {
                'class': 'imcms-doc-version-table'
            });
            $versionElementsContainer = $('<div>', {
                'class': 'imcms-doc-version-table-elements'
            });

            $tableContainer.append(prepareVersionTableTitleRow());
            $tableContainer.append($versionElementsContainer);

            return $tableContainer;
        }

        function buildInputDoc(){
            $inputDoc = components.texts.textNumber('<div>', {
                placeholder: '1001',
                text: texts.input,
                error: ''
            });

            let $find = components.buttons.negativeButton({
                text: texts.findButton,
                click: onFindClicked
            });

            return $('<div>', {
                html: [$inputDoc, $find]
            });
        }

        function buildVersionTableBlock(){
            return new BEM({
                block: 'imcms-doc-version-block',
                elements: {
                    'doc-row': buildInputDoc(),
                    'table': buildVersionTable()
                }
            }).buildBlockStructure('<div>');
        }

        const DocVersionsAdminTab = function (name, tabElements) {
            SuperAdminTab.call(this, name, tabElements);
        };

        DocVersionsAdminTab.prototype = Object.create(SuperAdminTab.prototype);

        DocVersionsAdminTab.prototype.getDocLink = () => texts.documentationLink;

        return new DocVersionsAdminTab(texts.name, [
            buildVersionTableBlock()
        ]);
    }
);
