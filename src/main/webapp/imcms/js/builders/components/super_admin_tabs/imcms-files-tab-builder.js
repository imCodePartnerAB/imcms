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

            const $fileRootContainer = $('<div>', {
                'class': 'root-dir'
            });

            const $subFilesContainer = $('<div>', {
                'class': 'first-sub-files'
            });

            firstFilesLoader.whenFilesLoaded(files => {
                $fileContainer.append($fileRootContainer);
                $fileRootContainer.append(files.map(file => fileToRow.transformRootDirToRow.call(
                    {subFilesContainerIndex: 0}, file, fileEditor))
                );
                $fileContainer.append(buildPathRow());
                $fileContainer.append($subFilesContainer);
            });

            return $fileContainer;
        }

        function buildSecondInstanceFiles() {
            const $fileContainer = $('<div>', {
                'class': 'second-files'
            });

            const $fileRootContainer = $('<div>', {
                'class': 'root-dir'
            });

            const $subFilesContainer = $('<div>', {
                'class': 'second-sub-files'
            });

            secondFilesLoader.whenFilesLoaded(files => {
                $fileContainer.append($fileRootContainer);
                $fileRootContainer.append(files.map(file => fileToRow.transformRootDirToRow.call(
                    {subFilesContainerIndex: 1}, file, fileEditor))
                );
                $fileContainer.append(buildPathRow());
                $fileContainer.append($subFilesContainer);
            });

            return $fileContainer;
        }

        function buildPathRow(){
            return new BEM({
                block: 'path-row',
                elements: {
                    'path': $('<div>', {text:"/"}),
                    'count': $('<div>', {"count": ""})
                }
            }).buildBlockStructure('<div>');
        }

        function buildDocumentsContainer() {
            return new BEM({
                block: 'templates-table',
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
                    'left-move': components.controls.left(fileEditor.bindMoveFile(1)).attr("title", texts.moveLeft),
                    'right-move': components.controls.right(fileEditor.bindMoveFile(0)).attr("title", texts.moveRight)
                }
            }).buildBlockStructure('<div>')
        }

        function buildCopyButtons() {
            return new BEM({
                block: 'buttons-copy',
                elements: {
                    'left-copy': components.controls.left(fileEditor.bindCopyFile(1)).attr("title", texts.copyLeft),
                    'right-copy': components.controls.right(fileEditor.bindCopyFile(0)).attr("title", texts.copyRight)
                }
            }).buildBlockStructure('<div>');
        }

        function buildFirstActionButtonsContainer() {
            return new BEM({
                block: 'first-files-action',
                elements: {
                    'add-file': components.controls.add(fileEditor.bindAddFile(0)).attr("title", texts.add),
                    'upload-file': components.controls.upload(fileEditor.bindUploadFile(0)).attr("title", texts.upload)
                }
            }).buildBlockStructure('<div>', {})
        }

        function buildSecondActionButtonsContainer() {
            return new BEM({
                block: 'second-files-action',
                elements: {
                    'add-file': components.controls.add(fileEditor.bindAddFile(1)).attr("title", texts.add),
                    'upload-file': components.controls.upload(fileEditor.bindUploadFile(1)).attr("title", texts.upload)
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

        function buildMoveCopyFilesContainer(){
            let $moveCopyFilesContainer = $("<div>", {id: "move-copy-files"});
            $moveCopyFilesContainer.append(buildTitleMove());
            $moveCopyFilesContainer.append(buildMoveButtons());
            $moveCopyFilesContainer.append(buildTitleCopy());
            $moveCopyFilesContainer.append(buildCopyButtons());
            return $moveCopyFilesContainer;
        }

        const FilesAdminTab = function (name, tabElements) {
            SuperAdminTab.call(this, name, tabElements);
        };

        FilesAdminTab.prototype = Object.create(SuperAdminTab.prototype);

        FilesAdminTab.prototype.getDocLink = () => texts.documentationLink;

        return new FilesAdminTab(texts.name, [
            buildTableFilesContainer(),
            buildButtonsActionContainer(),
            buildMoveCopyFilesContainer(),
            buildDocumentsContainer()
        ]);
    }
);
