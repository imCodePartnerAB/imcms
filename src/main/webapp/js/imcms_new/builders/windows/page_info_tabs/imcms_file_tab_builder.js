/**
 * Tab in Page Info window for File Document type.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.01.18
 */
Imcms.define("imcms-file-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-document-types", "imcms-page-info-tab-form-builder",
        "imcms-controls-builder", "jquery"
    ],
    function (BEM, components, docTypes, tabContentBuilder, controls, $) {

        var $filesContainerBody;

        function buildFilesContainerBody() {
            return $filesContainerBody = $("<div>", {"class": "files-container-body"});
        }

        function buildFilesContainerHead() {
            var filesContainerHeadBEM = new BEM({
                block: "files-container-head",
                elements: {
                    "title": "imcms-title"
                }
            });

            var $idTitle = filesContainerHeadBEM.buildElement("title", "<div>", {text: "ID"});
            var $nameTitle = filesContainerHeadBEM.buildElement("title", "<div>", {text: "Name"});
            var $isDefaultFileTitle = filesContainerHeadBEM.buildElement("title", "<div>", {text: "Default"});

            return filesContainerHeadBEM.buildBlock("<div>", [
                {"title": $idTitle},
                {"title": $nameTitle},
                {"title": $isDefaultFileTitle}
            ]);
        }

        function buildFileRow(file) {
            var $isDefaultFileRadioBtn = components.radios.imcmsRadio("<div>", {
                name: "isDefaultFile",
                value: file.name
            });

            var $row;

            return ($row = new BEM({
                block: "file-row",
                elements: {
                    "id": components.texts.textInput({value: file.fileId}),
                    "name": $("<div>", {text: file.filename}),
                    "default": $isDefaultFileRadioBtn,
                    "delete": controls.remove(function () {
                        $row.detach();

                        if (!tabData.formData) {

                        }

                        // tabData.formData.
                    })
                }
            }).buildBlockStructure("<div>", {"class": "files-container-body__file-row"}));
        }

        function buildFilesRow(files) {
            return (files || []).map(buildFileRow);
        }

        function appendFiles(files) {
            $filesContainerBody.append(buildFilesRow(files));
        }

        function transformFileToDTO(file) {
            return {
                fileId: "",
                filename: file.name,
                mimeType: file.type,
                defaultFile: false
            }
        }

        function transformFilesToDTO(files) {
            return Array.prototype.slice.call(files || []).map(transformFileToDTO);
        }

        var tabData = {};

        return {
            name: "files",
            tabIndex: null,
            isDocumentTypeSupported: function (docType) {
                return docType === docTypes.FILE;
            },
            showTab: function () {
                tabContentBuilder.showTab(this.tabIndex);
            },
            hideTab: function () {
                tabContentBuilder.hideTab(this.tabIndex);
            },
            buildTab: function (index) {
                this.tabIndex = index;

                var $fileInput = $("<input>", {
                    type: "file",
                    style: "display: none;",
                    multiple: "",
                    change: function () {
                        tabData.formData = tabData.formData || new FormData();

                        for (var i = 0; i < this.files.length; i++) {
                            tabData.formData.append('files', this.files[i]);
                        }

                        console.log(this.files);
                        console.log(tabData.formData.getAll("files"));
                        appendFiles(transformFilesToDTO(this.files));
                    }
                });

                var $uploadButtonContainer = $("<div>", {"class": "imcms-field"});

                var $uploadNewFilesButton = components.buttons.positiveButton({
                    text: "Upload",
                    click: function () {
                        $fileInput.click();
                    }
                });

                $uploadButtonContainer.append($fileInput, $uploadNewFilesButton);

                var $filesContainer = new BEM({
                    block: "files-container",
                    elements: {
                        "head": buildFilesContainerHead(),
                        "body": buildFilesContainerBody()
                    }
                }).buildBlockStructure("<div>");

                var blockElements = [
                    $uploadButtonContainer,
                    $filesContainer
                ];
                return tabContentBuilder.buildFormBlock(blockElements, index);
            },
            fillTabDataFromDocument: function (document) {
                appendFiles(document.files);
                // append to this.data.$filesContainerBody
            },
            saveData: function (documentDTO) {
                return documentDTO;
            },
            clearTabData: function () {
            }
        };
    }
);
