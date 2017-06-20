/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
CKEDITOR.disableAutoInline = true;
CKEDITOR.editorConfig = function( config ) {
    config.extraPlugins = 'autogrow,link,maximize,toolbarswitch';
    config.autoGrow_minHeight = 200;
    config.autoGrow_maxHeight = 600;
    config.autoGrow_bottomSpace = 50;
    config.disableAutoInline = true;

	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
};
