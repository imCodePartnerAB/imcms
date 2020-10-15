DocumentMenuService
===================


boolean hasUserAccessToDoc(int docId, UserDomainObject user);

    boolean isPublicMenuItem(int docId);

    Meta.DisabledLanguageShowMode getDisabledLanguageShowMode(int documentId);

    MenuItemDTO getMenuItemDTO(int docId, Language language);