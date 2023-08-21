define(
    'imcms-link-button-builder',
    [
        'imcms-bem-builder', 'imcms-buttons-builder', 'jquery'
    ],
    function (BEM, buttonBuilder, $) {

        function buildLinkButton(attributes) {
            let onClick = attributes.onClick ? attributes.onClick : () => window.open(attributes.link);

            return buttonBuilder.buttonWithIcon({
                button: buttonBuilder.neutralButton(({
                    text: attributes.title
                })),
                icon: $('<div>', {
                    'class': 'imcms-link-icon'
                }),
            }, {
                class: "imcms-link-button",
                click: onClick
            });
        }

    return {
        buildLinkButton: buildLinkButton
    };
});
