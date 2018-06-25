/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-users-tab-builder',
    [
        'imcms-window-tab-builder', 'imcms-i18n-texts', 'jquery', 'imcms-bem-builder', 'imcms-components-builder',
        'imcms-roles-rest-api'
    ],
    function (TabBuilder, texts, $, BEM, components, rolesRestApi) {

        texts = texts.superAdmin.users;

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

            return new BEM({
                block: 'imcms-search-row',
                elements: {
                    'users-name-filter': buildUsersNameFilter(),
                    'users-role-filter': buildUsersRoleFilter()
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-field'
            });
        }

        var UsersTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        UsersTab.prototype = Object.create(TabBuilder.prototype);

        UsersTab.prototype.tabElementsFactory = function () {
            return [
                buildTitle(),
                buildSearchRow()
            ];
        };

        return new UsersTab(texts.name);
    }
);
