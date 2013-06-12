package imcode.server.document.index

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfterAll, WordSpec}
import java.util.Date
import org.apache.lucene.document.DateTools


@RunWith(classOf[JUnitRunner])
class LuceneQueryConversionTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfterEach {

  "TermRangeQuery" should {
    "produce date range between infinity and upper bound" in {
      val format = org.apache.solr.common.util.DateUtil.getThreadLocalDateFormat
      val dateFrom =  DateTools.dateToString(new Date, DateTools.Resolution.MILLISECOND) // format.format(new Date)
      val dateTo = format.format(new Date)
      println("Date from: %s, date to: %s".format(dateFrom, dateTo))

      //todo: fix
      //val query1 = new TermRangeQuery("field", dateFrom, dateTo, true, true)
      //val query2 = new TermRangeQuery("field", null, dateTo, true, true)
    }
  }
}