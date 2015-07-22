Loop Tag
========

In this article:
    - `Introduction`_
    - `Use in template`_

Introduction
------------
Probably everyone faced with a situation where same content should displayed several times. Of course this problem can be solve with
well-known standard JSP tag ``forEach``, but it is not enough if count cycle of content changed very often. That is why ImCMS provide own cycle tag that called ``loop``.
``loop`` tag works like the ``forEach`` tag, but the main feature of it is visual editor that provide agile configuration of content`s count, etc.


Use in template
---------------

For configure ``loop`` tag in template just look at the code below.

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

