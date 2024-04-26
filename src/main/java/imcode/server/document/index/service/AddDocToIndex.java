package imcode.server.document.index.service;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static imcode.server.document.index.service.IndexUpdateOperation.ADD;

/**
 * Used to indicate index add operation
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.12.17.
 */
@EqualsAndHashCode
@ToString
public final class AddDocToIndex implements IndexUpdateOp {

    private final String docId;
	private final IndexUpdateOperation operation;

    public AddDocToIndex(String docId) {
        this.docId = docId;
	    this.operation = ADD;
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
