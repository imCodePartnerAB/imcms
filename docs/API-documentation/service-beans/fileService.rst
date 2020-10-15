FileService
===========


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