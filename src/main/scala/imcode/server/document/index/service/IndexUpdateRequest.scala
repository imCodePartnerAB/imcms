package imcode.server.document.index.service

sealed trait IndexUpdateRequest
case class AddDocToIndex(docId: Int) extends IndexUpdateRequest
case class DeleteDocFromIndex(docId: Int) extends IndexUpdateRequest
