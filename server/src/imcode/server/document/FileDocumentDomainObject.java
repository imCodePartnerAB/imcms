package imcode.server.document;

import imcode.server.user.UserDomainObject;
import imcode.util.InputStreamSource;

public class FileDocumentDomainObject extends DocumentDomainObject {

    private String filename;
    private String mimeType;
    private InputStreamSource inputStreamSource;

    public String getFilename() {
        return filename;
    }

    public void setFilename( String v ) {
        this.filename = v;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType( String mimeType ) {
        this.mimeType = mimeType;
    }

    public void setInputStreamSource( InputStreamSource inputStreamSource ) {
        this.inputStreamSource = inputStreamSource;
    }

    public InputStreamSource getInputStreamSource() {
        return inputStreamSource;
    }

    protected void loadAllLazilyLoadedDocumentTypeSpecificAttributes() {
        // nothing lazily loaded
    }

    public int getDocumentTypeId() {
        return DOCTYPE_FILE;
    }

    public void saveDocument( DocumentMapper documentMapper, UserDomainObject user ) {
        documentMapper.saveFileDocument( this );
    }

    public void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user ) {
        documentMapper.saveNewFileDocument( this );
    }

    public void initDocument( DocumentMapper documentMapper ) {
        documentMapper.initFileDocument( this );
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitFileDocument(this) ;
    }

}