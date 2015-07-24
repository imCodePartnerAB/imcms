Search Tag
==========


In this article:
    - `Introduction`_
    - `Use in template`_


Introduction
------------
ImCMS support powerful and flex search engine that integrate with well-known Apache Solr. Each document in the system index in solr.
Each content item on page indexing too: ImCMS provide searching by text content, document title, document alias, document menu description. Of course searching
depends on current language, that user has already been selected.


Use in template
---------------

For using search in template all that needed are insert search-tag in the desired place.
.. code-block:: jsp

    <imcms:search searchRequest="" skip="0" take="20">

    </imcms:search>


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









