Storage
=======

In this article:
    - `Introduction`_
    - `Storage Types`_
    - `Storage Configurations`_
    - `API`_

------------
Introduction
------------

ImCms is adapted for several file storage options and provides a convenient API for this feature.
So, user files (images and file documents) can be stored in one of the available ways.

-------------
Storage Types
-------------

* **Server** (default) - files are stored on the server where the system is running.
* **Cloud** - files are stored on AWS S3. *The files are kept private, so it cannot be accessed by anyone*.
* **Synchronized** - files are stored on the server and AWS S3. If the file does not exist on the server, then this file is returned from s3 and then stored on the server.

----------------------
Storage Configurations
----------------------

The storage type is selected and configured in the project properties.

You can choose which user files will be stored in what way in properties below.

.. code-block:: properties

    # Save image/file document to
    # disk (default value) - server storage
    # cloud - s3 storage
    # sync  - save to disk and s3. Get files from disk. If there is no required file on disk, pull from s3 and save to disk.
    image.storage.location =
    file.storage.location =

You must configure AWS S3 if you use *Cloud* or *Synchronized* storage type.
Even if you store user files in the default way, you can fill in these properties to use *Cloud* and *Synchronized* storage
in your custom logic.

.. code-block:: properties

    # Settings for cloud storage
    s3.access.key =
    s3.secret.key =
    s3.server.url =
    s3.bucket.name =

.. warning:: Before you start using *Cloud* and *Synchronized* storage for user files, you should manually transfer it to AWS S3.

You should use this `script <https://github.com/imCodePartnerAB/imcms/blob/6.0.0-dev/src/main/resources/scripts/s3RecursiveTransferFiles.sh>`_
(src/main/resources/scripts/s3RecursiveTransferFiles.sh) to transfer files from the server to AWS S3.
This script will correctly transfer and create the structure inside S3.

Usage example

.. code-block:: console

    bash s3RecursiveTransferFiles.sh --bucket <bucket-name> --dir <path-to-main-folder-with-images> --access <access-key> --secret <secret-key> --url <s3-server-url>

---
API
---

.. seealso:: Read Storage API :doc:`here </developer-documentation/api/storage/index>`

How to get the client that is used to save **images**

.. code-block:: java

    StorageСlient storageСlient = Imcms.getServices().getManagedBean("imageStorageClient", StorageClient.class);

How to get the client that is used to save **file documents**

.. code-block:: java

    StorageСlient storageСlient = Imcms.getServices().getManagedBean("fileDocumentStorageClient", StorageClient.class);
