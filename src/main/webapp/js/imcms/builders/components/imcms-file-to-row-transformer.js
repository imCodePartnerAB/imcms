define(
    'imcms-file-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder', 'imcms-i18n-texts'],
    function (BEM, $, components, texts) {

        texts = texts.superAdmin.files.title;

        function getOnFileClicked(file, fileEditor) {
            return function () {
                const $this = $(this);

                if ($this.hasClass('files-table__file-row--active')) return;
                else if ($this.hasClass('files-table__directory-row--active')) return;

                fileEditor.viewFile($this, file);
            }
        }

        function getOnSecondFileClicked(file, fileEditor) {
            return function () {
                const $this = $(this);

                if ($this.hasClass('files-table__file-row-second--active')) return;
                else if ($this.hasClass('files-table__directory-row-second--active')) return;

                fileEditor.viewSecondFile($this, file);
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
                            'download': components.controls.download().attr("title", texts.download),
                            'edit': components.controls.edit(fileEditor.editFile).attr("title", texts.edit),
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

                let infoRowAttributes = {
                    name: file.replace(/^.*[\\\/]/, ''),
                    click: getOnSecondFileClicked(file, fileEditor)
                };

                return new BEM({
                    block: "file-second-row",
                    elements: {
                        'file-name': $('<div>', {
                            text: file.replace(/^.*[\\\/]/, '')
                        }),
                        'download': components.controls.download(),
                        'edit': components.controls.edit(fileEditor.editFile),
                        'delete': components.controls.remove(fileEditor.deleteFile)
                    }
                }).buildBlockStructure("<div>", infoRowAttributes);
            }
        }
    }
);