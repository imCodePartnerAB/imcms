define(
    'imcms-profile-to-row-transformer', ['imcms-bem-builder', 'jquery'], function (BEM, $) {

        var profilesTableBEM = new BEM({
            block: 'profiles-table',
            elements: {
                'profile-row': ''
            }
        });

        function getOnProfileClicked(profile, profileEditor) {
            return function () {
                var $this = $(this);

                if ($this.hasClass('profile-table__profile-row--active')) return;

                profileEditor.viewProfile($this, profile);
            }
        }

        return {
            transform: function (profile, profileEditor) {
                return profilesTableBEM.makeBlockElement('profile-row', $('<div>', {
                    id: 'profile-id-' + profile.id,
                    text: profile.name + ' ' + profile.documentName,
                    click: getOnProfileClicked(profile, profileEditor)
                }))
            }
        };
    }
);