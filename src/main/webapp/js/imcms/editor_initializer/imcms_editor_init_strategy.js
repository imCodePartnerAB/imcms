/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
define("imcms-editor-init-strategy", ["jquery", 'imcms-modal-window-builder'], function ($, modalWindowBuilder) {
    return {
        initEditor: (editorInitData) => {

            function openEditor() {
                const $editedTag = $(this).parents(editorInitData.EDIT_AREA_SELECTOR);
                const editorData = $editedTag.data();

                if (editorData.external) {
                    const confirmMessage = `This ${editorInitData.context} (nbr #${editorData.index})`
                        + ` is edited on page ${editorData.external}\nGo to the page?`;

                    modalWindowBuilder.buildConfirmWindow(confirmMessage, () => {
                        const url = window.location.origin + window.location.pathname + '?meta_id=' + editorData.external;
                        window.open(url, '_blank');
                    });

                    return;
                }

                editorInitData.editorBuilder.setTag($editedTag).build(editorData);
            }

            const $controls = $(editorInitData.EDIT_AREA_SELECTOR)
                .find(editorInitData.CONTROL_SELECTOR)
                .click(openEditor);

            $controls.each(function () {
                const $this = $(this);

                if ($this.parents(".imcms-image-in-text").length) {
                    return;
                }

                $this.css("display", "block");
            });
        }
    };
});
