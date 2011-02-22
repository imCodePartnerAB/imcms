/*
 * jQuery defaultValue plugin
 *
 * Copyright (c) 2011 Tommy Ullberg (www.imcode.com)
 * Licensed under the GPL license and MIT:
 *   http://www.opensource.org/licenses/GPL-license.php
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Version: 1.0
 * 
 * Usage :
 *  jQuery(function($) {
 *    $('form').defaultValue('lightgrey') ;
 *  }) ;
 *  form fields of type text|password or textareas that has class='defaultValue'.
 *  Eg:
 *  <input type="text" name="myInput" value="" class="formField defaultValue" title="My default value" />
 *  ...are fixed so that they get another specified CSS class while not changed, and a default value from the title.
 *  The title is then removed:
 *  <input type="text" name="myInput" value="My default value" class="formField defaultValue lightgrey" title="" />
 *  To temporarily remove the default values, eg. for validation - use:
 *  $.defaultValueRemove() ;
 *  To restore them again - use:
 *  $.defaultValueRestore() ;
 *  Note: Is has to be initiated before these are used. (line #13)
 */

var $defaultValueSelector = null ;

jQ.fn.defaultValue = function(classWhileDefaultValue) {
	$defaultValueSelector = this ;
	return this.find('input[type=text],input[type=password],textarea').each(function() {
		var $this = $(this) ;
		var $thisType = $this.attr('type') ;
		if (!$this.hasClass('defaultValue') || !/^(text|password|textarea)$/i.test($thisType)) {
			return ;
		}
		var theValue = $this.attr('title').replace(/\r\n/g, '\n') ;
		$this.data('defaultValue', theValue) ;
		$this.removeAttr('title') ;
		$this.focus(function() {
			if (theValue == $this.val() || '' == $this.val()) {
				$this.val('') ;
				if (hasClass) {
					$this.removeClass(classWhileDefaultValue) ;
				}
			}
		}) ;
		$this.blur(function() {
			if (theValue == $this.val() || '' == $this.val()) {
				$this.val(theValue) ;
				if (hasClass) {
					$this.addClass(classWhileDefaultValue) ;
				}
			}
		}) ;
		$this.parents('form').each(function() {
			$(this).submit(function() {
				if (theValue == $this.val()) {
					$this.val('') ;
					if (hasClass) {
						$this.removeClass(classWhileDefaultValue) ;
					}
				}
			}) ;
		}) ;
		var hasClass = (null != classWhileDefaultValue && '' != classWhileDefaultValue) ;
		if ('' == $this.val()) {
			$this.val(theValue) ;
			if (hasClass) {
				$this.addClass(classWhileDefaultValue) ;
			}
		}
	}) ;
} ;


jQ.defaultValueRemove = function() {
	$defaultValueSelector.find('input[type=text],input[type=password],textarea').each(function() {
		var $this = $(this) ;
		var $thisType = $this.attr('type') ;
		if (!$this.hasClass('defaultValue') || !/^(text|password|textarea)$/i.test($thisType)) {
			return ;
		}
		var theValue = $this.data('defaultValue') ;
		if (theValue == $this.val()) {
			$this.val('') ;
		}
	}) ;
} ;

jQ.defaultValueRestore = function() {
	$defaultValueSelector.find('input[type=text],input[type=password],textarea').each(function() {
		var $this = $(this) ;
		var $thisType = $this.attr('type') ;
		if (!$this.hasClass('defaultValue') || !/^(text|password|textarea)$/i.test($thisType)) {
			return ;
		}
		var theValue = $this.data('defaultValue') ;
		if ('' == $this.val()) {
			$this.val(theValue) ;
		}
	}) ;
} ;

