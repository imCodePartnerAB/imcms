package com.imcode
package imcms
package admin.doc

import _root_.scala.collection.JavaConversions._
import _root_.com.vaadin.event.Action
import _root_.com.imcode.imcms.vaadin._
import _root_.com.imcode.imcms.admin.doc.search.{DocSearch, AllDocsContainer, CustomDocsContainer}
import _root_.com.imcode.imcms.admin.doc.meta.MetaEditor
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.document.{DocumentDomainObject, FileDocumentDomainObject, HtmlDocumentDomainObject}
import com.vaadin.ui._
import mapping.ProfileMapper
import mapping.ProfileMapper.SimpleProfile

object Actions {
  val IncludeToSelection = new Action("doc.mgr.action.include_to_selection".i)
  val ExcludeFromSelection = new Action("doc.mgr.action.exclude_from_selection".i)
  val Delete = new Action("doc.mgr.action.delete".i)
  val EditMeta = new Action("doc.mgr.action.edit_meta".i)
}


class DocManager(app: ImcmsApplication) extends ImcmsServicesSupport {
  val customDocs = new CustomDocs
  val search = new DocSearch(new AllDocsContainer)

  val docSelectionDlg = letret(new OKDialog("doc.dlg_selection.caption".i) with CustomSizeDialog) { dlg =>
    dlg.mainUI = customDocs.ui
    dlg.setSize(500, 500)
  }

  val ui = letret(new DocManagerUI(search.ui)) { ui =>
    ui.miShowSelection.setCommandHandler {
      app.show(docSelectionDlg, modal = false, resizable = true)
    }

    ui.miEditMeta.setCommandHandler {
      val dlg = new OkCancelDialog("Doc properties") with CustomSizeDialog with BottomMarginDialog
      val doc = imcmsServices.getDocumentMapper.getWorkingDocument(search.searchResultUI.first.get.intValue)
      val metaEditor = new MetaEditor(app, doc)

      dlg.mainUI = metaEditor.ui

      dlg.wrapOkHandler {
        // 1.validate
        // 2.copy changes into doc:
        // 3.state: ValidationError Either Doc
        // properties.state
        imcmsServices.getDocumentMapper.saveDocument(metaEditor.state, app.user)
      }

      dlg.setSize(500, 500)
      app.show(dlg, resizable = true)
    }

    // todo: parent doc or profile must be selected
    ui.miDocNewText.setCommandHandler {
      val dlg = new OkCancelDialog("New Text Document") with CustomSizeDialog with BottomMarginDialog

      val doc = new TextDocumentDomainObject
      val properties = new MetaEditor(app, doc)
      val contentUI = new NewTextDocumentFlowPage2UI

      dlg.mainUI = letret(new com.vaadin.ui.TabSheet with FullSize) { ts =>
        ts.addTab(properties.ui, "Properties", null)
        ts.addTab(contentUI, "Content", null)
      }

      dlg.setSize(500, 500)
      app.show(dlg, resizable = true)
    }

    ui.miDocNewURL.setCommandHandler {
      val dlg = new OkCancelDialog("New URL Document") with CustomSizeDialog with BottomMarginDialog

      val doc = new HtmlDocumentDomainObject
      val properties = new MetaEditor(app, doc)
      val contentUI = new URLDocEditorUI

      dlg.mainUI = letret(new com.vaadin.ui.TabSheet with FullSize) { ts =>
        ts.addTab(properties.ui, "Properties", null)
        ts.addTab(contentUI, "Content", null)
      }

      dlg.setSize(500, 500)
      app.show(dlg, resizable = true)
    }

    ui.miDocNewFile.setCommandHandler {
      val dlg = new OkCancelDialog("New File Document") with CustomSizeDialog with BottomMarginDialog

      val doc = new FileDocumentDomainObject
      val properties = new MetaEditor(app, doc)
      val contentUI = new FileDocEditor(app, doc, Seq.empty).ui

      dlg.mainUI = letret(new com.vaadin.ui.TabSheet with FullSize) { ts =>
        ts.addTab(properties.ui, "Properties", null)
        ts.addTab(contentUI, "Content", null)
      }

      dlg.setSize(500, 500)
      app.show(dlg, resizable = true)
    }

    // todo: in current SuperAdmin there is no profile name check
    // i.e. multiple docs can have the same name and name can be any value
    ui.miProfileEditName.setCommandHandler {
      whenSingle(search.searchResultUI.value) { docId =>
        val docIdStr = docId.toString
        val profileMapper = new ProfileMapper(imcmsServices.getDatabase)
        val profileOpt = profileMapper.getAll.find(_.getDocumentName == docIdStr)

        app.initAndShow(new OkCancelDialog("Edit profile name")) { dlg =>
          val mainUI = new EditProfileNameUI
          mainUI.txtName.value = profileOpt.map(_.getName).getOrElse("")

          dlg.mainUI = mainUI

          dlg.wrapOkHandler {
            mainUI.txtName.trimOpt match {
              case Some(name) =>
                // check name is not taken by a profile with other id

                profileOpt match {
                  case Some(profile: ProfileMapper.SimpleProfile) =>
                    profileMapper.update(new ProfileMapper.SimpleProfile(profile.getId.toString, name, profile.getDocumentName))
                    app.showInfoNotification("Profile name is updated")

                  case _ =>
                    profileMapper.create(new ProfileMapper.SimpleProfile(null, name, docIdStr))
                    app.showInfoNotification("Profile name is assigned")
                }

              case _ =>
                for (profile <- profileOpt) {
                  profileMapper.delete(profile.getId)
                  app.showInfoNotification("Profile name is removed")
                }
            }
          }
        }
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


class DocManagerUI(searchUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("doc.mgr.mi.doc".i)
  val miDocNew = miDoc.addItem("doc.mgr.mi.doc.new".i)
  val miDocNewText = miDocNew.addItem("doc.mgr.action.doc.new_text_doc".i)
  val miDocNewFile = miDocNew.addItem("doc.mgr.action.doc.new_file_doc".i)
  val miDocNewHTML = miDocNew.addItem("doc.mgr.action.doc.new_html_doc".i)
  val miDocNewURL = miDocNew.addItem("doc.mgr.action.doc.new_url_doc".i)
  val miDocCopy = miDoc.addItem("doc.mgr.mi.doc.copy".i)
  val miDocEdit = miDoc.addItem("doc.mgr.action.doc.edit".i)
  val miDocDelete = miDoc.addItem("doc.mgr.action.doc.delete".i)
  val miView = mb.addItem("doc.mgr.mi.view".i)
  val miShowSelection = miView.addItem("doc.mgr.action.show_selection".i)
  val miEditMeta = mb.addItem("doc.mgr.action.edit_meta".i)
  val miProfile = mb.addItem("doc.mgr.mi.profile".i)
  val miProfileEditName = miProfile.addItem("doc.mgr.mi.profile.edit_name".i)

  addComponents(this, mb, searchUI)
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

  addComponents(this, mb, searchUI)
  setExpandRatio(searchUI, 1.0f)
}


// New doc flow's first page
// dialog???

/**
 * Creates and returns document which inherits parent doc.
 */
class NewDocFactory(parentDoc: Option[DocumentDomainObject]) extends ImcmsServicesSupport {

  val ui = letret(new NewDocFactoryUI) { ui =>

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


  val ogProfile = new OptionGroup()
  val ogNamedProfile = new OptionGroup()
  val ogCopy = new OptionGroup()

  val cbDocTypeType = new ComboBox()
  val cbNamedProfileType = new ComboBox()
  val cbCopyType = new ComboBox()

//  addComponents(this,
//    ogProfile, txtParentDoc, cb)
}


class EditProfileNameUI extends FormLayout with UndefinedSize {
  val txtName = new TextField("Name")

  addComponent(txtName)
}
