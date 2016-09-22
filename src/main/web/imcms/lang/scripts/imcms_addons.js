(function ($) {
	var dataKey = "data-multiselect";

	$.fn.multiselect = function () {

		if (!this.length) {
			return this;
		}

		this.filter("select[multiple]").each(function (pos, item) {
			var $element = $(item),
				$div = $("<div>").addClass("multiselect-adapter"),
				$options = $element.children("option");

			$options.each(function (pos, item) {
				var $wrapper = $("<div>").addClass("field"),
					$item = $(item),
					$checkbox = $("<input>")
						.attr({
							type: "checkbox",
							name: $element.attr("name"),
							ignored: "true"
						})
						.val($item.val())
						.prop("checked", $item.is(":selected"))
						.change(function () {
							if ($checkbox.is(":checked")) {
								$item.attr("selected", true)
							} else {
								$item.removeAttr("selected");
							}
						}),
					$label = $("<label>").text($item.text());

				$.each(item.attributes, function () {
					// this.attributes is not a plain object, but an array
					// of attribute nodes, which contain both the name and value
					if (this.specified) {
						$label.attr(this.name, this.value);
					}
				});

				$wrapper.append($checkbox).append($label);
				$div.append($wrapper);
			});

			$div.insertAfter($element.next().is("label") ? $element.next() : $element);
			$element.hide().change(function () {
				var selected = $element.val(),
					$checkboxes = $div.find("checkbox");

				$checkboxes.prop("checked", false);

				$.each(selected, function (post, item) {
					$checkboxes.filter("[value=" + item + "]").prop("checked", true);
				});
			}).data(dataKey, $div);

		});
	};
})(jQuery);

/*
 CKEditor Addons
 */
CKEDITOR.define("confirmChanges", {});
CKEDITOR.define("validateText", {});
CKEDITOR.define("getTextHistory", {});

CKEDITOR.newCommandWithExecution = function (executionFunc) {
    return {
        modes: {wysiwyg: 1, source: 1},// This command works in both editing modes.
        editorFocus: false,// This command will not auto focus editor before execution.
        canUndo: false,// This command requires no undo snapshot.
        exec: executionFunc
    }
};

CKEDITOR.plugins.add("documentSaver", {
	init: function (editor) {
		var pluginName = "documentSaver";
		editor.documentSnapshot = editor.getData();
		editor.blurHandler = function (e) {
			if (e.editor.checkDirty()) {
				e.editor.openDialog(pluginName);
			}
		};
		editor.focusHandler = function (e) {
			e.removeListener();
			e.editor.on('blur', e.editor.blurHandler);
		};
		var switchToolbarCommandFunction = function (e) {
			var element = e.element;
			if (e.elementMode == 1) {
				e.execCommand("toolbarswitch");
			}
			for (var key in CKEDITOR.instances) {
				var editor = CKEDITOR.instances[key];
				if (element == editor.element) {
					return editor;
				}
			}
		};

        editor.on('blur', editor.blurHandler);

        var confirmNoEvent = function (e, callback) {
            if (!callback) {
                e = switchToolbarCommandFunction(e);
            }
            CKEDITOR.fire("confirmChangesEvent", {callback: callback}, e);
            editor.documentSnapshot = e.getData();
            e.resetDirty();
            if (!callback) {
                e.removeListener('blur', e.blurHandler);
                e.focusManager.unlock();
                e.element.$.blur();
                e.focusManager.blur();
                e.on("focus", e.focusHandler);
            }
        };

        var confirmWithEvent = function (e) {
            e = switchToolbarCommandFunction(e);
            var callback = Imcms.Events.getCallback("TextEditorRedirect");
            CKEDITOR.fire("confirmChangesEvent", {callback: callback}, e);
        };
        var confirmCommandWithEvent = CKEDITOR.newCommandWithExecution(confirmWithEvent);
        editor.addCommand("confirmChanges", confirmCommandWithEvent);
        editor.ui.addButton('confirm', {
            label: 'Save all changes',
            command: "confirmChanges",
            icon: "images/ic_apply.png"
        });

        var confirmCommandNoEvent = CKEDITOR.newCommandWithExecution(confirmNoEvent);
        editor.addCommand("confirmChangesWithoutEvent", confirmCommandNoEvent);

        var cancelCommandFunction = function (e) {
            var newEditor = switchToolbarCommandFunction(e),
                hideCommand = function (e) {
                    setTimeout(function () {
                        e.removeListener('instanceReady', hideCommand);
                        e.setData(e.documentSnapshot);
                        e.removeListener('blur', e.blurHandler);
                        e.focusManager.unlock();
                        e.element.$.blur();
                        e.focusManager.blur();
                        e.on("focus", e.focusHandler);
                    }, 1);
                };
            if (newEditor != e) {
                newEditor.on('instanceReady', hideCommand.bind(null, newEditor));

            } else {
                hideCommand(e);
            }
        };
        var cancelCommandDefinition = CKEDITOR.newCommandWithExecution(
            Imcms.Events.getCallbackOr("TextEditorRedirect", cancelCommandFunction)
        );
        editor.addCommand("cancelChanges", cancelCommandDefinition);
        editor.ui.addButton('cancel', {
            label: 'Cancel all changes and restore document to previous state',
            command: "cancelChanges",
            icon: "images/ic_cancel.png"
        });

        var saveCommandDefinition = CKEDITOR.newCommandWithExecution(function (e) {
            var $button = $('.' + e.id).find('.cke_button__savedata_icon')
                .css({
                    backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_loader.gif" + ")"
                });
            confirmNoEvent(e, function () {
                $button.css({
                    backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_save.png" + ")"
                });
            });
        });
        editor.addCommand("saveChanges", saveCommandDefinition);
        editor.ui.addButton('saveData', {
            label: 'Save all changes',
            command: "saveChanges",
            icon: "images/ic_save.png"
        });
	}
});
CKEDITOR.dialog.add("documentSaver", function (e) {
	return {
		title: 'Save Dialog',
		width: 200,
		height: 25,
		onOk: function () {
			e.execCommand("confirmChanges");
		},
		onCancel: function () {
			e.execCommand("cancelChanges");
		},
		onHide: function () {
			setTimeout(function () {
				e.focusManager.unlock();
				e.element.$.blur();
				e.focusManager.blur();
				$(e.focusManager.currentActive).blur();
			}, 500);
		},
		contents: [
			{
				id: 'general',
				label: 'Settings',
				elements: [
					{
						type: 'html',
						html: '<div style="text-align:center; line-height:25px">Save document?</div>'
					}
				]
			}
		]
	};
});

CKEDITOR.defineToolbar = function (editor) {
    var prefix;

    if (editor.elementMode == 1) { // full-screen editor mode
        editor.config.maxToolbar = true;
        prefix = "max";
        
    } else {
        editor.config.maxToolbar = false;
        prefix = "min";
    }

    if (editor.element.data("contenttype") === "text") {
        return prefix + "PlainText";

    } else if (editor.element.data("contenttype") === "from-html") {
        return prefix + "TextToolbar";

    } else {
        return prefix + "HtmlToolbar";
    }
};

CKEDITOR.switchFormat = false;

CKEDITOR.plugins.add("switchFormatToHTML", {
    init: function (editor) {
        editor.ui.addButton('switchFormatToHTML', {
            label: 'Switch format to HTML',
            command: "switchFormat",
            icon: "images/switch-between-html-and-text-mode.png"
        });
    }
});

CKEDITOR.plugins.add("switchFormatToText", {
    init: function (editor) {
        editor.ui.addButton('switchFormatToText', {
            label: 'Switch format to text',
            command: "switchFormat",
            icon: "images/plain_text.png"
        });
    }
});

CKEDITOR.plugins.add("fileBrowser", {
	init: function (editor) {

		var onChooseFile = function () {
			Imcms.Editors.Content.showDialog({
				onApply: $.proxy(onFileChosen, this),
				onCancel: $.proxy(onFileChosen, this)
			});
			editor.focusManager.blur();
			editor.element.$.blur();
		};
		var onFileChosen = function (data) {
			if (data) {
				editor.insertHtml('<img class="captionedImage" src="' + data.urlPathRelativeToContextPath + '" alt="" /><br>', 'unfiltered_html');
			}
			editor.focusManager.focus();
			editor.element.$.focus();
		};

		var openBrowserCommandDefinition = CKEDITOR.newCommandWithExecution(onChooseFile);
		editor.addCommand("openBrowser", openBrowserCommandDefinition);

        editor.ui.addButton('openBrowser', {
            label: 'Open Image Browser',
            command: "openBrowser",
            icon: "images/ic_images.png"
        });
	}
});

CKEDITOR.plugins.add("w3cValidator", {
	init: function (editor) {
		var w3cValidateCommand = function (e) {
			var $button = $('.' + e.id).find('.cke_button__w3cvalidate_icon')
				.css({
					backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_loader.gif" + ")"
				});
			//todo: Add message if content is invalid
			CKEDITOR.fire("validateText", {
				callback: function (data) {
					$button.css({
						backgroundImage: "url(" + CKEDITOR.basePath + (data.result ? "images/ic_valid.png" : "images/ic_invalid.png") + ")"
					});

					if (!data.result) {
						$(e.element.$).data("w3cValidateData", data);
						e.execCommand("w3cValidateDialog", data);
					}
				}
			}, e);
		};
		var w3cValidateCommandDefinition = CKEDITOR.newCommandWithExecution(w3cValidateCommand);
		editor.addCommand("w3cValidate", w3cValidateCommandDefinition);
		editor.addCommand("w3cValidateDialog", new CKEDITOR.dialogCommand("w3cValidationResultDialog"));

        editor.ui.addButton('w3cValidate', {
            label: 'Validate Content over W3C',
            command: "w3cValidate",
            icon: "images/ic_w3c.png"
        });
	}
});
CKEDITOR.dialog.add("w3cValidationResultDialog", function (e) {
	var $wrapper = $("<div>"),
		$content = $("<div>").addClass("imcms-w3c-errors").appendTo($wrapper),
		data = $(e.element.$).data("w3cValidateData");

	$("<div>").append($("<h2>").text("Validation Output: " + data.data.errors.length + " Errors")).appendTo($content);

	data.data.errors.forEach(function (item, pos) {
		var $container = $("<div>").addClass("imcms-w3c-error").appendTo($content),
			$errorMessage = $("<div>").text(pos + 1 + ". " + item.message.charAt(0).toUpperCase() + item.message.slice(1)).appendTo($container),
			$sourceContainer = $("<div>").appendTo($container),
			$source = $("<code>").addClass("language-html").html(item.line + ": " + item.source.replace(/(<([^>]+)>)/ig, "")).appendTo($sourceContainer);

		Prism.highlightElement($source[0]);
	});

	return {
		title: 'Validation Result Dialog',
		width: 600,
		minHeight: 250,
		contents: [
			{
				id: 'w3cValidation',
				label: 'Validation',
				elements: [
					{
						type: 'html',
						html: $("<div>").append($wrapper).html()
					}
				]
			}
		]
	};
});

CKEDITOR.plugins.add("textHistory", {
	init: function (editor) {
		var textHistoryCommand = function (e) {
			var $button = $('.' + e.id).find('.cke_button__texthistory_icon')
				.css({
					backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_loader.gif" + ")"
				});
			//todo: Add message if content is invalid
			CKEDITOR.fire("getTextHistory", {
				callback: function (data) {
					$button.css({
						backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_history.png" + ")"
					});

					$(e.element.$).data("textHistoryData", data);
					e.execCommand("textHistoryDialog", data);
				}
			}, e);
		};
		var textHistoryCommandDefinition = CKEDITOR.newCommandWithExecution(textHistoryCommand);
		editor.addCommand("textHistory", textHistoryCommandDefinition);
		editor.addCommand("textHistoryDialog", new CKEDITOR.dialogCommand("textHistory"));

        editor.ui.addButton('textHistory', {
            label: 'Text History',
            command: "textHistory",
            icon: "images/ic_history.png"
        });
	}
});

CKEDITOR.dialog.add("textHistory", function (e) {
    var $wrapper = $("<div>").addClass("imcms-text-history"),
        $leftPanel = $("<div>").addClass("imcms-left-panel").appendTo($wrapper),

        $content = $("<div>").addClass("imcms-content")
            .css({
                "max-width": "600px",
                "float": "right"
            })
            .appendTo($wrapper),

        data = $(e.element.$).data("textHistoryData")
            .map(function (textHistoryData) {
                textHistoryData.modifiedDate = new Date(textHistoryData.modifiedDate);
                textHistoryData.type = (textHistoryData.type == "HTML")
                    ? "html"
                    : "from-html";
                return textHistoryData;
            })
            .sort(function (a, b) {
                return (a.modifiedDate > b.modifiedDate ? -1 : (a.modifiedDate == b.modifiedDate ? 0 : 1));
            }),

        groupedData = {},
        $selected = undefined,
        selectedItem,
        previousType;

	data.forEach(function (it) {
		var key = it.modifiedDate.format("dd/m/yy");

		if (!(key in groupedData)) {
			groupedData[key] = [];
		}

		groupedData[key].push(it);
	});

    var buttonsShowed = false;

    function switchContentAndType() {
        if (selectedItem.type == "html") {
            $content.text($content.html());
            selectedItem.type = "from-html";

        } else {
            $content.html($content.text());
            selectedItem.type = "html";
        }
    }

    function addTextHistoryButton(text) {
        return $("<button>")
            .addClass("imcms-neutral")
            .css("float", "right")
            .html(text)
            .appendTo($wrapper)
            .click(switchContentAndType);
    }

    $.each(groupedData, function (key, list) {
        $("<div>").addClass("imcms-separator").text(key).appendTo($leftPanel);

        list.forEach(function (item) {
            $("<div>").appendTo($leftPanel)
                .append(item.modifiedDate.format("HH:MM:ss") + " | " + item.modifiedBy)
                .click(function () {
                    if (!buttonsShowed) {
                        addTextHistoryButton("As HTML");
                        addTextHistoryButton("As Text");
                        buttonsShowed = true;
                    }

                    if ($selected) {
                        $selected.removeClass("selected");
                    }

                    var callFunc = (item.type === "html")
                        ? "html"
                        : "text";

                    $content[callFunc](item.text);
                    $selected = $(this).addClass("selected");
                    selectedItem = item;
                    previousType = item.type;
                });
        })
    });

	return {
		title: 'Text History Dialog',
		width: 600,
		height: 400,
		onOk: function () {
            if (selectedItem) {
                e.setData(""); // clear previous text
                var contentType = $(e.element.$).data("contenttype");

                var callFunc = (contentType === "html")
                    ? "insertHtml"
                    : "insertText";

                e[callFunc](selectedItem.text);

                if (selectedItem.type !== contentType) {
                    e.execCommand("switchFormat");
                }
            }
		},
		contents: [
			{
				id: 'textHistory',
				label: 'Text History',
				elements: [
					{
						type: 'html',
						onLoad: function () {
							$("#" + this.domId).append($wrapper)
						},
						html: ""
					}
				]
			}
		]
	};
});

/**
 * Detect Element Resize Plugin for jQuery
 *
 * https://github.com/sdecima/javascript-detect-element-resize
 * Sebastian Decima
 *
 * version: 0.5.3
 **/
(function ($) {
	var attachEvent = document.attachEvent,
		stylesCreated = false;

	var jQuery_resize = $.fn.resize;

	$.fn.resize = function (callback) {
		return this.each(function () {
			if (this == window)
				jQuery_resize.call(jQuery(this), callback);
			else
				addResizeListener(this, callback);
		});
	};

	$.fn.removeResize = function (callback) {
		return this.each(function () {
			removeResizeListener(this, callback);
		});
	};

	if (!attachEvent) {
		var requestFrame = (function () {
			var raf = window.requestAnimationFrame || window.mozRequestAnimationFrame || window.webkitRequestAnimationFrame ||
				function (fn) {
					return window.setTimeout(fn, 20);
				};
			return function (fn) {
				return raf(fn);
			};
		})();

		var cancelFrame = (function () {
			var cancel = window.cancelAnimationFrame || window.mozCancelAnimationFrame || window.webkitCancelAnimationFrame ||
				window.clearTimeout;
			return function (id) {
				return cancel(id);
			};
		})();

		function resetTriggers(element) {
			var triggers = element.__resizeTriggers__,
				expand = triggers.firstElementChild,
				contract = triggers.lastElementChild,
				expandChild = expand.firstElementChild;
			contract.scrollLeft = contract.scrollWidth;
			contract.scrollTop = contract.scrollHeight;
			expandChild.style.width = expand.offsetWidth + 1 + 'px';
			expandChild.style.height = expand.offsetHeight + 1 + 'px';
			expand.scrollLeft = expand.scrollWidth;
			expand.scrollTop = expand.scrollHeight;
		}

		function checkTriggers(element) {
			return element.offsetWidth != element.__resizeLast__.width ||
				element.offsetHeight != element.__resizeLast__.height;
		}

		function scrollListener(e) {
			var element = this;
			resetTriggers(this);
			if (this.__resizeRAF__) cancelFrame(this.__resizeRAF__);
			this.__resizeRAF__ = requestFrame(function () {
				if (checkTriggers(element)) {
					element.__resizeLast__.width = element.offsetWidth;
					element.__resizeLast__.height = element.offsetHeight;
					element.__resizeListeners__.forEach(function (fn) {
						fn.call(element, e);
					});
				}
			});
		}

		/* Detect CSS Animations support to detect element display/re-attach */
		var animation = false,
			animationstring = 'animation',
			keyframeprefix = '',
			animationstartevent = 'animationstart',
			domPrefixes = 'Webkit Moz O ms'.split(' '),
			startEvents = 'webkitAnimationStart animationstart oAnimationStart MSAnimationStart'.split(' '),
			pfx = '';
		{
			var elm = document.createElement('fakeelement');
			if (elm.style.animationName !== undefined) {
				animation = true;
			}

			if (animation === false) {
				for (var i = 0; i < domPrefixes.length; i++) {
					if (elm.style[domPrefixes[i] + 'AnimationName'] !== undefined) {
						pfx = domPrefixes[i];
						animationstring = pfx + 'Animation';
						keyframeprefix = '-' + pfx.toLowerCase() + '-';
						animationstartevent = startEvents[i];
						animation = true;
						break;
					}
				}
			}
		}

		var animationName = 'resizeanim';
		var animationKeyframes = '@' + keyframeprefix + 'keyframes ' + animationName + ' { from { opacity: 0; } to { opacity: 0; } } ';
		var animationStyle = keyframeprefix + 'animation: 1ms ' + animationName + '; ';
	}

	function createStyles() {
		if (!stylesCreated) {
			//opacity:0 works around a chrome bug https://code.google.com/p/chromium/issues/detail?id=286360
			var css = (animationKeyframes ? animationKeyframes : '') +
					'.resize-triggers { ' + (animationStyle ? animationStyle : '') + 'visibility: hidden; opacity: 0; } ' +
					'.resize-triggers, .resize-triggers > div, .contract-trigger:before { content: \" \"; display: block; position: absolute; top: 0; left: 0; height: 100%; width: 100%; overflow: hidden; } .resize-triggers > div { background: #eee; overflow: auto; } .contract-trigger:before { width: 200%; height: 200%; }',
				head = document.head || document.getElementsByTagName('head')[0],
				style = document.createElement('style');

			style.type = 'text/css';
			if (style.styleSheet) {
				style.styleSheet.cssText = css;
			} else {
				style.appendChild(document.createTextNode(css));
			}

			head.appendChild(style);
			stylesCreated = true;
		}
	}

	window.addResizeListener = function (element, fn) {
		if (attachEvent) element.attachEvent('onresize', fn);
		else {
			if (!element.__resizeTriggers__) {
				if (getComputedStyle(element).position == 'static') element.style.position = 'relative';
				createStyles();
				element.__resizeLast__ = {};
				element.__resizeListeners__ = [];
				(element.__resizeTriggers__ = document.createElement('div')).className = 'resize-triggers';
				element.__resizeTriggers__.innerHTML = '<div class="expand-trigger"><div></div></div>' +
					'<div class="contract-trigger"></div>';
				element.appendChild(element.__resizeTriggers__);
				resetTriggers(element);
				element.addEventListener('scroll', scrollListener, true);

				/* Listen for a css animation to detect element display/re-attach */
				animationstartevent && element.__resizeTriggers__.addEventListener(animationstartevent, function (e) {
					if (e.animationName == animationName)
						resetTriggers(element);
				});
			}
			element.__resizeListeners__.push(fn);
		}
	};

	window.removeResizeListener = function (element, fn) {
		if (attachEvent) element.detachEvent('onresize', fn);
		else {
			element.__resizeListeners__.splice(element.__resizeListeners__.indexOf(fn), 1);
			if (!element.__resizeListeners__.length) {
				element.removeEventListener('scroll', scrollListener);
				element.__resizeTriggers__ = !element.removeChild(element.__resizeTriggers__);
			}
		}
	}
}(jQuery));

/**
 * Imcms Events addon, has no dependencies and is cross-browser
 * Event can be fired in one place and catch in other
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 */
Imcms.Events = {
    /**
     * All events
     */
    all: [],

    /**
     * Fires event by name
     * @param name event's name
     */
    fire: function (name) {
        var callback = Imcms.Events.getCallback(name);
        if (callback) {
            callback.call();
        }
    },

    /**
     * Set handler on event by name
     * @param name event's name
     * @param callback function that will be called when event fires
     */
    on: function (name, callback) {
        var data = {}; // data object can be added in future and use in callback as 1st_arg.data
        Imcms.Events.all.push({
            name: name,
            data: data,
            callback: callback
        })
    },

    /**
     * Returns callback of event if such exists
     * @param name event's name
     * @returns {*|callback|null}
     */
    getCallback: function (name) {
        var event = Imcms.Events.all.find(function (event) {
            return event.name == name;
        });

        if (event && event.callback) {
            return event.callback;
        }
    },

    /**
     * Returns callback of event if such exists or specified function
     * @param name event's name
     * @param orFunc - function that will be returned if there are no any handler for event
     */
    getCallbackOr: function (name, orFunc) {
        var callback = Imcms.Events.getCallback(name);

        return (callback) ? callback : orFunc;
    }
};

/*
 * Date Format 1.2.3
 * (c) 2007-2009 Steven Levithan <stevenlevithan.com>
 * MIT license
 *
 * Includes enhancements by Scott Trenda <scott.trenda.net>
 * and Kris Kowal <cixar.com/~kris.kowal/>
 *
 * Accepts a date, a mask, or a date and a mask.
 * Returns a formatted version of the given date.
 * The date defaults to the current date/time.
 * The mask defaults to dateFormat.masks.default.
 *
 * See more at http://blog.stevenlevithan.com/archives/date-time-format
 */
var dateFormat = function () {
    var	token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,
        timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
        timezoneClip = /[^-+\dA-Z]/g,
        pad = function (val, len) {
            val = String(val);
            len = len || 2;
            while (val.length < len) val = "0" + val;
            return val;
        };

    // Regexes and supporting functions are cached through closure
    return function (date, mask, utc) {
        var dF = dateFormat;

        // You can't provide utc if you skip other args (use the "UTC:" mask prefix)
        if (arguments.length == 1 && Object.prototype.toString.call(date) == "[object String]" && !/\d/.test(date)) {
            mask = date;
            date = undefined;
        }

        // Passing date through Date applies Date.parse, if necessary
        date = date ? new Date(date) : new Date;
        if (isNaN(date)) throw SyntaxError("invalid date");

        mask = String(dF.masks[mask] || mask || dF.masks["default"]);

        // Allow setting the utc argument via the mask
        if (mask.slice(0, 4) == "UTC:") {
            mask = mask.slice(4);
            utc = true;
        }

        var	_ = utc ? "getUTC" : "get",
            d = date[_ + "Date"](),
            D = date[_ + "Day"](),
            m = date[_ + "Month"](),
            y = date[_ + "FullYear"](),
            H = date[_ + "Hours"](),
            M = date[_ + "Minutes"](),
            s = date[_ + "Seconds"](),
            L = date[_ + "Milliseconds"](),
            o = utc ? 0 : date.getTimezoneOffset(),
            flags = {
                d:    d,
                dd:   pad(d),
                ddd:  dF.i18n.dayNames[D],
                dddd: dF.i18n.dayNames[D + 7],
                m:    m + 1,
                mm:   pad(m + 1),
                mmm:  dF.i18n.monthNames[m],
                mmmm: dF.i18n.monthNames[m + 12],
                yy:   String(y).slice(2),
                yyyy: y,
                h:    H % 12 || 12,
                hh:   pad(H % 12 || 12),
                H:    H,
                HH:   pad(H),
                M:    M,
                MM:   pad(M),
                s:    s,
                ss:   pad(s),
                l:    pad(L, 3),
                L:    pad(L > 99 ? Math.round(L / 10) : L),
                t:    H < 12 ? "a"  : "p",
                tt:   H < 12 ? "am" : "pm",
                T:    H < 12 ? "A"  : "P",
                TT:   H < 12 ? "AM" : "PM",
                Z:    utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
                o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
                S:    ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
            };

        return mask.replace(token, function ($0) {
            return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
        });
    };
}();

// Some common format strings
dateFormat.masks = {
    "default":      "ddd mmm dd yyyy HH:MM:ss",
    shortDate:      "m/d/yy",
    mediumDate:     "mmm d, yyyy",
    longDate:       "mmmm d, yyyy",
    fullDate:       "dddd, mmmm d, yyyy",
    shortTime:      "h:MM TT",
    mediumTime:     "h:MM:ss TT",
    longTime:       "h:MM:ss TT Z",
    isoDate:        "yyyy-mm-dd",
    isoTime:        "HH:MM:ss",
    isoDateTime:    "yyyy-mm-dd'T'HH:MM:ss",
    isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"
};

// Internationalization strings
dateFormat.i18n = {
    dayNames: [
        "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    ],
    monthNames: [
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
    ]
};

// For convenience...
Date.prototype.format = function (mask, utc) {
    return dateFormat(this, mask, utc);
};