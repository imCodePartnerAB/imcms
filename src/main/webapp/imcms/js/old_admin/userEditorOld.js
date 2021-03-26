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
const modal = require('imcms-modal-window-builder');
let texts = require('imcms-i18n-texts');

function activateUserAdminRoles() {
    texts = texts.languageFlags;
    const $form = $('#user-edit-form');
    const $userAdminRoleIds = $form.find('input[name=user_admin_role_ids]');

    if (!$userAdminRoleIds.length) return;

    $userAdminRoleIds.attr('disabled', 'disabled');

    const isUserAdminSelected = $form.find('input[name=role_ids]')
        .find("option")
        .filter(function () {
            const $option = $(this);
            return ($option.text() === 'Useradmin' && $option.is(':selected'));
        })
        .length;

    if (isUserAdminSelected) {
        $userAdminRoleIds.removeAttr('disabled');
    }
}

function onCancel() {
    window.history.back()
}

function onReset() {
    window.location.reload(true)
}

function onSubmit(e) {
    const $form = $('#user-edit-form');

    if ($form.find('input[name=login_name]').val() === "") {
        e.preventDefault();
        alert($('#must-fill-mandatory-fields-text').val());
        return;
    }

    const $pass1 = $form.find('input[name=password1]');
    const $pass2 = $form.find('input[name=password2]');

    if ($pass1.val() === $pass2.val()) {
        $('[name=user_phone_number]').removeAttr('disabled');
        return;
    }

    e.preventDefault();
    $pass2.val("");
    $pass1.val("").focus();
    alert($('#pass-verification-failed-text').val());
}

function loadLanguages() {
    const $langSelectContainer = $('#languages-select-container');

    const selectAttributes = {
        text: $langSelectContainer.attr('data-text'),
        name: 'lang_id'
    };

    const $select = components.selects.imcmsSelect("<div>", selectAttributes);
    $select.appendTo($langSelectContainer);

    languagesRestApi.getAvailableLangs()
        .done(languages => {
            languages = languages.map(lang => ({
                'data-value': lang.code,
                text: lang.name
            }));

            components.selects.addOptionsToSelect(languages, $select, $select.selectValue);
            $select.selectValue(imcms.userLanguage);
        })
        .fail(() => modal.buildErrorWindow(texts.error.loadFailed));
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

    const $editPhoneButton = components.controls.edit(bindOnEditClicked($newRow));
    const $deletePhoneButton = components.controls.remove(bindOnDeleteClicked($newRow));
    const $saveButton = components.buttons.saveButton({
        style: 'display: none;',
        click: bindOnSaveClick($newRow),
        text: 'Save'
    });

    $newRow.find('.imcms-label')
        .text('')
        .removeAttr('for')
        .end()
        .find('.imcms-select')
        .attr('disabled', 'disabled')
        .end()
        .find('#phone-type-selected')
        .removeAttr('id')
        .attr('name', 'user_phone_number_type')
        .end()
        .find('#phone')
        .removeAttr('id')
        .attr('disabled', 'disabled')
        .attr('name', 'user_phone_number')
        .val(phone)
        .end()
        .find('#phone-type-select')
        .removeAttr('id')
        .end()
        .find('#button-add-phone')
        .remove()
        .end()
        .find('.imcms-text-box')
        .append();

    $newRow.append($saveButton, $deletePhoneButton, $editPhoneButton).insertAfter($phoneTypeContainer);
}

function filterNonDigits(e) {
    return ((e.ctrlKey || e.altKey || e.metaKey)
        || (/^[0-9()+.,-]+$/g.test(e.key))
        || (e.key === "Backspace")
        || (e.key === "Shift")
        || (e.key && e.key.indexOf && !!~e.key.indexOf("Arrow"))
    );
}

$(function () {
    $('input[name=login_name]').focus();
    activateUserAdminRoles();
    loadLanguages();

    components.selects.makeImcmsSelect($('#phone-type-select'));

    $('#select-role-ids').change(activateUserAdminRoles);
    $('#edit-user-cancel').click(onCancel);
    $('#edit-user-reset').click(onReset);
    $('#edit-user-submit-button').click(onSubmit);
    $('#button-add-phone').click(addPhone);

    $('.imcms-input--phone').keydown(filterNonDigits).on('paste', e => {
        e.preventDefault();
    });

    $('.imcms-text-box--existing-phone-box').each(function () {
        const $row = $(this);

        components.selects.makeImcmsSelect($row.find('.imcms-select'));

        $row.find('.imcms-button--save').click(bindOnSaveClick($row));
        $row.find('.imcms-control--remove').click(bindOnDeleteClicked($row));
        $row.find('.imcms-control--edit').click(bindOnEditClicked($row));
    });
});
