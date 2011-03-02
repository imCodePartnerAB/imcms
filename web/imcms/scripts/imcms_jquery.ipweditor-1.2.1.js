(function($){
/*
 * Editable 1.3.3
 *
 * Copyright (c) 2009 Arash Karimzadeh (arashkarimzadeh.com)
 * Licensed under the MIT (MIT-LICENSE.txt)
 * http://www.opensource.org/licenses/mit-license.php
 *
 * Date: Mar 02 2009
 * MODIFIED: Tommy Ullberg, imCode. 2011-02-23 14:20
 * Changed FCKeditor to CKeditor and added more features.
 */
$.fn.editable = function(options){
	var defaults = {
		onEdit: null,
		onSubmit: null,
		onCancel: null,
		editClass: null,
		disabledClass: 'imcmsInlineEditDisabled',
		submit: null,
		cancel: null,
		type: 'text', //text, textarea or select
		submitBy: 'blur', //blur,change,dblclick,click
		editBy: 'click',
		editor: 'non',
		editorLang: null,
		options: null
	}
	
	
	if(options=='disable')
		return this.unbind(this.data('editable.options').editBy,this.data('editable.options').toEditable);
	if(options=='enable')
		return this.bind(this.data('editable.options').editBy,this.data('editable.options').toEditable);
	if(options=='destroy')
		return this.unbind(options.editBy).removeData() ;
		/*return  this.unbind(this.data('editable.options').editBy,this.data('editable.options').toEditable)
					.data('editable.previous',null)
					.data('editable.current',null)
					.data('editable.options',null);*/

	var options = $.extend(defaults, options);

	options.toEditable = function(){
		$this = $(this);
		// Check mode
		var textContainerId = $this.attr('id') ;
		var oTextField = getImcmsTextFieldFromString($, textContainerId) ;
		var isTextMode = ('text' == oTextField.mode) ;
		// Set data
		if (isTextMode) {
			$this.data('editable.current',$this.text().replace(/^\s*|\s*$/g, '')) ;
			$this.data('editable.isTextMode', true) ;
		} else {
			$this.data('editable.current',this.innerHTML.replace(/^\s*|\s*$/g, '')) ;
			$this.data('editable.isTextMode', false) ;
		}
		opts = $this.data('editable.options');
		$.editableFactory[opts.type].toEditable($this.empty(),opts);
		// Configure events,styles for changed content
		$this.data('editable.previous',$this.data('editable.current'))
			 .children()
				 .focus()
				 .addClass(opts.editClass);
		var $buttons = $('<div class="imcmsFormBtnDiv" style="text-align:right;" />') ;
		// Submit Event
		if (opts.submit) {
			$('<button class="imcmsFormBtnSmall" />').appendTo($buttons)
						.html(opts.submit)
						.one('mouseup',function(){opts.toNonEditable($(this).parent().parent(),true)});
		} else {
			$this.one(opts.submitBy,function(){opts.toNonEditable($(this),true)})
				 .children()
				 	.one(opts.submitBy,function(){opts.toNonEditable($(this).parent().parent(),true)});
		}
		// Cancel Event
		if (opts.cancel) {
			$('<button class="imcmsFormBtnSmall" />').appendTo($buttons)
						.html(opts.cancel)
						.one('mouseup',function(){opts.toNonEditable($(this).parent().parent(),false)});
		}
		if(opts.submit || opts.cancel){
			$buttons.appendTo($this) ;
		}
		// Call User Function
		if($.isFunction(opts.onEdit))
			opts.onEdit.apply($this,
				[{
					$container : $this,
					current    : $this.data('editable.current'),
					previous   : $this.data('editable.previous')
				}]
			);
	}
	options.toNonEditable = function($this,change){

		opts = $this.data('editable.options');
		// Configure events,styles for changed content

		$this.one(opts.editBy,opts.toEditable)
			.data( 'editable.current', change ? $.editableFactory[opts.type].getValue($this,opts) : $.editableFactory[opts.type].doCancel($this,opts))
			.sethtml(this, $this.data('editable.isTextMode'), opts.type=='password' ? '*****' : $this.data('editable.current')) ;
		
		// Call User Function
		var func = null;
		if($.isFunction(opts.onSubmit)&&change==true)
			func = opts.onSubmit;
		else if($.isFunction(opts.onCancel)&&change==false)
			func = opts.onCancel;
		if(func!=null)
			func.apply($this,
				[{
					$container : $this,
					current    : $this.data('editable.current'),
					previous   : $this.data('editable.previous')
				}]
			);
	}
	this.data('editable.options',options);/*
	console.log('$(this).hasClass(options.disabledClass): ' + $(this).hasClass(options.disabledClass)) ;
	if ($(this).hasClass(options.disabledClass)) {
		return ;
	}*/
	return this.one(options.editBy,options.toEditable);
}
$.editableFactory = {
	'text': {
		toEditable: function($this,options){
			var containerWidth = $this.attr('style','display:block;').width() ;
			$('<input type="text" style="width:' + (containerWidth - 6) + 'px;" />').appendTo($this)
						 .val($this.data('editable.current'));
		},
		getValue: function($this,options){
			return $this.removeAttr('style').children().val();
		},
		doCancel: function($this,options){
			return $this.removeAttr('style').data('editable.current') ;
		}
	},
	'password': {
		toEditable: function($this,options){
			$this.data('editable.current',$this.data('editable.password'));
			$this.data('editable.previous',$this.data('editable.password'));
			$('<input type="password" style="width:99%;" />').appendTo($this)
										 .val($this.data('editable.current'));
		},
		getValue: function($this,options){
			$this.data('editable.password',$this.children().val());
			return $this.children().val();
		},
		doCancel: function($this,options){
			return $this.children().val();
		}
	},
	'textarea': {
		toEditable: function($this,options){
			$this.attr('style','display:block;') ;
			var containerWidth = $this.width() ;
			var containerHeight = $this.html($this.data('editable.current')).height() ;
			$this.empty() ;
			var winH    = $(window).height() ;
			var wantedH = (winH - 200) ;
			//console.log('winH: ' + winH + ', containerHeight: ' + containerHeight) ;
			if (containerHeight < 20) {
				containerHeight = 20 ;
			} else if (containerHeight > wantedH) {
				containerHeight = wantedH ;
			}
			containerHeight = (containerHeight + 20).roundTo(20, true) ;
			//console.log('Set containerHeight: ' + containerHeight) ;
			$('<textarea style="width:' + (containerWidth - 6) + 'px; overflow:auto;" />')
							.height(containerHeight)
							.appendTo($this)
							.val($this.data('editable.current'));
		},
		getValue: function($this,options){
			return $this.removeAttr('style').children().val();
		},
		doCancel: function($this,options){
			return $this.removeAttr('style').children().val();
		}
	},
	'wysiwyg': {
		toEditable: function($this,options){
			//options.elementId = ($this.attr('id') || options.elementId) ;
			var thisId = $this.attr('id') ;
			//var editorWidth = getWidthFromImcmsDummyElement($, thisId.replace(/^(.+)_container$/, '$1')) ;
			var editorWidth = $this.attr('style','display:block;').width() ;
			// CKEditor
			if(CKEDITOR && options.editor){
				$('<textarea name="'+options.elementId +'" id="ta_'+thisId +'" />')
								.appendTo($this)
								.val($this.data('editable.current'));
				
				var ckToolBar = (editorWidth <= 230) ? 'imCMS_SIMPLE_0' : (editorWidth <= 300) ? 'imCMS_SIMPLE_1' : (editorWidth <= 440) ? 'imCMS_SIMPLE_2' : (editorWidth <= 700) ? 'imCMS_SIMPLE_3' : 'imCMS_ALL' ;
				
				//console.log(CKEDITOR + ', ' + options.editor + ', ' + thisId + ', ' + options.editorLang + ', editorWidth:' + editorWidth + ', ' + ckToolBar) ;
				
				initCkEditor($, 'ta_' + thisId, options.editorLang, editorWidth, ckToolBar) ;
			}else{
				$('<textarea name="'+options.editor.id+'" id="'+options.editor.id+'" style="width:100%" />').appendTo($this)
									.val($this.data('editable.current'));
								  
				var ed = new tinymce.Editor(options.editor.id, {});
				ed.settings = options.editor.settings;
				options.editor = ed;
				options.editor.render();
			}

		},
		getValue: function($this,options){
			var thisId = $this.removeAttr('style').attr('id') ;
			var retVal ;
			if (CKEDITOR && options.editor) {
				retVal = CKEDITOR.instances['ta_' + thisId].getData() ;
				CKEDITOR.instances['ta_' + thisId].destroy() ;
				return retVal ;
			} else {
				retVal =options.editor.getContent({format : 'text'}) ;
				options.editor.remove() ;
				return retVal ;
			}
		},
		doCancel: function($this,options){
			var thisId = $this.removeAttr('style').attr('id') ;
			if (CKEDITOR && options.editor) {
				CKEDITOR.instances['ta_' + thisId].destroy() ;
			} else {
				options.editor.remove() ;
			}
			return $this.data('editable.current') ;
		}
	},
	'select': {
		toEditable: function($this,options){
			$select = $('<select/>').appendTo($this);
			$.each( options.options,
					function(key,value){
						$('<option/>').appendTo($select)
									.html(value)
									.attr('value',key);
					}
				   )
			$select.children().each(
				function(){
					var opt = $(this);
					if(opt.text()==$this.data('editable.current'))
						return opt.attr('selected', 'selected').text();
				}
			)
		},
		getValue: function($this,options){
			var item = null;
			$('select', $this).children().each(
				function(){
					if($(this).attr('selected'))
						return item = $(this).text();
				}
			)
			return item;
		},
		doCancel: function($this,options){
			return $this.data('editable.current') ;
		}
	}
}

$.fn.sethtml = function(d, isTextMode, value){
	if (isTextMode) {
		value = value
						.replace(/</g, '&lt;')
						.replace(/>/g, '&gt;')
						.replace(/(\r?\n)/g, '<br/>$1') ;
		$(this[0]).html(value) ;
		//console.log('isTextMode: ' + value) ;
	} else {
		this[0].innerHTML = value ;
		//console.log('isHtmlMode: ' + value) ;
	}
} ;

Number.prototype.roundTo = function(num, roundUp) {
	var mo = this % num;
	if (!roundUp && mo <= (num / 2)) { 
		return this - mo ;
	} else {
		return this - mo + num ;
	}
}
/*
function getWidthFromImcmsDummyElement($, uniqueId) {
	var textW = 150 ;
	var $textFieldDummy = $('#' + uniqueId + '_dummy') ;
	if (1 == $textFieldDummy.length) {
		$textFieldDummy.attr('style', 'background:red; height:10px;').show(0, function() {
			textW = $textFieldDummy.width() ;
			$textFieldDummy.hide(0) ;
		}) ;
	}
	return textW ;
}
*/
})(jQ);