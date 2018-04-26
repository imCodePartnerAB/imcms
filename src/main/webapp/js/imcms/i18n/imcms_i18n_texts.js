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
                publicTitle: 'Visar den publicerade vyn',
                edit: "redigera",
                editTitle: 'Visar den redigerbara arbetsversionen',
                preview: "förhandsvisning",
                previewTitle: 'Visar den redigerbara arbetsversionen',
                publish: "publicera",
                publishTitle: 'Publicerar arbetsversionen direkt',
                pageInfo: "sid-info",
                pageInfoTitle: 'Visar alla sidinställningar och metadata',
                document: "dokument",
                documentTitle: 'Visar alla dokument i dokumentredigeraren',
                admin: "admin",
                adminTitle: 'Visar alla systeminställningar (endast för systemadministratörer)',
                logout: "logga ut"
            },
            pageInfo: {
                document: "Dokument",
                newDocument: "Nytt dokument",
                confirmMessage: "Spara ändringar?",
                confirmMessageOnCancel: "Vill du verkligen avsluta?",
                title: {
                    name: "Rubrik",
                    title: "Rubrik",
                    menuText: "Menytext",
                    linkToImage: "Bild URL",
                    linkToImagePlaceholder: "Länk till bild",
                    chooseImage: "Välj bild",
                    showIn: "Visa i",
                    sameFrame: "Samma ram",
                    newWindow: "Nytt fönster",
                    replaceAll: "Ersätt alla",
                    alias: "Dokumentets alias",
                    aliasPlaceholder: "detta-dokument-alias",
                    missingLangRule: "Om det begärda språket saknas:",
                    showInDefault: "Visa i standardspråk om det är aktiverat",
                    doNotShow: "Visa inte alls"
                },
                lifeCycle: {
                    name: "Livscykel",
                    status: {
                        title: "Status",
                        inProcess: "Nytt",
                        disapproved: "Ej godkänd",
                        approved: "Godkänd"
                    },
                    now: "Nu",
                    clear: "Rensa",
                    published: {
                        title: "Publicerad",
                        dateTitle: "Ange publicerat datum",
                        timeTitle: "Ange publicerad tid",
                        dateTimeTitle: "Sparad datum / tid"
                    },
                    archived: {
                        title: "Arkiverat ",
                        dateTitle: "Ange datum för arkivering",
                        timeTitle: "Ange tid datum för arkivering",
                        dateTimeTitle: "Sparad arkiveringsdatum-tid"
                    },
                    publicationEnd: {
                        title: "Publicering slutar",
                        dateTitle: "Ange slutdatum för publicering",
                        timeTitle: "Ange sluttid för publicering",
                        dateTimeTitle: "Sparat slutdatum-tid"
                    },
                    publisher: "Utgivare",
                    currentVersion: "Aktuell version:",
                    versionHasChanges: "Denna arbetsversion har ändringar",
                    publishMessage: "Tryck \"Spara och publicera den här versionen\" för att publicera"
                },
                appearance: {
                    name: "Utseende",
                    template: "Mall",
                    defaultChildTemplate: "Standardmall för nya dokument"
                },
                keywords: {
                    name: "Nyckelord",
                    title: "Nyckelord",
                    placeholder: "nyckelord",
                    add: "LÄGG TILL+",
                    disableSearch: "Inaktivera sökning"
                },
                categories: {
                    name: "Kategorier"
                },
                access: {
                    name: "Behörigheter",
                    role: "roll",
                    view: "läsa/se",
                    edit: "redigera",
                    restricted_1: "begränsad 1",
                    restricted_2: "begränsad 2",
                    addRole: "Lägg till roll"
                },
                permissions: {
                    name: "Behörighetsinställningar",
                    editText: "Redigera text",
                    editMenu: "Redigera meny",
                    editImage: "Redigera bild",
                    editLoop: "Redigera loop",
                    editDocInfo: "Redigera sid-info"
                },
                status: {
                    name: "Status",
                    created: "Skapad",
                    modified: "Ändrad",
                    archived: "Arkiverad",
                    published: "Publicerad",
                    publicationEnd: "Publicering avslutad",
                    by: "Av"
                },
                file: {
                    name: "Filer",
                    upload: "Ladda upp",
                    id: "ID",
                    fileName: "Namn",
                    isDefault: "Standard"
                },
                url: {
                    name: "URL",
                    title: "URL"
                },
                buttons: {
                    ok: "ok",
                    cancel: "avbryt",
                    saveAndPublish: "spara och publicera den här versionen"
                }
            },
            modal: {
                title: "Bekräfta",
                yes: "Ja",
                no: "Nej"
            },
            editors: {
                document: {
                    title: "Dokumenthanterare",
                    newDoc: "Nytt dokument",
                    freeText: "Fri text",
                    freeTextPlaceholder: "Skriv för att söka dokument",
                    owner: "Ägare",
                    category: "Kategori",
                    sort: {
                        id: "ID",
                        title: "Titel",
                        alias: "Alias",
                        type: "Typ",
                        status: "Status"
                    },
                    status: {
                        published: "Godkänd, publicerad",
                        publishedWaiting: "Godkänd, väntar",
                        inProcess: "Nytt, under arbete",
                        disapproved: "Ej godkänd",
                        archived: "Arkiverat",
                        passed: "Publiceringsänden"
                    }
                },
                newDocument: {
                    title: "Skapa nytt dokument",
                    textDoc: "Textdokument",
                    fileDoc: "Uppladdad fil",
                    urlDoc: "Länk (URL)"
                },
                newDocumentProfile: {
                    title: "Skapa nytt dokument",
                    createDocButton: "Skapa",
                    chooseProfileOrParent: "Välj profil för det nya dokumentet:",
                    validationErrorMessage: "Du måste välja en befintlig profil eller ange meta-id eller alias för ett textdokument",
                    buildByProfile: "Välj efter profil",
                    buildByParent: "Välj valfritt textdokuments ID",
                    buildByCurrentDocId: "Välj detta dokument som profil",
                    selectProfile: "Välj profil",
                    selectParent: "Välj 'förälder'"
                },
                menu: {
                    newDoc: "Ny",
                    title: "Meny-redigerare",
                    id: "ID",
                    docTitle: "Rubrik",
                    status: "Status",
                    removeConfirmation: "Vill du ta bort menyalternativet \"",
                    saveAndClose: "Spara och stäng"
                },
                loop: {
                    title: ";odul-redigerare",
                    createNew: "Skapa ny",
                    saveAndClose: "Spara och stäng",
                    id: "ID",
                    content: "innehåll",
                    isEnabled: "är aktiverad"
                },
                content: {
                    title: "Bildarkiv",
                    hideFolders: "Dölj mappar",
                    showFolders: "Visa mappar",
                    upload: "Ladda upp",
                    saveAndClose: "Spara och stäng",
                    removeFolderMessage: "Vill du ta bort mapp \"",
                    removeImageConfirm: "Vill du ta bort bilden ",
                    newFolderName: "Nytt mappnamn",
                    add: "LÄGG TILL+"
                },
                image: {
                    title: "Bild-redigerare",
                    panels: {
                        bottom: {
                            hide: "Dölj bottenpanelen",
                            show: "Visa bottenpanelen"
                        },
                        right: {
                            hide: "Dölj högerpanelen",
                            show: "Visa högerpanelen"
                        }
                    },
                    align: {
                        none: "Ingen justering",
                        center: "Fungerar endast om funktionen har implementerats av utvecklaren. [Utvecklarinformation: 'imcms-image-align-center' klass läggs till för vanlig imcms bildtagg]",
                        left: "Fungerar endast om funktionen har implementerats av utvecklaren. [Utvecklarinformation: 'imcms-image-align-left' klass läggs till för vanlig imcms bildtagg]",
                        right: "Fungerar endast om funktionen har implementerats av utvecklaren. [Utvecklarinformation: 'imcms-image-align-center' klass läggs till för vanlig imcms bildtagg]"
                    },
                    proportionsButtonTitle: "Bevara bildens proportioner Av/På",
                    displaySize: "Visad storlek",
                    height: "Höjd",
                    width: "Bredd",
                    preview: "Förhandsvisning",
                    original: "Original",
                    selectImage: "Välj bild",
                    altText: "Alt-text",
                    imageLink: "Bildlänk (URL)",
                    allLangs: "Alla språk",
                    advanced: "Avancerad",
                    simple: "Enkel",
                    none: "Ingen",
                    top: "topp",
                    right: "höger",
                    bottom: "botten",
                    left: "vänster",
                    spaceAround: "Luft runt bild",
                    cropCoords: "Krop-koordinater",
                    fileFormat: "Filformat",
                    alignment: "Textjustering",
                    altTextConfirm: "Alternativ text saknas. Vill du verkligen fortsätta?",
                    removeAndClose: "Ta bort och stäng",
                    saveAndClose: "Spara och stäng",
                    exif: {
                        button: "Visa exif info"
                    }
                }
            },
            textHistory: {
                title: "Texthistorik",
                cancel: "Avbryt",
                writeToText: "Skriv till textfält",
                viewSource: "Visa källa",
                viewText: "Visa text"
            },
            textValidation: {
                title: "Validering på W3C",
                ok: "OK",
                output: "Valideringsresultat: ",
                errors: " Fel",
                warnings: " Varningar"
            },
            sessionExpiredMessage: "Du har blivit utloggad p\u00e5 grund av inaktivitet. Omdirigera till inloggningssidan?",
            contentSaveWarningMessage: "Din session löper ut på 2 min. Var god, spara det nödvändiga innehållet. Sessionen fortsätter efter att du har sparat."
        },
        "en": {
            panel: {
                public: 'public',
                publicTitle: 'Shows the published view',
                edit: 'edit',
                editTitle: 'Shows the editable working version',
                preview: "preview",
                previewTitle: 'Previews the editable working version',
                publish: "publish offline",
                publishTitle: 'Publishes the working version directly',
                pageInfo: "page info",
                pageInfoTitle: 'Shows all page settings and meta-data',
                document: "document",
                documentTitle: 'Shows all documents in the document editor',
                admin: "admin",
                adminTitle: 'Shows all system settings (only for system administrators)',
                logout: "log out"
            },
            pageInfo: {
                document: "Document",
                newDocument: "New Document",
                confirmMessage: "Save changes?",
                confirmMessageOnCancel: "Do you really want to exit?",
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
                        inProcess: "New",
                        disapproved: "Disapproved",
                        approved: "Approved"
                    },
                    now: "Now",
                    clear: "Clear",
                    published: {
                        title: "Published",
                        dateTitle: "Set publish date",
                        timeTitle: "Set publish time",
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
                    publishMessage: "Please press \"Save and publish this version\" to publish"
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
                },
                access: {
                    name: "Access",
                    role: "role",
                    view: "view",
                    edit: "edit",
                    restricted_1: "restricted 1",
                    restricted_2: "restricted 2",
                    addRole: "Add role"
                },
                permissions: {
                    name: "Permission settings",
                    editText: "Edit text",
                    editMenu: "Edit menu",
                    editImage: "Edit image",
                    editLoop: "Edit loop",
                    editDocInfo: "Edit doc info"
                },
                status: {
                    name: "Status",
                    created: "Created",
                    modified: "Modified",
                    archived: "Archived",
                    published: "Published",
                    publicationEnd: "Publication end",
                    by: "By"
                },
                file: {
                    name: "Files",
                    upload: "Upload",
                    id: "ID",
                    fileName: "Name",
                    isDefault: "Default"
                },
                url: {
                    name: "URL",
                    title: "URL"
                },
                buttons: {
                    ok: "ok",
                    cancel: "cancel",
                    saveAndPublish: "save and publish this version"
                }
            },
            modal: {
                title: "Confirm",
                yes: "Yes",
                no: "No"
            },
            editors: {
                document: {
                    title: "Document Manager",
                    newDoc: "New",
                    freeText: "Free text",
                    freeTextPlaceholder: "Type to find document",
                    owner: "Owner",
                    category: "Category",
                    sort: {
                        id: "ID",
                        title: "Title",
                        alias: "Alias",
                        type: "Type",
                        status: "Status"
                    },
                    status: {
                        published: "Approved, published",
                        publishedWaiting: "Approved, waiting",
                        inProcess: "New",
                        disapproved: "Disapproved",
                        archived: "Archived",
                        passed: "Publication end"
                    }
                },
                newDocument: {
                    title: "Create new document",
                    textDoc: "Text Document",
                    fileDoc: "File Document",
                    urlDoc: "URL document"
                },
                newDocumentProfile: {
                    title: "Create new document",
                    createDocButton: "Create",
                    chooseProfileOrParent: "What settings shall be used to create the new document?",
                    validationErrorMessage: "You have to choose either an existing profile or specify a text document meta id or alias",
                    buildByProfile: "Use a profile",
                    buildByParent: "Use any text document id",
                    buildByCurrentDocId: "This document’s settings",
                    selectProfile: "Select profile",
                    selectParent: "Select parent document"
                },
                menu: {
                    newDoc: "New",
                    title: "Menu Editor",
                    id: "ID",
                    docTitle: "Title",
                    status: "Status",
                    removeConfirmation: "Do you want to remove the menu item \"",
                    saveAndClose: "Save and close"
                },
                loop: {
                    title: "Loop Editor",
                    createNew: "Create New",
                    saveAndClose: "Save and close",
                    id: "ID",
                    content: "text content",
                    isEnabled: "is enabled"
                },
                content: {
                    title: "Content manager",
                    hideFolders: "Hide folders",
                    showFolders: "Show folders",
                    upload: "Upload",
                    saveAndClose: "Save and close",
                    removeFolderMessage: "Do you want to remove folder \"",
                    removeImageConfirm: "Do you want to remove image ",
                    newFolderName: "New folder name",
                    add: "ADD+"
                },
                image: {
                    title: "Image Editor",
                    panels: {
                        bottom: {
                            hide: "Hide bottom panel",
                            show: "Show bottom panel"
                        },
                        right: {
                            hide: "Hide right panel",
                            show: "Show right panel"
                        }
                    },
                    align: {
                        none: "Align is not specified.",
                        center: "Works only if allowed by developer. [Developer info: 'imcms-image-align-center' class must be added for regular imcms image tag]",
                        left: "Works only if allowed by developer. [Developer info: 'imcms-image-align-left' class must be added for regular imcms image tag]",
                        right: "Works only if allowed by developer. [Developer info: 'imcms-image-align-right' class must be added for regular imcms image tag]"
                    },
                    proportionsButtonTitle: "Keep image proportion On/Off",
                    displaySize: "Display size",
                    height: "Height",
                    width: "Width",
                    preview: "Preview",
                    original: "Original",
                    selectImage: "Select Image",
                    altText: "Alt text",
                    imageLink: "Image link (URL)",
                    allLangs: "All languages",
                    advanced: "Advanced",
                    simple: "Standard",
                    none: "None",
                    top: "top",
                    right: "right",
                    bottom: "bottom",
                    left: "left",
                    spaceAround: "Space around image",
                    cropCoords: "Crop Coordinates",
                    fileFormat: "File format",
                    alignment: "Text alignment",
                    altTextConfirm: "Alternate text is missing. Are you sure you wish to continue?",
                    removeAndClose: "Remove and close",
                    saveAndClose: "Save and close",
                    exif: {
                        button: "Show exif info"
                    }
                }
            },
            textHistory: {
                title: "Text history",
                cancel: "Cancel",
                writeToText: "Write to text field",
                viewSource: "View Source",
                viewText: "View Text"
            },
            textValidation: {
                title: "Validation on W3C",
                ok: "OK",
                output: "Validation result: ",
                errors: " Errors",
                warnings: " Warnings"
            },
            sessionExpiredMessage: "You have been logged out due to inactivity. Redirect to login page?",
            contentSaveWarningMessage: "Your session will expire in 2 min. Please, save necessary content. Session will be continued after saving."
        }
    };

    return texts[imcms.userLanguage];
});
