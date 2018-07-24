/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-users-tab-builder',
    [
        'imcms-window-tab-builder', 'imcms-i18n-texts', 'jquery', 'imcms-bem-builder', 'imcms-components-builder',
        'imcms-roles-rest-api', 'imcms-users-rest-api', 'imcms'
    ],
    function (TabBuilder, texts, $, BEM, components, rolesRestApi, usersRestApi, imcms) {

        texts = texts.superAdmin.users;

        var $searchResultContainer;
        var userArchivedClass = 'imcms-user-info-row--archived';
        var $usersNameFilter;
        var $includeInactiveCheckbox;
        var $usersFilterSelect;

        function buildTitle() {
            return components.texts.titleText('<div>', texts.title, {
                'class': 'imcms-field'
            });
        }

        function buildSearchRow() {

            function buildUsersNameFilter() {
                var $usersNameFilterBox = components.texts.textBox('<div>', texts.searchFilter.byName);
                $usersNameFilter = $usersNameFilterBox.$input;
                return $usersNameFilterBox;
            }

            function buildUsersRoleFilter() {
                $usersFilterSelect = components.selects.multipleSelect("<div>", {
                    id: "users-role-filter",
                    name: "users-role-filter",
                    text: texts.searchFilter.byRole.title
                });

                rolesRestApi.read().done(function (roles) {
                    var rolesDataMapped = roles.map(function (role) {
                        return {
                            text: role.name,
                            value: role.id
                        }
                    });

                    components.selects.addOptionsToMultiSelect(rolesDataMapped, $usersFilterSelect);
                });

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
                return function () {
                    window.open(imcms.contextPath + '/api/user/edition/' + user.id, '_blank').focus();
                }
            }

            function getOnArchiveUser(user) {
                return function () {
                    usersRestApi.update({id: user.id, active: false}).success(function () {
                        $('#user-id-' + user.id).addClass(userArchivedClass)
                            .find('.imcms-control--archive')
                            .replaceWith($('<div>', {
                                'class': 'imcms-user-info-row__archive',
                                text: texts.searchResult.archived
                            }));
                    });
                }
            }

            var UserListBuilder = function ($searchResultContainer) {
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
                userToRow: function (user) {
                    var infoRowAttributes = {
                        id: 'user-id-' + user.id
                    };

                    user.active || (infoRowAttributes['class'] = userArchivedClass);

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
                            'user-name': $('<div>', {
                                text: user.login
                            }),
                            'email': $('<div>', {
                                text: user.email
                            }),
                            'edit': components.controls.edit(getOnEditUser(user)),
                            'archive': user.active
                                ? components.controls.archive(getOnArchiveUser(user))
                                : $('<div>', {text: texts.searchResult.archived})
                        }
                    }).buildBlockStructure('<div>', infoRowAttributes);
                },
                updateUsersFound: function (numberOfUsers) {
                    $usersCountContainer.text(numberOfUsers);
                    return this;
                },
                prepareTitleRow: function () {
                    var $titleRow = new BEM({
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
                var query = {
                    term: $usersNameFilter.val(),
                    includeInactive: $includeInactiveCheckbox.isChecked(),
                    roleIds: $usersFilterSelect.getSelectedValues()
                };
                var tableBuilder = new UserListBuilder($searchResultContainer).clearList();
                usersRestApi.search(query).done(tableBuilder.userAppender);
            }

            function buildListUsersButton() {
                var $button = components.buttons.positiveButton({
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

        function onCreateNewUserClicked() {
            window.open(imcms.contextPath + '/api/user/creation', '_blank').focus();
        }

        function buildCreateNewUserButton() {
            var $button = components.buttons.positiveButton({
                text: texts.createNewUser,
                click: onCreateNewUserClicked
            });

            return components.buttons.buttonsContainer('<div>', [$button]);
        }

        var UsersTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        UsersTab.prototype = Object.create(TabBuilder.prototype);

        UsersTab.prototype.tabElementsFactory = function () {
            return [
                buildTitle(),
                buildSearchRow(),
                buildSearchResultTitle(),
                $searchResultContainer = buildSearchResultContainer(),
                buildCreateNewUserButton()
            ];
        };

        return new UsersTab(texts.name);
    }
);
