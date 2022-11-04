Start With ImCMS as a User
==========================

It is quite simple to start working with ImCMS.
But since you've prepared database, there are few more things that are required.

In this article:
    - `Installing Java`_
    - `Installing Tomcat`_
    - `ImCMS Configuration`_
    - `Running ImCMS`_


Installing Java
---------------

To use ImCMS you must have Java 14 installed on your machine.
You can use either `OracleJDK <https://www.oracle.com/cis/java/technologies/javase/jdk14-archive-downloads.html>`_
or `OpenJDK <http://openjdk.java.net/install/>`_.


Installing Tomcat
-----------------

Tomcat is a servlet container for running web applications such as ImCMS.
First of all, download latest Tomcat 9 from `official site <https://tomcat.apache.org/download-90.cgi>`_ and then
simply extract an archive into any folder. We will call the result tomcat directory with it's full path **tomcat_home**.


ImCMS Configuration
-------------------

Next step - `download latest ImCMS <http://repo.imcode.com/maven2/com/imcode/imcms/imcms/6.0.0-beta18/imcms-6.0.0-beta18.war>`_.
Then put downloaded file here: ``tomcat_home/webapps/``.
You can rename this file, so result will look like this: ``tomcat_home/webapps/imcms.war``.

Before making any other changes, it is required to configure ImCMS to use your database.
Open downloaded file by any archive manager, and then open this file: ``/WEB-INF/conf/server.properties``.
Find property ``JdbcUrl`` and change it by setting up correct database name like this:
``JdbcUrl = jdbc:mysql://localhost:3306/<db-name>?characterEncoding=utf8``
Then edit database login section:

.. code-block:: properties

    # Database login
    User = #your database user name#
    Password = #your database user password#

Save the file and basic ImCMS configuration finished.

Running ImCMS
-------------

After basic configuration done, you can run ImCMS on Tomcat. To do so, go to this path: ``tomcat_home/bin``
and run startup script in terminal:

.. code-block:: console

    sh startup.sh

In the end of result message you should receive this: **Tomcat started.**
Tomcat started on standard path, check in your browser: http://localhost:8080/manager/
You should see this:

    .. image:: images/tomcat-imcms-example.png

In *Applications* section find path **/imcms** (or how you renamed downloaded file) with state *Running* - true.
Click on path and you should see this:

    .. image:: images/imcms-start-page-example.png

If not, check each step twice to be sure that you've done all things right.

