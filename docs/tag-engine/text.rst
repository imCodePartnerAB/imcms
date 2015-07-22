Text Tag
========

In this article:
    - `Introduction`_
    - `Use in template`_


Introduction
------------
Each web-page contains piece of text. It can be description of image of information about page. Usually it can be altered, edited and even deleted.
Since text can be stored in database ImCMS provide easy access to it via ``text`` tag.


Use in template
---------------

For configure ``text`` tag in template just look at the code below.


.. code-block:: jsp

    <imcms:text no="1" pre="<div>" post="</div>"/>


Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+--------------------+--------------+--------------------------------------------------+
| Attribute          | Type         | Description                                      |
+====================+==============+==================================================+
| no                 | Integer      | Identifier for current text                      |
+--------------------+--------------+--------------------------------------------------+
| document           | Integer      | Identify the linked document (default            |
|                    |              | - current document)                              |
+--------------------+--------------+--------------------------------------------------+
| version            | Integer      | Identify version of text                         |
+--------------------+--------------+--------------------------------------------------+
| placeholder        | String       | The text that was showed if native content are   |
|                    |              | empty                                            |
+--------------------+--------------+--------------------------------------------------+
| pre                | String       | Text or html tag that would be added before      |
|                    |              | text tag                                         |
+--------------------+--------------+--------------------------------------------------+
| post               | String       | Text or html tag that would be added after text  |
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
        <imcms:text no="1" document="1001" pre="<div>" post="</div>" placeholder="<i>this text is empty</i>"/>
    </body>
    </html>


