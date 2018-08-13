/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
Imcms.define(
    "imcms-text-discard-changes-plugin",
    ["imcms-modal-window-builder", "tinyMCE", "imcms-events", 'imcms-text-editor-toolbar-button-builder'],
    function (modalWindowBuilder, tinyMCE, events, toolbarButtonBuilder) {

        var title = 'Discard changes'; // todo: localize!
        var discardChangesMessage = "Discard changes?"; // todo: localize!

        function onDiscardChangesClick() {
            events.trigger("disable text editor blur");
            modalWindowBuilder.buildModalWindow(discardChangesMessage, function (isDiscard) {
                if (isDiscard) {
                    /** @namespace tinyMCE.activeEditor.startContent */
                    /** @namespace tinyMCE.activeEditor.bodyElement */
                    // tinyMCE.activeEditor.bodyElement.innerHTML = tinyMCE.activeEditor.startContent;
                    tinyMCE.activeEditor.setContent(tinyMCE.activeEditor.startContent);
                    tinyMCE.activeEditor.save();
                }
                events.trigger("enable text editor blur");
            });
        }

        function setEnablingStrategy() {
            var button = this;

            events.on("disable discard changes button", function () {
                button.disabled(true);
            });

            events.on("enable discard changes button", function () {
                button.disabled(false);
            });
        }

        return {
            pluginName: 'discard-changes',
            initDiscardChanges: function (editor) {

                editor.addButton(this.pluginName, {
                    icon: 'imcms-discard-changes-icon',
                    tooltip: title,
                    onclick: onDiscardChangesClick,
                    onPostRender: setEnablingStrategy
                });

                editor.on('NodeChange', function () {
                    var eventName = (tinyMCE.activeEditor.isDirty())
                        ? "enable discard changes button"
                        : "disable discard changes button";

                    events.trigger(eventName);
                });
            },
            buildPlainTextButton: function (activeTextEditor) {
                var $btn;

                activeTextEditor.$().on('DOMSubtreeModified', function () {
                    $btn.removeClass('text-toolbar__button--disabled');
                });

                var onClick = function () {
                    modalWindowBuilder.buildModalWindow(discardChangesMessage, function (isDiscard) {
                        if (!isDiscard) return;

                        activeTextEditor.setContent(activeTextEditor.startContent);
                        activeTextEditor.setDirty(false);

                        activeTextEditor.$()
                            .parent()
                            .find('.text-editor-save-button')
                            .addClass('text-toolbar__button--disabled');

                        $btn.addClass('text-toolbar__button--disabled');
                    });
                };

                return $btn = toolbarButtonBuilder.buildButton(
                    'text-editor-discard-changes-button', title, onClick, true
                )
            }
        }
    }
);
