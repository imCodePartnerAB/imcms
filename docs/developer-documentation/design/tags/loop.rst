Loop Related Tags
=================

Loop related tags tag represents the :doc:`Loop editor </user-documentation/editors/loop>` in Edit mode and
the content from the editor in Preview mode and on a published page.

.. seealso:: Read also the :doc:`Loop editor </user-documentation/editors/loop>` article.

***************
СontentLoop Tag
***************

Opens/closes an area for placing cycles.

Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+---------------------+---------+------------------------------------------------------------------------------+
| Attribute           | Type    | Description                                                                  |
+=====================+=========+==============================================================================+
| index               | Integer | id of the current text (recommended version).                                |
+---------------------+---------+------------------------------------------------------------------------------+
| document            | Integer | id of the document from which to take the text.                              |
|                     |         | This text cannot be changed on this page.                                    |
+---------------------+---------+------------------------------------------------------------------------------+
| label               | String  | the text editor title (visible to the admin).                                |
+---------------------+---------+------------------------------------------------------------------------------+
| showlabel           | Boolean | true (default) - show the label, false - don't show label.                   |
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

.. warning:: Loop tag must have ``index`` attribute!

********
Loop tag
********

Opens/closes the loop body, specified inside the *contentLoop* tag.

Inside *loop* tag you can use **loopItem** variable - LoopEntry - represents the current loop.

*************
Usage example
*************

.. code-block:: jsp

        <imcms:contentLoop index="1" label="Loop editor example" pre="<div>" post="</div>">
            <div class="demo-loop">
                <imcms:loop>
                    <div>#${loopItem.index} Loop example for doc 1001</div>
                    <div class="demo-loop">
                        <imcms:image no="1"/>
                        <hr>
                        <imcms:text no="1"/>
                    </div>
                    <br>
                </imcms:loop>
            </div>
        </imcms:contentLoop>
