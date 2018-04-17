Start with ImCMS as a developer
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
* Your favorite IDE or text editor

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

    mvn package

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

