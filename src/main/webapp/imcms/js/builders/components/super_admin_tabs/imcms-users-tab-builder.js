/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-users-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-i18n-texts', 'jquery', 'imcms-bem-builder', 'imcms-components-builder',
        'imcms-roles-rest-api', 'imcms-users-rest-api', 'imcms', 'imcms-modal-window-builder'
    ],
    function (SuperAdminTab, texts, $, BEM, components, rolesRestApi, usersRestApi, imcms, modal) {

        texts = texts.superAdmin.users;

        let $searchResultContainer;
        const userArchivedClass = 'imcms-user-info-row--archived';
        let $usersNameFilter;
        let $includeInactiveCheckbox;
        let $usersFilterSelect;

        function buildSearchRow() {

            function buildUsersNameFilter() {
                const $usersNameFilterBox = components.texts.textBox('<div>', texts.searchFilter.byName);
                $usersNameFilter = $usersNameFilterBox.$input.attr("maxLength", 250);
                return $usersNameFilterBox;
            }

            function buildUsersRoleFilter() {
                $usersFilterSelect = components.selects.multipleSelect('<div>', {
                    id: 'users-role-filter',
                    name: 'users-role-filter',
                    text: texts.searchFilter.byRole.title
                });

                rolesRestApi.read()
                    .done(roles => {
                        const rolesDataMapped = roles.map(role => ({
                            text: role.name,
                            value: role.id
                        }));

                        components.selects.addOptionsToMultiSelect(rolesDataMapped, $usersFilterSelect);
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.loadRolesFailed));

                return $usersFilterSelect;
            }

            function includeInactiveUsers() {
            }

            function buildUsersActivityFilter() {
                return $includeInactiveCheckbox = components.checkboxes.imcmsCheckbox('<div>', {
                    text: texts.searchFilter.inactiveUsers,
                    id: 'include-inactive-users',
                    click: includeInactiveUsers
                });
            }

            function getOnEditUser(user) {
                return () => {
                    window.open(imcms.contextPath + '/api/user/edition/' + user.id, '_self').focus();
                };
            }

            function getOnArchiveUser(user) {
                return () => {
                    usersRestApi.update({id: user.id, active: false})
                        .done(() => {
                            $('#user-id-' + user.id).addClass(userArchivedClass)
                                .find('.imcms-control--archive')
                                .replaceWith($('<div>', {
                                    'class': 'imcms-user-info-row__archive',
                                    text: texts.searchResult.archived
                                }));
                        })
                        .fail(() => modal.buildErrorWindow(texts.error.updateFailed));
                };
            }

            const UserListBuilder = function ($searchResultContainer) {
                this.$searchResultContainer = $searchResultContainer;
                this.userAppender = this.appendUsers.bind(this);
            };

            UserListBuilder.prototype = {
                isEmpty: true,
                clearList: function () {
                    this.$searchResultContainer.empty();
                    this.isEmpty = true;

                    return this;
                },
                userToRow: user => {
                    const infoRowAttributes = {
                        id: 'user-id-' + user.id
                    };

                    user.active || (infoRowAttributes['class'] = userArchivedClass);

                    const $controlEdit = components.controls.edit(getOnEditUser(user));
                    components.overlays.defaultTooltip($controlEdit, texts.tooltip.editUser);

                    const $controlArchive = components.controls.archive(getOnArchiveUser(user));
                    components.overlays.defaultTooltip($controlArchive, texts.tooltip.archiveUser);

                    const $archiveBlock = user.active
                        ? $controlArchive
                        : $('<div>', {text: texts.searchResult.archived});

                    const username = user.login;
                    const $userLogin = $('<div>', {text: username});
                    components.overlays.defaultTooltip($userLogin, username, {followCursor: 'horizontal'});

                    const email = user.email || '';
                    const $userEmail = $('<div>', {text: email});
                    if(user.email) components.overlays.defaultTooltip($userEmail, email, {followCursor: 'horizontal'});

                    return new BEM({
                        block: 'imcms-user-info-row',
                        elements: {
                            'user-id': $('<div>', {
                                text: user.id
                            }),
                            'first-name': $('<div>', {
                                text: user.firstName
                            }),
                            'last-name': $('<div>', {
                                text: user.lastName
                            }),
                            'user-name': $userLogin,
                            'email': $userEmail,
                            'edit': $controlEdit,
                            'archive': $archiveBlock,
                        }
                    }).buildBlockStructure('<div>', infoRowAttributes);
                },
                updateUsersFound: function (numberOfUsers) {
                    $usersCountContainer.text(numberOfUsers);
                    return this;
                },
                prepareTitleRow: function () {
                    const $titleRow = new BEM({
                        block: 'imcms-user-title-row',
                        elements: {
                            'user-id': $('<div>', {text: texts.searchResult.id}),
                            'first-name': $('<div>', {text: texts.searchResult.firstName}),
                            'last-name': $('<div>', {text: texts.searchResult.lastName}),
                            'user-name': $('<div>', {text: texts.searchResult.userName}),
                            'email': $('<div>', {text: texts.searchResult.email})
                        }
                    }).buildBlockStructure('<div>', {
                        'class': 'imcms-title'
                    });

                    this.$searchResultContainer.append($titleRow);
                    return this;
                },
                addRowsToList: function (userRows$) {
                    this.$searchResultContainer.css('display', 'block').append(userRows$);
                    return this;
                },
                appendUsers: function (users) {
                    this.updateUsersFound(users.length)
                        .prepareTitleRow()
                        .addRowsToList(users.map(this.userToRow));
                }
            };

            function listUsers() {
                const query = {
                    term: $usersNameFilter.val(),
                    includeInactive: $includeInactiveCheckbox.isChecked(),
                    roleIds: $usersFilterSelect.getSelectedValues()
                };
                const tableBuilder = new UserListBuilder($searchResultContainer).clearList();
                usersRestApi.search(query)
                    .done(tableBuilder.userAppender)
                    .fail(() => modal.buildErrorWindow(texts.error.searchFailed));
            }

            function buildListUsersButton() {
                const $button = components.buttons.positiveButton({
                    text: texts.searchFilter.listUsers,
                    click: listUsers
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'imcms-search-row',
                elements: {
                    'users-name-filter': buildUsersNameFilter(),
                    'users-role-filter': buildUsersRoleFilter(),
                    'users-activity-filter': buildUsersActivityFilter(),
                    'users-list-button': buildListUsersButton()
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-field'
            });
        }

        var $usersCountContainer;

        function buildSearchResultTitle() {
            return components.texts.titleText('<div>', texts.searchResult.title, {
                'class': 'imcms-field'
            }).prepend($usersCountContainer = $('<span>', {
                text: '0'
            }));
        }

        function buildSearchResultContainer() {
            return $('<div>', {
                'class': 'imcms-users-search-result',
                style: 'display: none;'
            });
        }

        function buildCreateNewUserButton() {
            function onCreateNewUserClicked() {
                window.open(imcms.contextPath + '/api/user/creation', '_self').focus();
            }

            const $button = components.buttons.positiveButton({
                text: texts.createNewUser,
                click: onCreateNewUserClicked
            });

            return components.buttons.buttonsContainer('<div>', [$button]);
        }

        const UserTab = function (name, tabElements) {
            SuperAdminTab.call(this, name, tabElements);
        };

        UserTab.prototype = Object.create(SuperAdminTab.prototype);

        UserTab.prototype.getDocLink = () => texts.documentationLink;

        return new UserTab(texts.name, [
            buildSearchRow(),
            buildSearchResultTitle(),
            $searchResultContainer = buildSearchResultContainer(),
            buildCreateNewUserButton()
        ]);
    }
);
