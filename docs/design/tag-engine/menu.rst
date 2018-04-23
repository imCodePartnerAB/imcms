Menu Tag
========


In this article:
    - `Introduction`_
    - `Use in template`_

Introduction
------------
Well-known, an integral part of the page is menu. It navigate users overall web-site, and describe what kind of content
was presented on. Since ImCMS based on document-presented pages each menu item can be present as specific document link (see also :doc:`Document Engine </document-engine/index>` section)

Use in template
---------------

There are several important part configure menu fully:

* HTML structure such as ``ul-li`` list or ``div-div`` list
* Information about menu-nesting

Default 2-level structured menu shows below:

.. code-block:: jsp

    <imcms:menu no='1' docId="1001">
        <ul>
            <imcms:menuloop>
                <imcms:menuitem>
                    <li>
                        <imcms:menuitemlink>
                            ${menuitem.document.headline}
                        </imcms:menuitemlink>
                        <!-- sub menu definition -->
                        <imcms:menuloop>
                            <imcms:menuitem>
                                <div>
                                    <imcms:menuitemlink>
                                        ${menuitem.document.headline}
                                    </imcms:menuitemlink>
                                </div>
                            </imcms:menuitem>
                        </imcms:menuloop>
                    </li>
                </imcms:menuitem>
            </imcms:menuloop>
        </ul>
    </imcms:menu>



Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+--------------------+--------------+--------------------------------------------------+
| Attribute          | Type         | Description                                      |
+====================+==============+==================================================+
| no                 | Integer      | Identifier for current menu                      |
+--------------------+--------------+--------------------------------------------------+
| docId              | Integer      | Identify the linked document (default            |
|                    |              | - current document)                              |
+--------------------+--------------+--------------------------------------------------+
| pre                | String       | Text or html tag that would be added before      |
|                    |              | menu tag                                         |
+--------------------+--------------+--------------------------------------------------+
| post               | String       | Text or html tag that would be added after menu  |
|                    |              | tag                                              |
+--------------------+--------------+--------------------------------------------------+

Example:
""""""""
.. code-block:: jsp

    <%@taglib prefix="imcms" uri="imcms" %>

    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>Template</title>
        <meta charset="utf-8"/>
    </head>
    <body>
        <imcms:menu no='1' docId="1001">
            <ul>
                <imcms:menuloop>
                    <imcms:menuitem>
                        <li>
                            <imcms:menuitemlink>
                                ${menuitem.document.headline}
                            </imcms:menuitemlink>
                            <!-- sub menu definition -->
                            <imcms:menuloop>
                                <imcms:menuitem>
                                    <div>
                                        <imcms:menuitemlink>
                                            ${menuitem.document.headline}
                                        </imcms:menuitemlink>
                                    </div>
                                </imcms:menuitem>
                            </imcms:menuloop>
                        </li>
                    </imcms:menuitem>
                </imcms:menuloop>
            </ul>
        </imcms:menu>
    </body>
    </html>

