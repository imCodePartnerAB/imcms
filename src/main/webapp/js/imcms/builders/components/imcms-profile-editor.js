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
        let $profileViewButtons;
        let $profileEditButtons;

        function buildProfileNameRow() {
            $profileNameRow = components.texts.textBox('<div>', {text: texts.editProfile.name});
            $profileNameRow.$input.attr('disabled', 'disabled');
            return $profileNameRow;
        }

        function buildProfileDocNameRow() {
            $profileDocNameRow = components.texts.textBox('<div>', {text: texts.editProfile.docName});
            $profileDocNameRow.$input.attr('disabled', 'disabled');
            return $profileDocNameRow;
        }

        function onCancelChanges($profileRowElement, profile) {

            getOnDiscardChanges(function () {
                onProfileView = onProfileSimpleView;
                currentProfile = profile;
                $profileRow = $profileRowElement;
                prepareProfileView();
            }).call();
        }

        function onEditProfile() {
            onProfileView = onCancelChanges;

            $profileViewButtons.slideUp();
            $profileEditButtons.slideDown();

            $profileNameRow.$input.removeAttr('disabled').focus();
            $profileDocNameRow.$input.removeAttr('disabled').focus();
        }

        function onDeleteRole() {
            modal.buildModalWindow(texts.warnDelete, function (confirmed) {
                if (!confirmed) return;

                profileRestApi.remove(currentProfile).done(function () {
                    $profileRow.remove();
                    currentProfile = null;
                    onEditDelegate = onSimpleEdit;
                    $container.slideUp();
                })
            });
        }


        function buildProfileViewButtons() {
            return $profileViewButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.positiveButton({
                    text: texts.editProfile.buttonEdit,
                    click: onEditProfile
                }),
                components.buttons.negativeButton({
                    text: texts.editProfile.buttonDelete,
                    click: onDeleteRole
                })
            ]);
        }

        function getOnDiscardChanges(onConfirm) {
            return function () {
                modal.buildModalWindow(texts.warnChangeMessage, function (confirmed) {
                    if (!confirmed) return;
                    onConfirm.call();
                });
            }
        }

        function buildProfileEditButtons() {
            return $profileEditButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.saveButton({
                    text: texts.createNewProfile.buttonSave,
                    click: function () {

                    }
                }),
                components.buttons.negativeButton({
                    text: texts.cancel,
                    click: getOnDiscardChanges(function () {
                        onProfileView = onProfileSimpleView;

                        if (currentProfile.id) {
                            prepareProfileView();

                        } else {
                            currentProfile = null;
                            onEditDelegate = onSimpleEdit;
                            $container.slideUp();
                        }
                    })
                })
            ], {
                style: 'display: none;'
            });
        }

        function prepareProfileView() {
            onEditDelegate = onSimpleEdit;

            $profileRow.parent()
                .find('.profile-table__profile-row--active')
                .removeClass('profile-table__profile-row--active');

            $profileRow.addClass('profile-table__profile-row--active');

            $profileNameRow.$input.attr('disabled', 'disabled');
            $profileNameRow.setValue(currentProfile.name);

            $profileDocNameRow.$input.attr('disabled', 'disabled');
            $profileDocNameRow.setValue(currentProfile.documentName);

            $container.css('display', 'inline-block');
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
                    'profile-docName-row': buildProfileDocNameRow(),
                    'profile-button-view': buildProfileViewButtons(),
                    'profile-button-edir': buildProfileEditButtons()
                }
            }).buildBlockStructure('<div>', {style: 'display: none;'}));
        }

        function viewProfile($profileRow, profile) {
            $container.slideDown();
            onProfileView($profileRow, profile);
        }

        function onSimpleEdit($profileRow, profile) {
            viewProfile($profileRow, profile);
            onEditProfile();
        }


        var onEditDelegate = onSimpleEdit;

        function editProfile($profileRow, profile) {
            onEditDelegate($profileRow, profile);
            onEditDelegate = function () {
            }
        }

        var profileEditor = {
            buildProfilesContainer: buildProfilesContainer,
            viewProfile: viewProfile,
            editProfile: editProfile
        };

        return profileEditor;


    }
);