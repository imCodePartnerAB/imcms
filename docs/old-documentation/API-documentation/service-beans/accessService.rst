AccessService
=============

In this article:
    - `Introduction`_
    - `Use API`_
    - `Description AccessType`_


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



Description AccessType
----------------------

``AccessType`` has values like:
  * IMAGE - access only for images
  * TEXT - access only for texts
  * MENU - access only for menu
  * LOOP - access only for loop
  * DOC_INFO - access only for doc_info
  * ALL - access only for all content