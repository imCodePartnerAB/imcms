UserRolesService
================

Init or get instance UserRolesService over global Imcms.getServices ``Imcms.getServices().getManagedBean(UserRolesService.class)``

Use API
-------

.. code-block:: jsp

    List<User> getUsersByRole(Role role);

    List<Role> getRolesByUser(int userId);

    void updateUserRoles(List<? extends Role> roles, User user);

Example usages
""""""""""""""
.. code-block:: jsp

    UserRolesService userRolesService = Imcms.getServices().getManagedBean(UserRolesService.class)

    List<Role> userRoles = userRolesService.getRolesByUser(1);

    List<User> usersWithSuperAdminRole = userRolesService.getUsersByRole(Roles.SUPER_ADMIN);