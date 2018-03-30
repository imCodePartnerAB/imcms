package imcode.server.document.index.service;

import static imcode.server.document.index.service.IndexUpdateOperation.DELETE;

/**
 * Used to indicate index deletion operation
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.12.17.
 */
public final class DeleteDocFromIndex implements IndexUpdateOp {

    private final int docId;

    public DeleteDocFromIndex(int docId) {
        this.docId = docId;
    }

    @Override
    public int docId() {
        return docId;
    }

    @Override
    public IndexUpdateOperation operation() {
        return DELETE;
    }
}
