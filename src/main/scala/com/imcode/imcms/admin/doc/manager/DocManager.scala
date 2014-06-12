package com.imcode
package imcms
package admin.doc.manager

import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.admin.doc.projection.{DocsProjection, DocsProjectionOps}
import com.imcode.imcms.mapping.ProfileMapper
import com.imcode.imcms.vaadin.Current
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._
import com.vaadin.event.Action
import imcode.server.document.{FileDocumentDomainObject, HtmlDocumentDomainObject, UrlDocumentDomainObject}

import scala.collection.JavaConverters._

class DocManager extends ImcmsServicesSupport {
    val projection = new DocsProjection(Current.imcmsUser)
    val projectionOps = new DocsProjectionOps(projection)
    val customDocs = new CustomDocs(Current.imcmsUser)

    val docSelectionDlg = new OKDialog("doc.dlg_selection.caption".i) with CustomSizeDialog with Resizable with NonModal |>> { dlg =>
        dlg.mainComponent = customDocs.view
        dlg.setSize(500, 500)
    }

    val view = new DocManagerView(projection.view) |>> { w =>
        //    ui.miSelectionShow.setCommandHandler { _ =>
        //      Current.ui.addWindow(docSelectionDlg)
        //    }

        w.miShow.setCommandHandler { _ => projectionOps.showSelectedDoc() }
        w.miEdit.setCommandHandler { _ => projectionOps.editSelectedDoc() }

        w.miNewTextDoc.setCommandHandler { _ => projectionOps.mkDocOfType[TextDocumentDomainObject] }
        w.miNewFileDoc.setCommandHandler { _ => projectionOps.mkDocOfType[FileDocumentDomainObject] }
        w.miNewUrlDoc.setCommandHandler { _ => projectionOps.mkDocOfType[UrlDocumentDomainObject] }
        w.miNewHtmlDoc.setCommandHandler { _ => projectionOps.mkDocOfType[HtmlDocumentDomainObject] }
        w.miDelete.setCommandHandler { _ => projectionOps.deleteSelectedDocs() }

        w.miProfileEditName.setCommandHandler { _ =>
            whenSingleton(projection.docsView.selection) { docId =>
                val docIdStr = docId.toString
                val profileMapper = new ProfileMapper(imcmsServices.getDatabase)
                val profileOpt = profileMapper.getAll.asScala.find(_.getDocumentName == docIdStr)

                new OkCancelDialog("Edit profile name") |>> { dlg =>
                    val mainWidget = new DocProfileNameEditorView
                    mainWidget.txtName.value = profileOpt.map(_.getName).getOrElse("")

                    dlg.mainComponent = mainWidget

                    dlg.setOkButtonHandler {
                        mainWidget.txtName.trimmedValueOpt match {
                            case Some(name) =>
                                // check name is not taken by a profile with other id

                                profileOpt match {
                                    case Some(profile: ProfileMapper.SimpleProfile) =>
                                        profileMapper.update(new ProfileMapper.SimpleProfile(profile.getId.toString, name, profile.getDocumentName))
                                        Current.page.showInfoNotification("Profile name is updated")

                                    case _ =>
                                        profileMapper.create(new ProfileMapper.SimpleProfile(null, name, docIdStr))
                                        Current.page.showInfoNotification("Profile name is assigned")
                                  }

                            case _ =>
                                for (profile <- profileOpt) {
                                    profileMapper.delete(profile.getId)
                                    Current.page.showInfoNotification("Profile name is removed")
                                }
                      }
                    }
                } |> Current.ui.addWindow
            }
        }

        w.miCopy.setCommandHandler { _ => projectionOps.copySelectedDoc() }

        projection.docsView.addActionHandler(new Action.Handler {
            def getActions(target: AnyRef, sender: AnyRef) = Array(Actions.IncludeToSelection, Actions.Delete)

            def handleAction(action: Action, sender: AnyRef, target: AnyRef) = action match {
                  case Actions.IncludeToSelection =>
                      customDocs.projection.docsContainer.addItem(target)
                      customDocs.projection.reload()

                  case _ =>
              }
        })

        projection.listen { docs =>
            val isSingle = docs.size == 1
            val isSelected = docs.nonEmpty

            w.miCopy.setEnabled(isSingle)
            w.miEdit.setEnabled(isSingle)
            w.miShow.setEnabled(isSingle)
            w.miDelete.setEnabled(isSelected)
        }

        projection.notifyListeners()
    }
}
