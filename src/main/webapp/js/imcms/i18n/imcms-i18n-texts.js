/**
 * Module holds texts used in UI in all available languages.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
const imcms = require('imcms');

const texts = {
    "sv": {
        panel: {
            settingsTitle: 'Inställningar för administratörspanelen',
            settingsList: {
                size: {
                    name: "Panelstorlek",
                    small: "Liten",
                    smallTitle: "Panelen blir mindre med färre texter",
                    large: "Stor",
                    largeTitle: "Panelen blir större med ytterligare texter"
                },
                appearance: {
                    name: "Panel Utseende",
                    auto: "Auto",
                    autoTitle: "Visas automatiskt när du flyttar musen nära sidans topp, döljer på att klicka på musen utanför",
                    hidden: "Dold",
                    hiddenTitle: "Panelen är dold som standard, du kan öppna den genom att klicka på den lilla ikonen längst upp på sidan",
                    visible: "Synlig",
                    visibleTitle: "Panelen är alltid synlig"
                },
                unlockPanel: "Lås Upp Administratörspanelen",
                hidePanel: "Dölj Administrationspanelen"
            },
            error: {
                loadFailed: "Misslyckades att ladda om. Försök igen"
            },
            "public": "offentlig",
            publicTitle: 'Visar den publicerade vyn',
            edit: "redigera",
            editTitle: 'Visar den redigerbara arbetsversionen',
            preview: "förhandsvisning",
            previewTitle: 'Visar den redigerbara arbetsversionen',
            publish: "publicera",
            publishTitle: 'Publicerar arbetsversionen direkt',
            pageInfo: "sid-info",
            pageInfoTitle: 'Visar alla sidinställningar och metadata',
            special: "Särskild",
            specialTitle: "Visar kundspecifik administration",
            document: "dokument",
            documentTitle: 'Visar alla dokument i dokumentredigeraren',
            admin: "admin",
            adminTitle: 'Visar alla systeminställningar (endast för systemadministratörer)',
            logout: "logga ut"
        },
        toolbar: {
            documentId: "Dokument ",
            documentIdTitle: "Dokument no",
            elementIndex: "Element ",
            elementIndexTitle: "Element no"
        },
        pageInfo: {
            document: "Dokument",
            URL: "Dokument URL",
            newDocument: "Nytt dokument",
            confirmMessage: "Spara ändringar?",
            confirmMessageOnCancel: "Vill du verkligen avsluta?",
            oneLanguageShouldBeEnabled: "Minst ett språk måste vara aktiverat!",
            title: {
                name: "Rubrik",
                title: "Rubrik",
                menuText: "Menytext",
                chooseImage: "Välj bild",
                showIn: "Visa i",
                sameFrame: "Samma ram",
                newWindow: "Nytt fönster",
                replaceAll: "Ersätt alla",
                alias: "Dokumentets alias",
                aliasPlaceholder: "detta-dokument-alias",
                missingLangRuleTitle: "Om det begärda språket saknas:",
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
                publishMessage: "Tryck \"Spara och publicera den här versionen\" för att publicera",
                error: {
                    userLoadFailed: 'Misslyckades med att hämta utgivare. Försök igen'
                }
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
                name: "Kategorier",
                error: {
                    loadFailed: "Misslyckades med att hämta kategorier. Försök få vinst"
                }
            },
            access: {
                name: "Behörigheter",
                role: "roll",
                view: "läsa/se",
                edit: "redigera",
                restricted_1: "begränsad 1",
                restricted_2: "begränsad 2",
                addRole: "Lägg till roll",
                linkableByOtherUsers: "Länkbar av andra användare",
                linkableForUnauthorizedUsers: "Länkbar för obehöriga användare",
                error: {
                    loadFailed: "Misslyckades med att hämta roller. Försök få vinst"
                }
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
            },
            error: {
                createDocumentFailed: 'Misslyckades med att spara dokument. Försök igen',
                loadDocumentFailed: 'Misslyckades att ladda. Försök igen'
            }
        },
        modal: {
            doNotShowAgain: "Visa inte igen",
            title: "Bekräfta",
            yes: "Ja",
            no: "Nej",
            ok: "Ok"
        },
        editors: {
            document: {
                title: "Dokumenthanterare",
                newDoc: "Nytt dokument",
                freeText: "Fri text",
                freeTextPlaceholder: "Skriv för att söka dokument",
                owner: "Ägare",
                category: "Kategori",
                controls: {
                    edit: {
                        title: "Redigera"
                    },
                    copy: {
                        title: "Kopiera",
                        confirmMessage: "Vill du verkligen kopiera (all information i dokumentet kommer att kopieras) doc med id "
                    }
                },
                sort: {
                    id: "ID",
                    title: "Titel",
                    alias: "Alias",
                    type: "Typ",
                    status: "Status"
                },
                status: {
                    published: "Publicerad",
                    publishedWaiting: "Godkänd, väntar",
                    inProcess: "Nytt, under arbete",
                    disapproved: "Ej godkänd",
                    archived: "Arkiverat",
                    passed: "Avslutad"
                },
                error: {
                    searchFailed: 'Sökningen misslyckades. Försök igen',
                    userLoadFailed: 'Misslyckades med att hämta användare. Försök igen',
                    categoriesLoadFailed: 'Misslyckades med att hämta kategorier. Försök igen',
                    copyDocumentFailed: 'Det gick inte att kopiera dokumentet. Försök igen',
                    removeDocumentFailed: 'Misslyckades med att ta bort dokument. Försök igen'
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
                selectParent: "Välj 'förälder'",
                error: {
                    loadProfilesFailed: "Misslyckades med att hämta kategorier. Försök igen"
                }
            },
            menu: {
                newDoc: "Ny",
                title: "Meny-redigerare",
                remove: "Ta bort ur menyn",
                copy: "Kopiera",
                edit: "Redigera Sid-info",
                id: "ID",
                docTitle: "Rubrik",
                status: "Status",
                removeConfirmation: "Vill du ta bort menyalternativet \"",
                saveAndClose: "Spara och stäng",
                error: {
                    createFailed: 'Det gick inte att spara menyn. Försök igen',
                    copyDocumentFailed: 'Det gick inte att kopiera dokumentet. Försök igen',
                    loadFailed: 'Misslyckades att ladda. Försök igen'
                }
            },
            loop: {
                title: ";odul-redigerare",
                createNew: "Skapa ny",
                saveAndClose: "Spara och stäng",
                id: "ID",
                content: "innehåll",
                isEnabled: "är aktiverad",
                error: {
                    createFailed: 'Misslyckades med att skapa loop. Försök igen',
                    loadFailed: 'Misslyckades att ladda. Försök igen'
                }
            },
            content: {
                title: "Bildarkiv",
                hideFolders: "Dölj mappar",
                showFolders: "Visa mappar",
                checkFolderImagesUsage: "Kontrollera efter använda bilder",
                createFolderImage: "Läng till underkatalog",
                cancel: "Avbryt",
                upload: "Ladda upp",
                useSelectedImage: "Använd vald bild",
                removeImageConfirm: "Vill du ta bort bilden ",
                imageStillUsed: "Bild i bruk",
                removeFolderMessage: "Vill du ta bort mapp \"",
                folderNotEmptyMessage: "Mappen är inte tom",
                newFolderName: "Nytt mappnamn",
                add: "LÄGG TILL+",
                openImage: "Öppna bild i nytt fönster",
                error: {
                    removeFailed: 'Misslyckades med att ta bort. Försök igen',
                    checkFailed: 'Misslyckades med att kontrollera. Försök igen',
                    addFolderFailed: 'Misslyckades med att lägga till mapp. Försök igen',
                    loadImagesFailed: 'Misslyckades med att hämta bilder. Försök igen',
                    uploadImagesFailed: 'Misslyckades med att ladda upp bilder. Försök igen'
                }
            },
            image: {
                title: "Bild-redigerare",
                proportion: "Läs proportioner",
                presetCrop: "Förinställd gröda",
                crop: "Gröda",
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
                buttons: {
                    zoomIn: 'Zooma in',
                    zoomOut: 'Zooma ut',
                    zoomReset: 'Återställ zoom till standardvärdet',
                    rotateLeft: 'Rotera vänster',
                    rotateRight: 'Vrid höger',
                    revert: 'Återställ till original',
                    cropping: 'Beskära bild',
                    cancelText: "Avbryt",
                    cancelTitle: "Avbryt ändringar",
                    applyChangeText: "Tillämpa",
                    applyChangeTitle: "Applicera förändringar",
                    rotationTitle: "Aktivera rotationsreglage"
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
                },
                error: {
                    loadFailed: 'Det gick inte att ladda bilden. Försök igen',
                    removeFailed: 'Misslyckades med att ta bort bilden. Försök igen',
                    createFailed: 'Misslyckades med att skapa bild. Försök igen'
                }
            },
            text: {
                confirmSave: "Spara ändringar?",
                error: {
                    createFailed: 'Misslyckades med att spara text. Försök igen'
                }
            }
        },
        languageFlags: {
            error: {
                loadFailed: 'Misslyckades med att ladda språk. Försök igen'
            }
        },
        textHistory: {
            title: "Texthistorik",
            cancel: "Avbryt",
            writeToText: "Skriv till textfält",
            viewSource: "Visa källa",
            viewText: "Visa text",
            error: {
                loadFailed: 'Misslyckades med att hämta historia. Försök igen'
            }
        },
        textValidation: {
            title: "Validering på W3C",
            pluginTitle: "Validera innehåll över W3C",
            ok: "OK",
            output: "Valideringsresultat: ",
            errors: " Fel",
            warnings: " Varningar",
            error: {
                validationFailed: 'Misslyckades att validera. Försök igen'
            }
        },
        sessionExpiredMessage: "Du har blivit utloggad p\u00e5 grund av inaktivitet. Omdirigera till inloggningssidan?",
        contentSaveWarningMessage: "Din session löper ut på 2 min. Var god, spara det nödvändiga innehållet. Sessionen fortsätter efter att du har sparat.",
        superAdmin: {
            head: 'SuperAdmin',
            users: {
                name: 'Användare',
                searchFilter: {
                    byName: {
                        text: 'Sök användare',
                        placeholder: 'Tomt fält visar allt'
                    },
                    byRole: {
                        title: 'Användare med roll'
                    },
                    inactiveUsers: 'Inkludera inaktiva användare',
                    listUsers: 'Lista användare'
                },
                searchResult: {
                    title: ' användarna hittades',
                    id: 'ID',
                    firstName: 'Förnamn',
                    lastName: 'Efternamn',
                    userName: 'Användarnamn',
                    email: 'E-post',
                    archived: 'arkiveras'
                },
                createNewUser: 'Create new user',
                error: {
                    loadRolesFailed: 'Misslyckades med att hämta roller. Försök igen',
                    updateFailed: 'Misslyckades med att uppdatera användaren. Försök igen',
                    searchFailed: 'Misslyckades med att söka efter användare. Försök igen'
                }
            },
            roles: {
                name: 'Roller',
                title: 'Roller: administration',
                createNewRole: 'Skapa ny roll',
                roleName: 'Rollnamn',
                save: 'Spara',
                cancel: 'Avbryt',
                externalRolesInfo: 'Du kan ställa in fjärrroller för att fungera som lokala',
                permissions: {
                    title: 'Rollbehörigheter',
                    getPasswordByEmail: 'Få lösenord via e-post',
                    accessToAdminPages: 'Tillgång till admin sidor',
                    useImagesInImageArchive: 'Använd bilder i bildarkivet',
                    changeImagesInImageArchive: 'Ändra bilder i bildarkivet'
                },
                deleteConfirm: 'Vill du verkligen ta bort den här rollen?',
                editRole: 'Redigera rollen',
                deleteRole: 'Radera rollen',
                saveChanges: 'Spara ändringar',
                discardChangesMessage: 'Ignorera ändringar?',
                error: {
                    loadFailed: 'Misslyckades med att hämta roller. Försök igen',
                    loadProvidersFailed: 'Misslyckades med auth leverantörer. Försök igen',
                    removeFailed: 'Misslyckades med att ta bort rollen. Försök igen',
                    updateFailed: 'Misslyckades med att uppdatera rollen. Försök igen',
                    createFailed: 'Misslyckades med att skapa roll. Försök igen',
                    externalRoles: {
                        loadFailed: 'Misslyckades med att hämta externa roller. Försök igen',
                        updateFailed: 'Misslyckades med att uppdatera extern roll. Försök igen'
                    },
                    azureRoles: {
                        loadFailed: 'Misslyckades med att hämta externa roller. Försök igen'
                    }
                }
            },
            ipAccess: {
                name: 'IP-åtkomst',
                title: 'IP-åtkomst',
                createNewRule: 'Ny',
                editRule: 'Redigera',
                deleteRule: 'Radera',
                save: 'Spara',
                cancel: 'Avbryt',
                deleteConfirm: 'Vill du verkligen ta bort den här regel?',
                saveChanges: 'Spara ändringar',
                discardChangesMessage: 'Ignorera ändringar?',
                fields: {
                    enabled: 'Aktiverad',
                    restricted: 'Begränsad',
                    ipRange: 'IP-område',
                    role: 'Roll',
                    user: 'Användare'
                },
                error: {
                    loadFailed: 'Misslyckades med att hämta regler. Försök igen',
                    removeFailed: 'Misslyckades med att ta bort regeln. Försök igen',
                    updateFailed: 'Misslyckades med att uppdatera regeln. Försök igen',
                    createFailed: 'Misslyckades med att skapa regel. Försök igen',
                    loadRolesFailed: 'Misslyckades med att hämta roller. Försök igen',
                    loadUsersFailed: 'Misslyckades med att hämta användare. Försök igen',
                    invalidIP: 'Ogiltig ipv4 / ipv6-adress'
                }
            },
            deleteDocs: {
                name: 'Radera dokument',
                title: 'Ta bort dokument med id',
                deleteConfirmation: 'VARNING: Dokumentet och dess data kommer att raderas. Är du säker?',
                deleteDocButton: 'Radera!'
            },
            templates: {
                name: 'Mallar'
            },
            files: {
                name: 'Filer',
                createFile: 'Skapa fil',
                upload: 'Ladda upp',
                rename: 'Ändra namn',
                add: 'Lägg till',
                title: {
                    createFileName: 'Ny fil / katalognamn',
                    delete: 'Ta bort',
                    move: 'Flytta',
                    copy: 'Kopiera',
                    download: 'Ladda ner',
                    edit: 'Redigera',
                },
            },
            search: {
                name: 'Sök'
            },
            linkValidator: {
                name: 'Länk Validator',
                titleOnlyBrokenLinks: "Lista endast brutna länkar",
                brokenLinks: "Lista bara brutna länkar",
                buttonValidation: 'Starta validering',
                startDocumentId: 'Starta id',
                endDocumentId: 'Avsluta id',
                linkInfoRow: {
                    pageAlias: 'Sid alias',
                    status: "Status",
                    type: 'Typ',
                    admin: "Administration",
                    reference: "Ref.",
                    link: "Länk",
                    hostFound: "Värd hittades",
                    hostReachable: "Värden kan nås",
                    pageFound: 'Sidan hittades'
                },
                error: {
                    validationFailed: 'Misslyckades att validera. Försök igen'
                }
            },
            categories: {
                name: 'Kategorier',
                createButtonName: "Skapa",
                removeButtonName: "Ta bort",
                saveButton: 'Spara',
                cancelButton: 'Avbryt',
                warnCancelMessage: 'Vill du verkligen avbryta detta?',
                confirmDelete: 'Radera?',
                sections: {
                    createCategoryType: {
                        title: 'Skapa kategori typ',
                        name: 'Kategori typnamn',
                        inherited: 'Erfaren av nya dokument',
                        singleSelect: 'Singel välj',
                        multiSelect: 'Flera val',
                        chooseType: 'Välj kategori Typ',
                    },
                    createCategory: {
                        title: 'Skapa kategori',
                        name: 'Kategori namn',
                        description: 'Beskrivning',
                        categoryType: 'Lägg till i kategoritypen',
                        chooseCategory: 'Välj kategori',
                    }
                },
                error: {
                    invalidName: 'Tomt namn är oacceptabelt! Vänligen välj ett annat namn!',
                    category: {
                        loadFailed: 'Misslyckades med att hämta kategori. Försök igen'
                    },
                    categoryType: {
                        loadFailed: 'Misslyckades med att hämta kategorierna. Försök igen',
                        removeFailed: 'Misslyckades med att ta bort kategoritypen. Försök igen'
                    }
                }
            },
            profiles: {
                name: 'Profiler',
                title: 'Profiler',
                createButton: "Skapa ny profil",
                warnChangeMessage: 'Vill du ändra den här profilen?',
                warnDelete: 'Ta du verkligen bort den här profilen?',
                warnCancelMessage: 'Vill du verkligen avbryta?',
                cancel: "Avbryt",
                titleTextName: 'Namn',
                titleTextDocName: 'Dokumentnamn',
                createNewProfile: {
                    titleTextName: 'Namn',
                    titleTextDocName: 'Dokumentnamn',
                    textFieldName: 'Textnamn',
                    textFieldDocName: 'Text doc namn',
                    buttonSave: 'Save'
                },
                editProfile: {
                    name: "Namn",
                    docName: 'Dokumentnamn',
                    buttonEdit: 'Redigera profil',
                    buttonDelete: 'Ta bort profil'
                },
                error: {
                    createFailed: 'Misslyckades med att skapa profil. Försök igen',
                    loadFailed: 'Misslyckades med att hämta profiler. Försök igen',
                    errorMessage: 'Tyvärr, men du gjorde fel!'
                }
            },
            systemProperties: {
                name: 'Systemegenskaper',
                sections: {
                    startPage: {
                        name: "Startsida",
                        description: "Sidonummer",
                        input: "Inmatningssida sida"
                    },
                    systemMessage: {
                        name: "Systemmeddelande",
                        description: "Meddelande",
                        inputBox: "Box Meddelande"
                    },
                    serverMaster: {
                        name: "Server Master",
                        descriptionName: "Server mästar namn",
                        descriptionEmail: "Servermaster-e-post",
                        descriptionByName: "Server mästare Namnbeskrivning",
                        descriptionByEmail: "Server mästare E-postbeskrivning",
                        inputName: "Inmatning av servermästarnamn",
                        inputEmail: "Inmatning av servermästare-post"
                    },
                    webMaster: {
                        name: "WebMaster",
                        descriptionName: "Webmaster namn",
                        descriptionEmail: "Webmaster e-post",
                        descriptionByName: "Webmasters namnbeskrivning",
                        descriptionByEmail: "Webmaster E-postbeskrivning",
                        inputName: "Inmatning av webbnamn",
                        inputEmail: "Webmaster e-postinmatning"
                    }
                },
                changeButton: "Byta",
                error: {
                    loadFailed: 'Misslyckades med att ladda egenskaper. Försök igen'
                }
            }
        },
        login: {
            alternativeLogin: "Alternativ inloggning:",
            error: {
                loadProvidersFailed: 'Misslyckades med auth leverantörer. Försök igen'
            }
        }
    },
    "en": {
        panel: {
            settingsTitle: 'Admin panel settings',
            settingsList: {
                size: {
                    name: "Panel Size",
                    small: "Small",
                    smallTitle: "Panel becomes smaller with fewer texts",
                    large: "Large",
                    largeTitle: "Panel becomes larger with additional texts"
                },
                appearance: {
                    name: "Panel Appearance",
                    auto: "Auto",
                    autoTitle: "Automatically appears when moving mouse near the page top, hides on clicking mouse outside",
                    hidden: "Hidden",
                    hiddenTitle: "Panel is hidden by default, you can open it by clicking on small icon on the top of the page",
                    visible: "Visible",
                    visibleTitle: "Panel is always visible"
                },
                unlockPanel: "Unlock Admin Panel",
                hidePanel: "Hide Admin Panel"
            },
            error: {
                loadFailed: "Failed to reload. Try again"
            },
            'public': 'public',
            publicTitle: 'Shows the published view',
            edit: 'edit',
            editTitle: 'Shows the editable working version',
            preview: "preview",
            previewTitle: 'Previews the editable working version',
            publish: "publish",
            publishTitle: 'Publishes the working version directly',
            pageInfo: "page info",
            pageInfoTitle: 'Shows all page settings and meta-data',
            special: "Special",
            specialTitle: "Shows client specific administration",
            document: "documents",
            documentTitle: 'Shows all documents in the document editor',
            admin: "admin",
            adminTitle: 'Shows all system settings (only for system administrators)',
            logout: "log out"
        },
        toolbar: {
            documentId: "Document ",
            documentIdTitle: "Document no",
            elementIndex: "Element ",
            elementIndexTitle: "Element no"
        },
        pageInfo: {
            document: "Document",
            URL: "Document URL",
            newDocument: "New Document",
            confirmMessage: "Save changes?",
            confirmMessageOnCancel: "Do you really want to exit?",
            oneLanguageShouldBeEnabled: "At least one language must be enabled!",
            title: {
                name: "Title",
                title: "Title",
                menuText: "Menu text",
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
                publishMessage: "Please press \"Save and publish this version\" to publish as version № ",
                error: {
                    userLoadFailed: 'Failed to fetch publishers. Try again'
                }
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
                name: "Categories",
                error: {
                    loadFailed: "Failed to fetch categories. Try gain"
                }
            },
            access: {
                name: "Access",
                role: "role",
                view: "view",
                edit: "edit",
                restricted_1: "restricted 1",
                restricted_2: "restricted 2",
                addRole: "Add role",
                linkableByOtherUsers: "Linkable By Other Users",
                linkableForUnauthorizedUsers: "Linkable For Unauthorized Users",
                error: {
                    loadFailed: "Failed to fetch roles. Try gain"
                }
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
            },
            error: {
                createDocumentFailed: 'Failed to save document. Try again',
                loadDocumentFailed: 'Failed to load. Try again'
            }
        },
        modal: {
            doNotShowAgain: "Do not show again",
            title: "Confirm",
            yes: "Yes",
            no: "No",
            ok: "Ok"
        },
        editors: {
            document: {
                title: "Document Manager",
                newDoc: "New",
                freeText: "Free text",
                freeTextPlaceholder: "Type to find document",
                owner: "Owner",
                category: "Category",
                controls: {
                    edit: {
                        title: "Edit"
                    },
                    copy: {
                        title: "Copy",
                        confirmMessage: "Do you really want to copy (all information in the document will be copied) doc with id "
                    }
                },
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
                },
                error: {
                    searchFailed: 'Search failed. Try again',
                    userLoadFailed: 'Failed to fetch users. Try again',
                    categoriesLoadFailed: 'Failed to fetch categories. Try again',
                    copyDocumentFailed: 'Failed to copy document. Try again',
                    removeDocumentFailed: 'Failed to remove document. Try again'
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
                selectParent: "Select parent document",
                error: {
                    loadProfilesFailed: "Failed to fetch categories. Try again"
                }
            },
            menu: {
                newDoc: "New",
                title: "Menu Editor",
                remove: "Remove",
                copy: "Copy",
                edit: "Edit",
                id: "ID",
                docTitle: "Title",
                status: "Status",
                removeConfirmation: "Do you want to remove the menu item \"",
                saveAndClose: "Save and close",
                error: {
                    createFailed: 'Failed to save menu. Try again',
                    copyDocumentFailed: 'Failed to copy document. Try again',
                    loadFailed: 'Failed to load. Try again'
                }
            },
            loop: {
                title: "Loop Editor",
                createNew: "Create New",
                saveAndClose: "Save and close",
                id: "ID",
                content: "text content",
                isEnabled: "is enabled",
                error: {
                    createFailed: 'Failed to create loop. Try again',
                    loadFailed: 'Failed to load. Try again'
                }
            },
            content: {
                title: "Content manager",
                hideFolders: "Hide folders",
                showFolders: "Show folders",
                checkFolderImagesUsage: "Check for used images",
                createFolderImage: "Add subdirectory",
                cancel: "Cancel",
                upload: "Upload",
                useSelectedImage: "Use selected image",
                removeImageConfirm: "Do you want to remove image ",
                imageStillUsed: "Image in use",
                removeFolderMessage: "Do you want to remove folder \"",
                folderNotEmptyMessage: "Folder not empty",
                newFolderName: "New folder name",
                add: "ADD+",
                openImage: "Open image in new window",
                error: {
                    removeFailed: 'Failed to remove. Try again',
                    checkFailed: 'Failed to check. Try again',
                    addFolderFailed: 'Failed to add folder. Try again',
                    loadImagesFailed: 'Failed to fetch images. Try again',
                    uploadImagesFailed: 'Failed to fetch images. Try again'
                }
            },
            image: {
                title: "Image Editor",
                proportion: "Proportions locked",
                presetCrop: "Preset crop",
                crop: "Crop",
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
                buttons: {
                    zoomIn: 'Zoom in',
                    zoomOut: 'Zoom out',
                    zoomReset: 'Reset zoom to default value',
                    rotateLeft: 'Rotate left',
                    rotateRight: 'Rotate right',
                    revert: 'Reset to original',
                    cropping: 'Crop image',
                    cancelText: "Cancel",
                    cancelTitle: "Cancel changes",
                    applyChangeText: "Apply",
                    applyChangeTitle: "Apply changes",
                    rotationTitle: "Activate rotation controls"
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
                },
                error: {
                    loadFailed: 'Failed to load image. Try again',
                    removeFailed: 'Failed to remove image. Try again',
                    createFailed: 'Failed to create image. Try again'
                }
            },
            text: {
                confirmSave: "Save changes?",
                error: {
                    createFailed: 'Failed to save text. Try again'
                }
            }
        },
        languageFlags: {
            error: {
                loadFailed: 'Failed to load languages. Try again'
            }
        },
        textHistory: {
            title: "Text history",
            cancel: "Cancel",
            writeToText: "Write to text field",
            viewSource: "View Source",
            viewText: "View Text",
            error: {
                loadFailed: 'Failed to fetch history. Try again'
            }
        },
        textValidation: {
            title: "Validation on W3C",
            pluginTitle: "Validate content over W3C",
            ok: "OK",
            output: "Validation result: ",
            errors: " Errors",
            warnings: " Warnings",
            error: {
                validationFailed: 'Failed to validate. Try again'
            }
        },
        sessionExpiredMessage: "You have been logged out due to inactivity. Redirect to login page?",
        contentSaveWarningMessage: "Your session will expire in 2 min. Please, save necessary content. Session will be continued after saving.",
        superAdmin: {
            head: 'SuperAdmin',
            users: {
                name: 'Users',
                searchFilter: {
                    byName: {
                        text: 'Search users',
                        placeholder: 'Empty field shows all'
                    },
                    byRole: {
                        title: 'User with role'
                    },
                    inactiveUsers: 'Include inactive users',
                    listUsers: 'List users'
                },
                searchResult: {
                    title: ' users found',
                    id: 'ID',
                    firstName: 'First Name',
                    lastName: 'Last Name',
                    userName: 'Username',
                    email: 'Email',
                    archived: 'archived'
                },
                createNewUser: 'Create new user',
                error: {
                    loadRolesFailed: 'Failed to fetch roles. Try again',
                    updateFailed: 'Failed to update user. Try again',
                    searchFailed: 'Failed to search for users. Try again'
                }
            },
            roles: {
                name: 'Roles',
                title: 'Roles administration',
                createNewRole: 'Create new role',
                roleName: 'Role name',
                save: 'Save',
                cancel: 'Cancel',
                externalRolesInfo: 'You can set remote roles to behave as local ones',
                permissions: {
                    title: 'Role permissions',
                    getPasswordByEmail: 'Get password by email',
                    accessToAdminPages: 'Access to admin pages',
                    useImagesInImageArchive: 'Use images in image archive',
                    changeImagesInImageArchive: 'Change images in image archive'
                },
                deleteConfirm: 'Do you really want to delete this role?',
                editRole: 'Edit role',
                deleteRole: 'Delete role',
                saveChanges: 'Save changes',
                discardChangesMessage: 'Discard changes?',
                error: {
                    loadFailed: 'Failed to fetch roles. Try again',
                    loadProvidersFailed: 'Failed to auth providers. Try again',
                    removeFailed: 'Failed to remove role. Try again',
                    updateFailed: 'Failed to update role. Try again',
                    createFailed: 'Failed to create role. Try again',
                    externalRoles: {
                        loadFailed: 'Failed to fetch external roles. Try again',
                        updateFailed: 'Failed to update external role. Try again'
                    },
                    azureRoles: {
                        loadFailed: 'Failed to fetch external roles. Try again'
                    }
                }
            },
            ipAccess: {
                name: 'IP Access',
                title: 'IP Access',
                createNewRule: 'New',
                editRule: 'Edit',
                deleteRule: 'Remove',
                save: 'Save',
                cancel: 'Cancel',
                deleteConfirm: 'Do you really want to delete this rule?',
                saveChanges: 'Save changes',
                discardChangesMessage: 'Discard changes?',
                fields: {
                    enabled: 'Enabled',
                    restricted: 'Restricted',
                    ipRange: 'IP Range',
                    role: 'Role',
                    user: 'User'
                },
                error: {
                    loadFailed: 'Failed to fetch rules. Try again',
                    removeFailed: 'Failed to remove rule. Try again',
                    updateFailed: 'Failed to update rule. Try again',
                    createFailed: 'Failed to create rule. Try again',
                    loadRolesFailed: 'Failed to fetch roles. Try again',
                    loadUsersFailed: 'Failed to fetch users. Try again',
                    invalidIP: 'Invalid ipv4/ipv6 address'
                }
            },
            deleteDocs: {
                name: 'Delete Documents',
                title: 'Delete document by id',
                deleteConfirmation: 'WARNING: Document and it\'s data will be deleted. Are you sure?',
                deleteDocButton: 'Delete!'
            },
            templates: {
                name: 'Templates'
            },
            files: {
                name: 'Files',
                createFile: 'Create file',
                upload: 'Upload',
                rename: 'Change name',
                add: 'Add',
                title: {
                    createFileName: 'New file/directory name',
                    delete: 'Delete',
                    move: 'Move',
                    copy: 'Copy',
                    download: 'Download',
                    edit: 'Edit',
                },
            },
            search: {
                name: 'Search'
            },
            linkValidator: {
                name: 'Link Validator',
                titleOnlyBrokenLinks: 'List only broken links',
                brokenLinks: 'List only broken links',
                buttonValidation: 'Start the validation',
                startDocumentId: 'Start id',
                endDocumentId: 'End id',
                linkInfoRow: {
                    pageAlias: 'Page alias',
                    status: 'Status',
                    type: 'Type',
                    admin: 'Admin',
                    reference: "Ref.",
                    link: 'Link',
                    hostFound: 'Host found',
                    hostReachable: 'Host reachable',
                    pageFound: 'Page found'
                },
                error: {
                    validationFailed: 'Failed to validate. Try again'
                }
            },
            categories: {
                name: 'Categories',
                createButtonName: "Create",
                removeButtonName: "Remove",
                saveButton: 'Save',
                cancelButton: 'Cancel',
                warnCancelMessage: 'Do you really want to cancel this ?',
                confirmDelete: 'Delete?',
                sections: {
                    createCategoryType: {
                        title: 'Create category type',
                        name: 'Category type name',
                        inherited: 'Inherited to new documents',
                        singleSelect: 'Single select',
                        multiSelect: 'Multi select',
                        chooseType: 'Choose Category Type',
                    },
                    createCategory: {
                        title: 'Create category',
                        name: 'Category name',
                        description: 'Description',
                        categoryType: 'Add to category type',
                        chooseCategory: 'Choose Category',
                    }
                },
                error: {
                    invalidName: 'This is name is unacceptable! Please choose a different name!',
                    category: {
                        loadFailed: 'Failed to fetch category. Try again'
                    },
                    categoryType: {
                        loadFailed: 'Failed to fetch categories types. Try again',
                        removeFailed: 'Failed to remove category type. Try again'
                    }
                }
            },
            profiles: {
                name: 'Profiles',
                title: 'Profiles',
                createButton: 'Create new profile',
                warnChangeMessage: 'Do you want to change this profile?',
                warnDelete: 'Do you really delete this profile?',
                warnCancelMessage: 'Do you want to really cancel?',
                cancel: 'Cancel',
                titleTextName: 'Name',
                titleTextDocName: 'Document name',
                createNewProfile: {
                    titleTextName: 'Name',
                    titleTextDocName: 'Document name',
                    textFieldName: 'Input name',
                    textFieldDocName: 'Input document name',
                    buttonSave: 'Save'
                },
                editProfile: {
                    name: 'Name',
                    docName: 'Document Name',
                    buttonEdit: 'Edit profile',
                    buttonDelete: 'Delete profile'
                },
                error: {
                    createFailed: 'Failed to create profile. Try again',
                    loadFailed: 'Failed to fetch profiles. Try again',
                    errorMessage: 'Sorry, but you did mistake!'
                }
            },
            systemProperties: {
                name: 'System Properties',
                sections: {
                    startPage: {
                        name: "Start page",
                        description: "Page number",
                        input: "Number page"
                    },
                    systemMessage: {
                        name: "System Message",
                        description: "Message",
                        inputBox: "Box Message"
                    },
                    serverMaster: {
                        name: "Server Master",
                        descriptionName: "Server master name",
                        descriptionEmail: "Server master email",
                        descriptionByName: "Server master Name description",
                        descriptionByEmail: "Server master Email description",
                        inputName: "Server master name input",
                        inputEmail: "Server master name email"
                    },
                    webMaster: {
                        name: "Web Master",
                        descriptionName: "Web master name",
                        descriptionEmail: "Web master email",
                        descriptionByName: "Web master Name description",
                        descriptionByEmail: "Web master Email description",
                        inputName: "Web master name input",
                        inputEmail: "Web master email input"
                    }
                },
                changeButton: "Change",
                error: {
                    loadFailed: 'Failed to load properties. Try again'
                }
            }
        },
        login: {
            alternativeLogin: "Alternative login:",
            error: {
                loadProvidersFailed: 'Failed to auth providers. Try again'
            }
        }
    }
};

module.exports = texts[imcms.userLanguage || 'en'];
