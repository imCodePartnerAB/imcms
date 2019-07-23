define(
    'imcms-file-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms'],
    function (BEM, $, components, texts, imcms) {

        texts = texts.superAdmin.files.title;
        let contextUrl = '/api/files/file/';

        function getOnFileDblClicked(file, fileEditor) {
            return function () {
                const $this = $(this);
                let isDblClick = true;

                fileEditor.viewFirstFilesContainer($this, file, isDblClick);
            }
        }

        function getOnFileClicked(file, fileEditor) {
            return function () {
                const $this = $(this);
                let isDblClick = false;
                fileEditor.viewFirstFilesContainer($this, file, isDblClick);
            }
        }

        function getOnSecondFileClicked(file, fileEditor) {
            return function () {
                const $this = $(this);
                let isDblClick = false;
                fileEditor.viewFirstFilesContainer($this, file, isDblClick);
            }
        }

        function getOnSecondFileDblClicked(file, fileEditor) {
            return function () {
                const $this = $(this);
                let isDblClick = true;

                fileEditor.viewSecondFilesContainer($this, file, isDblClick);
            }
        }


        return {
            transformFirstColumn: (file, fileEditor) => {

                let fullName = (file === "/..") ? "/.." : file.fullPath;

                let infoRowAttributes = {
                    name: ("/.." === fullName) ? "/.." : fullName.replace(/^.*[\\\/]/, ''),
                    dblclick: getOnFileDblClicked(file, fileEditor),
                    click: getOnFileClicked(file, fileEditor)
                };

                if (file.fileType === 'FILE') {
                    return new BEM({
                        block: "file-row",
                        elements: {
                            'file-name': $('<div>', {
                                text: fullName.replace(/^.*[\\\/]/, '')
                            }),
                            'download': $('<a>', {
                                html: components.controls.download(),
                                href: imcms.contextPath + contextUrl + fullName,
                                title: texts.download
                            }),
                            'edit': components.controls.edit(fileEditor.editFileInFirstColumn).attr("title", texts.edit),
                            'delete': components.controls.remove(fileEditor.deleteFile).attr("title", texts.delete)
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                } else if (file === "/..") {
                    return new BEM({
                        block: "symbol-row",
                        elements: {
                            'file-name': $('<div>', {
                                text: file
                            }),
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                } else {
                    return new BEM({
                        block: "directory-row",
                        elements: {
                            'file-name': $('<div>', {
                                text: ("/.." === fullName) ? "/.." : fullName.replace(/^.*[\\\/]/, '')
                            }),
                            'edit': components.controls.edit(fileEditor.editFileInFirstColumn).attr("title", texts.edit),
                            'delete': components.controls.remove(fileEditor.deleteFile).attr("title", texts.delete)
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                }
            },
            transformSecondColumn: (file, fileEditor) => {

                let fullName = (file === "/..") ? "/.." : file.fullPath;

                let infoRowAttributes = {
                    name: ("/.." === fullName) ? "/.." : fullName.replace(/^.*[\\\/]/, ''),
                    dblclick: getOnSecondFileDblClicked(file, fileEditor),
                    click: getOnSecondFileClicked(file, fileEditor)
                };

                if (file.fileType === 'FILE') {
                    return new BEM({
                        block: "file-second-row",
                        elements: {
                            'file-name': $('<div>', {
                                text: ("/.." === fullName) ? "/.." : fullName.replace(/^.*[\\\/]/, '')
                            }),
                            'download': $('<a>', {
                                html: components.controls.download(),
                                href: imcms.contextPath + contextUrl + fullName,
                                title: texts.download
                            }),
                            'edit': components.controls.edit(fileEditor.editFileInSecondColumn).attr("title", texts.edit),
                            'delete': components.controls.remove(fileEditor.deleteFile).attr("title", texts.delete)
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                } else if (file === "/..") {
                    return new BEM({
                        block: "exits-row",
                        elements: {
                            'file-name': $('<div>', {
                                text: file
                            }),
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                } else {
                    return new BEM({
                        block: "directory-row",
                        elements: {
                            'file-name': $('<div>', {
                                text: ("/.." === fullName) ? "/.." : fullName.replace(/^.*[\\\/]/, '')
                            }),
                            'edit': components.controls.edit(fileEditor.editFileInFirstColumn).attr("title", texts.edit),
                            'delete': components.controls.remove(fileEditor.deleteFile).attr("title", texts.delete)
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                }
            },
            transformDirToFirstRootColumn: (file, fileEditor) => {
                let infoRowAttributes = {
                    name: file.fullPath.replace(/^.*[\\\/]/, ''),
                    dblclick: getOnFileDblClicked(file, fileEditor),
                    click: getOnFileClicked(file, fileEditor)
                };

                return new BEM({
                    block: 'directory-row',
                    elements: {
                        'file-name': $('<div>', {
                            text: file.fullPath.replace(/^.*[\\\/]/, '')
                        }),
                        'delete': components.controls.remove(fileEditor.deleteFile).attr("title", texts.delete)
                    },
                }).buildBlockStructure("<div>", infoRowAttributes);
            },
            transformDirToSecondRootColumn: (file, fileEditor) => {
                let infoRowAttributes = {
                    name: file.fullPath.replace(/^.*[\\\/]/, ''),
                    dblclick: getOnSecondFileDblClicked(file, fileEditor),
                    click: getOnSecondFileClicked(file, fileEditor)
                };

                return new BEM({
                    block: 'directory-row',
                    elements: {
                        'file-name': $('<div>', {
                            text: file.fullPath.replace(/^.*[\\\/]/, '')
                        }),
                        'delete': components.controls.remove(fileEditor.deleteFile).attr("title", texts.delete)
                    },
                }).buildBlockStructure("<div>", infoRowAttributes);
            }
        }
    }
);