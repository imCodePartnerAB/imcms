package com.imcode.imcms.sysadmin.cache

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.mapping.DocumentLoaderCachingProxy
import java.util.Date
import com.imcode._;

class View(docLoaderCache: DocumentLoaderCachingProxy) extends VerticalLayout with Margin with Spacing {
  val tblMetas = new Table with Selectable with Immediate {
    addContainerProperties(this,
      ContainerProperty[JInteger]("Id"),
      ContainerProperty[String]("Type"),
      ContainerProperty[String]("Default version"))    
  }
  val tblVersions = new Table {
    addContainerProperties(this,
      ContainerProperty[JInteger]("No"),
      ContainerProperty[Date]("Created"),
      ContainerProperty[Date]("Modified"))
  }
  val tblDocs = new Table {
    addContainerProperties(this,
      ContainerProperty[JInteger]("No"),
      ContainerProperty[JInteger]("Version"),
      ContainerProperty[String]("Language"))
  }

  val tblDocLanguages = new Table {
    addContainerProperties(this,
      ContainerProperty[String]("Code"),
      ContainerProperty[String]("Name"))    
  }
  
  val btnReload = new Button("Reload") with LinkStyle

  private val lytTables = new GridLayout(2, 2) with Spacing {
    addComponent(tblMetas, 0, 0, 1, 0);
    addComponent(tblDocs)
    addComponent(tblVersions)
  }

  def reload() {
    tblMetas.removeAllItems
    for ((id, m) <- docLoaderCache.getMetas) {
      tblMetas.addItem(Array(id, m.getDocumentTypeId, m.getDefaultVersionNo), id)
    }
  }

  btnReload addListener unit {
    reload()
  }

  tblMetas addListener unit {
    tblVersions.removeAllItems
    tblDocs.removeAllItems
    
    whenSelected[JInteger](tblMetas) { selectedId =>
      let(docLoaderCache.getDocumentVersionInfo(selectedId)) { vi =>
        vi.getVersions foreach { v =>
          tblVersions.addItem(Array(v.getNo, v.getCreatedDt, v.getModifiedDt), v.getNo)
        }
      }

      val counter = new java.util.concurrent.atomic.AtomicInteger(0);
      
      for {
        docMap <- docLoaderCache.getWorkingDocuments.values
        (id, doc) <- docMap if id == selectedId
      } tblDocs.addItem(Array(Int box counter.incrementAndGet, doc.getVersionNo, doc.getLanguage.getCode), Int box counter.get)

      for {
        docMap <- docLoaderCache.getDefaultDocuments.values
        (id, doc) <- docMap if id == selectedId
      } tblDocs.addItem(Array(Int box counter.incrementAndGet, doc.getVersionNo, doc.getLanguage.getCode), Int box counter.get)
    }
  }

  addComponent(btnReload)
  addComponent(lytTables)
}

// new I18n(resource)
// I18n.
// I18n.cacheView(View)