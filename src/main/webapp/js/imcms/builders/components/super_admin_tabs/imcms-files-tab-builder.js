/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-files-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-files-rest-api',
        'imcms-modal-window-builder', 'imcms-field-wrapper', 'jquery', 'imcms-bem-builder', 'imcms-file-to-row-transformer',
        'imcms-file-editor'],
    function (SuperAdminTab, texts, components, filesRestApi, modal, fieldWrapper, $, BEM, fileToRow, fileEditor) {

        texts = texts.superAdmin.files;

        const firstFilesLoader = {
            files: false,
            callback: [],
            whenFilesLoaded: function (callback) {
                (this.files) ? callback(this.files) : this.callback.push(callback);
            },
            runCallbacks: function (files) {
                this.files = files;

                this.callback.forEach(callback => {
                    callback(files);
                });
            }
        };

        const secondFilesLoader = {
            files: false,
            callback: [],
            whenFilesLoaded: function (callback) {
                (this.files) ? callback(this.files) : this.callback.push(callback);
            },
            runCallbacks: function (files) {
                this.files = files;

                this.callback.forEach(callback => {
                    callback(files);
                });
            }
        };

        filesRestApi.read()
            .done(files => {
                firstFilesLoader.runCallbacks(files);
                secondFilesLoader.runCallbacks(files);
            })
            .fail(() => modal.buildErrorWindow(texts.error.loadError));

        function buildFirstInstanceFiles() {
            const $fileContainer = $('<div>', {
                'class': 'first-files'
            });

            firstFilesLoader.whenFilesLoaded(files => {
                $fileContainer.append(files.map(file => fileToRow.transformRootDirToRow.call(
                    {subFilesContainerIndex: 0}, file, fileEditor))
                );
            });

            return $fileContainer;
        }

        function buildSecondInstanceFiles() {
            const $fileSecondContainer = $('<div>', {
                'class': 'second-files'
            });

            secondFilesLoader.whenFilesLoaded(files => {
                $fileSecondContainer.append(files.map(file => fileToRow.transformRootDirToRow.call(
                    {subFilesContainerIndex: 1}, file, fileEditor))
                );
            });

            return $fileSecondContainer;
        }

        function buildDocumentsContainer() {
            return new BEM({
                block: 'documents-container',
                elements: {
                    'group-editor': fileEditor.getTemplateGroupEditor(),
                    'table-documents': fileEditor.displayDocs()
                }
            }).buildBlockStructure('<div>', {});
        }

        function buildTableFilesContainer() {
            return new BEM({
                block: 'files-table',
                elements: {
                    'first-instance': buildFirstInstanceFiles(),
                    'second-instance': buildSecondInstanceFiles()
                }
            }).buildBlockStructure('<div>', {})
        }

        function buildMoveButtons() {
            return new BEM({
                block: 'buttons-move',
                elements: {
                    'left-move': components.controls.left(fileEditor.buildMoveFile(1)).attr("title", texts.moveLeft),
                    'right-move': components.controls.right(fileEditor.buildMoveFile(0)).attr("title", texts.moveRight)
                }
            }).buildBlockStructure('<div>')
        }

        function buildCopyButtons() {
            return new BEM({
                block: 'buttons-copy',
                elements: {
                    'left-copy': components.controls.left(fileEditor.buildCopyFile(1)).attr("title", texts.copyLeft),
                    'right-copy': components.controls.right(fileEditor.buildCopyFile(0)).attr("title", texts.copyRight)
                }
            }).buildBlockStructure('<div>');
        }

        function buildFirstActionButtonsContainer() {
            return new BEM({
                block: 'first-files-action',
                elements: {
                    'add-file': components.controls.add(fileEditor.buildAddFile(0)).attr("title", texts.add),
                    'upload-file': components.controls.upload(fileEditor.buildUploadFile(0)).attr("title", texts.upload)
                }
            }).buildBlockStructure('<div>', {})
        }

        function buildSecondActionButtonsContainer() {
            return new BEM({
                block: 'second-files-action',
                elements: {
                    'add-file': components.controls.add(fileEditor.buildAddFile(1)).attr("title", texts.add),
                    'upload-file': components.controls.upload(fileEditor.buildUploadFile(1)).attr("title", texts.upload)
                }
            }).buildBlockStructure('<div>', {})
        }

        function buildButtonsActionContainer() {
            return new BEM({
                block: 'buttons-action',
                elements: {
                    'first-action': buildFirstActionButtonsContainer(),
                    "second-action": buildSecondActionButtonsContainer()
                }
            }).buildBlockStructure('<div>')
        }

        function buildTitleMove() {
            return $('<div>', {
                'class': 'title-move',
                text: texts.title.titleByMove
            });
        }

        function buildTitleCopy() {
            return $('<div>', {
                'class': 'title-copy',
                text: texts.title.titleByCopy
            });
        }

        return new SuperAdminTab(texts.name, [
            buildTableFilesContainer(),
            buildButtonsActionContainer(),
            buildTitleMove(),
            buildMoveButtons(),
            buildTitleCopy(),
            buildCopyButtons(),
            buildDocumentsContainer()
        ]);
    }
);
