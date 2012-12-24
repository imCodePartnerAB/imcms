package com.imcode
package imcms
package admin.doc

import _root_.imcode.server.document._
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject

import scala.collection.JavaConverters._

import com.vaadin.event.Action
import com.imcode.imcms.admin.doc.meta.MetaEditor
import com.imcode.imcms.mapping.ProfileMapper
import com.imcode.imcms.admin.doc.content._
import com.imcode.imcms.admin.doc.content.filedoc.FileDocContentEditor

import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._
import com.imcode.imcms.admin.doc.projection._
import com.imcode.imcms.api.{I18nMeta, I18nLanguage}
import com.vaadin.terminal.ExternalResource
import imcode.server.user.UserDomainObject

// import _root_.com.imcode.imcms.mapping.ProfileMapper.SimpleProfile ????

object Actions {
  val IncludeToSelection = new Action("doc.mgr.action.include_to_selection".i)
  val ExcludeFromSelection = new Action("doc.mgr.action.exclude_from_selection".i)
  val Delete = new Action("doc.mgr.action.delete".i)
  val EditMeta = new Action("doc.mgr.action.edit_meta".i)
}

class DocManager(app: ImcmsApplication) extends ImcmsServicesSupport {
  val projection = new DocsProjection(app.imcmsUser)
  val projectionOps = new DocsProjectionOps(projection)
  val customDocs = new CustomDocs(app.imcmsUser)

  val docSelectionDlg = new OKDialog("doc.dlg_selection.caption".i) with CustomSizeDialog |>> { dlg =>
    dlg.mainUI = customDocs.ui
    dlg.setSize(500, 500)
  }

  val ui = new DocManagerUI(projection.ui) |>> { ui =>
    ui.miSelectionShow.setCommandHandler {
      app.getMainWindow.show(docSelectionDlg, modal = false, resizable = true)
    }

    ui.miShow.setCommandHandler { projectionOps.showSelectedDoc() }
    ui.miEdit.setCommandHandler { projectionOps.editSelectedDoc() }

    ui.miNewTextDoc.setCommandHandler { projectionOps.mkDocOfType[TextDocumentDomainObject] }
    ui.miNewFileDoc.setCommandHandler { projectionOps.mkDocOfType[FileDocumentDomainObject] }
    ui.miNewUrlDoc.setCommandHandler { projectionOps.mkDocOfType[UrlDocumentDomainObject] }

    ui.miProfileEditName.setCommandHandler {
      whenSingle(projection.docsUI.selection) { docId =>
        val docIdStr = docId.toString
        val profileMapper = new ProfileMapper(imcmsServices.getDatabase)
        val profileOpt = profileMapper.getAll.asScala.find(_.getDocumentName == docIdStr)

        app.getMainWindow.initAndShow(new OkCancelDialog("Edit profile name")) { dlg =>
          val mainUI = new DocProfileNameEditorUI
          mainUI.txtName.value = profileOpt.map(_.getName).getOrElse("")

          dlg.mainUI = mainUI

          dlg.setOkButtonHandler {
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
      projectionOps.copySelectedDoc()
    }

    projection.docsUI.addActionHandler(new Action.Handler {
      import Actions._

      def getActions(target: AnyRef, sender: AnyRef) = Array(IncludeToSelection, Delete)

      def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
        action match {
          case IncludeToSelection =>
            customDocs.projection.docsContainer.addItem(target)
            customDocs.projection.updateUI()
            customDocs.projection.reload()
          case _ =>
        }
    })
  }
}


class DocManagerUI(searchUI: DocsProjectionUI) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miNew = mb.addItem("doc.mgr.mi.new".i)
  val miNewTextDoc = miNew.addItem("doc.mgr.mi.new.text_doc".i)
  val miNewFileDoc = miNew.addItem("doc.mgr.mi.new.file_doc".i)
  val miNewUrlDoc = miNew.addItem("doc.mgr.mi.new.url_doc".i)

  val miCopy = mb.addItem("doc.mgr.mi.copy".i)
  val miEdit = mb.addItem("doc.mgr.action.edit".i)
  val miDelete = mb.addItem("doc.mgr.action.delete".i)

  val miShow = mb.addItem("doc.mgr.mi.view".i)
  //val miViewContent = miView.addItem("doc.mgr.mi.view.content".i)
  //val miViewStructure = miView.addItem("doc.mgr.mi.view.structure".i)

  val miSelection = mb.addItem("doc.mgr.mi.selection".i)
  val miSelectionShow = miSelection.addItem("doc.mgr.mi.selection.show".i)
  val miProfile = mb.addItem("doc.mgr.mi.profile".i)

  val miProfileEditName = miProfile.addItem("doc.mgr.mi.profile.edit_name".i)

  this.addComponents(mb, searchUI)
  setExpandRatio(searchUI, 1.0f)
}



/**
 * Custom docs .
 */
class CustomDocs(user: UserDomainObject) {
  val projection = new DocsProjection(user)
  val ui = new CustomDocsUI(projection.ui)

  projection.docsUI.addActionHandler(new Action.Handler {
    import Actions._

    def getActions(target: AnyRef, sender: AnyRef) = Array(ExcludeFromSelection, Delete)

    def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
      action match {
        case ExcludeFromSelection => sender.asInstanceOf[Table].removeItem(target)
        case _ =>
      }
  })
}


class CustomDocsUI(projectionUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("doc.selection.mi.doc".i)

  this.addComponents(mb, projectionUI)
  setExpandRatio(projectionUI, 1.0f)
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