StorageFile
===========

Represents a file from the storage.

.. warning:: You must close the instance after using.

-------
Methods
-------

.. code-block:: java

    StoragePath getPath();

    long lastModified();

    long size();

------------------

.. code-block:: java

    InputStream getContent()

returns inputStream of the file. Do not close inputStream if you want to use more than once.
The method does not support multithreading.
