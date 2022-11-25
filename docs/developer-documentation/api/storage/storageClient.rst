StorageClient
=============

Represents a client for managing storage.

-------
Methods
-------

.. code-block:: java

    StorageFile getFile(StoragePath path) throws StorageFileNotFoundException

returns a file from storage.

``StorageFileNotFoundException`` - error if the file is missing.

------------------

.. code-block:: java

    List<StoragePath> listPaths(StoragePath path)

returns file/directory paths inside the specified folder.

------------------

.. code-block:: java

    List<StoragePath> walk(StoragePath path)

returns the paths to all nested files/directories within the specified folder.

------------------

.. code-block:: java

    boolean exists(StoragePath path)

check for the existence of a file/directory.

------------------

.. code-block:: java

    void create(StoragePath path)

create an empty file/directory.

------------------

.. code-block:: java

    void put(StoragePath path, InputStream inputStream)

put a file with content.

------------------

.. code-block:: java

    void move(StoragePath fromPath, StoragePath toPath)

move or rename a file/directory.

``fromPath`` - the path to the file/directory to move.

``toPath`` - the path to the target file.

------------------

.. code-block:: java

    void copy(StoragePath sourcePath, StoragePath toPath)

copy a file/directory.

``sourcePath`` - the path to the file to copy.

``toPath`` - the path to the target file.

------------------

.. code-block:: java

    default boolean canPut(StoragePath path)

tests whether a file is writable.

------------------

.. code-block:: java

    void delete(StoragePath path, boolean force)

delete a file/directory. To remove a directory with files, set ``force`` as true, otherwise throw an error.

-----------
Realization
-----------

**com.imcode.imcms.storage.impl.disk.DiskStorageClient** - *Server* storage type.

**com.imcode.imcms.storage.impl.cloud.CloudStorageClient** - *Cloud* storage type.

**com.imcode.imcms.storage.impl.cloud.CloudStorageClient** - *Syncronized* storage type.
