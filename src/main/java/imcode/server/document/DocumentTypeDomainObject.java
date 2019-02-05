package imcode.server.document;

import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.util.IdLocalizedNamePair;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DocumentTypeDomainObject extends IdLocalizedNamePair {

    public static final int TEXT_ID = DocumentType.TEXT.ordinal();
    public static final int URL_ID = DocumentType.URL.ordinal();
    public static final int HTML_ID = DocumentType.HTML.ordinal();
    public static final int FILE_ID = DocumentType.FILE.ordinal();
    private static final String DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX = "document_type/name/";
    public final static DocumentTypeDomainObject TEXT = new DocumentTypeDomainObject(TEXT_ID, new LocalizedMessage(DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX + "text"));
    public final static DocumentTypeDomainObject URL = new DocumentTypeDomainObject(URL_ID, new LocalizedMessage(DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX + "url"));
    public final static DocumentTypeDomainObject HTML = new DocumentTypeDomainObject(HTML_ID, new LocalizedMessage(DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX + "html"));
    public final static DocumentTypeDomainObject FILE = new DocumentTypeDomainObject(FILE_ID, new LocalizedMessage(DOCUMENT_TYPE_NAME_LOCALIZED_MESSAGE_PREFIX + "file"));
    private final static DocumentTypeDomainObject[] ALL_DOCUMENT_TYPES = {
            FILE,
            HTML,
            TEXT,
            URL,
    };
    public final static Map<Integer, DocumentTypeDomainObject> TYPES = Collections.unmodifiableMap(
            new HashMap<Integer, DocumentTypeDomainObject>() {
                {
                    for (DocumentTypeDomainObject type : ALL_DOCUMENT_TYPES) {
                        put(type.getId(), type);
                    }
                }
            }
    );

    public DocumentTypeDomainObject(int id, LocalizedMessage name) {
        super(id, name);
    }

    public static int[] getAllDocumentTypeIds() {
        int[] allDocumentTypeIds = new int[ALL_DOCUMENT_TYPES.length];
        for (int i = 0; i < ALL_DOCUMENT_TYPES.length; i++) {
            DocumentTypeDomainObject documentType = ALL_DOCUMENT_TYPES[i];
            allDocumentTypeIds[i] = documentType.getId();
        }
        return allDocumentTypeIds;
    }

    public static Set<Integer> getAllDocumentTypeIdsSet() {
        Set<Integer> set = new HashSet<>();
        int[] allDocumentTypeIds = getAllDocumentTypeIds();
        for (int documentTypeId : allDocumentTypeIds) {
            set.add(documentTypeId);
        }
        return set;
    }
}
