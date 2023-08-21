/**
 * Tab in Page Info window for File Document type.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.01.18
 */
define("imcms-file-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-document-types", "jquery", "imcms-i18n-texts",
        "imcms-page-info-tab"
    ],
    function (BEM, components, docTypes, $, texts, PageInfoTab) {

        texts = texts.pageInfo.file;

        let $filesListContainerBody, $fileInput;

        function buildFilesContainerBody() {
            return $filesListContainerBody = $("<div>", {"class": "files-container-body"});
        }

        function buildFilesContainerHead() {
            const filesContainerHeadBEM = new BEM({
                block: "files-container-head",
                elements: {
                    "title": "imcms-title"
                }
            });

            const $idTitle = filesContainerHeadBEM.buildElement("title", "<div>", {text: texts.id});
            const $nameTitle = filesContainerHeadBEM.buildElement("title", "<div>", {text: texts.fileName});
            const $isDefaultFileTitle = filesContainerHeadBEM.buildElement("title", "<div>", {text: texts.isDefault});

            return filesContainerHeadBEM.buildBlock("<div>", [
                {"id": $idTitle},
                {"name": $nameTitle},
                {"default": $isDefaultFileTitle}
            ]);
        }

        function buildFileRow(file) {
            const $isDefaultFileRadioBtn = components.radios.imcmsRadio("<div>", {
                name: "isDefaultFile",
                value: file.name,
                checked: file.defaultFile
            });

            let $row;

            return ($row = new BEM({
                block: "file-row",
                elements: {
                    "id": components.texts.textInput({value: file.fileId}),
                    "name": components.texts.infoText("<div>", file.filename),
                    "default": $isDefaultFileRadioBtn,
                    "delete": components.controls.remove(() => {
                        $row.remove();

                        let files;
                        if($row.find('input:radio').is(':checked') && (files = $filesListContainerBody.children()).length > 0){
                            $(files[0]).find('input:radio').prop('checked', true);
                        }
                    })
                }
            }).buildBlockStructure("<div>", {
                "class": "files-container-body__file-row",
                "data-file-id": file.id,
                "data-file-type": file.mimeType
            }));
        }

        function buildFilesRow(files) {
            files = files || [];

            const emptyOrHasDefault = files.length === 0 || files.find(file => file.defaultFile === true);

            if ($filesListContainerBody.children().length === 0 && !emptyOrHasDefault) {
                files[0].defaultFile = true;
            }

            return files.map(buildFileRow);
        }

        function appendFiles(files) {
            $filesListContainerBody.append(buildFilesRow(files));
        }

        function transformFileToDTO(file) {
            return {
                id: null,
                fileId: "",
                filename: file.name,
                mimeType: file.type,
                defaultFile: false
            }
        }

        function transformFilesToDTO(files) {
            return Array.prototype.slice.call(files || []).map(transformFileToDTO);
        }

        function getFileObjects(docId) {
            return $filesListContainerBody.children()
                .toArray()
                .map(fileRowDOM => {
                    const $fileRow = $(fileRowDOM);

                    return {
                        id: $fileRow.data("fileId"),
                        docId: docId,
                        fileId: $fileRow.find(".file-row__id").val(),
                        filename: $fileRow.find(".file-row__name").text(),
                        mimeType: $fileRow.data("fileType"),
                        defaultFile: $fileRow.find("input[name=isDefaultFile]").is(":checked")
                    }
                });
        }

        let tabData = {};

        const FilesTab = function (name, docType) {
            PageInfoTab.apply(this, arguments);
        };

        FilesTab.prototype = Object.create(PageInfoTab.prototype);

        FilesTab.prototype.tabElementsFactory = function () {
            $fileInput = $("<input>", {
                type: "file",
                style: "display: none;",
                multiple: "",
                change: function () {
                    tabData.formData = tabData.formData || new FormData();

                    for (let i = 0; i < this.files.length; i++) {
                        tabData.formData.append('files', this.files[i]);
                    }

                    appendFiles(transformFilesToDTO(this.files));
                }
            });

            const $uploadButtonContainer = $("<div>", {"class": "imcms-field"});

            const $uploadNewFilesButton = components.buttons.positiveButton({
                text: texts.upload,
                click: () => {
                    $fileInput.click();
                }
            });

            $uploadButtonContainer.append($fileInput, $uploadNewFilesButton);

            const $filesListContainer = new BEM({
                block: "files-container",
                elements: {
                    "head": buildFilesContainerHead(),
                    "body": buildFilesContainerBody()
                }
            }).buildBlockStructure("<div>");

            return [
                $uploadButtonContainer,
                $filesListContainer
            ];
        };
        FilesTab.prototype.fillTabDataFromDocument = document => {
            appendFiles(document.files);
        };
        FilesTab.prototype.saveData = function (document) {
            if (!this.isDocumentTypeSupported(document.type)) {
                return document;
            }

            document.files = getFileObjects(document.id);
            document.newFiles = tabData.formData;
            return document;
        };
        FilesTab.prototype.clearTabData = () => {
            $filesListContainerBody.empty();
            $fileInput.val('');
            tabData = {};
        };

        FilesTab.prototype.getDocLink = () => texts.documentationLink;

        return new FilesTab(texts.name, docTypes.FILE);
    }
);
