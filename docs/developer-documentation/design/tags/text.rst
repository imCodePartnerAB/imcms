Text Tag
========

The *Text* tag represents the :doc:`Text editor </user-documentation/editors/text>` in Edit mode and
the content from the editor in Preview mode and on a published page.

.. seealso:: Read also the :doc:`Text editor </user-documentation/editors/text>` article.

.. code-block:: jsp

	<imcms:text index="1"/>

Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+---------------------+---------+------------------------------------------------------------------------------+
| Attribute           | Type    |  Description                                                                 |
+=====================+=========+==============================================================================+
| no                  | Integer | id of the current text (deprecated version).                                 |
+---------------------+---------+------------------------------------------------------------------------------+
| index               | Integer | id of the current text (recommended version).                                |
+---------------------+---------+------------------------------------------------------------------------------+
| document            | Integer | id of the document from which to take the text.                              |
|                     |         | This text cannot be changed on this page.                                    |
+---------------------+---------+------------------------------------------------------------------------------+
| placeholder         | String  | the text to show if there is no content.                                     |
+---------------------+---------+------------------------------------------------------------------------------+
| label               | String  | the text editor title (visible to the admin).                                |
+---------------------+---------+------------------------------------------------------------------------------+
| showlabel           | Boolean | true (default) - show the label, false - don't show label.                   |
+---------------------+---------+------------------------------------------------------------------------------+
|                     |         | Possible values:                                                             |
|                     |         |                                                                              |
|                     |         | * absence of this attribute - the Edit mode will show the text editor,       |
| mode                | String  |   the Preview and the published page will show the content.                  |
|                     |         | * ``read`` - the Edit mode will now show the text editor,                    |
|                     |         |   but the Preview and the published page will show the content.              |
|                     |         | * ``write`` - the Edit mode will show the text editor,                       |
|                     |         |   but the Preview and the published page will not show the content.          |
+---------------------+---------+------------------------------------------------------------------------------+
|                     |         | Possible values:                                                             |
|                     |         |                                                                              |
| formats             | String  | * absence of this attribute -                                                |
|                     |         |   editor takes *what-you-see-is-what-you-get* type.                          |
|                     |         | * ``text`` - editor takes *plain text* type.                                 |
|                     |         | * ``html`` - editor takes *html* type.                                       |
+---------------------+---------+------------------------------------------------------------------------------+
| pre                 | String  | text or html before the content (if the content is not empty).               |
+---------------------+---------+------------------------------------------------------------------------------+
| post                | String  | text or html after the content (if the content is not empty).                |
+---------------------+---------+------------------------------------------------------------------------------+
| showEditToSuperAdmin| String  | text editor visibility in Edit mode only for superadmin.                     |
+---------------------+---------+------------------------------------------------------------------------------+
|                     |         | Possible values:                                                             |
|                     |         |                                                                              |
| showMode            | String  | * absence of this attribute/``default`` -                                    |
|                     |         |   the editor is highlighted in Edit mode.                                    |
|                     |         | * ``small`` - the editor in Edit mode looks like in the preview,             |
|                     |         |   but after clicking it opens the normal editor.                             |
+---------------------+---------+------------------------------------------------------------------------------+

.. warning:: Text tag must have ``no`` or ``index`` attribute!

*************
Usage example
*************

.. code-block:: jsp

	<imcms:text index="1" mode="read"/>
	<imcms:text index="1" mode="write" showEditToSuperAdmin="true"/>

.. code-block:: jsp

	<imcms:text index="2" document="1001" placeholder="<i>this text is empty</i>" label="Test text tag" showlabel="true"/>

.. code-block:: jsp

	<imcms:text index="3" document="1001" pre="<div>" post="</div>" formats="html" showMode="small"/>
