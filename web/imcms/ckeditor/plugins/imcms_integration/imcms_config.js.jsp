<%@ page
	
	contentType="text/javascript"
	
	pageEncoding="UTF-8"
	
%><%

boolean isInlineEditing = (null != request.getParameter("isInlineEditing")) ;

%>
//CKEDITOR.config.language                      = 'sv';
CKEDITOR.config.skin                          = 'office2003' ; // kama || v2 || office2003
CKEDITOR.config.tabSpaces                     = 2 ;
CKEDITOR.config.entities                      = false ;
CKEDITOR.config.resize_enabled                = true ;
CKEDITOR.config.resize_dir                    = 'vertical' ;
CKEDITOR.config.disableNativeSpellChecker     = false ;
CKEDITOR.config.image_removeLinkByEmptyURL    = true ;
CKEDITOR.config.startupFocus                  = true ;

//CKEDITOR.config.pasteFromWordCleanupFile      = 'default' ;
CKEDITOR.config.pasteFromWordPromptCleanup    = true ;
CKEDITOR.config.pasteFromWordRemoveFontStyles = true ;
CKEDITOR.config.pasteFromWordRemoveStyles     = true ;

CKEDITOR.config.removeFormatAttributes        = 'class,style,lang,width,height,align,hspace,valign,hasbox' ;
CKEDITOR.config.removeFormatTags              = 'big,code,del,dfn,font,ins,kbd,q,samp,small,span,strike,strong,sub,sup,tt,var' ;

//CKEDITOR.config.menu_groups                   = 'clipboard,tablecell,tablecellproperties,tablerow,tablecolumn,table,anchor,link' ;
//CKEDITOR.config.removePlugins                 = 'image,form' ; // Doesn't work: http://dev.ckeditor.com/ticket/6221

// Workaround for removing built-in image support
CKEDITOR.config.plugins                       = '' +
                                                'about,a11yhelp,basicstyles,bidi,blockquote,button,clipboard,colorbutton,colordialog,contextmenu,dialogadvtab,' +
																								'div,' +
																								'elementspath,' +
																								'enterkey,' +
																								'entities,' +
																					//			'filebrowser,' +
																								'find,' +
																					//			'flash,' +
																								'font,' +
																								'format,' +
																					//			'forms,' +
																								'horizontalrule,' +
																								'htmldataprocessor,' +
																					//			'image,' +
                                                'indent,' +
																								'justify,' +
																								'keystrokes,' +
																					//			'link,' +
																								'list,liststyle,maximize,newpage,pagebreak,pastefromword,pastetext,popup,' +
                                                'preview,print,removeformat,resize,save,scayt,smiley,showblocks,showborders,sourcearea,stylescombo,table,tabletools,' +
                                                'specialchar,tab,templates,toolbar,undo,wysiwygarea,wsc' ;


CKEDITOR.config.extraPlugins                  = 'imcms_image,imcms_link,pastefromword,pastetext,tab<%= isInlineEditing ? ",autogrow" : "" %>' ;//,autogrow

CKEDITOR.config.toolbar_imCMS_ALL = [
	['Source','Maximize', 'Preview'],
	['Cut','Copy','Paste','PasteText','PasteFromWord', 'SpellChecker'],
	['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
	['ImcmsLink','Unlink','Anchor'],['ImcmsImage','Table'],
	['SpecialChar','ShowBlocks'],
	'/',
	['Format','-','Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
	['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
	['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],['About']
] ;

CKEDITOR.config.toolbar_imCMS_SIMPLE_3 = [
	['Source','Maximize','-','Format','-','Bold','Italic','Underline','Strike','-','ImcmsLink','ImcmsImage'],
	['NumberedList','BulletedList','-','Outdent','Indent','-','SpecialChar']
] ;

CKEDITOR.config.toolbar_imCMS_SIMPLE_2 = [
	['Source','Maximize','-','Bold','Italic','Underline','Strike','-','ImcmsLink','ImcmsImage'],'/',
	['NumberedList','BulletedList','-','Outdent','Indent','-','SpecialChar']
] ;

CKEDITOR.config.toolbar_imCMS_SIMPLE_1 = [
	['Source','Maximize'],
	['Bold','Italic','Underline','-','ImcmsLink']
] ;

CKEDITOR.config.toolbar_imCMS_SIMPLE_0 = [
	['Source','Maximize'],'/',
	['Bold','Italic','Underline','-','ImcmsLink']
] ;



