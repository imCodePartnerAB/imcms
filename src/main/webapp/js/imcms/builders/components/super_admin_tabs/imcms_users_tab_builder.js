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

        function buildTitle() {
            return components.texts.titleText('<div>', texts.title, {
                'class': 'imcms-field'
            });
        }

        function buildSearchRow() {

            function buildUsersNameFilter() {
                return components.texts.textBox('<div>', texts.searchFilter.byName);
            }

            function buildUsersRoleFilter() {
                var onSelected = function (value) {
                    console.log('Selected ' + value);
                };

                var $usersFilterSelectContainer = components.selects.selectContainer("<div>", {
                    id: "users-role-filter",
                    name: "users-role-filter",
                    text: texts.searchFilter.byRole.title,
                    emptySelect: true,
                    onSelected: onSelected
                });

                rolesRestApi.read().done(function (roles) {
                    var rolesDataMapped = roles.map(function (role) {
                        return {
                            text: role.name,
                            "data-value": role.id
                        }
                    });

                    components.selects.addOptionsToSelect(
                        rolesDataMapped, $usersFilterSelectContainer.getSelect(), onSelected
                    );
                });

                return $usersFilterSelectContainer;
            }

            function includeInactiveUsers() {
            }

            function buildUsersActivityFilter() {
                return components.checkboxes.imcmsCheckbox('<div>', {
                    text: texts.searchFilter.inactiveUsers,
                    click: includeInactiveUsers
                });
            }

            function onEditUser() {
                // window.open(url, '_blank').focus(); // todo: implement with correct url
            }

            function onArchiveUser() {
                // todo: implement
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
                    return new BEM({
                        block: 'imcms-user-info-row',
                        elements: {
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
                            'edit': components.controls.edit(onEditUser),
                            'archive': components.controls.archive(onArchiveUser)
                        }
                    }).buildBlockStructure('<div>');
                },
                updateUsersFound: function (numberOfUsers) {
                    $usersCountContainer.text(numberOfUsers);
                    return this;
                },
                prepareTitleRow: function () {
                    var $titleRow = new BEM({
                        block: 'imcms-user-title-row',
                        elements: {
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
                var tableBuilder = new UserListBuilder($searchResultContainer).clearList();
                usersRestApi.read().done(tableBuilder.userAppender);
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
            window.open(imcms.contextPath + '/api/user/create', '_blank').focus();
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
