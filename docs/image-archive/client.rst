Configure IA client
===================

In this article:
    - `Introduction`_
    - `Database and Hibernate Configuration`_
    - `Image Archive Own Configuration`_

Introduction
------------

Image Archive is divided into two parts: server and client. Both of them needs to be configured.

Client side includes into existing project by maven dependency:

.. code-block:: xml

    <dependency>
        <groupId>com.imcode.imcms.addon.imagearchive</groupId>
        <artifactId>client</artifactId>
        <version>1.0-SNAPSHOT</version>
        <type>war</type>
    </dependency>
    <dependency>
        <groupId>com.imcode.imcms.addon.imagearchive</groupId>
        <artifactId>client</artifactId>
        <version>1.0-SNAPSHOT</version>
        <type>jar</type>
        <classifier>classes</classifier>
    </dependency>

With this dependencies client's .war and required .jar are ready to use after some configuration. Most of next properties already available in project, but for full info lets see all required properties for client.

Database and Hibernate Configuration
------------------------------------

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
*
    Hibernate Configuration

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

URL to the separate image archive application, as seen by the clients browser, in form:

   **<host> [":" <port>] "/" <context-path> "/archive"**

For example: ``localhost:8080/client/archive`` or ``http://www.skurup.se/archive``

.. code-block:: properties

    ImageArchiveUrl =


URL to Image Archive server.

For example: ``http://skurup-imagearchive.dev.imcode.com`` or ``http://localhost:8081``

.. code-block:: properties

    ia-server-url =


IDs of the roles that are allowed to see the "Choose from image archive" button in image edit page, delimited by ",". If not specified, everyone is allowed.

.. code-block:: properties

    ImageArchiveAllowedRoleIds = 2


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


Path to images, in file system and URL.

.. code-block:: properties

    ImageArchiveImagePath = archivedimages/
    ImageArchiveImageUrl = /archivedimages/


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


Next two properties may be empty:

Path to libraries, can be relative or absolute. Each folder in this directory will become a library in Image Archive - these folders can be created using imCMS file manager. Each library can contain one or more raw images which can be activated in Image Archive.

.. code-block:: properties

        imcms-libraries-path =


Absolute or relative paths separated by ";". Each path will become a library in Image archive, can be used for gaining access to old Image Archive.

.. code-block:: properties

        imcms-old-library-paths =

