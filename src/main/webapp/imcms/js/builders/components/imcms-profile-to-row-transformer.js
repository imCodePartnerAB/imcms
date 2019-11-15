define(
    'imcms-profile-to-row-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder'],
    function (BEM, $, components) {
        function getOnProfileClicked(profile, profileEditor) {
            return function () {
                const $this = $(this);

                if ($this.hasClass('profiles-table__profile-row--active')) return;

                profileEditor.viewProfile($this, profile);
            }
        }

        return {
            transform: (profile, profileEditor) => {

                let infoRowAttributes = {
                    id: 'profile-id-' + profile.id,
                    click: getOnProfileClicked(profile, profileEditor)
                };

                return new BEM({
                    block: "profile-info-row",
                    elements: {
                        'profile-name': $('<div>', {
                            text: profile.name
                        }),
                        'profile-doc-name': $('<div>', {
                            text: profile.documentName
                        }),
                        'delete': components.controls.remove(profileEditor.deleteButton)
                    }
                }).buildBlockStructure("<div>", infoRowAttributes);
            }
        }
    }
);