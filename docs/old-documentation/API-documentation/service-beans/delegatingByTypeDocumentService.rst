DelegatingByTypeDocumentService
===============================

In this article:
    - `Introduction`_
    - `Use API`_
    - `Description parameters`_
    - `Description about Document`_
    - `The most important setters/getters for Document`_


.. warning:: This init instance over Imcms.getServices().getDocumentService() working from 10 version

Introduction
------------
Imcms works smartly. Now we don't have to worry and create unnecessary pieces of code for editing documents, because the system took care of us
We can easy to make manipulation any type documents with using this API.
Imcms provides list API which wrote below.

Use API
-------

For what in order to get instance ``DelegatingByTypeDocumentService`` need to use Imcms.getServices().getDocumentService();

.. code-block:: jsp

     Imcms.getServices().getDocumentService().countDocuments(); //get counts documents from db;

     Imcms.getServices().getDocumentService().get(int docId);

     Imcms.getServices().getDocumentService().createNewDocument(DocumentType type, Integer parentDocId);

     Imcms.getServices().getDocumentService().save(Document saveMe);

     Imcms.getServices().getDocumentService().publishDocument(int docId, int userId);

     Imcms.getServices().getDocumentService().index(int docId);

     Imcms.getServices().getDocumentService().copy(int docId);

     Imcms.getServices().getDocumentService().deleteByDocId(Integer docIdToDelete);

     Imcms.getServices().getDocumentService().getUniqueAlias(String alias);

Description parameters
----------------------

+---------------------------+----------------------------------------------------------------+
| Type                      | Description                                                    |
+===========================+================================================================+
| DocumentType              | Imcms support any types (FILE, HTML,TEXT,URL)                  |
+---------------------------+----------------------------------------------------------------+
| PublicationStatus         | Imcms has aby publication status of document                   |
|                           | (NEW,DISAPPROVED,APPROVED)                                     |
+---------------------------+----------------------------------------------------------------+
| AuditDTO                  | AuditDTO - this custom formatted date time.                    |
|                           |                                                                |
+---------------------------+----------------------------------------------------------------+
| DisabledLanguageShowMode  | ``SHOW_IN_DEFAULT_LANGUAGE`` - show document in the            |
|                           | default language if current language disabled for this document|
|                           | ``DO_NOT_SHOW`` - doesn't show document                        |
+---------------------------+----------------------------------------------------------------+
| Set<Category>             |                                                                |
|                           |                                                                |
+---------------------------+----------------------------------------------------------------+
| Set<RestrictedPermission> | Each documents have restricted permission for edit             |
|                           | contents                                                       |
+---------------------------+----------------------------------------------------------------+
| Map<Integer, Permission>  |  Info about permission read in the document java code          |
|                           |  by path ``com/imcode/imcms/persistence/entity/Meta``          |
+---------------------------+----------------------------------------------------------------+


Description about Document
--------------------------

Document it is super class for ``TextDocumentDTO``, ``DocumentDTO``, ``UrlDocumentDTO``, ``FileDocumentDTO``.
Therefore, we can easily return any type of data that need - just inject call methods to above documents constructor;

Example
"""""""
.. code-block:: jsp

  DocumentService docService = Imcms.getServices().getDocumentService();
  TextDocumentDTO documentDTO = new TextDocumentDTO(docService.createNewDocument(Meta.DocumentType.TEXT, 1001));

The most important setters/getters for Document
-----------------------------------------------

Document has different fields for getter/setter for value

#.        setType(DocumentType type);
#.        setTarget(String target);
#.        setAlias(String alias);
#.        setName(String name);
#.        setCommonContents(List<CommonContents> contents);
#.        setPublicationStatus(PublicationStatus publicationStatus);
#.        setPublished(AuditDTO audit);
#.        setArchived(AuditDTO audit);
#.        setPublicationEnd(AuditDTO audit);
#.        setModified(AuditDTO audit);
#.        setCreated(AuditDTO audit);
#.        setDisabledLanguageShowMode(DisabledLanguageShowMode disabledLanguageShowMode);
#.        setCurrentVersion(AuditDTO audit);
#.        setLatestVersion(AuditDTO audit);
#.        setKeywords(Set<String> keywords);
#.        setSearchDisabled(boolean isSearchDisabled);
#.        setCategories(Set<Category> categories);
#.        setRestrictedPermissions(Set<RestrictedPermission> restrictedPermissions);
#.        setRoleIdToPermission(Map<Integer, Permission> roleIdToPermission);
#.        setLinkableByOtherUsers(boolean isLinkableByOtherUsers);
#.        setLinkableForUnauthorizedUsers(boolean isLinkableForUnauthorizedUsers);



