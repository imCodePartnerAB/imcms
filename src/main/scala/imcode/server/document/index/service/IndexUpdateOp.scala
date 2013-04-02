package imcode.server.document.index.service

sealed trait IndexUpdateOp
case class AddDocToIndex(docId: Int) extends IndexUpdateOp
case class DeleteDocFromIndex(docId: Int) extends IndexUpdateOp
