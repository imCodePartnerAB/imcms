Start With ImCMS as a Developer
===============================

In this article:
    - `Required things`_
    - `Download sources`_
    - `Building application`_


Required things
---------------

There are some additional requirements if you want to develop ImCMS:

* git
* Maven 3+
* Your favorite IDE, text editor or even only terminal

Download sources
----------------

At first you need to clone ImCMS from github by this link: https://github.com/imCodePartnerAB/imcms.git


Building application
--------------------

Maven is used as a build tool so run next command from project root after sources downloaded:

.. code-block:: console

    mvn clean

Success execution means that all was done right.

Then package it:

.. code-block:: console

    mvn clean package

Your first package should fail with this message:

.. warning:: `build.properties` file was just created, fill in required properties and run execution again

Open this file (it is located in project root directory) ``build.properties``.
Besides a lot of defaults, find this:

.. code-block:: properties

    db-host = localhost
    db-name = imcms
    db-user = root
    db-pass =

And write down your database host, name, user and password.

It's not the end! There are a lot of tests that should be executed on maven's package phase.
Test DB is needed for this purposes. Go to ``src/test/resources/test.server.properties``
and write down correct values for next properties:

.. code-block:: properties

    JdbcDriver = com.mysql.jdbc.Driver
    JdbcUrl = jdbc:mysql://localhost:3306/imcms_test?characterEncoding=utf8&useSSL=false
    User = root
    Password = root

Pay attention to ``JdbcUrl`` property - there is a DB name after ``localhost:3306/``, by default it is ``imcms_test``,
so you can create DB with such name (just like in :doc:`Before You Start </getting-started/setup/before>` section)
or with another name - then simply put this name into ``test.server.properties`` file instead of default DB name.

Since you've done, execute maven clean package again:

.. code-block:: console

    mvn clean package

All tests should be executed successfully, application built and ready to use. Deploy it to Tomcat and run.
