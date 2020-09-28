AccessService
=============

In this article:
    - `Introduction`_
    - `Use API`_


Introduction
------------
To know do the user have access to do something with some document or not.
Imcms provides feature check permission and access for anybody user. Need to use just AccessService, which easy initialize.
Init AccessService - ``Imcms.getServices().getAccessService()``;


Use API
-------

  .. code-block:: jsp
  AccessService accessService = Imcms.getServices().getAccessService();

  boolean hasUserEditAccess = accessService.hasUserEditAccess(UserDomainObject user, Integer documentId, AccessType accessType);
  //Check, does the user have for access for edit document - ``documentId``

  RestrictedPermission getEditPermission = accessService.getEditPermission(UserDomainObject user, int documentId);
  //Get all permission for user on the current document ``documentId``


