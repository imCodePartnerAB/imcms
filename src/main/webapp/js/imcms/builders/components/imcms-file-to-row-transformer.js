define(
    'imcms-file-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder'],
    function (BEM, $, components) {
        function getOnFileClicked(file) {
            return function () {
                const $this = $(this);
            }
        }

        return {
            transform: (file, fileEditor) => {

                let infoRowAttributes = {
                    click: getOnFileClicked(file),
                    style: "display: flex;"
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