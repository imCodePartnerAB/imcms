package imcode.server.document.index.service;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static imcode.server.document.index.service.IndexUpdateOperation.UPDATE_VERSION;

@EqualsAndHashCode
@ToString
public final class UpdateDocumentVersionInIndex implements IndexUpdateOp {
	private final String docId;
	private final IndexUpdateOperation operation;

	public UpdateDocumentVersionInIndex(String docId) {
		this.docId = docId;
		this.operation = UPDATE_VERSION;
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
