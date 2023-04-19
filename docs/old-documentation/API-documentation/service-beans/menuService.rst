MenuService
===========

Init or get instance MenuService over global Imcms.getServices ``Imcms.getServices().getMenuService();``

.. code-block:: jsp

    List<MenuItemDTO> getMenuItems(int docId, int menuIndex, String language, boolean nested, String typeSort)

    List<MenuItemDTO> getSortedMenuItems(MenuDTO menuDTO, String langCode)

    List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, String language, boolean nested)

    List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language, boolean nested)

    String getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                       boolean nested, String attributes, String treeKey, String wrap)

    String getPublicMenuAsHtml(int docId, int menuIndex, String language,
                                      boolean nested, String attributes, String treeKey, String wrap)

    String getVisibleMenuAsHtml(int docId, int menuIndex)

    String getPublicMenuAsHtml(int docId, int menuIndex)

    MenuDTO saveFrom(MenuDTO menuDTO)

    void deleteByVersion(Version version)

    void deleteByDocId(Integer docIdToDelete)

    Menu removeId(Menu jpa, Version newVersion)

    List<Menu> getAll();

Block parameters:
"""""""""""""""""
+----------------------+--------------+--------------------------------------------------+
| Parameters           | Type         | Description                                      |
+======================+==============+==================================================+
| menuIndex            | int          | index ``no``                                     |
+----------------------+--------------+--------------------------------------------------+
| docId                | int          | Identify the linked document                     |
|                      |              |                                                  |
+----------------------+--------------+--------------------------------------------------+
| nested               | boolean      | show nested in menu area                         |
+----------------------+--------------+--------------------------------------------------+
| attributes           | String       |                                                  |
+----------------------+--------------+--------------------------------------------------+
| treeKey              | String       | number key for start menu item                   |
+----------------------+--------------+--------------------------------------------------+
| wrap                 | String       |  list wrap tags for each menu item               |
+----------------------+--------------+--------------------------------------------------+


Fields MenuDTO
""""""""""""""

#. Integer menuIndex;
#. Integer docId;
#. List<MenuItemDTO> menuItems;
#. boolean nested; - possible display menu items like nested
#. String typeSort; - sort type need to for client side



