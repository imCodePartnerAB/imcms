Base Information
================

In this article:
    - `Document Types`_
    - `Document Versioning`_
    - `Languages`_

--------------
Document Types
--------------

The main element of the system is a **document** with settings.

There are three types of documents:

* Text Document - represents a web page.
* File Document - represents a file or multiple files. When a user visits such a document, the system returns the default file (more :doc:`here </user-documentation/document-management/page-info/file-document>`).
* Url Document - represents a link. When a user visits such a document, the system redirects to the specified link.

-------------------
Document Versioning
-------------------

Each document has a working and published version. The regular user sees only the published version (if it exists).
Only an administrator can see and make changes to the working version.
When the administrator publishes a version, the document is published with all changes.
In order to publish the document you have to use the *Publish* button in the *Admin Panel* or
*Save And Publish* button in the *Page Info*.

.. note:: The regular user only sees the published version of the document!

Changes in any content editor, in *Title*, *Metadata* Tabs in the *Page Info* are made to the working version.
Other changes are made simultaneously to the working and published versions.

*********************************
Additionally - disable versioning
*********************************

Versioning can be disabled.
In this case, the publishing feature is omitted and the regular user sees the working version.

---------
Languages
---------

The system has *available languages* and a *default language* (one of the available language which is used when language preference are unknown).

The administrator can enable/disable the language for a document. The content displayed on the page depends on the language.

The user can change the language of the document by specifying a language code in a ``lang`` parameter.
For example, ``<domain-name>/<document-alias>?lang=sv``

The user also has a specific language, which affects the interface language of the imCMS.
