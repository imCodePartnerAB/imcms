package com.imcode
package imcms.admin.instance.monitor.cache

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import java.util.Date
import imcms.api.DocumentVersion
import imcode.server.document.DocumentTypeDomainObject
import imcms.mapping.{DocLoaderCachingProxy}
import com.imcode.imcms.vaadin.ui._

class View(docLoaderCache: DocLoaderCachingProxy) extends VerticalLayout with Margin with Spacing {
  val tblMetas = new Table("Metas") with GenericProperty[JInteger] with Selectable with Immediate {
    addContainerProperties(this,
      PropertyDescriptor[JInteger]("Id"),
      PropertyDescriptor[String]("Type"),
      PropertyDescriptor[Date]("Created date"),
      PropertyDescriptor[Date]("Modified date"),
      PropertyDescriptor[String]("Default version"))
  }
  val tblVersions = new Table("Available versions") {
    addContainerProperties(this,
      PropertyDescriptor[JInteger]("No"),
      PropertyDescriptor[Date]("Created"),
      PropertyDescriptor[Date]("Modified"))
  }
  val tblDocs = new Table("Documents") {
    addContainerProperties(this,
      PropertyDescriptor[JInteger]("#"),
      PropertyDescriptor[String]("Version"),
      PropertyDescriptor[String]("Language"))
  }

  val tblLanguages = new Table("Available languages") {
    addContainerProperties(this,
      PropertyDescriptor[String]("Name"))
  }
  
  val btnReload = new Button("Reload") with LinkStyle

  private val lytTables = new GridLayout(2,2) with Spacing {
    addComponentsTo(this, tblMetas, tblDocs, tblVersions, tblLanguages)
  }

  def reload() {
    tblMetas.removeAllItems
//    for ((id, m) <- docLoaderCache.getMetas) {
//      tblMetas.addItem(Array(id,
//                             DocumentTypeDomainObject.TYPES.get(m.getDocumentTypeId).getName,
//                             m.getCreatedDatetime,
//                             m.getModifiedDatetime,
//                             let(m.getDefaultVersionNo) { no =>
//                               if (no == DocumentVersion.WORKING_VERSION_NO) "Working" else no.toString
//                             }),
//                       id)
//    }
  }

  btnReload addClickHandler {
    reload()
  }

  tblMetas addValueChangeHandler {
    tblVersions.removeAllItems
    tblDocs.removeAllItems
    tblLanguages.removeAllItems
    
    whenSelected(tblMetas) { docId =>
      docLoaderCache.getMeta(docId).getEnabledLanguages foreach { l =>
        tblLanguages.addItem(Array(l.getName), l.getId)  
      }

      docLoaderCache.getDocVersionInfo(docId).getVersions foreach { v =>
        tblVersions.addItem(Array(v.getNo, v.getCreatedDt, v.getModifiedDt), v.getNo)
      }                                                                                

      val counter = new java.util.concurrent.atomic.AtomicInteger(0)
      
//      for {
//        docMap <- docLoaderCache.getWorkingDocuments.values
//        (id, doc) <- docMap if id == docId
//        no = Int box counter.incrementAndGet
//      } tblDocs.addItem(Array(no, "Working", doc.getLanguage.getName), no)
//
//      for {
//        docMap <- docLoaderCache.getDefaultDocuments.values
//        (id, doc) <- docMap if id == docId
//        no = Int box counter.incrementAndGet
//      } tblDocs.addItem(Array(no, doc.getVersionNo.toString, doc.getLanguage.getName), no)
    }
  }

  addComponent(btnReload)
  addComponent(lytTables)
}