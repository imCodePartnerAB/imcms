RoleService
===========


In order to init RoleService bean need to use - ``Imcms.getServices().getManagedBean(RoleService.class)``

Use API
-------

.. code-block:: jsp

    Role getById(int id);

    List<Role> getAll();

    Role save(Role saveMe);

    Role saveNewRole(Role role);

    void delete(int roleID);

Description about API
"""""""""""""""""""""

``save(Role role)`` - update injected role

``saveNewRole(Role role)`` -  create new role
