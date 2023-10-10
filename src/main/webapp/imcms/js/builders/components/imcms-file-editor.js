define(
    'imcms-file-editor',
    ['imcms-modal-window-builder', 'imcms-i18n-texts', 'imcms-bem-builder', 'imcms-components-builder',
        'imcms-files-rest-api', 'imcms-file-to-row-transformer', 'jquery', 'imcms-document-transformer',
        'imcms-template-groups-rest-api', "imcms-templates-rest-api", 'imcms', 'imcms-super-admin-page-builder'],
    function (modal, texts, BEM, components, fileRestApi, fileToRow, $, docToRow, groupsRestApi, templatesRestApi, imcms) {

        texts = texts.superAdmin.files;

        const CONTEXT_URL = '/api/files/file/';
        const FILE_SRC_URL = imcms.contextPath + CONTEXT_URL;
        const TEMPLATE_ROOT_PATH = '/WEB-INF/templates/text';

        const selectedFileHighlightingClassName = 'files-table__file-row--selected';
        const selectedDirHighlightingClassName = 'files-table__directory-row--selected';
        const newFileHighlightingClassName = 'files-table__file-row--active';

        let $fileSourceRow;
        let currentFile;
        let $documentsContainer;
        let selectedFiles;
        let selectedFilesRows;
        let currentFolder;
        let currentFileEditor;

        initData();

        function getMainContainer() {
            return $('.imcms-form').find('.files-table');
        }

        function getAllSubFilesContainers() {
            const columns = Array.from(getMainContainer().children());
            return columns
                .map(column => $(column))
                .flatMap(column => column.children().get(-1))
                .map(container => $(container));
        }

        function getSubFilesContainerByIndex(index) {
            return $(getAllSubFilesContainers()[index]);
        }

        function getPathRowByIndex(index) {
            const $column = $(getMainContainer().children()[index]);
            return $column.find('.path-row');
        }

        function getDirPathFromFullPath(path) {
            return path.substring(0, path.lastIndexOf('/'));
        }

        function getDirPathBySubFilesContainer(container) {
            const index = getIndexOfSubFilesContainer(container);
            return getDirPathByIndex(index);
        }

        function getDirPathByIndex(index) {
            return getPathRowByIndex(index).find('.path-row__path').attr('full-path');
        }

        function isRootDir(physicalPath){
            return physicalPath.lastIndexOf("/") > 0;
        }

        function createBackDir(fullPath, physicalPath) {
            return {
                fileName: '../',
                fullPath: getDirPathFromFullPath(fullPath),
                physicalPath: getDirPathFromFullPath(physicalPath),
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

        const $loadingAnimation = $('<div>', {
            class: 'loading-animation',
            style: 'display: none'
        });

        const $docsNumberLabel = $('<div>', {
            class: 'imcms-label',
            style: 'display: none',
        });

        function buildDocumentsContainer() {
            $documentsContainer = new BEM({
                block: 'table-documents',
                elements: {
                    'loading-animation': $loadingAnimation,
                    'docs-number': $docsNumberLabel,
                }
            }).buildBlockStructure('<div>');

            return $documentsContainer;
        }

        function setEnabledEditMode(isEnabled) {
            if (isEnabled) {
                $templateGroupDefaultButtons.slideUp();
                $templateGroupEditButtons.slideDown();

                $templateGroupSelect.hide();
                $templateGroupNameTextField.show();
            } else {
                $templateGroupDefaultButtons.slideDown();
                $templateGroupEditButtons.slideUp();

                $templateGroupNameTextField.hide();
                $templateGroupSelect.show();

                $templatesTableTitle.show();
                $templatesTable.show();
            }
        }

        function initData() {
            groupsRestApi.read().done(templateGroups => {
                const mappedData = templateGroups.map(group => mapGroupToOption(group));

                components.selects.addOptionsToSelect(mappedData, $templateGroupSelect, onSelectTemplateGroup);
            }).fail(() => modal.buildErrorWindow(texts.error.loadGroups));
        }

        function mapGroupToOption(group) {
            return {
                text: group.name,
                'data-value': group.id
            };
        }

        const $templateGroupCreateButton = components.buttons.positiveButton({
            text: texts.groupData.create,
            click: onCreateTemplateGroup
        });

        let selectedValueBeforeSaving;

        function onCreateTemplateGroup() {
            $templateGroupNameTextField.attr('mode', 'create');
            $templateGroupNameTextField.setValue('');

            setEnabledEditMode(true);
            $templateGroupNameTextField.slideDown();
            $templatesTableTitle.hide();
            $templatesTable.hide();

            selectedValueBeforeSaving = $templateGroupSelect.getSelectedValue();
        }

        const $templateGroupTitle = $('<div>', {
            text: texts.groupData.title,
            class: 'imcms-label'
        });

        const $templateGroupSelect = components.selects.imcmsSelect('<div>', {
            id: 'template-group',
            name: 'template-group'
        });

        function onSelectTemplateGroup() {
            if ($templateGroupEditButtons.css('display') !== 'none') {
                onCancelTemplateGroup();
            } else {
                const templateName = $templateGroupSelect.selectedText();
                fillTemplatesTableByTemplateGroup(templateName);
                $templateGroupNameTextField.setValue(templateName);
                $templateGroupDefaultButtons.slideDown();
            }
        }

        const $templateGroupNameTextField = components.texts.textField('<div>', {});

        const $templateGroupDefaultButtons = components.buttons.buttonsContainer('<div>', [
            components.buttons.errorButton({
                text: texts.groupData.delete,
                click: onDeleteTemplateGroup
            }),
            components.buttons.positiveButton({
                text: texts.groupData.edit,
                click: onEditTemplateGroup
            }),
        ], {
            style: 'display: none'
        });

        function onEditTemplateGroup() {
            $templateGroupNameTextField.attr('mode', 'edit');
            setEnabledEditMode(true);

            selectedValueBeforeSaving = $templateGroupSelect.getSelectedValue()
        }

        function onDeleteTemplateGroup() {
            if (!$templateGroupSelect.getSelectedValue()) {
                return;
            }

            modal.buildModalWindow(texts.groupData.deleteConfirm, confirmed => {
                if (!confirmed) return;

                const id = $templateGroupSelect.getSelectedValue();

                groupsRestApi.remove(id).done(() => {
                    $templateGroupSelect.deleteOption(id);
                    $templateGroupSelect.selectFirst();
                    setEnabledEditMode(false);
                    $templatesTableTitle.hide();
                    $templatesTable.hide();
                    $templateGroupDefaultButtons.slideUp();
                }).fail(() => modal.buildErrorWindow(texts.error.deleteGroup));
            });
        }

        const $templateGroupEditButtons = components.buttons.buttonsContainer('<div>', [
            components.buttons.negativeButton({
                text: texts.groupData.cancel,
                click: onCancelTemplateGroup
            }),
            components.buttons.saveButton({
                text: texts.groupData.save,
                click: onSaveTemplateGroup
            }),
        ], {
            style: 'display: none;'
        });

        function onSaveTemplateGroup() {
            if (!$templateGroupNameTextField.getValue()) {
                return;
            }

            modal.buildModalWindow(texts.groupData.saveConfirm, confirmed => {
                if (!confirmed) return;

                if ($templateGroupNameTextField.attr('mode') === 'edit') {
                    editTemplateGroup();
                } else if ($templateGroupNameTextField.attr('mode') === 'create') {
                    createTemplateGroup();
                }
            });
        }

        function editTemplateGroup() {
            const editedGroup = {
                id: $templateGroupSelect.getSelectedValue(),
                name: $templateGroupNameTextField.getValue()
            };

            groupsRestApi.replace(editedGroup).done(group => {
                $templateGroupSelect.deleteOption($templateGroupSelect.getSelectedValue());
                components.selects.addOptionsToSelect([mapGroupToOption(group)], $templateGroupSelect, onSelectTemplateGroup);
                $templateGroupSelect.selectValue(group.id);

                setEnabledEditMode(false);
            }).fail(() => modal.buildErrorWindow(texts.error.editGroup));
        }

        function createTemplateGroup() {
            const created = {
                name: $templateGroupNameTextField.getValue()
            };

            groupsRestApi.create(created).done(group => {
                components.selects.addOptionsToSelect([mapGroupToOption(group)], $templateGroupSelect, onSelectTemplateGroup);
                $templateGroupSelect.selectValue(group.id);

                setEnabledEditMode(false);
            }).fail(() => modal.buildErrorWindow(texts.error.createGroup));
        }

        function onCancelTemplateGroup() {
            modal.buildModalWindow(texts.groupData.cancelConfirm, confirmed => {
                if (!confirmed) {
                    $templateGroupSelect.selectValue(selectedValueBeforeSaving);
                    return;
                }

                setEnabledEditMode(false);
            });
        }

        const $templatesTableTitle = $('<div>', {
            text: texts.groupData.templatesTableTitle,
            class: 'imcms-label',
            style: 'display: none'
        });

        const $templatesTable = $('<div>', {
            class: 'templates-data',
            style: 'display: none;'
        });

        function fillTemplatesTableByTemplateGroup(groupName) {
            groupsRestApi.get(groupName).done(group => {
                $templatesTableTitle.show();
                $templatesTable.show();
                $templatesTable.children().remove();

                group.templates.sort((template1, template2) => {
                    if (template1.name < template2.name)
                        return -1;
                    if (template1.name > template2.name)
                        return 1;
                    return 0;
                })
                    .forEach(template => $templatesTable.append(templateToRow(template)));

            }).fail(() => modal.buildErrorWindow(texts.error.loadGroup));
        }

        function templateToRow(template) {
            const $removeButton = components.controls
                .remove(onClickDeleteTemplateFromGroup.bind({template}))
                .attr("title", texts.title.delete);

            return new BEM({
                block: 'template-info-row',
                elements: {
                    'template-name': $('<div>').text(template.name),
                    'delete': $removeButton
                }
            }).buildBlockStructure('<div>');
        }

        function deleteTemplate(id) {
            templatesRestApi.delete(id).done(() => {
                fillTemplatesTableByTemplateGroup($templateGroupSelect.selectedText());
            }).fail(() => modal.buildErrorWindow(texts.error.deleteTemplate));
        }

        function onClickDeleteTemplateFromGroup(){

            const templateGroupId = $templateGroupSelect.getSelectedValue();

            const transferData = {
                templateGroupId,
                templateName: this.template.name,
            };

            groupsRestApi.deleteTemplate(transferData).done(() => {
                fillTemplatesTableByTemplateGroup($templateGroupSelect.selectedText());
            }).fail(() => modal.buildErrorWindow(texts.error.deleteGroupFromTemplate));
        }

        function onClickDeleteTemplate(templatePath, sourceFile) {
            const templateName = {
                template: templatePath
            };
            fileRestApi.getDocuments(templateName).done(documents => {
                if (documents.length > 0) {
                    confirmDeleteTemplate(() => showDeleteTemplateModalWindow(sourceFile));
                }else{
                    deleteFileRequest(sourceFile);
                }
            }).fail(() => modal.buildErrorWindow(texts.error.loadDocError));
        }

        function confirmDeleteTemplate(onConfirm) {
            modal.buildModalWindow(texts.template.boundDocumentsWarn, confirmed => {
                if (confirmed) {
                    onConfirm();
                }
            });
        }

        function showDeleteTemplateModalWindow(sourceFile) {
            templatesRestApi.read().done(templates => {

                if(templates.length < 2){
                    modal.buildWarningWindow(texts.error.noOtherTemplates);
                    return;
                }

                //to find the needed a template object
                let fileTemplateName = sourceFile.fileName;
                let i = getIndexOfTemplateInArrayByName(templates, fileTemplateName.substring(0, fileTemplateName.lastIndexOf('.')));       //to delete an extension
                let template = templates[i];

                deleteTemplateFromArrayById(templates, template.id);
                const templatesRadioButtons = templates
                    .map(template => components.radios.imcmsRadio("<div>", {
                        text: template.name,
                        name: 'template',
                        value: template.id,
                    }));

                const templatesRadioButtonsGroup = components.radios.group.apply(null, templatesRadioButtons);

                const $templatesRadioButtonsContainer = components.radios.radioContainer('<div>', templatesRadioButtons, {
                    class: 'templates-radios-container'
                });

                templatesRadioButtons[0].setChecked(true);

                modal.buildOptionalModalWindow(texts.title.replaceTemplate, $templatesRadioButtonsContainer, confirmed => {
                    if (confirmed) {
                        const transferData = {
                            oldTemplate: template.name,
                            newTemplate: getTemplateFromArrayById(templates, templatesRadioButtonsGroup.getCheckedValue()).name
                        };

                        templatesRestApi.replaceOnDoc(transferData).done(() => {
                            changeTemplateAmountOfDocuments(transferData.oldTemplate, -1);
                            changeTemplateAmountOfDocuments(transferData.newTemplate, 1);

                            deleteFileRequest(sourceFile);
                        }).fail(() => modal.buildErrorWindow(texts.error.replaceTemplate));
                    }
                });
            }).fail(() => modal.buildErrorWindow(texts.error.loadTemplates));
        }

        function changeTemplateAmountOfDocuments(name, number){
            let $amount = $fileSourceRow.parent().find("[name='" + name + "']").find(".file-row__amount-docs");
            let amount = Number.parseInt($amount.attr("amount")) + number;
            $amount.attr("amount", amount);
            $amount.text("[" + amount + "]");
        }

        function changeCountFiles(index, number){
            const $pathRowCount = getPathRowByIndex(index).find(".path-row__count");
            const count = Number.parseInt($pathRowCount.attr("count")) + number;
            $pathRowCount.attr("count", count);
            $pathRowCount.text("[" + count + "]");
        }

        function getIndexOfTemplateInArrayById(array, id) {
            return array.findIndex(template => template.id == id);
        }

        function getIndexOfTemplateInArrayByName(array, name) {
            return array.findIndex(template => template.name == name);
        }

        function getTemplateFromArrayById(array, id) {
            return array[getIndexOfTemplateInArrayById(array, id)];
        }

        function deleteTemplateFromArrayById(array, id) {
            array.splice(getIndexOfTemplateInArrayById(array, id), 1);
            return array;
        }

        const $templateGroupEditor = new BEM({
            block: 'group-editor',
            elements: {
                'create-button': $templateGroupCreateButton,
                'template-group-title': $templateGroupTitle,
                'select': $templateGroupSelect,
                'name-row': $templateGroupNameTextField,
                'default-buttons': $templateGroupDefaultButtons,
                'edit-buttons': $templateGroupEditButtons,
                'templates-data-title': $templatesTableTitle,
                'templates-data': $templatesTable
            }
        }).buildBlockStructure('<div>', {});

        function getTemplateGroupEditor() {
            $templateGroupNameTextField.css('display', 'none');

            $templateGroupEditor.hide();

            return $templateGroupEditor;
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

        function updateHighlightingForDir(elem) {
            elem.parent()
                .find('.' + selectedDirHighlightingClassName)
                .removeClass(selectedDirHighlightingClassName);
            elem.addClass(selectedDirHighlightingClassName);
        }

        function buildViewFilesContainer($fileRow, file, isDblClick) {
            currentFile = file;
            $fileSourceRow = $fileRow;

            if (file.fileType === 'DIRECTORY' && isDblClick) {
                updateHighlightingForDir($fileSourceRow);

                onDirectoryDblClick.call(this, $fileRow, file);
            } else if (file.fileType === 'FILE' && isDblClick) {
                onFileDblClick(file);
            } else if (file.fileType === 'FILE' && isTemplate(file)) {
                onTemplateClick(file);
            }
        }

        function onDirectoryDblClick($fileRow, file, callback) {
            currentFileEditor = this;
            currentFolder = file;
            getAllSubFilesContainers().forEach(container =>
                deleteAllHighlightingInSubFilesContainer(container)
            );
            selectedFilesRows = [];
            selectedFiles = [];

            const path = file.fullPath;

            fileRestApi.get(path).done(files => {
                const $subFilesContainer = $('<div>').addClass(this.subFilesClassName);
                const $filesContainer = $('.' + this.columnClassName);
                $filesContainer.find('.' + this.subFilesClassName).remove();
                $filesContainer.append($subFilesContainer);

                const index = getIndexOfSubFilesContainer($subFilesContainer);

                const $pathRow = getPathRowByIndex(index);

                const $pathRowPath = $pathRow.find(".path-row__path");
                $pathRowPath.text(file.physicalPath);
                $pathRowPath.attr("full-path", path);

                const $pathRowAmount = $pathRow.find(".path-row__count");
                $pathRowAmount.text("[" + files.length + "]");
                $pathRowAmount.attr("count", files.length);

                const dirPath1 = getDirPathByIndex(0);
                const dirPath2 = getDirPathByIndex(1);
                if(dirPath1 && dirPath1.endsWith(TEMPLATE_ROOT_PATH) || (dirPath2 && dirPath2.endsWith(TEMPLATE_ROOT_PATH))) {
                    $templateGroupEditor.show();

                    $('#move-copy-files').hide();
                } else {
                    $templateGroupEditor.hide();
                    $docsNumberLabel.hide();
                    $documentsContainer.find('.documents-data').remove();

                    $('#move-copy-files').show();
                }

                const transformFileToRow = fileToRow.transformFileToRow.bind({subFilesContainerIndex: index});

                if(isRootDir(file.physicalPath)) $subFilesContainer.append(transformFileToRow(createBackDir(path, file.physicalPath), fileEditor));

                 files.sort((file1, file2) =>{
                     if(file1.editable === file2.editable) return 0;
                     return file1.editable ? -1 : 1;
                 }).forEach(file => integrateFileInContainerAsRow(file, $subFilesContainer, transformFileToRow));

                callback ? callback() : '';
                }
            ).fail(() => modal.buildErrorWindow(texts.error.loadError));
        }

        function onFileDblClick(file) {
            const fileName = file.fileName;

            if (isImage(fileName)) {
                const $image = fileToImgElement(file);
                const $imageContainer = $('<div>');
                $imageContainer.append($image);

                modal.buildViewModalWindow($imageContainer);
            } else if (isTextFormat(fileName)) {
                const $textViewBox = fileToTextView(file);

                modal.buildViewModalWindow($textViewBox);
            } else {
                downloadFile(file);
            }
        }

        function isImage(fileName) {
            const pattern = new RegExp('.(GIF|JPE?G|PNG|SVG)$', 'gi');
            return pattern.test(fileName);
        }

        function isTextFormat(fileName) {
            const pattern = new RegExp('.(HTML?|CSS|JS|VBS|TXT|INC|JSP|ASP|FRAG|LOG)$', 'gi');
            return pattern.test(fileName);
        }

        function isTemplate(file) {
            const pattern = new RegExp('.(JSP|HTML)$', 'gi');
            return pattern.test(file.fileName) && file.physicalPath.startsWith(TEMPLATE_ROOT_PATH);
        }

        function fileToImgElement(file) {
            return $('<img>').attr('src', FILE_SRC_URL + file.fullPath);
        }

        function fileToTextView(file) {
            const $textArea = components.texts.textAreaField('<div>', {readonly: 'readonly'});
            const pathFile = {
                path: file.fullPath
            };
            fileRestApi.getFile(pathFile).done(file => {
                $textArea.addClass('text-preview');
                $textArea.setValue(decodeURIComponent(escape(window.atob(file.contents))));
            }).fail(() => modal.buildErrorWindow(texts.error.loadFileError));
            return $textArea;
        }

        function downloadFile(file) {
            window.location.replace(FILE_SRC_URL + file.fullPath);
        }

        function onTemplateClick(file) {
            $documentsContainer.find('.documents-data').remove();
            $loadingAnimation.show();

            const templateName = {
                template: file.fullPath
            };

            fileRestApi.getDocuments(templateName).done(documents => {
                    $documentsContainer.find('.documents-data').remove();

                    if (documents.length > 0) {
                        $docsNumberLabel.show();
                        $docsNumberLabel.text(texts.documentData.docsNumber + documents.length);

                        const $documentsData = $('<div>').addClass('documents-data');

                        const documentsRows = documents.map(doc => docToRow.transform(doc, fileEditor));
                        $documentsData.append(documentsRows);

                        $documentsContainer.append($documentsData).show();
                    } else {
                        $docsNumberLabel.hide();
                    }

                $loadingAnimation.hide();
                }
            ).fail(() => modal.buildErrorWindow(texts.error.loadDocError));
        }

        let newFileNameField;
        const editDirectoryNameField = buildFileNameField(texts.title.directoryName);
        const editFileNameField = buildFileNameField(texts.title.fileName);
        const checkBoxIsDirectory = buildIsDirectoryCheckBox();
        const contentTextArea = buildContentTextArea();
        let editCheckBox;

        function setEnableEditContent() {
            contentTextArea.show();

            const isEditable = new RegExp('.(GIF|JPE?G|PNG|PDF|MP4)$', 'gi');
            if (isEditable.test(currentFile.fullPath) || checkBoxIsDirectory.isChecked()) {
                editCheckBox.$input.attr('disabled', 'disabled');
                editCheckBox.hide();
                contentTextArea.hide();
            }
            if (editCheckBox.isChecked()) {
                contentTextArea.$input.removeAttr('disabled');
                editFileNameField.$input.attr('disabled', 'disabled');
                checkBoxIsDirectory.$input.attr('disabled', 'disabled');
            } else {
                contentTextArea.$input.attr('disabled', 'disabled');
                editFileNameField.$input.removeAttr('disabled');
            }
        }

        function prepareOnEditFile(confirmEditFile) {
                editCheckBox = buildIsEditCheckBox();

                if (currentFile.fileType === 'FILE') {
                    editFileNameField.setValue(currentFile.fileName);
                    setEnableEditContent();
                } else {
                    editDirectoryNameField.setValue(currentFile.fileName);
                }

                const pathFile = {
                    path: currentFile.fullPath
                };
                fileRestApi.getFile(pathFile).done(file => {
                    contentTextArea.setValue(decodeURIComponent(escape(window.atob(file.contents))));
                }).fail(() => modal.buildErrorWindow(texts.error.loadFileError));

                confirmEditFile();
        }

        function confirmEdit(onRenameFile, onEditFileContent) {
            if (currentFile.fileType === 'DIRECTORY') {
                return modal.buildEditDirectoryModalWindow(editDirectoryNameField, confirmed => {
                    if (confirmed) {
                        onRenameFile();
                    }
                });
            } else {
                const modalBody = isImage(currentFile.fileName) && !new RegExp('.(SVG)$', 'gi').test(currentFile.fileName)
                    ? buildImageContent() : contentTextArea;
                return modal.buildEditFileModalWindow(editFileNameField, modalBody, editCheckBox, confirmed => {
                    if (confirmed && !editCheckBox.isChecked()) {
                        onRenameFile()
                    }
                    if (confirmed && editCheckBox.isChecked()) {
                        onEditFileContent()
                    }
                });
            }
        }

        function onEditFileContent() {
            const name = currentFile.fileType === 'FILE' ? editFileNameField.getValue() : editDirectoryNameField.getValue();
            const currentFullPath = this.getTargetDirectoryPath() + "/" + name;

            const fileToSaveWithContent = new FormData();
            fileToSaveWithContent.append("filename", name);
            fileToSaveWithContent.append("fullPath", currentFullPath);
            fileToSaveWithContent.append("content", contentTextArea.getValue());

            fileRestApi.change(fileToSaveWithContent).done(file => {
                currentFile.contents = file.contents;
                contentTextArea.setValue(file.contents);

                if ($templatesTable.css('display') !== 'none') {
                    fillTemplatesTableByTemplateGroup($templateGroupSelect.selectedText());
                }
            }).fail(() => modal.buildErrorWindow(texts.error.editFailed));
        }

        function onRenameFile() {
            const name = currentFile.fileType === 'FILE'
                ? editFileNameField.getValue()
                : editDirectoryNameField.getValue();

            if (!name) return;

            const targetSubFilesContainer = this.getTargetSubFilesContainer();

            const fileToSave = {
                src: currentFile.fullPath,
                newName: name,
            };

            fileRestApi.rename(fileToSave).done(file => {
                currentFile = file;
                $fileSourceRow.remove();
                $fileSourceRow = integrateFileInContainerAsRow(file, targetSubFilesContainer, this.transformFileToRow);
            }).fail(() => modal.buildErrorWindow(texts.error.renameFailed));
        }

        function deleteFile(file) {
            modal.buildModalWindow(texts.warnDeleteMessage, confirmed => {
                if (!confirmed) return;

                let sourceFile = {
                    fileName: file.fileName,
                    fullPath: file.fullPath
                };

                if(isTemplate(file)){
                    onClickDeleteTemplate(file.physicalPath, sourceFile);
                    return;
                }

                deleteFileRequest(sourceFile);
            });
        }

        function deleteFileRequest(sourceFile){
            fileRestApi.deleteFile(sourceFile).done(() => {

                const index = $fileSourceRow.parent().hasClass('first-sub-files') ? 0 : 1;
                changeCountFiles(index, -1);

                if ($templatesTable.css('display') !== 'none') {
                    fillTemplatesTableByTemplateGroup($templateGroupSelect.selectedText());
                }

                $documentsContainer.hide();
                $fileSourceRow.remove();
            }).fail(() => modal.buildErrorWindow(texts.error.deleteFailed));
        }

        function buildFileNameField(label) {
            return components.texts.textField('<div>', {
                text: label
            });

        }

        function buildContentTextArea() {
            return components.texts.textAreaField('<div>', {
                id: 'content',
                text: texts.title.titleContent,
            })
        }

        function buildImageContent(){
            const $imageContent = $('<div>');

            const $image = fileToImgElement(currentFile);
            const $imageContainer = $('<div>', {class: 'edit-image-file'});
            $imageContainer.append($image);

            $imageContent.append($imageContainer);
            $image.load(function() {
                const infoText = components.texts.titleText("<div>", `(${$(this).width()} x ${$(this).height()})  ${currentFile.size}  `);
                infoText.append(components.texts.infoText('<a>', currentFile.physicalPath, {
                    href: currentFile.physicalPath,
                    target: '_blank',
                    style: 'text-decoration: underline'})
                );

                $imageContent.prepend(infoText);
            });

            return $imageContent;
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

        function confirmAddFile(currentPath, onConfirm) {
            checkBoxIsDirectory.$input.removeAttr('disabled');

            let title;
            let checkBox;
            if(currentPath.endsWith(TEMPLATE_ROOT_PATH)){
                newFileNameField = buildFileNameField(texts.title.createFileName);
                checkBox = "";
                title = texts.title.createFile;
            }else{
                newFileNameField = buildFileNameField(texts.title.createFileOrDirectoryName);
                checkBox = checkBoxIsDirectory;
                title = texts.title.createFileOrDirectory;
            }

            return modal.buildCreateFileModalWindow(
                title, newFileNameField, checkBox, confirmed => {
                    if (confirmed) {
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

            fileRestApi.exists({path: currentFullPath})
                .done(fileExists => {
                    if (fileExists) {
                        modal.buildErrorWindow(texts.error.fileAlreadyExists);
                        return;
                    }

                    fileRestApi.create(fileToSave).done(newFile => {
                        changeCountFiles(this.targetSubFilesContainerIndex, 1);

                        currentFile = newFile;
                        $fileSourceRow = integrateFileInContainerAsRow(newFile, targetSubFilesContainer, this.transformFileToRow);
                        $fileSourceRow.addClass(newFileHighlightingClassName);
                    }).fail(() => modal.buildErrorWindow(texts.error.createError));
                })
        }

        function uploadFile() {
            let $fileInput = $('<input>', {
                type: 'file',
                multiple: '',
                accept: ".jsp, .jspx, .html, .css, .js, .txt, .pdf, .mp4, image/*"
            });
            $fileInput.click();

            $fileInput.change(() => {
                let formData = new FormData();

                let files =  Array.from($fileInput.prop('files'));
                files.forEach(file => {
                    const fileName = file.name.split(' ');
                    formData.append(fileName.join('_'), file)
                });

                const targetSubFilesContainer = this.getTargetSubFilesContainer();
                const targetDirectoryPath = getDirPathBySubFilesContainer(targetSubFilesContainer);
                formData.append("targetDirectory", targetDirectoryPath);

                const paths = files.map(file => targetDirectoryPath + '/' + file.name).join(",");
                fileRestApi.existsAll({paths: paths})
                    .done(files => {
                        if (files.length) {
                            modal.buildWarningWindow(texts.error.duplicateFiles, () => {
                                const duplicateFileResolver = new DuplicateFileResolver(files);
                                duplicateFileResolver.resolveForUpload(formData)
                                    .then(uploadFiles)
                                    .catch(console.error);
                            });
                        } else {
                            uploadFiles(formData);
                        }
                    });
            });
        }

        function uploadFiles(formData) {
            // because 1 param always there
            if (Array.from(formData.keys()).length === 1) return;

            fileRestApi.upload(formData).done(uploadedFiles => {
                refreshCurrentFolder(() => highlightNewFiles(uploadedFiles));
            }).fail((response) => {
                modal.buildErrorWindow(texts.error.uploadError)
            });
        }

        function moveFile() {
            const targetSubFilesContainer = this.getTargetSubFilesContainer();
            const targetDirectory=getDirPathBySubFilesContainer(targetSubFilesContainer);

            let paths = {
                src: selectedFiles.map(file => file.fullPath).toString(),
                target:targetDirectory
            };

            const pathsToCheck = selectedFiles.map(file => targetDirectory + '/' + file.fileName).join(",");
            fileRestApi.existsAll({paths: pathsToCheck})
                .done(files => {
                    if (files.length) {
                        modal.buildWarningWindow(texts.error.duplicateFiles, callback => {
                            new DuplicateFileResolver(selectedFiles)
                                .resolveForMove(targetDirectory)
                                .then(modifiedFiles => {
                                    if (modifiedFiles.length) {
                                        paths.src = modifiedFiles.map(file => file.fullPath).toString();
                                        moveFiles.call(this, paths, targetSubFilesContainer)
                                    }
                                })
                        })
                    } else {
                        moveFiles.call(this, paths, targetSubFilesContainer);
                    }
                })
        }

        function moveFiles(paths, targetSubFilesContainer) {
            fileRestApi.move(paths).done(files => {
                changeCountFiles(this.targetSubFilesContainerIndex, files.length);
                changeCountFiles(this.targetSubFilesContainerIndex === 0 ? 1 : 0, 0-files.length);

                selectedFiles = files;

                const allSubFilesContainers = getAllSubFilesContainers();
                if (getDirPathByIndex(0) === getDirPathByIndex(1)) {
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
            const targetDirectory = getDirPathBySubFilesContainer(targetSubFilesContainer);

            let paths = {
                src: selectedFiles.map(file => file.fullPath).toString(),
                target: targetDirectory
            };

            const pathsToCheck = selectedFiles.map(file => targetDirectory + '/' + file.fileName).join(",");
            fileRestApi.existsAll({paths: pathsToCheck})
                .done(files => {

                    if (files.length) {
                        modal.buildWarningWindow(texts.error.duplicateFiles, callback => {
                            new DuplicateFileResolver(selectedFiles)
                                .resolveForCopy(targetDirectory)
                                .then(modifiedFiles => {
                                    if (modifiedFiles.length) {
                                        paths.src = modifiedFiles.map(file => file.fullPath).toString();
                                        copyFiles.call(this, paths, targetSubFilesContainer)
                                    }
                                })
                        })
                    } else {
                        copyFiles.call(this, paths, targetSubFilesContainer);
                    }
                })
        }

        function copyFiles(paths, targetSubFilesContainer) {
            fileRestApi.copy(paths).done(newFiles => {
                refreshCurrentFolder(() => highlightNewFiles(newFiles));
            }).fail(() => modal.buildErrorWindow(texts.error.copyError));
        }

        function highlightNewFiles(files) {
            const $currentSubFiles = $("." + currentFileEditor.subFilesClassName).children();

            files.forEach(file => {
                $currentSubFiles.filter((index, fileRow) => {
                    const fileRowFilename = $(fileRow).find(".file-row__file-name").text();
                    return fileRowFilename === file.fileName;
                }).addClass(newFileHighlightingClassName);
            })
        }

        function refreshCurrentFolder(callback) {
            onDirectoryDblClick.call(currentFileEditor, null, currentFolder, callback);
        }

        class DuplicateFileResolver{
            constructor(files) {
                this._files = files;
                this._current = 0;
                this._canBuildNext = true;
                this._newFiles = [];
            }

            async resolveForUpload(formData) {
                this._formData = formData;

                while (this._canBuildNext) {
                    await this.#buildForUpload();
                }

                if (this._newFiles.length) {
                    refreshCurrentFolder(() => highlightNewFiles(this._newFiles));
                }

                return this._formData;
            }

            async resolveForCopy(targetDirectory){
                this._targetDirectory = targetDirectory;
                while (this._canBuildNext) {
                    await this.#buildForCopy();
                }

                if (this._newFiles.length) {
                    refreshCurrentFolder(() => highlightNewFiles(this._newFiles));
                }

                return this._files;
            }

            async resolveForMove(targetDirectory){
                this._targetDirectory = targetDirectory;
                while (this._canBuildNext) {
                    await this.#buildForMove();
                }

                if (this._newFiles.length) {
                    refreshCurrentFolder(() => highlightNewFiles(this._newFiles));
                }

                return this._files;
            }

            #next() {
                const file = this._files[this._current++];
                if (!file) {
                    this._canBuildNext = false;
                    return null;
                }

                return file;
            }

            #addToNewFiles(file){
                this._newFiles.push(file);
            }

            #removeCurrentFile(file) {
                this._files.splice(this._files.indexOf(file), 1);
            }

            #removeFromFormData(filename){
                this._formData.delete(filename)
            }

            #buildOverwriteButton(onClick){
                return  components.buttons.positiveButton({
                    text: texts.overwrite,
                    click: onClick
                });
            }

            #buildCancelButton(onClick){
                return components.buttons.negativeButton({
                    text: texts.cancel,
                    click: onClick
                });
            }

            #buildDefaultRenameButton(onClick){
                return components.buttons.positiveButton({
                    text: texts.defaultRename,
                    click: onClick
                })
            }

            #buildChooseFilenameButton(onClick) {
                return components.buttons.positiveButton({
                    text: texts.chooseFilename,
                    click: onClick
                });
            }

            #buildFilenameRow(filename){
                return components.texts.infoText("<div>", texts.title.filename + filename).css("display", "inline-block");
            }

            #buildEditFilenameField(filename){
                const $editFileNameField = buildFileNameField(texts.title.newFilename)
                    .css({
                        "width": "80%",
                        "display": "inline-block"
                    });

                $editFileNameField.setValue(filename);

                return $editFileNameField;
            }

            #buildSelect(){
                const $select = components.selects.imcmsSelect('<div>', {
                    id: 'eugene',
                    name: 'eugene',
                    text:texts.title.selectTitle,
                    emptySelect: false
                }, [
                    {
                        'data-value': "CURRENT",
                        text: texts.title.current
                    },
                    {
                        'data-value': "EXISTING",
                        text: texts.title.existing
                    }
                ]).css({
                    "width": "20%",
                    "display": "inline-block",

                });
                $select.find(".imcms-drop-down-list").css("width", "90%");
                $select.selectValue("CURRENT");
                return $select;
            }

            #buildForUpload() {
                const file = this.#next();
                if (!file) {
                    return Promise.resolve();
                }

                return new Promise((resolve, reject) => {
                    const $overwriteButton = this.#buildOverwriteButton(() => {
                        const fileToSaveWithContent = new FormData();
                        const filename = file.fileName;
                        fileToSaveWithContent.append("filename", filename);
                        fileToSaveWithContent.append("fullPath", file.fullPath);
                        fileToSaveWithContent.append(filename, this._formData.get(filename));

                        fileRestApi.change(fileToSaveWithContent)
                            .done(() => {
                                this.#addToNewFiles(file);
                                this.#removeFromFormData(file.fileName);
                                this.#removeCurrentFile(file);
                                resolve();
                            });
                    });

                    const $defaultRenameButton = this.#buildDefaultRenameButton(() => {
                        const data = {path: file.fullPath};
                        fileRestApi.defaultRename(data)
                            .done(() => {
                                this.#addToNewFiles(file);
                                resolve();
                            });
                    });

                    const $chooseFilenameButton = this.#buildChooseFilenameButton(() => {
                        const $filename = this.#buildFilenameRow(file.fileName);
                        const $select = this.#buildSelect();
                        const $editFileNameField = this.#buildEditFilenameField(file.fileName);

                        modal.buildEditFileModalWindow($filename, $editFileNameField, $select, confirmed => {
                            if (!confirmed) {
                                this.#removeFromFormData(file.fileName);
                                this.#removeCurrentFile(file);
                                resolve();
                                return;
                            }

                            const selectedValue = $select.getSelectedValue();
                            const newFilename = $editFileNameField.getValue();
                            if (selectedValue === "CURRENT") {
                                this._formData.append(newFilename, this._formData.get(file.fileName));
                                this._formData.delete(file.fileName);
                                this.#addToNewFiles({fileName:newFilename});
                                resolve();
                            } else {
                                const data = {
                                    src: file.fullPath,
                                    newName: newFilename,
                                };
                                fileRestApi.rename(data).done(() => {
                                    resolve();
                                });
                            }
                        });
                    });

                    const $cancelButton = this.#buildCancelButton(() => {
                        this.#removeFromFormData(file.fileName);
                        this.#removeCurrentFile(file);
                        resolve();
                    });

                    const $buttons = [$overwriteButton, $defaultRenameButton, $chooseFilenameButton, $cancelButton];
                    modal.buildModalWindowWithButtonGroup(texts.title.chooseAction + file.fileName, $buttons);
                });
            }

            #buildForCopy() {
                const file = this.#next();
                if (!file) {
                    return Promise.resolve();
                }

                if (file.fileType === 'DIRECTORY') {
                    modal.buildErrorWindow(texts.error.onlyFilesSupported, callback => {
                        return Promise.resolve()
                    });
                    return;
                }

                return new Promise((resolve, reject) => {
                    const $overwriteButton = this.#buildOverwriteButton(() => {
                        const params = {
                            src: file.fullPath,
                            target: this._targetDirectory,
                            overwrite: true
                        };

                        fileRestApi.copy(params)
                            .done(() => {
                                this.#addToNewFiles(file);
                                this.#removeCurrentFile(file);
                                resolve();
                            });
                    });

                    const $defaultRenameButton = this.#buildDefaultRenameButton(() => {
                        const data = {path: this._targetDirectory + '/' + file.fileName};
                        fileRestApi.defaultRename(data)
                            .done((file) => {
                                resolve();
                            });
                    });

                    const $chooseFilenameButton = this.#buildChooseFilenameButton(() => {
                        const $filename = this.#buildFilenameRow(file.fileName);
                        const $select = this.#buildSelect();
                        const $editFileNameField = this.#buildEditFilenameField(file.fileName);

                        modal.buildEditFileModalWindow($filename, $editFileNameField, $select, confirmed => {
                            if (!confirmed) {
                                this.#removeCurrentFile(file);
                                resolve();
                            }

                            const selectedValue = $select.getSelectedValue();
                            const newFilename = $editFileNameField.getValue();
                            if (selectedValue === "CURRENT") {
                                const formData=new FormData();
                                formData.append("src", file.fullPath);
                                formData.append("target", this._targetDirectory);
                                formData.append("newFilename", newFilename);

                                fileRestApi.copyWithRename(formData)
                                    .done(() => {
                                        this.#addToNewFiles({fileName:newFilename});
                                        this.#removeCurrentFile(file);
                                        resolve();
                                    })
                            } else {
                                const data = {
                                    src: this._targetDirectory + '/' + file.fileName,
                                    newName: newFilename,
                                };
                                fileRestApi.rename(data).done(() => {
                                    resolve();
                                });
                            }
                        });
                    });

                    const $cancelButton = this.#buildCancelButton(() => {
                        this.#removeCurrentFile(file);
                        resolve();
                    });

                    const $buttons = [$overwriteButton, $defaultRenameButton, $chooseFilenameButton, $cancelButton];
                    modal.buildModalWindowWithButtonGroup(texts.title.chooseAction + file.fileName, $buttons);
                });
            }

            #buildForMove(){
                const file = this.#next();
                if (!file) {
                    return Promise.resolve();
                }

                if (file.fileType === 'DIRECTORY') {
                    modal.buildErrorWindow(texts.error.onlyFilesSupported, callback => {
                        return Promise.resolve()
                    });
                    return;
                }

                return new Promise((resolve, reject) => {
                    const $defaultRenameButton = this.#buildDefaultRenameButton(() => {
                        const data = {path: this._targetDirectory + '/' + file.fileName};
                        fileRestApi.defaultRename(data)
                            .done((file) => {
                                resolve();
                            });
                    });

                    const $chooseFilenameButton = this.#buildChooseFilenameButton(() => {
                        const $filename = this.#buildFilenameRow(file.fileName);
                        const $select = this.#buildSelect();
                        const $editFileNameField = this.#buildEditFilenameField(file.fileName);

                        modal.buildEditFileModalWindow($filename, $editFileNameField, $select, confirmed => {
                            if (!confirmed) {
                                this.#removeCurrentFile(file);
                                resolve();
                            }

                            const selectedValue = $select.getSelectedValue();
                            const newFilename = $editFileNameField.getValue();
                            if (selectedValue === "CURRENT") {
                                const formData=new FormData();
                                formData.append("src", file.fullPath);
                                formData.append("target", this._targetDirectory);
                                formData.append("newFilename", newFilename);

                                fileRestApi.moveWithRename(formData)
                                    .done(() => {
                                        this.#addToNewFiles(file);
                                        this.#removeCurrentFile(file);
                                        resolve();
                                    })
                            } else {
                                const data = {
                                    src: this._targetDirectory + '/' + file.fileName,
                                    newName: newFilename,
                                };
                                fileRestApi.rename(data).done(() => {
                                    resolve();
                                });
                            }
                        });
                    });

                    const $cancelButton = this.#buildCancelButton(() => {
                        this.#removeCurrentFile(file);
                        resolve();
                    });

                    const $buttons = [$defaultRenameButton, $chooseFilenameButton, $cancelButton];
                    modal.buildModalWindowWithButtonGroup(texts.title.chooseAction + file.fileName, $buttons);
                });
            }
        }

        function addTemplateToGroup() {
            modal.buildModalWindow(texts.groupData.addToGroupConfirm, confirmed => {
                if (!confirmed) return;

                const templateGroupId = $templateGroupSelect.getSelectedValue();
                const templateGroupName = $templateGroupSelect.selectedText();

                const transferData = {
                    templateGroupId,
                    templateName: currentFile.fileName,
                };

                groupsRestApi.addTemplate(transferData).done(() => {
                    if ($templatesTable.css('display') !== 'none') {
                        fillTemplatesTableByTemplateGroup(templateGroupName);
                    }
                }).fail(() => modal.buildErrorWindow(texts.error.addTemplateToGroup));
            });
        }

        function buildBoundData(targetSubFilesContainerIndex) {
            return {
                getTargetSubFilesContainer: () => getSubFilesContainerByIndex(targetSubFilesContainerIndex),
                transformFileToRow: bindTransformFileToRow(targetSubFilesContainerIndex),
                targetSubFilesContainerIndex: targetSubFilesContainerIndex
            }
        }

        function bindTransformFileToRow(subFilesContainerIndex) {
            return fileToRow.transformFileToRow.bind({subFilesContainerIndex});
        }

        function bindViewSubFilesContainer(subFilesContainerIndex) {
            const classes = {
                subFilesClassName: subFilesContainerIndex === 0 ? 'first-sub-files' : 'second-sub-files',
                columnClassName: subFilesContainerIndex === 0 ? 'files-table__first-instance' : 'files-table__second-instance'
            };
            return buildViewFilesContainer.bind(classes);
        }

        function bindUploadFile(subFilesContainerIndex) {
            return uploadFile.bind(buildBoundData(subFilesContainerIndex))
        }

        function bindAddFile(subFilesContainerIndex) {
            const saveFile = onSaveFile.bind(buildBoundData(subFilesContainerIndex));
            return () => confirmAddFile(getDirPathByIndex(subFilesContainerIndex), saveFile);
        }

        function bindEditFile(subFilesContainerIndex) {
            const renameFile = onRenameFile.bind(buildBoundData(subFilesContainerIndex));
            const editFileContent = onEditFileContent.bind({
                getTargetDirectoryPath: () => getDirPathByIndex(subFilesContainerIndex)
            });
            const buildConfirmEdit = () => confirmEdit(renameFile, editFileContent);
            return () => prepareOnEditFile(buildConfirmEdit);
        }

        function bindMoveFile(subFilesContainerIndex) {
            const index = subFilesContainerIndex === 0 ? 1 : 0;
            return moveFile.bind(buildBoundData(index));
        }

        function bindCopyFile(subFilesContainerIndex) {
            const index = subFilesContainerIndex === 0 ? 1 : 0;
            return copyFile.bind(buildBoundData(index));
        }

        const fileEditor = {
            isTemplate,
            bindViewSubFilesContainer,
            bindUploadFile,
            bindAddFile,
            bindEditFile,
            bindMoveFile,
            bindCopyFile,
            deleteFile,
            downloadFile,
            getTemplateGroupEditor,
            addTemplateToGroup,
            displayDocs: buildDocumentsContainer,
            viewDoc: getViewDocById
        };

        return fileEditor;
    }
);
