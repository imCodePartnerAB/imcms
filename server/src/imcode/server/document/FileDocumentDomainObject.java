package imcode.server.document;

import imcode.util.InputStreamSource;
import imcode.util.Utility;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class FileDocumentDomainObject extends DocumentDomainObject {

    private Map fileVariants = createFileVariantsMap();

    private String defaultFileVariantName ;

    protected void loadAllLazilyLoadedDocumentTypeSpecificAttributes() {
        // nothing lazily loaded
    }

    public int getDocumentTypeId() {
        return DOCTYPE_FILE;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitFileDocument( this );
    }

    public void addFileVariant( String fileVariantName, FileVariant fileVariant ) {
        if ( null == fileVariantName ) {
            throw new NullArgumentException( "fileVariantName" );
        }
        if ( !fileVariants.containsKey( defaultFileVariantName ) ) {
            defaultFileVariantName = fileVariantName;
        }
        fileVariants.put( fileVariantName, fileVariant );
    }

    public Map getFileVariants() {
        Map map = createFileVariantsMap();
        map.putAll( fileVariants ) ;
        return map;
    }

    private Map createFileVariantsMap() {
        return MapUtils.orderedMap( new HashMap() );
    }

    public FileVariant getFileVariant( String fileVariantName ) {
        return (FileVariant)fileVariants.get( fileVariantName );
    }

    public FileVariant removeFileVariant( String fileVariantName ) {
        FileVariant fileVariant = (FileVariant)fileVariants.remove( fileVariantName );
        selectDefaultFileVariantName( fileVariantName );
        return fileVariant;
    }

    private void selectDefaultFileVariantName( String fileVariantName ) {
        if (fileVariants.isEmpty()) {
            defaultFileVariantName = null ;
        } else if ( defaultFileVariantName.equals( fileVariantName ) ) {
            defaultFileVariantName = (String)Utility.firstElementOfSetByOrderOf( fileVariants.keySet(), String.CASE_INSENSITIVE_ORDER ) ;
        }
    }

    public void setDefaultFileVariantName( String defaultFileVariantName ) {
        if ( !fileVariants.containsKey( defaultFileVariantName ) ) {
            throw new IllegalArgumentException( "Cannot set defaultFileVariantName to non-existant key "
                                                + defaultFileVariantName );
        }
        this.defaultFileVariantName = defaultFileVariantName;
    }

    public String getDefaultFileVariantName() {
        return defaultFileVariantName;
    }

    public FileVariant getFileVariantOrDefault( String fileVariantName ) {
        FileVariant fileVariant = getFileVariant( fileVariantName );
        if ( null == fileVariant ) {
            fileVariant = getDefaultFileVariant();
        }
        return fileVariant;
    }

    public FileVariant getDefaultFileVariant() {
        return (FileVariant)fileVariants.get( defaultFileVariantName );

    }

    public void renameFileVariant( String oldFileVariantName, String newFileVariantName ) {
        if ( null == oldFileVariantName ) {
            throw new NullArgumentException( "oldFileVariantName" );
        }
        if ( null == newFileVariantName ) {
            throw new NullArgumentException( "newFileVariantName" );
        }
        if (!fileVariants.containsKey( oldFileVariantName )) {
            throw new IllegalStateException( "There is no fileVariant with the name " + oldFileVariantName );
        }
        if (oldFileVariantName.equals( newFileVariantName )) {
            return ;
        }
        if (fileVariants.containsKey( newFileVariantName )) {
            throw new IllegalStateException( "There already is a fileVariant with the name "+newFileVariantName ) ;
        }
        fileVariants.put(newFileVariantName, fileVariants.remove( oldFileVariantName )) ;
        if (defaultFileVariantName.equals( oldFileVariantName )) {
            defaultFileVariantName = newFileVariantName ;
        }
    }

    public static class FileVariant {

        private String filename;
        private String mimeType;
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