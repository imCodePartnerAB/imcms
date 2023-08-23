import '../../css/imcms-imports_files.css';

/**
 * Starter for document manager view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
const toolbarBuilder = require('imcms-standalone-editor-toolbar-builder');

const documentEditorBuilder = require("imcms-document-editor-builder");
const events = require("imcms-events");
const imcms = require("imcms");
const $ = require("jquery");

events.on("document-editor-closed", () => {
    const returnUrl = $("#return-url").val();
	window.location.replace(`/api/redirect?returnUrl=${returnUrl}`);
});

$(function () {
	const returnUrl = $("#return-url").val();
	const toolbarContent = [
		{
			type: 'language'
		},
		{
			type: 'close',
			link: (returnUrl) ? returnUrl : "",
			showIfSeparate: true
		}
	];

    documentEditorBuilder.build();
	toolbarBuilder.buildPanel(toolbarContent);
});
