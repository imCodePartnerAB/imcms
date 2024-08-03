Image Data Tag
==============

The *Image Data* tag represents the Alternate text and Description text.

.. seealso:: Read also the :doc:`Image Attributes </user-documentation/editors/image/image-editor.html#image-attributes>` article.

.. code-block:: jsp

    <imcms:image:data no="100">
        <div>
            <b>Image data:</b>
            <div>Image alternateText: ${alternateText}</div>
            <div>Image descriptionText: ${descriptionText}</div>
        </div>
    </imcms:image:data>

Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+---------------------+---------+------------------------------------------------------------------------------+
| Attribute           | Type    |  Description                                                                 |
+=====================+=========+==============================================================================+
| no                  | Integer | id of the current text (deprecated version).                                 |
+---------------------+---------+------------------------------------------------------------------------------+
| document            | Integer | id of the document from which to take the text.                              |
+---------------------+---------+------------------------------------------------------------------------------+

.. warning:: Image tag must have ``no`` attribute!
