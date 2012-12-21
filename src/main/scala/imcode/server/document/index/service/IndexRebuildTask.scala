package imcode.server.document.index.service

import java.util.concurrent.Future

trait IndexRebuildTask {
  def future(): Future[_]
  def progress(): Option[IndexRebuildProgress]
}
