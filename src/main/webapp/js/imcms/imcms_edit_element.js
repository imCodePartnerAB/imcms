(function (Imcms) {
    // prevents document's body shift to right as usually in edit mode
    Imcms.isEditMode = false;

    $(document).ready(function () {
        $("body").css("padding-left", 0);
    });

    /**
     * Provides possibility to edit specified text/image/menu without document content.
     * Created by Serhii from Ubrainians for Imcode
     * on 30.08.16.
     */
    return Imcms.SingleEdit = {
        /**
         * Count fails to prevent recycling
         * @type {number}
         */
        failCount: 0,

        Text: {
            /**
             * When CKEditor loads, switches it to full-screen
             */
            init: function () {
                CKEDITOR.on('instanceReady', function (event) {
                    var editor = event.editor;

                    if (editor.config.toolbar.indexOf("max") !== 0) {
                        editor.execCommand('toolbarswitch');
                    }
                });

                Imcms.Events.on("TextEditorRedirect", function () {
                    Imcms.BackgroundWorker.createTask({
                        showProcessWindow: true,
                        redirectURL: Imcms.Linker.get("admin.document.redirect.full", Imcms.document.id)
                    })()
                });
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
                // as there are no any event for Imcms.Editors that is initialized, we should use setTimeout
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
                    Imcms.Events.on("imcmsEditorClose", function () {
                        Imcms.BackgroundWorker.createTask({
                            showProcessWindow: true,
                            redirectURL: Imcms.Linker.get("admin.document.redirect.full", Imcms.document.id)
                        })()
                    });
                }, 500);
            }
        }
    };
})(Imcms);
