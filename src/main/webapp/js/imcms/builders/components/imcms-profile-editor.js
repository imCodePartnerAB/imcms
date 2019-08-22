define(
    'imcms-profile-editor',
    [
        'imcms-bem-builder', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms-modal-window-builder',
        'imcms-profiles-rest-api', 'imcms-profile-to-row-transformer'
    ],
    function (BEM, components, texts, modal, profileRestApi, profileToRow) {

        texts = texts.superAdmin.profiles;

        let $profileNameRow;
        let $profileDocNameRow;
        let $profileEditButtons;

        function buildProfileNameRow() {
            $profileNameRow = components.texts.textBox('<div>', {
                text: texts.editProfile.name
            });
            $profileNameRow.$input.attr('enabled', 'enabled');
            return $profileNameRow;
        }

        function buildProfileDocNameRow() {
            $profileDocNameRow = components.texts.textBox('<div>', {
                text: texts.editProfile.docName,
            });
            $profileDocNameRow.$input.attr('enabled', 'enabled');
            return $profileDocNameRow;
        }

        let errorMsg;

        function buildErrorBlock() {
            errorMsg = components.texts.errorText("<div>", texts.error.errorMessage, {style: 'display: none;'});
            return errorMsg;
        }

        function onCancelChanges($profileRowElement, profile) {

            getOnDiscardChanges(() => {
                onProfileView = onProfileSimpleView;
                currentProfile = profile;
                $profileRow = $profileRowElement;
                prepareProfileView();
            }).call();
        }

        function onEditProfile() {
            onProfileView = onCancelChanges;

            $profileEditButtons.slideDown();

            $profileNameRow.$input.focus();
            $profileDocNameRow.$input.focus();
        }

        function onDeleteProfile() {
            modal.buildModalWindow(texts.warnDelete, confirmed => {
                if (!confirmed) return;

                profileRestApi.remove(currentProfile)
                    .done(() => {
                        $profileRow.remove();
                        currentProfile = null;
                        onEditDelegate = onSimpleEdit;
                        $container.slideUp();
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.removeFailed));
            });
        }

        function onSaveProfile() {

            let name = $profileNameRow.getValue();
            let docName = $profileDocNameRow.getValue();

            if (!name && !docName) {
                $profileNameRow.$input.focus();
                $profileDocNameRow.$input.focus();
                return;
            }

            let currentProfileToSave = {
                id: currentProfile.id,
                name: name,
                documentName: docName
            };

            if (currentProfileToSave.id) {
                profileRestApi.replace(currentProfileToSave).done(savedProfile => {
                    currentProfile = savedProfile;
                    $profileRow.find('.profile-info-row__profile-name').text(currentProfile.name);
                    $profileRow.find('.profile-info-row__profile-doc-name').text(currentProfile.documentName);
                    onProfileView = onProfileSimpleView;
                    prepareProfileView();
                }).fail(() => {
                    errorMsg.css('display', 'inline-block').slideDown();
                });
            } else {
                profileRestApi.create(currentProfileToSave)
                    .done(profile => {
                        $profileRow = profileToRow.transform((currentProfile = profile), profileEditor);

                        $container.parent().find('.profiles-table').append($profileRow);

                        onProfileView = onProfileSimpleView;
                        prepareProfileView();
                    })
                    .fail(() => {
                        errorMsg.css('display', 'inline-block').slideDown();
                    });
            }
        }

        function getOnDiscardChanges(onConfirm) {
            return () => {
                modal.buildModalWindow(texts.warnChangeMessage, confirmed => {
                    if (!confirmed) return;
                    onConfirm.call();
                });
            }
        }

        function getOnWarnCancel(onConfirm) {
            return () => {
                modal.buildModalWindow(texts.warnCancelMessage, confirmed => {
                    if (!confirmed) return;
                    onConfirm.call();
                })
            }
        }

        function buildProfileEditButtons() {
            return $profileEditButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.negativeButton({
                    text: texts.cancel,
                    click: getOnWarnCancel(() => {
                        onProfileView = onProfileSimpleView;

                        if (currentProfile.id) {
                            prepareProfileView();
                            $container.slideUp();

                        } else {
                            currentProfile = null;
                            onEditDelegate = onSimpleEdit;
                            $container.slideUp();
                        }
                    })
                }),
                components.buttons.saveButton({
                    text: texts.createNewProfile.buttonSave,
                    click: onSaveProfile
                }),
            ]);
        }

        function prepareProfileView() {
            onEditDelegate = onSimpleEdit;

            $profileRow.parent()
                .find('.profiles-table__profile-row--active')
                .removeClass('profiles-table__profile-row--active');

            $profileRow.addClass('profiles-table__profile-row--active');

            $profileNameRow.$input.attr('enabled', 'enabled');
            $profileNameRow.setValue(currentProfile.name);

            $profileDocNameRow.$input.attr('enabled', 'enabled');
            $profileDocNameRow.setValue(currentProfile.documentName);

            $profileEditButtons.slideDown('fast');

            $container.css('display', 'inline-block');
            errorMsg.css('display', 'none').slideUp();
        }

        function onProfileSimpleView($profileRowElement, profile) {
            if (currentProfile && currentProfile.id === profile.id) return;
            currentProfile = profile;
            $profileRow = $profileRowElement;

            prepareProfileView();
        }

        var $container;
        var currentProfile;
        var $profileRow;
        var onProfileView = onProfileSimpleView;


        function buildProfilesContainer() {
            return $container || ($container = new BEM({
                block: 'profiles-editor',
                elements: {
                    'profile-name-row': buildProfileNameRow(),
                    'profile-doc-name-row': buildProfileDocNameRow(),
                    'error-row': buildErrorBlock(),
                    'profile-button-edit': buildProfileEditButtons()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'}));
        }

        function viewProfile($profileRow, profile) {
            $container.slideDown();
            onProfileView($profileRow, profile);
        }

        var onEditDelegate = onSimpleEdit;

        function onSimpleEdit($profileRow, profile) {
            viewProfile($profileRow, profile);
            onEditProfile();
        }

        function editProfile($profileRow, profile) {
            onEditDelegate($profileRow, profile);
            onEditDelegate = () => {
            }
        }

        var profileEditor = {
            buildProfilesContainer: buildProfilesContainer,
            viewProfile: viewProfile,
            editProfile: editProfile,
            deleteButton: onDeleteProfile
        };

        return profileEditor;
    }
);