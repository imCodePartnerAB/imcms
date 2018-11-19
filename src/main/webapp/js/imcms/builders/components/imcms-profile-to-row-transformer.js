define(
    'imcms-profile-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder'], function (BEM, $, components) {

        var profilesTableBEM = new BEM({
            block: 'profiles-table',
            elements: {
                'profile-row': '',
                'profile-delete-button': ''
            }
        });

        let $profileDeleteButtons;

        function getOnProfileClicked(profile, profileEditor) {
            return function () {
                var $this = $(this);

                if ($this.hasClass('profile-table__profile-row--active')) return;

                profileEditor.viewProfile($this, profile);
            }
        }

        function getOnDeleteClicked(profile, profileEditor) {
            return function () {
                var $this = $(this);

                if ($this.hasClass('profile-table__profile-row--active')) return;

                profileEditor.deleteButton($this, profile);
            }
        }

        function buildProfileDeleteButtons() {
            return $profileDeleteButtons = components.buttons.closeButton({
                name: 'delete',
                click: function () {

                }
            });
        }

        return {
            transform: function (profile, profileEditor) {
                let $profileName = profilesTableBEM.makeBlockElement('profile-row', $("<div>", {
                    id: 'profile-id-' + profile.id,
                    text: profile.name,
                    click: getOnProfileClicked(profile, profileEditor)
                }));
                $profileName.modifiers = ["profileName"];

                let $profileDocName = profilesTableBEM.makeBlockElement('profile-row', $("<div>", {
                    id: 'profile-id-' + profile.id,
                    text: profile.documentName,
                    click: getOnProfileClicked(profile, profileEditor)
                }));
                $profileDocName.modifiers = ["profileDocName"];

                let $buttonDelete = components.buttons.closeButton({
                    id: 'profile-id-' + profile.id,
                    click: function () {
                    }
                });
                $buttonDelete.modifiers = ["buttonDelete"];

                return new BEM({
                    block: "imcms-profiles-list",
                    elements: {
                        'profile-name-docName':
                            [
                                $profileName,
                                $profileDocName,
                                $buttonDelete
                            ]
                    }
                }).buildBlockStructure("<div>");
            }
        }
    }
);