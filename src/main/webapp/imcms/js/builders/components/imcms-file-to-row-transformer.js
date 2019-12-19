define(
    'imcms-file-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder', 'imcms-i18n-texts'],
    function (BEM, $, components, texts) {

        texts = texts.superAdmin.files.title;

        const TEMPLATE_PATTERN = new RegExp('.(JSP|HTML)$', 'gi');

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

                if (file.fileType === 'FILE' && TEMPLATE_PATTERN.test(file.fullPath)) {
                    return new BEM({
                        block: "template-row",
                        elements: {
                            'file-name': $('<div>', {text: file.fileName}),
                            'add-to-group': components.controls.plus(withClick(fileEditor.addTemplateToGroup)).attr("title", texts.addToGroup),
                            'download': components.controls.download(withClick(() => fileEditor.downloadFile(file))).attr('title', texts.download),
                            'edit': components.controls.edit(withClick(fileEditor.bindEditFile(this.subFilesContainerIndex))).attr("title", texts.edit),
                            'delete': components.controls.remove(withClick(() => fileEditor.deleteFile(file))).attr("title", texts.delete),
                        },
                    }).buildBlockStructure("<div>", infoRowAttributes);
                } else if (file.fileType === 'FILE') {
                    return new BEM({
                        block: "file-row",
                        elements: {
                            'file-name': $('<div>', {text: file.fileName}),
                            'download': components.controls.download(withClick(() => fileEditor.downloadFile(file))).attr('title', texts.download),
                            'edit': components.controls.edit(withClick(fileEditor.bindEditFile(this.subFilesContainerIndex))).attr("title", texts.edit),
                            'delete': components.controls.remove(withClick(() => fileEditor.deleteFile(file))).attr("title", texts.delete)
                        },
                    }).buildBlockStructure("<div>", infoRowAttributes);
                } else if (file.fileType === 'DIRECTORY' && file.fileName === "/..") {
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
                            'edit': components.controls.edit(withClick(fileEditor.bindEditFile(this.subFilesContainerIndex))).attr("title", texts.edit),
                            'delete': components.controls.remove(withClick(() => fileEditor.deleteFile(file))).attr("title", texts.delete)
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
                        'delete': components.controls.remove(() => fileEditor.deleteFile(file)).attr("title", texts.delete)
                    },
                }).buildBlockStructure("<div>", infoRowAttributes);
            },
        }
    }
);