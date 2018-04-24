First Web Site
==============


#. Follow the :doc:`setup guide </getting-started/index>` to run ImCMS.

#. Now, if everything is ok you will get next page:

    .. image:: images/imcms-start-page-example.png

   it means that ImCMS system has started and now working perfectly.

#. Lets login as admin. Go to ``/login`` (don't miss context path if such exist). By default login and password is the same - ``admin``

#. If everything is okay you will see same page, but with **Admin Panel** (see :doc:`Admin Panel </content-management/admin-panel>` section).

    .. image:: images/imcms-logged-in-start-page-example.png

#.
    Lets create new document. Follow :doc:`Document Management </content-management/document/base>` guide for that.

#.
    Great - now you know how to create new page, but you can ask the question: *How should I fill content on my new page?* -
    This is the time to use **Edit Mode**. It gives an ability to manage all editable content you can find in
    :doc:`Content Management</content-management/index>` section. The simplest one is text editing, we will try to write
    our first *Hello World*.

    .. |editIcon| image:: images/imcms-panel-edit-button.png
        :width: 30pt
        :height: 30pt

    .. |saveIcon| image:: images/imcms-text-editor-save-button.png
        :width: 25pt
        :height: 25pt

    .. |editTextIcon| image:: images/icon_edit.png
        :width: 15pt
        :height: 15pt

    - Enable *Edit Mode* |editIcon| on the admin panel
    - Find pencil icon |editTextIcon| on the page and click it
    - Type "Hello World"
    - On Text Editor panel find button with |saveIcon| icon and click it
    - Text has been saved - reload page to be sure

#.
    Now everything in prepared but this page opened for every one and maybe you don`t want to show data on it.
    There are two things that helps you to prevent access to this page - close it for every one, or create special group of users.
    The better way is creating group of users and give them permissions to view current page - this has been described
    in :doc:`Role Management </content-management/role>` section.
