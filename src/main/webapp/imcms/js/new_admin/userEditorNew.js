/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 18.06.18
 */

import '../../css/imcms_admin.css';
import '../../css/imcms-imports_files.css';
import '../../css/imcms-edit-user-page.css';

const $ = require('jquery');
const components = require('imcms-components-builder');
const languagesRestApi = require('imcms-languages-rest-api');
const imcms = require('imcms');
const modal = require("imcms-modal-window-builder");
let texts = require("imcms-i18n-texts");
const BEM = require('imcms-bem-builder');
const userPropertiesRestAPI = require('imcms-user-properties-rest-api');

const superAdminRoleId = 1;

function unBlockingUser() {
    const $form = $('#user-edit-form');
    const blockingFlag = $form.find('#flagControlBlocking');

    blockingFlag.change(function () {
        const checkBox = blockingFlag[0];
        if (checkBox.checked) {
            checkBox.value = true
        } else {
            checkBox.value = false
        }
    })
}

function onSubmit(e) {
    const $form = $('#user-edit-form');
    const valuePass = $form.find('input[name=password]').val();
    const valuePass2 = $form.find('input[name=password2]').val();
    if (!$form.find('input[name=login]').val()) {
        e.preventDefault();
        alert($('#must-fill-mandatory-fields-text').val());
        return;
    } else if (valuePass !== valuePass2) {
        e.preventDefault();
        alert($('#pass-verification-failed-text').val());
        return;
    }

    const chooseLang = $('#languages-select-container').find('input[name=langCode]').val();
    alert(texts.languageFlags.alertInfoLanguage + chooseLang + ')');

    $('[name=userPhoneNumber]').removeAttr('disabled');
}

let properties = {};
let createProperties = {};
let editProperties = {};
let removeProperties = {};

const $horizontalLine = $('<hr/>');
let $propertiesContainer = $('<div>', {
    class: 'user-properties'
});


function addRow(key, values) {
    properties[key] = {values};
    const $row = buildRow(key);
    $('.imcms-create-properties-modal-window__modal-body').append($propertiesContainer.append($row));
}

function buildInputs(values) {
    const key = values[0];
    const value = values[1];
    const $inputs = [components.texts.textBox('<div>', {
        name: 'userKeyProperties',
        value: key
    }),
        components.texts.textBox('<div>', {
            name: 'userValueProperties',
            value: value
        })
    ];

    $inputs.map(input => input.$input.attr('disabled', 'disabled'));

    return $inputs;
}

function removeRow(key) {
    modal.buildModalWindow(texts.userProperties.deleteConfirm, onConfirm => {
        if (onConfirm) {
            if(!!properties[key].values[2]) removeProperties[key] = properties[key];

            delete properties[key];
            delete editProperties[key];
            delete createProperties[key];
        }
        renderRows();
    });
}

function activeEditPropertiesInputs(key, $saveUpdateButton, $editBtn) {
    $editBtn.hide();
    $saveUpdateButton.show();
    properties[key].$inputs.map(input => input.$input.removeAttr('disabled'));
}

function updateRowProperties(key, $propertyRow) {
    const keyProperty = $propertyRow.find("input[name='userKeyProperties']").val();
    const valueProperty = $propertyRow.find("input[name='userValueProperties']").val();

    if(keyProperty.replace(/\s/g,"") === "" || valueProperty.replace(/\s/g,"") === ""){
        alert(texts.userProperties.emptyValueError);
        return;
    }

    let isDuplicateKey = Object.getOwnPropertySymbols(properties)
        .filter((propertyKey) => propertyKey !== key)
        .some((key) => properties[key].values[0] === keyProperty);

    if (!isDuplicateKey) {
        const previousPropertyValues = properties[key].values;
        if(previousPropertyValues[0] !== keyProperty || previousPropertyValues[1] !== valueProperty){
            properties[key].values = [keyProperty, valueProperty, previousPropertyValues[2]];

            if(!!previousPropertyValues[2]){
                editProperties[key] = properties[key];
            }else{
                createProperties[key] = properties[key];
            }
        }

        $propertyRow.find(".imcms-field__save-btn").hide();
        $propertyRow.find(".imcms-field__update-btn").show();
        renderRows();
    } else {
        alert(texts.userProperties.wrongKeyError);
    }
}

function buildRow(key) {
    const propValues = properties[key].values;
    const $inputs = buildInputs(propValues);
    properties[key].$inputs = $inputs;

    const $removeButton = $(components.controls.remove(() => removeRow(key)));
    const $saveUpdateButton = $(components.controls.check(function () {
        updateRowProperties(key, $(this).parent())
    }));
    const $editButton = $(components.controls.edit(function (){
        activeEditPropertiesInputs(key, $saveUpdateButton, $(this))
    }));

    return new BEM({
        block: 'imcms-field',
        elements: {
            'item': $inputs,
            'update-btn': $editButton,
            'save-btn': $saveUpdateButton,
            'button': $removeButton
        }
    }).buildBlockStructure('<div>', {
        class: 'imcms-flex--d-flex imcms-flex--align-items-center',
    });
}

function renderRows() {
    $propertiesContainer.children().remove();
    Object.getOwnPropertySymbols(properties)
        .map((key) => buildRow(key))
        .forEach(($row) => $('.imcms-create-properties-modal-window__modal-body').append($propertiesContainer.append($row)));
}

function buildRowForNewUserProperty() {

    function cleanInputs() {
        $keyInput.setValue('');
        $valueInput.setValue('');
    }

    const $keyInput = components.texts.textBox('<div>', {text: texts.userProperties.key});
    const $valueInput = components.texts.textBox('<div>', {text: texts.userProperties.value});

    const $addButton = components.buttons.positiveButton({
        text: texts.userProperties.add,
        click: () => {
            const keysProperties = getAllExistPropertyKeys();
            const inputKeyVal = $keyInput.getValue();
            const inputValueVal = $valueInput.getValue();

            if(inputKeyVal.replace(/\s/g,"") === "" || inputValueVal.replace(/\s/g,"") === ""){
                alert(texts.userProperties.emptyValueError);
                return;
            }

            if (keysProperties.includes(inputKeyVal)) {
                alert(texts.userProperties.wrongKeyError);
                return;
            }

            const key = Symbol();
            const values = [inputKeyVal, inputValueVal, null];
            addRow(key, values);
            createProperties[key] = {values};

            cleanInputs();
        },
    });

    return new BEM({
        block: 'imcms-field',
        elements: {
            'item': [$keyInput, $valueInput],
            'add-property': $addButton,
        },
    }).buildBlockStructure('<div>', {
        class: 'imcms-flex--d-flex imcms-flex--align-items-flex-start',
    })
}

function getAllExistPropertyKeys() {
    return Object.getOwnPropertySymbols(properties)
        .map((key) => properties[key])
        .map(prop => prop.values)
        .map(value => value[0]);
}

function updateValuesOnProperties() {
    Object.getOwnPropertySymbols(properties)
        .map((key) => properties[key])
        .forEach((prop) => {
            const propertyId = prop.values[2].id;
            prop.values = prop.$inputs.map(($input) => $input.getValue());
            prop.values.push({id: propertyId})
        });
}

function onViewUserProperties() {

    const $form = $('#user-edit-form');
    const editedUserId = $form[0].id.value;
    $propertiesContainer.children().remove();
    properties = {};

    userPropertiesRestAPI.getPropertiesByUserId(editedUserId).done(props => {
        props.forEach(prop => {
            const key = Symbol();
            const keyProp = prop.keyName;
            const valueProp = prop.value;
            const id = {id: prop.id};
            addRow(key, [keyProp, valueProp, id]);
        });
    }).fail(() => modal.buildErrorWindow(texts.userProperties.errorMessage));

    function buildPropertiesContainer() {
        return new BEM({
            block: 'user-properties-content',
            elements: {
                'items': buildRowForNewUserProperty(),
                'line': $horizontalLine,
                'properties': $propertiesContainer
            },
        }).buildBlockStructure('<div>');
    }

    setBodyScrollingRule("hidden");
    return modal.buildCreatePropertiesKeyValueModalWindow(buildPropertiesContainer(), confirmed => {
        if (confirmed) {
            const removeUserProperties = mapPropertiesToUserProperties(removeProperties);
            const editUserProperties = mapPropertiesToUserProperties(editProperties);
            const createUserProperties = mapPropertiesToUserProperties(createProperties);

            let totalProperties = {
                deletedProperties: removeUserProperties,
                editedProperties: editUserProperties,
                createdProperties: createUserProperties
            }

            if(removeProperties.length > 0 || editUserProperties.length > 0 || createUserProperties.length > 0) {
                userPropertiesRestAPI.updateAll(totalProperties).done(() => {
                    alert(texts.userProperties.savedSuccess);
                }).fail(() => modal.buildErrorWindow(texts.userProperties.errorMessage));
            }
        }

        setBodyScrollingRule("auto");
    });

    function setBodyScrollingRule(overflowValue) {
        $("body").css("overflow", overflowValue);
    }

    function mapPropertiesToUserProperties(propertiesArray) {
        return Object.getOwnPropertySymbols(propertiesArray)
            .map((key) => {
                const propKey = propertiesArray[key].values[0];
                const propValue = propertiesArray[key].values[1];
                const id = !!propertiesArray[key].values[2] ? propertiesArray[key].values[2].id : null;
                return {
                    id: id,
                    userId: editedUserId,
                    keyName: propKey,
                    value: propValue
                }
            });
    }
}

function mapToPropertyDTO(propertiesData) {
    const $form = $('#user-edit-form');
    const editedUserId = $form[0].id.value;
    return {
        id: propertiesData[2].id,
        userId: editedUserId,
        keyName: propertiesData[0],
        value: propertiesData[1]
    }
}

function onReset() {
    window.location.reload(true);
}

function onCancel() {
    window.close();
}

function onRedirectSuperAdminPage() {
    window.location.replace(imcms.contextPath + "/api/admin/manager")
}

function loadLanguages() {
    const $langSelectContainer = $('#languages-select-container');

    const selectAttributes = {
        text: $langSelectContainer.attr('data-text'),
        name: 'langCode'
    };

    const $select = components.selects.imcmsSelect("<div>", selectAttributes);
    $select.appendTo($langSelectContainer);

    let languages = imcms.availableLanguages.map(lang => ({
        'data-value': lang.code,
        text: lang.name
    }));
    components.selects.addOptionsToSelect(languages, $select, $select.selectValue);
    $select.selectValue(imcms.userLanguage);
}

function bindOnEditClicked($phoneRow) {
    return () => {
        $phoneRow.find('[disabled]')
            .removeAttr('disabled')
            .end()
            .find('.imcms-control')
            .hide()
            .end()
            .find('.imcms-button--save')
            .show()
            .end()
            .find('.imcms-input--phone')
            .focus();
    };
}

function bindOnDeleteClicked($phoneRow) {
    return () => {
        $phoneRow.remove();
    };
}

function bindOnSaveClick($phoneRow) {
    return function () {

        const phoneType = $phoneRow.find('input[name=userPhoneNumberType]').val();
        const phone = $phoneRow.find('input[name=userPhoneNumber]').val().trim();

        if (Number.parseInt(phoneType) === 3 && !isMobilePhoneNumberValid(phone)) {
            alert(texts.superAdmin.users.error.invalidMobilePhoneNumber);
            return;
        }
        $(this).hide();
        $phoneRow.find('.imcms-select,.imcms-input--phone')
            .attr('disabled', 'disabled')
            .end()
            .find('.imcms-control')
            .show();
    };
}

function addPhone(e) {
    e.preventDefault();

    const $phoneInput = $('#phone');
    let phone = $phoneInput.val().trim();

    if (!phone) return;

    $phoneInput.val('');

    const $phoneTypeContainer = $('#phone-type-select').parent();
    const $newRow = $phoneTypeContainer.clone(true, true);

    $newRow.addClass('imcms-text-box--existing-phone-box');

    const $editContainer = $('<div>', {
            html: [components.controls.edit(bindOnEditClicked($newRow)),
                components.controls.remove(bindOnDeleteClicked($newRow))],
            class: 'imcms-phone-edit-buttons'
        });

    const $saveButton = components.buttons.saveButton({
        style: 'display: none;',
        click: bindOnSaveClick($newRow),
        text: texts.save,
        type: 'button'
    });

    const $phoneTypeSelect = $newRow.find('.imcms-label')
        .text('')
        .removeAttr('for')
        .end()
        .find('.imcms-select')
        .attr('disabled', 'disabled')
        .end()
        .find('#phone-type-selected')
        .removeAttr('id')
        .attr('name', 'userPhoneNumberType')
        .end()
        .find('#phone')
        .removeAttr('id')
        .attr('disabled', 'disabled')
        .attr('name', 'userPhoneNumber')
        .val(phone)
        .end()
        .find('#phone-type-select');

    $phoneTypeSelect.removeAttr('id')
        .end()
        .find('#button-add-phone')
        .remove()
        .end()
        .find('.imcms-text-box')
        .append();

    initPhoneNumberTypeImcmsSelect($newRow);

    const phoneType = $phoneTypeSelect.find('input[name=userPhoneNumberType]').val();
    if (Number.parseInt(phoneType) === 3 && !isMobilePhoneNumberValid(phone)) {
        alert(texts.superAdmin.users.error.invalidMobilePhoneNumber);
        return;
    }

    $newRow.append($saveButton, $editContainer).insertAfter($phoneTypeContainer);
    if (Number.parseInt(phoneType) === 3) {
        const $phoneNumberInput = $newRow.find(".imcms-input--phone");
        components.overlays.defaultTooltip($phoneNumberInput, texts.superAdmin.users.tooltip.mobilePhoneNumberTip, {placement: 'bottom'});
    }
}

function filterNonDigits(e) {
    return ((e.ctrlKey || e.altKey || e.metaKey)
        || (/^[0-9()+.,-]+$/g.test(e.key))
        || (e.key === "Backspace")
        || (e.key === "Shift")
        || (e.key && e.key.indexOf && !!~e.key.indexOf("Arrow"))
    );
}

function isMobilePhoneNumberValid(mobilePhoneNumber) {
    return /^\+[1-9]{1}[0-9]{3,14}$/gm.test(mobilePhoneNumber);
}

function onPhoneNumberTypeOptionClick(phoneNumberType) {
    if (Number.parseInt(phoneNumberType) === 3) {
        this.attr({
            "placeholder": "Ex: +46846401211",
        });
        components.overlays.defaultTooltip(this, texts.superAdmin.users.tooltip.mobilePhoneNumberTip, {placement: 'bottom'});
        return;
    }
    this.attr({
        "placeholder": "",
    });
    components.overlays.disable(this);
}

function initPhoneNumberTypeImcmsSelect($row) {
    const $select = $row.find('.imcms-select');
    const $phoneNumberInput = $row.find(".imcms-input--phone");

    components.selects.makeImcmsSelect($select, (phoneNumberType) => {
        onPhoneNumberTypeOptionClick.call($phoneNumberInput, phoneNumberType);
    });
}

$(function () {
    $('input[name=login]').focus();
    loadLanguages();
    unBlockingUser();

    $('.imcms-info-head__close').click(onRedirectSuperAdminPage);

    $('#edit-user-submit-button').click(onSubmit);
    $('#edit-user-reset').click(onReset);
    $('#edit-user-cancel').click(onRedirectSuperAdminPage);
    $('#edit-user-properties').click(onViewUserProperties);
    $('#button-add-phone').click(addPhone);

    if(!imcms.isSuperAdmin) $(`#role-${superAdminRoleId}`).prop("disabled", true);

    $('.imcms-input--phone').keydown(filterNonDigits).on('paste', e => {
        e.preventDefault();
    });


    $('.imcms-text-box--phone-box').each(function () {
        const $row = $(this);

        initPhoneNumberTypeImcmsSelect($row);

        $row.find('.imcms-button--save').click(bindOnSaveClick($row));
        $row.find('.imcms-control--remove').click(bindOnDeleteClicked($row));
        $row.find('.imcms-control--edit').click(bindOnEditClicked($row));
    });
});
