package imcode.server.document.index

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import org.apache.lucene.search.TermRangeQuery
import java.util.Date
import org.apache.lucene.document.DateTools


@RunWith(classOf[JUnitRunner])
class LuceneQueryConversionTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  "TermRangeQuery" should {
    "produce date range between infinity and upper bound" in {
      val format = org.apache.solr.common.util.DateUtil.getThreadLocalDateFormat
      val dateFrom =  DateTools.dateToString(new Date, DateTools.Resolution.MILLISECOND) // format.format(new Date)
      val dateTo = format.format(new Date)
      println("Date from: %s, date to: %s".format(dateFrom, dateTo))
      val query1 = new TermRangeQuery("field", dateFrom, dateTo, true, true)
      val query2 = new TermRangeQuery("field", null, dateTo, true, true)

      println("query1: " + query1.toString())
      println("query2: " + query2.toString())
    }
  }
}