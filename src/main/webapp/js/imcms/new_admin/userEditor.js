/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 18.06.18
 */
Imcms.require(
    ['jquery', 'imcms-components-builder', 'imcms-uuid-generator', 'imcms-languages-rest-api', 'imcms'],
    function ($, components, uuid, languagesRestApi, imcms) {
        function activateUserAdminRoles() {
            var $form = $('#user-edit-form');
            var $userAdminRoleIds = $form.find('input[name=userAdminRoleIds]');

            if (!$userAdminRoleIds.length) return;

            $userAdminRoleIds.attr('disabled', 'disabled');

            var isUserAdminSelected = $form.find('input[name=roleIds]')
                .find("option")
                .filter(function () {
                    var $option = $(this);
                    return ($option.text() === 'Useradmin' && $option.is(':selected'));
                })
                .length;

            if (isUserAdminSelected) {
                $userAdminRoleIds.removeAttr('disabled');
            }
        }

        function onSubmit(e) {
            var $form = $('#user-edit-form');

            if ($form.find('input[name=loginName]').val() === "") {
                e.preventDefault();
                alert($('#must-fill-mandatory-fields-text').val());
                return;
            }

            var $pass1 = $form.find('input[name=password1]');
            var $pass2 = $form.find('input[name=password2]');

            if ($pass1.val() === $pass2.val()) {
                $('[name=userPhoneNumber]').removeAttr('disabled');
                return;
            }

            e.preventDefault();
            $pass2.val("");
            $pass1.val("").focus();
            alert($('#pass-verification-failed-text').val());
        }

        function loadLanguages() {
            var $langSelectContainer = $('#languages-select-container');

            var selectAttributes = {
                text: $langSelectContainer.attr('data-text'),
                name: 'langCode'
            };

            var $select = components.selects.imcmsSelect("<div>", selectAttributes);
            $select.appendTo($langSelectContainer);

            languagesRestApi.read().done(function (languages) {

                languages = languages.map(function (lang) {
                    return {
                        'data-value': lang.code,
                        text: lang.name
                    }
                });

                components.selects.addOptionsToSelect(languages, $select, $select.selectValue);
                $select.selectValue(imcms.userLanguage);
            });
        }

        function bindOnEditClicked($phoneRow) {
            return function () {
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
            return function () {
                $phoneRow.detach();
            }
        }

        function bindOnSaveClick($phoneRow) {
            return function () {
                $(this).hide();

                $phoneRow.find('.imcms-select,.imcms-input--phone')
                    .attr('disabled', 'disabled')
                    .end()
                    .find('.imcms-control')
                    .show();
            }
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
                .detach()
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
            $('input[name=loginName]').focus();
            activateUserAdminRoles();
            loadLanguages();

            components.selects.makeImcmsSelect($('#phone-type-select'));

            $('#select-role-ids').change(activateUserAdminRoles);
            $('#edit-user-submit-button').click(onSubmit);
            $('#button-add-phone').click(addPhone);

            $('.imcms-input--phone').keydown(filterNonDigits).on('paste', function (e) {
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
    }
);
