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

        function createLayout() {
            return $("<div>", {"class": "imcms-modal-layout imcms-modal-layout--front"});
        }

        function buildHead() {
            return new BEM({
                block: "imcms-head",
                elements: {
                    "title": components.texts.titleText("<div>", texts.title)
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
                    "modal-head": buildHead(),
                    "modal-body": buildBody(question),
                    "modal-footer": buildFooter(onConfirmed, onDeclined)
                }
            }).buildBlockStructure("<div>");
        }

        var ModalWindow = function (question, callback) {
            this.onConfirmed = this.buildOnDecide(true, callback);
            this.onDeclined = this.buildOnDecide(false, callback);
            this.$modal = createModalWindow(question, this.onConfirmed, this.onDeclined);
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

        function buildModalWindow(question, callback) {
            return new ModalWindow(question, callback)
                .addShadow()
                .appendTo($("body"));
        }

        module.exports = {
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
            }
        };
    }
);
