Text Management
===============

In this article:
    - `Introduction`_
    - `Open Editor`_
    - `Editor Features`_

------------
Introduction
------------

ImCMS 6 provide feature - on-place text-editor, that can help easily change text on page.

-----------
Open Editor
-----------

There are several way to open **Text Editor**:

    1. Over text editor`s *label*:
        - Enable **Edit Mode**

        - Find on the left side of the page blue label with text **Text Editor** (as shown below)
        and click on it to open the editor. When label hovered - the link text is highlighted.


        .. image:: menu/_static/01-TextEditorLabel.png


    2. Since **contenteditable** attribute presented in all browser text editor will be opened when content is start changing.


---------------
---------------
Editor Features
---------------

ImCMS text editor is **`CKEditor <http://ckeditor.com/>`_**. All information about this text editor presented on `official site <http://ckeditor.com/demo>`.

But ImCMS customize a bit this great editor and provide own features:

    .. |linkIcon| image:: text/_static/02-LinkEditorIcon.png
        :width: 20pt
        :height: 20pt


    1. |linkIcon| ``Link`` - ImCMS provide easy way to add document as link. Together with the usual CKEditor linking plugin methods,
    ImCMS provide own feature that call *Search Document Dialog* (figure below).


    .. image:: text/_static/03-SearchDocumentDialog.png


    It can be opened from default CK Link Editor

    .. image:: text/_static/04-LinkEditor.png


    ----------------------------------------------------------------
    .. |imageBrowserIcon| image:: text/_static/05-ImageBrowserIcon.png
        :width: 20pt
        :height: 20pt


    2. |imageBrowserIcon| ``Image Browser`` - this feature give access to default ImCMS Content Manager