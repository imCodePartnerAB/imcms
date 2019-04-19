define(
    'imcms-file-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder'],
    function (BEM, $, components) {
        function getOnFileClicked(file) {
            return function () {
                const $this = $(this);
            }
        }

        return {
            transform: (file) => {

                let infoRowAttributes = {
                    click: getOnFileClicked(file)
                };

                return new BEM({
                    block: "file-row",
                    elements: {
                        'file-name': $('<div>', {
                            text: file
                        }),
                        'delete': components.controls.remove(),
                        'edit': components.controls.edit(),
                        'upload': components.controls.upload()
                    }
                }).buildBlockStructure("<div>", infoRowAttributes);
            }
        }
    }
);