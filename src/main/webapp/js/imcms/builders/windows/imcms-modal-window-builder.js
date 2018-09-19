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

        function buildFooter(onConfirmed, onDeclined) {
            var $yesButton = buildYesButton(onConfirmed);
            var $noButton = buildNoButton(onDeclined);

            return new BEM({
                block: "imcms-modal-footer",
                elements: {
                    "button": [$yesButton, $noButton]
                }
            }).buildBlockStructure("<div>");
        }

        function buildWarningFooter(onConfirmed) {
            var $warningButton = buildWarningButton(onConfirmed);

            return new BEM({
                block: "imcms-modal-footer",
                elements: {
                    "button": [$warningButton]
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

        function buildBody(question) {
            return new BEM({
                block: "imcms-modal-body",
                elements: {
                    "text": components.texts.infoText("<div>", question)
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

        function createModalWarningWindow(message, onConfirmed) {
            return new BEM({
                block: "imcms-modal-window",
                elements: {
                    "modal-head": buildHead(),
                    "modal-body": buildBody(message),
                    "modal-footer": buildWarningFooter(onConfirmed)
                }
            }).buildBlockStructure("<div>", {"class": "imcms-modal-window--warning"},);
        }

        var ModalWindow = function (question, callback) {
            this.onConfirmed = this.buildOnDecide(true, callback);
            this.onDeclined = this.buildOnDecide(false, callback);
            this.$modal = createModalWindow(question, this.onConfirmed, this.onDeclined);
        };

        var ModalWarningWindow = function (message, callback) {
            this.onConfirmed = this.confirmAction(callback);
            this.$modal = createModalWarningWindow(message, this.onConfirmed);
        };

        ModalWindow.prototype = {
            buildOnDecide: function (isConfirm, callback) {
                var context = this;

                return function () {
                    callback(isConfirm);
                    context.closeModal();
                    return false;
                }
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
            var context = this;
            return function () {
                callback();
                context.closeModal();
                return false;
            }
        };

        function buildModalWindow(question, callback) {
            return new ModalWindow(question, callback)
                .addShadow()
                .appendTo($("body"));
        }

        function buildWarningWindow(message, callback) {
            return new ModalWarningWindow(message, callback)
                .addShadow()
                .appendTo($("body"));
        }

        return {
            buildModalWindow: buildModalWindow,
            buildConfirmWindow: function (question, onConfirm) {
                buildModalWindow(question, function (confirm) {
                    confirm && onConfirm.call();
                })
            },
            buildConfirmWindowWithDontShowAgain: function (question, onConfirm, cookieName) {
                var cookie = cookies.getCookie(cookieName);
                var doNotShow = "do-not-show";

                if (cookie === doNotShow) {
                    onConfirm.call();
                    return;
                }

                var $checkbox = components.checkboxes.imcmsCheckbox('<div>', {
                    text: 'Do not show again'
                });

                var modalWindow = buildModalWindow(question, function (confirm) {
                    if (confirm) {
                        if ($checkbox.isChecked()) {
                            cookies.setCookie(cookieName, doNotShow, {expires: 30})
                        }
                        onConfirm.call();
                    }
                });

                modalWindow.$modal.find('.imcms-modal-body').append($checkbox);
            },
            buildWarningWindow: function (message, callback) {
                buildWarningWindow(message, callback)
            }
        };
    }
);
