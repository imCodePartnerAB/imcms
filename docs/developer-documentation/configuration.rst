System Configuration
====================

In this article:
    - `Introduction`_
    - `Database`_
    - `Authentication`_
    - `Document`_
    - `Language`_
    - `Image`_
    - `Security`_
    - `File Management`_
    - `Storage`_
    - `Solr/index`_
    - `Cache`_
    - `Template CSS Feature`_
    - `SMS`_
    - `Mail`_
    - `Other`_

------------
Introduction
------------

You can configure the system according to your requirements.
To do this, override the necessary properties described below.
The specified values show the default values.

.. warning:: ``user`` and ``password`` are two required properties that must be overridden!

How can you get project properties

.. code-block:: properties

    Imcms.getServerProperties();

--------
Database
--------

.. code-block:: properties

    # Database driver and url
    JdbcDriver=com.mysql.cj.jdbc.Driver
    JdbcUrl=jdbc:mysql://localhost:3306/imcms?characterEncoding=utf8&useTimezone=true&serverTimezone=UTC
    # Database login
    User=
    Password=

    # Max number of connections to the database
    MaxConnectionCount=20

*********
Hibernate
*********

.. code-block:: properties

    # It automatically validates or exports schema DDL.
    hbm2ddl.auto=validate
    # It is used to display the executed SQL statements to console.
    show_sql=false
    # It is used to print the SQL in the log and console.
    format_sql=true
    # If enabled, the Hibernate generate comments inside the SQL. It is used to make debugging easier.
    use_sql_comments=true
    # It represents the type of database used in hibernate to generate SQL statements for a particular relational database.
    dialect=org.hibernate.dialect.MySQL5InnoDBDialect
    # It represents the classname of a TransactionFactory which is used with Hibernate Transaction API.
    hibernate_cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory
    # It is used to disable the second-level cache, which is enabled by default for classes which specify a mapping.
    hibernate_use_second_level_cache=true
    # It is used to enable the query cache.
    hibernate.cache.use_query_cache=true

--------------
Authentication
--------------

Coming soon...

--------
Document
--------

.. code-block:: properties

    # Enable/disable document versioning feature.
    # Possible values: true, false.
    document.versioning=true

    # Path to the files of the file document. Path relative to the webapp root on the server storage.
    FilePath=WEB-INF/uploads/

--------
Language
--------

.. code-block:: properties

    # The language used when no language preference is known.
    DefaultLanguage=swe
    # Available languages. Need to use 2 letters language codes (en;sv) with ';' delimiter
    AvailableLanguages=en;sv;de

-----
Image
-----

.. code-block:: properties

    # Path to the images. Path relative to the webapp root on the server storage.
    ImagePath = images

    # Maximum size of an uploaded image in bytes. By default 250 MB.
    ImageArchiveMaxImageUploadSize = 262144000

    # ImageMagick is a software suite for creating, editing and composing images. It can be downloaded from http://www.imagemagick.org.
    # This path should lead to where ImageMagick is installed, and is required only on windows.
    # For example: C:\\Program Files\\ImageMagick-6.9.6-Q16
    ImageMagickPath =

***********
Compression
***********

.. code-block:: properties

    # Select a compression service.
    # Available options: imageOptim, resmush, default(imageMagick)
    image.compression.service =


    # ImageOptim configurations.

    image.compression.imageoptim.url = https://im2.io
    image.compression.imageoptim.username =
    # Desired image quality.
    # Available options: lossless, high, medium(balanced quality/filesize tradeoff), low.
    image.compression.imageoptim.quality =


    # reSmush.it configurations.

    image.compression.resmush.url = http://api.resmush.it/ws.php
    # Desired image quality.
    # Value between 0 and 100. Recommended: 80
    image.compression.resmush.quality =

--------
Security
--------

***********
CSRF Filter
***********

.. code-block:: properties

    # Enable/disable CSRF protection.
    # Possible values: true, false.
    csrf-include = true

**********
XSS Filter
**********

.. code-block:: properties

    # Enable/disable XSS protection.
    # Possible values: true, false.
    xss-include = true
    # Exclusion URLs. Separated by ','.
    # Add /** for zero or more 'subdirectories' in URL.
    # See for example: /example/example2,/example3/**.
    xss-exclusions =

---------------
File Management
---------------

.. code-block:: properties

    # Main folder for file management.
    # Path relative to the webapp root.
    # '.' set webapp as main folder.
    rootPath = .
    # Folders (inside rootPath) in which superadmin can manage files.
    # Path relative to the webapp root.
    # Separated by ';' or ':'.
    FileAdminRootPaths = css/;images/;javascript/;jsp/;WEB-INF/logs;WEB-INF/templates/

    # User ids that have access to FileAdmin.
    # Separated by ';'.
    admin.files.allowed-users =

-----
Storage
-----

.. code-block:: properties

    # AWS S3 configurations.
    s3.access.key =
    s3.secret.key =
    s3.server.url =
    s3.bucket.name =

*************
Image Storage
*************

.. code-block:: properties

    # Save image to
    # disk (default value) - disk storage
    # cloud - s3 storage
    # sync  - save to disk and s3. Get files from disk. If there is no required file on disk, pull from s3 and save to disk.
    image.storage.location = disk

*********************
File Document Storage
*********************

.. code-block:: properties

    # Save file document to
    # disk (default value) - disk storage
    # cloud - s3 storage
    # sync  - save to disk and s3. Get files from disk. If there is no required file on disk, pull from s3 and save to disk.
    file.storage.location = disk

----------
Solr/index
----------

.. code-block:: properties

    # Remote SOLr server URL.
    # Type: Http(s) URL.
    SolrUrl =


    # The number of minutes between scheduled indexings, default 0 means no scheduled indexings.
    # Schedules periodic index rebuild with fixed interval.
    # Cancels any previously scheduled rebuild.
    IndexingSchedulePeriodInMinutes = 0


    # List of mime types that will not be indexed.
    # Separated by ','.
    IndexDisabledFileMimes =

    # List of filename extensions that will not be indexed
    # Separated by ','.
    IndexDisabledFileExtensions =

-----
Cache
-----

.. code-block:: properties

    # Disable public document cache.
    # Possible values: true, false.
    disabledCache=false

--------------------
Template CSS Feature
--------------------

Coming soon...

---
SMS
---

Coming soon...

----
Mail
----

.. code-block:: properties

    # The hostname of the mail server with which to connect.
    SmtpServer=
    # The port number of the mail server to connect to.
    SmtpPort=25

-----
Other
-----

.. code-block:: properties

    #The URL of the server to which exception logs will be sent.
    # Type: Http(s) URL.
    ErrorLoggerUrl = https://errors.imcode.com/ErrorLogger

.. code-block:: properties

    # Workaround for servlet containers (e.g. Tomcat)
    # which don't provide a way to properly decode URI's
    # (path-info and query-string) as UTF-8.
    # The value is the faulty encoding used by the container.
    # Set to empty for the default system encoding.
    WorkaroundUriEncoding=iso-8859-1
