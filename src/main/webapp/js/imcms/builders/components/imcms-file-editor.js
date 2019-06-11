define(
    'imcms-file-editor',
    ['imcms-modal-window-builder', 'imcms-i18n-texts', 'imcms-bem-builder', 'imcms-components-builder',
        'imcms-files-rest-api', 'imcms-file-to-row-transformer', 'jquery'],
    function (modal, texts, BEM, components, fileRestApi, fileToRow, $) {

        texts = texts.superAdmin.files;

        let windowCreateFile;
        let currentFile;
        let firstSubFilesContainer;
        let secondSubFilesContainer;
        let currentPath;
        let currentSecondPath;

        function buildViewFile($fileRow, file) {
            firstSubFilesContainer = $('<div>', {
                'class': 'first-sub-files'
            });

            let path = (file === '/..') ? currentPath + file : file.fullPath;
            currentFile = file;
            $fileSourceRow = $fileRow;

            if (file === '/..' || file.fileType === 'DIRECTORY') {
                fileRestApi.get(path).done(files => {
                        $('.files-table__first-instance').find('.first-sub-files').remove();
                        firstSubFilesContainer.append(fileToRow.transformFirstColumn('/..', this));
                        $('.files-table__first-instance')
                            .append(firstSubFilesContainer.append(files.map(file => fileToRow.transformFirstColumn(file, this))));
                    currentPath = path;
                    }
                ).fail(() => modal.buildErrorWindow(texts.error.loadError));
            } else {
                currentPath = path;
            }
        }

        function buildViewTwoFile($fileRow, file) {
            secondSubFilesContainer = $('<div>', {
                'class': 'second-sub-files'
            });

            let path = (file === '/..') ? currentSecondPath + file : file.fullPath;


            if (file === '/..' || file.fileType === 'DIRECTORY') {
                fileRestApi.get(path).done(files => {
                        $('.files-table__second-instance').find('.second-sub-files').remove();
                        secondSubFilesContainer.append(fileToRow.transformSecondColumn('/..', this));
                        $('.files-table__second-instance')
                            .append(secondSubFilesContainer.append(files.map(file => fileToRow.transformSecondColumn(file, this))));
                        currentSecondPath = path;
                    }
                ).fail(() => modal.buildErrorWindow(texts.error.loadError));
            }
        }

        function buildEditFile($fileRow, file) {

        }

        function buildDeleteFile() {
            modal.buildModalWindow(texts.warnDeleteMessage, confirmed => {
                if (!confirmed) return;

                fileRestApi.delete(currentFile.fullPath).done(() => {
                        $fileSourceRow.remove();
                        currentFile = null;
                    }
                ).fail(() => modal.buildErrorWindow(texts.error.deleteFailed));
            });
        }

        let newFileNameField = buildCreateField();
        let checkBoxIsDirectory = buildIsDirectoryCheckBox();

        function buildCreateField() {
            return components.texts.textField('<div>', {
                text: texts.title.createFileName
            });

        }

        function buildIsDirectoryCheckBox() {
            return components.checkboxes.imcmsCheckbox("<div>", {
                text: texts.title.createDirectory
            });

        }

        function onSaveFile() {
            let name = newFileNameField.getValue();
            let isDirectory = checkBoxIsDirectory.isChecked();

            if (!name) return;

            let currentFullPath = currentPath + "/" + name;

            let fileToSave = {
                fileName: name,
                fullPath: currentFullPath,
                fileType: isDirectory ? 'DIRECTORY' : 'FILE'
            };


            fileRestApi.create(fileToSave).done(newFile => {
                $fileSourceRow = fileToRow.transformFirstColumn((currentFile = newFile), fileEditor);

                firstSubFilesContainer.append($fileSourceRow);

            }).fail(() => modal.buildErrorWindow("Do not create!"));
        }


        function buildAddFile() {
            windowCreateFile =
                modal.buildCreateFileModalWindow(
                    texts.createFile, newFileNameField, checkBoxIsDirectory, confirmed => {
                        if (!confirmed) {
                            onSaveFile()
                        }
                    });


            return windowCreateFile;
        }

        function downloadFile() {

        }

        function uploadFile() {

        }

        function onCancelChanges($fileRowElement, file) {

            getOnDiscardChanges(() => {
                onFileView = onFileSimpleView;
                currentFile = file;
                $fileSourceRow = $fileRowElement;
                prepareFileView();
            }).call();
        }

        function onEditFile() {
            onFileView = onCancelChanges;

            let name = newFileNameField.getValue();
            let isDirectory = checkBoxIsDirectory.isChecked();

            if (!name) return;

            let fileToSave = {
                name: name,
                isDirectory: isDirectory
            };

            fileRestApi.rename(fileToSave).done(savedFile => {
                currentFile = savedFile;
            })
        }

        function getOnDiscardChanges(onConfirm) {
            return () => {
                modal.buildModalWindow(texts.warnChangeMessage, confirmed => {
                    if (!confirmed) return;
                    onConfirm.call();
                });
            }
        }

        let onEditDelegate = onSimpleEdit;
        let onFileView = onFileSimpleView;
        let $fileSourceRow;

        function prepareFileView() {
            onEditDelegate = onSimpleEdit;

            $fileSourceRow.parent()
                .find('.files-table__file-row--active')
                .removeClass('files-table__file-row--active');

            $fileSourceRow.addClass('files-table__file-row--active');
        }

        function onSimpleEdit($fileRow, file) {
            buildViewFile($fileRow, file);
            onEditFile();
        }

        function onFileSimpleView($fileRowElement, file) {
            if (currentFile && currentFile.fullPath === file.fullPath) return;
            currentFile = file;
            $fileSourceRow = $fileRowElement;

            prepareFileView();
        }

        let fileEditor = {
            addFile: buildAddFile,
            viewFile: buildViewFile,
            viewSecondFile: buildViewTwoFile,
            editFile: buildEditFile,
            deleteFile: buildDeleteFile,
            downloadFile: downloadFile,
            uploadFile: uploadFile
        };

        return fileEditor;
    }
);