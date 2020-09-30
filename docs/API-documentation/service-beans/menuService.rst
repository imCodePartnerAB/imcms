MenuService
===========


In this article:
    - `Introduction`_
    - `Use API`_



Introduction
------------

Use API
-------

Init or get instance MenuService over global Imcms.getServices ``Imcms.getServices().getMenuService();``

.. code-block:: jsp

    Imcms.getServices().getMenuService().getMenuItems(int docId, int menuIndex, String language, boolean nested, String typeSort)

    Imcms.getServices().getMenuService().getSortedMenuItems(MenuDTO menuDTO, String langCode)

    Imcms.getServices().getMenuService().getVisibleMenuItems(int docId, int menuIndex, String language, boolean nested)

    Imcms.getServices().getMenuService().getPublicMenuItems(int docId, int menuIndex, String language, boolean nested)

    Imcms.getServices().getMenuService().getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                       boolean nested, String attributes, String treeKey, String wrap)

    Imcms.getServices().getMenuService().getPublicMenuAsHtml(int docId, int menuIndex, String language,
                                      boolean nested, String attributes, String treeKey, String wrap)

    Imcms.getServices().getMenuService().getVisibleMenuAsHtml(int docId, int menuIndex)

    Imcms.getServices().getMenuService().getPublicMenuAsHtml(int docId, int menuIndex)

    Imcms.getServices().getMenuService().saveFrom(MenuDTO menuDTO)

    Imcms.getServices().getMenuService().deleteByVersion(Version version)

    Imcms.getServices().getMenuService().deleteByDocId(Integer docIdToDelete)

    Imcms.getServices().getMenuService().removeId(Menu jpa, Version newVersion)

    Imcms.getServices().getMenuService().getAll();



