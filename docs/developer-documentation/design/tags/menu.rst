Menu Related Tags
=================

Menu Related tags represents the :doc:`Menu editor </user-documentation/editors/menu>` in Edit mode and
the content from the editor in Preview mode and on a published page.

.. seealso:: Read also the :doc:`Menu editor </user-documentation/editors/menu>` article.

********
Menu Tag
********

Opens/closes the body of the menu.

Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+---------------------+---------+------------------------------------------------------------------------------+
| Attribute           | Type    |  Description                                                                 |
+=====================+=========+==============================================================================+
| index               | Integer | id of the current text (recommended version).                                |
+---------------------+---------+------------------------------------------------------------------------------+
| document            | Integer | id of the document from which to take the text.                              |
|                     |         | This text cannot be changed on this page.                                    |
+---------------------+---------+------------------------------------------------------------------------------+
| label               | String  | the text editor title (visible to the admin).                                |
+---------------------+---------+------------------------------------------------------------------------------+
| showlabel           | Boolean | true (default) - show the label, false - don't show label.                   |
+---------------------+---------+------------------------------------------------------------------------------+
| pre                 | String  | text or html before the content (if the content is not empty).               |
+---------------------+---------+------------------------------------------------------------------------------+
| post                | String  | text or html after the content (if the content is not empty).                |
+---------------------+---------+------------------------------------------------------------------------------+
|                     |         | Possible values:                                                             |
|                     |         |                                                                              |
| showMode            | String  | * absence of this attribute/``default`` -                                    |
|                     |         |   the editor is highlighted in Edit mode.                                    |
|                     |         | * ``small`` - the editor in Edit mode looks like in the preview,             |
|                     |         |   but after clicking it opens the normal editor.                             |
+---------------------+---------+------------------------------------------------------------------------------+

.. warning:: Manu tag must have ``index`` attribute!

************
MenuLoop Tag
************

Opens/closes a loop of elements, specified inside the *menu* or *menuLoop* tags.

Inside *menuLoop* tag you can use following variable:

* **menuItem** - MenuItemDTO - represents the current menu item.
* **isCurrent** - Boolean - represents a boolean whether the current item is the current document.
* **hasChildren** - Boolean - represents a boolean whether the current item has nested elements.
* **menuItems** - List<MenuItemDTO> - represents nested elements of the current menu item.

****************
MenuItemLink Tag
****************

Represents a link to a document, specified inside the *menuLoop* tag. You need to specify the text inside this tag.

Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+---------------------+---------+------------------------------------------------------------------------------+
+ Attribute           + Type    +  Description                                                                 +
+=====================+=========+==============================================================================+
+ classes             + String  + specify classes for <a> (space separated).                                   +
+---------------------+---------+------------------------------------------------------------------------------+

*************
Usage example
*************

.. code-block:: jsp

    <imcms:menu index='1' pre="<div>" post="</div>" label="Test menu tag">
        <div class="imcms-demo-page__menu imcms-demo-menu">
            <imcms:menuLoop>
                <div class="imcms-demo-menu__menu-item imcms-demo-menu-item${hasChildren?' imcms-demo-menu__menu-item--parent':''}${isCurrent?' imcms-demo-menu__menu-item--active':''}">
                    <imcms:menuItemLink classes="imcms-demo-menu-item__text">${menuItem.title}</imcms:menuItemLink>
                    <imcms:menuLoop>
                        <div class="imcms-demo-menu__menu-items imcms-demo-menu__menu-items--child">
                            <div class="imcms-demo-menu__menu-item">
                                <imcms:menuItemLink
                                        classes="imcms-demo-menu-item__text">${menuItem.title}</imcms:menuItemLink>
                            </div>
                        </div>
                    </imcms:menuLoop>
                </div>
            </imcms:menuLoop>
        </div>
    </imcms:menu>
