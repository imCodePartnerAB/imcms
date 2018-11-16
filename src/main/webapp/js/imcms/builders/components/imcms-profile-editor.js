define(
    'imcms-profile-editor',
    [
        'imcms-bem-builder', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms-modal-window-builder',
        'imcms-profiles-rest-api', 'imcms-profile-to-row-transformer'
    ],
    function (BEM, components, texts, modal, profileRestApi, profileToRow) {

        texts = texts.superAdmin.profiles;

        let $profileNameRow;

        function buildProfileNameRow() {
            $profileNameRow = components.texts.textBox('<div>', {text: texts.title});
            $profileNameRow.$input.attr('disabled', 'disabled');
            return $profileNameRow;
        }

        function onEditProfile() {

        }

        function prepareProfileView() {

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


        function viewProfile($profileRow, profile) {
            $container.slideDown();
            onProfileView($profileRow, profile);
        }

        function onSimpleEdit($profileRow, profile) {
            viewProfile($profileRow, profile);
            onEditProfile();
        }


        var onEditDelegate = onSimpleEdit;



    }
);