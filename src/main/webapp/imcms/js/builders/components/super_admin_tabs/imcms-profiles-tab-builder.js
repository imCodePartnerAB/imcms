define(
    'imcms-profiles-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-bem-builder', 'imcms-field-wrapper',
        'jquery', 'imcms-profiles-rest-api', 'imcms-profile-to-row-transformer', 'imcms-profile-editor', 'imcms-modal-window-builder'
    ],
    function (SuperAdminTab, texts, components, BEM, fieldWrapper, $, profileRestApi, profileToRow, profileEditor, modal) {

        texts = texts.superAdmin.profiles;

        const profilesLoader = {
            profiles: false,
            callback: [],
            whenProfilesLoaded: function (callback) {
                (this.profiles) ? callback(this.profiles) : this.callback.push(callback);
            },
            runCallbacks: function (profiles) {
                this.profiles = profiles;

                this.callback.forEach(callback => {
                    callback(profiles);
                });
            }
        };

        profileRestApi.read()
            .done(profiles => {
                profilesLoader.runCallbacks(profiles);
            })
            .fail(() => modal.buildErrorWindow(texts.error.loadFailed));

        let $profileContainer;

        function buildTitleText() {
            return fieldWrapper.wrap(components.texts.titleText('<div>', texts.title))
        }

        function buildCreateButton() {
            return fieldWrapper.wrap(components.buttons.positiveButton({
                text: texts.createButton,
                click: onCreateNewProfile
            }));
        }

        function onCreateNewProfile() {
            $profileContainer.find('.profiles-table__profile-row--active')
                .removeClass('profiles-table__profile-row--active');

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

            profilesLoader.whenProfilesLoaded(profiles => {
                $profileContainer.append(buildTitleRow());
                $profileContainer.append(profiles.map(profile => profileToRow.transform(profile, profileEditor)));
            });

            return fieldWrapper.wrap([$profileContainer, profileEditor.buildProfilesContainer()]);
        }

        function buildTitleRow() {
            let $titleRow = new BEM({
                block: 'title-profile-row',
                elements: {
                    'name': $('<div>', {text: texts.titleTextName}),
                    'doc-name': $('<div>', {text: texts.titleTextDocName})
                }
            }).buildBlockStructure('<div>', {
                'class': 'table-title'
            });
            return $titleRow;
        }

        const ProfilesAdminTab = function (name, tabElements) {
            SuperAdminTab.call(this, name, tabElements);
        };

        ProfilesAdminTab.prototype = Object.create(SuperAdminTab.prototype);

        ProfilesAdminTab.prototype.getDocLink = () => texts.documentationLink;

        return new ProfilesAdminTab(texts.name, [
            buildTitleText(),
            buildCreateButton(),
            buildProfileContainer()
        ]);
    }
);