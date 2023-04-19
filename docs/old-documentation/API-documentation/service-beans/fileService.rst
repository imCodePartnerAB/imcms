FileService
===========

In this article:
    - `Introduction`_
    - `Use API`_
    - `Fields in SourceFile`_

Introduction
------------
This file service works in scope a certain path like property ``FileAdminRootPaths`` which was set in `properties.file`.
So, we can control different files in this scope.
In order to init FileService need to use -  ``Imcms.getServices().getManagedBean(FileService.class)``


Use API
-------

.. code-block:: jsp

    List<SourceFile> getRootFiles();

    List<SourceFile> getFiles(Path file) throws IOException;

    SourceFile getFile(Path file) throws IOException;

    List<DocumentDTO> getDocumentsByTemplatePath(Path template) throws IOException;

    void deleteFile(Path file) throws IOException;

    List<SourceFile> moveFile(List<Path> src, Path target) throws IOException;

    SourceFile moveFile(Path src, Path target) throws IOException;

    List<SourceFile> copyFile(List<Path> src, Path target) throws IOException;

    SourceFile saveFile(Path location, byte[] contents, OpenOption writeMode) throws IOException;

    Template saveTemplateInGroup(Path template, String templateGroupName) throws IOException;

    SourceFile createFile(SourceFile file, boolean isDirectory) throws IOException;

    void replaceDocsOnNewTemplate(Path oldTemplate, Path newTemplate);


About parameter Path need to read documentation about nio2 `here <https://www.baeldung.com/java-nio-2-path>`_.

Fields in SourceFile
--------------------

#.     String fileName;
#.     String physicalPath;
#.     String fullPath;
#.     FileType fileType; -  DIRECTORY, FILE
#.     byte[] contents;
#.     String size;

