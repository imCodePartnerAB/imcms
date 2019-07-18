define(
    'imcms-file-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms'],
    function (BEM, $, components, texts, imcms) {

        texts = texts.superAdmin.files.title;
        let contextUrl = '/api/files/file/';

        function getOnFileClicked(file, fileEditor) {
            return function () {
                const $this = $(this);

                if ($this.hasClass('files-table__file-row--active')) return;
                else if ($this.hasClass('files-table__directory-row--active')) return;

                fileEditor.viewFirstFilesContainer($this, file);
            }
        }

        function getOnSecondFileClicked(file, fileEditor) {
            return function () {
                const $this = $(this);

                if ($this.hasClass('files-table__file-row--active')) return;
                else if ($this.hasClass('files-table__directory-row--active')) return;

                fileEditor.viewSecondFilesContainer($this, file);
            }
        }


        return {
            transformFirstColumn: (file, fileEditor) => {

                let fullName = (file === "/..") ? "/.." : file.fullPath;

                let infoRowAttributes = {
                    name: ("/.." === fullName) ? "/.." : fullName.replace(/^.*[\\\/]/, ''),
                    click: getOnFileClicked(file, fileEditor)
                };

                if (file.fileType === 'FILE') {
                    return new BEM({
                        block: "file-row",
                        elements: {
                            'file-name': $('<div>', {
                                text: ("/.." === fullName) ? "/.." : fullName.replace(/^.*[\\\/]/, '')
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
                } else {
                    return new BEM({
                        block: "directory-row",
                        elements: {
                            'file-name': $('<div>', {
                                text: ("/.." === fullName) ? "/.." : fullName.replace(/^.*[\\\/]/, '')
                            })
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                }
            },
            transformSecondColumn: (file, fileEditor) => {

                let fullName = (file === "/..") ? "/.." : file.fullPath;

                let infoRowAttributes = {
                    name: ("/.." === fullName) ? "/.." : fullName.replace(/^.*[\\\/]/, ''),
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
                } else {
                    return new BEM({
                        block: "directory-row",
                        elements: {
                            'file-name': $('<div>', {
                                text: ("/.." === fullName) ? "/.." : fullName.replace(/^.*[\\\/]/, '')
                            })
                        }
                    }).buildBlockStructure("<div>", infoRowAttributes);
                }
            }
        }
    }
);