package imcode.server.document;

public class DocumentDeletingVisitor extends DocumentVisitor {

    public void visitFileDocument(FileDocumentDomainObject fileDocument) {
        DocumentMapper.deleteAllFileDocumentFiles(fileDocument);
    }
}
