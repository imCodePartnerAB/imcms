AccessService
=============
**com.imcode.imcms.domain.service**

This service is designed to obtain information about user permissions in a individual document and the whole system.

**************
Initialization
**************

.. code-block:: java

    AccessService accessService = Imcms.getServices().getAccessService();

*******
Methods
*******

.. code-block:: java

    boolean hasUserViewAccess(UserDomainObject user, Integer documentId);

check the user's access to view the document.

------------------

.. code-block:: java

    boolean hasUserPublishAccess(UserDomainObject user, int docId);

check the user's access to the publication of the document.

------------------

.. code-block:: java

    RestrictedPermission getPermission(UserDomainObject user, int documentId);

get data about the user's permissions in a specific document.

------------------

.. code-block:: java

    RolePermissions getTotalRolePermissionsByUser(UserDomainObject user);

get data about the user's permissions in the whole system.

------------------

.. code-block:: java

	boolean hasUserFileAdminAccess(int userId);

check the user's access to the *Files* tab on the *Admin page*.

------------------

.. code-block:: java

    boolean hasUserEditAccess(UserDomainObject user, Integer documentId, AccessContentType accessContentType);

check the user's access to Page Info or editing any type of content.

``AccessContentType`` has following values: ``ALL``, ``IMAGE``, ``TEXT``, ``MENU``, ``LOOP``, ``DOC_INFO``.
