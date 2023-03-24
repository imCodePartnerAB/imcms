package imcode.server.document.index.service;

import lombok.EqualsAndHashCode;

import static imcode.server.document.index.service.IndexUpdateOperation.UPDATE_VERSION;

@EqualsAndHashCode
public final class UpdateDocumentVersionInIndex implements IndexUpdateOp {
	private final int docId;
	private final IndexUpdateOperation operation;

	public UpdateDocumentVersionInIndex(int docId) {
		this.docId = docId;
		this.operation = UPDATE_VERSION;
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
