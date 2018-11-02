/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-system-properties-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder'],
    function (SuperAdminTab, texts, components) {

        texts = texts.superAdmin.systemProperties;

        let inputNumberPage;
        let messageSystem;
        let webMasterFieldName;
        let webMasterFieldEmail;
        let serverMasterName;
        let serverMasterEmail;

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

        function buildTitleTextStartPage() {
            return components.texts.titleText('<div>', texts.sections.startPage.name, {})
        }


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
            console.log(texts.changeButton);

            let $button = components.buttons.positiveButton({
                text: texts.changeButton,
                click: function setServerMasterbyNameAndEmail() {

                }
            });

            return components.buttons.buttonsContainer('<div>', [$button]);
        }

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


        return new SuperAdminTab(texts.name, [
            buildTitleTextStartPage(),
            buildCreateFiledInputNumberPage(),
            buildPageNumberButton(),
            buildCreateTitleSystemMessage(),
            buildCreateSystemMessageInput(),
            buildCreateSystemMessageButton(),
            buildCreateTitleServerMaster(),
            buildServerMasterNameInput(),
            buildServerMasterEmailInput(),
            buildCreateServerMasterButton(),
            buildCreateTilteWebMaster(),
            buildCreateInputWebMasterName(),
            buildCreateInputWebMasterEmail(),
            buildCreateMasterWebButton()

        ]);
    }
);
