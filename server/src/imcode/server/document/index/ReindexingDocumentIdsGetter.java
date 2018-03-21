package imcode.server.document.index;

/**
 * Interface for client app realisation of which documents should be reindex
 * <p>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 09.03.17.
 */
public interface ReindexingDocumentIdsGetter {
    int[] getDocumentIds();
}
