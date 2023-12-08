SCSS
====

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
         *      destination: 'dist-css'
         *  };
         *
         *  // Result files structure would be: './dist-css/main.css'
         */
        module.exports = {
            entry: {
                main: './css/test.scss'
            },
            destination: 'dist-css'
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
        		<groupId>com.github.eirslett</groupId>
        		<artifactId>frontend-maven-plugin</artifactId>
        		<version>1.15.0</version>
        		<configuration>
        			<workingDirectory>${project.build.directory}/${project.build.finalName}</workingDirectory>
        			<installDirectory>${project.build.directory}/</installDirectory>
                          <nodeVersion>v16.17.0</nodeVersion>
                          <npmVersion>8.15.0</npmVersion>
        		</configuration>
        		<executions>
        			<execution>
        				<id>install-node-and-npm</id>
        				<phase>package</phase>
        				<goals>
        					<goal>install-node-and-npm</goal>
        				</goals>
        			</execution>
        			<execution>
        				<id>install webpack</id>
        				<phase>package</phase>
        				<goals>
        					<goal>npm</goal>
        				</goals>
        				<configuration>
        					<arguments>install webpack</arguments>
        				</configuration>
        			</execution>
        			<execution>
        				<id>build scss</id>
        				<phase>package</phase>
        				<goals>
        					<goal>npm</goal>
        				</goals>
        				<configuration>
        					<arguments>run build:scss</arguments>
        				</configuration>
        			</execution>
        		</executions>
        	</plugin>

That's all you need, on next ``package`` phase in maven scss will be compiled into css, check in maven destination directory.
If something vent wrong, check `here <https://svn.imcode.com/imcode/customers/imcms/trunk>`_, I've managed it to work.

**Also latest nodeJS+NPM is required to be installed on a machine where you want to use it!**
