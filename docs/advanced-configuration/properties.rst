Properties
==========

In this article:
    - `Introduction`_
    - `Server properties`_
    - `Properties in other files`_

------------
Introduction
------------

In ImCMS we have convenient service for working with ``*.properties`` files - ``imcode.util.PropertyManager``. This class
has own cache of properties files for quick access.

Server properties
"""""""""""""""""

While starting application, ImCMS automatically read its root path and goes to ``WEB-INF/conf/server.properties`` to
read server properties. Only after this moment we can read server properties from cache. If you get
``FileNotFoundException`` or ``NullPointerException`` from PropertyManager, there are two explanations:

#. You point wrong path to properties file.

#. You want to read some properties from any file (and server too) before ImCMS read its root path.

So you have to check arguments (first of all). If not helps, there are two solutions:

* Wait when ImCMS reads it's root path automatically (recommended).

* Set root path manually if ImCMS do not do that yet.

ImCMS sets path from system root to ``target`` folder. Try to find root path from something like:

.. code-block:: java

    ServletContext servletContext = filterConfig.getServletContext();
    String rootPath = servletContext.getRealPath("/");

And then set root by next method:

.. code-block:: java

    PropertyManager.setRoot(String rootPath);

or if you find path by another way in ``File`` type:

.. code-block:: java

    PropertyManager.setRoot(File rootPath);

**! Be sure that root path is set by you or ImCMS before read any properties !**

If root path is set, properties from ``server.properties`` can be accessed by next methods:

-
    read the value of ``property`` from server properties:
    .. code-block:: java

        PropertyManager.getServerProperty(String property);
-
    returns server properties in ``Properties`` type for next use:
    .. code-block:: java

        PropertyManager.getServerProperties();


Properties in other files
"""""""""""""""""""""""""

**! Be sure that root path is set by you or ImCMS before read any properties !**

If you need to read some properties from another files, you may use next methods:

-
    read the value of ``property`` from properties file by specified path:
    .. code-block:: java

        PropertyManager.getPropertyFrom(String path, String property);

-
    returns properties which lies by specified path in ``Properties`` type for next use:
    .. code-block:: java

        PropertyManager.getPropertiesFrom(String path);
