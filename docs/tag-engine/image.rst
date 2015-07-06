Image Tag
=========

In this article:
	- `Use in template`_


Use in template
---------------

ImCMS by default provide easy access to image, stored in the system. Also for using image in template all that needed are insert image-tag in the desired place.

Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+--------------------+--------------+-------------------------------------------------+
| Attribute          | Type         | Description                                     |
+====================+==============+=================================================+
| no                 | Integer      | Identifier for current image                    |
+--------------------+--------------+-------------------------------------------------+
| document           | Integer      | Identify the linked document (default           |
|                    |              | - current document                              |
+--------------------+--------------+-------------------------------------------------+
| version            | Integer      | Identify version of text                        |
+--------------------+--------------+-------------------------------------------------+
| formats            | String       | Text format                                     |
+--------------------+--------------+-------------------------------------------------+
| pre                | String       | Text or html tag that would be added befor text |
+--------------------+--------------+-------------------------------------------------+
| post               | String       | Text or html tag that would be added after text |
+--------------------+--------------+-------------------------------------------------+

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
        <imcms:image no="1" document="1001" pre="<div>" post="</div>"/>
    </body>
    </html>


