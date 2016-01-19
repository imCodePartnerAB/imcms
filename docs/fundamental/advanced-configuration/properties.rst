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
read server properties. Only after this moment we can read server properties from cache. If you want to read some
properties from any file (and server too) before ImCMS reads root path, you have to set root path manually using
servletContext etc. (examples will be shown later).

If root path is set, properties from ``server.properties`` can be accessed by next methods:

- read the value of ``property`` from server properties:
.. code-block:: java

    PropertyManager.getServerConfProperty(String property);

- returns server properties in ``Properties`` type for next use:
.. code-block:: java

    PropertyManager.getServerConfProperties();


Properties in other files
"""""""""""""""""""""""""

If you need to read some properties from another files, you may use next methods:

- read the value of ``property`` from properties file by specified path:
.. code-block:: java

    PropertyManager.getPropertyFrom(String path, String property);

- read the Integer value of ``property`` from properties file by specified path:
.. code-block:: java

    PropertyManager.getIntegerPropertyFrom(String path, String property);

- returns properties which lies by specified path in ``Properties`` type for next use:
.. code-block:: java

    PropertyManager.getPropertiesFrom(String path);
