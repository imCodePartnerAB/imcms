Image Tag
=========

In this article:
    - `Introduction`_
    - `Use in template`_


Introduction
------------
Today, maybe, there are no pages that contains only text content. It can contains topic, or news and of course it contain logo image, or preview image.
That is why it is very important to flexibly configure images. ImCMS provide own ``image`` tag that give easy access to image, stored in the system.

Use in template
---------------

For using image in template all that needed are insert image-tag in the desired place.

Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+--------------------+--------------+--------------------------------------------------+
| Attribute          | Type         | Description                                      |
+====================+==============+==================================================+
| no                 | Integer      | Identifier for current image                     |
+--------------------+--------------+--------------------------------------------------+
| document           | Integer      | Identify the linked document (default            |
|                    |              | - current document)                              |
+--------------------+--------------+--------------------------------------------------+
| version            | Integer      | Identify version of image                        |
+--------------------+--------------+--------------------------------------------------+
| id                 | String       | Html attribute ``id``                            |
+--------------------+--------------+--------------------------------------------------+
| pre                | String       | Text or html tag that would be added before      |
|                    |              | image tag                                        |
+--------------------+--------------+--------------------------------------------------+
| post               | String       | Text or html tag that would be added after image |
|                    |              | tag                                              |
+--------------------+--------------+--------------------------------------------------+
| styleClass         | String       | Add html attribute ``class`` to image            |
+--------------------+--------------+--------------------------------------------------+
| style              | String       | Add html attribute ``style`` to image            |
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
        <imcms:image no="1" document="1001" pre="<div>" post="</div>"/>
    </body>
    </html>


