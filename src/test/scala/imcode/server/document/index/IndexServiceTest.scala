package imcode.server.document.index

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcode.server.Config
import com.imcode._
import com.imcode.imcms.test.Test
import java.lang.IllegalStateException
import java.io.{File}
import org.apache.commons.io.FileUtils
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.client.solrj.{SolrQuery, SolrServer}
import org.apache.solr.client.solrj.response.QueryResponse
import junit.framework.Assert._
import java.util.Date
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfter, BeforeAndAfterAll, WordSpec}

@RunWith(classOf[JUnitRunner])
class IndexServiceTest extends WordSpec with BeforeAndAfterAll {
  //with BeforeAndAfterEach

  override def beforeAll() {
    Test.solr.recreateHome()
  }

  "IndexService constructor" should  {
    "throw an Exception when neither Config.solrUrl nor Config.solrHome is set" in {
      val ex = intercept[IllegalStateException] {
        //new SolrServerFactory(new Config)
      }

      assert("Configuration error. Neither Config.solrUrl nor Config.solrHome is set." === ex.getMessage)
    }

    "create embedded SOLr server when Config.solrHome is set and Config.solrUrl is not" in  {
      val config = new Config() |>> { c =>
        c.setSolrHome(Test.solr.home)
      }

      //val solrServer = new SolrServerFactory(config).solrServer
      //assert(solrServer.isInstanceOf[EmbeddedSolrServer], "EmbeddedSolrServer")
    }

    "create remote SOLr server when Config.solrUrl is set" in  {
      val config = new Config() |>> { c =>
        c.setSolrHome("ingore")
        c.setSolrUrl("http://localhost/solr/imcms")
      }

      //val solrServer = new SolrServerFactory(config).solrServer
      //assert(solrServer.isInstanceOf[HttpSolrServer], "HttpSolrServer")

      fail("test connection using jetty")
    }
  }

  // xmlformat, binary format

  "SolrServer" should {
    val dateTime = new Date
    val dateTimeStr = "%1$TFT%1$TT.%1$TLZ" format dateTime

    "store document using all fields defined in schema" in {
      val solr = Test.solr.createEmbeddedServer()
      val df = new DocFields |>> { df =>
        df.metaId = 1001.toString
        df.docTypeId = 2
        df.createdDateTime = dateTime
      }

      solr.addBean(df)
      solr.commit()
    }

    "find previously stored document by *:*" in {
      val solr = Test.solr.createEmbeddedServer()
      val query = new SolrQuery("*:*")
      val response = solr.query(query)
      val results = response.getResults

      assertEquals("results count", 1, results.getNumFound)

      val doc = results.get(0)

      assertEquals("metaId", "1001", doc.getFieldValue(DocumentIndex.FIELD__META_ID))
    }

    "find previously stored document by meta_id:1001 AND doc_type_id:2" in {
      val solr = Test.solr.createEmbeddedServer()
      val query = new SolrQuery("meta_id:1001 AND doc_type_id:2")
      val response = solr.query(query)
      val results = response.getResults

      assertEquals("results count", 1, results.getNumFound)

      val doc = results.get(0)

      assertEquals("metaId", "1001", doc.getFieldValue(DocumentIndex.FIELD__META_ID))
    }

    "find nothing by created_datetime:dateTime AND doc_type_id:1" in {
      val solr = Test.solr.createEmbeddedServer()
      val query = new SolrQuery("created_datetime:\""+dateTimeStr+"\" AND doc_type_id:1")
      val response = solr.query(query)
      val results = response.getResults

      assertEquals("results count", 3, results.getNumFound)
    }
  }
}


/*
//  @Field(FIELD__PHASE)
//  var xxx: String = _

//  @Field(FIELD__PROPERTY_PREFIX)
//  var xxx: String = _
   <dynamicField name="property.*" type="string" indexed="true" stored="false" multiValued="true" />
   <field name="timestamp" type="date" indexed="true" stored="true" default="NOW"/>
   <dynamicField name="imcms.document*" type="string" indexed="true" stored="false" multiValued="true" />
   <dynamicField name="text*" type="string" indexed="true" stored="false" multiValued="true" />

 */
class DocFields {
  import org.apache.solr.client.solrj.beans.Field
  import DocumentIndex._

  @Field("meta_id")
  var metaId: String = _

  @Field("doc_type_id")
  var docTypeId: JInteger = _ // Int?

  @Field("created_datetime")
  var createdDateTime: Date = _

//  @Field(FIELD__DOC_TYPE_ID)
//  var docTypeId: JInteger = _ // Int?
//
//  @Field(FIELD__IMAGE_LINK_URL)
//  var imageLinkUrl: JList[String] = _
//
//  @Field(FIELD__NONSTRIPPED_TEXT)
//  var nonStrippedText: JList[String] = _
//
//  @Field(FIELD__TEXT)
//  var text: JList[String] = _
//
//  @Field(FIELD__KEYWORD)
//  var keyword: JList[String] = _
//
//  @Field(FIELD__ACTIVATED_DATETIME)
//  var activatedDateTime: Date = _
//
//  @Field(FIELD__ARCHIVED_DATETIME)
//  var archivedDateTime: Date = _
//
//  @Field(FIELD__CATEGORY)
//  var category: String = _
//
//  @Field(FIELD__CATEGORY_ID)
//  var categoryId: JInteger = _
//
//  @Field(FIELD__CATEGORY_TYPE)
//  var categoryType: JInteger = _
//
//  @Field(FIELD__CATEGORY_TYPE_ID)
//  var categoryTypeId: JInteger = _
//
//  @Field(FIELD__CREATED_DATETIME)
//  var createdDateTime: Date = _
//
//  @Field(FIELD__META_HEADLINE)
//  var metaHeadline: String = _
//
//  @Field(FIELD__META_HEADLINE_KEYWORD)
//  var metaHeadlineKeyword: String = _
//
//  @Field(FIELD__META_ID)
//  var metaId: String = _
//
//  @Field(FIELD__META_ID_LEXICOGRAPHIC)
//  var metaIdLexicographic: JInteger = _ // Int?
//
//  @Field(FIELD__META_TEXT)
//  var metaText: String = _
//
//  @Field(FIELD__MODIFIED_DATETIME)
//  var modifiedDatetime: Date = _
//
//  @Field(FIELD__PARENT_ID)
//  var parentId: JList[String] = _
//
//  @Field(FIELD__PARENT_MENU_ID)
//  var parentMenuId: JList[String] = _
//
//  @Field(FIELD__HAS_PARENTS)
//  var hasParent: JBoolean = _
//
//  @Field(FIELD__PUBLICATION_END_DATETIME)
//  var publicationEndDateTime: Date = _
//
//  @Field(FIELD__PUBLICATION_START_DATETIME)
//  var publicationStartDateTime: Date = _
//
//  @Field(FIELD__ROLE_ID)
//  var roleId: JList[String] = _
//
//  @Field(FIELD__STATUS)
//  var status: JInteger = _
//
//
//  @Field(FIELD__MIME_TYPE)
//  var mimeType: String = _
//
//  @Field(FIELD__CREATOR_ID)
//  var creatorId: JInteger = _ //
//
//  @Field(FIELD__PUBLISHER_ID)
//  var publisherId: JInteger = _
//
//  @Field(FIELD__ALIAS)
//  var alias: String = _
//
//  @Field(FIELD__TEMPLATE)
//  var template: String = _
//
//  @Field(FIELD__CHILD_ID)
//  var childId: JList[JInteger] = _
//
//  @Field(FIELD__HAS_CHILDREN)
//  var hasChildren: JBoolean = _
}