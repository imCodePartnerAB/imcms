/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.08.18
 */
Imcms.define('imcms-text-editor', ['tinyMCE'], function (tinyMCE) {

    var activeEditor;

    return {
        getActiveTextEditor: function () {
            return activeEditor || tinyMCE.activeEditor;
        },
        setActiveTextEditor: function (activeTextEditor) {
            if (activeEditor && (activeTextEditor !== activeEditor)) {
                activeEditor.triggerBlur();
            }

            activeEditor = activeTextEditor;
        }
    }
});
