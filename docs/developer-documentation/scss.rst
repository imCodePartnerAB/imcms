SCSS
====

In this article:
    - `Introduction`_
    - `Client configuration`_

------------
Introduction
------------

ImCMS uses sass compiler to build and optimize css files with usage of variables.

---------------------------------------
Client(side project) SCSS configuration
---------------------------------------

If you want ImCMS to compile your \*.scss files, do the following:

-
    Create new file named *client.config.js* with following content in this directory: *src/main/webapp*

    .. code-block:: javascript

        /**
         * Client configuration used by SCSS compiler. You have to declare relative
         * compiled files destination path. And also you have to declare input entries,
         * where key is output file name and value is relative path to the source file.
         *
         * @example
         *  module.exports = {
         *      entry: {
         *          main: './css/test.scss'
         *      },
         *      rebuildImcmsCSS: false,
         *      destination: './dist'
         *  };
         *  // Result files structure would be: './dist/main.css'
         *
         * @example in case you need to rebuild imcms css files(e.g. contextPath)
         *  module.exports = {
         *      entry: {
         *          main: './css/test.scss'
         *      },
         *      rebuildImcmsCSS: true,
         *      destination: './dist'
         *  };
         *  // Result files structure would be: './dist/main.css'
         */
            module.exports = {
        	    entry: {
        		    main: './scss/test.scss',
        	    },
        	    rebuildImcmsCSS: true,
        	    destination: './dist'
            };

-
    Create some .scss file for test purposes, e.g. test.scss under css directory

-   Configuration have two parts (notice the comment inside *client.config.js* file):

        - ``destination`` is the relative path to where all compiled css should be
        - ``entry`` is key-value pairs, where key is the name of future file, and the value is the path to file that had to be compiled. All it's dependencies (@import's) will be bundled together

-
    Configure it with paths and files you want to test

-
    Add this code to maven:

    .. code-block:: xml

        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>1.6.0</version>
            <executions>
                <execution>
                    <id>install webpack</id>
                    <phase>package</phase>
                    <goals>
                        <goal>exec</goal>
                    </goals>
                    <configuration>
                        <executable>npm</executable>
                        <workingDirectory>${project.build.directory}/${project.build.finalName}</workingDirectory>
                        <arguments>
                            <argument>install</argument>
                        </arguments>
                    </configuration>
                </execution>
                <execution>
                    <id>build scss</id>
                    <phase>package</phase>
                    <goals>
                        <goal>exec</goal>
                    </goals>
                    <configuration>
                        <executable>npm</executable>
                        <workingDirectory>${project.build.directory}/${project.build.finalName}</workingDirectory>
                        <arguments>
                            <argument>run</argument>
                            <argument>build:scss</argument>
                        </arguments>
                    </configuration>
                </execution>
            </executions>
        </plugin>

That's all you need, on next ``package`` phase in maven scss will be compiled into css, check in maven destination directory.
If something vent wrong, check `here <https://svn.imcode.com/imcode/customers/imcms/trunk>`_, I've managed it to work.

**Check /imcms/css/_variables.scss file! If you need to change/add new variables in this file in side project - create same file in your project.**

**Also latest nodeJS+NPM is required to be installed on a machine where you want to use it!**
