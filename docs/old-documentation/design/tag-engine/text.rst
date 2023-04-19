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

+---------------------+--------------+--------------------------------------------------+
| Attribute           | Type         | Description                                      |
+=====================+==============+==================================================+
| no                  | Integer      | Identifier for current text                      |
+---------------------+--------------+--------------------------------------------------+
| document            | Integer      | Identify the linked document (default            |
|                     |              | - current document)                              |
+---------------------+--------------+--------------------------------------------------+
| version             | Integer      | Identify version of text                         |
+---------------------+--------------+--------------------------------------------------+
| placeholder         | String       | The text that was showed if native content are   |
|                     |              | empty                                            |
+---------------------+--------------+--------------------------------------------------+
| pre                 | String       | Text or html tag that would be added before      |
|                     |              | text tag                                         |
+---------------------+--------------+--------------------------------------------------+
| post                | String       | Text or html tag that would be added after text  |
|                     |              | tag                                              |
+---------------------+--------------+--------------------------------------------------+
|                     |              | Possible values:                                 |
|                     |              | ``read`` - means that text won't be editable     |
| mode                | String       | ``write`` - editable text, just as without       |
|                     |              | ``mode`` attribute                               |
|                     |              |                                                  |
+---------------------+--------------+--------------------------------------------------+
|                     |              | If set, format switch won't be able.             |
|                     |              | Possible values:                                 |
|                     |              | ``text`` - formatting panel will have only simple|
|                     |              | text editor options, content won't be represented|
|                     |              | as HTML                                          |
|                     |              | ``html`` - formatting panel will have HTML editor|
| formats             | String       | options, content will be represented as HTML     |
|                     |              | ``cleanhtml`` - formatting panel will have HTML  |
|                     |              | editor options, content will be represented as   |
|                     |              | HTML and all tags will be checked according to   |
|                     |              | tags whitelist - system property                 |
|                     |              | ``text.editor.html.tags.whitelist``, where tags  |
|                     |              | are separated by semicolon. Not allowed tags will|
|                     |              | be removed from content.                         |
+---------------------+--------------+--------------------------------------------------+
| label               | String       | Text label that is connected to current text tag |
+---------------------+--------------+--------------------------------------------------+
|                     |              | Set ``true`` if you want to see text label near  |
| showlabel           | String       | text tag content in admin edit mode              |
|                     |              |                                                  |
+---------------------+--------------+--------------------------------------------------+
|                     |              | Set ``true`` if you want to that be text field   |
|showEditToSuperAdmin | String       | will be show only for super admin                |
|                     |              |                                                  |
+---------------------+--------------+--------------------------------------------------+

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
        <imcms:text no="1" document="1001" pre="<div>" post="</div>" placeholder="<i>this text is empty</i>"
                    label="Test text tag" showlabel="true" formats="html", showEditToSuperAdmin="true"/>
    </body>
    </html>


