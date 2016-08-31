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
    Image: {
        /**
         * Runs Image editor when it is initialized
         */
        init: function () {
            Imcms.SingleEdit.openEditor();
        }
    },
    Menu: {
        /**
         * Runs Menu editor when it is initialized
         */
        init: function () {
            Imcms.SingleEdit.openEditor();
        }
    },

    /**
     * Unified editor calling
     */
    openEditor: function () {
        // as there are no any event for Imcms.Editors that it is initialized, we should use setTimeout
        setTimeout(function () {
            try {
                $('.editor-frame').click();
            } catch (e) {
                if (Imcms.SingleEdit.failCount < 50) { // to prevent recycling
                    Imcms.SingleEdit.failCount++;
                    console.log("SingleEdit::init : Waiting for Imcms.Editors initializing first.");
                    Imcms.SingleEdit.openEditor();
                    return;
                }
            }

            setTimeout(function () {
                $("#tagWrap").removeClass("hidden");
            }, 500);
        }, 10);
    }
};

$(document).ready(function () {
    $("body").css("padding-left", 0);
});
