<%@ page
	
	import="imcode.util.Utility, imcode.server.LanguageMapper, com.imcode.imcms.api.ContentManagementSystem, com.imcode.imcms.servlet.AjaxServlet, imcode.server.document.textdocument.TextDomainObject"
  
	contentType="text/javascript"
  pageEncoding="UTF-8"
	
%><%!

boolean DEBUG_INIT = true ;
boolean DEBUG_SAVE = false ;

%><%

boolean isSwe = false ;
try {
	isSwe =	ContentManagementSystem.fromRequest(request).getCurrentUser().getLanguage().getIsoCode639_2().equals("swe");
} catch (Exception e) {}

String cp = request.getContextPath() ;

%>
jQ(document).ready(function($) {
	if (private_USE_INLINE_EDITING) editablePluginActivate($) ;
}) ;

function getImcmsTextFieldFromString($, string) {
	var re = new RegExp('^imcmsInlineTextEdit_(\\d{4,6})_(\\d+)_(text|html)_.+$', '') ;
	if (re.test(string)) {
		var arrMatch = re.exec(string) ;
		return {
			meta_id : arrMatch[1],
			txt_no  : arrMatch[2],
			mode    : arrMatch[3]
		} ;
	}
	return null ;
}

function editablePluginActivate($) {<%
	if (DEBUG_INIT) { %>
	imLog('editablePluginActivate($) ;') ;<%
	} %>
	$('.imcmsInlineEditEditor').editable({
		type:       'wysiwyg',
		editBy:     'dblclick',
		editor:     CKEDITOR,
		editorLang: '<%= LanguageMapper.convert639_2to639_1(Utility.getLoggedOnUser(request).getLanguageIso639_2()) %>',
		submit:     'Spara',
		cancel:     'Avbryt',<%--
		onEdit:     function(content) {
			console.log('content.$container.attr(\'id\'): ' + content.$container.attr('id')) ;
		}, --%>
		onSubmit:   function(content) {
			var textContainerId = content.$container.attr('id') ;
			var oTextField = getImcmsTextFieldFromString($, textContainerId) ;
			if (null != oTextField) {<%
				if (DEBUG_SAVE) { %>
				console.log('oTextField.meta_id: ' + oTextField.meta_id + '\noTextField.txt_no:  ' + oTextField.txt_no + '\noTextField.mode:    ' + oTextField.mode + '\ntextContainerId:    ' + textContainerId) ;
				alert(content.current) ;<%
				} %>
				saveText($, textContainerId, oTextField.meta_id, oTextField.txt_no, oTextField.mode, content.current) ;
			}
		}
	}) ;
	$('.imcmsInlineEditInput').editable({
		type:       'text',
		editBy:     'dblclick',
		submit:     'Spara',
		cancel:     'Avbryt',
		onEdit:     function(content) {
			var textContainerId = content.$container.attr('id') ;
			var oTextField = getImcmsTextFieldFromString($, textContainerId) ;
			var $buttonDiv = $('#' + textContainerId).find('.imcmsFormBtnDiv:first') ;
			if ($buttonDiv.length > 0) {
				$buttonDiv.prepend('<div class="textMode imcmsToolTip" title="' + oTextField.mode + '<%= isSwe ? "läge" : "&nbsp;mode" %>">' + oTextField.mode + '</div>') ;
			}
		},
		onSubmit:   function(content) {
			var textContainerId = content.$container.attr('id') ;
			var oTextField = getImcmsTextFieldFromString($, textContainerId) ;
			if (null != oTextField) {<%
				if (DEBUG_SAVE) { %>
				console.log('oTextField.meta_id: ' + oTextField.meta_id + '\noTextField.txt_no:  ' + oTextField.txt_no + '\noTextField.mode:    ' + oTextField.mode + '\ntextContainerId:    ' + textContainerId) ;
				alert(content.current) ;<%
				} %>
				saveText($, textContainerId, oTextField.meta_id, oTextField.txt_no, oTextField.mode, content.current) ;
			}
		}
	}) ;
	$('.imcmsInlineEditTextarea').editable({
		type:       'textarea',
		editBy:     'dblclick',
		submit:     'Spara',
		cancel:     'Avbryt',
		onEdit:     function(content) {
			var textContainerId = content.$container.attr('id') ;
			var oTextField = getImcmsTextFieldFromString($, textContainerId) ;
			var $buttonDiv = $('#' + textContainerId).find('.imcmsFormBtnDiv:first') ;
			if ($buttonDiv.length > 0) {
				$buttonDiv.prepend('<div class="textMode imcmsToolTip" title="' + oTextField.mode + '<%= isSwe ? "läge" : "&nbsp;mode" %>">' + oTextField.mode + '</div>') ;
			}
		},
		onSubmit:   function(content) {
			var textContainerId = content.$container.attr('id') ;
			var oTextField = getImcmsTextFieldFromString($, textContainerId) ;
			if (null != oTextField) {<%
				if (DEBUG_SAVE) { %>
				console.log('oTextField.meta_id: ' + oTextField.meta_id + '\noTextField.txt_no:  ' + oTextField.txt_no + '\noTextField.mode:    ' + oTextField.mode + '\ntextContainerId:    ' + textContainerId) ;
				alert(content.current) ;<%
				} %>
				saveText($, textContainerId, oTextField.meta_id, oTextField.txt_no, oTextField.mode, content.current) ;
			}
		}
	}) ;
}

function editablePluginDestroy($) {<%
	if (DEBUG_INIT) { %>
	imLog('editablePluginDestroy($) ;') ;<%
	} %>
	$('.imcmsInlineEditEditor').editable('destroy') ;
	$('.imcmsInlineEditInput').editable('destroy') ;
	$('.imcmsInlineEditTextarea').editable('destroy') ;
}

<%--
function editablePluginConfig($, configStr) {
	$('.imcmsInlineEditEditor').editable(configStr) ;
	$('.imcmsInlineEditInput').editable(configStr) ;
	$('.imcmsInlineEditTextarea').editable(configStr) ;
}
--%>

function setSizeOfEditor($) {<%-- Called by init. Not used here. --%>}
function checkIframeScroll($){<%-- Called by init. Not used here. --%>}



function saveText($, containerId, metaId, textNo, textFormat, textContent) {
	var isSavedSuccess = false ;
	var $container = $('#' + containerId) ;
	$.ajax({
		url     : '<%= AjaxServlet.getPath(cp) %>',
		type    : 'POST',
		dataType : 'json',
		data    : {
			action  : 'saveText',
			meta_id : metaId,
			txt_no  : textNo,
			do_log  : true,
			format  : (textFormat.indexOf('text') != -1 ? <%= TextDomainObject.TEXT_TYPE_PLAIN %> : <%= TextDomainObject.TEXT_TYPE_HTML %>),
			text    : textContent
		},
		cache   : false,
		success : function(response) {
			var isSaved       = (null != response && response.isSaved) ;
			var responseError = (null != response && '' != response.error) ? '<br/><i>' + response.error + '</i>' : '' ;
			if (isSaved) {<%
				if (DEBUG_SAVE) { %>
				console.log('saved!') ;<%
				} %>
				var $messOk = $('<div id="imcmsInlineEditMessage_ok"><%= isSwe ? "Sparat!" : "Saved!" %></div>') ;// TODO: Put after div instead. Or disable doubleclick while it exists. Included in content.
				$container.append($messOk) ;
				$messOk
					.fadeIn('fast')<%
					for (int i = 0; i < 2; i++) { %>
					.animate({color:'#fff'}, 200)
					.animate({color:'#0b0'}, 200)<%
					} %>
					.delay(2000)
					.hide('fast', function() {
						$messOk.remove() ;
					}) ;
				isSavedSuccess = true ;<%
				if (DEBUG_SAVE) { %>
				console.log('isSavedSuccess:' + isSavedSuccess) ;<%
				} %>
			} else {<%
				if (DEBUG_SAVE) { %>
				console.log('NOT saved!') ;<%
				} %>
				var $messNotSaved = $('<div id="imcmsInlineEditMessage_error"><%= isSwe ? "ERROR! Ej sparat!" : "ERROR! Not saved!" %>' + responseError + '</div>') ;
				$container.append($messNotSaved) ;
				$messNotSaved
					.fadeIn('fast')<%
					for (int i = 0; i < 2; i++) { %>
					.animate({color:'#fff'}, 200)
					.animate({color:'#f00'}, 200)<%
					} %>
					.delay(2000)
					.hide('fast', function() {
						$messNotSaved.remove() ;
					}) ;
			}
		},
		error: function() {<%
			if (DEBUG_SAVE) { %>
			console.log('Save error!') ;<%
			} %>
			var $messError = $('<div id="imcmsInlineEditMessage_error"><%= isSwe ? "ERROR! Ej sparat!" : "ERROR! Not saved!" %></div>') ;
			$container.append($messError) ;
			$messError
				.slideDown('fast')<%
				for (int i = 0; i < 2; i++) { %>
				.animate({color:'#fff'}, 200)
				.animate({color:'#f00'}, 200)<%
				} %>
				.delay(2000)
				.hide('fast', function() {
					$messError.remove() ;
				}) ;
		}
	}) ;
}