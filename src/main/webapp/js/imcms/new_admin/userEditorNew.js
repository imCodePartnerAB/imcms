/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 18.06.18
 */

import '../../../imcms/css/imcms_admin.css';
import '../../../css/imcms-imports_files.css';
import '../../../css/imcms-edit-user-page.css';

var $ = require('jquery');
var components = require('imcms-components-builder');
var languagesRestApi = require('imcms-languages-rest-api');
var imcms = require('imcms');

function activateUserAdminRoles() {
    var $form = $('#user-edit-form');
    var $userAdminRoleIds = $form.find('input[name=userAdminRoleIds]');

    var onUserAdminRoleClicked = function () {
        var $checkbox = $(this);

        if ($checkbox.is(':checked')) {
            $userAdminRoleIds.removeAttr('disabled');

        } else {
            $userAdminRoleIds.removeAttr('checked');
            $userAdminRoleIds.attr('disabled', 'disabled');
        }
    };

    var $userAdminRole = $form.find('#role-1');
    $userAdminRole.click(onUserAdminRoleClicked);

    onUserAdminRoleClicked.call($userAdminRole);
}

function onSubmit(e) {
    var $form = $('#user-edit-form');

    if (!$form.find('input[name=login]').val() || !$form.find('#email').val()) {
        e.preventDefault();
        alert($('#must-fill-mandatory-fields-text').val());
        return;
    }

    $('[name=userPhoneNumber]').removeAttr('disabled');
}

function onReset() {
    window.location.reload(true);
}

function onCancel() {
    window.location.href = imcms.contextPath || "/";
}

function loadLanguages() {
    var $langSelectContainer = $('#languages-select-container');

    var selectAttributes = {
        text: $langSelectContainer.attr('data-text'),
        name: 'langCode'
    };

    var $select = components.selects.imcmsSelect("<div>", selectAttributes);
    $select.appendTo($langSelectContainer);

    languagesRestApi.read().done(languages => {

        languages = languages.map(lang => ({
            'data-value': lang.code,
            text: lang.name
        }));

        components.selects.addOptionsToSelect(languages, $select, $select.selectValue);
        $select.selectValue(imcms.userLanguage);
    });
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
    }
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

    var $phoneInput = $('#phone');
    var phone = $phoneInput.val().trim();

    if (!phone) return;

    $phoneInput.val('');

    var $phoneTypeContainer = $('#phone-type-select').parent();
    var $newRow = $phoneTypeContainer.clone(true, true);

    $newRow.addClass('imcms-text-box--existing-phone-box');

    var $editPhoneButton = components.controls.edit(bindOnEditClicked($newRow));
    var $deletePhoneButton = components.controls.remove(bindOnDeleteClicked($newRow));
    var $saveButton = components.buttons.saveButton({
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

    $('#edit-user-submit-button').click(onSubmit);
    $('#edit-user-reset').click(onReset);
    $('#edit-user-cancel').click(onCancel);
    $('#button-add-phone').click(addPhone);

    $('.imcms-input--phone').keydown(filterNonDigits).on('paste', e => {
        e.preventDefault();
    });


    $('.imcms-text-box--existing-phone-box').each(function () {
        var $row = $(this);

        components.selects.makeImcmsSelect($row.find('.imcms-select'));

        $row.find('.imcms-button--save').click(bindOnSaveClick($row));
        $row.find('.imcms-control--remove').click(bindOnDeleteClicked($row));
        $row.find('.imcms-control--edit').click(bindOnEditClicked($row));
    });
});
