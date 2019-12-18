define(
    "imcms-modal-window-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "jquery", "imcms-i18n-texts", "imcms-window-keys-controller",
        'imcms-cookies'
    ],
    function (BEM, components, $, texts, windowKeysController, cookies) {

        texts = texts.modal;

        function buildOkButton(callback) {
            return components.buttons.positiveButton({
                text: texts.ok,
                click: callback
            });
        }

        function buildYesButton(callback) {
            return components.buttons.positiveButton({
                text: texts.yes,
                click: callback
            });
        }

        function buildSaveButton(callback) {
            return components.buttons.positiveButton({
                text: texts.save,
                click: callback
            });
        }

        function buildCreateButton(callback) {
            return components.buttons.positiveButton({
                text: texts.create,
                click: callback
            });
        }

        function buildNoButton(callback) {
            return components.buttons.negativeButton({
                text: texts.no,
                click: callback
            });
        }

        function buildCancelButton(callback) {
            return components.buttons.negativeButton({
                text: texts.cancel,
                click: callback
            });
        }

        function buildWarningButton(callback) {
            return components.buttons.warningButton({
                text: texts.ok,
                click: callback
            });
        }

        function buildErrorButton(callback) {
            return components.buttons.errorButton({
                text: texts.ok,
                click: callback
            });
        }

        function buildFooter(onConfirmed, onDeclined) {
            const $yesButton = buildYesButton(onConfirmed);
            const $noButton = buildNoButton(onDeclined);

            return new BEM({
                block: "imcms-modal-footer",
                elements: {
                    "button": [$yesButton, $noButton]
                }
            }).buildBlockStructure("<div>");
        }

        function buildSaveFooter(onConfirmed, onDeclined) {
            const $saveButton = buildSaveButton(onConfirmed);
            const $cancelButton = buildCancelButton(onDeclined);

            return new BEM({
                block: "imcms-modal-footer",
                elements: {
                    "button": [$saveButton, $cancelButton]
                }
            }).buildBlockStructure("<div>");
        }

        function buildCreateFooter(onConfirmed, onDeclined) {
            const $createButton = buildCreateButton(onConfirmed);
            const $cancelButton = buildCancelButton(onDeclined);

            return new BEM({
                block: "imcms-modal-footer",
                elements: {
                    "button": [$createButton, $cancelButton]
                }
            }).buildBlockStructure("<div>");
        }

        function buildOkFooter(onConfirmed) {
            const $okButton = buildOkButton(onConfirmed);

            return new BEM({
                block: "imcms-modal-footer",
                elements: {
                    "button": [$okButton]
                }
            }).buildBlockStructure("<div>");
        }

        function buildWarningFooter(onConfirmed) {
            const $warningButton = buildWarningButton(onConfirmed);

            return new BEM({
                block: "imcms-modal-footer",
                elements: {
                    "button": [$warningButton]
                }
            }).buildBlockStructure("<div>");
        }

        function buildErrorFooter(onConfirmed) {
            const $errorButton = buildErrorButton(onConfirmed);

            return new BEM({
                block: "imcms-modal-footer",
                elements: {
                    "button": [$errorButton]
                }
            }).buildBlockStructure("<div>");
        }

        function createLayout() {
            return $("<div>", {"class": "imcms-modal-layout imcms-modal-layout--front"});
        }

        function buildHead(titleText) {
            return new BEM({
                block: "imcms-head",
                elements: {
                    "title": components.texts.titleText("<div>", titleText)
                }
            }).buildBlockStructure("<div>");
        }

        function buildBody(text) {
            return new BEM({
                block: "imcms-modal-body",
                elements: {
                    "text": components.texts.infoText("<div>", text)
                }
            }).buildBlockStructure("<div>");
        }

        function buildCreateFileBody(textField, checkBoxIsDir) {
            return new BEM({
                block: "imcms-modal-body",
                elements: {
                    "name": textField,
                    "is-directory": checkBoxIsDir
                }
            }).buildBlockStructure("<div>");
        }

        function buildEditFileBody(textField, textarea, editCheckBox) {
            return new BEM({
                block: "imcms-modal-body",
                elements: {
                    "name": textField,
                    "edit-content": editCheckBox,
                    "content": textarea
                }
            }).buildBlockStructure("<div>");
        }

        function buildEditDirectoryBody(textField) {
            return new BEM({
                block: "imcms-modal-body",
                elements: {
                    "name": textField,
                }
            }).buildBlockStructure("<div>");
        }

        function buildOptionalBody(message, optionalBox) {
            return new BEM({
                block: 'imcms-modal-body',
                elements: {
                    'text': components.texts.infoText("<div>", message),
                    'optional-box': optionalBox
                }
            }).buildBlockStructure("<div>");
        }

        function buildViewBody(viewBox) {
            return new BEM({
                block: 'imcms-modal-body',
                elements: {
                    'view-box': viewBox
                }
            }).buildBlockStructure("<div>");
        }

        function buildHTMLBody(htmlText) {
            return new BEM({
                block: "imcms-modal-body",
                elements: {
                    "text": components.texts.infoHtml("<div>", htmlText)
                }
            }).buildBlockStructure("<div>");
        }

        function createModalWindow(question, onConfirmed, onDeclined) {
            return new BEM({
                block: "imcms-modal-window",
                elements: {
                    "modal-head": buildHead(texts.title),
                    "modal-body": buildBody(question),
                    "modal-footer": buildFooter(onConfirmed, onDeclined)
                }
            }).buildBlockStructure("<div>");
        }

        function CreateCreateFileModalWindow(textField, checkBoxIsDir, onConfirmed, onDeclined) {
            return new BEM({
                block: "imcms-create-modal-window",
                elements: {
                    "modal-head": buildHead(texts.createFileOrDirectory),
                    "modal-body": buildCreateFileBody(textField, checkBoxIsDir),
                    "modal-footer": buildCreateFooter(onConfirmed, onDeclined)
                }
            }).buildBlockStructure("<div>");
        }

        function CreateEditFileModalWindow(textField, textarea, editCheckBox, onConfirmed, onDeclined) {
            return new BEM({
                block: "imcms-edit-modal-window",
                elements: {
                    "modal-head": buildHead(texts.editFile),
                    "modal-body": buildEditFileBody(textField, textarea, editCheckBox),
                    "modal-footer": buildSaveFooter(onConfirmed, onDeclined)
                }
            }).buildBlockStructure("<div>");
        }

        function CreateEditDirectoryModalWindow(textField, onConfirmed, onDeclined) {
            return new BEM({
                block: "imcms-edit-modal-window",
                elements: {
                    "modal-head": buildHead(texts.editDirectory),
                    "modal-body": buildEditDirectoryBody(textField),
                    "modal-footer": buildSaveFooter(onConfirmed, onDeclined)
                }
            }).buildBlockStructure("<div>");
        }

        function createModalOptionalWindow(message, optionalBox, onConfirmed, onDeclined) {
            return new BEM({
                block: 'imcms-modal-optional-window',
                elements: {
                    "modal-head": buildHead(texts.title),
                    "modal-body": buildOptionalBody(message, optionalBox),
                    "modal-footer": buildFooter(onConfirmed, onDeclined)
                }
            }).buildBlockStructure("<div>");
        }

        function createModalViewWindow(viewBox, onConfirmed) {
            return new BEM({
                block: 'imcms-modal-view-window',
                elements: {
                    "modal-body": buildViewBody(viewBox),
                    "modal-footer": buildOkFooter(onConfirmed)
                }
            }).buildBlockStructure("<div>");
        }

        function createModalWarningWindow(message, onConfirmed) {
            return new BEM({
                block: "imcms-modal-window",
                elements: {
                    "modal-head": buildHead(),
                    "modal-body": buildHTMLBody(message),
                    "modal-footer": buildWarningFooter(onConfirmed)
                }
            }).buildBlockStructure("<div>", {"class": "imcms-modal-window--warning"});
        }

        function createModalErrorWindow(message, onConfirmed) {
            return new BEM({
                block: "imcms-modal-window",
                elements: {
                    "modal-head": buildHead(),
                    "modal-body": buildHTMLBody(message),
                    "modal-footer": buildErrorFooter(onConfirmed)
                }
            }).buildBlockStructure("<div>", {"class": "imcms-modal-window--error"});
        }

        const ModalWindow = function (question, callback) {
            this.onConfirmed = this.buildOnDecide(true, callback);
            this.onDeclined = this.buildOnDecide(false, callback);
            this.$modal = createModalWindow(question, this.onConfirmed, this.onDeclined);
        };

        const CreateFileModalWindow = function (textField, checkBoxIsDir, callback) {
            this.onConfirmed = this.buildOnDecide(true, callback);
            this.onDeclined = this.buildOnDecide(false, callback);
            this.$modal = CreateCreateFileModalWindow(textField, checkBoxIsDir, this.onConfirmed, this.onDeclined);
        };

        const EditFileModalWindow = function (textField, textarea, editCheckBox, callback) {
            this.onConfirmed = this.buildOnDecide(true, callback);
            this.onDeclined = this.buildOnDecide(false, callback);
            this.$modal = CreateEditFileModalWindow(
                textField, textarea, editCheckBox, this.onConfirmed, this.onDeclined
            );
        };

        const EditDirectoryModalWindow = function (textField, callback) {
            this.onConfirmed = this.buildOnDecide(true, callback);
            this.onDeclined = this.buildOnDecide(false, callback);
            this.$modal = CreateEditDirectoryModalWindow(
                textField, this.onConfirmed, this.onDeclined
            );
        };

        const ModalOptionalWindow = function (message, optionalBox, callback) {
            this.onConfirmed = this.buildOnDecide(true, callback);
            this.onDeclined = this.buildOnDecide(false, callback);
            this.$modal = createModalOptionalWindow(message, optionalBox, this.onConfirmed, this.onDeclined);
        };

        const ModalViewWindow = function (viewBox, callback) {
            this.onConfirmed = this.confirmAction(callback);
            this.$modal = createModalViewWindow(viewBox, this.onConfirmed);
        };

        const ModalWarningWindow = function (message, callback) {
            this.onConfirmed = this.confirmAction(callback);
            this.$modal = createModalWarningWindow(message, this.onConfirmed);
        };

        const ModalErrorWindow = function (message, callback) {
            this.onConfirmed = this.confirmAction(callback);
            this.$modal = createModalErrorWindow(message, this.onConfirmed);
        };

        ModalWindow.prototype = {
            buildOnDecide: function (isConfirm, callback) {
                const context = this;

                return () => {
                    callback(isConfirm);
                    context.closeModal();
                    return false;
                };
            },

            addShadow: function () {
                this.$shadow = createLayout();
                return this;
            },

            closeModal: function () {
                windowKeysController.unRegister();
                this.$modal.remove();
                this.$shadow && this.$shadow.remove();
            },

            appendTo: function ($appendToMe) {
                $appendToMe.append(this.$modal, this.$shadow);
                windowKeysController.registerWindow(this.onDeclined, this.onConfirmed);
                return this;
            }
        };

        CreateFileModalWindow.prototype = Object.create(ModalWindow.prototype);

        EditFileModalWindow.prototype = Object.create(ModalWindow.prototype);
        EditDirectoryModalWindow.prototype = Object.create(ModalWindow.prototype);

        ModalOptionalWindow.prototype = Object.create(ModalWindow.prototype);

        ModalWarningWindow.prototype = Object.create(ModalWindow.prototype);
        ModalWarningWindow.prototype.confirmAction = function (callback) {
            const context = this;
            return () => {
                if (callback) {
                    callback();
                }
                context.closeModal();
                return false;
            };
        };

        ModalErrorWindow.prototype = Object.create(ModalWarningWindow.prototype);

        ModalViewWindow.prototype = Object.create(ModalWarningWindow.prototype);

        function buildModalWindow(question, callback) {
            return new ModalWindow(question, callback)
                .addShadow()
                .appendTo($("body"));
        }

        function buildCreateFileModalWindow(textField, checkBoxIsDir, callback) {
            return new CreateFileModalWindow(textField, checkBoxIsDir, callback)
                .addShadow()
                .appendTo($("body"));
        }

        function buildEditFileModalWindow(textField, textarea, editCheckBox, callback) {
            return new EditFileModalWindow(textField, textarea, editCheckBox, callback)
                .addShadow()
                .appendTo($("body"));
        }

        function buildEditDirectoryModalWindow(textField, callback) {
            return new EditDirectoryModalWindow(textField, callback)
                .addShadow()
                .appendTo($("body"));
        }

        function buildOptionalModalWindow(message, optionalBox, callback) {
            return new ModalOptionalWindow(message, optionalBox, callback)
                .addShadow()
                .appendTo($('body'));
        }

        function buildViewModalWindow(viewBow, callback) {
            return new ModalViewWindow(viewBow, callback)
                .addShadow()
                .appendTo($('body'));
        }

        function buildWarningWindow(message, callback) {
            return new ModalWarningWindow(message, callback)
                .addShadow()
                .appendTo($("body"));
        }

        function buildErrorWindow(message, callback) {
            return new ModalErrorWindow(message, callback)
                .addShadow()
                .appendTo($("body"));
        }

        module.exports = {
            buildModalWindow,
            buildWarningWindow,
            buildErrorWindow,
            buildOptionalModalWindow,
            buildViewModalWindow,
            buildCreateFileModalWindow,
            buildEditFileModalWindow,
            buildEditDirectoryModalWindow,
            buildConfirmWindow: (question, onConfirm) => {
                buildModalWindow(question, confirm => {
                    confirm && onConfirm.call();
                });
            },
            buildConfirmWindowWithDontShowAgain: (question, onConfirm, cookieName) => {
                const cookie = cookies.getCookie(cookieName);
                const doNotShow = "do-not-show";

                if (cookie === doNotShow) {
                    onConfirm.call();
                    return;
                }

                const $checkbox = components.checkboxes.imcmsCheckbox('<div>', {
                    text: texts.doNotShowAgain
                });

                const modalWindow = buildModalWindow(question, confirm => {
                    if (confirm) {
                        if ($checkbox.isChecked()) {
                            cookies.setCookie(cookieName, doNotShow, {expires: 30})
                        }
                        onConfirm.call();
                    }
                });

                modalWindow.$modal.find('.imcms-modal-body').append($checkbox);
            },
        }
    }
);
