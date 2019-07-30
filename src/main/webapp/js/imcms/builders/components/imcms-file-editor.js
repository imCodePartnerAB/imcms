define(
    'imcms-file-editor',
    ['imcms-modal-window-builder', 'imcms-i18n-texts', 'imcms-bem-builder', 'imcms-components-builder',
        'imcms-files-rest-api', 'imcms-file-to-row-transformer', 'jquery', 'imcms-document-transformer',
        'imcms-template-groups-rest-api', 'imcms-super-admin-page-builder', 'imcms'],
    function (modal, texts, BEM, components, fileRestApi, fileToRow, $, docToRow, groupsRestApi) {

        texts = texts.superAdmin.files;

        const selectedFileHighlightingClassName = 'files-table__file-row--selected';
        const selectedDirHighlightingClassName = 'files-table__directory-row--selected';
        const newFileHighlightingClassName = 'files-table__file-row--active';

        let $fileSourceRow;
        let currentFile;
        let $documentsContainer;
        let $documentsData;
        let selectedFiles;
        let selectedFilesRows;

        initData();

        function getMainContainer() {
            return $('.imcms-form').find('.files-table');
        }

        function getSubFilesContainerByChildRow(childRow) {
            const parent = childRow.parent();

            if (parent.parent().hasClass(getMainContainer().attr('class'))) {
                return $(childRow.get(-1));
            }
            return getSubFilesContainerByChildRow(parent);
        }

        function getAllSubFilesContainers() {
            const columns = Array.from(getMainContainer().children());
            return columns
                .map(column => $(column))
                .flatMap(column => column.children().get(-1))
                .map(container => $(container))
                .filter(container => !container.hasClass('root-directory-row'));
        }

        function getSubFilesContainerByIndex(index) {
            return $(getAllSubFilesContainers()[index]);
        }

        function getPathFromElement(element) {
            return element.attr('path');
        }

        function getDirPathFromFullPath(path) {
            return path.substring(0, path.lastIndexOf('/'));
        }

        function getDirPathBySubFilesContainer(container) {
            return getPathFromElement(container);
        }

        function getDirPathBySubFilesContainerIndex(index) {
            return getDirPathBySubFilesContainer(getSubFilesContainerByIndex(index));
        }

        function createBackDir(currentPath) {
            return {
                fileName: '/..',
                fullPath: getDirPathFromFullPath(currentPath),
                fileType: 'DIRECTORY'
            }
        }

        function getIndexOfSubFilesContainer(container) {
            const containers = getAllSubFilesContainers();

            for (let i = 0; i < containers.length; i++) {
                if (container.attr('class') === $(containers[i]).attr('class')) {
                    return i;
                }
            }
            return -1;
        }

        function isDirsInSubFilesContainersEquals(container1, container2) {
            return container1.attr('path') === container2.attr('path');
        }

        function integrateFileInContainerAsRow(file, filesContainer, transformFileToRowFunction) {
            let row = transformFileToRowFunction(file, fileEditor);
            filesContainer.append(row);

            row.click(event => {
                if (!event.ctrlKey) {
                    selectedFilesRows = [];
                    selectedFiles = [];
                } else if (event.ctrlKey && $fileSourceRow.hasClass(selectedFileHighlightingClassName)) {
                    deleteHighlighting($fileSourceRow);
                    selectedFilesRows.pop($fileSourceRow);
                    selectedFiles.pop(currentFile);
                    return;
                }

                const allSubFilesContainers = getAllSubFilesContainers();

                allSubFilesContainers.forEach(container =>
                    deleteAllHighlightingInSubFilesContainer(container, newFileHighlightingClassName)
                );

                selectedFilesRows.push($fileSourceRow);
                selectedFiles.push(currentFile);

                allSubFilesContainers.forEach(container =>
                    updateHighlighting(container, selectedFilesRows)
                );
            });

            return row;
        }

        function updateHighlighting(container, rows) {
            deleteAllHighlightingInSubFilesContainer(container);
            rows.forEach(elem => addHighlighting(elem));
        }

        function deleteAllHighlightingInSubFilesContainer(container, highlightingClassName = selectedFileHighlightingClassName) {
            if (!container) {
                return;
            }
            const selector = '.' + container.attr('class') + ' .' + highlightingClassName;
            $(selector).removeClass(highlightingClassName);
        }

        function addHighlighting(elem) {
            const $elem = $(elem);

            if ($elem.hasClass(selectedFileHighlightingClassName)) {
                return;
            }
            $elem.addClass(selectedFileHighlightingClassName);
        }

        function deleteHighlighting(elem) {
            $(elem).removeClass(selectedFileHighlightingClassName);
        }

        function buildDocumentsContainer() {
            $documentsContainer = $('<div>', {
                'class': 'table-documents',
                'style': 'display: none;'
            });
            return $documentsContainer;
        }

        function initData() {
            groupsRestApi.read().done(templateGroups => {
                const mappedData = templateGroups.map(group => ({
                    text: group.name,
                    "data-value": group.id
                }));

                components.selects.addOptionsToSelect(mappedData, $templateGroupSelect);
            }).fail(() => modal.buildErrorWindow(texts.error.loadGroupsError));
        }

        const $templateGroupSelect = components.selects.imcmsSelect('<div>', {
            id: 'template-group',
            name: 'template-group',
            text: texts.groupData.title,
            emptySelect: true,
            change: onChangeTemplateGroup
        });

        function onChangeTemplateGroup() {
            $templateGroupNameTextField.setValue(1);
        }

        const $templateGroupNameTextField = components.texts.textBox('<div>', {});

        const $templateGroupDefaultButtons = components.buttons.buttonsContainer('<div>', [
            components.buttons.positiveButton({
                text: texts.groupData.edit,
                click: onEditTemplateGroup
            }),
            components.buttons.negativeButton({
                text: texts.groupData.delete,
                click: onDeleteTemplateGroup
            })
        ]);

        function onEditTemplateGroup() {
            $templateGroupDefaultButtons.slideUp();
            $templateGroupEditButtons.slideDown();

            $templateGroupNameTextField.$input.removeAttr('disabled').focus();
        }

        function onDeleteTemplateGroup() {
            modal.buildModalWindow(texts.deleteConfirm, confirmed => {
                if (!confirmed) return;

                // rolesRestAPI.remove(currentRole)
                //     .done(() => {
                //         $roleRow.remove();
                //         currentRole = null;
                //         onEditDelegate = onSimpleEdit;
                //         $container.slideUp();
                //     })
                //     .fail(() => modal.buildErrorWindow(texts.error.removeFailed));
            });
        }

        const $templateGroupEditButtons = components.buttons.buttonsContainer('<div>', [
            components.buttons.saveButton({
                text: texts.groupData.save,
                click: onSaveTemplateGroup
            }),
            components.buttons.negativeButton({
                text: texts.groupData.cancel,
                click: onCancelTemplateGroup
            })
        ], {
            style: 'display: none;'
        });

        function onSaveTemplateGroup() {

        }

        function onCancelTemplateGroup() {

        }

        function getTemplateGroupEditor() {
            $templateGroupNameTextField.$input.attr('disabled', 'disabled');

            return new BEM({
                block: 'group-editor',
                elements: {
                    // components.selects.addOptionsToSelect(rolesDataMapped, $userRoleSelect);
                    'template-group-select': $templateGroupSelect,
                    'template-group-name-row': $templateGroupNameTextField,
                    'template-group-default-buttons': $templateGroupDefaultButtons,
                    'template-group-edit-buttons': $templateGroupEditButtons
                }
            }).buildBlockStructure('<div>', {});
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

        function updateHighlightingForDir(elem) {
            elem.parent()
                .find('.' + selectedDirHighlightingClassName)
                .removeClass(selectedDirHighlightingClassName);
            elem.addClass(selectedDirHighlightingClassName);
        }

        function buildViewFilesContainer($fileRow, file, isDblClick) {
            currentFile = file;
            $fileSourceRow = $fileRow;
            if (file.fileType === 'DIRECTORY') {
                updateHighlightingForDir($fileSourceRow);
                deleteAllHighlightingInSubFilesContainer(getSubFilesContainerByChildRow($fileSourceRow), selectedDirHighlightingClassName);
            }
            if (file.fileType === 'DIRECTORY' && isDblClick) {
                onDirectoryDblClick.call(this, $fileRow, file);
            } else {
                onTemplateClick();
            }
        }

        function onDirectoryDblClick($fileRow, file) {
            getAllSubFilesContainers().forEach(container =>
                deleteAllHighlightingInSubFilesContainer(container)
            );
            selectedFilesRows = [];
            selectedFiles = [];

            const path = file.fullPath;

            fileRestApi.get(path).done(files => {
                    const $subFilesContainer = $('<div>', {
                        class: this.subFilesClassName,
                        path: path
                    });
                    const $filesContainer = $('.' + this.columnClassName);
                    $filesContainer.find('.' + this.subFilesClassName).remove();
                    $filesContainer.append($subFilesContainer);

                    const transformFileToRow = fileToRow.transformFileToRow.bind({
                        subFilesContainerIndex: getIndexOfSubFilesContainer($subFilesContainer)
                    });
                    $subFilesContainer.append(transformFileToRow(createBackDir(path), fileEditor));

                    files.forEach(file => integrateFileInContainerAsRow(file, $subFilesContainer, transformFileToRow));
                }
            ).fail(() => modal.buildErrorWindow(texts.error.loadError));
        }

        function onTemplateClick() {
            const isTemplate = new RegExp('.(JSP|HTML)$', 'gi');
            const templateName = {
                template: currentFile.fullPath
            };
            if (isTemplate.test(currentFile.fullPath)) {
                fileRestApi.getDocuments(templateName).done(documents => {
                        if (documents.length > 0) {
                            $documentsData = $('<div>').addClass('documents-data');
                            let documentsRows = documents.map(doc => docToRow.transform(doc, fileEditor));
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

        const newFileNameField = buildCreateField();
        const checkBoxIsDirectory = buildIsDirectoryCheckBox();
        const contentTextArea = buildContentTextArea();
        let editCheckBox;

        function setEnableEditContent() {
            const isImage = new RegExp('.(GIF|JPE?G|PNG|PDF)$', 'gi');
            contentTextArea.show();
            if (isImage.test(currentFile.fullPath) || checkBoxIsDirectory.isChecked()) {
                editCheckBox.$input.attr('disabled', 'disabled');
                contentTextArea.hide();
            }
            if (editCheckBox.isChecked()) {
                contentTextArea.$input.removeAttr('disabled');
                newFileNameField.$input.attr('disabled', 'disabled');
                checkBoxIsDirectory.$input.attr('disabled', 'disabled');
            } else {
                contentTextArea.$input.attr('disabled', 'disabled');
                newFileNameField.$input.removeAttr('disabled');
            }
        }

        function prepareOnEditFile(confirmEditFile) {
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

                confirmEditFile();
            });
        }

        function confirmEditFile(onRenameFile, onEditFileContent) {
            return modal.buildEditFileModalWindow(
                texts.editorFile, newFileNameField, checkBoxIsDirectory, contentTextArea, editCheckBox, confirmed => {
                    if (!confirmed && !editCheckBox.isChecked()) {
                        onRenameFile()
                    }
                    if (!confirmed && editCheckBox.isChecked()) {
                        onEditFileContent()
                    }
                });
        }

        function onEditFileContent() {
            let name = newFileNameField.getValue();
            let currentFullPath = this.getTargetDirectoryPath() + "/" + name;

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

        function onRenameFile() {
            const name = newFileNameField.getValue();
            if (!name) return;

            const targetSubFilesContainer = this.getTargetSubFilesContainer();
            const targetDirectoryPath = getDirPathBySubFilesContainer(targetSubFilesContainer);

            const targetPath = targetDirectoryPath + "/" + name;
            const fileToSave = {
                src: currentFile.fullPath,
                target: targetPath,
            };

            fileRestApi.rename(fileToSave).done(file => {
                currentFile = file;
                $fileSourceRow.remove();
                $fileSourceRow = integrateFileInContainerAsRow(file, targetSubFilesContainer, this.transformFileToRow);
            }).fail(() => modal.buildErrorWindow(texts.error.renameFailed));
        }

        function deleteFile() {
            modal.buildModalWindow(texts.warnDeleteMessage, confirmed => {
                if (!confirmed) return;

                let sourceFile = {
                    fileName: currentFile.fileName,
                    fullPath: currentFile.fullPath
                };

                fileRestApi.deleteFile(sourceFile).done(() => {
                    $documentsContainer.remove();
                        $fileSourceRow.remove();
                        currentFile = null;
                    }
                ).fail(() => modal.buildErrorWindow(texts.error.deleteFailed));
            });
        }

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

        function confirmAddFile(onConfirm) {
            newFileNameField.setValue('').$input.removeAttr('disabled');
            checkBoxIsDirectory.$input.removeAttr('disabled');
            return modal.buildCreateFileModalWindow(
                texts.createFile, newFileNameField, checkBoxIsDirectory, confirmed => {
                    if (!confirmed) {
                        onConfirm();
                    }
                });
        }

        function onSaveFile() {
            let name = newFileNameField.getValue();
            let isDirectory = checkBoxIsDirectory.isChecked();

            if (!name) return;

            const targetSubFilesContainer = this.getTargetSubFilesContainer();
            const targetDirectoryPath = getDirPathBySubFilesContainer(targetSubFilesContainer);
            const currentFullPath = targetDirectoryPath + "/" + name;

            let fileToSave = {
                fileName: name,
                fullPath: currentFullPath,
                fileType: isDirectory ? 'DIRECTORY' : 'FILE'
            };

            fileRestApi.create(fileToSave).done(newFile => {
                currentFile = newFile;
                $fileSourceRow = integrateFileInContainerAsRow(newFile, targetSubFilesContainer, this.transformFileToRow);
                $fileSourceRow.addClass(newFileHighlightingClassName);
            }).fail(() => modal.buildErrorWindow(texts.error.createError));
        }

        function uploadFile() {
            let $fileInput = $('<input>', {
                type: 'file',
                multiple: ''
            });
            $fileInput.click();

            $fileInput.change(() => {
                let formData = new FormData();

                let files = $fileInput.prop('files');
                Array.from(files).forEach(file => {
                    const fileName = file.name.split(' ');
                    formData.append(fileName.join('_'), file)
                });

                const targetSubFilesContainer = this.getTargetSubFilesContainer();
                const targetDirectoryPath = getDirPathBySubFilesContainer(targetSubFilesContainer);
                formData.append("targetDirectory", targetDirectoryPath);

                fileRestApi.upload(formData).done(uploadedFiles => {
                    uploadedFiles.forEach(file => {
                        currentFile = file;
                        $fileSourceRow = integrateFileInContainerAsRow(file, targetSubFilesContainer, this.transformFileToRow);
                        $fileSourceRow.addClass(newFileHighlightingClassName);
                    });
                }).fail(() => modal.buildErrorWindow(texts.error.uploadError));
            });
        }

        function moveFile() {
            const targetSubFilesContainer = this.getTargetSubFilesContainer();

            let paths = {
                src: selectedFiles.map(file => file.fullPath).toString(),
                target: getDirPathBySubFilesContainer(targetSubFilesContainer)
            };

            fileRestApi.move(paths).done(files => {
                selectedFiles = files;

                const allSubFilesContainers = getAllSubFilesContainers();
                if (isDirsInSubFilesContainersEquals(allSubFilesContainers[0], allSubFilesContainers[1])) {
                    selectedFilesRows = [];
                } else {
                    selectedFilesRows.forEach(row => row.remove());
                    selectedFilesRows = [];

                    files.forEach(file =>
                        selectedFilesRows.push(
                            integrateFileInContainerAsRow(file, targetSubFilesContainer, this.transformFileToRow)
                        )
                    );
                }

                allSubFilesContainers.forEach(container =>
                    updateHighlighting(container, selectedFilesRows)
                );
            }).fail(() => modal.buildErrorWindow(texts.error.moveError));
        }

        function copyFile() {
            const targetSubFilesContainer = this.getTargetSubFilesContainer();

            let paths = {
                src: selectedFiles.map(file => file.fullPath).toString(),
                target: getDirPathBySubFilesContainer(targetSubFilesContainer)
            };

            fileRestApi.copy(paths).done(newFiles => {
                selectedFiles = newFiles;
                selectedFilesRows = [];

                const allSubFilesContainers = getAllSubFilesContainers();
                if (!isDirsInSubFilesContainersEquals(allSubFilesContainers[0], allSubFilesContainers[1])) {
                    newFiles.forEach(file =>
                        selectedFilesRows.push(
                            integrateFileInContainerAsRow(file, targetSubFilesContainer, this.transformFileToRow)
                        )
                    );
                }

                allSubFilesContainers.forEach(container =>
                    updateHighlighting(container, selectedFilesRows)
                );
            }).fail(() => modal.buildErrorWindow(texts.error.copyError));
        }

        function buildBoundData(targetSubFilesContainerIndex) {
            return {
                getTargetSubFilesContainer: () => getSubFilesContainerByIndex(targetSubFilesContainerIndex),
                transformFileToRow: buildTransformFileToRow(targetSubFilesContainerIndex)
            }
        }

        function buildTransformFileToRow(subFilesContainerIndex) {
            return fileToRow.transformFileToRow.bind({subFilesContainerIndex});
        }

        function buildViewSubFilesContainer(subFilesContainerIndex) {
            const classes = {
                subFilesClassName: subFilesContainerIndex === 0 ? 'first-sub-files' : 'second-sub-files',
                columnClassName: subFilesContainerIndex === 0 ? 'files-table__first-instance' : 'files-table__second-instance'
            };
            return buildViewFilesContainer.bind(classes);
        }

        function buildUploadFile(subFilesContainerIndex) {
            return uploadFile.bind(buildBoundData(subFilesContainerIndex))
        }

        function buildAddFile(subFilesContainerIndex) {
            const saveFile = onSaveFile.bind(buildBoundData(subFilesContainerIndex));
            return () => confirmAddFile(saveFile);
        }

        function buildEditFile(subFilesContainerIndex) {
            const renameFile = onRenameFile.bind(buildBoundData(subFilesContainerIndex));
            const editFileContent = onEditFileContent.bind({
                getTargetDirectoryPath: () => getDirPathBySubFilesContainerIndex(subFilesContainerIndex)
            });
            const buildConfirmEditIn = () => confirmEditFile(renameFile, editFileContent);
            return () => prepareOnEditFile(buildConfirmEditIn);
        }

        function buildMoveFile(subFilesContainerIndex) {
            const index = subFilesContainerIndex === 0 ? 1 : 0;
            return moveFile.bind(buildBoundData(index));
        }

        function buildCopyFile(subFilesContainerIndex) {
            const index = subFilesContainerIndex === 0 ? 1 : 0;
            return copyFile.bind(buildBoundData(index));
        }

        const fileEditor = {
            buildViewSubFilesContainer,
            buildUploadFile,
            buildAddFile,
            buildEditFile,
            buildMoveFile,
            buildCopyFile,
            deleteFile,
            getTemplateGroupEditor,
            displayDocs: buildDocumentsContainer,
            viewDoc: getViewDocById
        };

        return fileEditor;
    }
);