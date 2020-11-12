ImcmsLanguages
==============


In order to get the current ``IMCMS`` language need to use code like below

Example usages current lang imcms
"""""""""""""""""""""""""""""""""

.. code-block:: jsp

    Language language = Imcms.getLanguage();

    String codeLanguage = language.getCode();

    String nativeLanguage = language.getNativeName();

