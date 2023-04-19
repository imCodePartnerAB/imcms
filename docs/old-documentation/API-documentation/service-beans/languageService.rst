LanguageService
===============

In this article:
    - `Use API`_

Use API
-------

For what in order to use LanguageService API need to init this the service.
How to init LanguageService and uses API look at the code below.

.. code-block:: jsp

   LanguageService languageService = Imcms.getServices().getLanguageService();

   Language language = languageService.findByCode(String code); //find language by code language from db

   List<Language> langs = languageService.getAll(); //get all languages

   List<Language> langs = languageService.getAvailableLanguages(); // get all available which set into server.properties

   Language langDefault = languageService.getDefaultLanguage(); // get default language imcms


Block parameters:
""""""""""""""""

+---------------------+--------------+--------------------------------------------------+
| Parameter           | Type         | Description                                      |
+=====================+==============+==================================================+
| code                | String       | Language code                                    |
+---------------------+--------------+--------------------------------------------------+






