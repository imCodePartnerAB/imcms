package imcode.server.document.index;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;

/**
 * Default implementation of reindexing document ids getter.
 * <p>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 10.03.17.
 */
public class DefaultReindexingDocumentIdsGetter implements ReindexingDocumentIdsGetter {
    private final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

    @Override
    public int[] getDocumentIds() {
        return documentMapper.getAllDocumentIdsForIndexing();
    }
}
