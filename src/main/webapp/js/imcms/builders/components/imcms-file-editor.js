define(
    'imcms-file-editor',
    ['imcms-modal-window-builder', 'imcms-i18n-texts', 'imcms-bem-builder', 'imcms-components-builder'],
    function (modal, texts, BEM, components) {

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

            windowCreateFile =
                modal.buildCreateFileModalWindow(
                    texts.createFile, texts.title.createFileName, texts.title.createDirectory, confirmed => {
                    });

            return windowCreateFile;
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