import '../../../css/imcms-imports_files.css';

/**
 * Starter for text edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.02.18
 */
var textEditorInitializer = require("imcms-text-editor-initializer");
var imageEditorInitializer = require("imcms-image-editor-initializer");
var imcms = require("imcms");
var $ = require('jquery');

imcms.textEditorFullScreenEnabled = true;

$(function () {
    textEditorInitializer.initEditor({autoFocus: true});
    imageEditorInitializer.initEditor();
});
