define(
    'imcms-file-editor',
    ['imcms-modal-window-builder', 'imcms-i18n-texts', 'imcms-bem-builder', 'imcms-components-builder', 'imcms-window-builder'],
    function (modal, texts, BEM, components, WindowBuilder) {

        texts = texts.superAdmin.files;

        let $typeNameRow;
        let $isDirectory;
        let windowCreateFile;

        function buildViewFile() {

        }

        function buildEditFile() {

        }

        function buildDeleteFile() {
            return alert("delete!=)");
        }

        function buildAddFile() {

            function buildFlagIsDirectoryCheckBox() {
                return $isDirectory = components.checkboxes.imcmsCheckbox("<div>", {
                    text: texts.createFile
                })
            }

            function buildCreateFileNameRow() {
                $typeNameRow = components.texts.textAreaField('<div>', {
                    text: texts.title.createFileName
                });
                return $typeNameRow;
            }

            windowCreateFile = modal.buildModalWindow(texts.createFile, confirmed => {
                if (!confirmed) return;

                return new BEM({
                    block: 'create-file',
                    elements: {
                        'name-file': buildCreateFileNameRow(),
                        'flag-file': buildFlagIsDirectoryCheckBox()
                    }
                })
            });

            return windowCreateFile.buildWindow();
        }

        function downloadFile() {

        }

        function uploadFile() {

        }

        let fileEditor = {
            addFile: buildAddFile,
            viewFile: buildViewFile,
            editFile: buildEditFile,
            deleteFile: buildDeleteFile,
            downloadFile: downloadFile,
            uploadFile: uploadFile
        };

        return fileEditor;
    }
);