/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-system-properties-tab-builder',
    ['imcms-super-admin-tab', 'imcms-bem-builder', 'imcms-i18n-texts', 'imcms-settings-rest-api',
        'imcms-components-builder', 'jquery', 'imcms-modal-window-builder'],
    function (SuperAdminTab, BEM, texts, propertyRestApi, components, $, modal) {

        texts = texts.superAdmin.systemProperties;

        let inputNumberPage;
        //let propertyArchiveClass = 'imcms-settings-row--startPage';
        let serverMasterName;
        let serverMasterEmail;
        let webMasterFieldName;
        let webMasterFieldEmail;
        let localProperties = {};


        function buildPageRow() {

            function buildTitleTextStartPage() {
                return components.texts.titleText('<div>', texts.sections.startPage.name, {})
            }

            function buildCreateFiledInputNumberPage() {
                let $pageNumberBox = components.texts.textBox('<div>', {
                    name: 'startDocument'
                });
                inputNumberPage = $pageNumberBox.$input;
                return $pageNumberBox;
            }

            function buildPageNumberButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.changeButton,
                    click: function () {
                        let propertyId = inputNumberPage.data('id');
                        let propertyValue = inputNumberPage.val();
                        console.log(propertyName);
                        changeClickHandler({id: propertyId, value: propertyValue});
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'imcms-settings-row',
                elements: {
                    'settings-title': buildTitleTextStartPage(),
                    'settings-input': buildCreateFiledInputNumberPage(),
                    'settings-button': buildPageNumberButton(),
                }
            }).buildBlockStructure('<div>');
        }

        function changeClickHandler(property) {
            propertyRestApi.update({id: property.id, value: property.value});
        }

        //fix it
        propertyRestApi.getAllProperties().done(function (properties) {
            localProperties = properties;
            properties.forEach(function (property) {
                if (property.name === 'StartDocument') {
                    inputNumberPage.val(property.value);
                    inputNumberPage.data('id', property.id);
                }

                if (property.name === 'ServerMaster') {
                    serverMasterName.val(property.value);
                    serverMasterName.data('id', property.id);
                }

                if (property.name === 'ServerMasterAddress') {
                    serverMasterEmail.val(property.value);
                    serverMasterEmail.data('id', property.id);
                }

                if (property.name === 'WebMaster') {
                    webMasterFieldName.val(property.value);
                    webMasterFieldName.data('id', property.id);
                }

                if (property.name === 'WebMasterAddress') {
                    webMasterFieldEmail.val(property.value);
                    webMasterFieldEmail.data('id', property.id);
                }
            })
        });

        function buildSystemMessageRow() {
            let messageSystem;

            function buildCreateTitleSystemMessage() {
                return components.texts.titleText('<div>', texts.sections.systemMessage.name, {})

            }

            function buildCreateSystemMessageInput() {
                let $fieldForSystemMessage = components.texts.textBox('<div>', texts.sections.systemMessage.inputBox);
                messageSystem = $fieldForSystemMessage.$input;

                return $fieldForSystemMessage;
            }


            function buildCreateSystemMessageButton() {

                let $button = components.buttons.positiveButton({
                    text: texts.changeButton,
                    click: function (property) {
                        changeClickHandler(property.value);
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            propertyRestApi.findByName('SystemMessage').done(function (property) {
                messageSystem.val(property.value)
            });

            return new BEM({
                block: 'imcms-settings-row',
                elements: {
                    'settings-title': buildCreateTitleSystemMessage(),
                    'settings-input': buildCreateSystemMessageInput(),
                    'settings-button': buildCreateSystemMessageButton(),
                }
            }).buildBlockStructure('<div>');
        }

        function buildServerMasterRow() {


            function buildCreateTitleServerMaster() {
                return components.texts.titleText('<div>', texts.sections.serverMaster.name, {})

            }

            function buildServerMasterNameInput() {
                let $serverMasterName = components.texts.textBox('<div>', texts.sections.serverMaster.inputName);
                serverMasterName = $serverMasterName.$input;

                return $serverMasterName;
            }

            function buildServerMasterEmailInput() {
                let $serverMasterEmailInput = components.texts.textBox('<div>', texts.sections.serverMaster.inputEmail);
                serverMasterEmail = $serverMasterEmailInput.$input;

                return serverMasterEmail;
            }

            function buildCreateServerMasterButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.changeButton,
                    click: function setServerMasterbyNameAndEmail() {

                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'imcms-settings-row',
                elements: {
                    'settings-title': buildCreateTitleServerMaster(),
                    'settings-input-name': buildServerMasterNameInput(),
                    'settings-input-email': buildServerMasterEmailInput(),
                    'settings-button': buildCreateServerMasterButton(),
                }
            }).buildBlockStructure('<div>');
        }

        function buildWebMasterRow() {

            function buildCreateTilteWebMaster() {
                return components.texts.titleText('<div>', texts.sections.webMaster.name, {})

            }

            function buildCreateInputWebMasterName() {
                let $nameWebMasterInput = components.texts.textBox('<div>', texts.sections.webMaster.inputName);
                webMasterFieldName = $nameWebMasterInput.$input;

                return $nameWebMasterInput;
            }

            function buildCreateInputWebMasterEmail() {
                let $emailWebMasterInput = components.texts.textBox('<div>', texts.sections.webMaster.inputEmail);
                webMasterFieldEmail = $emailWebMasterInput.$input;

                return $emailWebMasterInput;
            }


            function buildCreateMasterWebButton() {
                let $button = components.buttons.positiveButton({
                    text: texts.changeButton,
                    click: function () {

                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'imcms-settings-row',
                elements: {
                    'settings-title': buildCreateTilteWebMaster(),
                    'settings-input-name': buildCreateInputWebMasterName(),
                    'settings-input-email': buildCreateInputWebMasterEmail(),
                    'settings-button': buildCreateMasterWebButton(),
                }
            }).buildBlockStructure('<div>');
        }

        return new SuperAdminTab(texts.name, [
            buildPageRow(),
            buildSystemMessageRow(),
            buildServerMasterRow(),
            buildWebMasterRow(),
        ]);
    }
);
