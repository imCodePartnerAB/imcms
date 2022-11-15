Image Tag
=========

The *Image* tag represents the :doc:`Image editor </user-documentation/editors/image/image-editor>` in Edit mode and
the content from the editor in Preview mode and on a published page.

.. seealso:: Read also the :doc:`Image editor </user-documentation/editors/image>` article.

.. code-block:: jsp

	<imcms:image index="1"/>

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
| label               | String  | the text editor title (visible to the admin).                                |
+---------------------+---------+------------------------------------------------------------------------------+
| showlabel           | Boolean | true (default) - show the label, false - don't show label.                   |
+---------------------+---------+------------------------------------------------------------------------------+
| style               | String  | min-width/length, width/length, max-width/length in pixels                   |
|                     |         | that limit the size of the image.                                            |
+---------------------+---------+------------------------------------------------------------------------------+
| pre                 | String  | text or html before the content (if the content is not empty).               |
+---------------------+---------+------------------------------------------------------------------------------+
| post                | String  | text or html after the content (if the content is not empty).                |
+---------------------+---------+------------------------------------------------------------------------------+
|                     |         | Possible values:                                                             |
|                     |         |                                                                              |
| showMode            | String  | * absence of this attribute/``default`` -                                    |
|                     |         |   the editor is highlighted in Edit mode.                                    |
|                     |         | * ``small`` - the editor in Edit mode looks like in the preview,             |
|                     |         |   but after clicking it opens the normal editor.                             |
+---------------------+---------+------------------------------------------------------------------------------+

.. warning:: Image tag must have ``no`` or ``index`` attribute!

*************
Usage example
*************

.. code-block:: jsp

	<imcms:image index="1" label="Test image tag" style="max-width: 120px; max-height: 120px;"/>

.. code-block:: jsp

	<imcms:image index="2" pre="<div>" post="</div>" showMode="small"/>
