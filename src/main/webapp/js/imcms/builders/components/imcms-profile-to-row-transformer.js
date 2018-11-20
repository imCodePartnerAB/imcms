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

                let infoRowAttributes = {
                    id: 'profile-id-' + profile.id,
                    click: getOnProfileClicked(profile, profileEditor)
                };

                let $profileName = profilesTableBEM.makeBlockElement('profile-row-item', $("<span>", {
                    text: profile.name
                }));
                $profileName.modifiers = ["profile-name"];

                let $profileDocName = profilesTableBEM.makeBlockElement('profile-row-item', $("<span>", {
                    text: profile.documentName
                }));
                $profileDocName.modifiers = ["profile-doc-name"];

                let $buttonDelete = components.buttons.closeButton({
                    click: profileEditor.deleteButton
                });
                $buttonDelete.modifiers = ["button-delete"];

                return new BEM({
                    block: "imcms-profile-row",
                    elements: {
                        'profile-table-row':
                            [
                                $profileName,
                                $profileDocName,
                                $buttonDelete
                            ]
                    }
                }).buildBlockStructure("<div>", infoRowAttributes);
            }
        }
    }
);