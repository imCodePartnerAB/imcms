Template
========

In this article:
    - `Introduction`_
    - `Template Management`_

------------
Introduction
------------

Template is a page layout. Every *Text* document has a template and represents web page. You can use the template in many documents.

All templates are located in ``/WEB-INF/templates/text``.

.. warning:: The template file must have one of the following extensions: ``jsp``, ``jspx``, ``html``.

``jsp`` is a recommended extension, because you can use custom ImCMS tags in jsp only.

-------------------
Template Management
-------------------

You can manage templates in the following ways:

1. In the Tomcat. Add a new template file to ``WEB-INF/templates/text`` and restart the Tomcat. After removing the template, you also have to restart the Tomcat. You can edit the layout of the template without rebooting.
2. In the ImCMS. You can add a new template file, edit the layout and remove using *Admin Page* -> :doc:`Files tab </user-documentation/admin-settings/files>`.

To set a template for a *Text* document, use the *Page Info* -> *Appearance* tab.
