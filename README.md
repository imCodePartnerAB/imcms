imcms
=====

imCMS, or as we prefer to call it I’m CMS, is a project that has been developed during more than 10 years. It is a java-based CMS system, with tag-based templates, and using MySQL DB (even MS SQL works). The system is used in more than hundred installations in Sweden, most frequently within the public sector.

During the last year imCMS has been updated to a very new codebase, integrating among others Spring and Hibernate. This product is almost ready to go out for Alpha-testing now.

During the years imCMS has been developed within the Swedish company: imCode Partner AB. Our intention is to try to create a community around the code. This is one of few qualified Java-based CMS systems, so we hope that we would find a number of contributors.

Johan Larsson "johlra" is our responsible project manager.

imCMS is licensed under the AGPL license.

Read the docs: http://docs.imcms.net/en/latest/

All versions is available to download from
http://repo.imcode.com/maven2/com/imcode/imcms/imcms/

The install instruction could need an update, they are old but should work in most cases
Newer version of imCMS work on Java 8, Tomcat 8.5 and MySQL 5.7

Installing imCMS:


Prerequisites:

    * Java 8+.
    
    * NodeJS 8+

    * Apache Tomcat 8.5+ or later, or another servlet-api 2.3 (or later) compatible servlet engine.

    * Install MySQL 5.7 or later.


Installing imCMS:

    * Rename the file "imcms-<version>.war" to "imcms.war",
      and install it in the "tomcat/webapps" directory.

    * Start Tomcat.
      Tomcat will find the war-file you installed, 
      and unpack it into a directory named after the war-file, "tomcat/webapps/imcms".


Setting up the database:

    * Create a new database on your database server, as appropriate for your type of database server
      and your preferred default language.

    Example:

        To create a MySQL database, a command like the following might be appropriate:

            mysql -u <user> -p -e "CREATE DATABASE imcms CHARACTER SET utf8 COLLATE utf8_swedish_ci"
           
        On Microsoft SQL Server you can use "Enterprise Manager" to create a database,
        or use "SQL Query Analyzer" to execute something like the following SQL command:
         
            CREATE DATABASE imcms COLLATE Finnish_Swedish_CI_AS

        See the documentation for your database server for details.
  

Setting up imCMS:

    * Edit the file "server.properties" in the directory "tomcat/webapps/imcms/WEB-INF/conf",
      and set the database parameters ("JdbcDriver", "JdbcUrl", "User", "Password", "MaxConnectionCount")
      according to your setup.
      Set "SmtpServer" to the hostname of your SMTP (outgoing mail) server.

    * If you want to add a file-extension to be recognized as a mime-type by imCMS,
      add a "<mime-mapping>" to "tomcat/webapps/imcms/WEB-INF/web.xml".
      See http://www.iana.org/assignments/media-types/ for the mime-type registry.

    * Restart Tomcat to reload the settings.
    

Running:

    * Try logging in at " http://localhost:8080/imcms/login/ " as user "admin" with password "admin".
      (Replace "localhost:8080" with whatever hostname and port your servlet engine is running on.)

    * The database configured in "server.properties" will be automatically set up,
      which might take from a few seconds to a minute or so.
      This only happens the first time you run imCMS. 
      
    * When the database setup is finished you should see the welcome page.


You're done!
