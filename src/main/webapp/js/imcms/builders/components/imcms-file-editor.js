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
        let currentFirstPath;
        let currentSecondPath;
        let windowEditFile;

        function buildViewFirstFilesContainer($fileRow, file) {
            let path = (file === '/..') ? currentFirstPath + file : file.fullPath;
            currentFile = file;
            $fileSourceRow = $fileRow;

            if (file === '/..' || file.fileType === 'DIRECTORY') {
                fileRestApi.get(path).done(files => {
                    let filesRows = files.map(file => fileToRow.transformFirstColumn(file, this));

                    firstSubFilesContainer = $('<div>').addClass('first-sub-files');
                    firstSubFilesContainer
                        .append(fileToRow.transformFirstColumn('/..', this))
                        .append(filesRows);

                    let $filesContainer = $('.files-table__first-instance');
                    $filesContainer.find('.first-sub-files').remove();
                    $filesContainer.append(firstSubFilesContainer);

                    currentFirstPath = path;
                    }
                ).fail(() => modal.buildErrorWindow(texts.error.loadError));
            } else {
                let templateName = {
                    template: currentFile.fullPath
                };
                fileRestApi.getDocuments(templateName).done(


                ).fail(() => modal.buildErrorMessage("localize!"));
            }
        }

        let $documentsContainer;

        function buildDocumentsContainer() {
            $documentsContainer = $('<div>', {
                'class': 'table-documents'
            });


        }

        function buildViewSecondFilesContainer($fileRow, file) {
            let path = (file === '/..') ? currentSecondPath + file : file.fullPath;
            currentFile = file;
            $fileSourceRow = $fileRow;

            if (file === '/..' || file.fileType === 'DIRECTORY') {
                fileRestApi.get(path).done(files => {
                    let filesRows = files.map(file => fileToRow.transformSecondColumn(file, this));

                    secondSubFilesContainer = $('<div>').addClass('second-sub-files');
                    secondSubFilesContainer
                        .append(fileToRow.transformSecondColumn('/..', this))
                        .append(filesRows);

                    let $filesContainer = $('.files-table__second-instance');
                    $filesContainer.find('.second-sub-files').remove();
                    $filesContainer.append(secondSubFilesContainer);

                        currentSecondPath = path;
                    }
                ).fail(() => modal.buildErrorWindow(texts.error.loadError));
            }
        }

        function prepareOnEditFileInFirstColumn() {
            prepareOnEditFile(() => buildEditFile(currentFirstPath, firstSubFilesContainer, fileToRow.transformFirstColumn));
        }

        function prepareOnEditFileInSecondColumn() {
            prepareOnEditFile(() => buildEditFile(currentSecondPath, secondSubFilesContainer, fileToRow.transformSecondColumn));
        }

        function prepareOnEditFile(buildEditFile) {

            modal.buildModalWindow(texts.warnChangeMessage, confirmed => {
                if (!confirmed) return;

                let isDirectory = 'DIRECTORY' === currentFile.fileType;
                newFileNameField.setValue(currentFile.fileName);
                checkBoxIsDirectory.setChecked(isDirectory).$input.attr('disabled', 'disabled');

                buildEditFile();
            });
        }

        function buildEditFile(currentPath, subFilesContainer, transformColumn) {
            windowEditFile =
                modal.buildCreateFileModalWindow(
                    texts.editorFile, newFileNameField, checkBoxIsDirectory, confirmed => {
                        if (!confirmed) {
                            onEditFile(currentPath, subFilesContainer, transformColumn)
                        }
                    });


            return windowEditFile;
        }

        function onEditFile(currentPath, subFilesContainer, transformColumn) {
            let name = newFileNameField.getValue();
            if (!name) return;
            let currentFullPath = currentPath + "/" + name;
            let fileToSave = {
                src: currentFile.fullPath,
                target: currentFullPath
            };

            fileRestApi.rename(fileToSave).done(newFile => {
                currentFile = newFile;

                $fileSourceRow.remove();
                $fileSourceRow = transformColumn(currentFile, fileEditor);
                subFilesContainer.append($fileSourceRow);

            }).fail(() => modal.buildErrorWindow(texts.error.editFailed));
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

        function buildAddFileInFirstColumn() {
            confirmAddFile(() => onSaveFile(currentFirstPath, firstSubFilesContainer, fileToRow.transformFirstColumn));
            return windowCreateFile;
        }

        function buildAddFileInSecondColumn() {
            confirmAddFile(() => onSaveFile(currentSecondPath, secondSubFilesContainer, fileToRow.transformSecondColumn));
            return windowCreateFile;
        }

        function confirmAddFile(onConfirm) {
            newFileNameField.setValue('');
            checkBoxIsDirectory.$input.removeAttr('disabled');
            windowCreateFile =
                modal.buildCreateFileModalWindow(
                    texts.createFile, newFileNameField, checkBoxIsDirectory, confirmed => {
                        if (!confirmed) {
                            onConfirm();
                        }
                    });
        }

        function onSaveFile(currentPath, subFilesContainer, transformColumn) {
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
                $fileSourceRow = transformColumn((currentFile = newFile), fileEditor);

                subFilesContainer.append($fileSourceRow);

            }).fail(() => modal.buildErrorWindow(texts.error.createError));
        }

        function uploadFileInFirstColumn() {
            uploadFile(currentFirstPath, firstSubFilesContainer, fileToRow.transformFirstColumn);
        }

        function uploadFileInSecondColumn() {
            uploadFile(currentSecondPath, secondSubFilesContainer, fileToRow.transformSecondColumn);
        }

        function uploadFile(targetDirectory, subFilesContainer, transformColumn) {
            let $fileInput = $('<input>', {
                type: 'file',
                multiple: ''
            });
            $fileInput.click();

            $fileInput.change(() => {
                let formData = new FormData();

                let files = $fileInput.prop('files');
                Array.from(files).forEach(file => formData.append(file.name, file));

                formData.append("targetDirectory", targetDirectory);

                fileRestApi.upload(formData).done(uploadedFiles => {
                    currentFile = null;

                    uploadedFiles.forEach(file => {
                        $fileSourceRow = transformColumn(file, fileEditor);
                        subFilesContainer.append($fileSourceRow);
                    });
                }).fail(() => modal.buildErrorWindow(texts.error.uploadError));
            });
        }

        function onCancelChanges($fileRowElement, file) {

            getOnDiscardChanges(() => {
                onFileView = onFileSimpleView;
                currentFile = file;
                $fileSourceRow = $fileRowElement;
                prepareFileView();
            }).call();
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
            buildViewFirstFilesContainer($fileRow, file);
            // onEditFile();
        }

        function onFileSimpleView($fileRowElement, file) {
            if (currentFile && currentFile.fullPath === file.fullPath) return;
            currentFile = file;
            $fileSourceRow = $fileRowElement;

            prepareFileView();
        }

        function moveFileRight() {
            moveFile(currentSecondPath, secondSubFilesContainer);
        }

        function moveFileLeft() {
            moveFile(currentFirstPath, firstSubFilesContainer);
        }

        function moveFile(targetPath, targetSubFilesContainer) {
            if (currentFile.fileType !== 'FILE') return;

            let newFileFullPath = targetPath + "/" + currentFile.fileName;

            if (newFileFullPath === currentFile.fullPath) return;

            let paths = {
                src: currentFile.fullPath,
                target: newFileFullPath
            };

            fileRestApi.move(paths).done(() => {
                currentFile.fullPath = newFileFullPath;

                $fileSourceRow.remove();
                $fileSourceRow = fileToRow.transformFirstColumn(currentFile, fileEditor);
                targetSubFilesContainer.append($fileSourceRow);
            }).fail(() => modal.buildErrorWindow(texts.error.moveError));
        }

        function copyFileRight() {
            copyFile(currentSecondPath, secondSubFilesContainer);
        }

        function copyFileLeft() {
            copyFile(currentFirstPath, firstSubFilesContainer);
        }

        function copyFile(targetPath, targetSubFilesContainer) {
            if (currentFile.fileType !== 'FILE') return;

            let newFileFullPath = targetPath + "/" + currentFile.fileName;

            if (newFileFullPath === currentFile.fullPath) return;

            let paths = {
                src: currentFile.fullPath,
                target: newFileFullPath
            };

            fileRestApi.copy(paths).done(newFile => {
                $fileSourceRow = fileToRow.transformFirstColumn(newFile, fileEditor);
                targetSubFilesContainer.append($fileSourceRow);
            }).fail(() => modal.buildErrorWindow(texts.error.copyError));
        }

        let fileEditor = {
            addFileInFirstColumn: buildAddFileInFirstColumn,
            addFileInSecondColumn: buildAddFileInSecondColumn,
            viewFirstFilesContainer: buildViewFirstFilesContainer,
            viewSecondFilesContainer: buildViewSecondFilesContainer,
            editFileInFirstColumn: prepareOnEditFileInFirstColumn,
            editFileInSecondColumn: prepareOnEditFileInSecondColumn,
            deleteFile: buildDeleteFile,
            uploadFileInFirstColumn: uploadFileInFirstColumn,
            uploadFileInSecondColumn: uploadFileInSecondColumn,
            moveFileRight: moveFileRight,
            moveFileLeft: moveFileLeft,
            copyFileRight: copyFileRight,
            copyFileLeft: copyFileLeft
        };

        return fileEditor;
    }
);