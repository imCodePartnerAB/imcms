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

        return {
            name: "files",
            data: {},
            tabIndex: null,
            isDocumentTypeSupported: function (docType) {
                return docType === docTypes.file;
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
                        var formData = new FormData();

                        for (var i = 0; i < this.files.length; i++) {
                            formData.append('files', this.files[i]);
                        }

                        // todo: save somewhere
                    }
                });

                var $uploadNewFilesButton = components.buttons.positiveButton({
                    text: "Upload",
                    click: function () {
                        $fileInput.click();
                    }
                });

                var blockElements = [
                    $uploadNewFilesButton
                ];
                return tabContentBuilder.buildFormBlock(blockElements, index);
            },
            fillTabDataFromDocument: function (document) {
            },
            saveData: function (documentDTO) {
                return documentDTO;
            },
            clearTabData: function () {
            }
        };
    }
);
