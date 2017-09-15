/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 26.07.17.
 */
Imcms.define("imcms-choose-image-builder",
    ["imcms-bem-builder", "imcms-texts-builder", "imcms-buttons-builder", "imcms-content-manager-builder"],
    function (BEM, texts, buttons, contentManager) {
        return {
            container: function (tag, attributes) {
                return new BEM({
                    block: "imcms-choose-image",
                    elements: {
                        "text-box": texts.textField("<div>", {
                            id: attributes.id,
                            name: attributes.name,
                            text: attributes["label-text"],
                            placeholder: attributes.placeholder
                        }),
                        "button": buttons.neutral("<button>", {
                            text: attributes["button-text"],
                            click: attributes.click || contentManager.build
                        })
                    }
                }).buildBlockStructure("<div>");
            }
        }
    }
);
