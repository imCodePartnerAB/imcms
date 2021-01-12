Base Management
===============

In this article:
    - `Introduction`_
    - `Create Document`_
    - `Remove Document`_
    - `Edit Life Cycle`_
    - `Edit Appearance`_
    - `Edit Access`_
    - `Edit Keywords`_
    - `Edit Categories`_

------------
Introduction
------------

This article describes basic document creating and configuring.

---------------
Create Document
---------------

Today there are two way of document creation:

#.
    Create document in document manager:
    - Click on *Documents* on *Admin Panel*

    .. image:: base/_static/01-OpenDocumentManager.png

    - In opened window click on *+* button to open *new document dialog*

    .. image:: base/_static/02-CreateNewDocument.png

    - Opened window is *document prototyping* window: **Document Type** - document type such as Text Document, or File Document, etc; **Document Parent** - document's parent document, that will be inherited for current document.

    .. image:: base/_static/03-PrototypeNewDocument.png


    - Click *OK* button, and configure document in opened window.

#.
    Create document in menu manager
    * Enable **Edit Mode** as shown below

    .. image:: base/_static/04-EnableEditMode.png

    * Find **Menu Editor** label on the right side of page an click on it for open **Menu Editor**

    .. image:: base/_static/05-MenuEditorLabel.png

    * Click on **+** button

    .. image:: base/_static/06-CreateNewDocumentFromMenu.png

    * Follow steps from Create Document in Document Manager section.


.. _label:
    * nothing


---------------
Remove Document
---------------

Select multi remove documents in editors and select documents which we would want to remove.


#.

   - Click on *Multi-Remove* and choose checkbox on document, which we would want to remove:
   .. image:: base/_static/multi-remove.png

---------------
Edit Life Cycle
---------------

ImCMS provide base document managing, that help change document status. There are 3 types of life cycle exists:

- In Process - it is mean that document has just been created and it is preparing now.
- Approved - this status says that document is ready to use.
- Disapproved - document is disabled, and cannot be accessed.


.. image:: base/_static/07-EditLifeCycle.png

---------------
Edit Appearance
---------------

This editor section provides access to manage documents alias, how document will be opened from menu (in new window, in same frame, etc)
document name, description, link image for all available in ImCMS system languages.


.. image:: base/_static/08-EditAppearance.png

-----------
Edit Access
-----------

Since ImCMS is care about document securing it provide editor section for configure user-role linking with permission set for current document.
There are five types of roles:

- NONE - no access to current page
- VIEW - role with this permission can only view content on this page
- RESTRICTED 1 - first custom permission set
- RESTRICTED 2 - second custom permission set
- FULL - role with this permission have access to any configuration of current document (usual it is **admin**)


More information about custom permission sets configuration is in :doc:`Text Document Management </content-management/document/text-document>` section

.. image:: base/_static/09-EditAccess.png

-------------
Edit Keywords
-------------

Each document in ImCMS system is indexed by the search system, that called :doc:`Solr </advanced-configuration/solr>`.
That's why it is very important to mark document with special keywords that make searching easily.

.. image:: base/_static/10-EditKeywords.png

---------------
Edit Categories
---------------

Since ImCMS provide categories, each document can categorized. It should be noted, that one document can be assigned to
several categories at the same time if category type support multiply selecting.

.. image:: base/_static/11-EditCategories.png


-------------------
Document Versioning
-------------------

Imcms has versioning feature. Which can be disabled or enabled in properties.

If versioning is enabled:
To apply changes press publish button on admin panel. Until that plain users. Will not seee any changes.
Also you can preview new content before making publish by appropriate button on admin panel.

If versioning is disabled:
No additional actions required.


.. warning:: If previously versioning was disabled by properties, all existed documents created during that period would be immediately published after change even if versioning will be enabled again. In order to make local changes to go to public version and press ``Publish`` after that versioning will start work on that document.
