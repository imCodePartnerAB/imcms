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

        let $fileContainer;
        let $fileSecondContainer;

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

        let currentFile;
        let $fileUrl;
        function buildFirstInstanceFiles() {
            $fileContainer = $('<div>', {
                'class': 'first-files'
            });

            firstFilesLoader.whenFilesLoaded(files => {
                $fileContainer.append(files.map(file => fileToRow.transformFirstColumn(file, fileEditor)));
            });

            return $fileContainer;
        }

        function buildSecondInstanceFiles() {
            $fileSecondContainer = $('<div>', {
                'class': 'second-files'
            });

            secondFilesLoader.whenFilesLoaded(files => {
                $fileSecondContainer.append(files.map(file => fileToRow.transformSecondColumn(file.fullPath, fileEditor)));
            });

            return $fileSecondContainer;
        }

        let filesContainer;

        function buildTableFilesContainer() {
            return filesContainer = new BEM({
                block: 'files-table',
                elements: {
                    'first-instance': buildFirstInstanceFiles(),
                    'second-instance': buildSecondInstanceFiles()
                }
            }).buildBlockStructure('<div>', {})
        }

        function clickActionMoveRight(currentPosition) {

        }

        function clickActionMoveLeft(currentPosition) {

        }

        function buildMoveButtons() {
            let $buttonMoveLeft = components.buttons.positiveButton({
                text: texts.moveLeft,
                click: function () {
                }
            });
            let buttonMoveRight = components.buttons.positiveButton({
                text: texts.moveRight,
                click: function () {

                }
            });

            return new BEM({
                block: 'buttons-move',
                elements: {
                    'left-move': $buttonMoveLeft,
                    'right-move': buttonMoveRight,
                }
            }).buildBlockStructure('<div>')
        }

        let $actionButtonsContainer;

        function buildFirstActionButtonsContainer() {
            return $actionButtonsContainer = new BEM({
                block: 'first-files-action',
                elements: {
                    'add-file': components.controls.add(fileEditor.addFile).attr("title", texts.add),
                    'upload-file': components.controls.upload().attr("title", texts.upload)
                }
            }).buildBlockStructure('<div>', {})
        }

        let $actionSecondButtonsContainer;

        function buildSecondActionButtonsContainer() {
            return $actionSecondButtonsContainer = new BEM({
                block: 'second-files-action',
                elements: {
                    'add-file': components.controls.add(fileEditor.addFile).attr("title", texts.add),
                    'upload-file': components.controls.upload().attr("title", texts.upload)
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


        return new SuperAdminTab(texts.name, [
            buildTableFilesContainer(),
            buildMoveButtons(),
            buildButtonsActionContainer()
        ]);
    }
);
