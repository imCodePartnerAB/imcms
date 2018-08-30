/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-delete-docs-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-bem-builder',
        'imcms-modal-window-builder', 'imcms-documents-rest-api'
    ],
    function (SuperAdminTab, texts, components, BEM, modal, docs) {

        texts = texts.superAdmin.deleteDocs;

        var $inputBlock;

        function onDeleteClicked() {
            $inputBlock.find('.imcms-error-msg').slideUp();

            modal.buildModalWindow(texts.deleteConfirmation, function (confirm) {
                if (!confirm) return;

                docs.remove($inputBlock.getInput().val())
                    .success(function () {
                        $inputBlock.getInput().val('')
                    })
                    .error(function () {
                        $inputBlock.find('.imcms-error-msg').slideDown()
                    })
            })
        }

        function buildDeleteDocsBlock() {
            return new BEM({
                block: 'delete-document-row',
                elements: {
                    'input': $inputBlock = components.texts.textNumber('<div>', {
                        placeholder: '1001',
                        text: texts.title,
                        error: 'Document does not exist!'
                    }),
                    'confirm': components.buttons.negativeButton({
                        text: texts.deleteDocButton,
                        click: onDeleteClicked
                    })
                }
            }).buildBlockStructure('<div>')
        }

        return new SuperAdminTab(texts.name, [
            buildDeleteDocsBlock()
        ]);
    }
);
