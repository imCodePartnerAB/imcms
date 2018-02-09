/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
Imcms.define(
    "imcms-text-discard-changes-plugin",
    ["imcms-modal-window-builder", "tinyMCE", "imcms-events"],
    function (modalWindowBuilder, tinyMCE, events) {

        function onDiscardChangesClick() {
            events.trigger("disable text editor blur");
            modalWindowBuilder.buildModalWindow("Discard changes?", function (isDiscard) {
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
                    tooltip: 'Discard changes',
                    onclick: onDiscardChangesClick,
                    onPostRender: setEnablingStrategy
                });

                editor.on('NodeChange', function () {
                    if (tinyMCE.activeEditor.isDirty()) {
                        events.trigger("enable discard changes button");

                    } else {
                        events.trigger("disable discard changes button");
                    }
                });
            }
        };
    }
);
