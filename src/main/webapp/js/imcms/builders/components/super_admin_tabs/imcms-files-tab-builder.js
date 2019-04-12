/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-files-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-files-rest-api',
        'imcms-modal-window-builder', 'imcms-field-wrapper', 'jquery', 'imcms-bem-builder'],
    function (SuperAdminTab, texts, components, filesRestApi, modal, fieldWrapper, $, BEM) {

        texts = texts.superAdmin.files;

        let $typeNameRow;
        let $firstFileContainer;
        let $secondFileContainer;

        const filesLoader = {
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
                filesLoader.runCallbacks(files);
            })
            .fail(() => modal.buildErrorWindow('do not load files!'));

        function buildMainWindowContainerByFiles() {
            $firstFileContainer = $('<div>', {
                'class': 'files-table'
            });

            filesLoader.whenFilesLoaded(files => {
                // $fileContainer.append(files.map(file => fileToRow.transform(file, fileEditor)));
                $firstFileContainer.append(files);

                console.log(files.map(file => file + "NAME !!"));
            });

            return $firstFileContainer;
        }

        function buildSecondMainWindowContainerByFiles() {
            $secondFileContainer = $('<div>', {
                'class': 'files-second-table'
            });

            filesLoader.whenFilesLoaded(files => {
                // $fileContainer.append(files.map(file => fileToRow.transform(file, fileEditor)));
                $secondFileContainer.append(files);
            });

            return fieldWrapper.wrap([$secondFileContainer]);
        }

        function buildCreateFileNameRow() {
            $typeNameRow = components.texts.textAreaField('<div>', {
                text: texts.title.createFileName
            });
            return $typeNameRow;
        }


        function buildToMoveFilesButtons() {
            let $buttons = components.buttons.buttonsContainer('<div>', [
                components.buttons.positiveButton({
                    text: texts.moveRight,
                    click: function () {
                    }
                }),
                components.buttons.positiveButton({
                    text: texts.moveLeft,
                    click: function () {

                    }
                })
            ]);

            return fieldWrapper.wrap([texts.title.move, $buttons]);
        }

        function buildToCopyFilesButtons() {
            let $buttons = components.buttons.buttonsContainer('<div>', [
                components.buttons.positiveButton({
                    text: texts.moveRight,
                    click: function () {
                    }
                }),
                components.buttons.positiveButton({
                    text: texts.moveLeft,
                    click: function () {

                    }
                })
            ]);

            return fieldWrapper.wrap([texts.title.copy, $buttons]);
        }

        function buildToDownloadFilesButtons() {
            let $buttons = components.buttons.buttonsContainer('<div>', [
                components.buttons.positiveButton({
                    text: texts.moveRight,
                    click: function () {
                    }
                }),
                components.buttons.positiveButton({
                    text: texts.moveLeft,
                    click: function () {

                    }
                })
            ]);

            return fieldWrapper.wrap([texts.title.download, $buttons]);
        }

        function buildToEditFilesButtons() {
            let $buttons = components.buttons.buttonsContainer('<div>', [
                components.buttons.positiveButton({
                    text: texts.moveRight,
                    click: function () {
                    }
                }),
                components.buttons.positiveButton({
                    text: texts.moveLeft,
                    click: function () {

                    }
                })
            ]);

            return fieldWrapper.wrap([texts.title.edit, $buttons]);
        }

        let $actionButtonsContainer;

        function buildActionButtonsContainer() {
            return $actionButtonsContainer = new BEM({
                block: 'buttons-action',
                elements: {
                    'move': buildToMoveFilesButtons(),
                    'copy': buildToCopyFilesButtons(),
                    'download': buildToDownloadFilesButtons(),
                    'edit': buildToEditFilesButtons()
                }
            }).buildBlockStructure('<div>', {/*style: 'display: none;'*/})
        }


        return new SuperAdminTab(texts.name, [
            buildMainWindowContainerByFiles(),
            buildSecondMainWindowContainerByFiles(),
            buildActionButtonsContainer(),
            buildCreateFileNameRow()
        ]);
    }
);
