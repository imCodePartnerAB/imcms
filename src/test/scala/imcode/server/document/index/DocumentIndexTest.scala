package imcode.server.document.index

import org.junit.Test
import org.junit.Assert._
import com.imcode.imcms.test.{TestSetup => ImcmsTest}
import imcode.server.Imcms
import com.imcode.imcms.test.fixtures.UserFX
import com.imcode.imcms.api.{DefaultContentManagementSystem, LuceneParsedQuery}
import org.apache.lucene.search.{Sort, Query}

class DocumentIndexTest {

  ImcmsTest.solr.recreateHome()
  ImcmsTest.db.prepare(recreateBeforePrepare = true)
  ImcmsTest.imcms.init(start = true, prepareDbOnStart = false)

  val documentIndex = Imcms.getServices.getDocumentMapper.getDocumentIndex  //DocumentIndexFactory.create(Imcms.getServices)
  val cms = DefaultContentManagementSystem.create(Imcms.getServices, UserFX.mkSuperAdmin, Imcms.getApiDataSource)

  @Test
  def searchUsingLegacyDocumentQuery() {
    val query = new LuceneParsedQuery("*:*")
    cms.getDocumentService.search(query)
  }
}
