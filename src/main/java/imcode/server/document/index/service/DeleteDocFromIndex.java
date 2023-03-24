package imcode.server.document.index.service;

import lombok.EqualsAndHashCode;

import static imcode.server.document.index.service.IndexUpdateOperation.DELETE;

/**
 * Used to indicate index deletion operation
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.12.17.
 */
@EqualsAndHashCode
public final class DeleteDocFromIndex implements IndexUpdateOp {

    private final int docId;
	private final IndexUpdateOperation operation;

    public DeleteDocFromIndex(int docId) {
        this.docId = docId;
	    this.operation = DELETE;
    }

    @Override
    public int docId() {
        return docId;
    }

    @Override
    public IndexUpdateOperation operation() {
        return operation;
    }
}
