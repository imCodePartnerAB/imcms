Language
========

General actions with language (the java-code)
"""""""""""""""""""""""""""""""""""""""""""""

* get default language:

.. code-block:: java

    Imcms.getServices().getDocumentLanguages().getDefault();

* set language by creating new:

.. code-block:: java

    Imcms.getUser().getDocGetterCallback().setLanguage(new DocumentLanguage(String code, String name, String nativeName), boolean isDefaultLang);

where ``code`` is language ISO-639-1 code.
     * @param name       language name
     * @param nativeName language native name
     */
    public DocumentLanguage(String code, String name, String nativeName) {
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
    }

Imcms.getServices().getDocumentMapper().getDocument(---id---).getMeta().getEnabledLanguages();
Imcms.getServices().getDocumentMapper().getDocument(---id---).setLanguage(---DocumentLanguage--);

