/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-system-properties-tab-builder',
    ['imcms-super-admin-tab', 'imcms-bem-builder', 'imcms-i18n-texts', 'imcms-components-builder'],
    function (SuperAdminTab, BEM, texts, components) {

        texts = texts.superAdmin.systemProperties;

        function buildPageRow() {
            let inputNumberPage;

            function buildTitleTextStartPage() {
                return components.texts.titleText('<div>', texts.sections.startPage.name, {})
            }

            function buildCreateFiledInputNumberPage() {
                let $pageNumberBox = components.texts.textBox('<div>', texts.sections.startPage.input);
                inputNumberPage = $pageNumberBox.$input;

                return $pageNumberBox;
            }

            function buildPageNumberButton() {
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
                    'settings-title': buildTitleTextStartPage(),
                    'settings-input': buildCreateFiledInputNumberPage(),
                    'settings-button': buildPageNumberButton(),
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-field'
            });
        }

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
                function setStartDocument() {

                }

                let $button = components.buttons.positiveButton({
                    text: texts.changeButton,
                    click: setStartDocument()
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }


            return new BEM({
                block: 'imcms-settings-row',
                elements: {
                    'settings-title': buildCreateTitleSystemMessage(),
                    'settings-input': buildCreateSystemMessageInput(),
                    'settings-button': buildCreateSystemMessageButton(),
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-field'
            });
        }

        function buildServerMasterRow() {
            let serverMasterName;
            let serverMasterEmail;

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
            }).buildBlockStructure('<div>', {
                'class': 'imcms-field'
            });
        }

        function buildWebMasterRow() {
            let webMasterFieldName;
            let webMasterFieldEmail;

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
            }).buildBlockStructure('<div>', {
                'class': 'imcms-field'
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
