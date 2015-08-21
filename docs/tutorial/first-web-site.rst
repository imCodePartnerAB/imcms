First Web Site
==============

In this article:



1. First that you need it is download the `Apache Tomcat <http://tomcat.apache.org/download-80.cgi>`_.

2. Check if :doc:`all required part </getting has installed yet.

   3. Download :download:`ImCMS distribution file <http://repo.imcode.com/maven2/com/imcode/imcms/imcms/SNAPSHOT/>` and follow :doc:`Installation guide </getting-started/setup/install>`.

4. If everything is OK you will get something like that:


    .. image:: first-web-site/_static/01-FirstStartUp.png


    that is mean that your database configuration has not prepared yet. To prepare your configuration follow :doc:`Configuration guide </getting-started/setup/configuration>`.

5. Now, if everything is ok you will get next page:


    .. image:: first-web-site/_static/02-WelcomeImCMSPage.png


    that is mean that ImCMS system has started and now working perfectly.

6. Let's login as admin. There are two way to login in the system:

    - Find in the top right corner button ``SignIn`` end hover it.

    - Type in your browser address ``http://localhost:8080/imcms/login/``.

By default way login and password is the same - ``admin``

7. If everything is okay you will see same page, but with **Admin Panel** (see :doc:`Admin Panel </content-management/admin-panel>` section).


    .. image:: first-web-site/_static/03-PageWithAdminPanel.png

8. Let's create new document. Click on Admin Panel **Document List** and follow :doc:`Document Management </content-management/document/index>` guide.

9. It is good, if document has been created, but nobody except you don`t know about it, soo let's add created document to menu.
To add document to menu consider :doc:`Menu Management </content-management/menu>` topic.

10. Great - now you know how to create page and add it to menu, but you ask the question - 'How I should fill content on my new page'.
This is the time to enable **Edit Mode** - edit mode give ability to edit all editable content on the page all editable element you can find on :doc:`Content Management</content-management/index>` section.
Consider text editing, we try to write our first *Hello World*.


.. |saveIcon| image:: text/_static/04-ApplyTextEditingIcon.png
    :width: 20pt
    :height: 20pt


- Enable *Edit Mode*
- Find on the left side of page label with text **Text Editor** and click it.
- Put "Hello World"
- On Text Editor panel find button with |saveIcon| icon and click it.
- Reload page to be sure that text has been saved.


