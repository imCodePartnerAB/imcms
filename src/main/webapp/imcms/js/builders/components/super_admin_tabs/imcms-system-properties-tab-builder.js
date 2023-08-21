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
	        const $success = $('<div>', {
		        class: 'page-number success-animation',
		        style: 'display: none',
	        });

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
                        updateProperty({id: propertyId, value: propertyValue}, $success);
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'page-number',
                elements: {
                    'settings-input': buildCreateFiledInputNumberPage(),
                    'settings-button': buildPageNumberButton(),
	                'success': $success
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-settings-row'
            });
        }

        function updateProperty(property, $success) {
			$success.hide();
            propertyRestApi.update({id: property.id, value: property.value})
	            .done($success.show(500));
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
	        const $success = $('<div>', {
		        class: 'system-message success-animation',
		        style: 'display: none',
	        });

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
                        updateProperty({id: propertyId, value: propertyValue}, $success);
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'system-message',
                elements: {
                    'settings-input': buildCreateSystemMessageInput(),
                    'settings-button': buildCreateSystemMessageButton(),
	                'success':$success
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-settings-row'
            });
        }

        function buildServerMasterRow() {
	        const $success = $('<div>', {
		        class: 'server-master-data success-animation',
		        style: 'display: none',
	        });

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

	                    if (validateEmail(serverMasterEmailProperty.value)) {
		                    updateProperty(serverMasterNameProperty, $success);
		                    updateProperty(serverMasterEmailProperty, $success);
	                    }else modal.buildErrorWindow(texts.sections.error.incorrectEmail)
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
	                'success':$success
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-settings-row'
            });
        }

        function buildWebMasterRow() {
	        const $success = $('<div>', {
		        class: 'web-master-data success-animation',
		        style: 'display: none',
	        });

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

	                    if (validateEmail(webMasterEmailProperty.value)) {
		                    updateProperty(webMasterNameProperty, $success);
		                    updateProperty(webMasterEmailProperty, $success);
	                    }else modal.buildErrorWindow(texts.sections.error.incorrectEmail)
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
	                'success':$success
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-settings-row'
            });
        }

	    function validateEmail(email) {
		    const emailRegex = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/;
		    return email === '' ? true : emailRegex.test(email);
	    }

        const SystemPropertiesAdminTab = function (name, tabElements) {
            SuperAdminTab.call(this, name, tabElements);
        };

        SystemPropertiesAdminTab.prototype = Object.create(SuperAdminTab.prototype);

        SystemPropertiesAdminTab.prototype.getDocLink = () => texts.documentationLink;

        return new SystemPropertiesAdminTab(texts.name, [
            buildPageRow(),
            buildSystemMessageRow(),
            buildServerMasterRow(),
            buildWebMasterRow(),
        ]);
    }
);
