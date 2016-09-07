/**
 * Provides possibility to edit specified text/image/menu without document content.
 * Created by Serhii from Ubrainians for Imcode
 * on 30.08.16.
 */
Imcms.SingleEdit = {
    /**
     * Count fails to prevent recycling
     * @type {number}
     */
    failCount: 0,

    Text: {
        /**
         * Indicates that CKEditor was switched (or not) to full-screen
         * @type {boolean}
         */
        fired: false,

        /**
         * When CKEditor loads, switches it to full-screen
         */
        init: function () {
            CKEDITOR.on('instanceReady', function (event) {
                if (!Imcms.SingleEdit.Text.fired) {
                    var editor = event.editor;

                    if (editor.elementMode == 3) { // only if not full screen to prevent recycled switching
                        editor.execCommand('toolbarswitch');
                    }

                    Imcms.SingleEdit.Text.fired = true;
                }
            })
        }
    },

    /**
     * Unified editor
     */
    Editor: {
        /**
         * Unified editor calling, runs when editor is loaded
         */
        init: function () {
            // as there are no any event for Imcms.Editors that it is initialized, we should use setTimeout
            setTimeout(function () {
                try {
                    $('.editor-frame').click();
                } catch (e) {
                    if (Imcms.SingleEdit.failCount < 50) { // to prevent recycling
                        Imcms.SingleEdit.failCount++;
                        console.log("SingleEdit::init : Waiting for Imcms.Editors initializing first.");
                        Imcms.SingleEdit.Editor.init();
                        return;
                    }
                }

                // add event listener to redirect to document when editor closes
                window.addEventListener("imcmsEditorClose", function () {
                    Imcms.BackgroundWorker.createTask({
                        showProcessWindow: true,
                        redirectURL: Imcms.Linker.get("admin.document.redirect.full", Imcms.document.meta)
                    })()
                });
            }, 15);
        }
    }
};

// prevents document's body shift to right as usually in edit mode
Imcms.isEditMode = false;

$(document).ready(function () {
    $("body").css("padding-left", 0);
});
