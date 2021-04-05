    define('imcms-properties-tab-builder',
    [
        'imcms-bem-builder', 'imcms-components-builder', 'imcms-users-rest-api', 'imcms', 'imcms-i18n-texts',
        'imcms-page-info-tab', 'jquery', 'js-utils',
    ],
    function (BEM, components, usersRestApi, imcms, i18Texts, PageInfoTab, $, jsUtils) {

        const texts = i18Texts.pageInfo.properties;

        const ALIAS_PROPERTY_KEY = 'imcms.document.alias';

        /**
         * Model of properties. Since we can not bind change() to input, I put inputs to $inputs field,
         * so we can update 'values' property like:
         *  obj.values = obj.$inputs.map(($input) => $input.getValue())
         * I use Symbol, because we don't have any value for property identification.
         *
         * @example
         *  {
         *      [Symbol()]: {
         *          values: ['propName', 'propValue'],
         *          $inputs: [$, $].
         *      }
         *  }
         * @type {Object.<Symbol, { values: [string, string], $inputs?: [Object, Object]}>}
         * @see updateValuesOnProperties
         */
        let properties = {};

        const $rowContainer = $('<div>');

        function updateValuesOnProperties() {
            Object.getOwnPropertySymbols(properties)
                .map((key) => properties[key])
                .forEach((prop) => {
                   prop.values = prop.$inputs.map(($input) => $input.getValue())
                });
        }

        function buildRowForNewProperty() {
            const $keyInput = components.texts.textBox('<div>', { text: texts.key });
            const $valueInput = components.texts.textBox('<div>', { text: texts.value });

            const $addButton = components.buttons.positiveButton({
                text: texts.add,
                click: () => {
                    addRow([$keyInput.getValue(), $valueInput.getValue()]);
                    $keyInput.setValue('');
                    $valueInput.setValue('');
                },
            });

            return new BEM({
                block: 'imcms-field',
                elements: {
                    'item': [$keyInput, $valueInput],
                    'button': $addButton,
                },
            }).buildBlockStructure('<div>', {
                class: 'imcms-flex--d-flex imcms-flex--align-items-flex-end',
            })
        }

        function addRow(values) {
            const key = Symbol();
            properties[key] = { values };
            const $row = buildRow(key);
            $rowContainer.append($row);
        }

        function buildRow(key) {
            const $inputs = properties[key].values.map((value) => buildInput(value));

            properties[key].$inputs = $inputs;

            const $removeButton = $(components.controls.remove(() => removeRow(key))).css("padding-right", "80px");

            return new BEM({
                block: 'imcms-field',
                elements: {
                    'item': $inputs,
                    'button': $removeButton,
                }
            }).buildBlockStructure('<div>', {
                class: 'imcms-flex--d-flex imcms-flex--align-items-center',
            });
        }

        function buildInput(value) {
            return components.texts.textBox('<div>', { value });
        }

        function removeRow(key) {
            delete properties[key];
            renderRows();
        }

        function renderRows() {
            $rowContainer.children().remove();
            Object.getOwnPropertySymbols(properties)
                .map((key) => buildRow(key))
                .forEach(($row) => $rowContainer.append($row));
        }

        const PropertiesTab = function (name) {
            PageInfoTab.call(this, name);
        };

        PropertiesTab.prototype = Object.create(PageInfoTab.prototype);

        PropertiesTab.prototype.isDocumentTypeSupported = () => true;

        PropertiesTab.prototype.tabElementsFactory = () => [
            buildRowForNewProperty(),
            $('<hr/>'),
            $rowContainer,
        ];

        /**
         * @param {Object.<string, string>} props
         * @return {Object.<Symbol, { values: [string, string]}>}
         */
        function mapDtoPropertiesToProperties(props) {
            const entries = Object.entries(props)
                .map((entry) => [Symbol(), { values: entry }]);
            return jsUtils.fromEntries(entries)
        }

        /**
         * @param {Object.<Symbol, { values: [string, string]}>} props
         * @return {Object.<string, string>}
         */
        function mapPropertiesToDtoProperties(props) {
            const entries = Object.getOwnPropertySymbols(props)
                .map((key) => props[key].values);
            return jsUtils.fromEntries(entries);
        }

        PropertiesTab.prototype.fillTabDataFromDocument = (document) => {
            const documentProperties = filterDocumentDtoProperties(document.properties);
            properties = mapDtoPropertiesToProperties(documentProperties);
            renderRows();
        };

        function filterDocumentDtoProperties(properties) {
            const entries = Object.entries(properties).filter(([key]) => key !== ALIAS_PROPERTY_KEY);
            return jsUtils.fromEntries(entries);
        }

        PropertiesTab.prototype.saveData = (document) => {
            updateValuesOnProperties();
            return {
                ...document,
                properties: mapPropertiesToDtoProperties(properties),
            }
        };

        return new PropertiesTab(texts.name);
    }
);