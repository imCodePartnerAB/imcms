package imcode.server.document;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

public class DocumentCreatingVisitor extends DocumentStoringVisitor {

    public DocumentCreatingVisitor( UserDomainObject user ) {
        super(user);
    }

    public void visitFileDocument( FileDocumentDomainObject document ) {
        String[] fileDocumentColumns = {"meta_id", "filename", "mime", "created_as_image"};

        String sqlFileDocsInsertStr = makeSqlInsertString( "fileupload_docs", fileDocumentColumns );

        service.sqlUpdateQuery( sqlFileDocsInsertStr, new String[]{
            "" + document.getId(), document.getFilename(), document.getMimeType(), document.isCreatedAsImage() ? "1" : "0"
        } );
        saveFile( document );
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject document ) {
        String[] htmlDocumentColumns = {"meta_id", "frame_set"};

        String sqlUrlDocsInsertStr = makeSqlInsertString( "frameset_docs", htmlDocumentColumns );

        service.sqlUpdateQuery( sqlUrlDocsInsertStr, new String[]{
            "" + document.getId(), document.getHtml()
        } );

    }

    public void visitUrlDocument( UrlDocumentDomainObject document ) {
        String[] urlDocumentColumns = {"meta_id", "frame_name", "target", "url_ref", "url_txt", "lang_prefix"};

        String sqlUrlDocsInsertStr = DocumentStoringVisitor.makeSqlInsertString( "url_docs", urlDocumentColumns );

        service.sqlUpdateQuery( sqlUrlDocsInsertStr, new String[]{
            "" + document.getId(), "", "", document.getUrl(), "", ""
        } );

    }

    public void visitTextDocument( TextDocumentDomainObject textDocument ) {
        String sqlTextDocsInsertStr = "INSERT INTO text_docs (meta_id, template_id, group_id, default_template_1, default_template_2) VALUES (?,?,?,?,?)";
        TemplateDomainObject textDocumentTemplate = textDocument.getTemplate();
        service.sqlUpdateQuery( sqlTextDocsInsertStr,
                                new String[]{
                                    "" + textDocument.getId(), "" + textDocumentTemplate.getId(),
                                    "" + textDocument.getTemplateGroupId(),
                                    "" + textDocument.getDefaultTemplateIdForRestrictedPermissionSetOne(),
                                    "" + textDocument.getDefaultTemplateIdForRestrictedPermissionSetTwo()
                                } );

        updateTextDocumentTexts( textDocument );
        updateTextDocumentImages( textDocument, user );
        updateTextDocumentIncludes( textDocument );

    }
}
