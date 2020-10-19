CommonContentService
====================

In this article:
    - `Introduction`_
    - `Use API`_

.. warning:: This init instance over Imcms.getServices().getCommonContentService() working from 10 version

Introduction
------------

Each documents page have common content with enable/disable languages, which store itself data about document.
We can easy get all info about it , just use ``CommonContentService``.
Init CommonContentService bean service - Imcms.getServices().getCommonContentService();
Look at the code below how use API.

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

    Set<CommonContent> getByVersion(Version version);

    deleteByDocId(Integer docId);

Block parameters:
"""""""""""""""""
+----------------------+--------------+--------------------------------------------------+
| Parameters           | Type         | Description                                      |
+======================+==============+==================================================+
| versionNo            | Integer      | number version document                          |
+----------------------+--------------+--------------------------------------------------+
| docId                | Integer      | Identify the linked document                     |
|                      |              |                                                  |
+----------------------+--------------+--------------------------------------------------+
| saveUs               | Collection<T>| List common contents which need to save          |
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

Checking what languages the document is active in, example:
"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
Using lambda and stream API java8+

.. code-block:: jsp

   List<Language> listEnableLang = Imcms.getServices().getCommonContentService().getOrCreateCommonContents(int docId, int versionNo)
   					.stream()
   					.filter(CommonContent::isEnabled)
   					.map(CommonContent::getLanguage)
   					.collect(Collectors.toList());


Using simple code without stream API:

.. code-block:: jsp

   List<CommonContent> contents = Imcms.getServices().getCommonContentService().getOrCreateCommonContents(int docId, int versionNo);
   			List<Language> languages = new ArrayList<>();
   			for (CommonContent commonContent: contents) {
   				if (commonContent.isEnabled()) {
   					languages.add(commonContent.getLanguage());
   				}
   			}



