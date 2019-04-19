/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-files-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-files-rest-api',
        'imcms-modal-window-builder', 'imcms-field-wrapper', 'jquery', 'imcms-bem-builder', 'imcms-file-to-row-transformer'],
    function (SuperAdminTab, texts, components, filesRestApi, modal, fieldWrapper, $, BEM, fileToRow) {

        texts = texts.superAdmin.files;

        let $typeNameRow;
        let $fileContainer;

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
            $fileContainer = $('<div>', {
                'class': 'files-table'
            });

            filesLoader.whenFilesLoaded(files => {
                $fileContainer.append(files.map(file => fileToRow.transform(file)));
            });

            filesLoader.whenFilesLoaded(files => {
                $fileContainer.append(files.map(file => fileToRow.transform(file)));
            });

            return $fileContainer;
        }

        function buildCreateFileNameRow() {
            $typeNameRow = components.texts.textAreaField('<div>', {
                text: texts.title.createFileName
            });
            return $typeNameRow;
        }


        function buildToDownloadFilesButtons() {
            let $buttons = components.buttons.buttonsContainer('<div>', [
                components.buttons.positiveButton({
                    text: texts.title.download,
                    click: function () {
                    }
                }),
                components.buttons.positiveButton({
                    text: texts.title.download,
                    click: function () {

                    }
                })
            ]);

            return fieldWrapper.wrap([texts.title.download, $buttons]);
        }

        let $actionButtonsContainer;

        function buildActionButtonsContainer() {
            return $actionButtonsContainer = new BEM({
                block: 'buttons-action',
                elements: {
                    'download': buildToDownloadFilesButtons()
                }
            }).buildBlockStructure('<div>', {})
        }


        return new SuperAdminTab(texts.name, [
            buildMainWindowContainerByFiles(),
            buildActionButtonsContainer(),
            buildCreateFileNameRow()
        ]);
    }
);
