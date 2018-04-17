Start with ImCMS as a user
==========================

It is quite simple to to start working with ImCMS.
But since you've prepared database, there are few more things that are required.

In this article:
    - `Installing Java`_
    - `Installing Tomcat`_
    - `ImCMS Configuration`_
    - `Running ImCMS`_


Installing Java
---------------

To use ImCMS you have to have java8 installed on your machine.
You can use either `OracleJDK <https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html>`_
or `OpenJDK <http://openjdk.java.net/install/>`_.


Installing Tomcat
-----------------

Tomcat is a servlet container for running web applications such as ImCMS.
First of all, download latest Tomcat 8 from `official site <https://tomcat.apache.org/download-80.cgi>`_ and then
simply extract an archive into any folder. We will call the result tomcat directory with it's full path **tomcat_home**.


ImCMS Configuration
-------------------

Next step - `download latest ImCMS <http://repo.imcode.com/maven2/com/imcode/imcms/imcms/6.0.0-rc1/imcms-6.0.0-rc1.war>`_.
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
In *Applications* section you should see path **/imcms** with state *Running* - false.
