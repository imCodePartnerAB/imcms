define(
    "imcms-modal-window-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "jquery", "imcms-i18n-texts", "imcms-window-keys-controller",
        'imcms-cookies'
    ],
    function (BEM, components, $, texts, windowKeysController, cookies) {

        texts = texts.modal;

        function buildYesButton(callback) {
            return components.buttons.positiveButton({
                text: texts.yes,
                click: callback
            });
        }

        function buildNoButton(callback) {
            return components.buttons.negativeButton({
                text: texts.no,
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

        function buildCreateBody(text, textField, checkBoxIsDir) {
            return new BEM({
                block: "imcms-modal-body",
                elements: {
                    "text": components.texts.infoText("<div>", text),
                    "name": textField,
                    "is-directory": checkBoxIsDir
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

        function fileCreateModalWindow(question, textField, checkBoxIsDir, onConfirmed, onDeclined) {
            return new BEM({
                block: "imcms-create-modal-window",
                elements: {
                    "modal-head": buildHead(texts.title),
                    "modal-body": buildCreateBody(question, textField, checkBoxIsDir),
                    "modal-footer": buildFooter(onConfirmed, onDeclined)
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

        const CreateModalWindow = function (question, textField, checkBoxIsDir, callback) {
            this.onConfirmed = this.buildOnDecide(true, callback);
            this.onDeclined = this.buildOnDecide(false, callback);
            this.$modal = fileCreateModalWindow(question, textField, checkBoxIsDir, this.onConfirmed, this.onDeclined);
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

        CreateModalWindow.prototype = {
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

        ModalErrorWindow.prototype = Object.create(ModalWindow.prototype);
        ModalErrorWindow.prototype.confirmAction = function (callback) {
            const context = this;
            return () => {
                if (callback) {
                    callback();
                }
                context.closeModal();
                return false;
            };
        };

        function buildModalWindow(question, callback) {
            return new ModalWindow(question, callback)
                .addShadow()
                .appendTo($("body"));
        }

        function buildCreateModalWindow(question, textField, checkBoxIsDir, callback) {
            return new CreateModalWindow(question, textField, checkBoxIsDir, callback)
                .addShadow()
                .appendTo($("body"));
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
            buildModalWindow: buildModalWindow,
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
            buildWarningWindow: (message, callback) => {
                buildWarningWindow(message, callback);
            },
            buildErrorWindow: (message, callback) => {
                buildErrorWindow(message, callback);
            },
            buildCreateFileModalWindow: (question, textField, checkBoxIsDir, onConfirm) => {
                buildCreateModalWindow(question, textField, checkBoxIsDir, confirm => {
                    if (confirm) {
                        onConfirm.call();
                    }
                });
            }
        };
    }
);
