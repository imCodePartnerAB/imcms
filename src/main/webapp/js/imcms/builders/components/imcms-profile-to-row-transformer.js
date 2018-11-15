define(
    'imcms-profile-to-row-transformer', ['imcms-bem-builder', 'jquery'], function (BEM, $) {

        var profilesTableBEM = new BEM({
            block: 'profiles-table',
            elements: {
                'profile-row': ''
            }
        });

        return {
            transform: function (profile) {
                return profilesTableBEM.makeBlockElement('profile-row', $('<div>', {
                    id: 'profile-id-' + profile.id,
                    text: profile.name
                }))
            }
        };
    }
);