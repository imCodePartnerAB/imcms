import '../../css/imcms_admin.css';
import '../../css/imcms-imports_files.css';
import '../../css/imcms-edit-user-page.css';

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 18.06.18
 */
const $ = require('jquery');
const components = require('imcms-components-builder');
const imcms = require('imcms');
const modal = require("imcms-modal-window-builder");
let texts = require("imcms-i18n-texts");

const superAdminRoleId = 1;

function onSubmit(e) {
    const $form = $('#user-edit-form');
    const $pass1 = $form.find('input[name=password]');
    const $pass2 = $form.find('input[name=password2]');

    if (!$form.find('input[name=login]').val()
        || !$pass1.val()
        || !$pass2.val()) {
        e.preventDefault();
        alert($('#must-fill-mandatory-fields-text').val());
        return;
    }

    if ($pass1.val() === $pass2.val()) {
        $('[name=userPhoneNumber]').removeAttr('disabled');
        return;
    }

    e.preventDefault();
    $pass2.val("");
    $pass1.val("").focus();
    alert($('#pass-verification-failed-text').val());
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

    let languages = imcms.availableAdminLanguages.map(lang => ({
        'data-value': lang.code,
        text: lang.name
    }));
    components.selects.addOptionsToSelect(languages, $select, $select.selectValue);
    $select.selectValue(imcms.defaultAdminLanguage.code);
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

    $('.imcms-info-head__close').click(onRedirectSuperAdminPage);

    $('#edit-user-submit-button').click(onSubmit);
    $('#edit-user-reset').click(onReset);
    $('#edit-user-cancel').click(onRedirectSuperAdminPage);
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
