package imcode.server.document;

import imcode.util.LocalizedMessage;

public class DocumentTypeDomainObject {

    public static final int TEXT_ID = 2;
    public static final int URL_ID = 5;
    public static final int BROWSER_ID = 6;
    public static final int HTML_ID = 7;
    public static final int FILE_ID = 8;
    public static final int CONFERENCE_ID = 102;
    public static final int CHAT_ID = 103;
    public static final int BILLBOARD_ID = 104;

    private static final String DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX = "document_type/name/";

    public final static DocumentTypeDomainObject TEXT = new DocumentTypeDomainObject( TEXT_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX
                                                                                                                       + "text" ) );
    public final static DocumentTypeDomainObject URL = new DocumentTypeDomainObject( URL_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX
                                                                                                                      + "url" ) );
    public final static DocumentTypeDomainObject BROWSER = new DocumentTypeDomainObject( BROWSER_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX
                                                                                                                          + "browser" ) );
    public final static DocumentTypeDomainObject HTML = new DocumentTypeDomainObject( HTML_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX
                                                                                                                       + "html" ) );
    public final static DocumentTypeDomainObject FILE = new DocumentTypeDomainObject( FILE_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX
                                                                                                                       + "file" ) );
    public final static DocumentTypeDomainObject CONFERENCE = new DocumentTypeDomainObject( CONFERENCE_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX
                                                                                                                               + "conference" ) );
    public final static DocumentTypeDomainObject CHAT = new DocumentTypeDomainObject( CHAT_ID, new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX
                                                                                                                         + "chat" ) );
    public final static DocumentTypeDomainObject BILLBOARD = new DocumentTypeDomainObject( BILLBOARD_ID,
                                                                                                   new LocalizedMessage( DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX
                                                                                                                         + "billboard" ) );
    final static DocumentTypeDomainObject[] ALL_DOCUMENT_TYPES = {
       BILLBOARD,
       BROWSER,
       CHAT,
       CONFERENCE,
       FILE,
       HTML,
       TEXT,
       URL,
    } ;
    public static final int PSEUDO_DOCTYPE_ID_FORTUNES = 106;

    private final int id ;
    private final LocalizedMessage name ;

    public DocumentTypeDomainObject( int id, LocalizedMessage name ) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public LocalizedMessage getName() {
        return name;
    }

    public String toString() {
        return ""+id ;
    }

    public static int[] getAllDocumentTypeIds() {
        int[] allDocumentTypeIds = new int[ALL_DOCUMENT_TYPES.length] ;
        for ( int i = 0; i < ALL_DOCUMENT_TYPES.length; i++ ) {
            DocumentTypeDomainObject documentType = ALL_DOCUMENT_TYPES[i];
            allDocumentTypeIds[i] = documentType.getId() ;
        }
        return allDocumentTypeIds ;
    }
}
