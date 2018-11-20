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
            $profileNameRow = components.texts.textBox('<div>', {text: texts.editProfile.name});
            $profileNameRow.$input.attr('enable', 'enable');
            return $profileNameRow;
        }

        function buildProfileDocNameRow() {
            $profileDocNameRow = components.texts.textBox('<div>', {text: texts.editProfile.docName});
            $profileDocNameRow.$input.attr('enable', 'enable');
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

            $profileEditButtons.slideDown();

            $profileNameRow.$input.focus();
            $profileDocNameRow.$input.focus();
        }

        function onDeleteProfile() {
            modal.buildModalWindow(texts.warnDelete, function (confirmed) {
                if (!confirmed) return;

                profileRestApi.deleteById(currentProfile).done(function () {
                    $profileRow.remove();
                    currentProfile = null;
                    onEditDelegate = onSimpleEdit;
                    $container.slideUp();
                })
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

            let saveEntity = {
                id: currentProfile.id,
                name: name,
                documentName: docName
            };

            if (saveEntity.id) {
                profileRestApi.update(saveEntity).done(function (savedProfile) {
                    currentProfile.id = savedProfile.id;
                    $profileRow.textBox(currentProfile.name = savedProfile.name,
                        currentProfile.documentName = savedProfile.documentName);
                    onProfileView = onProfileSimpleView;
                    prepareProfileView();
                });
            } else {
                profileRestApi.create(saveEntity).done(function (profile) {
                    $profileRow = profileToRow.transform((currentProfile = profile), profileEditor);
                    $container.parent().find('.profile-table').append($profileRow);

                    onProfileView = onProfileSimpleView;
                    prepareProfileView();
                });
            }
        }

        function getOnDiscardChanges(onConfirm) {
            return function () {
                modal.buildModalWindow(texts.warnChangeMessage, function (confirmed) {
                    if (!confirmed) return;
                    onConfirm.call();
                });
            }
        }

        function getOnWarnCancel(onConfirm) {
            return function () {
                modal.buildModalWindow(texts.warnCancelMessage, function (confirmed) {
                    if (!confirmed) return;
                    onConfirm.call();
                })
            }
        }

        function buildProfileEditButtons() {
            return $profileEditButtons = components.buttons.buttonsContainer('<div>', [
                components.buttons.saveButton({
                    text: texts.createNewProfile.buttonSave,
                    click: onSaveProfile
                }),
                components.buttons.negativeButton({
                    text: texts.cancel,
                    click: getOnWarnCancel(function () {
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
            ], {});
        }

        function prepareProfileView() {
            onEditDelegate = onSimpleEdit;

            $profileRow.parent()
                .find('.profile-table__profile-row--active')
                .removeClass('profile-table__profile-row--active');

            $profileRow.addClass('profile-table__profile-row--active');

            $profileNameRow.$input.attr('enable', 'enable');
            $profileNameRow.setValue(currentProfile.name);

            $profileDocNameRow.$input.attr('enable', 'enable');
            $profileDocNameRow.setValue(currentProfile.documentName);

            $profileEditButtons.slideDown('fast');

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
                    'profile-doc-name-row': buildProfileDocNameRow(),
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
            onEditDelegate = function () {
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