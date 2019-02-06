/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
define(
    "imcms-text-discard-changes-plugin",
    ["imcms-modal-window-builder", "tinymce", "imcms-events", 'imcms-text-editor-toolbar-button-builder'],
    function (modalWindowBuilder, tinyMCE, events, toolbarButtonBuilder) {

        var title = 'Discard changes'; // todo: localize!
        var discardChangesMessage = "Discard changes?"; // todo: localize!

        function onDiscardChangesClick() {
            events.trigger("disable text editor blur");
            modalWindowBuilder.buildModalWindow(discardChangesMessage, isDiscard => {
                if (isDiscard) {
                    tinyMCE.activeEditor.setContent(tinyMCE.activeEditor.startContent);
                    tinyMCE.activeEditor.save();
                }
                events.trigger("enable text editor blur");
            });
        }

        function setEnablingStrategy() {
            var button = this;
            button.disabled(true);

            events.on("disable discard changes button", () => {
                button.disabled(true);
            });

            events.on("enable discard changes button", () => {
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

                editor.on('NodeChange', () => {
                    var eventName = (tinyMCE.activeEditor.isDirty())
                        ? "enable discard changes button"
                        : "disable discard changes button";

                    events.trigger(eventName);
                });
            },
            buildPlainTextButton: activeTextEditor => {
                var $btn;

                activeTextEditor.$().on('change keyup paste', () => {
                    $btn.removeClass('text-toolbar__button--disabled');
                });

                var onClick = () => {
                    modalWindowBuilder.buildModalWindow(discardChangesMessage, isDiscard => {
                        if (!isDiscard) return;

                        activeTextEditor.setContent(activeTextEditor.startContent);
                        activeTextEditor.setDirty(false);
                    });
                };

                return $btn = toolbarButtonBuilder.buildButton(
                    'text-editor-discard-changes-button', title, onClick, true
                )
            }
        }
    }
);
