StoragePath
===========
**com.imcode.imcms.storage**

In this article:
    - `Introduction`_
    - `Initialization`_
    - `Methods`_
    - `Additional classes`_

------------
Introduction
------------

Represents the file path and file type. Directories and files are separated by ``StoragePath.PATH_SEPARATOR`` (``/``).

*Directory - directory1/directory2/*

*File - directory1/file.txt*

.. note:: If the file type is directory, the path ends with ``/``.

--------------
Initialization
--------------

There is no public constructor but you can use a static method to get an instance.

.. code-block:: java

    static StoragePath get(FileType type, String... paths)

-------
Methods
-------

.. code-block:: java

    StoragePath resolve(FileType type, String... paths)

appends the given paths to the current path.

------------------

.. code-block:: java

    StoragePath resolve(FileType type, StoragePath... paths)

appends the given paths to the current path

------------------

.. code-block:: java

    StoragePath relativize(StoragePath path)

removes the current path from the given path

------------------

.. code-block:: java

    String getName()

    StoragePath getParentPath()

    FileType getType()

------------------

.. code-block:: java

    String toString()

returns path as a String.

------------------
Additional classes
------------------

********
FileType
********
**com.imcode.imcms.api.SourceFile**

Possible values: ``FILE``, ``DIRECTORY``.
