package imcode.server.document;

import imcode.util.InputStreamSource;
import org.apache.commons.lang.NullArgumentException;

import java.util.HashMap;
import java.util.Map;

public class FileDocumentDomainObject extends DocumentDomainObject {

    private Map fileVariants = new HashMap() ;
    private String defaultFileVariantName = "";

    protected void loadAllLazilyLoadedDocumentTypeSpecificAttributes() {
        // nothing lazily loaded
    }

    public int getDocumentTypeId() {
        return DOCTYPE_FILE;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitFileDocument( this );
    }

    public void addFileVariant( String variantName, FileDocumentFile fileDocumentFile ) {
        if (null == variantName) {
            throw new NullArgumentException( "variantName" ) ;
        }
        if (!fileVariants.containsKey( defaultFileVariantName )) {
            defaultFileVariantName = variantName ;
        }
        fileVariants.put( variantName, fileDocumentFile ) ;
    }

    public Map getFileVariants() {
        return fileVariants;
    }

    public FileDocumentFile getFileVariant( String fileVariantName ) {
        return (FileDocumentFile)fileVariants.get( fileVariantName ) ;
    }

    public FileDocumentFile removeFileVariant( String fileVariantName ) {
        if (1 == fileVariants.size()) {
            throw new IllegalStateException( "Can't remove last fileVariant.");
        }
        return (FileDocumentFile)fileVariants.remove( fileVariantName ) ;
    }

    public void setDefaultFileVariantName( String defaultFileVariantName ) {
        if (!fileVariants.containsKey( defaultFileVariantName )) {
            throw new IllegalArgumentException( "Cannot set defaultFileVariantName to non-existant key "+defaultFileVariantName) ;
        }
        this.defaultFileVariantName = defaultFileVariantName;
    }

    public String getDefaultFileVariantName() {
        return defaultFileVariantName;
    }

    public FileDocumentFile getFileVariantOrDefault( String fileVariantName ) {
        FileDocumentFile fileVariant = getFileVariant( fileVariantName ) ;
        if (null == fileVariant) {
            fileVariant = getDefaultFileVariant() ;
        }
        return fileVariant ;
    }

    public FileDocumentFile getDefaultFileVariant() {
        return (FileDocumentFile)fileVariants.get(defaultFileVariantName) ;

    }

    public static class FileDocumentFile {

        private String filename ;
        private String mimeType ;
        private InputStreamSource inputStreamSource;
        private boolean createdAsImage;

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

        public void setCreatedAsImage( boolean createdAsImage ) {
            this.createdAsImage = createdAsImage;
        }

        public boolean isCreatedAsImage() {
            return createdAsImage;
        }
    }
}