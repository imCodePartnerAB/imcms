DocumentMenuService
===================


Description
"""""""""""
DocumentMenuService uses for different manipulation with menu items for current text document.
In order to init bean for DocumentMenuService need to use - ``Imcms.getService().getManagedBean(DocumentMenuService.class)``


Use API
-------

.. code-block:: jsp

    DocumentMenuService docMenuService = Imcms.getService().getManagedBean(DocumentMenuService.class);

    boolean hasUserAccessToDoc(int docId, UserDomainObject user);

    boolean isPublicMenuItem(int docId);

    Meta.DisabledLanguageShowMode getDisabledLanguageShowMode(int documentId);

    MenuItemDTO getMenuItemDTO(int docId, Language language);