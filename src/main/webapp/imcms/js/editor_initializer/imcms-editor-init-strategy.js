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
                    const index = editorData.menuIndex || editorData.index;
                    const confirmMessage = `This ${editorInitData.context} (#${index})`
                        + ` is edited on page ${editorData.external}\nGo to the page?`;

                    modalWindowBuilder.buildConfirmWindow(confirmMessage, () => {
                        const url = window.location.origin + window.location.pathname + '?meta_id=' + editorData.external;
                        window.open(url, '_blank');
                    });

                    return;
                }

                editorInitData.editorBuilder.setTag($editedTag).build(editorData);
            }

	        $(editorInitData.EDIT_AREA_SELECTOR)
                .find(editorInitData.CONTROL_SELECTOR)
	            .off('click')
                .click(openEditor);

	        $(editorInitData.EDIT_AREA_SELECTOR).find(".imcms-editor-area__control-wrap")
		        .children().each(function () {
                const $this = $(this);

                if ($this.parents(".imcms-image-in-text").length) {
                    return;
                }

                $this.css("display", "inline-block");
            });
        }
    };
});
