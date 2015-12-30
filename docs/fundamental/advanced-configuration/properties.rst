Properties
==========

In this article:
    - `Introduction`_
    - `Server properties`_
    - `Properties in other files`_

------------
Introduction
------------

In ImCMS we have convenient service for working with *.properties files - ``imcode.util.PropertyManager``.
This class has own cache of properties files for quick access.

Server properties
"""""""""""""""""

While starting application, ImCMS automatically read its root path and goes to ``WEB-INF/conf/server.properties`` to
read server properties. Properties from this file can be accessed by next methods:

.. code-block:: java

    PropertyManager.getServerConfProperty(String property);

- read the value of ``property`` from server properties.

.. code-block:: java

    PropertyManager.getServerConfProperties();

- returns server properties in ``Properties`` type for next use.

Properties in other files
"""""""""""""""""""""""""

If you need to read some properties from another files, you may use next methods:

.. code-block:: java

    PropertyManager.getPropertyFrom(String path, String property);

- read the value of ``property`` from properties file by specified path.

.. code-block:: java

    PropertyManager.getIntegerPropertyFrom(String path, String property);

- read the Integer value of ``property`` from properties file by specified path.

.. code-block:: java

    PropertyManager.getPropertiesFrom(String path);

- returns properties which lies by specified path in ``Properties`` type for next use.