Menu Tag
========


In this article:
    - `Introduction`_
    - `Use in template`_

Introduction
------------
Well-known, an integral part of the page is menu it navigate users overall web-site, and describe what kind of content
was presented on. Since ImCMS based on document-presented pages each menu item can presented as specific document link (see also :doc:`/document-engine` section)

Use in template
---------------

For configure ``Loop`` tag in template just look at the code above.

.. code-block:: jsp

    <imcms:loop no="1" pre="<div>" post="</div>">
        ...HTML or JPS tags here...
    </imcms:loop>



Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+--------------------+--------------+--------------------------------------------------+
| Attribute          | Type         | Description                                      |
+====================+==============+==================================================+
| no                 | Integer      | Identifier for current loop                      |
+--------------------+--------------+--------------------------------------------------+
| pre                | String       | Text or html tag that would be added before      |
|                    |              | loop tag                                         |
+--------------------+--------------+--------------------------------------------------+
| post               | String       | Text or html tag that would be added after loop  |
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
        <imcms:loop no="1" pre="<div>" post="</div>">
            Hello world with loop content
        </imcms:loop>
    </body>
    </html>

