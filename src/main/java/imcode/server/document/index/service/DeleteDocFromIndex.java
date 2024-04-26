package imcode.server.document.index.service;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static imcode.server.document.index.service.IndexUpdateOperation.DELETE;

/**
 * Used to indicate index deletion operation
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.12.17.
 */
@EqualsAndHashCode
@ToString
public final class DeleteDocFromIndex implements IndexUpdateOp {

    private final String docId;
	private final IndexUpdateOperation operation;

    public DeleteDocFromIndex(String docId) {
        this.docId = docId;
	    this.operation = DELETE;
    }

    @Override
    public String docId() {
        return docId;
    }

    @Override
    public IndexUpdateOperation operation() {
        return operation;
    }
}
