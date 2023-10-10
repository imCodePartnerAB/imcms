define(
    'imcms-file-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder', 'imcms-i18n-texts'],
    function (BEM, $, components, texts) {

        texts = texts.superAdmin.files.title;

        function getOnFileDblClicked(file, buildViewFunc) {
            return function () {
                const $this = $(this);
                const isDblClick = true;

                buildViewFunc($this, file, isDblClick);
            }
        }

        function getOnFileClicked(file, buildViewFunc) {
            return function () {
                const $this = $(this);
                const isDblClick = false;

                buildViewFunc($this, file, isDblClick);
            }
        }

        return {
            transformFileToRow: function (file, fileEditor) {
                const buildViewFunc = fileEditor.bindViewSubFilesContainer(this.subFilesContainerIndex);

                const infoRowAttributes = {
                    dblclick: getOnFileDblClicked(file, buildViewFunc),
                    click: getOnFileClicked(file, buildViewFunc),
                };

                const withClick = callback => {
                    return function() {
                        getOnFileClicked(file, buildViewFunc).call(this);
                        callback();
                    }
                };

                if (file.fileType === 'FILE' && fileEditor.isTemplate(file)) {
                    infoRowAttributes.name = file.fileName.substring(0, file.fileName.lastIndexOf('.'));
                    return new BEM({
                        block: "file-row",
                        elements: {
                            'file-name': $('<div>', {text: file.fileName}),
                            'amount-docs': $('<div>', {text: '[' + file.numberOfDocuments + ']', "amount": file.numberOfDocuments}),
                            'file-size': $('<div>', {text: file.size}),
                            'add-to-group': components.controls.plus(withClick(fileEditor.addTemplateToGroup)).attr("title", texts.addToGroup),
                            'download': components.controls.download(withClick(() => fileEditor.downloadFile(file))).attr('title', texts.download),
                            'edit': file.editable ? components.controls.edit(withClick(fileEditor.bindEditFile(this.subFilesContainerIndex))).attr("title", texts.edit) : "",
                            'delete': file.editable ? components.controls.remove(withClick(() => fileEditor.deleteFile(file))).attr("title", texts.delete) : ""
                        },
                    }).buildBlockStructure("<div>", infoRowAttributes);
                } else if (file.fileType === 'FILE') {
                    return new BEM({
                        block: "file-row",
                        elements: {
                            'file-name': $('<div>', {text: file.fileName}),
                            'file-size': $('<div>', {text: file.size}),
                            'download': components.controls.download(withClick(() => fileEditor.downloadFile(file))).attr('title', texts.download),
                            'edit': file.editable ? components.controls.edit(withClick(fileEditor.bindEditFile(this.subFilesContainerIndex))).attr("title", texts.edit) : "",
                            'delete': file.editable ? components.controls.remove(withClick(() => fileEditor.deleteFile(file))).attr("title", texts.delete) : ""
                        },
                    }).buildBlockStructure("<div>", infoRowAttributes);
                } else if (file.fileType === 'DIRECTORY' && file.fileName === "../") {
                    return new BEM({
                        block: "directory-row",
                        elements: {
                            'file-name': $('<div>', {text: file.fileName}),
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                } else {
                    return new BEM({
                        block: "directory-row",
                        elements: {
                            'file-name': $('<div>', {text: file.fileName + '/'}),
                            'edit': file.editable ? components.controls.edit(withClick(fileEditor.bindEditFile(this.subFilesContainerIndex))).attr("title", texts.edit) : "",
                            'delete': file.editable ? components.controls.remove(withClick(() => fileEditor.deleteFile(file))).attr("title", texts.delete): ""
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                }
            },
            transformRootDirToRow: function (file, fileEditor) {
                const buildViewFunc = fileEditor.bindViewSubFilesContainer(this.subFilesContainerIndex);

                const infoRowAttributes = {
                    dblclick: getOnFileDblClicked(file, buildViewFunc),
                    click: getOnFileClicked(file, buildViewFunc)
                };

                return new BEM({
                    block: 'root-directory-row',
                    elements: {
                        'file-name': $('<div>', {text: file.fileName + '/'}),
                        'delete': file.editable ? components.controls.remove(() => fileEditor.deleteFile(file)).attr("title", texts.delete) : ""
                    },
                }).buildBlockStructure("<div>", infoRowAttributes);
            },
        }
    }
);
