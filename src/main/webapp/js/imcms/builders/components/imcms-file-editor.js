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


            if (file === '/..' || file.fileType === 'DIRECTORY') {
                fileRestApi.get(path).done(files => {
                        $('.files-table__first-instance').find('.first-sub-files').remove();
                        firstSubFilesContainer.append(fileToRow.transformFirstColumn('/..', this));
                        $('.files-table__first-instance')
                            .append(firstSubFilesContainer.append(files.map(file => fileToRow.transformFirstColumn(file, this))));
                    currentPath = path;
                    }
                ).fail(() => modal.buildErrorWindow(texts.error.loadError));
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
            return alert("delete!=)");
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

            let currentFullPath = currentPath + name;

            let fileToSave = {
                fileName: name,
                fullPath: currentFullPath,
                fileType: isDirectory ? 'DIRECTORY' : 'FILE'
            };


            fileRestApi.create(fileToSave).done(newFile => {
                $fileRow = fileToRow.transformFirstColumn((currentFile = newFile), fileEditor);

                // $container.parent().find('.files-table').append($fileRow);
                $('.first-files').append($fileRow);
                // $('.second-files').append($fileRow);

                // onFileView = onFileSimpleView;
                // prepareFileView();
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
                $fileRow = $fileRowElement;
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

            fileRestApi.replace(fileToSave).done(savedFile => {
                currentFile = savedFile;
            })
            // $profileEditButtons.slideDown();
            //
            // $profileNameRow.$input.focus();
            // $profileDocNameRow.$input.focus();
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
        let $fileRow;

        function prepareFileView() {
            onEditDelegate = onSimpleEdit;

            $fileRow.parent()
                .find('.files-table__file-row--active')
                .removeClass('files-table__file-row--active');

            $fileRow.addClass('files-table__file-row--active');
        }

        function onSimpleEdit($fileRow, file) {
            buildViewFile($fileRow, file);
            onEditFile();
        }

        function onFileSimpleView($fileRowElement, file) {
            if (currentFile) return;
            currentFile = file;
            $fileRow = $fileRowElement;

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