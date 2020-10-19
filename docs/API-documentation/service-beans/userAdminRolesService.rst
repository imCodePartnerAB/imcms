UserAdminRolesService
=====================


Init or get instance UserAdminRolesService over global Imcms.getServices ``Imcms.getServices().getManagedBean(UserAdminRolesService.class)``

Use API
-------

.. code-block:: jsp

    List<User> getUsersByAdminRole(Role role);

    List<Role> getAdminRolesByUser(int userId);

    void updateUserAdminRoles(List<? extends Role> roles, User user);


Example usages
""""""""""""""

.. code-block:: jsp

    Role role = roleService.getById(idRole);

    List<User> getUsersByAdminRole(Role role);

.. note::
  Check how to use service bean :doc:`RoleService</API-documentation/service-beans/roleService>`