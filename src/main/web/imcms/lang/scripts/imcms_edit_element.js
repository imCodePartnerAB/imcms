/**
 * Provides possibility to edit specified text/image/menu without document content.
 * Created by Serhii from Ubrainians for Imcode
 * on 30.08.16.
 */
Imcms.SingleEdit = {};
Imcms.SingleEdit.Text = {};
Imcms.SingleEdit.Image = {};
Imcms.SingleEdit.Menu = {};

Imcms.SingleEdit.Text.fired = false;

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

Imcms.SingleEdit.Image.init = function () {

};

Imcms.SingleEdit.Menu.init = function () {

};
