define(
    'imcms-file-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder'],
    function (BEM, $, components) {
        function getOnFileClicked(file, fileEditor) {
            return function () {
                const $this = $(this);

                if ($this.hasClass('files-table__file-row--active')) return;

                fileEditor.viewFile($this, file);
            }
        }

        return {
            transform: (file, fileEditor) => {

                let infoRowAttributes = {
                    name: file,
                    click: getOnFileClicked(file, fileEditor)
                };

                return new BEM({
                    block: "file-row",
                    elements: {
                        'file-name': $('<div>', {
                            text: file
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