package com.imcode
package imcms.admin.filesystem

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.imcode.imcms.admin.filesystem.{IconImagePicker, FileBrowserWithImagePreview}
import imcode.server.document.{CategoryDomainObject}
import java.io.File
import com.vaadin.ui.Window.Notification

class FileManager {
  val browser = letret(new FileBrowser) { browser =>
    browser.addLocation("Home", Imcms.getPath)
    browser.addLocation("Templates", new File(Imcms.getPath, "WEB-INF/templates/text"))
    browser.addLocation("Images", new File(Imcms.getPath, "images"))
    browser.addLocation("Conf", new File(Imcms.getPath, "WEB-INF/conf"))
    browser.addLocation("Logs", new File(Imcms.getPath, "WEB-INF/logs"))
  }

  val ui = letret(new FileManagerUI(browser.ui)) { ui =>
  }
}


class FileManagerUI(browserUI: FileBrowserUI) extends VerticalLayout with FullSize {
  val mb = new MenuBar
  val miReload = mb.addItem("Reload", null)
  val miView = mb.addItem("View", null)
  val miEdit = mb.addItem("Edit", null)
  val miCopy = mb.addItem("Copy", null)
  val miMove = mb.addItem("Move", null)
  val miDelete = mb.addItem("Delete", null)
  val miDownload = mb.addItem("Download", null)
  val miUpload = mb.addItem("Upload", null)

  addComponents(this, mb, browserUI)
}


//      lytButtons.btnReload addListener block { fileBrowser.reload()}
//      lytButtons.btnCopy addListener block {
//        initAndShow(new OkCancelDialog("Copy to - choose destination directory")
//            with CustomSizeDialog with BottomMarginDialog, resizable = true) { w =>
//          let(w.mainContent = new FileBrowser) { b =>
//            b setSplitPosition 30
//            b addDirectoryTree("Home", Imcms.getPath)
//            b addDirectoryTree("Templates", new File(Imcms.getPath, "WEB-INF/templates/text"))
//            b addDirectoryTree("Images", new File(Imcms.getPath, "images"))
//            b addDirectoryTree("Conf", new File(Imcms.getPath, "WEB-INF/conf"))
//            b addDirectoryTree("Logs", new File(Imcms.getPath, "WEB-INF/logs"))
//          }
//
//          w setWidth "600px"
//          w setHeight "400px"
//        }
//      }