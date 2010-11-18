package com.imcode.imcms.sysadmin.cache

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.mapping.DocumentLoaderCachingProxy
import java.util.Date
import com.imcode._
import imcms.api.DocumentVersion
import imcode.server.document.DocumentTypeDomainObject;

class View(docLoaderCache: DocumentLoaderCachingProxy) extends VerticalLayout with Margin with Spacing {
  val tblMetas = new Table("Metas") with I18nCaption with Selectable with Immediate {
    addContainerProperties(this,
      ContainerProperty[JInteger]("Id"),
      ContainerProperty[String]("Type"),
      ContainerProperty[Date]("Created date"),
      ContainerProperty[Date]("Modified date"),
      ContainerProperty[String]("Default version"))    
  }
  val tblVersions = new Table("Available versions") with I18nCaption {
    addContainerProperties(this,
      ContainerProperty[JInteger]("No"),
      ContainerProperty[Date]("Created"),
      ContainerProperty[Date]("Modified"))
  }
  val tblDocs = new Table("Documents") with I18nCaption {
    addContainerProperties(this,
      ContainerProperty[JInteger]("#"),
      ContainerProperty[String]("Version"),
      ContainerProperty[String]("Language"))
  }

  val tblLanguages = new Table("Available languages") with I18nCaption {
    addContainerProperties(this,
      ContainerProperty[String]("Name"))    
  }
  
  val btnReload = new Button("Reload") with LinkStyle

  private val lytTables = new GridLayout(2,2) with Spacing {
    addComponents(this, tblMetas, tblDocs, tblVersions, tblLanguages)    
  }

  def reload() {
    tblMetas.removeAllItems
    for ((id, m) <- docLoaderCache.getMetas) {
      tblMetas.addItem(Array(id,
                             DocumentTypeDomainObject.TYPES.get(m.getDocumentTypeId).getName,
                             m.getCreatedDatetime,
                             m.getModifiedDatetime,
                             let(m.getDefaultVersionNo) { no =>
                               if (no == DocumentVersion.WORKING_VERSION_NO) "Working" else no.toString
                             }),
                       id)
    }
  }

  btnReload addListener block {
    reload()
  }

  tblMetas addListener block {
    tblVersions.removeAllItems
    tblDocs.removeAllItems
    tblLanguages.removeAllItems
    
    whenSelected[JInteger](tblMetas) { docId =>
      docLoaderCache.getMeta(docId).getLanguages foreach { l =>
        tblLanguages.addItem(Array(l.getName), l.getId)  
      }

      docLoaderCache.getDocumentVersionInfo(docId).getVersions foreach { v =>
        tblVersions.addItem(Array(v.getNo, v.getCreatedDt, v.getModifiedDt), v.getNo)
      }                                                                                

      val counter = new java.util.concurrent.atomic.AtomicInteger(0)
      
      for {
        docMap <- docLoaderCache.getWorkingDocuments.values
        (id, doc) <- docMap if id == docId
        no = Int box counter.incrementAndGet
      } tblDocs.addItem(Array(no, "Working", doc.getLanguage.getName), no)

      for {
        docMap <- docLoaderCache.getDefaultDocuments.values
        (id, doc) <- docMap if id == docId
        no = Int box counter.incrementAndGet
      } tblDocs.addItem(Array(no, doc.getVersionNo.toString, doc.getLanguage.getName), no)
    }
  }

  addComponent(btnReload)
  addComponent(lytTables)
}