Access control
==============

In this article:
    - `Introduction`_
    - `Role permissions (the whole system)`_
    - `Document permissions (single document)`_
    - `Additional explanations`_

------------
Introduction
------------

ImCMS 6 provides flexible access control. Each user has at least one role.
A role is used to classify users and grant them certain permissions.

By default, the system has 2 roles:

* **Users** - does not give any permissions, each user has this role.

* **Superadmin** - gives permissions for absolutely any actions.

.. note:: Each user has *Users* role.

We specify permissions for the entire system when :doc:`creating a role</user-documentation/admin-settings/roles>`.
Then we grant permissions to these roles in a document separately when :doc:`creating the document</user-documentation/document-management/page-info/base>`.

-----------------------------------
Role permissions (the whole system)
-----------------------------------

Location - *Admin* page -> *Roles* tab

* **Get password by email** - gives access to recover password by sending confirmation code to email (Login page -> *Forgot password* button).

* **Access to admin pages** - gives access to the *Site Specific* tab and the *Admin page*, but with limitations.
    * No access to *Ip Access*, *Files*, *Profiles*, *System Properties*, *Index/cache*, *Data-version*, *Documentation* tabs.
    * *Users* tab - a user with this permission cannot edit superadmins and doesn't see them when searching. Also, a such user cannot give the *Superadmin* role when creating/editing.

* **Access to document editor** - gives access to the *Document Manager*. But only the user with the necessary document permission (*EDIT* or *Edit doc info*, see below) can open the *Page Info* of a specific document.

* **Publish own created documents** - the user can publish own documents.

* **Publish all documents (only with EDIT-permission)** - the user can publish any document if he has EDIT permission for the document.

--------------------------------------
Document permissions (single document)
--------------------------------------

Location *Page Info* -> *Access* and *Permission Settings* tabs

* **VIEW** - the role can see page of the document, the document when searching and in the menu.
* **EDIT** - the role can edit all content and has full access to page info + **VIEW**
* **Restricted 1/2**
	* **Edit text** - access to text editors + **VIEW**
	* **Edit menu** - access to menu editors + **VIEW**
	* **Edit image** - access to image editors + **VIEW**
	* **Edit loop** - access to loop editors + **VIEW**
	* **Edit doc info** - access to *Page Info*, with limitations (No access to *Permission Setting*, *Properties*, *All data* tabs) + **VIEW**

* **VIEW for all users** - **VIEW** for all users - all users can see page the document, the document when searching and in the menu.

* **Visibility in the menu for authorized users** - visibility of this document in the menu for authorized users (menu admins see all items).
* **Visibility in the menu for unauthorized users** - visibility of this document in the menu for unauthorized users.

------------------

Location *Page Info* -> *Keywords* tab

* **Disable search** - disable the search for this document.

-----------------------
Additional explanations
-----------------------

An **unauthorized user see the document** page only if **VIEW for all users** is enabled and the **document is published**.

In order to make a **document visible** to a specific user **when searching**, you have to:

1. Enable **VIEW for all users** or give **VIEW** for one of the user's roles.
2. **Disable** *Disable search*.

In order to make a **document visible** to a specific user **in a menu**, you have to:

1. Make sure the document is **published**.
2. Enable **VIEW for all users** or give **VIEW** for one of the user's roles.
3. Enable **Visibility in the menu for authorized users**.