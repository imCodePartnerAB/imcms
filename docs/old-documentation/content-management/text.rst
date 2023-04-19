Text Management
===============

In this article:
    - `Introduction`_
    - `Open Editor`_
    - `Editor Features`_

------------
Introduction
------------

ImCMS 6 provides text and html editing feature - on-place text-editor, that can help easily change text on page.

-----------
Open Editor
-----------

In ImCMS, there are three modes of text editor: plain text, html and what-you-see-is-what-you-get editor.

There are several ways to open **Text Editor** :

#.
    Over text editor's *label*:

    - Go **Edit Mode** on admin panel. On each text tag in this mode you will see the pencil icon:
        .. image:: text/_static/00-TextEditor.png

    - Click on it:
        .. image:: text/_static/01-TextEditorLabel.png
        .. image:: text/_static/text-editor-active.png

#.
    Over *direct editing* feature - go to /api/admin/text?meta-id={id-of-doc-here}&index={index-number-of-text},
    example: http://imcms.dev.imcode.com/api/admin/text?meta-id=1001&index=1

#.
    Currently, instead `no`, we have to use `index`

---------------
Editor Features
---------------

ImCMS text editor is `TinyMCE v4 <https://www.tiny.cloud/>`_. All information about this text editor presented on `official site <https://www.tiny.cloud/docs/>`_.

But ImCMS customize a bit this great editor and provides own features:

.. |imageBrowserIcon| image:: text/_static/05-ImageBrowserIcon.png
    :width: 20pt
    :height: 20pt

.. |textHistoryIcon| image:: text/_static/text_history.png
    :width: 20pt
    :height: 20pt

.. |w3cValidationIcon| image:: text/_static/ic_w3c.png
    :width: 20pt
    :height: 20pt

.. |switchToPlainTextIcon| image:: text/_static/ic_plain_text.png
    :width: 20pt
    :height: 20pt

.. |switchToHtmlIcon| image:: text/_static/ic_html.png
    :width: 20pt
    :height: 20pt

.. |switchToEditorIcon| image:: text/_static/ic_text_editor.png
    :width: 20pt
    :height: 20pt

.. |contentFilteringPoliciesIcon| image:: text/_static/ic_filter.png
    :width: 20pt
    :height: 20pt

- |imageBrowserIcon| ``Image Browser`` - this feature gives access to default ImCMS Image Editor. You can add and edit images in text

- |textHistoryIcon| ``Text History`` - you can review the history of all changes for current text

- |w3cValidationIcon| ``W3C Validation`` - validates current text/html

- |switchToPlainTextIcon| ``Switch to Plain Text Editor Mode`` - you can switch to this mode for plain text edition, all TinyMCE features will be disabled

- |switchToHtmlIcon| ``Switch to HTML Edit Mode`` - you can switch to this mode for HTML code edition, all TinyMCE features will be disabled

- |switchToEditorIcon| ``Switch to TinyMCE Editor Mode`` - you can switch from text/html mode back to TinyMCE editor

- |contentFilteringPoliciesIcon| ``HTML Content Filtering Policy`` - approach to filter HTML content. It has three options:

    - Restricted:

        - ``head``, ``script``, ``embed``, ``style`` : tags + content between tags are removed
        - ``html``, ``body``, ``doctype`` : only tags are removed
        - ``class``, ``style`` and unknown attributes are removed. Known attributes are ``src`` ``href`` ``rel`` ``alt`` ``align`` ``width`` ``height`` ``border`` ``cellspacing`` ``cellpadding`` ``target`` ``title``  etc. are kept

    - Relaxed:

        - ``head``, ``script``, ``embed``, ``style`` : tags + content between tags are removed
        - ``html``, ``body``, ``doctype`` : only tags are removed
        - ``class``, ``style`` and unknown attributes are kept

    - Everything Is Allowed

