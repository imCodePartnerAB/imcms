package com.imcode
package imcms
package admin.doc

import scala.collection.JavaConversions._
import _root_.imcode.server.document._
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject

import com.vaadin.event.Action
import com.imcode.imcms.vaadin._
import com.imcode.imcms.admin.doc.meta.MetaEditor
import com.imcode.imcms.mapping.ProfileMapper
import com.imcode.imcms.admin.doc.content._
import com.imcode.imcms.admin.doc.content.filedoc.FileDocContentEditor
import com.imcode.imcms.admin.doc.search.{DocSearchUI, DocSearch, AllDocsContainer, CustomDocsContainer}

import java.net.URL
import com.vaadin.terminal.ExternalResource
import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._

// import _root_.com.imcode.imcms.mapping.ProfileMapper.SimpleProfile ????

object Actions {
  val IncludeToSelection = new Action("doc.mgr.action.include_to_selection".i)
  val ExcludeFromSelection = new Action("doc.mgr.action.exclude_from_selection".i)
  val Delete = new Action("doc.mgr.action.delete".i)
  val EditMeta = new Action("doc.mgr.action.edit_meta".i)
}


object DocManager {

  def mkDocEditorDlg(doc: DocumentDomainObject, caption: String) = {
    val dlg = new OkCancelDialog(caption) with CustomSizeDialog with BottomMarginDialog {
      val metaEditor = new MetaEditor(doc)
      val contentEditor = doc match {
        case textDoc: TextDocumentDomainObject => new TextDocContentEditor(textDoc)
        case fileDoc: FileDocumentDomainObject => new FileDocContentEditor(fileDoc)
        case urlDoc: UrlDocumentDomainObject => new UrlDocContentEditor(urlDoc)
        case _ => new UnsupportedDocContentEditor(doc)
      }

      mainUI = new TabSheet with FullSize |>> { ts =>
        ts.addTab(metaEditor.ui, "Properties", null)
        ts.addTab(contentEditor.ui, "Content", null)
      }
    }

    dlg
  }
}

class DocManager(app: ImcmsApplication) extends ImcmsServicesSupport {
  val search = new DocSearch(new AllDocsContainer)
  val customDocs = new CustomDocs

  val docSelectionDlg = new OKDialog("doc.dlg_selection.caption".i) with CustomSizeDialog |>> { dlg =>
    dlg.mainUI = customDocs.ui
    dlg.setSize(500, 500)
  }

  val ui = new DocManagerUI(search.ui) |>> { ui =>
    ui.miSelectionShow.setCommandHandler {
      app.getMainWindow.show(docSelectionDlg, modal = false, resizable = true)
    }

    // todo: check select single doc
    // todo: add embedded/popu view???
    // todo: remove buttons
    ui.miView.setCommandHandler {
      whenSingle(search.searchResultUI.selection) { docId =>
        val appURL = app.getURL
        val docURL = new URL(appURL.getProtocol, appURL.getHost, appURL.getPort, "/%d" format docId)

        app.getMainWindow.initAndShow(new OKDialog("Doc content") with CustomSizeDialog with NoMarginDialog, resizable = true) { dlg =>
//          dlg.mainUI = new Embedded with FullSize |>> { browser =>
//            browser.setType(Embedded.TYPE_BROWSER)
//            browser.setSource(new ExternalResource(new URL("/" + docId))) // docURL
//          }

          dlg.mainUI = new VerticalLayout with FullSize |>> { lyt =>
            val mb = new MenuBar
            val mi = mb.addItem("Menu")
            1 to 10 foreach { mi addItem _.toString }

            val emb = new Embedded with FullSize |>> { browser =>
              browser.setType(Embedded.TYPE_BROWSER)
              browser.setSource(new ExternalResource(docURL)) //
            }

            addComponentsTo(lyt, mb, emb)
            lyt.setExpandRatio(emb, 1.0f)
          }

          dlg.setSize(600, 600)
        }
      }
    }

    // todo: a doc must be selected
    // todo: allow change several at once???
    // todo: permissions
    ui.miEdit.setCommandHandler {
      search.searchResultUI.selection match {
        case Seq() =>
        case Seq(_, _, _*) =>
        case Seq(docId) => imcmsServices.getDocumentMapper.getDocument(docId) match {
          case null =>
          case doc =>
            app.getMainWindow.initAndShow(DocManager.mkDocEditorDlg(doc, "Edit document"), resizable = true) { dlg =>
              dlg.setSize(500, 500)

              dlg.setOkHandler {
                (dlg.metaEditor.collectValues(), dlg.contentEditor.collectValues()) match {
                  case (Left(errorMsgs), _) =>
                    ui.topWindow.showErrorNotification(errorMsgs.mkString(","))

                  case (_, Left(errorMsgs)) =>
                    ui.topWindow.showErrorNotification(errorMsgs.mkString(","))

                  case (Right((metaDoc, i18nMetas)), Right(doc)) =>
                    doc.setMeta(metaDoc.getMeta)

                    imcmsServices.getDocumentMapper.saveDocument(doc, i18nMetas, ui.getApplication.user)
                    search.search()
                    ui.topWindow.showInfoNotification("Document has been saved")
                    dlg.close()
                }
              }
            }
        }
      }
    } // ui.miEdit

    val newDocCommandListener: MenuBar#MenuItem => Unit = { mi =>
      search.searchResultUI.selection match {
        case Seq() =>
        case Seq(_, _, _*) =>
        case Seq(docId) => imcmsServices.getDocumentMapper.getDocument(docId) match {
          case null =>
          case doc if !doc.isInstanceOf[TextDocumentDomainObject] =>
          case doc: TextDocumentDomainObject =>
            val (docType, dlgCaption) = (mi: @unchecked) match {
              case ui.miNewTextDoc => (DocumentTypeDomainObject.TEXT, "New text document")
              case ui.miNewFileDoc => (DocumentTypeDomainObject.FILE, "New file document")
              case ui.miNewURLDoc => (DocumentTypeDomainObject.URL, "New url document")
              case ui.miNewHTMLDoc => (DocumentTypeDomainObject.HTML, "New html document")
            }

            val newDoc = imcmsServices.getDocumentMapper.createDocumentOfTypeFromParent(docType.getId, doc, ui.getApplication.user)
            app.getMainWindow.initAndShow(DocManager.mkDocEditorDlg(newDoc, dlgCaption), resizable = true) { dlg =>
              dlg.setSize(500, 500)
              dlg.setOkHandler {
                (dlg.metaEditor.collectValues(), dlg.contentEditor.collectValues()) match {
                  case (Left(errorMsgs), _) =>
                    ui.topWindow.showErrorNotification(errorMsgs.mkString(","))

                  case (_, Left(errorMsgs)) =>
                    ui.topWindow.showErrorNotification(errorMsgs.mkString(","))

                  case (Right((metaDoc, i18nMetas)), Right(doc)) =>
                    doc.setMeta(metaDoc.getMeta)

                    imcmsServices.getDocumentMapper.saveNewDocument(doc, i18nMetas, ui.getApplication.user)
                    search.search()
                    ui.topWindow.showInfoNotification("New document has been created")
                    dlg.close()
                }
              }
            }
        }
      }
    } // val newDocCommandListener

    doto(ui.miNewTextDoc, ui.miNewFileDoc, ui.miNewURLDoc, ui.miNewHTMLDoc) {
      _ setCommandListener newDocCommandListener
    }

    ui.miProfileEditName.setCommandHandler {
      whenSingle(search.searchResultUI.selection) { docId =>
        val docIdStr = docId.toString
        val profileMapper = new ProfileMapper(imcmsServices.getDatabase)
        val profileOpt = profileMapper.getAll.find(_.getDocumentName == docIdStr)

        app.getMainWindow.initAndShow(new OkCancelDialog("Edit profile name")) { dlg =>
          val mainUI = new DocProfileNameEditorUI
          mainUI.txtName.value = profileOpt.map(_.getName).getOrElse("")

          dlg.mainUI = mainUI

          dlg.setOkHandler {
            mainUI.txtName.trimOpt match {
              case Some(name) =>
                // check name is not taken by a profile with other id

                profileOpt match {
                  case Some(profile: ProfileMapper.SimpleProfile) =>
                    profileMapper.update(new ProfileMapper.SimpleProfile(profile.getId.toString, name, profile.getDocumentName))
                    app.getMainWindow.showInfoNotification("Profile name is updated")

                  case _ =>
                    profileMapper.create(new ProfileMapper.SimpleProfile(null, name, docIdStr))
                    app.getMainWindow.showInfoNotification("Profile name is assigned")
                }

              case _ =>
                for (profile <- profileOpt) {
                  profileMapper.delete(profile.getId)
                  app.getMainWindow.showInfoNotification("Profile name is removed")
                }
            }
          }
        }
      }
    }

    ui.miCopy.setCommandHandler {
      whenSingle(search.searchResultUI.selection) { docId =>
        // todo copy selected document VERSION, not working???
        // dialog with drop down???? -> version select
        imcmsServices.getDocumentMapper.copyDocument(imcmsServices.getDocumentMapper.getWorkingDocument(docId), app.user)
        search.search()
        app.getMainWindow.showInfoNotification("Document has been copied")
      }
    }

    search.searchResultUI.addActionHandler(new Action.Handler {
      import Actions._

      def getActions(target: AnyRef, sender: AnyRef) = Array(IncludeToSelection, Delete)

      def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
        action match {
          case IncludeToSelection =>
            customDocs.search.docsContainer.addItem(target)
            customDocs.search.update()
            customDocs.search.search()
          case _ =>
        }
    })
  }
}


class DocManagerUI(searchUI: DocSearchUI) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miNew = mb.addItem("doc.mgr.mi.new".i)
  val miNewTextDoc = miNew.addItem("doc.mgr.mi.new.text_doc".i)
  val miNewFileDoc = miNew.addItem("doc.mgr.mi.new.file_doc".i)
  val miNewHTMLDoc = miNew.addItem("doc.mgr.mi.new.html_doc".i)
  val miNewURLDoc = miNew.addItem("doc.mgr.mi.new.url_doc".i)

  val miCopy = mb.addItem("doc.mgr.mi.copy".i)
  val miEdit = mb.addItem("doc.mgr.action.edit".i)
  val miDelete = mb.addItem("doc.mgr.action.delete".i)

  val miView = mb.addItem("doc.mgr.mi.view".i)
  //val miViewContent = miView.addItem("doc.mgr.mi.view.content".i)
  //val miViewStructure = miView.addItem("doc.mgr.mi.view.structure".i)

  val miSelection = mb.addItem("doc.mgr.mi.selection".i)
  val miSelectionShow = miSelection.addItem("doc.mgr.mi.selection.show".i)
  val miProfile = mb.addItem("doc.mgr.mi.profile".i)

  val miProfileEditName = miProfile.addItem("doc.mgr.mi.profile.edit_name".i)

  addComponentsTo(this, mb, searchUI)
  setExpandRatio(searchUI, 1.0f)
}



/**
 * Custom docs selection.
 */
class CustomDocs {
  val search = new DocSearch(new CustomDocsContainer)
  val ui = new CustomDocsUI(search.ui)

  search.searchResultUI.addActionHandler(new Action.Handler {
    import Actions._

    def getActions(target: AnyRef, sender: AnyRef) = Array(ExcludeFromSelection, Delete)

    def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
      action match {
        case ExcludeFromSelection => sender.asInstanceOf[Table].removeItem(target)
        case _ =>
      }
  })
}


class CustomDocsUI(searchUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("doc.selection.mi.doc".i)

  addComponentsTo(this, mb, searchUI)
  setExpandRatio(searchUI, 1.0f)
}


// New doc flow's first page
// dialog???

/**
 * Creates and returns document which inherits parent doc.
 */
class NewDocFactory(parentDoc: Option[DocumentDomainObject]) extends ImcmsServicesSupport {

  val ui = new NewDocFactoryUI |>> { ui =>

  }

  //def doc = /*if copy*/ if (true) imcmsServices.getDocumentMapper.copyDocument(parentDoc)
}

class NewDocFactoryUI extends GridLayout(3, 3) with UndefinedSize {

  // copy, profile, iinheritance
//  val btnSelectProfile = new Button("...")
//  val btnSelectNamedProfile = new Button("...")
//  val btnSelectForCopy = new Button("...")
  val txtProfileDoc = new TextField
  val cbNamedProfileDoc = new TextField
  val txtCopyDoc = new TextField


  val ogProfile = new OptionGroup
  val ogNamedProfile = new OptionGroup
  val ogCopy = new OptionGroup

  val cbDocTypeType = new ComboBox
  val cbNamedProfileType = new ComboBox
  val cbCopyType = new ComboBox

//  addComponents(this,
//    ogProfile, txtParentDoc, cb)
}


class DocProfileNameEditorUI extends FormLayout with UndefinedSize {
  val txtName = new TextField("Name")

  addComponent(txtName)
}


// DOC STRUCTURE OUTLINE

//def docStructure = new TabSheetView {
//  addTab(new VerticalLayoutUI("Document structure outline") {
//    val lytMenu = new HorizontalLayout {
//      setSpacing(true)
//      val txtId = new TextField("Text doc (meta) id")
//      val btnShow = new Button("Show")
//
//      addComponents(this, txtId, btnShow)
//    }
//    val lytStructure = new VerticalLayout {
//      setSpacing(true)
//    }
//
//    lytMenu.btnShow addClickHandler {
//      lytMenu.txtId.getValue match {
//        case IntNumber(id) =>
//          Imcms.getServices.getDocumentMapper.getDocument(id) match {
//            case null =>
//              app.getMainWindow.show(new MsgDialog("Information", "No document with id ["+id+"]."))
//            case doc: TextDocumentDomainObject =>
//              lytStructure.removeAllComponents
//              lytStructure.addComponent(new Form(new GridLayout(2,1)) {
//                setSpacing(true)
//                setCaption("Texts")
//                let(getLayout.asInstanceOf[GridLayout]) { l =>
//                  for ((textId, text) <- doc.getTexts) {
//                    addComponents(l, new Label(textId.toString), new Label(text.getText))
//                  }
//                }
//              })
//
//             lytStructure.addComponent(new Form(new GridLayout(2,1)) {
//                setSpacing(true)
//                setCaption("Images")
//                let(getLayout.asInstanceOf[GridLayout]) { l =>
//                  for ((imageId, image) <- doc.getImages) {
//                    addComponents(l, new Label(imageId.toString), new Label(image.getImageUrl))
//                  }
//                }
//              })
//
//             lytStructure.addComponent(new Form(new GridLayout(2,1)) {
//                setSpacing(true)
//                setCaption("Menus")
//                let(getLayout.asInstanceOf[GridLayout]) { l =>
//                  for ((menuId, menu) <- doc.getMenus) {
//                    addComponents(l, new Label(menuId.toString), new Label(menu.getMenuItems.map(_.getDocumentId).mkString(", ")))
//                  }
//                }
//              })
//
//            case _ =>
//              app.getMainWindow.show(new MsgDialog("Information", "Not a text document."))
//
//          }
//        case _: String =>
//          app.getMainWindow.show(new MsgDialog("Information", "Document id must be integer."))
//      }
//    }
//
//    addComponents(this, lytMenu, lytStructure)
//  })
//}

