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
                    name: "Titel",
                    title: "Titel",
                    menuText: "Titel",
                    linkToImage: "Meny text",
                    linkToImagePlaceholder: "Länk till bild",
                    chooseImage: "Bildväg",
                    showIn: "välja...",
                    sameFrame: "Visa in",
                    newWindow: "Samma ram",
                    replaceAll: "Nytt fönster",
                    alias: "Ersätt alla",
                    aliasPlaceholder: "Dokument alias",
                    missingLangRule: "detta-dokument-alias",
                    showInDefault: "Om det begärda språket saknas:",
                    doNotShow: "Visa i standardspråk om det är aktiverat"
                },
                lifeCycle: {
                    name: "Livscykel",
                    status: {
                        title: "Status",
                        inProcess: "Pågående",
                        disapproved: "Ej godkänd",
                        approved: "Godkänd"
                    },
                    now: "Nu",
                    clear: "Klar",
                    published: {
                        title: "Publicerat",
                        dateTitle: "Ange publicerat datum",
                        timeTitle: "Ange publicerad tid",
                        dateTimeTitle: "Sparade datum och tid för publicering"
                    },
                    archived: {
                        title: "Arkiverat ",
                        dateTitle: "Ange arkiverat datum",
                        timeTitle: "Ange arkiverad tid",
                        dateTimeTitle: "Sparade arkiverad datum-tid"
                    },
                    publicationEnd: {
                        title: "Publiceringsänden",
                        dateTitle: "Ange publikationsslut datum",
                        timeTitle: "Ange publikationsslut tid",
                        dateTimeTitle: "Sparat slutet av publikationen datum-tid"
                    },
                    publisher: "Utgivare",
                    currentVersion: "Aktuell version:",
                    versionHasChanges: "Denna offline version har ändringar.",
                    publishMessage: "Vänligen tryck \"Spara och publicera den här versionen\" för att publicera som: version"
                },
                appearance: {
                    name: "Utseende",
                    template: "Mall",
                    defaultChildTemplate: "Standard barnmall"
                },
                keywords: {
                    name: "Nyckelord",
                    title: "Nyckelord",
                    placeholder: "nyckelord",
                    add: "TILLÄGGA+",
                    disableSearch: "Inaktivera sökningen"
                },
                categories: {
                    name: "Kategorier"
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
                },
                lifeCycle: {
                    name: "Life Cycle",
                    status: {
                        title: "Status",
                        inProcess: "In Process",
                        disapproved: "Disapproved",
                        approved: "Approved"
                    },
                    now: "Now",
                    clear: "Clear",
                    published: {
                        title: "Published",
                        dateTitle: "Set published date",
                        timeTitle: "Set published time",
                        dateTimeTitle: "Saved publish date-time"
                    },
                    archived: {
                        title: "Archived",
                        dateTitle: "Set archived date",
                        timeTitle: "Set archived time",
                        dateTimeTitle: "Saved archived date-time"
                    },
                    publicationEnd: {
                        title: "Publication end",
                        dateTitle: "Set publication end date",
                        timeTitle: "Set publication end time",
                        dateTimeTitle: "Saved publication end date-time"
                    },
                    publisher: "Publisher",
                    currentVersion: "Current version:",
                    versionHasChanges: "This offline version has changes.",
                    publishMessage: "Please press \"Save and publish this version\" to publish as: version "
                },
                appearance: {
                    name: "Appearance",
                    template: "Template",
                    defaultChildTemplate: "Default child template"
                },
                keywords: {
                    name: "Keywords",
                    title: "Keywords",
                    placeholder: "keyword",
                    add: "ADD+",
                    disableSearch: "Disable search"
                },
                categories: {
                    name: "Categories"
                }
            }
        }
    };

    return texts[imcms.userLanguage];
});
