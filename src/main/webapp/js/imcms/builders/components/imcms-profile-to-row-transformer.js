define(
    'imcms-profile-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder'], function (BEM, $, components) {

        var profilesTableBEM = new BEM({
            block: 'profiles-table',
            elements: {
                'profile-row': '',
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
                let $profileName = profilesTableBEM.makeBlockElement('profile-row-item', $("<div>", {
                    id: 'profile-id-' + profile.id,
                    text: profile.name,
                    click: getOnProfileClicked(profile, profileEditor)
                }));
                $profileName.modifiers = ["profile-name"];

                let $profileDocName = profilesTableBEM.makeBlockElement('profile-row-item', $("<div>", {
                    id: 'profile-id-' + profile.id,
                    text: profile.documentName,
                    click: getOnProfileClicked(profile, profileEditor)
                }));
                $profileDocName.modifiers = ["profile-doc-name"];

                let $buttonDelete = components.buttons.closeButton({
                    id: 'profile-id-' + profile.id,
                    click: profileEditor.deleteButton
                });
                $buttonDelete.modifiers = ["button-delete"];

                return new BEM({
                    block: "imcms-profile-row",
                    //click: getOnProfileClicked(profile, profileEditor)
                    elements: {
                        'profile-table-row':
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