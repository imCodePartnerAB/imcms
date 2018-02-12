/**
 * Module holds texts used in UI in all available languages.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
Imcms.define("imcms-i18n-texts", ["imcms"], function (imcms) {

    var texts = {
        "sv": {
            panel: {
                public: "offentlig",
                edit: "redigera",
                preview: "förhandsvisning",
                publish: "publicera offline",
                pageInfo: "sida info",
                document: "dokumentera",
                admin: "administration",
                logout: "logga ut"
            },
            pageInfo: {
                title: {
                    title: "Titel",
                    menuText: "Titel",
                    linkToImage: "Menytext",
                    linkToImagePlaceholder: "Länk till bild",
                    chooseImage: "Bildväg",
                    showIn: "välja...",
                    sameFrame: "Visa in",
                    newWindow: "Samma ram",
                    replaceAll: "Nytt fönster",
                    alias: "Ersätt alla",
                    aliasPlaceholder: "Dokumentalias",
                    missingLangRule: "detta dokument alias",
                    showInDefault: "Om det begärda språket saknas:",
                    doNotShow: "Visa i standardspråk om det är aktiverat"
                }
            }
        },
        "en": {
            panel: {
                public: 'public',
                edit: 'edit',
                preview: "preview",
                publish: "publish offline",
                pageInfo: "page info",
                document: "document",
                admin: "admin",
                logout: "log out"
            },
            pageInfo: {
                title: {
                    name: "Title",
                    title: "Title",
                    menuText: "Menu text",
                    linkToImage: "Link to image",
                    linkToImagePlaceholder: "Image path",
                    chooseImage: "choose...",
                    showIn: "Show in",
                    sameFrame: "Same frame",
                    newWindow: "New window",
                    replaceAll: "Replace all",
                    alias: "Document alias",
                    aliasPlaceholder: "this-doc-alias",
                    missingLangRuleTitle: "If requested language is missing:",
                    showInDefault: "Show in default language if enabled",
                    doNotShow: "Don't show at all"
                }
            }
        }
    };

    return texts[imcms.userLanguage];
});
