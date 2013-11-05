package com.imcode
package imcms
package admin.doc.manager

import com.vaadin.ui.UI
import com.imcode.imcms.admin.doc.projection.{DocsProjectionOps, DocsProjection}
import com.imcode.imcms.vaadin.ui.dialog._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._
import _root_.imcode.server.document.{UrlDocumentDomainObject, FileDocumentDomainObject}
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.mapping.ProfileMapper

import scala.collection.JavaConverters._
import com.vaadin.server.Page
import com.vaadin.event.Action

class DocManager(app: UI) extends ImcmsServicesSupport {
  val projection = new DocsProjection(app.imcmsUser)
  val projectionOps = new DocsProjectionOps(projection)
  val customDocs = new CustomDocs(app.imcmsUser)

  val docSelectionDlg = new OKDialog("doc.dlg_selection.caption".i) with CustomSizeDialog with Resizable with NonModal |>> { dlg =>
    dlg.mainUI = customDocs.ui
    dlg.setSize(500, 500)
  }

  val ui = new DocManagerUI(projection.ui) |>> { ui =>
//    ui.miSelectionShow.setCommandHandler { _ =>
//      UI.getCurrent.addWindow(docSelectionDlg)
//    }

    ui.miShow.setCommandHandler { _ => projectionOps.showSelectedDoc() }
    ui.miEdit.setCommandHandler { _ => projectionOps.editSelectedDoc() }

    ui.miNewTextDoc.setCommandHandler { _ => projectionOps.mkDocOfType[TextDocumentDomainObject] }
    ui.miNewFileDoc.setCommandHandler { _ => projectionOps.mkDocOfType[FileDocumentDomainObject] }
    ui.miNewUrlDoc.setCommandHandler { _ => projectionOps.mkDocOfType[UrlDocumentDomainObject] }
    ui.miDelete.setCommandHandler { _ => projectionOps.deleteSelectedDocs() }

    ui.miProfileEditName.setCommandHandler { _ =>
      whenSingleton(projection.docsUI.selection) { docId =>
        val docIdStr = docId.toString
        val profileMapper = new ProfileMapper(imcmsServices.getDatabase)
        val profileOpt = profileMapper.getAll.asScala.find(_.getDocumentName == docIdStr)

        new OkCancelDialog("Edit profile name") |>> { dlg =>
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
                    Page.getCurrent.showInfoNotification("Profile name is updated")

                  case _ =>
                    profileMapper.create(new ProfileMapper.SimpleProfile(null, name, docIdStr))
                    Page.getCurrent.showInfoNotification("Profile name is assigned")
                }

              case _ =>
                for (profile <- profileOpt) {
                  profileMapper.delete(profile.getId)
                  Page.getCurrent.showInfoNotification("Profile name is removed")
                }
            }
          }
        } |> UI.getCurrent.addWindow
      }
    }

    ui.miCopy.setCommandHandler { _ =>
      projectionOps.copySelectedDoc()
    }

    projection.docsUI.addActionHandler(new Action.Handler {

      def getActions(target: AnyRef, sender: AnyRef) = Array(Actions.IncludeToSelection, Actions.Delete)

      def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
        action match {
          case Actions.IncludeToSelection =>
            customDocs.projection.docsContainer.addItem(target)
            customDocs.projection.reload()
          case _ =>
        }
    })

    projection.listen { docs =>
      val isSingle = docs.size == 1
      val isSelected = docs.nonEmpty

      ui.miCopy.setEnabled(isSingle)
      ui.miEdit.setEnabled(isSingle)
      ui.miShow.setEnabled(isSingle)
      ui.miDelete.setEnabled(isSelected)
    }

    projection.notifyListeners()
  }
}
