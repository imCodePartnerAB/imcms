/**
 * Tab in Page Info window for File Document type.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.01.18
 */
Imcms.define("imcms-file-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-document-types", "jquery", "imcms-i18n-texts",
        "imcms-page-info-tab"
    ],
    function (BEM, components, docTypes, $, texts, PageInfoTab) {

        texts = texts.pageInfo.file;

        var $filesListContainerBody, $fileInput;

        function buildFilesContainerBody() {
            return $filesListContainerBody = $("<div>", {"class": "files-container-body"});
        }

        function buildFilesContainerHead() {
            var filesContainerHeadBEM = new BEM({
                block: "files-container-head",
                elements: {
                    "title": "imcms-title"
                }
            });

            var $idTitle = filesContainerHeadBEM.buildElement("title", "<div>", {text: texts.id});
            var $nameTitle = filesContainerHeadBEM.buildElement("title", "<div>", {text: texts.fileName});
            var $isDefaultFileTitle = filesContainerHeadBEM.buildElement("title", "<div>", {text: texts.isDefault});

            return filesContainerHeadBEM.buildBlock("<div>", [
                {"title": $idTitle},
                {"title": $nameTitle},
                {"title": $isDefaultFileTitle}
            ]);
        }

        function buildFileRow(file) {
            var $isDefaultFileRadioBtn = components.radios.imcmsRadio("<div>", {
                name: "isDefaultFile",
                value: file.name,
                checked: file.defaultFile
            });

            var $row;

            return ($row = new BEM({
                block: "file-row",
                elements: {
                    "id": components.texts.textInput({value: file.fileId}),
                    "name": $("<div>", {text: file.filename}),
                    "default": $isDefaultFileRadioBtn,
                    "delete": components.controls.remove(function () {
                        $row.remove();
                    })
                }
            }).buildBlockStructure("<div>", {
                "class": "files-container-body__file-row",
                "data-file-id": file.id,
                "data-file-type": file.mimeType
            }));
        }

        function buildFilesRow(files) {
            return (files || []).map(buildFileRow);
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
                .map(function (fileRowDOM) {
                    var $fileRow = $(fileRowDOM);

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

        var tabData = {};

        var FilesTab = function (name, docType) {
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

                    for (var i = 0; i < this.files.length; i++) {
                        tabData.formData.append('files', this.files[i]);
                    }

                    appendFiles(transformFilesToDTO(this.files));
                }
            });

            var $uploadButtonContainer = $("<div>", {"class": "imcms-field"});

            var $uploadNewFilesButton = components.buttons.positiveButton({
                text: texts.upload,
                click: function () {
                    $fileInput.click();
                }
            });

            $uploadButtonContainer.append($fileInput, $uploadNewFilesButton);

            var $filesListContainer = new BEM({
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
        FilesTab.prototype.fillTabDataFromDocument = function (document) {
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
        FilesTab.prototype.clearTabData = function () {
            $filesListContainerBody.empty();
            $fileInput.val('');
            tabData = {};
        };

        return new FilesTab(texts.name, docTypes.FILE);
    }
);
