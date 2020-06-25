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

function activateUserAdminRoles() {
    texts = texts.languageFlags;
    const $form = $('#user-edit-form');
    const $userAdminRoleIds = $form.find('input[name=userAdminRoleIds]');

    const onUserAdminRoleClicked = function () {
        const $checkbox = $(this);

        if ($checkbox.is(':checked')) {
            $userAdminRoleIds.removeAttr('disabled');

        } else {
            $userAdminRoleIds.removeAttr('checked');
            $userAdminRoleIds.attr('disabled', 'disabled');
        }
    };

    const $userAdminRole = $form.find('#role-1');
    $userAdminRole.click(onUserAdminRoleClicked);

    onUserAdminRoleClicked.call($userAdminRole);
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
    alert(texts.alertInfoLanguage + chooseLang + ')');

    $('[name=userPhoneNumber]').removeAttr('disabled');
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

    languagesRestApi.read()
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
        .attr('name', 'userPhoneNumberType')
        .end()
        .find('#phone')
        .removeAttr('id')
        .attr('disabled', 'disabled')
        .attr('name', 'userPhoneNumber')
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
    $('input[name=login]').focus();
    activateUserAdminRoles();
    loadLanguages();

    components.selects.makeImcmsSelect($('#phone-type-select'));

    $('.imcms-info-head__close').click(onRedirectSuperAdminPage);

    $('#edit-user-submit-button').click(onSubmit);
    $('#edit-user-reset').click(onReset);
    $('#edit-user-cancel').click(onRedirectSuperAdminPage);
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
