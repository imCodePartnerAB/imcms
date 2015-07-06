Configure ImCMS
===============

In this article:
	- `Setting up database`_
	- `Setting up ImCMS`_

Setting up database
-------------------

Create a new database on your database server, as appropriate for your type of database server and your preferred default language.

Example:
^^^^^^^^
To create a **MySQL** database, a command like the following might be appropriate:

.. code-block:: console

	mysql -u <user> -p -e "CREATE DATABASE imcms CHARACTER SET utf8"

On **Microsoft SQL Server** you can use **"Enterprise Manager"** to create a database, or use "SQL Query Analyzer" to execute something like the following SQL command:

.. code-block:: console

         CREATE DATABASE imcms

See the documentation for your database server for details.

Setting up ImCMS
----------------

* Edit the file **"server.properties"** in the directory **"tomcat/webapps/imcms/WEB-INF/conf"**, and set the database parameters (``"JdbcDriver"``, ``"JdbcUrl"``, ``"User"``, ``"Password"``,  ``"MaxConnectionCount"``) according to your setup. 
  
  Set **"SmtpServer"** to the hostname of your SMTP (outgoing mail) server.

* If you want to add a file-extension to be recognized as a mime-type by imCMS, add a ``"<mime-mapping>"`` to **"tomcat/webapps/imcms/WEB-INF/web.xml"**.

  See `documentation <http://www.iana.org/assignments/media-types/>`_ for the mime-type registry.

* Restart Tomcat to reload the settings.

Example:
^^^^^^^^

Configuration below show, how to configure ImCMS for **MySQL**

.. code-block:: properties

   JdbcDriver = com.mysql.jdbc.Driver
   JdbcUrl = jdbc:mysql://localhost:3306/imcms?characterEncoding=utf8

   User = root
   Password = root

   MaxConnectionCount = 20


For more information see `advanced configuration </fundamental/advanced-configuration/index>`_ section