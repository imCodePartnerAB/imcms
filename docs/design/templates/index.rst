Templates
=========

*Describes how to create/modify templates in ImCMS*

In this article:
    - `Create new template`_
    - `Modify template`_
    - `Summary`_


Create new template
-------------------

#. Create new file with ``jsp`` extension in ``WEB-INF/templates`` directory.

#. Add following code to created file


    .. code-block:: jsp

        <!DOCTYPE html>
        <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
        <head>
            <title>Template</title>
            <meta charset="utf-8"/>
        </head>
        <body>
            Hello World with New Template!
        </body>
        </html>


#. Run application using **Tomcat** (make a package if you are using maven, of package only this new file if you are using your IDE).

#. Login into ImCMS as Admin. Check :doc:`Logging in </tutorial/login>` section.

#.
    Open **Page info** on **Admin Panel** (see :doc:`Admin Panel guide </content-management/admin-panel>`),
    choose **Appearance** tab and select your file in **Template** list.

#. Save changes by clicking **"OK"** button


---------------
Modify template
---------------

Modify your templates in ``/WEB-INF/templates`` directory until you're done. Keep your templates as simple as possible.
Try to use one template per document, move common used parts into separated JSP files with their including. Logic can be
moved to functions in TLDs. Use JSTL tags over scriptlets.


Summary
-------

Now you know how to create, modify and use templates.

