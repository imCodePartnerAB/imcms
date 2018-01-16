/**
 * Tab in Page Info window for File Document type.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.01.18
 */
Imcms.define("imcms-file-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-document-types", "imcms-page-info-tab-form-builder",
        "jquery"
    ],
    function (BEM, components, docTypes, tabContentBuilder, $) {

        function buildFilesContainerBody() {
            return $("<div>", {"class": "files-container-body"});
        }

        function buildFilesContainerHead() {
            var filesContainerHeadBEM = new BEM({
                block: "files-container-head",
                elements: {
                    "title": "imcms-title"
                }
            });

            return filesContainerHeadBEM.buildBlock("<div>", [
                {"title": $("<div>", {text: "ID"})},
                {"title": $("<div>", {text: "Name"})},
                {"title": $("<div>", {text: "Default"})}
            ]);
        }

        function appendFiles(files) {

        }

        return {
            name: "files",
            data: {},
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
                var parent = this;

                var $fileInput = $("<input>", {
                    type: "file",
                    style: "display: none;",
                    multiple: "",
                    change: function () {
                        var formData = new FormData();

                        for (var i = 0; i < this.files.length; i++) {
                            formData.append('files', this.files[i]);
                        }

                        parent.data.formData = formData;

                        appendFiles(this.files);
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
                    elements: [
                        buildFilesContainerHead(),
                        this.data.$filesContainerBody = buildFilesContainerBody()
                    ]
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
