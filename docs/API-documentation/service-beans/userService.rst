UserService
===========


In this article:
    - `Introduction`_
    - `Use API`_



Introduction
------------

Use API
-------

Init or get instance UserService over global Imcms.getServices ``Imcms.getServices().getUserService();``

.. code-block:: jsp

    Imcms.getServices().getUserService().getUser(int id);

    Imcms.getServices().getUserService().getUser(String login);

    Imcms.getServices().getUserService().updateUser(UserDTO updateData);

    Imcms.getServices().getUserService().getAdminUsers();

    Imcms.getServices().getUserService().getAllActiveUsers();

    Imcms.getServices().getUserService().getUsersByEmail(String email);

    Imcms.getServices().getUserService().getUserData(int userId);

    Imcms.getServices().getUserService().saveUser(UserFormData userData);

    Imcms.getServices().getUserService().searchUsers(String searchTerm, Set<Integer> withRoles, boolean includeInactive)

    Imcms.getServices().getUserService().findAll(boolean includeExternal, boolean includeInactive)

    Imcms.getServices().getUserService().findByNamePrefix(String prefix, boolean includeInactive)

    Imcms.getServices().getUserService().saveExternalUser(ExternalUser user)


