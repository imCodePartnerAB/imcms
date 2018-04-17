Before You Start
================

It is required to have `MySQL <https://dev.mysql.com/doc/refman/5.7/en/installing.html>`_ database server installed
for ImCMS.

Setting up database
-------------------

ImCMS works fine with **MySQL 5.7** but may work on higher versions too.
Create new database on your database server, as appropriate for your type of database server
and your preferred default language.

Example:
^^^^^^^^
To create a MySQL database, a command like the following might be appropriate:

.. code-block:: console

    mysql -u <user> -p -e "CREATE DATABASE <db-name> CHARACTER SET utf8 COLLATE utf8_swedish_ci"

Here is an example of such command execution:

    .. image:: db-creation-example.png

After you write the password for mysql user new database is created and is ready to use.
Don't forget your DB name, it will be needed soon for next ImCMS configuration.
