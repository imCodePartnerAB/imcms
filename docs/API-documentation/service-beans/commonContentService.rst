CommonContentService
====================

In this article:
    - `Introduction`_
    - `Use API`_


Introduction
------------

Use API
-------
  .. code-block:: jsp

    List<CommonContent> getOrCreateCommonContents(int docId, int versionNo);
    //Get document's common contents for all languages
    //If common content of non working version is null it creates new common content based on working.

    CommonContent getOrCreate(int docId, int versionNo, Language language);
    //Gets common content for working or published versions.
    //If common content of non working version is null it creates new common content based on working.

    <T extends CommonContent> void save(int docId, Collection<T> saveUs);

    Set<CommonContent> getByVersion(Version version); //``see documentation versionService how to get Version``

    deleteByDocId(Integer docId);

Block parameters:
"""""""""""""""""
+----------------------+--------------+--------------------------------------------------+
| Attribute            | Type         | Description                                      |
+======================+==============+==================================================+
| versionNo            | Integer      | number version document                          |
+----------------------+--------------+--------------------------------------------------+
| docId                | Integer      | Identify the linked document                     |
|                      |              |                                                  |
+----------------------+--------------+--------------------------------------------------+
| Collection<T> saveUs | Collection<T>| List common contents which need to save          |
+----------------------+--------------+--------------------------------------------------+





How to use ``getOrCreate`` method simple example:
"""""""""""""""""""""""""""""""""""""""""""""""""
How to init languageService see to languageService-documentation
  .. code-block:: jsp

   Language language = languageService.findByCode(String code);
   int versionNo = 0; //working version
   int docId = 1001;

   CommonContentService commonContentService = Imcms.getServices().getCommonContentService();

   commonContentService.getOrCreate(int docId, int versionNo, Language language);




