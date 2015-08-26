First Web Site
==============

In this article:



1. First that you need it is download the `Apache Tomcat <http://tomcat.apache.org/download-80.cgi>`_.

2. Check if :doc:`all required part </getting-started/setup/requirement>` has installed yet.

3. Download :download:`ImCMS distribution file <http://repo.imcode.com/maven2/com/imcode/imcms/imcms/SNAPSHOT/imcms-20150820.105717-1.war>` and follow :doc:`Installation guide </getting-started/setup/install>`.

4. If everything is OK you will get something like that:


    .. image:: first-web-site/_static/01-FirstStartUp.png


    that is mean that your database configuration has not prepared yet. To prepare your configuration follow :doc:`Configuration guide </getting-started/setup/configuration>`.

5. Now, if everything is ok you will get next page:


    .. image:: first-web-site/_static/02-WelcomeImCMSPage.png


   that is mean that ImCMS system has started and now working perfectly.

6. Lets login as admin. There are two way to login in the system:

    - Find in the top right corner button ``SignIn`` end hover it.

    - Type in your browser address ``http://localhost:8080/imcms/login/``.

   By default way login and password is the same - ``admin``

7. If everything is okay you will see same page, but with **Admin Panel** (see :doc:`Admin Panel </content-management/admin-panel>` section).


    .. image:: first-web-site/_static/03-PageWithAdminPanel.png

8. Lets create new document. Click on Admin Panel **Document List** and follow :doc:`Document Management </content-management/document/index>` guide.

9. It is good, if document has been created, but nobody know, except you, about it, soo let's add created document to menu.
To add document to menu consider :doc:`Menu Management </content-management/menu>` topic.

10. Great - now you know how to create page and add it to menu, but you ask the question - 'How I should fill content on my new page'.
This is the time to enable **Edit Mode** - edit mode give ability to edit all editable content on the page all editable element you can find on :doc:`Content Management</content-management/index>` section.
Consider text editing, we try to write our first *Hello World*.


.. |saveIcon| image:: first-web-site/_static/04-ApplyTextEditingIcon.png
    :width: 20pt
    :height: 20pt


- Enable *Edit Mode*
- Find on the left side of page label with text **Text Editor** and click it.
- Put "Hello World"
- On Text Editor panel find button with |saveIcon| icon and click it.
- Reload page to be sure that text has been saved.



11. Now - everything in prepared but this page opened for every one and maybe you don`t want to show data on it.
There are two things that helps you to prevent access to this page - close it for every one, or create special group of users.
The better way is  creating group of users and give them permissions to view current page. Since users grouped by role lets create new
role in system - this operation has been described in :doc:`Role Management </content-management/role>` section.

