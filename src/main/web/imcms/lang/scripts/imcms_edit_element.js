/**
 * Provides possibility to edit specified text/image/menu without document content.
 * Created by Serhii from Ubrainians for Imcode
 * on 30.08.16.
 */
Imcms.SingleEdit = {};
Imcms.SingleEdit.Text = {};
Imcms.SingleEdit.Image = {};
Imcms.SingleEdit.Menu = {};

/**
 * Count fails to prevent recycling
 * @type {number}
 */
Imcms.SingleEdit.failCount = 0;

/**
 * Unified editor calling
 */
Imcms.SingleEdit.openEditor = function () {
    // as there are no any event for Imcms.Editors that it is initialized, we should use setTimeout
    setTimeout(function () {
        try {
            $('.editor-frame').click();
        } catch (e) {
            if (Imcms.SingleEdit.failCount < 20) { // to prevent recycling
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
};

/**
 * Indicates that CKEditor was switched (or not) to full-screen
 * @type {boolean}
 */
Imcms.SingleEdit.Text.fired = false;

/**
 * When CKEditor loads, switches it to full-screen
 */
Imcms.SingleEdit.Text.init = function () {
    CKEDITOR.on('instanceReady', function (event) {
        if (!Imcms.SingleEdit.Text.fired) {
            var editor = event.editor;

            if (editor.elementMode == 3) { // only if not full screen to prevent recycled switching
                editor.execCommand('toolbarswitch');
            }

            Imcms.SingleEdit.Text.fired = true;
        }
    })
};

/**
 * Runs Image editor when it is initialized.
 */
Imcms.SingleEdit.Image.init = function () {
    Imcms.SingleEdit.openEditor();
};

Imcms.SingleEdit.Menu.init = function () {

};

$(document).ready(function () {
    $("body").css("padding-left", 0);
});