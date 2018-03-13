package imcode.server.document.index;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.DocumentDomainObject;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation to receive docs for reindexing.
 * <p>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 10.03.17.
 */
public class DefaultReindexingDocumentRepository implements DocumentRepository {

    private final DocumentMapper documentMapper;

    public DefaultReindexingDocumentRepository(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    @Override
    public Set<DocumentDomainObject> getDocs() {
        return documentMapper.getDocumentIdsForIndexing()
                .stream()
                .map(docId -> documentMapper.getDocument(docId, true))
                .collect(Collectors.toSet());
    }
}
