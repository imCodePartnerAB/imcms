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

                if ($this.hasClass('profiles-table__profile-row--active')) return;

                profileEditor.viewProfile($this, profile);
            }
        }

        return {
            transform: function (profile, profileEditor) {

                let infoRowAttributes = {
                    id: 'profile-id-' + profile.id,
                    click: getOnProfileClicked(profile, profileEditor)
                };

                let $profileName = profilesTableBEM.makeBlockElement('profile-name', $("<span>", {
                    text: profile.name
                }));

                let $profileDocName = profilesTableBEM.makeBlockElement('profile-doc-name', $("<span>", {
                    text: profile.documentName
                }));

                let $buttonDelete = components.buttons.closeButton({
                    click: profileEditor.deleteButton
                });
                $buttonDelete.modifiers = ["delete"];

                return new BEM({
                    block: "field-profile-row",
                    elements: {
                        '':
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