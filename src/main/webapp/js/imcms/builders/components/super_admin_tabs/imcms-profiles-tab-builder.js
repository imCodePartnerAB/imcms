define(
    'imcms-profiles-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-bem-builder', 'imcms-field-wrapper',
        'jquery', 'imcms-profiles-rest-api', 'imcms-profile-to-row-transformer', 'imcms-profile-editor'],
    function (SuperAdminTab, texts, components, BEM, fieldWrapper, $, profileRestApi, profileToRow, profileEditor) {

        texts = texts.superAdmin.profiles;

        var profilesLoader = {
            profiles: false,
            callback: [],
            whenProfilesLoaded: function (callback) {
                (this.profiles) ? callback(this.profiles) : this.callback.push(callback);

            },
            runCallbacks: function (profiles) {
                this.profiles = profiles;

                this.callback.forEach(function (callback) {
                    callback(profiles)
                })
            }
        };

        profileRestApi.getAllProfiles().done(function (profiles) {
            profilesLoader.runCallbacks(profiles);
        });

        let $profileContainer;

        function buildBlockProfiles() {

            function createTitleText() {
                return components.texts.titleText('<div>', texts.title, {})
            }

            function createButtonCreate() {
                return fieldWrapper.wrap(components.buttons.positiveButton({
                    text: texts.createButton,
                    click: onCreateNewProfile
                }));
            }

            function buildCreateTitlesForProfiles() {
                return components.texts.titleText('<div>',
                    texts.createNewProfile.titleTextName + '|' + texts.createNewProfile.titleTextDocName, {})
            }

            return new BEM({
                block: 'imcms-profiles-block',
                elements: {
                    'profile-title': createTitleText(),
                    'create-button': createButtonCreate(),
                    'title-table-profiles': buildCreateTitlesForProfiles()
                }
            }).buildBlockStructure('<div>');
        }

        function onCreateNewProfile() {
            $profileContainer.find('.profile-table__profile-row--active')
                .removeClass('profile-table__profile-row--active');

            profileEditor.editProfile($('<div>'), {
                id: null,
                name: '',
                documentName: ''
            });
        }

        function buildProfileContainer() {

            $profileContainer = $('<div>', {
                'class': 'profiles-table'
            });

            profilesLoader.whenProfilesLoaded(function (profiles) {
                $profileContainer.append(profiles.map(function (profile) {
                    return profileToRow.transform(profile, profileEditor);
                }))
            });

            return fieldWrapper.wrap([$profileContainer, profileEditor.buildProfilesContainer()]);
        }

        return new SuperAdminTab(texts.name, [
            buildBlockProfiles(),
            buildProfileContainer(),
        ]);
    }
);