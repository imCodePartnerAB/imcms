package imcode.server.document;

import imcode.util.IdLocalizedNamePair;
import com.imcode.imcms.util.l10n.LocalizedMessage;

import java.util.Set;
import java.util.HashSet;

public class DocumentTypeDomainObject extends IdLocalizedNamePair {

    public static final int TEXT_ID = 2;
    public static final int URL_ID = 5;
    public static final int BROWSER_ID = 6;
    public static final int HTML_ID = 7;
    public static final int FILE_ID = 8;

    private static final String DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX = "document_type/name/";

    public final static DocumentTypeDomainObject TEXT = new DocumentTypeDomainObject( TEXT_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX + "text" ) );
    public final static DocumentTypeDomainObject URL = new DocumentTypeDomainObject( URL_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX + "url" ) );
    public final static DocumentTypeDomainObject BROWSER = new DocumentTypeDomainObject( BROWSER_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX + "browser" ) );
    public final static DocumentTypeDomainObject HTML = new DocumentTypeDomainObject( HTML_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX + "html" ) );
    public final static DocumentTypeDomainObject FILE = new DocumentTypeDomainObject( FILE_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX + "file" ) );

    final static DocumentTypeDomainObject[] ALL_DOCUMENT_TYPES = {
       BROWSER,
       FILE,
       HTML,
       TEXT,
       URL,
    } ;

    public DocumentTypeDomainObject( int id, LocalizedMessage name ) {
        super(id, name);
    }

    public static int[] getAllDocumentTypeIds() {
        int[] allDocumentTypeIds = new int[ALL_DOCUMENT_TYPES.length] ;
        for ( int i = 0; i < ALL_DOCUMENT_TYPES.length; i++ ) {
            DocumentTypeDomainObject documentType = ALL_DOCUMENT_TYPES[i];
            allDocumentTypeIds[i] = documentType.getId() ;
        }
        return allDocumentTypeIds ;
    }

    public static Set getAllDocumentTypeIdsSet() {
        Set set = new HashSet() ;
        int[] allDocumentTypeIds = getAllDocumentTypeIds();
        for ( int i = 0; i < allDocumentTypeIds.length; i++ ) {
            int documentTypeId = allDocumentTypeIds[i];
            set.add(new Integer(documentTypeId)) ;
        }
        return set ;
    }
}
