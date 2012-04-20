package com.imcode
package imcms.admin



import scala.collection.JavaConversions._
import com.imcode._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcms.api._
import imcms.servlet.superadmin.AdminSearchTerms
import java.util.{Locale, Date}
import com.vaadin.ui._

//import imcms.admin.chat.{MessageView, Chat}

import imcms.admin.access.user.{UserManager}
import imcode.util.Utility
import imcode.server.user._
import imcode.server.{SystemData, Imcms}
import scala.actors.Actor._
import scala.actors._
import imcode.server.document.textdocument.TextDocumentDomainObject
import java.io.{ByteArrayInputStream, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{ThemeResource, UserError}
import imcode.server.document._
import com.imcode.imcms.vaadin._
import com.vaadin.ui.Window.Notification
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.imcode.imcms.vaadin.Theme.Icons._


/*
 todo: #ARCHITECTURE OVERVIEW#

 Admin Application
 |-manager1
   |-action1
   |-action2
   |-actionN
 |-manager2
   |-...
 |-managerN
   |-...

 FALSE STATEMENT, refactor: Administration is performed using managers and editors (sometimes combined into flows).
 Every manager is associated with a set of actions/tasks
 For each action a user must have a permission...

 * Editor is intended to change (and validate edited object) model but not to perform physical changes - that a
 * manager's task (commits).
 *
 * An editor can be viewed as a MVC where model is typically an administered object (such as document, text or image)
 * or its
 * itself or in more sophisticated cases an instance of a separate class containing additional parameters and attributes.
 * A view or UI of an editor is represented by one or several standard vaadin components such as Layout, Panel or Window.
 * A controller is just set of routines and callbacks located inside or outside of UI and/or model classes.
 * In some (mostly trivial) cases model and/or controller are embedded in a view component.
 *
 * A flow is also an editor which contains one or more editors which may have (partially) shared model and controller.
 * - commit???
 *
 * In some trivial cases an editor might not have a separate UI class.
 */
class Application extends com.vaadin.Application with ImcmsApplication { app =>

  def canAccess {
//            if ( !user.isSuperAdmin() && !user.isUserAdminAndCanEditAtLeastOneRole() ) {
//                Utility.forwardToLogin( request, response );
//                return;
//            }
//
//        if ( !user.canAccessAdminPages() ) {
//            Utility.forwardToLogin( request, response );
//            return;
//        }
  }

  // Main menu items IDS
//  object NewMenu {
//    object About
//    object Settings {
//      object Languages
//      object Properties
//    }
//    object Documents {
//      object Categories
//      object Templates
//    }
//    object Permissions {
//      object Users
//      object Roles
//      object IP_Access
//    }
//    object Statistics {
//      object SearchTerms
//      object SessionCounter
//    }
//    object Filesystem
//    object System {
//      object Cache
//    }
//  }

  object Menu extends MenuItem {
    object About extends MenuItem(this, Some(About16))

    object Documents extends MenuItem(this) {
      object Categories extends MenuItem(this, Some(Done16))
      object Templates extends MenuItem(this, Some(Done16))
    }
    object Permissions extends MenuItem(this) {
      object Users extends MenuItem(this, Some(Done16))
      object Roles extends MenuItem(this, Some(Done16))
      object IP_Access extends MenuItem(this, Some(Done16))
    }

    object System extends MenuItem(this) {
      object Settings extends MenuItem(this) {
        object Languages extends MenuItem(this, Some(Done16))
        object Properties extends MenuItem(this, Some(Done16))
      }
      object Monitor extends MenuItem(this) {
        object SearchTerms extends MenuItem(this)
        object Session extends MenuItem(this, Some(Done16))
        object Cache extends MenuItem(this)
        object LinkValidator extends MenuItem(this)
      }
      object Files extends MenuItem(this, Some(Done16))
    }
  }

  val systemDao = Imcms.getSpringBean("systemDao").asInstanceOf[SystemDao]



  def NA(id: Any) = new TabSheetView {
    addTab(new VerticalLayoutUI(id.toString) {
      addComponent(new Label("NOT AVAILABLE"))
    })
  }

  val labelAbout = new VerticalLayoutUI {
    addComponent(new Panel("About") {
      getContent |> {
        case c: VerticalLayout =>
          c.setMargin(true)
          c.setSpacing(true)
      }

      addComponent(new Label("""|Welcome to the imCMS new admin UI prototype -
                     | please pick a task from the menu. Note that some views are not (yet) available.
                     |""".stripMargin))
    })
  }

  val wndMain = new Window {
    val content = new HorizontalSplitPanel {
      setSplitPosition(15)
      setSizeFull
    }

    val treeMenu = new Tree {
      setImmediate(true)
    }

    def initMenu(menu: MenuItem, setParent: Boolean = false) {
      menu.parent match {
        case null =>
          menu.items foreach (initMenu(_))

        case parent =>
          treeMenu addItem menu
          if (setParent) treeMenu setParent (menu, parent)
          menu.icon foreach (treeMenu.setItemIcon(menu, _))

          menu.items match {
            case Nil => treeMenu setChildrenAllowed (menu, false)
            case items => items foreach (initMenu(_, setParent=true))
          }
      }
    }

    treeMenu addValueChangeListener { e =>
      content.setSecondComponent(
        e.getProperty.getValue match {
          case null | Menu.About => labelAbout

          case Menu.System.Monitor.SearchTerms => searchTerms
          case Menu.Documents.Categories => categories
          case Menu.System.Settings.Languages => languagesPanel
          case Menu.System.Settings.Properties => settingsProperties
          case Menu.System.Monitor.Session => sessionMonitor
          case Menu.Documents => documents
          case Menu.Permissions.Roles => roles
          case Menu.Permissions.Users => users
          case Menu.Permissions.IP_Access => ipAccess
          case Menu.System.Files => filesystem
          case Menu.Documents.Templates => templates
          case Menu.System.Monitor.Cache => systemCacheView

          case other => NA(other)
        })
    }


    content setFirstComponent treeMenu
    //this setContent content

    val splitView = new VerticalSplitPanel |< { p =>
      p.setFirstComponent(content)
      p.setSecondComponent(chat)
      p.setSplitPosition(85)
    }

    this setContent splitView
  }

  def init {
    setTheme("imcms")
    setLocale(new Locale(user.getLanguageIso639_2))
    wndMain initMenu Menu
    Menu.items foreach { wndMain.treeMenu expandItemsRecursively _ }
    wndMain.treeMenu select Menu.About
    this setMainWindow wndMain
  }


  // doadmin prototype
//  def docadmin = {
//    import com.imcode.imcms.admin.doc.{MetaEditor, EditorsFactory, MetaModel}
//
//    val dm = Imcms.getServices.getDocumentMapper
//    val btnNewTextDoc = new Button("New text doc")
//    val btnNewFileDoc = new Button("New file doc")
//    val btnNewUrlDoc = new Button("New url doc")
//    val btnDocInfo = new Button("Doc info")
//    val btnDocPermissions = new Button("Doc Permissions")
//    val btnEdit = new Button("Edit doc") // edit content
//    val btnReload = new Button("RELOAD") with LinkStyle
//
//    val docAdmin = new EditorsFactory(app, Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(1))
//
//    val tblDocs = new Table with ValueType[JInteger] with Selectable with Immediate {
//      addContainerProperties(this,
//        ContainerProperty[JInteger]("Id"),
//        ContainerProperty[String]("Type"),
//        ContainerProperty[Date]("Created date"),
//        ContainerProperty[Date]("Modified date"),
//        ContainerProperty[String]("Default version no"))
//    }
//
//    val lytBar = new HorizontalLayout with Spacing {
//      addComponents(this, btnNewTextDoc, btnNewFileDoc, btnNewUrlDoc, btnDocInfo, btnEdit, btnDocPermissions, btnReload)
//    }
//
//    btnReload addClickHandler { reload _ }
//
//    btnNewTextDoc addClickHandler {
//      app.initAndShow(new Dialog("New text document") with BottomMarginDialog) { dlg =>
//        val parentDoc = dm.getDocument(1001)
//        val onCommit = { doc: TextDocumentDomainObject =>
//          getMainWindow.showNotification("Text document [id = %d] has been created" format doc.getId, Notification.TYPE_HUMANIZED_MESSAGE)
//          dlg.close
//          reload()
//        }
//
//        val flow = docAdmin.newTextDocFlow(parentDoc)
//        val flowUI = flow.ui
//
//        flow.commitListeners += onCommit
//
//        dlg.setMainContent(flowUI)
//
//        flowUI.setWidth("600px")
//        flowUI.setHeight("800px")
//      }
//    }
//
//    btnDocPermissions addClickHandler {
//
//      whenSelected(tblDocs) { id =>
//        val model = MetaModel(id)
//        val editor = new PermissionsSheet(app, model.meta, Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(1))
//
//
//
//        app.initAndShow(new OkCancelDialog) { dlg =>
//          dlg.setMainContent(editor.ui)
//        }
//      }
//    }
//
//    btnDocInfo addClickHandler {
//      whenSelected(tblDocs) { id =>
//        val model = MetaModel(id)
//        val editor = new MetaEditor(app, model)
//
//        app.initAndShow(new OkCancelDialog) { d =>
//          d.setMainContent(editor.ui)
//        }
//      }
//    }
//
//    btnNewUrlDoc addClickHandler {
//      app.initAndShow(new Dialog("New url document")) { dlg =>
//        val parentDoc = dm.getDocument(1001)
//        val flow = docAdmin.newURLDocFlow(parentDoc)
//        dlg.setMainContent(flow.ui)
//        dlg.setWidth("600px")
//        dlg.setHeight("800px")
//      }
//    }
//
//    btnNewFileDoc addClickHandler {
//      app.initAndShow(new Dialog("New file document")) { dlg =>
//        val parentDoc = dm.getDocument(1001)
//        val onCommit = { doc: FileDocumentDomainObject =>
//          getMainWindow.showNotification("File document [id = %d] has been created" format doc.getId, Notification.TYPE_HUMANIZED_MESSAGE)
//          dlg.close
//          reload()
//        }
//
//        val flow = docAdmin.newFileDocFlow(parentDoc)
//        val flowUI = flow.ui
//
//        flow.commitListeners += onCommit
//
//        flowUI.bar.btnCancel addClickHandler {
//          dlg.close
//        }
//
//        dlg.mainUI = flowUI
//
//        dlg.setWidth("600px")
//        dlg.setHeight("800px")
//      }
//    }
//
//    btnEdit addClickHandler {
//      whenSelected(tblDocs) { id =>
//        // show edit meta dialog
//      }
//    }
//
//    def reload() {
//      tblDocs.removeAllItems
//      for {
//        id <- dm.getAllDocumentIds
//        meta = dm.getDocumentLoaderCachingProxy.getMeta(id)
//      } {
//        addItem(tblDocs, id, id, meta.getDocumentTypeId, meta.getCreatedDatetime, meta.getModifiedDatetime, meta.getDefaultVersionNo.toString)
//      }
//    }
//
//    new VerticalLayout with Margin with Spacing {
//      addComponents(this, lytBar, tblDocs)
//      reload()
//    }
//  } // docadmin

//  def systemCacheView = {
//    import com.imcode.imcms.sysadmin.cache.View
//
//    new cache.View(Imcms.getDocumentMapper.getCahcingDocumentGetter)
//  }
  def systemCacheView = new com.imcode.imcms.admin.system.monitor.cache.View(Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy)


  lazy val languagesPanel = new VerticalLayout with Margin {
    val manager = new com.imcode.imcms.admin.system.settings.language.LanguageManager(app)
    val tabSheet = new TabSheet
    tabSheet.addTab(manager.ui, "Language", Tab32)

    manager.ui.setMargin(true)

    addComponent(tabSheet)
  }


  lazy val documents = new VerticalLayout with Margin with FullSize {
    val manager = new com.imcode.imcms.admin.doc.DocManager(app)
    val tabSheet = new TabSheet with FullSize
    tabSheet.addTab(manager.ui, "Document", Tab32)

    manager.ui.setMargin(true)

    addComponent(tabSheet)
  }

  lazy val ipAccess = new VerticalLayout with Margin {
    val manager = new com.imcode.imcms.admin.access.ip.IPAccessManager(app)
    val tabSheet = new TabSheet
    tabSheet.addTab(manager.ui, "IP Access ", Tab32)

    manager.ui.setMargin(true)

    addComponent(tabSheet)
  }


  lazy val roles = new VerticalLayout with Margin {
    val roleManager = new com.imcode.imcms.admin.access.role.RoleManager(app)

    val tabSheet = new TabSheet
    tabSheet.addTab(roleManager.ui, "Roles and their permissions", Tab32)

    roleManager.ui.setMargin(true)

    addComponent(tabSheet)
  }




  lazy val settingsProperties = new VerticalLayout with Margin {
    val manager = new com.imcode.imcms.admin.system.settings.property.PropertyManagerManager(app)
    val tabSheet = new TabSheet
    tabSheet.addTab(manager.ui, "System Properties", Tab32)

    manager.ui.setMargin(true)

    addComponent(tabSheet)
  }


  lazy val sessionMonitor = new VerticalLayout with Margin {
    val manager = new com.imcode.imcms.admin.system.monitor.session.counter.SessionCounterManager(app)
    val tabSheet = new TabSheet
    tabSheet.addTab(manager.ui, "Counter", Tab32)

    manager.ui.setMargin(true)

    addComponent(tabSheet)
  }




  lazy val searchTerms = new TabSheetView {
    addTab(new VerticalLayoutUI("Popular search terms") {
      val tblTerms = new Table {
        addContainerProperties(this, ContainerProperty[String]("Term"), ContainerProperty[String]("Count"))
        setPageLength(10)
      }

      val lytBar = new HorizontalLayout {
        setSpacing(true)
        setCaption("Date range")
        val calFrom = new DateField()
        val calTo = new DateField()
        val btnReload = new Button("Reload")

        calFrom.setValue(new Date)
        //calFrom.setStyle("calendar")
        calFrom.setResolution(DateField.RESOLUTION_DAY)

        calTo.setValue(new Date)
        //calTo.setStyle("calendar")
        calTo.setResolution(DateField.RESOLUTION_DAY)

        addComponents(this, calFrom, calTo, btnReload)
      }

      addComponents(this, tblTerms, lytBar)

      def reload() {
        val terms = AdminSearchTerms.getTermCounts(lytBar.calFrom.getValue.asInstanceOf[Date],
          lytBar.calTo.getValue.asInstanceOf[Date])

        tblTerms.removeAllItems
        terms foreach { t =>
          val item = Array[AnyRef](t.getTerm, t.getCount.toString)
          tblTerms.addItem(item, item)
        }
      }

      lytBar.btnReload addClickHandler { reload() }

      reload()
    })
  }


  lazy val filesystem = new TabSheetView {
    tabSheet.setSizeFull
    setSizeFull
    addTab(new VerticalLayoutUI("File manager") {
      setSizeFull

      addComponent((new com.imcode.imcms.admin.system.file.FileManager(app)).ui)
    })
  }

  lazy val templates = new VerticalLayout with Margin {
    val templateManager = new com.imcode.imcms.admin.doc.template.TemplateManager(app)
    val templateGroupManager = new com.imcode.imcms.admin.doc.template.group.TemplateGroupManager(app)

    val tabSheet = new TabSheet
    tabSheet.addTab(templateManager.ui, "Templates", Tab32)
    tabSheet.addTab(templateGroupManager.ui, "Template Groups", Tab32)

    templateManager.ui.setMargin(true)
    templateGroupManager.ui.setMargin(true)

    addComponent(tabSheet)
  }


  lazy val categories = new VerticalLayout with Margin {
    val categoryManager = new com.imcode.imcms.admin.doc.category.CategoryManager(app)
    val categoryTypeManager = new com.imcode.imcms.admin.doc.category.`type`.CategoryTypeManager(app)

    val tabSheet = new TabSheet
    tabSheet.addTab(categoryManager.ui, "Categories ", Tab32)
    tabSheet.addTab(categoryTypeManager.ui, "Category types", Tab32)

    categoryManager.ui.setMargin(true)
    categoryTypeManager.ui.setMargin(true)

    addComponent(tabSheet)
  } // category

  lazy val chat =  new VerticalLayout

  lazy val users = new VerticalLayout with Margin {
    val manager = new UserManager(app)
    val tabSheet = new TabSheet
    tabSheet.addTab(manager.ui, "Users and their permissions", Tab32)
    manager.ui.setMargin(true)
    addComponent(tabSheet)
  }


}