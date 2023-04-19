Configure IA server
===================

In this article:
    - `Introduction`_
    - `Database Configuration`_
    - `Hibernate Configuration`_
    - `Image Archive Own Configuration`_

Introduction
------------

Image Archive is divided into two parts: server and client. Both of them needs to be configured. Lets see how to
configure the server side.

To set up Image Archive server we have application container:

`https://svn.imcode.com/imcode/customers/imagearchive/trunk`

Set it up with next properties:

Database Configuration
----------------------

*
    For SQL Server:
    .. code-block:: properties

        jdbc-driver = net.sourceforge.jtds.jdbc.Driver
        jdbc-url = jdbc:jtds:sqlserver://localhost:1433/;AppName=imCMS;DatabaseName=imcms
        hibernate-dialect = com.imcode.imcms.addon.imagearchive.util.SQLServerDialect

*
    For MySQL:
    .. code-block:: properties

        jdbc-driver = com.mysql.jdbc.Driver
        jdbc-url = jdbc:mysql://localhost:3306/iarch_new?characterEncoding=utf8
        hibernate-dialect = org.hibernate.dialect.MySQL5InnoDBDialect

*
    For both:
    .. code-block:: properties

        jdbc-username =
        jdbc-password =


Hibernate Configuration
-----------------------

Automatically validates or exports schema DDL to the database when the ``SessionFactory`` is created.

.. code-block:: properties

    hibernate-hbm2ddl-auto =


Possible values:

   - **validate**: validate that the schema matches, make no changes to the schema of the database, you probably want this for production

   - **update**: update the schema to reflect the entities being persisted

   - **create**: creates the schema necessary for your entities, destroying any previous data

   - **create-drop**: create the schema as in create above, but also drop the schema at the end of the session. This is great in early development or for testing.

Image Archive Own Configuration
-------------------------------

URL to imCMS application that makes use of this Image Archive, as seen by the clients browser, in form:

   **<host> [":" <port>] "/" <context-path>**

For example: ``test.com/imcms`` or ``http://localhost:8080/skurup``

.. code-block:: properties

        imcms-root-url =


Path where all the images that are uploaded to Image Archive will be stored, can be relative or absolute.

For example: ``/var/image_archive``

.. warning:: Be sure that user have rights to change folder content.
.. code-block:: properties

        storage-path =


Path where temporary images that are being processed are stored.

For example: ``/tmp`` or ``C:/tmp``

.. warning:: Be sure that user have rights to change folder content.
.. code-block:: properties

        temp-path =


ImageMagick is a software suite for creating, editing and composing images. It can be downloaded from http://www.imagemagick.org. This path should lead to where ImageMagick is installed, and is required only on windows. For linux leave it empty.

For example: ``C:/program files/imagemagick-6.4.9-q16``

.. code-block:: properties

        image-magick-path =


Maximum size of an uploaded image in bytes. By default 250 MB.

.. code-block:: properties

        max-image-upload-size = 262144000


Maximum size of an uploaded ZIP archive in bytes. By default 250 MB.

.. code-block:: properties

        max-zip-upload-size = 262144000


URL path to login, in imCMS, relative to context path.

.. code-block:: properties

        imcms-login-url-path = login


Name for a directory within libraries folder, that will contain each users personal library.
This directory will be automatically created.

.. code-block:: properties

        imcms-users-library-folder = users


Images from Image Archive that are being used by imCMS will be stored here, can be relative or absolute.

.. code-block:: properties

        imcms-images-path =


Next two properties may be empty:

Path to libraries, can be relative or absolute. Each folder in this directory will become a library in Image Archive - these folders can be created using imCMS file manager. Each library can contain one or more raw images which can be activated in Image Archive.

.. code-block:: properties

        imcms-libraries-path =


Absolute or relative paths separated by ";". Each path will become a library in Image archive, can be used for gaining access to old Image Archive.

.. code-block:: properties

        imcms-old-library-paths =

