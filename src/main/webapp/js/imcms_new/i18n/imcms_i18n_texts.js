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
            }
        }
    };

    return texts[imcms.userLanguage];
});
