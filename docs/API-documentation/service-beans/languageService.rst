languageService
===============

In this article:
    - `Introduction`_
    - `Use API`_
    - `Description using DTOs objects`_



Introduction
------------


Use API
-------

For what in order to use LanguageService API need to init this the service.
How to init LanguageService and uses API look at the code below.

.. code-block:: jsp

   LanguageService languageService = Imcms.getServices().getLanguageService();

   Language language = languageService.findByCode(String code);

   List<Language> langs = languageService.getAll();

   List<Language> langs = languageService.getAvailableLanguages();

   Language langDefault = languageService.getDefaultLanguage();


Block parameters:
""""""""""""""""

+---------------------+--------------+--------------------------------------------------+
| Attribute           | Type         | Description                                      |
+=====================+==============+==================================================+
| code                | String       | Language code                                    |
+---------------------+--------------+--------------------------------------------------+

Description using DTOs objects
------------------------------

On the client side has to work only with DTOs objects. Imcms has support easy mapping simple current type object to DTO.
How to map objects to DTOs loo at the code example below.
WARNIGN: (Sometimes jsp files don't support API stream lambda expression, will be sure that use Java 8+)






