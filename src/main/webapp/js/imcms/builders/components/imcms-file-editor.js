define(
    'imcms-file-editor',
    ['imcms-modal-window-builder', 'imcms-i18n-texts', 'imcms-bem-builder', 'imcms-components-builder',
        'imcms-files-rest-api', 'imcms-file-to-row-transformer', 'jquery', 'imcms-document-transformer',
        'imcms-super-admin-page-builder', 'imcms'],
    function (modal, texts, BEM, components, fileRestApi, fileToRow, $, docToRow) {

        texts = texts.superAdmin.files;

        let windowCreateFile;
        let currentFile;
        let firstSubFilesContainer;
        let secondSubFilesContainer;
        let currentFirstPath;
        let currentSecondPath;
        let windowEditFile;
        let $documentsContainer;
        let $documentsData;

        function buildDocumentsContainer() {
            $documentsContainer = $('<div>', {
                'class': 'table-documents',
                'style': 'display: none;'
            });


            return $documentsContainer;
        }

        function getViewDocById($docRow, doc) {
            let question = texts.warnViewDocMessage;

            modal.buildModalWindow(question, function (answer) {
                if (!answer) {
                    return;
                }

                window.location.replace("/" + doc.id);
            });
        }

        function buildTitleRow() {
            return new BEM({
                block: 'title-doc-row',
                elements: {
                    'doc-id': $('<div>', {text: texts.documentData.docId}),
                    'doc-type': $('<div>', {text: texts.documentData.docType})
                }
            }).buildBlockStructure('<div>', {
                'class': 'table-title'
            });
        }

        function addActiveForFile(type) {
            if (type === 'DIRECTORY') {
                $fileSourceRow.parent()
                    .find('.files-table__directory-row--active')
                    .removeClass('files-table__directory-row--active');
                $fileSourceRow.addClass('files-table__directory-row--active');
            } else {
                $fileSourceRow.parent()
                    .find('.files-table__file-row--active')
                    .removeClass('files-table__file-row--active');
                $fileSourceRow.addClass('files-table__file-row--active');
            }
        }

        function buildViewFirstFilesContainer($fileRow, file) {
            let path = (file === '/..') ? currentFirstPath + file : file.fullPath;

            currentFile = file;
            $fileSourceRow = $fileRow;

            if (file === '/..' || file.fileType === 'DIRECTORY') {
                fileRestApi.get(path).done(files => {
                    let filesRows = files.map(file => fileToRow.transformFirstColumn(file, this));

                    addActiveForFile(file.fileType);


                    firstSubFilesContainer = $('<div>').addClass('first-sub-files');
                    firstSubFilesContainer
                        .append(fileToRow.transformFirstColumn('/..', this))
                        .append(filesRows);

                    let $filesContainer = $('.files-table__first-instance');
                    $filesContainer.find('.first-sub-files').remove();
                    $filesContainer.append(firstSubFilesContainer);

                    $documentsContainer.hide();

                    currentFirstPath = path;
                    }
                ).fail(() => modal.buildErrorWindow(texts.error.loadError));
            } else {

                addActiveForFile(file.fileType);

                let templateName = {
                    template: currentFile.fullPath
                };
                fileRestApi.getDocuments(templateName).done(documents => {
                    if (documents.length > 0) {
                        $documentsData = $('<div>').addClass('documents-data');
                        let documentsRows = documents.map(doc => docToRow.transform(doc, this));
                        $documentsContainer.find('.documents-data').remove();
                        $documentsData.append(buildTitleRow());
                        $documentsData.append(documentsRows);
                        $documentsContainer.append($documentsData).show();
                    } else {
                        $documentsContainer.find('.documents-data').remove();
                    }
                    }
                ).fail(() => modal.buildErrorWindow(texts.error.loadDocError));
            }

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
            } else {
                let templateName = {
                    template: currentFile.fullPath
                };
                fileRestApi.getDocuments(templateName).done(documents => {
                        if (documents.length > 0) {
                            $documentsData = $('<div>').addClass('documents-data');
                            let documentsRows = documents.map(doc => docToRow.transform(doc, this));
                            $documentsContainer.find('.documents-data').remove();
                            $documentsData.append(buildTitleRow());
                            $documentsData.append(documentsRows);
                            $documentsContainer.append($documentsData).show();
                        } else {
                            $documentsContainer.find('.documents-data').remove();
                        }
                    }
                ).fail(() => modal.buildErrorWindow(texts.error.loadDocError));
            }
        }

        function setEnableEditContent() {
            if (editCheckBox.isChecked()) {
                contentTextArea.$input.removeAttr('disabled');
                newFileNameField.$input.attr('disabled', 'disabled');
                checkBoxIsDirectory.$input.attr('disabled', 'disabled');
            } else {
                contentTextArea.$input.attr('disabled', 'disabled');
                newFileNameField.$input.removeAttr('disabled');
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

                editCheckBox = buildIsEditCheckBox();
                let isDirectory = 'DIRECTORY' === currentFile.fileType;
                newFileNameField.setValue(currentFile.fileName);
                checkBoxIsDirectory.setChecked(isDirectory).$input.attr('disabled', 'disabled');
                setEnableEditContent();
                let contentsLine = currentFile.contents;
                if (contentsLine) {
                    contentTextArea.setValue(
                        contentsLine.join("\n")
                    );
                } else {
                    contentTextArea.setValue(contentsLine);
                }

                buildEditFile();
            });
        }

        function buildEditFile(currentPath, subFilesContainer, transformColumn) {
            windowEditFile =
                modal.buildEditFileModalWindow(
                    texts.editorFile, newFileNameField, checkBoxIsDirectory, contentTextArea, editCheckBox, unconfirmed => {
                        if (!unconfirmed && !editCheckBox.isChecked()) {
                            onEditFile(currentPath, subFilesContainer, transformColumn)
                        }
                        if (!unconfirmed && editCheckBox.isChecked()) {
                            onEditFileContent(currentPath)
                        }
                    });

            return windowEditFile;
        }

        function onEditFileContent(currentPath) {
            let name = newFileNameField.getValue();
            let currentFullPath = currentPath + "/" + name;

            let fileToSaveWithContent = {
                fileName: name,
                fullPath: currentFullPath,
                content: contentTextArea.getValue()
            };

            fileRestApi.change(fileToSaveWithContent).done(file => {
                currentFile.contents = file.contents;
                contentTextArea.setValue(file.contents);
            }).fail(() => modal.buildErrorWindow(texts.error.editFailed));
        }

        function onEditFile(currentPath, subFilesContainer, transformColumn) {
            let name = newFileNameField.getValue();
            if (!name) return;
            let currentFullPath = currentPath + "/" + name;
            let fileToSave = {
                src: currentFile.fullPath,
                target: currentFullPath,
            };


            fileRestApi.rename(fileToSave).done(newFile => {
                currentFile = newFile;

                $fileSourceRow.remove();
                $fileSourceRow = transformColumn(currentFile, fileEditor);
                subFilesContainer.append($fileSourceRow);
                $fileSourceRow.addClass('files-table__file-row--active');

            }).fail(() => modal.buildErrorWindow(texts.error.renameFailed));
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
        let contentTextArea = buildContentTextArea();
        let editCheckBox;

        function buildCreateField() {
            return components.texts.textField('<div>', {
                text: texts.title.createFileName
            });

        }

        function buildContentTextArea() {
            return components.texts.textAreaField('<div>', {
                id: 'content',
                text: texts.title.titleContent,
            })
        }

        function buildIsEditCheckBox() {
            return components.checkboxes.imcmsCheckbox('<div>', {
                text: texts.title.titleEditContent,
                change: setEnableEditContent
            })
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

                $fileSourceRow.addClass('files-table__file-row--active');
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

        let $fileSourceRow;

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
            copyFileLeft: copyFileLeft,
            displayDocs: buildDocumentsContainer,
            viewDoc: getViewDocById
        };

        return fileEditor;
    }
);