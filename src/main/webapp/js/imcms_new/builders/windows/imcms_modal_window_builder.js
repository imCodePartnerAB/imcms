Imcms.define("imcms-modal-window-builder",
    ["imcms-bem-builder", "imcms-components-builder", "jquery"],
    function (BEM, components, $) {
        var $modal, $shadow;

        function createModalWindow(question, callback) {
            function closeModal() {
                $modal.add($shadow).detach();
            }

            function buildHead() {
                return new BEM({
                    block: "imcms-head",
                    elements: {
                        "title": components.texts.titleText("<div>", "Confirmation")
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

            function buildFooter(callback) {
                var $yesButton = components.buttons.positiveButton({
                    text: "Yes",
                    click: function () {
                        callback(true);
                        closeModal();
                    }
                });

                var $noButton = components.buttons.negativeButton({
                    text: "No",
                    click: function () {
                        callback(false);
                        closeModal();
                    }
                });

                return new BEM({
                    block: "imcms-modal-footer",
                    elements: {
                        "button": [$yesButton, $noButton]
                    }
                }).buildBlockStructure("<div>");
            }

            return new BEM({
                block: "imcms-modal-window",
                elements: {
                    "modal-head": buildHead(),
                    "modal-body": buildBody(question),
                    "modal-footer": buildFooter(callback)
                }
            }).buildBlockStructure("<div>");
        }

        function createLayout() {
            return $("<div>", {"class": "imcms-modal-layout imcms-modal-layout--front"});
        }

        return {
            buildModalWindow: function (question, callback) {
                $modal = createModalWindow(question, callback);
                $shadow = createLayout();

                $("body").append($shadow, $modal);
            }
        };
    }
);
