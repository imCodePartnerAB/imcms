package imcode.server.document.index.service

case class IndexRebuildProgress(startTimeMillis: Long, currentTimeMillis: Long, totalDocsCount: Int, indexedDocsCount: Int)
