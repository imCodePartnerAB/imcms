Language
========

General actions with language
"""""""""""""""""""""""""""""

*
    get default language:
    .. code-block:: java

        DocumentLanguage defaultLang = Imcms.getServices().getDocumentLanguages().getDefault();

*
    get document's enabled languages:
    .. code-block:: java

        Imcms.getServices().getDocumentMapper().getDocument("current document's id").getMeta().getEnabledLanguages();

    This code will return the ``Set<DocumentLanguage>``,  which can be used for choosing the right language and setting it.

*
    set document's language:
    .. code-block:: java

        Imcms.getServices().getDocumentMapper().getDocument("current document's id").setLanguage(DocumentLanguage language);

*
    set user's language (not recommended):
    .. code-block:: java

        Imcms.getUser().getDocGetterCallback().setLanguage(DocumentLanguage language, boolean isDefaultLang);

*
    set only one(or more, doesn't matter) language on admin panel in ``server.properties`` file:

    You have to set in value ``AvailableLanguages``, language which you would want to see in admin panel.
    Currently, necessary use 2 letters language codes (en;sv) with ';' delimiter


