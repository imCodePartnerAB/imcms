package imcode.server.document.index.service;

import static imcode.server.document.index.service.IndexUpdateOperation.ADD;

/**
 * Used to indicate index add operation
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.12.17.
 */
public final class AddDocToIndex implements IndexUpdateOp {

    private final int docId;

    public AddDocToIndex(int docId) {
        this.docId = docId;
    }

    @Override
    public int docId() {
        return docId;
    }

    @Override
    public IndexUpdateOperation operation() {
        return ADD;
    }

}
