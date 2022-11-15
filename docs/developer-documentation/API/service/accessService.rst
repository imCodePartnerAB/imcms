
.. code-block:: java

    boolean hasUserEditAccess(UserDomainObject user, Integer documentId, AccessContentType accessContentType);

fwqfqwfqwfqwfqwfwqfwqwfw

.. code-block:: java

    boolean hasUserPublishAccess(UserDomainObject user, int docId);

    RestrictedPermission getPermission(UserDomainObject user, int documentId);

    RolePermissions getTotalRolePermissionsByUser(UserDomainObject user);

	boolean hasUserFileAdminAccess(int userId);