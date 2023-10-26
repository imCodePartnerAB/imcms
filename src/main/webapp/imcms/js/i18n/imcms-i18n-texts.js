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
            special: "Admin",
            specialTitle: "Visar kundspecifik administration",
            document: "dokument",
            documentTitle: 'Visar alla dokument i dokumentredigeraren',
            admin: "system",
            adminTitle: 'Visar alla systeminställningar (endast för systemadministratörer)',
            logout: "logga ut"
        },
        toolbar: {
            documentId: "Dokument ",
            documentIdTitle: "Dokument no",
            elementIndex: "Element ",
            elementIndexTitle: "Element no"
        },
        toolTipText: {
            textHistory: "Visa texthistorik",
            validateContent: "Bekräfta innehåll över W3C",
            fullScreen: "Full skärm",
            save: "Spara",
            htmlContent: "Hantering av HTML-innehållsfiltrering",
            bold: 'Djärv',
            italic: 'Kursiv',
            underline: 'Understrykning',
            sourceCode: "Källkod",
            bulletListText: "Bullet list",
            numberedListText: "Numrerad lista",
            horizontalLineText: "Horisontell linje",
            alignLeftText: "Justera vänster",
            alignRightText: "Justera rätt",
            alignCenterText: "Justera centrum",
            alignJustify: "Rättfärdiga",
            insertLinkText: "Infoga/Redigera länk",
            addImageText: "Lägg till bild",
            switchTextEditor: "Byt till textredigerare",
            switchTextMode: "Växla till vanligt textläge",
            switchHTMLMode: "Växla till HTML-läge",
            discardChange: "Ändra ändringar",
            discardChangesQuestion: 'Ignorera ändringar?',
            textEditor: 'Textredigerare',
            imageEditor: 'Bildredigerare',
            normal: 'Vanligt',
            auto: 'Auto',
            maximize: 'Maximera',
            close: 'Stänga',
            filterPolicy: {
                restricted: 'Endast text',
                relaxed: 'Kraftigt filter',
                allowedAll: 'Inget filter',
                titleRestricted: 'Olagliga taggar (huvud, skript, inbäddad stil) kommer att tas bort med innehåll. Alla taggar istället för <p> och <br> kommer att tas bort men innehållet bevaras.',
                titleRelaxed: 'Olagliga taggar (huvud, skript, inbäddad stil) kommer att tas bort med innehåll. Alla taggar istället för basic (b, i, li, sub, a) kommer att tas bort men innehåll bevaras.',
                titleAllowedAll: 'Allt är tillåtet',
                chooseFilter: 'Välj filter för klistrade data'
            }
        },
        pageInfo: {
            document: "Dokument",
            newDocument: {
                text: "Nytt TEXT-document",
                url: "Nytt URL-document",
                file: "Nytt FIL-dokument",
            },
            confirmMessage: "Spara ändringar?",
            confirmMessageOnSaveAndPublish: "Spara ändringar och publicera den här versionen?",
            confirmMessageOnCancel: "Vill du verkligen avsluta?",
            oneLanguageShouldBeEnabled: "Minst ett språk måste vara aktiverat!",
            documentation: "Detaljer om användning",
            documentationLink: "user-documentation/document-management/page-info/index.html",
            title: {
                documentationLink: "user-documentation/document-management/page-info/base.html#title-tab",
                name: "Rubrik",
                title: "Rubrik",
                menuText: "Menytext",
                chooseImage: "Välj bild",
	            showIn: "Visa i",
	            sameFrame: "Samma ram",
	            newWindow: "Nytt fönster",
	            replaceAll: "Ersätt alla",
	            alias: "Förenklad adress",
	            aliasPlaceholder: "detta-dokument-alias",
	            makeSuggestion: 'Ge ett förslag',
	            confirmOverwritingAlias: 'Vill du skriva över det aktuella aliaset?',
	            missingLangRuleTitle: "Om det begärda språket saknas:",
	            showInDefault: "Visa i standardspråk om det är aktiverat",
	            doNotShow: "Visa inte alls",
	            useDefaultLanguageAlias: "Använd standardspråkalias för alla språk!"
            },
            lifeCycle: {
                documentationLink: "user-documentation/document-management/page-info/base.html#life-cycle-tab",
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
                publishMessage: "Tryck \"Spara och publicera\" för att publicera № ",
                error: {
                    userLoadFailed: 'Misslyckades med att hämta utgivare. Försök igen'
                }
            },
            appearance: {
                documentationLink: "user-documentation/document-management/page-info/text-document.html#appearance-tab",
                name: "Utseende",
                template: "Mall",
                defaultChildTemplate: "Standardmall för nya dokument",
	            previewTemplateCSSBtnText: 'Förhandsgranska mall CSS-layout',
	            previewTemplateCSSBtnInfo: 'Klicka och den fungerande versionen av mallens css-stilar kommer att tillämpas på den aktuella sidan!'
            },
	        metadata: {
                documentationLink: "user-documentation/document-management/page-info/text-document.html#metadata-tab",
		        name: "SID INFO",
		        content: "Innehåll",
		        select: "Metatagg",
		        noData: 'Inga föremål',
                add: "LÄGG TILL+"
	        },
            keywords: {
                documentationLink: "user-documentation/document-management/page-info/base.html#keywords-tab",
                name: "Nyckelord",
                title: "Nyckelord",
                placeholder: "nyckelord",
                add: "LÄGG TILL+",
                disableSearch: "Inaktivera sökning"
            },
            categories: {
                documentationLink: "user-documentation/document-management/page-info/base.html#categories-tab",
                name: "Kategorier",
                error: {
                    loadFailed: "Misslyckades med att hämta kategorier. Vänligen försök igen."
                }
            },
            access: {
                documentationLink: "user-documentation/document-management/page-info/base.html#access-tab",
                name: "Behörigheter",
                role: "roll",
                view: "läsa/se",
                edit: "redigera",
                restricted_1: "begränsad 1",
                restricted_2: "begränsad 2",
                addRole: "Lägg till roll",
                linkableByOtherUsers: "Dela ut dokumentet för andra administratörer",
                linkableForUnauthorizedUsers: "Visa länk för obehöriga användare",
                visible: "LÄSA/SE för alla användare",
                error: {
                    loadFailed: "Misslyckades med att hämta roller. Vänligen försök igen."
                }
            },
            permissions: {
                documentationLink: "user-documentation/document-management/page-info/text-document.html#permission-settings-tab",
                name: "Behörighetsinställningar",
                editText: "Redigera text",
                editMenu: "Redigera meny",
                editImage: "Redigera bild",
                editLoop: "Redigera loop",
                editDocInfo: "Redigera sid-info"
            },
            status: {
                documentationLink: "user-documentation/document-management/page-info/base.html#status-tab",
                name: "Status",
                created: "Skapad",
                modified: "Ändrad",
                archived: "Arkiverad",
                published: "Publicerad",
                publicationEnd: "Publicering avslutad",
                by: "Av"
            },
            cache: {
                documentationLink: "developer-documentation/cache.html",
                name: 'Cache',
                cacheSettings: "Cache inställningar",
                cacheForUnauthorizedUsers: 'Cache för obehöriga användare',
                cacheForAuthorizedUsers: 'Cache för behöriga användare',
                invalidateButton: 'Ogiltig',
                invalidateTitle: 'Ogiltig cache',
                error: {
                    failedClear: 'Det gick inte att rensa det aktuella dokumentcachen!'
                }
            },
            file: {
                documentationLink: "user-documentation/document-management/page-info/file-document.html",
                name: "Filer",
                upload: "Ladda upp",
                id: "ID",
                fileName: "Namn",
                isDefault: "Standard"
            },
            url: {
                documentationLink: "user-documentation/document-management/page-info/url-document.html",
                name: "URL",
                title: "URL"
            },
            properties: {
                documentationLink: "user-documentation/document-management/page-info/base.html#properties-tab",
                name: 'Egenskaper',
                add: 'Lägg till',
                key: 'Egendomsnamn',
                value: 'Värde',
            },
            buttons: {
                ok: "ok",
                cancel: "avbryt",
                saveAndPublish: "spara och publicera",
                next: "nästa",
            },
            error: {
	            createDocumentFailed: 'Misslyckades med att spara dokument. Försök igen',
	            loadDocumentFailed: 'Misslyckades att ladda. Kanske har du inte tillgång',
	            duplicateAlias: 'Dokumentet innehåller dubblettalias i: %s språk',
	            noAccess: "Ingen tillgång till sidinformation"
            },
            allData:{
                name: 'All Data',
                edit: 'Ändra',
                errorGettingData: 'Fel att få all data!',
                noData: 'Inga föremål',
                index: 'Index',
                id: 'Id',
                text:{
                    title:'Texter',
                    access:'Tillgång',
                    type:'Typ'
                },
                menu:{
                    title:'Menyer',
                    countElements:'Antal punkter'
                },
                category:{
                    title:'Kategorier',
                    visible:'Synlig'
                },
                loop:{
                    title:'loops',
                    titleSingle:'Loop',
                    countElements:'Antal poster'
                },
                image:{
                    title:'Bilder',
                    allLanguages:'Alla språk',
                    path:'Väg'
                }
            }
        },
        modal: {
            doNotShowAgain: "Visa inte igen",
            title: "Bekräfta",
            editFile: "Redigera fil",
            editDirectory: "Redigera mapp",
            createFile: "Skapa fil",
            createFileOrDirectory: "Skapa fil/mapp",
            userProperties: 'Manager användaregenskaper',
            options: "Alternative",
            yes: "Ja",
            no: "Nej",
            ok: "Ok",
            cancel: "Avbryt",
            save: "Spara",
            create: "Skapa",
        },
        editors: {
            document: {
                title: "Dokumenthanterare",
                freeText: "Fri text",
                freeTextPlaceholder: "Skriv för att söka dokument",
                owner: "Ägare",
                category: "Kategori",
                by: 'av',
                notShownInSelectedLang: 'Inte visad på valt språk',
                deleteInfo: 'Ta bort dokument slutfördes!',
                controls: {
                    edit: {
                        title: "Redigera"
                    },
                    question: 'Vill du ta bort de här dokumenten? Detta kan inte göras ogjort!',
                    question2: 'Du kan inte ångra detta dokument! Ta bort?',
                    multiRemoveInfo: 'Ta bort',
                    actions: 'Åtgärd',
                    removeAction: 'Radera',
                    putToBasketAction: 'Pappers-korg',
                    copy: {
                        title: "Kopiera",
                        confirmMessage: "Vill du verkligen kopiera (all information i dokumentet kommer att kopieras) doc med id ",
                        action: 'Kopiering'
                    }
                },
                sort: {
                    id: "ID",
                    title: "Rubrik",
                    alias: "Alias",
                    modified: "Ändrad",
                    published: "Publ",
                    type: "Typ",
                    status: "Status",
                    version: 'Ver'
                },
                id: {
                    tooltip: {
                        createdOn: 'Skapad'
                    }
                },
                modified: {
                    tooltip: {
                        lastChangedOn: 'Dokumentet ändrades senast',
                    }
                },
                published: {
                    tooltip: {
                        publishedOn: 'Dokumentet publicerades',
                    }
                },
                version: {
                    tooltip: {
                        hasNewerVersion: 'Dokumentet har en opublicerad arbetsversion',
                        noWorkingVersion: 'Dokumentet har ingen arbetsversion',
                    },
                },
                error: {
                    searchFailed: 'Inga sökresultat',
                    userLoadFailed: 'Misslyckades med att hämta användare. Försök igen',
                    categoriesLoadFailed: 'Misslyckades med att hämta kategorier. Försök igen',
                    copyDocumentFailed: 'Det gick inte att kopiera dokumentet. Försök igen',
                    removeDocumentFailed: 'Misslyckades med att ta bort dokument. Försök igen',
                    removeProtectedDocumentFailed: 'Det är inte tillåtet att radera dokument',
                    putToWasteBasketFailed: 'Det gick inte att lägga dokument i papperskorgen'
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
                selectParentPlaceholder: "Ange dokument-ID",
                error: {
                    loadProfilesFailed: "Misslyckades med att hämta kategorier. Försök igen"
                }
            },
            menu: {
                title: "Redigera meny ",
                teaser: "Teaser-menyn ",
                page: "sida ",
                menuTitle: "meny ",
                remove: "Ta bort ur menyn",
                copy: "Kopiera",
                edit: "Redigera Sid-info",
                order: 'Ordning',
                id: "ID",
                docTitle: "Rubrik",
                publishDate: 'Publ',
                publishedTimePrompt: 'Aldrig publicerad!',
                modifiedDate: 'Ändrad',
                status: "Status",
                version: 'Ver',
                removeConfirmation: "Vill du ta bort menyalternativet \"",
                confirmFlatSortMessage: 'Om du byter till en annan sortering återställs ditt TreeSort till en platt meny! Vill du fortsätta?',
                saveAndClose: "Spara och stäng",
                sortNumberTitle: 'Sortera efter siffror',
                visibility: {
                    name: 'Syn',
                    title: {
                        authorized: "B",
                        unauthorized: "O",
                        both: "Alla"
                    },
                    tooltip: {
                        authorized: "Elementet är synligt för behöriga användare",
                        unauthorized: "Elementet är synligt för obehöriga användare",
                        both: "Elementet är synligt för alla användare",
                        nobody: "Elementet är inte synligt för någon"
                    }
                },
                error: {
                    createFailed: 'Det gick inte att spara menyn. Försök igen',
                    copyDocumentFailed: 'Det gick inte att kopiera dokumentet. Försök igen',
                    loadFailed: 'Misslyckades att ladda. Försök igen',
                    invalidSortNumber: 'Ogiltigt data sorteringsnummer!',
                    invalidPosition: 'Ogiltig position!',
                    fixInvalidPosition: 'Menyområdet har en ogiltig position! Snälla fixa det!'

                },
                typesSort: {
                    treeSort: 'Trädsortering',
                    manual: 'Manuell',
                    alphabeticalAsc: 'Alfabetiskt (A-Ö)',
                    alphabeticalDesc: 'Alfabetiskt (Ö-A)',
                    publishedDateAsc: 'Publicerade (nya först)',
                    publishedDateDesc: 'Publicerade (gamla först)',
                    modifiedDateAsc: 'Ändrade (nya först)',
                    modifiedDateDesc: 'Ändrade (gamla först)',

                },
                multiRemoveInfo: 'Ta bort',
                multiRemove: 'Avlägsna',
            },
            loop: {
                title: "Redaktörslinga",
	            teaser: "Teaser-menyn ",
	            page: "sida ",
	            loopTitle: "ögla ",
                createNew: "Skapa ny",
                saveAndClose: "Spara och stäng",
	            resetSorting: "återställ sorteringen",
                id: "ID",
                content: "innehåll",
	            image: "bild",
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
                checkFolderImagesUsage: "Gå till katalog",
                createFolderImage: "Läng till underkatalog",
                editFolderImage: "Ändra Katalognamn",
                deleteFolderImage: "Radera katalog",
                cancel: "Avbryt",
                upload: "Ladda upp",
                useSelectedImage: "Använd vald bild",
                removeImageConfirm: "Vill du ta bort bilden ",
                imageStillUsed: "Bild i bruk",
                removeFolderMessage: "Vill du ta bort mapp \"",
                folderNotEmptyMessage: "Mappen är inte tom",
                newFolderName: "Nytt mappnamn",
                add: "LÄGG TILL+",
                cancelChanges: "Avbryt ändringar",
                openImage: "Öppna bild i nytt fönster",
                editMetadata: "Redigera metadata",
                sortBy: 'Sortera på',
                sorting: {
                    default: 'Standard',
                    az: 'A-Ö',
                    za: 'Ö-A',
                    dateNewFirst: 'Datum (nya först)',
                    dateOldFirst: 'Date (gamla först)',
                },
                error: {
                    removeFailed: 'Misslyckades med att ta bort. Försök igen',
                    checkFailed: 'Misslyckades med att kontrollera. Försök igen',
                    addFolderFailed: 'Misslyckades med att lägga till mapp. Försök igen',
                    loadImagesFailed: 'Misslyckades med att hämta bilder. Försök igen',
                    uploadImagesFailed: 'Misslyckades med att ladda upp bilder. Försök igen'
                },
            },
            image: {
                title: "Redigera bild",
                page: 'sida ',
                imageName: 'bild ',
                teaser: 'Teaser-bild',
                proportion: "Proportioner",
                compression: "Kompression",
                presetCrop: "Förinställt beskärningsformat",
                crop: "Förinställt",
                activeTitle: 'Aktiv bild:',
                noSelectedImage: 'Ingen bild vald!',
                editInNewWindow: 'Redigera i nytt fönster',
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
                    reset: 'Återställ till originalvärde',
                    rotateLeft: 'Rotera vänster',
                    rotateRight: 'Vrid höger',
                    revert: 'Återgå till den ursprungliga förhandsgranskningen',
                    cropping: 'Beskära bild',
                    cancelText: "Avbryt",
                    cancelTitle: "Avbryt ändringar",
                    applyChangeText: "Tillämpa",
                    applyChangeTitle: "Applicera förändringar",
                    removeCropping: "Ta bort beskärning",
                    removeCroppingDescription: "Återgå till tidigare beskärning",
                    revertCropping: "Återgå till originalet",
                    revertCroppingDescription: "Gå tillbaka till det ursprungliga beskärningsområdet",
                    rotationTitle: "Aktivera rotationsreglage"
                },
                styleInfo: {
                    title: 'Begränsade stilar',
                    info: 'Den använda bilden är begränsad till denna stil'
                },
                proportionsButtonTitle: "Bevara bildens proportioner På/Av",
                compressionButtonTitle: "Bildkomprimering På/Av",
                wantedSize: "Önskad storlek (W x H)",
                revertWantedSize: "Önskad storlek (H x W)",
                displaySize: "Visad storlek (W x H)",
                revertDisplaySize: "Visad storlek (H x W)",
                originSize: 'Original storlek (W x H)',
                height: "Höjd",
                width: "Bredd",
                preview: "Förhandsvisning",
                edit: 'Redigerad',
                original: "Original",
                selectImage: "Bildbibliotek",
                suggestAltText: 'Ge ett förslag',
                warnChange: 'Alt-texten är inte tom! Vill du ändra alt-text?',
                altText: "Alt. text (För synnedsatta. Bör anges)",
                altTextRequired: "Alt. text krävs!",
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
                cropCoords: "Beskärningskoordinater",
                fileFormat: "Filformat",
                alignment: "Textjustering",
                altTextConfirm: "Alternativ text saknas. Vill du verkligen fortsätta?",
                showHistory: "Visa historia",
                hideHistory: "Dölj historik",
                cancelAndClose: "Avbryt och stäng",
                removeAndClose: "Ta bort och stäng",
                saveAndClose: "Spara och stäng",
                restrictedStyle: 'Begränsad stil:',
                infoStyle: 'Den använda bilden är begränsad till den här stilen.',
                zoomGrade: 'Zoomgrad',
                exif: {
                    button: "Visa exif info",
                    title: "Visa denna bild EXIF-information",
                },
                error: {
                    loadFailed: 'Det gick inte att ladda bilden. Försök igen',
                    removeFailed: 'Misslyckades med att ta bort bilden. Försök igen',
                    createFailed: 'Misslyckades med att skapa bild. Försök igen'
                }
            },
            imageMetadata: {
                titleMetadataEditor: 'Bildmetadata',
                titleExifInfo: 'Exif Info',
                save: 'Spara',
                cancel: 'Avbryt',
                photographer: 'Fotograf',
                resolution: 'Upplösning (w x h)',
                originalFileSize: 'Originalvikt',
                originalFileType: 'Originalfiltyp',
                uploadedBy: 'Uppladdad av',
                modifiedDate: 'Ändrat datum',
                copyright: 'Copyright',
                licensePeriod: 'Licensperiod',
                titleAllExifMode: 'All Exif Information',
                titleCustomExifMode: 'Anpassad metadata',
                customExifButton: 'Växla till anpassad metadata',
                allExifButton: 'Växla till alla EXIF',
                error: {
                    saveMetadataFailed: 'Misslyckades med att spara metadata'
                }
            },
            text: {
                confirmSave: "Spara ändringar?",
                error: {
                    createFailed: 'Misslyckades med att spara text. Försök igen',
                    filterFailed: 'Det gick inte att filtrera text. Try again',
                }
            }
        },
        status: {
            title: {
                published: "Publicerad",
                publishedWaiting: "Godkänd, väntar",
                inProcess: "Nytt, u a",
                disapproved: "Ej godkänd",
                archived: "Arkiverat",
                passed: "Avslutad",
                wasteBasket: "Papperskorg"
            },
            tooltip: {
                published: "Dokumentet är publicerat",
                publishedWaiting: "Godkänd, väntar",
                inProcess: "Dokumentet är nytt och under arbete",
                disapproved: "Ej godkänd",
                archived: "Arkiverat",
                passed: "Avslutad",
                wasteBasket: "Dokument i papperskorgen"
            },
        },
        languageFlags: {
            alertInfoLanguage: 'Språket kommer att gälla efter ny inloggning (lang:',
            error: {
                loadFailed: 'Misslyckades med att ladda språk. Försök igen'
            }
        },
        userProperties: {
            key: 'Nyckel',
            value: 'Värde',
            add: 'Tillägga',
            successDelete: 'Lyckades bort!',
            wrongKeyError: 'Nyckeln finns, skriv en ny nyckel!',
            emptyValueError: 'Nyckeln och värdet får inte vara tomma!',
            errorMessage: 'Något händer fel',
            deleteConfirm: 'Vill du ta bort egendom?',
            updateMessage: 'Uppdatera fastighets framgång!',
            savedSuccess: 'Fastigheter framgång sparad!',
            save: 'Spara'
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
            oldInterface: 'Äldre gränssnitt',
            documentation: 'Detaljer om användning',
            documentationLink: "user-documentation/admin-settings/index.html",
            users: {
                documentationLink: "user-documentation/admin-settings/users.html",
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
                createNewUser: 'Skapa ny användare',
                error: {
                    loadRolesFailed: 'Misslyckades med att hämta roller. Försök igen',
                    updateFailed: 'Misslyckades med att uppdatera användaren. Försök igen',
                    searchFailed: 'Misslyckades med att söka efter användare. Försök igen',
                    invalidMobilePhoneNumber: 'Ogiltigt mobilnummer!'
                },
                tooltip: {
                    archiveUser: 'Till arkivanvändare',
                    editUser: 'Redigera',
                    mobilePhoneNumberTip: 'Mobilnummer måste innehålla + och landskod!'
                },
            },
            roles: {
                documentationLink: "user-documentation/admin-settings/roles.html",
                name: 'Roller',
                title: 'Roller: administration',
                createNewRole: 'Skapa ny roll',
                roleName: 'Rollnamn',
                save: 'Spara',
                cancel: 'Avbryt',
                documentEditor: 'Dokumentredigerare',
                externalRolesInfo: 'Du kan ställa in fjärrroller för att fungera som lokala',
                permissions: {
                    title: 'Rollbehörigheter',
                    getPasswordByEmail: 'Få lösenord via e-post',
                    accessToAdminPages: 'Tillgång till admin sidor',
                    accessToDocumentEditor: 'Tillgång till dokumentredigerare',
                    publishOwnDocuments: 'Publicera egna skapade dokument',
                    publishAllDocuments: 'Publicera alla dokument (endast med EDIT-tillstånd)'
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
                documentationLink: "user-documentation/admin-settings/ip-access.html",
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
                fromIp: 'Från IP-nummer',
                tillIp: 'Till IP-nummer',
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
            versionControl: {
                documentationLink: "user-documentation/admin-settings/version-control.html",
                name: 'Version Control',
                title: 'Version Control',
                versionId: 'Version ID',
                login: 'User Login',
                publicationDate: 'Publication Date',
                reviewButton: 'Review',
                resetButton: 'Reset',
                input: 'Find versions by document ID',
                findButton: 'Find',
                resetSuccess: 'Dokumentversionen har återställts till ',
                error: {
                    findFailed: 'Kunde inte hämta dokumentversioner',
                    resetFailed: 'Det gick inte att återställa dokumentversionen '
                }
            },
            deleteDocs: {
                documentationLink: "user-documentation/admin-settings/delete-documents.html",
                name: 'Radera dokument',
                title: 'Ta bort dokument med id',
                deleteConfirmation: 'VARNING: Dokumentet och dess data kommer att raderas. Är du säker?',
                deleteDocButton: 'Radera!',
                error: {
                    missedDoc: 'Dokument finns inte!',
                    protectedDoc: 'Det är inte tillåtet att radera detta dokument',
                    removeDocumentFailed: 'Misslyckades med att ta bort dokument',
                    removeProtectedDocumentFailed: 'Det är inte tillåtet att radera dokument'
                },
                basket: {
                    title: 'Papperskorg',
                    metaId: 'Id',
                    headline: 'Länkrubrik',
                    addedDate: 'Tillagd datum',
                    userLogin: 'Användarnamn',
                    input: 'Lägg till metaid i papperskorgen',
                    putButton: "Ok",
                    restoreButton: 'Återställ',
                    deleteButton: 'Radera',
                    error: {
                        readFailed: 'Kunde inte hämta dokument från papperskorgen',
                        putFailed: 'Kunde inte lägga dokument i papperskorgen',
                        restoreFailed: 'Kunde inte återskapa dokument från papperskorgen'
                    }
                }
            },
            dataVersion: {
                name: 'Data-Version',
                error: {
                    loadVersion: 'Det gick inte att ladda versionen!'
                }
            },
            documentsImport: {
                documentationLink: "user-documentation/admin-settings/import.html",
                name: "Importera dokument",
                selectionWindowContainer: {
                    aliasControlCard: "Kontrollalias",
                    editDocumentsCard: "Redigera dokument",
                    uploadDocumentsCard: "Ladda upp dokument"
                },
                controlAliasSection: {
                    removeAliases: "Ta bort alias",
                    replaceAliases: "Byt ut alias",
                    replaceFail: "Det gick inte att ersätta alias",
                    removeFail: "Det gick inte att ta bort alias"
                },
                importSection: {
                    importButton: "Importera dokument",
                    importFail: "Det gick inte att importera dokument!"
                },
                importEntityReferenceSection: {
                    roleReferences: "Rollreferenser",
                    templateReferences: "Mallreferenser",
                    categoryReferences: "Kategorireferenser",
                    categoryTypeReferences: "Kategoritypreferenser",
                    roleReferenceTitle: "roll",
                    templateReferenceTitle: "mall",
                    categoryReferenceTitle: "kategori",
                    categoryTypeReferenceTitle: "kategorityp",
                    roleReferenceTitlePlural: "roller",
                    templateReferenceTitlePlural: "mallar",
                    categoryReferenceTitlePlural: "kategorier",
                    categoryTypeReferenceTitlePlural: "kategorityper",
                    success: "Sparad...",
                    fail: "Misslyckades...",
                },
                uploadDocumentsSection: {
                    uploadButton: "Ladda upp dokument",
                    uploadFail: "Det gick inte att ladda upp!",
                },
                importDocumentListBuilder: {
                    titles: {
                        id: "Importera dokument-ID",
                        metaId: "Meta ID",
                        status: "Status"
                    }
                },
                controls: {
                    previous: "<-- Tidigare",
                    next: "Nästa -->",
                    switchInputType: "Ändra inmatningstyp",
                    rangeInput: {
                        name: "Ange start- och slutdokument-id(rb4)",
                        startId: "Start ID",
                        endId: "End ID",
                        warning: "Ge räckvidd!"
                    },
                    listInput: {
                        name: "Tillhandahåll dokument doc id(rb4)",
                        docIdInput: "Ange dokument-id(rb4)",
                        removeButton: {
                            name: "Avlägsna",
                            warning: "Välj alternativ/er först"
                        },
                        addButton: {
                            name: "Lägg till",
                            warning: "Ange endast dokument-id"
                        }
                    },
                    listButton: {
                        name: "Lista dokument",
                        fail: "Det gick inte att lista dokument!",
                    },
                    filter: {
                        name: "Filtrera",
                        excludeImported: "Uteslut importerade",
                        excludeSkipped: "Uteslut överhoppad"
                    }
                }
            },
            files: {
                documentationLink: "user-documentation/admin-settings/files.html",
                name: 'Filer',
                upload: 'Ladda upp',
                rename: 'Ändra namn',
                add: 'Lägg till',
                moveRight: 'Flytta höger',
                moveLeft: 'Flytta vänster',
                copyRight: 'Kopiera till höger',
                copyLeft: 'Kopiera till vänster',
                overwrite:'Skriva över',
                cancel: 'Annullera',
                defaultRename: 'Byt namn som standard',
                chooseFilename:'Välj filnamn',
                warnDeleteMessage: 'Vill du verkligen ta bort filen?',
                warnEditMessage: 'Redigerar du verkligen innehållsfilen?',
                warnViewDocMessage: 'Vill du omdirigera på nuvarande dokument?',
                title: {
                    createFile: 'Skapa fil',
                    createFileOrDirectory: 'Skapa fil/katalog',
                    createFileName: 'Filnamn',
                    createFileOrDirectoryName: 'Fil/Katalognamn',
                    createDirectory: 'Katalog',
                    fileName: 'Filnamn',
                    directoryName: 'Katalognamn',
                    delete: 'Radera',
                    move: 'Flytta',
                    copy: 'Kopiera',
                    download: 'Ladda ner',
                    edit: 'Redigera',
                    addToGroup: 'Lägg till i gruppen',
                    titleByMove: 'Flytta fil',
                    titleByCopy: 'Kopiera fil',
                    titleContent: 'Filinnehåll',
                    titleEditContent: 'Redigera filinnehåll!',
                    replaceTemplate: 'Välj mall',
                    filename:"Filnamn: ",
                    newFilename:'Nytt filnamn',
                    selectTitle: 'Aktuell/Befintlig fil',
                    current: 'NUVARANDE',
                    existing:'EXISTERANDE',
                    chooseAction:'Välj vad du ska göra med: ',
                },
                error: {
                    loadError: 'Misslyckades med att ladda filer. Försök igen!',
                    loadFileError: 'Misslyckades med att ladda filen. Försök igen!',
                    deleteFailed: 'Misslyckades radera!',
                    renameFailed: 'Misslyckades med att byta namn på filen. Försök igen!',
                    editFailed: 'Misslyckades med att redigera filinnehåll. Försök igen!',
                    createError: 'Misslyckades att skapa fil!',
                    downloadError: 'Misslyckades med att skapa nedladdningsfil. Försök igen!',
                    moveError: 'Misslyckades med att flytta filen. Försök igen!',
                    copyError: 'Misslyckades med att kopiera filen. Försök igen!',
                    uploadError: 'Det gick inte att ladda upp filen. Försök igen!',
                    loadDocError: 'Misslyckades med att ladda dokument!',
                    loadGroups: 'Det gick inte att ladda mallgrupper!',
                    deleteGroup: 'Det gick inte att ta bort mallgrupp!',
                    createGroup: 'Det gick inte att skapa mallgrupp!',
                    editGroup: 'Det gick inte att redigera mallgruppen!',
                    loadGroup: 'Det gick inte att mallgrupp!',
                    addTemplateToGroup: 'Det gick inte att lägga till mall i mallgruppen!',
                    deleteTemplate: 'Det gick inte att ta bort mallen!',
                    deleteGroupFromTemplate: 'Det gick inte att ta bort gruppen från mallen!',
                    replaceTemplate: 'Det gick inte att ersätta mallen!',
                    noOtherTemplates: 'Inga andra mallar',
                    loadTemplates: 'Det gick inte att ladda mallar!',
                    fileAlreadyExists:'Filen finns redan med ett sådant namn!',
                    duplicateFiles: 'Det finns filer med dubbletter av namn!',
                    onlyFilesSupported:'Endast filer stöds!'
                },
                documentData: {
                    docsNumber: 'Antal dokument: ',
                    docView: 'SE',
                    docEdit: 'EDERA',
                },
                groupData: {
                    title: 'Mallgrupp',
                    templatesTableTitle: 'Mallar i gruppen:',
                    edit: 'Redigera',
                    delete: 'Radera',
                    save: 'Spara',
                    cancel: 'Avbryt',
                    create: 'Skapa mallgrupp',
                    deleteConfirm: 'Vill du verkligen ta bort mallgrupp?',
                    saveConfirm: 'Vill du verkligen spara mallgrupp?',
                    cancelConfirm: 'Vill du verkligen avbryta ändringar?',
                    addToGroupConfirm: 'Vill du verkligen lägga till den här mallen i den aktuella mallgruppen?'
                },
                template: {
                    boundDocumentsWarn: 'Vissa dokument relaterade till den här mallen. Vill du ersätta den med en annan mall?',
                }
            },
	        templatesCSS: {
                documentationLink: "user-documentation/admin-settings/template-css.html",
		        name: 'Mallar CSS',
		        editorTitle: 'Mall CSS Editor',
		        templatesSelectTitle: 'Välj mall',
		        history: {
			        headText: "Mallar CSS-historik",
			        closeBtnText: "Avbryt",
			        useBtnText: "Använda sig av",
		        },
		        buttons: {
			        historyBtnText: "Historia",
			        activeVersionBtnText: "Historia",
			        workingVersionBtnText: "Arbetssätt",
			        saveBtnText: "Spara",
			        clearBtnText: "Klar",
			        publishBtnText: "Publicera",
		        },
		        errors: {
			        EMPTY_AREA: "Välj mall",
			        ACTIVE_VERSION: "Byt till fungerande version för att redigera",
			        SAVE_FIRST: "Spara den först för att publicera",
			        EQUALS_WORKING_VERSION: 'Gör ändringar först!',
		        }
	        },
            search: {
                name: 'Sök'
            },
            linkValidator: {
                documentationLink: "user-documentation/admin-settings/link-validator.html",
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
            imagesTab: {
                documentationLink: "user-documentation/editors/image/image-library.html",
                title: 'Bilder',
                label: 'Länk till bildbibliotek'
            },
            categories: {
                documentationLink: "user-documentation/admin-settings/categories.html",
                name: 'Kategorier',
                createButtonName: "Skapa",
                removeButtonName: "Radera",
                saveButton: 'Spara',
                cancelButton: 'Avbryt',
                warnCancelMessage: 'Vill du verkligen avbryta detta?',
                confirmDelete: 'Radera?',
                sections: {
                    createCategoryType: {
                        title: 'Skapa kategori typ',
                        name: 'Kategoritypnamn',
                        inherited: 'Ärvs till nya dokument',
                        visible: 'Synlig i Sid-Info',
                        singleSelect: 'Enkelval',
                        multiSelect: 'Flerval',
                        chooseType: 'Välj kategorityp',
                    },
                    createCategory: {
                        title: 'Skapa kategori',
                        name: 'Kategorinamn',
                        description: 'Beskrivning',
                        categoryType: 'Lägg till i kategoritypen',
                        chooseCategory: 'Välj kategori',
                    }
                },
                error: {
                    invalidName: 'Tomt namn är oacceptabelt! Vänligen välj ett annat namn!',
                    category: {
                        loadFailed: 'Misslyckades med att hämta kategori. Försök igen',
                        removeFailed: 'Denna kategori används i dokument! Är du säker på att du vill ta bort den?',
                        removeForceFailed: 'Misslyckades med att ta bort kategori'
                    },
                    categoryType: {
                        loadFailed: 'Misslyckades med att hämta kategorierna. Försök igen',
                        removeFailed: 'Denna kategorityp har kategorier! Är du säker på att du vill ta bort den?',
                        removeForceFailed: 'Misslyckades med att ta bort kategorierna'
                    }
                }
            },
            profiles: {
                documentationLink: "user-documentation/admin-settings/profiles.html",
                name: 'Profiler',
                title: 'Profiler',
                createButton: "Skapa ny profil",
                warnChangeMessage: 'Vill du ändra den här profilen?',
                warnDelete: 'Ta du verkligen bort den här profilen?',
                warnCancelMessage: 'Vill du verkligen avbryta?',
                cancel: "Avbryt",
                titleTextName: 'Namn',
                titleTextDocName: 'Dokument id/alias',
                createNewProfile: {
                    titleTextName: 'Namn',
                    titleTextDocName: 'Dokument id/alias',
                    textFieldName: 'Textnamn',
                    textFieldDocName: 'Text doc id/alias',
                    buttonSave: 'Save'
                },
                editProfile: {
                    name: "Namn",
                    docName: 'Dokument id/alias',
                    buttonEdit: 'Redigera profil',
                    buttonDelete: 'Radera profil'
                },
                error: {
                    createFailed: 'Misslyckades med att skapa profil. Försök igen',
                    loadFailed: 'Misslyckades med att hämta profiler. Försök igen',
                    errorMessage: 'Tyvärr, men du gjorde fel!'
                }
            },
            systemProperties: {
                documentationLink: "user-documentation/admin-settings/system-properties.html",
                name: 'Systemegenskaper',
                nameInputTitle: 'Namn',
                emailInputTitle: 'E-post',
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
                    },
	                error: {
		                incorrectEmail: "Felaktig e-post"
	                }
                },
                changeButton: "Spara",
                error: {
                    loadFailed: 'Misslyckades med att ladda egenskaper. Försök igen'
                }
            },
            temporalContent: {
                documentationLink: "user-documentation/admin-settings/index-cache.html",
                name: 'Index / Cache',
                actions: {
                    rebuildIndex: 'Indexera om innehållet',
                    deletePublicDocCache: 'Töm publik dokumentcahce',
                    deleteOtherCache: 'Töm annan cache',
                    deleteStaticCache: 'Töm statisk cache',
                    buildCacheDocument: 'Bygg up cache'
                },
                warning: {
                    buildCacheWarning: 'Är du säker på att bygga cache?'
                },
                init: 'Verkställ',
                initIndexing: 'Indexerar',
                indexing: 'Indexering',
                lastUpdate: 'Indexerad',
                timeLeft: 'Tid kvar',
                lastDeleteCache: 'Raderad',
                caching: 'Caching',
                initCaching: 'Bygg caches',
                lastBuildCache: "Skapad"
            },
            menuTab: {
                name: 'Meny'
            },
            documentationTab: {
                title: 'Dokumentation ',
                label: 'Länk till dokumentation senaste versionen'
            }
        },
        login: {
            alternativeLogin: "Alternativ inloggning:",
            error: {
                loadProvidersFailed: 'Misslyckades med auth leverantörer. Försök igen'
            }
        },
        dateTime: {
            yearMonthDay: 'åååå-mm-dd',
            hourMinute: 'tt:mm',
        },
        windowTabs: {
            advancedButton: 'Avancerad meny'
        },
        save: "Spara",
        none: 'Ingen'
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
            special: "Admin",
            specialTitle: "Shows client specific administration",
            document: "documents",
            documentTitle: 'Shows all documents in the document editor',
            admin: "system",
            adminTitle: 'Shows all system settings (only for system administrators)',
            logout: "log out"
        },
        toolbar: {
            documentId: "Document ",
            documentIdTitle: "Document no",
            elementIndex: "Element ",
            elementIndexTitle: "Element no"
        },
        toolTipText: {
            textHistory: "Show text history",
            validateContent: "Validate content over W3C",
            fullScreen: "Full screen",
            save: "Save",
            htmlContent: "HTML content filtering policy",
            bold: 'Bold',
            italic: 'Italic',
            underline: 'Underline',
            sourceCode: "Source code",
            bulletListText: "Bullet list",
            numberedListText: "Numbered list",
            horizontalLineText: "Horizontal line",
            alignLeftText: "Align left",
            alignRightText: "Align right",
            alignCenterText: "Align center",
            alignJustify: "Justify",
            insertLinkText: "Insert/Edit link",
            addImageText: "Add Image",
            switchTextEditor: "Switch to text editor",
            switchTextMode: "Switch to plain text mode",
            switchHTMLMode: "Switch to HTML mode",
            discardChange: "Discard changes",
            discardChangesQuestion: 'Discard changes?',
            textEditor: 'Text Editor',
            imageEditor: 'Image Editor',
            normal: 'Normal',
            auto: 'Auto',
            maximize: 'Maximize',
            close: 'Close',
            filterPolicy: {
                restricted: 'Text only',
                relaxed: 'Strong filter',
                allowedAll: 'No filter',
                titleRestricted: 'Illegal tags (head, script, embed, style) will be removed with content. All tags instead of <p> and <br> will be removed but content kept.',
                titleRelaxed: 'Illegal tags (head, script, embed, style) will be removed with content. All tags instead of basic (b, i, li, sub, a) will be removed but content kept.',
                titleAllowedAll: 'Everything is allowed',
                chooseFilter: 'Choose filter for pasted data'
            }
        },
        pageInfo: {
            document: "Document",
            newDocument: {
                text: "New TEXT document",
                url: "New URL document",
                file: "New FILE document",
            },
            confirmMessage: "Save changes?",
            confirmMessageOnSaveAndPublish: "Save changes and publish this version?",
            confirmMessageOnCancel: "Do you really want to exit?",
            oneLanguageShouldBeEnabled: "At least one language must be enabled!",
            documentation: "Usage details",
            documentationLink: "user-documentation/document-management/page-info/index.html",
            title: {
                documentationLink: "user-documentation/document-management/page-info/base.html#title-tab",
                name: "Title",
                title: "Title",
                menuText: "Menu text",
                chooseImage: "choose...",
	            showIn: "Show in",
	            sameFrame: "Same frame",
	            newWindow: "New window",
	            replaceAll: "Replace all",
	            alias: "Simplified address",
	            aliasPlaceholder: "your-simplified-address",
	            makeSuggestion: 'Make a suggestion',
	            confirmOverwritingAlias: 'Do you want to overwrite current alias?',
	            missingLangRuleTitle: "If requested language is missing:",
	            showInDefault: "Show in default language if enabled",
	            doNotShow: "Don't show at all",
	            useDefaultLanguageAlias: "Use default language alias for all languages!"
            },
            lifeCycle: {
                documentationLink: "user-documentation/document-management/page-info/base.html#life-cycle-tab",
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
                publishMessage: "Please press \"Save and publish\" to publish as version № ",
                error: {
                    userLoadFailed: 'Failed to fetch publishers. Try again'
                }
            },
            appearance: {
                documentationLink: "user-documentation/document-management/page-info/text-document.html#appearance-tab",
                name: "Appearance",
                template: "Template",
                defaultChildTemplate: "Default child template",
	            previewTemplateCSSBtnText:'Preview Template CSS Layout',
	            previewTemplateCSSBtnInfo: 'Click and the working version of template css styles will be applied on current page!'
            },
	        metadata: {
                documentationLink: "user-documentation/document-management/page-info/text-document.html#metadata-tab",
		        name: "Metadata",
		        content: "Content",
		        select: "Meta tag",
		        noData: 'No items',
                add: "ADD+"
	        },
            keywords: {
                documentationLink: "user-documentation/document-management/page-info/base.html#keywords-tab",
                name: "Keywords",
                title: "Keywords",
                placeholder: "keyword",
                add: "ADD+",
                disableSearch: "Disable search"
            },
            categories: {
                documentationLink: "user-documentation/document-management/page-info/base.html#categories-tab",
                name: "Categories",
                error: {
                    loadFailed: "Failed to fetch categories. Try gain"
                }
            },
            access: {
                documentationLink: "user-documentation/document-management/page-info/base.html#access-tab",
                name: "Access",
                role: "role",
                view: "view",
                edit: "edit",
                restricted_1: "restricted 1",
                restricted_2: "restricted 2",
                addRole: "Add role",
                linkableByOtherUsers: "Share the document with other administrators",
                linkableForUnauthorizedUsers: "Show link to unauthorized users",
                visible: "VIEW for all users",
                error: {
                    loadFailed: "Failed to fetch roles. Try gain"
                }
            },
            permissions: {
                documentationLink: "user-documentation/document-management/page-info/text-document.html#permission-settings-tab",
                name: "Permission settings",
                editText: "Edit text",
                editMenu: "Edit menu",
                editImage: "Edit image",
                editLoop: "Edit loop",
                editDocInfo: "Edit doc info"
            },
            status: {
                documentationLink: "user-documentation/document-management/page-info/base.html#status-tab",
                name: "Status",
                created: "Created",
                modified: "Modified",
                archived: "Archived",
                published: "Published",
                publicationEnd: "Publication end",
                by: "By"
            },
            cache: {
                documentationLink: "developer-documentation/cache.html",
                name: 'Cache',
                cacheSettings: "Cache settings",
                cacheForUnauthorizedUsers: 'Cache for unauthorized users',
                cacheForAuthorizedUsers: 'Cache for authorized users',
                invalidateButton: 'Clear cache',
                invalidateTitle: 'Invalidate public',
                error: {
                    failedClear: 'Failed clear current document cache! '
                }
            },
            file: {
                documentationLink: "user-documentation/document-management/page-info/file-document.html",
                name: "Files",
                upload: "Upload",
                id: "ID",
                fileName: "Name",
                isDefault: "Default"
            },
            url: {
                documentationLink: "user-documentation/document-management/page-info/url-document.html",
                name: "URL",
                title: "URL"
            },
            properties: {
                documentationLink: "user-documentation/document-management/page-info/base.html#properties-tab",
                name: 'Properties',
                add: 'Add',
                key: 'Property name',
                value: 'Value',
            },
            buttons: {
                ok: "ok",
                cancel: "cancel",
                saveAndPublish: "save and publish",
                next: "next",
            },
            error: {
	            createDocumentFailed: 'Failed to save document. Try again',
	            loadDocumentFailed: 'Failed to load. Maybe you do not have access',
	            duplicateAlias: 'The document contains duplicate alias in: %s languages',
	            loadRolesFailed: "Failed to fetch roles",
	            noAccess: "No access to page info"
            },
            allData:{
                documentationLink: "user-documentation/document-management/page-info/text-document.html#all-data-tab",
                name: 'All Data',
                edit: 'Edit',
                errorGettingData: 'Error getting all data!',
                noData: 'No items',
                index: 'Index',
                id: 'Id',
                language: 'Language',
                text:{
                    title:'Texts',
                    access:'Access',
                    type:'Type'
                },
                menu:{
                    title:'Menus',
                    countElements:'Number of items'
                },
                category:{
                    title:'Categories',
                    visible:'Visible'
                },
                loop:{
                    title:'Loops',
                    titleSingle:'Loop',
                    countElements:'Number of entries'
                },
                image:{
                    title:'Images',
                    allLanguages:'All languages',
                    path:'Path'
                }
            }
        },
        modal: {
            doNotShowAgain: "Do not show again",
            title: "Confirm",
            editFile: "Edit file",
            editDirectory: "Edit directory",
            userProperties: 'Manager User-Properties',
            options: "Options",
            yes: "Yes",
            no: "No",
            ok: "Ok",
            cancel: "Cancel",
            save: "Save",
            create: "Create",
        },
        editors: {
            document: {
                title: "Document Manager",
                freeText: "Free text",
                freeTextPlaceholder: "Type to find document",
                owner: "Owner",
                category: "Category",
                by: 'by',
                notShownInSelectedLang: 'Not shown in the selected language',
                deleteInfo: 'Delete documents were completed!',
                controls: {
                    edit: {
                        title: "Edit"
                    },
                    question: 'Are you sure want to delete this documents? This can not be undone!',
                    question2: 'You can not revert this document! Remove?',
                    multiRemoveInfo: 'Remove',
                    actions: 'Action',
                    removeAction: 'Delete',
                    putToBasketAction: 'Waste basket',
                    copy: {
                        title: "Copy",
                        confirmMessage: "Do you really want to copy (all information in the document will be copied) doc with id ",
                        action: 'Copying'
                    }
                },
                sort: {
                    id: "ID",
                    title: "Title",
                    alias: "Alias",
                    modified: "Changed",
                    published: "Publ",
                    type: "Type",
                    status: "State",
                    version: 'Ver'
                },
                id: {
                    tooltip: {
                        createdOn: 'Created on'
                    }
                },
                modified: {
                    tooltip: {
                        lastChangedOn: 'The document was last changed on',
                    }
                },
                published: {
                    tooltip: {
                        publishedOn: 'The document was published on',
                    }
                },
                version: {
                    tooltip: {
                        hasNewerVersion: 'The document has an unpublished working version',
                        noWorkingVersion: 'The document has no working version',
                    },
                },
                error: {
                    searchFailed: 'No search result',
                    userLoadFailed: 'Failed to fetch users. Try again',
                    categoriesLoadFailed: 'Failed to fetch categories. Try again',
                    copyDocumentFailed: 'Failed to copy document. Try again',
                    removeDocumentFailed: 'Failed to remove document. Try again',
                    removeProtectedDocumentFailed: 'It is not allowed to delete documents',
                    putToWasteBasketFailed: 'Failed to put documents to the waste basket'
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
                selectParentPlaceholder: "State document ID",
                error: {
                    loadProfilesFailed: "Failed to fetch categories. Try again"
                }
            },
            menu: {
                title: "Menu Editor ",
                teaser: "Teaser menu ",
                page: "page ",
                menuTitle: "menu ",
                remove: "Remove",
                copy: "Copy",
                edit: "Edit",
                order: 'Order',
                id: "ID",
                docTitle: "Title",
                publishDate: 'Publ',
                modifiedDate: 'Changed',
                publishedTimePrompt: 'Never published!',
                status: "State",
                version: 'Ver',
                removeConfirmation: "Do you want to remove the menu item \"",
                saveAndClose: "Save and close",
                confirmFlatSortMessage: 'If you change to another sorting, your TreeSort will be resetted to a flat menu! Do you wish to proceed?',
                sortNumberTitle: 'Sort by numbers',
                visibility: {
                    name: 'Vis',
                    title: {
                        authorized: "A",
                        unauthorized: "U",
                        both: "All"
                    },
                    tooltip: {
                        authorized: "The element is visible to authorized users",
                        unauthorized: "The element is visible to unauthorized users",
                        both: "The element is visible to all users",
                        nobody: "The element is not visible to anyone"
                    }
                },
                error: {
                    createFailed: 'Failed to save menu. Try again',
                    copyDocumentFailed: 'Failed to copy document. Try again',
                    loadFailed: 'Failed to load. Try again',
                    invalidSortNumber: 'Invalid data sort number!',
                    invalidPosition: 'Invalid position!',
                    fixInvalidPosition: 'Menu area has invalid position item! Please fix it !'
                },
                typesSort: {
                    treeSort: 'Tree sort',
                    manual: 'Manual',
                    alphabeticalAsc: 'Alphabetical (A-Z)',
                    alphabeticalDesc: 'Alphabetical (Z-A)',
                    publishedDateAsc: 'Published (new first)',
                    publishedDateDesc: 'Published (old first)',
                    modifiedDateAsc: 'Changed (new first)',
                    modifiedDateDesc: 'Changed (old first)',

                },
                multiRemoveInfo: 'Remove',
                multiRemove: 'Remove',
            },
            loop: {
                title: "Loop Editor",
	            teaser:"Teaser menu ",
	            page:"page ",
	            loopTitle: "loop ",
                createNew: "Create New",
                saveAndClose: "Save and close",
	            resetSorting: "Reset sorting",
                id: "ID",
                content: "text content",
	            image: "image",
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
                editFolderImage: "Edit folder",
                deleteFolderImage: "Delete folder",
                cancel: "Cancel",
                upload: "Upload",
                useSelectedImage: "Use selected image",
                removeImageConfirm: "Do you want to remove image ",
                imageStillUsed: "Image in use",
                removeFolderMessage: "Do you want to remove folder \"",
                folderNotEmptyMessage: "Folder not empty",
                newFolderName: "New folder name",
                add: "ADD+",
                cancelChanges: "Cancel changes",
                openImage: "Open image in new window",
                editMetadata: "Edit metadata",
                sortBy: 'Sort by',
                sorting: {
                    default: 'Default',
                    az: 'A-Z',
                    za: 'Z-A',
                    dateNewFirst: 'Date (new first)',
                    dateOldFirst: 'Date (old first)',
                },
                error: {
                    removeFailed: 'Failed to remove. Try again',
                    checkFailed: 'Failed to check. Try again',
                    addFolderFailed: 'Failed to add folder. Try again',
                    loadImagesFailed: 'Failed to fetch images. Try again',
                    uploadImagesFailed: 'Failed to fetch images. Try again'
                },
            },
            image: {
                title: "Edit Image",
                page: 'page ',
                imageName: 'image ',
                teaser: "Teaser image ",
                proportion: "Proportions",
                compression: "Compression",
                presetCrop: "Preset crop format",
                crop: "Crop",
                activeTitle: 'Active image:',
                noSelectedImage: 'No image selected!',
                editInNewWindow: 'Edit in new window',
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
                    reset: 'Reset to original value',
                    rotateLeft: 'Rotate left',
                    rotateRight: 'Rotate right',
                    revert: 'Revert to the original preview',
                    cropping: 'Crop image',
                    cancelText: "Cancel",
                    cancelTitle: "Cancel changes",
                    applyChangeText: "Apply",
                    applyChangeTitle: "Apply changes",
                    removeCropping: "Remove cropping",
                    removeCroppingDescription: "Revert to previous cropping",
                    revertCropping: "Revert to original",
                    revertCroppingDescription: "Revert to original cropping area",
                    rotationTitle: "Activate rotation controls"
                },
                styleInfo: {
                    title: 'Restricted styles',
                    info: 'The used image is restricted to this styles'
                },
                proportionsButtonTitle: "Keep image proportion On/Off",
                compressionButtonTitle: "Image compression On/Off",
                wantedSize: "Wanted size (W x H)",
                revertWantedSize: "Wanted size (H x W)",
                displaySize: "Display size (W x H)",
                revertDisplaySize: "Display size (H x W)",
                originSize: 'Original size (W x H)',
                height: "Height",
                width: "Width",
                preview: "Preview",
                edit: "Edited",
                original: "Original",
                selectImage: "Image Library",
                suggestAltText: 'Make a suggestion',
                warnChange: 'Alt text isn\'t empty! Do you want to change alt text?',
                altText: "Alt. text (For visually impaired. Should be specified)",
                altTextRequired: "Alt. text is required",
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
                showHistory: "Show history",
                hideHistory: "Hide history",
                cancelAndClose: "Cancel and close",
                removeAndClose: "Remove and close",
                saveAndClose: "Save and close",
                cancel: "Cancel",
                restrictedStyle: 'Restricted style:',
                infoStyle: 'The used image is restricted to this style.',
                zoomGrade: 'Zoom grade',
                exif: {
                    button: "Show exif info",
                    title: "Show this image EXIF information",
                },
                error: {
                    loadFailed: 'Failed to load image. Try again',
                    removeFailed: 'Failed to remove image. Try again',
                    createFailed: 'Failed to create image. Try again',
                    loadHistoryFailed: 'Failed to load history'
                }
            },
            imageMetadata: {
                titleMetadataEditor: 'Image Metadata',
                titleExifInfo: 'Exif Info',
                save: 'Save',
                cancel: 'Cancel',
                photographer: 'Photographer',
                resolution: 'Resolution (w x h)',
                originalFileSize: 'Original size',
                originalFileType: 'Original file type',
                uploadedBy: 'Uploaded by',
                modifiedDate: 'Modified date',
                copyright: 'Copyright',
                licensePeriod: 'License period',
                titleAllExifMode: 'All Exif Information',
                titleCustomExifMode: 'Custom Metadata',
                customExifButton: 'Switch to custom metadata',
                allExifButton: 'Switch to all exif',
                error: {
                    saveMetadataFailed: 'Failed to save metadata'
                }
            },
            text: {
                confirmSave: "Save changes?",
                error: {
                    createFailed: 'Failed to save text. Try again',
                    filterFailed: 'Failed to filter text. Try again',
                }
            }
        },
        status: {
            title: {
                published: "A, p",
                publishedWaiting: "A, w",
                inProcess: "New, u c",
                disapproved: "D",
                archived: "Arc",
                passed: "P end",
                wasteBasket: "W basket"
            },
            tooltip: {
                published: "The document is published",
                publishedWaiting: "Approved, waiting",
                inProcess: "The document is new and under construction",
                disapproved: "Disapproved",
                archived: "Archived",
                passed: "Publication end",
                wasteBasket: "The document in the waste basket"
            },
        },
        languageFlags: {
            alertInfoLanguage: 'Language will effect after new login (lang: ',
            error: {
                loadFailed: 'Failed to load languages. Try again'
            }
        },
        userProperties: {
            key: 'Key',
            value: 'Value',
            add: 'Add',
            successDelete: 'Successed deleted!',
            wrongKeyError: 'The key exists, please write another key!',
            emptyValueError : 'The key and value must not be empty!',
            errorMessage: 'Something happens wrong',
            deleteConfirm: 'Do you want to remove property?',
            updateMessage: 'Update property success!',
            savedSuccess: 'Properties success saved!',
            save: 'Save'
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
            oldInterface: 'Old interface',
            documentation: 'Usage details',
            documentationLink: "user-documentation/admin-settings/index.html",
            users: {
                documentationLink: "user-documentation/admin-settings/users.html",
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
                    searchFailed: 'Failed to search for users. Try again',
                    invalidMobilePhoneNumber: 'Invalid mobile phone number!'
                },
                tooltip: {
                    archiveUser: 'Archive user',
                    editUser: 'Edit',
                    mobilePhoneNumberTip: "Mobile phone number must contain + and country code!"
                },
            },
            roles: {
                documentationLink: "user-documentation/admin-settings/roles.html",
                name: 'Roles',
                title: 'Roles administration',
                createNewRole: 'Create new role',
                roleName: 'Role name',
                save: 'Save',
                documentEditor: 'Document editor',
                cancel: 'Cancel',
                externalRolesInfo: 'You can set remote roles to behave as local ones',
                permissions: {
                    title: 'Role permissions',
                    getPasswordByEmail: 'Get password by email',
                    accessToAdminPages: 'Access to admin pages',
                    accessToDocumentEditor: 'Access to document editor',
                    publishOwnDocuments: 'Publish own created documents',
                    publishAllDocuments: 'Publish all documents (only with EDIT-permission)'
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
                documentationLink: "user-documentation/admin-settings/ip-access.html",
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
                fromIp: 'From IP-number',
                tillIp: 'Till IP-number',
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
            versionControl: {
                documentationLink: "user-documentation/admin-settings/version-control.html",
                name: 'Version Control',
                title: 'Version Control',
                versionId: 'Version ID',
                login: 'User Login',
                publicationDate: 'Publication Date',
                reviewButton: 'Review',
                resetButton: 'Reset',
                input: 'Find versions by document ID',
                findButton: 'Find',
                resetSuccess: 'Document version has been reset to ',
                error: {
                    findFailed: 'Failed to fetch document versions',
                    resetFailed: 'Failed to reset document version '
                }
            },
            deleteDocs: {
                documentationLink: "user-documentation/admin-settings/delete-documents.html",
                name: 'Delete Documents',
                title: 'Delete document by id',
                deleteConfirmation: 'WARNING: Document and it\'s data will be deleted. Are you sure?',
                deleteDocButton: 'Delete!',
                error: {
                    missedDoc: 'Document does not exist!',
                    protectedDoc: 'It is not allowed to delete this document',
                    removeDocumentFailed: 'Failed to remove document. Try again',
                    removeProtectedDocumentFailed: 'It is not allowed to delete documents'
                },
                basket: {
                    title: 'Waste Basket',
                    metaId: 'Id',
                    headline: 'Headline',
                    addedDate: 'Added date',
                    userLogin: 'Username',
                    input: 'Put document to the waste basket by id',
                    putButton: "Put",
                    restoreButton: 'Restore',
                    deleteButton: 'Delete',
                    error: {
                        readFailed: 'Failed to fetch documents from the waste basket',
                        putFailed: 'Failed to put the document',
                        restoreFailed: 'Failed to restore the document from the waste basket'
                    }
                }
            },
            dataVersion: {
                name: 'Data-Version',
                error: {
                    loadVersion: 'Failed version load!'
                }
            },
	        documentsImport: {
                documentationLink: "user-documentation/admin-settings/import.html",
		        name: "Import Documents",
                selectionWindowContainer:{
                    aliasControlCard:"Control alias",
                    editDocumentsCard:"Edit documents",
                    uploadDocumentsCard:"Upload documents"
                },
                controlAliasSection:{
                    removeAliases:"Remove aliases",
                    replaceAliases:"Replace aliases",
					replaceFail:"Failed to replace aliases",
					removeFail:"Failed to remove aliases"
                },
                importSection:{
                    importButton:"Import documents",
					importFail:"Failed to import documents!"
                },
                importEntityReferenceSection:{
                    roleReferences:"Role references",
                    templateReferences:"Template references",
                    categoryReferences:"Category references",
                    categoryTypeReferences:"Category type references",
                    roleReferenceTitle:"role",
                    templateReferenceTitle:"template",
                    categoryReferenceTitle:"category type",
                    categoryTypeReferenceTitle:"category type",
                    roleReferenceTitlePlural:"roles",
                    templateReferenceTitlePlural:"template",
                    categoryReferenceTitlePlural:"categories",
                    categoryTypeReferenceTitlePlural:"category types",
                    success:"Saved...",
                    fail:"Failed...",
                },
                uploadDocumentsSection:{
                    uploadButton:"Upload documents",
                    uploadFail: "Failed to upload!",
                },
                importDocumentListBuilder:{
                    titles:{
                        id:"Import document ID",
                        metaId:"Meta ID",
                        status:"Status"
                    }
                },
                controls:{
                    previous:"<-- Previous",
                    next:"Next -->",
                    switchInputType:"Change input type",
                    rangeInput:{
                        name:"Provide start and end doc id(rb4)",
						startId:"Start ID",
						endId:"End ID",
                        warning:"Provide range!"
                    },
                    listInput:{
                        name:"Provide documents doc id(rb4)",
						docIdInput:"Enter doc id(rb4)",
						removeButton:{
							name:"Remove",
							warning:"Select option/s first"
						},
						addButton:{
							name:"Add",
							warning:"Input only doc id"
						}
                    },
                    listButton:{
						name:"List documents",
                        fail:"Failed to list documents!",
                    },
                    filter:{
                        name:"Filter",
                        excludeImported:"Exclude imported",
                        excludeSkipped:"Exclude skipped"
                    }
                }
	        },
            files: {
                documentationLink: "user-documentation/admin-settings/files.html",
                name: 'Files',
                upload: 'Upload',
                rename: 'Change name',
                add: 'Add',
                moveRight: 'Move right',
                moveLeft: 'Move left',
                copyRight: 'Copy right',
                copyLeft: 'Copy left',
                overwrite:'Overwrite',
                cancel: 'Cancel',
                defaultRename: 'Default rename',
                chooseFilename:'Choose filename',
                warnDeleteMessage: 'Do you really want to delete the file?',
                warnEditMessage: 'Do you really edit content file?',
                warnViewDocMessage: 'Do you want to redirect on current document?',
                title: {
                    createFile: 'Create file',
                    createFileOrDirectory: 'Create file/directory',
                    createFileName: 'File name',
                    createFileOrDirectoryName: 'File/Directory name',
                    createDirectory: 'Directory',
                    fileName: 'File name',
                    directoryName: 'Directory name',
                    delete: 'Delete',
                    move: 'Move',
                    copy: 'Copy',
                    download: 'Download',
                    edit: 'Edit',
                    addToGroup: 'Add to group',
                    titleByMove: 'Buttons for move file',
                    titleByCopy: 'Buttons for copy file',
                    titleContent: 'File content',
                    titleEditContent: 'Edit file content!',
                    replaceTemplate: 'Choose template',
                    filename:"Filename: ",
                    newFilename:'New filename',
                    selectTitle: 'Current/Existing file',
                    current: 'CURRENT',
                    existing:'EXISTING',
                    chooseAction:'Choose what to do with: ',
                },
                error: {
                    loadError: 'Failed to load files. Try again!',
                    loadFileError: 'Failed to load files. Try again!',
                    deleteFailed: 'Failed delete!',
                    renameFailed: 'Failed to rename file. Try again!',
                    editFailed: 'Failed to edit file content. Try again!',
                    createError: 'Failed to create file!',
                    downloadError: 'Failed to create download file. Try again!',
                    moveError: 'Failed to move file. Try again!',
                    copyError: 'Failed to copy file. Try again!',
                    uploadError: 'Failed to upload file. Try again!',
                    loadDocError: 'Failed to load documents!',
                    loadGroups: 'Failed to load template groups!',
                    deleteGroup: 'Failed to delete template group!',
                    createGroup: 'Failed to create template group!',
                    editGroup: 'Failed to edit template group!',
                    loadGroup: 'Failed to load template group!',
                    addTemplateToGroup: 'Failed to add template to template group!',
                    deleteTemplate: 'Failed to delete template!',
                    deleteGroupFromTemplate: 'Failed to delete group from template!',
                    replaceTemplate: 'Failed to replace template!',
                    noOtherTemplates: 'No other templates!',
                    loadTemplates: 'Failed to load templates!',
                    fileAlreadyExists:'File already exists with such name!',
                    duplicateFiles: 'There are files with duplicate names!',
                    onlyFilesSupported:'Only files supported!'
                },
                documentData: {
                    docsNumber: 'Number of documents: ',
                    docView: 'VIEW',
                    docEdit: 'EDIT',
                },
                groupData: {
                    title: 'Template group',
                    templatesTableTitle: 'Templates in the group:',
                    edit: 'Edit',
                    delete: 'Delete',
                    save: 'Save',
                    cancel: 'Cancel',
                    create: 'Create template group',
                    deleteConfirm: 'Do you really want to delete template group?',
                    saveConfirm: 'Do you really want to save template group?',
                    cancelConfirm: 'Do you really want to cancel changes?',
                    addToGroupConfirm: 'Do you really want to add this template to the current template group?'
                },
                template: {
                    boundDocumentsWarn: 'Some documents related to this template. Do you want to replace it to another template?',
                }
            },
	        templatesCSS: {
                documentationLink: "user-documentation/admin-settings/template-css.html",
		        name: 'Templates CSS',
		        editorTitle: 'Template CSS Editor',
		        templatesSelectTitle: 'Choose template',
		        history: {
			        headText: "Templates CSS History",
			        closeBtnText: "Cancel",
			        useBtnText: "Use",
		        },
		        buttons: {
			        historyBtnText: "History",
			        activeVersionBtnText: "Active",
			        workingVersionBtnText: "Working",
			        saveBtnText: "Save",
			        clearBtnText: "Clear",
			        publishBtnText: "Publish",
		        },
		        errors: {
			        EMPTY_AREA: "Choose template!",
			        ACTIVE_VERSION: "In order to edit switch to working version!",
			        SAVE_FIRST: "In order to publish please save it first!",
			        EQUALS_WORKING_VERSION:'Make changes first!'
		        }
	        },
            search: {
                name: 'Search'
            },
            linkValidator: {
                documentationLink: "user-documentation/admin-settings/link-validator.html",
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
            imagesTab: {
                documentationLink: "user-documentation/editors/image/image-library.html",
                title: 'Images',
                label: 'Link to images library'
            },
            categories: {
                documentationLink: "user-documentation/admin-settings/categories.html",
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
                        visible: 'Visible in Page-Info',
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
                        loadFailed: 'Failed to fetch category. Try again',
                        removeFailed: 'This category is used in documents! Are you sure you want to delete it?',
                        removeForceFailed: 'Failed to remove category'
                    },
                    categoryType: {
                        loadFailed: 'Failed to fetch categories types. Try again',
                        removeFailed: 'This category type has categories! Are you sure you want to remove it?',
                        removeForceFailed: 'Failed to remove categories types'
                    }
                }
            },
            profiles: {
                documentationLink: "user-documentation/admin-settings/profiles.html",
                name: 'Profiles',
                title: 'Profiles',
                createButton: 'Create new profile',
                warnChangeMessage: 'Do you want to change this profile?',
                warnDelete: 'Do you really delete this profile?',
                warnCancelMessage: 'Do you want to really cancel?',
                cancel: 'Cancel',
                titleTextName: 'Name',
                titleTextDocName: 'Document id/alias',
                createNewProfile: {
                    titleTextName: 'Name',
                    titleTextDocName: 'Document id/alias',
                    textFieldName: 'Input name',
                    textFieldDocName: 'Input document id/alias',
                    buttonSave: 'Save'
                },
                editProfile: {
                    name: 'Name',
                    docName: 'Document id/alias',
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
                documentationLink: "user-documentation/admin-settings/system-properties.html",
                name: 'System Properties',
                nameInputTitle: 'Name',
                emailInputTitle: 'E-mail',
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
                    },
	                error: {
		                incorrectEmail: "Incorrect email"
	                }
                },
                changeButton: "Change",
                error: {
                    loadFailed: 'Failed to load properties. Try again'
                }
            },
            temporalContent: {
                documentationLink: "user-documentation/admin-settings/index-cache.html",
                name: 'Index / Cache',
                actions: {
                    rebuildIndex: 'Reindex documents',
                    deletePublicDocCache: 'Delete public documents cache',
                    deleteOtherCache: 'Delete other web cache',
                    deleteStaticCache: 'Delete static web cache',
                    buildCacheDocument: 'Build cache'
                },
                warning: {
                  buildCacheWarning: 'Are you sure to build cache ?'
                },
                init: 'Init',
                initIndexing: 'Indexes',
                indexing: 'Indexing',
                lastUpdate: 'Last updated',
                timeLeft: 'Time left',
                lastDeleteCache: 'Last updated',
                caching: 'Caching',
                initCaching: 'Build Caches',
                lastBuildCache: 'Built'
            },
            menuTab: {
                name: 'Menu'
            },
            documentationTab: {
                title: 'Documentation',
                label: 'Link to documentation latest version '
            }
        },
        login: {
            alternativeLogin: "Alternative login:",
            error: {
                loadProvidersFailed: 'Failed to auth providers. Try again'
            }
        },
        dateTime: {
            yearMonthDay: 'yyyy-mm-dd',
            hourMinute: 'hh:mm',
        },
        windowTabs: {
            advancedButton: 'Advanced menu'
        },
        save: "Save",
        none: 'None'
    },
    "no": {
        panel: {
            settingsTitle: 'Administrasjonspanelinnstillinger',
            settingsList: {
                size: {
                    name: "Panelstørrelse",
                    small: "Liten",
                    smallTitle: "Panelet blir mindre med færre tekster",
                    large: "Stor",
                    largeTitle: "Panelet blir større med tilleggstekster"
                },
                appearance: {
                    name: "Panelutseende",
                    auto: "Auto",
                    autoTitle: "Vises automatisk når du beveger musen nær toppen av siden, skjuler seg når du klikker med musen utenfor",
                    hidden: "Skjult",
                    hiddenTitle: "Panelet er skjult som standard, du kan åpne det ved å klikke på det lille ikonet øverst på siden",
                    visible: "Synlig",
                    visibleTitle: "Panelet er alltid synlig"
                },
                unlockPanel: "Lås opp administrasjonspanelet",
                hidePanel: "Skjul administrasjonspanelet"
            },
            error: {
                loadFailed: "Kunne ikke laste inn på nytt. Prøv igjen"
            },
            "public": "offentlig",
            publicTitle: 'Viser den publiserte visningen',
            edit: "redigere",
            editTitle: 'Viser den redigerbare arbeidsversjonen',
            preview: "forhåndsvisning",
            previewTitle: 'Viser den redigerbare arbeidsversjonen',
            publish: "publisere",
            publishTitle: 'Publiser den fungerende versjonen umiddelbart',
            pageInfo: "side info",
            pageInfoTitle: 'Viser alle sideinnstillinger og metadata',
            special: "Admin",
            specialTitle: "Viser kundespesifikk administrasjon",
            document: "dokument",
            documentTitle: 'Viser alle dokumenter i dokumentredigereren',
            admin: "system",
            adminTitle: 'Viser alle systeminnstillinger (kun for systemadministratorer)',
            logout: "Logg ut"
        },
        toolbar: {
            documentId: "Dokument ",
            documentIdTitle: "Dokument no",
            elementIndex: "Element ",
            elementIndexTitle: "Element no"
        },
        toolTipText: {
            textHistory: "Se teksthistorikk",
            validateContent: "Valider innhold over W3C",
            fullScreen: "Full skjerm",
            save: "Lagre",
            htmlContent: "Håndtering av HTML-innholdsfiltrering",
            bold: 'Fet',
            italic: 'Kursiv',
            underline: 'Understreking',
            sourceCode: "Kildekode",
            bulletListText: "Punktliste",
            numberedListText: "Nummerert liste",
            horizontalLineText: "Horisontal linje",
            alignLeftText: "Juster venstre",
            alignRightText: "Juster høyre",
            alignCenterText: "Midtstill",
            alignJustify: "i henhold til standard",
            insertLinkText: "Sett inn/rediger lenke",
            addImageText: "Legg til bilde",
            switchTextEditor: "Bytt til tekstredigering",
            switchTextMode: "Bytt til ren tekstmodus",
            switchHTMLMode: "Bytt til HTML-modus",
            discardChange: "Angre endringer",
            discardChangesQuestion: 'Ignorer endringer?',
            textEditor: 'Tekstredigerer',
            imageEditor: 'Bilderedigerer',
            normal: 'Vanlig',
            auto: 'Auto',
            maximize: 'Maksimer',
            close: 'Lukk',
            filterPolicy: {
                restricted: 'Kun tekst',
                relaxed: 'Kraftig filter',
                allowedAll: 'Ingen filter',
                titleRestricted: 'Ulovlige tagger (header, script, innebygd stil) vil bli fjernet med innhold. Alle tagger i stedet for <p> og <br> vil bli fjernet, men innholdet vil bli bevart.',
                titleRelaxed: 'Ulovlige tagger (header, script, innebygd stil) vil bli fjernet med innhold. Alle tagger i stedet for basic (b, i, li, sub, a) vil bli fjernet, men innholdet blir bevart.',
                titleAllowedAll: 'Alt er lov',
                chooseFilter: 'Velg filter for innlimte data'
            }
        },
        pageInfo: {
            document: "Dokument",
            newDocument: {
                text: "Nytt TEXT-dokument",
                url: "Nytt URL-dokument",
                file: "Nytt FIL-dokument",
            },
            confirmMessage: "Lagre endringer?",
            confirmMessageOnSaveAndPublish: "Vil du lagre endringer og publisere denne versjonen?",
            confirmMessageOnCancel: "Vil du virkelig slutte?",
            oneLanguageShouldBeEnabled: "Minst ett språk må være aktivert!",
            documentation: "Detaljer om bruk",
            documentationLink: "user-documentation/document-management/page-info/index.html",
            title: {
                documentationLink: "user-documentation/document-management/page-info/base.html#title-tab",
                name: "Overskrift",
                title: "Overskrift",
                menuText: "Menytekst",
                chooseImage: "Velg bilde",
                showIn: "Vis inn",
                sameFrame: "Samme ramme",
                newWindow: "Nytt vindu",
                replaceAll: "Bytt ut alle",
                alias: "Forenklet adresse",
                aliasPlaceholder: "dette-dokument-alias",
                makeSuggestion: 'Komme med et forslag',
                confirmOverwritingAlias: 'Vil du overskrive gjeldende alias?',
                missingLangRuleTitle: "Hvis det forespurte språket mangler:",
                showInDefault: "Vises på standardspråk hvis aktivert",
                doNotShow: "Ikke vis i det hele tatt",
                useDefaultLanguageAlias: "Bruk standardspråkalias for alle språk!"
            },
            lifeCycle: {
                documentationLink: "user-documentation/document-management/page-info/base.html#life-cycle-tab",
                name: "Livssyklus",
                status: {
                    title: "Status",
                    inProcess: "Ny",
                    disapproved: "Ikke godkjent",
                    approved: "Godkjent"
                },
                now: "Nå",
                clear: "Klar",
                published: {
                    title: "Publisert",
                    dateTitle: "Angi publiseringsdato",
                    timeTitle: "Angi publisert tid",
                    dateTimeTitle: "Lagret dato/klokkeslett"
                },
                archived: {
                    title: "Arkivert ",
                    dateTitle: "Skriv inn innleveringsdatoen",
                    timeTitle: "Angi tidspunkt for arkivering",
                    dateTimeTitle: "Lagret arkiv dato-klokkeslett"
                },
                publicationEnd: {
                    title: "Publikasjonen avsluttes",
                    dateTitle: "Angi sluttdato for publikasjonen",
                    timeTitle: "Angi sluttid for publisering",
                    dateTimeTitle: "Lagret sluttdato-tid"
                },
                publisher: "Forlegger",
                currentVersion: "Gjeldende versjon:",
                versionHasChanges: "Denne fungerende versjonen har endringer",
                publishMessage: "Trykk \"Lagre og publiser\" for å publisere",
                error: {
                    userLoadFailed: 'Kunne ikke hente utgiveren. Prøv igjen'
                }
            },
            appearance: {
                documentationLink: "user-documentation/document-management/page-info/text-document.html#appearance-tab",
                name: "Utseende",
                template: "Mal",
                defaultChildTemplate: "Standard mal for nye dokumenter",
                previewTemplateCSSBtnText:'Forhåndsvis mal CSS-oppsett',
                previewTemplateCSSBtnInfo: 'Klikk og arbeidsversjonen av mal css-stiler vil bli brukt på gjeldende side!'
            },
            metadata: {
                documentationLink: "user-documentation/document-management/page-info/text-document.html#metadata-tab",
                name: "Metadata",
                content: "Content",
                select: "Meta tag",
                noData: 'Ingen data',
                add: "LEGGE TIL+"
            },
            keywords: {
                documentationLink: "user-documentation/document-management/page-info/base.html#keywords-tab",
                name: "Nøkkelord",
                title: "Nøkkelord",
                placeholder: "nøkkelord",
                add: "LEGGE TIL+",
                disableSearch: "Deaktiver søk"
            },
            categories: {
                documentationLink: "user-documentation/document-management/page-info/base.html#categories-tab",
                name: "Kategorier",
                error: {
                    loadFailed: "Kunne ikke hente kategorier. Vær så snill, prøv på nytt."
                }
            },
            access: {
                documentationLink: "user-documentation/document-management/page-info/base.html#access-tab",
                name: "Tillatelser",
                role: "rolle",
                view: "lese/se",
                edit: "redigere",
                restricted_1: "begrenset 1",
                restricted_2: "begrenset 2",
                addRole: "Legg til rolle",
                linkableByOtherUsers: "Jaa asiakirja muiden ylläpitäjien kanssa",
                linkableForUnauthorizedUsers: "Näytä linkki luvattomille käyttäjille",
                visible: "VIS for alle brukere",
                error: {
                    loadFailed: "Kunne ikke hente roller. Vær så snill, prøv på nytt."
                }
            },
            permissions: {
                documentationLink: "user-documentation/document-management/page-info/text-document.html#permission-settings-tab",
                name: "Tillatelsesinnstillinger",
                editText: "Rediger tekst",
                editMenu: "Rediger menyen",
                editImage: "Rediger bilde",
                editLoop: "Rediger loop",
                editDocInfo: "Rediger sideinformasjon"
            },
            status: {
                documentationLink: "user-documentation/document-management/page-info/base.html#status-tab",
                name: "Status",
                created: "Opprettet",
                modified: "Endret",
                archived: "Arkivert",
                published: "Publisert",
                publicationEnd: "Publisering fullført",
                by: "Av"
            },
            cache: {
                documentationLink: "developer-documentation/cache.html",
                name: 'Cache',
                cacheSettings: "Bufferinnstillinger",
                cacheForUnauthorizedUsers: 'Buffer for uautoriserte brukere',
                cacheForAuthorizedUsers: 'Buffer for autoriserte brukere',
                invalidateButton: 'Ugyldig',
                invalidateTitle: 'Ugyldig cache',
                error: {
                    failedClear: 'Kunne ikke tømme gjeldende dokumentbuffer!'
                }
            },
            file: {
                documentationLink: "user-documentation/document-management/page-info/file-document.html",
                name: "Filer",
                upload: "Laste opp",
                id: "ID",
                fileName: "Navn",
                isDefault: "Standard"
            },
            url: {
                documentationLink: "user-documentation/document-management/page-info/url-document.html",
                name: "URL",
                title: "URL"
            },
            properties: {
                documentationLink: "user-documentation/document-management/page-info/base.html#properties-tab",
                name: 'Egenskaper',
                add: 'Legge til',
                key: 'Eiendomsnavn',
                value: 'Verdi',
            },
            buttons: {
                ok: "ok",
                cancel: "avbryt",
                saveAndPublish: "lagre og publiser",
                next: "neste",
            },
            error: {
                createDocumentFailed: 'Kunne ikke lagre dokumentet. Prøv igjen',
                loadDocumentFailed: 'Kunne ikke laste. Prøv igjen',
                duplicateAlias: 'Dokumentet inneholder duplikatalias på: %s språk',
                loadRolesFailed: "Kunne ikke hente roller",
                noAccess: "Ingen tilgang til sideinformasjon"
            },
            allData:{
                name: 'Alle data',
                edit: 'Redigere',
                errorGettingData: 'Feil ved henting av alle data!',
                noData: 'Ingen ting',
                index: 'Index',
                id: 'Id',
                language: 'Språk',
                text:{
                    title:'Tekster',
                    access:'Adgang',
                    type:'Type'
                },
                menu:{
                    title:'Menyer',
                    countElements:'Antall ting'
                },
                category:{
                    title:'Kategorier',
                    visible:'Synlig'
                },
                loop:{
                    title:'Loops',
                    titleSingle:'Loop',
                    countElements:'Antall innlegg'
                },
                image:{
                    title:'Bilder',
                    allLanguages:'Alle språk',
                    path:'Filsøkväg'
                }
            }
        },
        modal: {
            doNotShowAgain: "Ikke vis igjen",
            title: "Bekrefte",
            editFile: "Rediger fil",
            editDirectory: "Rediger mappe",
            userProperties: 'Manager-brukeregenskaper',
            options: "Alternativer",
            yes: "Ja",
            no: "Nei",
            ok: "Ok",
            cancel: "Avbryt",
            save: "Lagre",
            create: "Skape",
        },
        editors: {
            document: {
                title: "Dokumentansvarlig",
                freeText: "Fri tekst",
                freeTextPlaceholder: "Skriv for å søke etter dokumenter",
                owner: "Eier",
                category: "Kategori",
                by: 'av',
                notShownInSelectedLang: 'Ikke vist på valgt språk',
                deleteInfo: 'Sletting av dokumenter ble fullført!',
                controls: {
                    edit: {
                        title: "Redigere"
                    },
                    question: 'Er du sikker på at du vil slette disse dokumentene? Dette kan ikke angres!',
                    question2: 'Du kan ikke tilbakestille dette dokumentet! Fjerne?',
                    multiRemoveInfo: 'Remove',
                    actions: 'Action',
                    removeAction: 'Delete',
                    putToBasketAction: 'Waste basket',
                    copy: {
                        title: "Kopiere",
                        confirmMessage: "Vil du virkelig kopiere (all informasjon i dokumentet vil bli kopiert) doc med id ",
                        action: 'Kopierer'
                    }
                },
                sort: {
                    id: "ID",
                    title: "Overskrift",
                    alias: "Alias",
                    modified: "Endret",
                    published: "Publ",
                    type: "Type",
                    status: "Status",
                    version: 'Ver'
                },
                id: {
                    tooltip: {
                        createdOn: 'Opprettet'
                    }
                },
                modified: {
                    tooltip: {
                        lastChangedOn: 'Dokumentet ble sist endret',
                    }
                },
                published: {
                    tooltip: {
                        publishedOn: 'Dokumentet ble publisert',
                    }
                },
                version: {
                    tooltip: {
                        hasNewerVersion: 'Dokumentet har en upublisert arbeidsversjon',
                        noWorkingVersion: 'Dokumentet har ingen fungerende versjon',
                    },
                },
                error: {
                    searchFailed: 'Ingen søkeresultater',
                    userLoadFailed: 'Kunne ikke hente bruker. Prøv igjen',
                    categoriesLoadFailed: 'Kunne ikke hente kategorier. Prøv igjen',
                    copyDocumentFailed: 'Dokumentet kunne ikke kopieres. Prøv igjen',
                    removeDocumentFailed: 'Kunne ikke slette dokumentet. Prøv igjen',
                    removeProtectedDocumentFailed: 'It is not allowed to delete documents',
                    putToWasteBasketFailed: 'Failed to put documents to the waste basket'
                }
            },
            newDocument: {
                title: "Opprett nytt dokument",
                textDoc: "Tekstdokument",
                fileDoc: "Lastet opp fil",
                urlDoc: "Link (URL)"
            },
            newDocumentProfile: {
                title: "Opprett nytt dokument",
                createDocButton: "Skape",
                chooseProfileOrParent: "Velg profil for det nye dokumentet:",
                validationErrorMessage: "Du må velge en eksisterende profil eller angi meta-IDen eller aliaset til et tekstdokument",
                buildByProfile: "Velg etter profil",
                buildByParent: "Velg en tekstdokument-ID",
                buildByCurrentDocId: "Velg dette dokumentet som profil",
                selectProfile: "Velg profil",
                selectParent: "Velg 'parent'",
                selectParentPlaceholder: "Skriv inn dokument-ID",
                error: {
                    loadProfilesFailed: "Kunne ikke hente kategorier. Prøv igjen"
                }
            },
            menu: {
                title: "Rediger menyen ",
                teaser: "Teaser-menyen ",
                page: "side ",
                menuTitle: "meny ",
                remove: "Fjern fra menyen",
                copy: "Kopiere",
                edit: "Rediger sideinformasjon",
                order: 'Rekkefølge',
                id: "ID",
                docTitle: "Overskrift",
                publishDate: 'Publ',
                publishedTimePrompt: 'Aldri publisert!',
                modifiedDate: 'Endret',
                status: "Status",
                version: 'Ver',
                removeConfirmation: "Vil du fjerne menypunktet \"",
                confirmFlatSortMessage: 'Hvis du bytter til en annen type, vil TreeSort gå tilbake til en flat meny! Vil du fortsette?',
                saveAndClose: "Lagre og lukk",
                sortNumberTitle: 'Sorter etter tall',
                visibility: {
                    name: 'Vis',
                    title: {
                        authorized: "A",
                        unauthorized: "U",
                        both: "Alle"
                    },
                    tooltip: {
                        authorized: "Elementet er synlig for autoriserte brukere",
                        unauthorized: "Elementet er synlig for uautoriserte brukere",
                        both: "Elementet er synlig for alle brukere",
                        nobody: "Elementet er ikke synlig for noen"
                    }
                },
                error: {
                    createFailed: 'Menyen kunne ikke lagres. Prøv igjen',
                    copyDocumentFailed: 'Dokumentet kunne ikke kopieres. Prøv igjen',
                    loadFailed: 'Kunne ikke laste. Prøv igjen',
                    invalidSortNumber: 'Ugyldig datasorteringsnummer!',
                    invalidPosition: 'Ugyldig stilling!',
                    fixInvalidPosition: 'Menyområdet har en ugyldig plassering! Vennligst fiks det!'

                },
                typesSort: {
                    treeSort: 'Tresortering',
                    manual: 'Manuell',
                    alphabeticalAsc: 'Alfabetiskt (A-Å)',
                    alphabeticalDesc: 'Alfabetiskt (Å-A)',
                    publishedDateAsc: 'Publisert (ny først)',
                    publishedDateDesc: 'Publisert (gammel først)',
                    modifiedDateAsc: 'Endret (ny først)',
                    modifiedDateDesc: 'Modifisert (gammel først)',

                },
                multiRemoveInfo: 'Fjernet',
                multiRemove: 'Fjerne',
            },
            loop: {
                title: "Editor loop",
                teaser:"Teaser meny ",
                page:"page ",
                loopTitle: "loop ",
                createNew: "Lag ny",
                saveAndClose: "Lagre og lukk",
                resetSorting: "Tilbakestill sorteringen",
                id: "ID",
                content: "innhold",
                image: "bilde",
                isEnabled: "er aktivert",
                error: {
                    createFailed: 'Kunne ikke opprette loop. Prøv igjen',
                    loadFailed: 'Kunne ikke laste. Prøv igjen'
                }
            },
            content: {
                title: "Bildearkiv",
                hideFolders: "Skjul mapper",
                showFolders: "Vis mapper",
                checkFolderImagesUsage: "Gå til katalogen",
                createFolderImage: "Legg til underkatalog",
                editFolderImage: "Endre katalognavn",
                deleteFolderImage: "Slett katalog",
                cancel: "Avbryt",
                upload: "Laste opp",
                useSelectedImage: "Bruk det valgte bildet",
                removeImageConfirm: "Vil du slette bildet ",
                imageStillUsed: "Bilde i bruk",
                removeFolderMessage: "Vil du slette mappen \"",
                folderNotEmptyMessage: "Mappen er ikke tom",
                newFolderName: "Nytt mappenavn",
                add: "LEGGE TIL+",
                cancelChanges: "Avbryt endringer",
                openImage: "Åpne bildet i nytt vindu",
                editMetadata: "Edit metadata",
                sortBy: 'Sorter etter',
                sorting: {
                    default: 'Standard',
                    az: 'A-Å',
                    za: 'Å-A',
                    dateNewFirst: 'Dato (ny først)',
                    dateOldFirst: 'Dato (gammel først)',
                },
                error: {
                    removeFailed: 'Kunne ikke slette. Prøv igjen',
                    checkFailed: 'Kunne ikke sjekke. Prøv igjen',
                    addFolderFailed: 'Kunne ikke legge til mappe. Prøv igjen',
                    loadImagesFailed: 'Kunne ikke hente bilder. Prøv igjen',
                    uploadImagesFailed: 'Kunne ikke laste opp bilder. Prøv igjen'
                },
            },
            image: {
                title: "Rediger bilde",
                page: 'side ',
                imageName: 'bilde ',
                teaser: 'Teaser-bilde',
                proportion: "Aktiver proporsjoner",
                compression: "Komprimering",
                presetCrop: "Forhåndsinnstilt beskjæringsformat",
                crop: "Forhåndsinnstilt",
                activeTitle: 'Aktivt bilde:',
                noSelectedImage: 'Ingen bilder er valgt!',
                editInNewWindow: 'Rediger i nytt vindu',
                panels: {
                    bottom: {
                        hide: "Skjul bunnpanelet",
                        show: "Vis det nederste panelet"
                    },
                    right: {
                        hide: "Skjul høyre panel",
                        show: "Vis høyre panel"
                    }
                },
                align: {
                    none: "Ingen justering",
                    center: "Fungerer kun hvis funksjonen er implementert av utvikleren. [Utviklerinfo: 'imcms-image-align-center'-klassen lagt til for vanlig imcms-bildetag]",
                    left: "Fungerer kun hvis funksjonen er implementert av utvikleren. [Utviklerinfo: 'imcms-image-align-left'-klassen lagt til for vanlig imcms-bildetag]",
                    right: "Fungerer kun hvis funksjonen er implementert av utvikleren. [Utviklerinfo: 'imcms-image-align-center'-klassen lagt til for vanlig imcms-bildetag]"
                },
                buttons: {
                    zoomIn: 'Zoom inn',
                    zoomOut: 'Zoom ut',
                    reset: 'Tilbakestill til opprinnelig verdi',
                    rotateLeft: 'Rotér mot venstre',
                    rotateRight: 'Roter til høyre',
                    revert: 'Gå tilbake til den opprinnelige forhåndsvisningen',
                    cropping: 'Beskjær bildet',
                    cancelText: "Avbryt",
                    cancelTitle: "Avbryt endringer",
                    applyChangeText: "Gjør om",
                    applyChangeTitle: "Bruk endringer",
                    removeCropping: "Fjern beskjæring",
                    removeCroppingDescription: "Gå tilbake til forrige beskjæring",
                    revertCropping: "Gå tilbake til originalen",
                    revertCroppingDescription: "Gå tilbake til det opprinnelige beskjæringsområdet",
                    rotationTitle: "Aktiver rotasjonskontroller"
                },
                styleInfo: {
                    title: 'Begrensede stiler',
                    info: 'Bildet som brukes er begrenset til denne stilen'
                },
                proportionsButtonTitle: "Bevar bildeproporsjoner Av/På",
                compressionButtonTitle: "Bildekomprimering På/Av",
                wantedSize: "Ønsket størrelse (B x H)",
                revertWantedSize: "Ønsket størrelse (H x B)",
                displaySize: "Vist størrelse (B x H)",
                revertDisplaySize: "Vist størrelse (H x B)",
                originSize: 'Original størrelse (B x H)',
                height: "Høyde",
                width: "Bredde",
                preview: "Forhåndsvisning",
                edit: 'Redigere',
                original: "Original",
                selectImage: "Velg bilde",
                suggestAltText: 'Komme med et forslag',
                warnChange: 'Alt-teksten er ikke tom! Vil du endre alt-tekst?',
                altText: "Alt. tekst (For synshemmede. Bør spesifiseres)",
                altTextRequired: "Alternativ tekst kreves!",
                imageLink: "Bildelink (URL)",
                allLangs: "Alle språk",
                advanced: "Avansert",
                simple: "Enkel",
                none: "Nei",
                top: "topp",
                right: "høyre",
                bottom: "bunn",
                left: "venstre",
                spaceAround: "Luft rundt bildet",
                cropCoords: "Beskjæringskoordinater",
                fileFormat: "Filformat",
                alignment: "Tekstjustering",
                altTextConfirm: "Alternativ tekst mangler. Vil du virkelig fortsette?",
                showHistory: "Vis historikk",
                hideHistory: "Skjul historie",
                cancelAndClose: "Avbryt og lukk",
                removeAndClose: "Fjern og lukk",
                saveAndClose: "Lagre og lukk",
                cancel: "Avbryt",
                restrictedStyle: 'Begrenset stil:',
                infoStyle: 'Bildet som brukes er begrenset til denne stilen.',
                zoomGrade: 'Zoom grad',
                exif: {
                    button: "Vis Exif info",
                    title: "Se dette bildet EXIF-informasjon",
                },
                error: {
                    loadFailed: 'Bildet kunne ikke lastes inn. Prøv igjen',
                    removeFailed: 'Kunne ikke slette bildet. Prøv igjen',
                    createFailed: 'Kunne ikke lage bildet. Prøv igjen',
                    loadHistoryFailed: 'Kunne ikke laste inn loggen'
                }
            },
            imageMetadata: {
                titleMetadataEditor: 'Image Metadata',
                titleExifInfo: 'Exif Info',
                save: 'Save',
                cancel: 'Cancel',
                photographer: 'Photographer',
                resolution: 'Resolution (w x h)',
                originalFileSize: 'Original size',
                originalFileType: 'Original file type',
                uploadedBy: 'Uploaded by',
                modifiedDate: 'Modified date',
                copyright: 'Copyright',
                licensePeriod: 'License period',
                titleAllExifMode: 'All Exif Information',
                titleCustomExifMode: 'Custom Metadata',
                customExifButton: 'Switch to custom metadata',
                allExifButton: 'Switch to all exif',
                error: {
                    saveMetadataFailed: 'Failed to save metadata'
                }
            },
            text: {
                confirmSave: "Lagre endringer?",
                error: {
                    createFailed: 'Kunne ikke lagre tekst. Prøv igjen',
                    filterFailed: 'Kan ikke filtrere tekst. Prøv igjen',
                }
            }
        },
        status: {
            title: {
                published: "Publisert",
                publishedWaiting: "Godkjent, venter",
                inProcess: "Ny, u a",
                disapproved: "Ikke godkjent",
                archived: "Arkivert",
                passed: "Fullført",
                wasteBasket: "W basket"
            },
            tooltip: {
                published: "Dokumentet er publisert",
                publishedWaiting: "Godkjent, venter",
                inProcess: "Dokumentet er nytt og under konstruksjon",
                disapproved: "Ikke godkjent",
                archived: "Arkivert",
                passed: "Publication end",
                wasteBasket: "The document in the waste basket"
            },
        },
        languageFlags: {
            alertInfoLanguage: 'Språket vil gjelde etter ny pålogging (lang:',
            error: {
                loadFailed: 'Kunne ikke laste inn språket. Prøv igjen'
            }
        },
        userProperties: {
            key: 'Nøkkel',
            value: 'Verdi',
            add: 'Legge til',
            successDelete: 'Vellykket slettet!',
            wrongKeyError: 'Nøkkelen finnes, vennligst skriv en annen nøkkel!',
            emptyValueError: 'Nøkkelen og verdien må ikke være tomme!',
            errorMessage: 'Noe gikk galt',
            deleteConfirm: 'Ønsker du å fjerne eiendom?',
            updateMessage: 'Oppdater eiendommen suksess!',
            savedSuccess: 'Egenskapers suksess reddet!',
            save: 'Save'
        },
        textHistory: {
            title: "Teksthistorikk",
            cancel: "Avbryt",
            writeToText: "Skriv til tekstfeltet",
            viewSource: "Vis kilde",
            viewText: "Vis tekst",
            error: {
                loadFailed: 'Kunne ikke hente loggen. Prøv igjen'
            }
        },
        textValidation: {
            title: "Validering ved W3C",
            pluginTitle: "Valider innhold over W3C",
            ok: "OK",
            output: "Valideringsresultater: ",
            errors: " Feil",
            warnings: " Advarsler",
            error: {
                validationFailed: 'Kunne ikke validere. Prøv igjen'
            }
        },
        sessionExpiredMessage: "Du har blitt logget ut på grunn av inaktivitet. Omdirigerer til påloggingssiden?",
        contentSaveWarningMessage: "Økten din utløper om 2 min. Vennligst lagre det nødvendige innholdet. Økten fortsetter etter at du har lagret.",
        superAdmin: {
            head: 'SuperAdmin',
            oldInterface: 'Eldre grensesnitt',
            documentation: 'Detaljer om bruk',
            documentationLink: "user-documentation/admin-settings/index.html",
            users: {
                documentationLink: "user-documentation/admin-settings/users.html",
                name: 'Bruker',
                searchFilter: {
                    byName: {
                        text: 'Søk bruker',
                        placeholder: 'Tomt felt viser alt'
                    },
                    byRole: {
                        title: 'Bruker med rolle'
                    },
                    inactiveUsers: 'Inkluder inaktive brukere',
                    listUsers: 'List brukere'
                },
                searchResult: {
                    title: ' brukerne ble funnet',
                    id: 'ID',
                    firstName: 'Fornavn',
                    lastName: 'Etternavn',
                    userName: 'Brukernavn',
                    email: 'E-post',
                    archived: 'er arkivert'
                },
                createNewUser: 'Opprett ny bruker',
                error: {
                    loadRolesFailed: 'Kunne ikke hente roller. Prøv igjen',
                    updateFailed: 'Kunne ikke oppdatere brukeren. Prøv igjen',
                    searchFailed: 'Kunne ikke søke etter bruker. Prøv igjen',
                    invalidMobilePhoneNumber: 'Ugyldig mobilnummer!'
                },
                tooltip: {
                    archiveUser: 'For å arkivere brukere',
                    editUser: 'Redigere',
                    mobilePhoneNumberTip: 'Mobilnummer må inneholde + og landskode!'
                },
            },
            roles: {
                documentationLink: "user-documentation/admin-settings/roles.html",
                name: 'Roller',
                title: 'Roller: administrasjon',
                createNewRole: 'Opprett ny rolle',
                roleName: 'Rollenavn',
                save: 'Lagre',
                documentEditor: 'Dokumentredigering',
                cancel: 'Avbryt',
                externalRolesInfo: 'Du kan angi eksterne roller til å fungere som lokale',
                permissions: {
                    title: 'Rolletillatelser',
                    getPasswordByEmail: 'Motta passord på e-post',
                    accessToAdminPages: 'Tilgang til admin sider',
                    accessToDocumentEditor: 'Tilgang til dokumentredigering',
                    publishOwnDocuments: 'Publiser egne opprettede dokumenter',
                    publishAllDocuments: 'Publiser alle dokumenter (kun med EDIT-tillatelse)'
                },
                deleteConfirm: 'Er du sikker på at du vil slette denne rollen?',
                editRole: 'Rediger rollen',
                deleteRole: 'Slett rollen',
                saveChanges: 'Lagre endringer',
                discardChangesMessage: 'Ignorer endringer?',
                error: {
                    loadFailed: 'Kunne ikke hente roller. Prøv igjen',
                    loadProvidersFailed: 'Kunne ikke godkjenne leverandører. Prøv igjen',
                    removeFailed: 'Kunne ikke slette rollen. Prøv igjen',
                    updateFailed: 'Kunne ikke oppdatere rollen. Prøv igjen',
                    createFailed: 'Kunne ikke opprette rolle. Prøv igjen',
                    externalRoles: {
                        loadFailed: 'Kunne ikke hente eksterne roller. Prøv igjen',
                        updateFailed: 'Kunne ikke oppdatere ekstern rolle. Prøv igjen'
                    },
                    azureRoles: {
                        loadFailed: 'Kunne ikke hente eksterne roller. Prøv igjen'
                    }
                }
            },
            ipAccess: {
                documentationLink: "user-documentation/admin-settings/ip-access.html",
                name: 'IP-tilgang',
                title: 'IP-tilgang',
                createNewRule: 'Ny',
                editRule: 'Redigere',
                deleteRule: 'Slett',
                save: 'Lagre',
                cancel: 'Avbryt',
                deleteConfirm: 'Er du sikker på at du vil slette denne regelen?',
                saveChanges: 'Lagre endringer',
                discardChangesMessage: 'Ignorer endringer?',
                fromIp: 'Fra IP-nummer',
                tillIp: 'Til IP-nummer',
                fields: {
                    enabled: 'Aktivert',
                    restricted: 'Begrenset',
                    ipRange: 'IP-område',
                    role: 'Rolle',
                    user: 'Bruker'
                },
                error: {
                    loadFailed: 'Kunne ikke hente regler. Prøv igjen',
                    removeFailed: 'Kunne ikke slette regelen. Prøv igjen',
                    updateFailed: 'Kunne ikke oppdatere regelen. Prøv igjen',
                    createFailed: 'Kunne ikke opprette regelen. Prøv igjen',
                    loadRolesFailed: 'Kunne ikke hente roller. Prøv igjen',
                    loadUsersFailed: 'Kunne ikke hente bruker. Prøv igjen',
                    invalidIP: 'Ugyldig ipv4 / ipv6-adresse'
                }
            },
            versionControl: {
                documentationLink: "user-documentation/admin-settings/version-control.html",
                name: 'Version Control',
                versionId: 'Version ID',
                login: 'User Login',
                publicationDate: 'Publication Date',
                reviewButton: 'Review',
                resetButton: 'Reset',
                input: 'Find versions by document ID',
                findButton: 'Find',
                resetSuccess: 'Document version has been reset to ',
                error: {
                    findFailed: 'Failed to fetch document versions',
                    resetFailed: 'Failed to reset document version '
                }
            },
            deleteDocs: {
                documentationLink: "user-documentation/admin-settings/delete-documents.html",
                name: 'Slett dokumenter',
                title: 'Slett dokument med id',
                deleteConfirmation: 'ADVARSEL: Dokumentet og dets data vil bli slettet. Er du sikker?',
                deleteDocButton: 'Slett!',
                error: {
                    missedDoc: 'Dokumentet finnes ikke!',
                    protectedDoc: 'Det er ikke tillatt å slette dette dokumentet',
                    removeDocumentFailed: 'Kunne ikke slette dokumentet',
                    removeProtectedDocumentFailed: 'Det er ikke tillatt å slette dokumenter'
                },
                basket: {
                    title: 'Waste Basket',
                    metaId: 'Id',
                    headline: 'Headline',
                    addedDate: 'Added date',
                    userLogin: 'Username',
                    input: 'Put document to the waste basket by id',
                    putButton: "Put",
                    restoreButton: 'Restore',
                    deleteButton: 'Delete',
                    error: {
                        readFailed: 'Failed to fetch documents from the waste basket',
                        putFailed: 'Failed to put the document',
                        restoreFailed: 'Failed to restore the document from the waste basket'
                    }
                }
            },
            dataVersion: {
                name: 'Dataversjon',
                error: {
                    loadVersion: 'Kunne ikke laste inn versjonen!'
                }
            },
            documentsImport: {
                documentationLink: "user-documentation/admin-settings/import.html",
                name: "Import Documents",
                selectionWindowContainer:{
                    aliasControlCard:"Control alias",
                    editDocumentsCard:"Edit documents",
                    uploadDocumentsCard:"Upload documents"
                },
                controlAliasSection:{
                    removeAliases:"Remove aliases",
                    replaceAliases:"Replace aliases",
                    replaceFail:"Failed to replace aliases",
                    removeFail:"Failed to remove aliases"
                },
                importSection:{
                    importButton:"Import documents",
                    importFail:"Failed to import documents!"
                },
                importEntityReferenceSection:{
                    roleReferences:"Role references",
                    templateReferences:"Template references",
                    categoryReferences:"Category references",
                    categoryTypeReferences:"Category type references",
                    roleReferenceTitle:"role",
                    templateReferenceTitle:"template",
                    categoryReferenceTitle:"category type",
                    categoryTypeReferenceTitle:"category type",
                    roleReferenceTitlePlural:"roles",
                    templateReferenceTitlePlural:"template",
                    categoryReferenceTitlePlural:"categories",
                    categoryTypeReferenceTitlePlural:"category types",
                    success:"Saved...",
                    fail:"Failed...",
                },
                uploadDocumentsSection:{
                    uploadButton:"Upload documents",
                    uploadFail: "Failed to upload!",
                },
                importDocumentListBuilder:{
                    titles:{
                        id:"Import document ID",
                        metaId:"Meta ID",
                        status:"Status"
                    }
                },
                controls:{
                    previous:"<-- Previous",
                    next:"Next -->",
                    switchInputType:"Change input type",
                    rangeInput:{
                        name:"Provide start and end doc id(rb4)",
                        startId:"Start ID",
                        endId:"End ID",
                        warning:"Provide range!"
                    },
                    listInput:{
                        name:"Provide documents doc id(rb4)",
                        docIdInput:"Enter doc id(rb4)",
                        removeButton:{
                            name:"Remove",
                            warning:"Select option/s first"
                        },
                        addButton:{
                            name:"Add",
                            warning:"Input only doc id"
                        }
                    },
                    listButton:{
                        name:"List documents",
                        fail:"Failed to list documents!",
                    },
                    filter:{
                        name:"Filter",
                        excludeImported:"Exclude imported",
                        excludeSkipped:"Exclude skipped"
                    }
                }
            },
            files: {
                documentationLink: "user-documentation/admin-settings/files.html",
                name: 'Filer',
                upload: 'Last opp',
                rename: 'Endre navn',
                add: 'Legg til',
                moveRight: 'Flytt til høyre',
                moveLeft: 'Gå til venstre',
                copyRight: 'Kopier til høyre',
                copyLeft: 'Kopier til venstre',
                overwrite:'Overskriv',
                cancel: 'Avbryt',
                defaultRename: 'Standard endre navn',
                chooseFilename:'Velg filnavn',
                warnDeleteMessage: 'Er du sikker på at du vil slette filen?',
                warnEditMessage: 'Redigerer du virkelig innholdsfilen?',
                warnViewDocMessage: 'Vil du omdirigere på gjeldende dokument?',
                title: {
                    createFile: 'Opprett fil',
                    createFileOrDirectory: 'Opprett fil/katalog',
                    createFileName: 'Filnavn',
                    createFileOrDirectoryName: 'Fil-/katalognavn',
                    createDirectory: 'Katalog',
                    fileName: 'Filnavn',
                    directoryName: 'Katalognavn',
                    delete: 'Slett',
                    move: 'Flytt',
                    copy: 'Kopiere',
                    download: 'Nedlasting',
                    edit: 'Redigere',
                    addToGroup: 'Legg til i gruppe',
                    titleByMove: 'Flytt fil',
                    titleByCopy: 'Kopier fil',
                    titleContent: 'Filinnhold',
                    titleEditContent: 'Rediger filinnhold!',
                    replaceTemplate: 'Velg mal',
                    filename:"Filnavn: ",
                    newFilename:'Nytt filnavn',
                    selectTitle: 'Gjeldende/eksisterende fil',
                    current: 'NÅVÆRENDE',
                    existing:'EKSISTERENDE',
                    chooseAction:'Velg hva du skal gjøre med: ',
                },
                error: {
                    loadError: 'Kunne ikke laste inn filer. Prøv igjen!',
                    loadFileError: 'Kunne ikke laste inn filen. Prøv igjen!',
                    deleteFailed: 'Kunne ikke slette!',
                    renameFailed: 'Kunne ikke gi nytt navn til filen. Prøv igjen!',
                    editFailed: 'Kunne ikke redigere filinnholdet. Prøv igjen!',
                    createError: 'Mislykkes i å opprette fil!',
                    downloadError: 'Kunne ikke opprette nedlastingsfil. Prøv igjen!',
                    moveError: 'Kunne ikke flytte filen. Prøv igjen!',
                    copyError: 'Kunne ikke kopiere filen. Prøv igjen!',
                    uploadError: 'Filen kunne ikke lastes opp. Prøv igjen!',
                    loadDocError: 'Kunne ikke laste dokumentet!',
                    loadGroupsError: 'Kunne ikke laste inn malgrupper!',
                    deleteGroup: 'Kunne ikke slette malgruppen!',
                    createGroup: 'Kunne ikke opprette malgruppe!',
                    editGroup: 'Kunne ikke redigere malgruppen!',
                    loadGroup: 'Kunne ikke bruke malgruppen!',
                    addTemplateToGroup: 'Kunne ikke legge til mal i malgruppe!',
                    deleteTemplate: 'Kunne ikke slette malen!',
                    deleteGroupFromTemplate: 'Kunne ikke slette gruppen fra malen!',
                    replaceTemplate: 'Kunne ikke erstatte malen!',
                    noOtherTemplates: 'Ingen andre maler!',
                    loadTemplates: 'Kunne ikke laste inn maler!',
                    fileAlreadyExists:'Filen eksisterer allerede med et slikt navn!',
                    duplicateFiles: 'Det er filer med dupliserte navn!',
                    onlyFilesSupported:'Kun filer støttes!'
                },
                documentData: {
                    docsNumber: 'Antall dokumenter: ',
                    docView: 'VIEW',
                    docEdit: 'EDIT',
                },
                groupData: {
                    title: 'Malgruppe',
                    templatesTableTitle: 'Maler i gruppen:',
                    edit: 'Redigere',
                    delete: 'Slett',
                    save: 'Lagre',
                    cancel: 'Avbryt',
                    create: 'Opprett malgruppe',
                    deleteConfirm: 'Er du sikker på at du vil slette malgruppe?',
                    saveConfirm: 'Er du sikker på at du vil lagre malgruppen?',
                    cancelConfirm: 'Er du sikker på at du vil avbryte endringer?',
                    addToGroupConfirm: 'Er du sikker på at du vil legge til denne malen i den gjeldende malgruppen?'
                },
                template: {
                    boundDocumentsWarn: 'Noen dokumenter relatert til denne malen. Vil du erstatte den med en annen mal?',
                }
            },
            templatesCSS: {
                documentationLink: "user-documentation/admin-settings/template-css.html",
                name: 'Maler CSS',
                editorTitle: 'Mal CSS Editor',
                templatesSelectTitle: 'Velg mal',
                history: {
                    headText: "Maler CSS-historikk",
                    closeBtnText: "Avbryt",
                    useBtnText: "Bruk",
                },
                buttons: {
                    historyBtnText: "Historie",
                    activeVersionBtnText: "Aktiv",
                    workingVersionBtnText: "Arbeider",
                    saveBtnText: "Lagre",
                    clearBtnText: "Klar",
                    publishBtnText: "Publisere",
                },
                errors: {
                    EMPTY_AREA: "Velg mal!",
                    ACTIVE_VERSION: "For å redigere bytt til fungerende versjon!",
                    SAVE_FIRST: "For å publisere, vennligst lagre det først!",
                    EQUALS_WORKING_VERSION:'Gjør endringer først!'
                }
            },
            search: {
                name: 'Søk'
            },
            linkValidator: {
                documentationLink: "user-documentation/admin-settings/link-validator.html",
                name: 'Link Validator',
                titleOnlyBrokenLinks: "Vis kun ødelagte lenker",
                brokenLinks: "Vis kun ødelagte lenker",
                buttonValidation: 'Start validering',
                startDocumentId: 'Start id',
                endDocumentId: 'Slutt id',
                linkInfoRow: {
                    pageAlias: 'Sidealias',
                    status: "Status",
                    type: 'Type',
                    admin: "Administrasjon",
                    reference: "Ref.",
                    link: "Link",
                    hostFound: "Vert funnet",
                    hostReachable: "Verdier kan nås",
                    pageFound: 'Siden ble funnet'
                },
                error: {
                    validationFailed: 'Kunne ikke validere. Prøv igjen'
                },
            },
            imagesTab: {
                documentationLink: "user-documentation/editors/image/image-library.html",
                title: 'Bilder',
                label: 'Link til bildebibliotek'
            },
            categories: {
                documentationLink: "user-documentation/admin-settings/categories.html",
                name: 'Kategorier',
                createButtonName: "Skape",
                removeButtonName: "Slett",
                saveButton: 'Lagre',
                cancelButton: 'Avbryt',
                warnCancelMessage: 'Er du sikker på at du vil avbryte dette?',
                confirmDelete: 'Slett?',
                sections: {
                    createCategoryType: {
                        title: 'Opprett kategoritype',
                        name: 'Kategoritypenavn',
                        inherited: 'Arvet til nye dokumenter',
                        visible: 'Synlig i sideinfo',
                        singleSelect: 'Singlevalg',
                        multiSelect: 'Multivalg',
                        chooseType: 'Velg kategoritype',
                    },
                    createCategory: {
                        title: 'Opprett kategori',
                        name: 'Kategori navn',
                        description: 'Beskrivelse',
                        categoryType: 'Legg til kategoritype',
                        chooseCategory: 'Velg kategori',
                    }
                },
                error: {
                    invalidName: 'Tomt navn er ikke lov! Vennligst velg et annet navn!',
                    category: {
                        loadFailed: 'Kunne ikke hente kategori. Prøv igjen',
                        removeFailed: 'Denne kategorien brukes i dokumenter! Er du sikker på at du vil slette den?',
                        removeForceFailed: 'Kunne ikke fjerne kategorien'
                    },
                    categoryType: {
                        loadFailed: 'Kunne ikke hente kategoriene. Prøv igjen',
                        removeFailed: 'Denne kategoritypen har kategorier! Er du sikker på at du vil fjerne den?',
                        removeForceFailed: 'Kunne ikke fjerne kategoritypen'
                    }
                }
            },
            profiles: {
                documentationLink: "user-documentation/admin-settings/profiles.html",
                name: 'Profiler',
                title: 'Profiler',
                createButton: "Opprett ny profil",
                warnChangeMessage: 'Vil du endre denne profilen?',
                warnDelete: 'Sletter du virkelig denne profilen?',
                warnCancelMessage: 'Er du sikker på at du vil avbryte?',
                cancel: "Avbryt",
                titleTextName: 'Navn',
                titleTextDocName: 'Dokumentnavn',
                createNewProfile: {
                    titleTextName: 'Navn',
                    titleTextDocName: 'Dokumentnavn',
                    textFieldName: 'Tekstnavn',
                    textFieldDocName: 'Tekst doc navn',
                    buttonSave: 'Save'
                },
                editProfile: {
                    name: "Navn",
                    docName: 'Dokumentnavn',
                    buttonEdit: 'Rediger profil',
                    buttonDelete: 'Slett profil'
                },
                error: {
                    createFailed: 'Kunne ikke opprette profil. Prøv igjen',
                    loadFailed: 'Kunne ikke hente profiler. Prøv igjen',
                    errorMessage: 'Beklager, men du tok feil!'
                }
            },
            systemProperties: {
                documentationLink: "user-documentation/admin-settings/system-properties.html",
                name: 'System egenskaper',
                nameInputTitle: 'Navn',
                emailInputTitle: 'E-post',
                sections: {
                    startPage: {
                        name: "Hjemmeside",
                        description: "Side nummer",
                        input: "Inndatasideside"
                    },
                    systemMessage: {
                        name: "Systemmelding",
                        description: "Beskjed",
                        inputBox: "Box Beskjed"
                    },
                    serverMaster: {
                        name: "Servermester",
                        descriptionName: "Servermester navn",
                        descriptionEmail: "Servermester-e-post",
                        descriptionByName: "Servermester Navnebeskrivelse",
                        descriptionByEmail: "Servermester E-postbeskrivelse",
                        inputName: "Skriver inn servermasternavn",
                        inputEmail: "Skriver inn servermaster e-post"
                    },
                    webMaster: {
                        name: "WebMaster",
                        descriptionName: "Webmaster navn",
                        descriptionEmail: "Webmaster e-post",
                        descriptionByName: "Webmasters navnebeskrivelse",
                        descriptionByEmail: "Webmaster E-postbeskrivelse",
                        inputName: "Skriver inn webbnavn",
                        inputEmail: "Webmaster e-postbeskrivelse"
                    },
                    error: {
                        incorrectEmail: "Ukorrekt email"
                    }
                },
                changeButton: "Lagre",
                error: {
                    loadFailed: 'Kunne ikke laste inn egenskaper. Prøv igjen'
                }
            },
            temporalContent: {
                documentationLink: "user-documentation/admin-settings/index-cache.html",
                name: 'Index / Cache',
                actions: {
                    rebuildIndex: 'Reindekser innholdet',
                    deletePublicDocCache: 'Tøm offentlig dokumentcache',
                    deleteOtherCache: 'Tøm annen cache',
                    deleteStaticCache: 'Tøm statisk cache',
                    buildCacheDocument: 'Bygg opp cache'
                },
                warning: {
                    buildCacheWarning: 'Er du sikker på å bygge cache?'
                },
                init: 'Henrette',
                initIndexing: 'Indekser',
                indexing: 'Indeksering',
                lastUpdate: 'Indeksert',
                timeLeft: 'Tid igjen',
                lastDeleteCache: 'Slettet',
                caching: 'Caching',
                initCaching: 'Bygge caches',
                lastBuildCache: "Opprettet"
            },
            menuTab: {
                name: 'Meny'
            },
            documentationTab: {
                title: 'Dokumentasjon',
                label: 'Link til dokumentasjon siste versjon '
            }
        },
        login: {
            alternativeLogin: "Alternativ pålogging:",
            error: {
                loadProvidersFailed: 'Kunne ikke godkjenne leverandører. Prøv igjen'
            }
        },
        dateTime: {
            yearMonthDay: 'åååå-mm-dd',
            hourMinute: 'tt:mm',
        },
        windowTabs: {
            advancedButton: 'Advanced menu'
        },
        save: "Lagre",
        none: 'Ingen'
    },
};

module.exports = texts[imcms.userLanguage] ? texts[imcms.userLanguage] : texts['en'];
