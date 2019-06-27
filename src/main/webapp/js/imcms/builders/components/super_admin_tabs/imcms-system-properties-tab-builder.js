/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-system-properties-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-bem-builder', 'imcms-i18n-texts', 'imcms-settings-rest-api',
        'imcms-components-builder', "imcms-modal-window-builder", "jquery"
    ],
    function (SuperAdminTab, BEM, texts, propertyRestApi, components, modal, $) {

        texts = texts.superAdmin.systemProperties;

        let inputNumberPage;
        let serverMasterName;
        let serverMasterEmail;
        let webMasterFieldName;
        let webMasterFieldEmail;
        let systemMessage;

        function buildPageRow() {

            function buildCreateFiledInputNumberPage() {
                let $pageNumberBox = components.texts.textBox('<div>', {
                    name: 'startDocument',
                    text: texts.sections.startPage.name
                });
                inputNumberPage = $pageNumberBox.$input;
                return $pageNumberBox;
            }

            function buildPageNumberButton() {
                let $button = components.buttons.positiveButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.changeButton,
                    click: () => {
                        let propertyId = inputNumberPage.data('id');
                        let propertyValue = inputNumberPage.val();
                        updateProperty({id: propertyId, value: propertyValue});
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'page-number',
                elements: {
                    'settings-input': buildCreateFiledInputNumberPage(),
                    'settings-button': buildPageNumberButton(),
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-settings-row'
            });
        }

        function updateProperty(property) {
            propertyRestApi.update({id: property.id, value: property.value});
        }

        propertyRestApi.getAllProperties()
            .done(properties => {
                properties.forEach(property => {
                    switch (property.name) {
                        case 'StartDocument':
                            inputNumberPage.val(property.value);
                            inputNumberPage.data('id', property.id);
                            break;
                        case 'SystemMessage':
                            systemMessage.val(property.value);
                            systemMessage.data('id', property.id);
                            break;
                        case 'ServerMaster':
                            serverMasterName.val(property.value);
                            serverMasterName.data('id', property.id);
                            break;
                        case 'ServerMasterAddress':
                            serverMasterEmail.val(property.value);
                            serverMasterEmail.data('id', property.id);
                            break;
                        case 'WebMaster':
                            webMasterFieldName.val(property.value);
                            webMasterFieldName.data('id', property.id);
                            break;
                        case 'WebMasterAddress':
                            webMasterFieldEmail.val(property.value);
                            webMasterFieldEmail.data('id', property.id);
                            break;
                    }
                });
            })
            .fail(() => modal.buildErrorWindow(texts.error.loadFailed));

        function buildSystemMessageRow() {

            function buildCreateSystemMessageInput() {
                let $fieldForSystemMessage = components.texts.textArea('<div>', {
                    name: 'systemMessage',
                    text: texts.sections.systemMessage.name
                });
                systemMessage = $fieldForSystemMessage.$input;

                return $fieldForSystemMessage;
            }


            function buildCreateSystemMessageButton() {
                let $button = components.buttons.positiveButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.changeButton,
                    click: () => {
                        let propertyId = systemMessage.data('id');
                        let propertyValue = systemMessage.val();
                        updateProperty({id: propertyId, value: propertyValue});
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'system-message',
                elements: {
                    'settings-input': buildCreateSystemMessageInput(),
                    'settings-button': buildCreateSystemMessageButton(),
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-settings-row'
            });
        }

        function buildServerMasterRow() {

            function buildServerMasterTitle() {
                return $('<div>', {
                    'class': 'imcms-title',
                    text: texts.sections.serverMaster.name
                });
            }

            function buildServerMasterNameInput() {
                let $serverMasterName = components.texts.textBox('<div>', {
                    name: 'ServerMaster',
                    text: texts.nameInputTitle
                });
                serverMasterName = $serverMasterName.$input;

                return $serverMasterName;
            }

            function buildServerMasterEmailInput() {
                let $serverMasterEmailInput = components.texts.textBox('<div>', {
                    name: 'ServerMasterAddress',
                    text: texts.emailInputTitle
                });
                serverMasterEmail = $serverMasterEmailInput.$input;

                return $serverMasterEmailInput;
            }

            function buildCreateServerMasterButton() {
                let $button = components.buttons.positiveButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.changeButton,
                    click: () => {
                        let propertyIdForName = serverMasterName.data('id');
                        let propertyValueForName = serverMasterName.val();
                        let propertyIdForEmail = serverMasterEmail.data('id');
                        let propertyValueForEmail = serverMasterEmail.val();

                        let serverMasterNameProperty = {id: propertyIdForName, value: propertyValueForName};
                        let serverMasterEmailProperty = {id: propertyIdForEmail, value: propertyValueForEmail};

                        updateProperty(serverMasterNameProperty);
                        updateProperty(serverMasterEmailProperty);
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'server-master',
                elements: {
                    'settings-title': buildServerMasterTitle(),
                    'settings-input-name': buildServerMasterNameInput(),
                    'settings-input-email': buildServerMasterEmailInput(),
                    'settings-button': buildCreateServerMasterButton(),
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-settings-row'
            });
        }

        function buildWebMasterRow() {

            function buildWebMasterTitle() {
                return $('<div>', {
                    'class': 'imcms-title',
                    text: texts.sections.webMaster.name
                })
            }

            function buildCreateInputWebMasterName() {
                let $nameWebMasterInput = components.texts.textBox('<div>', {
                    name: 'WebMaster',
                    text: texts.nameInputTitle
                });
                webMasterFieldName = $nameWebMasterInput.$input;

                return $nameWebMasterInput;
            }

            function buildCreateInputWebMasterEmail() {
                let $emailWebMasterInput = components.texts.textBox('<div>', {
                    name: 'WebMasterAddress',
                    text: texts.emailInputTitle
                });
                webMasterFieldEmail = $emailWebMasterInput.$input;

                return $emailWebMasterInput;
            }


            function buildCreateMasterWebButton() {
                let $button = components.buttons.positiveButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.changeButton,
                    click: () => {
                        let propertyIdForWebName = webMasterFieldName.data('id');
                        let propertyValueForWebName = webMasterFieldName.val();
                        let propertyIdForWebEmail = webMasterFieldEmail.data('id');
                        let propertyValueForWebEmail = webMasterFieldEmail.val();

                        let webMasterNameProperty = {id: propertyIdForWebName, value: propertyValueForWebName};
                        let webMasterEmailProperty = {id: propertyIdForWebEmail, value: propertyValueForWebEmail};

                        updateProperty(webMasterNameProperty);
                        updateProperty(webMasterEmailProperty);
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'web-master',
                elements: {
                    'settings-title': buildWebMasterTitle(),
                    'settings-input-name': buildCreateInputWebMasterName(),
                    'settings-input-email': buildCreateInputWebMasterEmail(),
                    'settings-button': buildCreateMasterWebButton(),
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-settings-row'
            });
        }

        return new SuperAdminTab(texts.name, [
            buildPageRow(),
            buildSystemMessageRow(),
            buildServerMasterRow(),
            buildWebMasterRow(),
        ]);
    }
);
