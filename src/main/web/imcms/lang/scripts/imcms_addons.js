/**
 * Created by Shadowgun on 12.02.2015.
 */

/**
 * Extension for Array
 * @param {*} value
 */
Array.prototype.remove = function (value) {
    while (true) {
        var index = this.indexOf(value);
        if (index < 0) return;
        this.splice(index, 1);
    }
};
/*
 JQuery Addons Data
 return all attr in tag that started with `data-`
 */

(function ($) {
    var $super = $.fn.data;
    $.fn.data = function () {
        if (arguments.length === 0) {
            if (this.length === 0) {
                console.error("No element");
                return null;
            }

            var uglyResult = {
                prettify: function () {
                    var prettyResult = {};
                    for (var property in this) {
                        if (this.hasOwnProperty(property)) {
                            prettyResult[property.replace("data-", "")] = this[property];
                        }
                    }

                    return prettyResult;
                }
            };
            $.each(this[0].attributes, function () {
                if (this.specified && this.name.indexOf("data-") > -1) {
                    uglyResult[this.name] = this.value;
                }
            });

            return uglyResult;
        }

        return $super.apply(this, arguments);
    };
})(jQuery);
/*
 CKEditor Addons
 */
CKEDITOR.define("confirmChanges", {});
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
        var confirmCommandFunction = function (e) {
            CKEDITOR.fire("confirmChanges", {}, e);
            editor.documentSnapshot = e.getData();
            e.resetDirty();
            e.removeListener('blur', e.blurHandler);
            e.focusManager.unlock();
            e.element.$.blur();
            e.focusManager.blur();
            e.on("focus", e.focusHandler);
        };
        var confirmCommandDefinition =
        {
            // This command works in both editing modes.
            modes: {wysiwyg: 1, source: 1},

            // This command will not auto focus editor before execution.
            editorFocus: false,

            // This command requires no undo snapshot.
            canUndo: false,

            exec: confirmCommandFunction
        };
        editor.addCommand("confirmChanges", confirmCommandDefinition);
        var cancelCommandFunction = function (e) {
            e.setData(e.documentSnapshot);
            e.removeListener('blur', e.blurHandler);
            e.focusManager.unlock();
            e.element.$.blur();
            e.focusManager.blur();
            e.on("focus", e.focusHandler);
        };
        var cancelCommandDefinition =
        {
            // This command works in both editing modes.
            modes: {wysiwyg: 1, source: 1},

            // This command will not auto focus editor before execution.
            editorFocus: false,

            // This command requires no undo snapshot.
            canUndo: false,

            exec: cancelCommandFunction
        };
        editor.on('blur', editor.blurHandler);


        editor.addCommand("cancelChanges", cancelCommandDefinition);
        editor.ui.addButton('confirm',
            {
                label: 'Save all changes',
                command: "confirmChanges",
                icon: "images/ic_apply.png"
            });
        editor.ui.addButton('cancel',
            {
                label: 'Cancel all changes and restore document to previous state',
                command: "cancelChanges",
                icon: "images/ic_cancel.png"
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

        var openBrowserCommandDefinition =
        {
            // This command works in both editing modes.
            modes: {wysiwyg: 1, source: 1},

            // This command will not auto focus editor before execution.
            editorFocus: false,

            // This command requires no undo snapshot.
            canUndo: false,

            exec: onChooseFile
        };
        editor.addCommand("openBrowser", openBrowserCommandDefinition);


        editor.ui.addButton('openBrowser',
            {
                label: 'Open Image Browser',
                command: "openBrowser",
                icon: "images/ic_apply.png"
            });
    }
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
    }

    $.fn.removeResize = function (callback) {
        return this.each(function () {
            removeResizeListener(this, callback);
        });
    }

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
        };

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
        };

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
