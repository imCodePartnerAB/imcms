package com.imcode
package imcms
package admin

import scala.collection.JavaConverters._
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
import com.imcode.imcms.vaadin.Theme.Icon


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


  object Menu extends TreeMenuItem {
    @OrderedMethod(0) object About extends TreeMenuItem("menu.about", Icon.About16)

    @OrderedMethod(1) object Documents extends TreeMenuItem("menu.documents") {
      @OrderedMethod(0) object Categories extends TreeMenuItem("menu.documents.categories", Icon.Done16)
      @OrderedMethod(1) object Templates extends TreeMenuItem("menu.documents.templates", Icon.Done16)
    }

    @OrderedMethod(2) object Permissions extends TreeMenuItem("menu.permissions") {
      @OrderedMethod(0) object Users extends TreeMenuItem("menu.permissions.users", Icon.Done16)
      @OrderedMethod(1) object Roles extends TreeMenuItem("menu.permissions.roles", Icon.Done16)
      @OrderedMethod(2) object IP_Access extends TreeMenuItem("menu.permissions.ip_access", Icon.Done16)
    }

    @OrderedMethod(3) object System extends TreeMenuItem("menu.system") {
      @OrderedMethod(0) object Settings extends TreeMenuItem("menu.system.settings") {
        @OrderedMethod(0) object Languages extends TreeMenuItem("menu.system.settings.languages", Icon.Done16)
        @OrderedMethod(1) object Properties extends TreeMenuItem("menu.system.settings.properties", Icon.Done16)
      }

      @OrderedMethod(1) object Monitor extends TreeMenuItem("menu.monitor") {
        @OrderedMethod(0) object SearchTerms extends TreeMenuItem("menu.monitor.search_terms")
        @OrderedMethod(1) object Session extends TreeMenuItem("menu.monitor.session", Icon.Done16)
        @OrderedMethod(2) object Cache extends TreeMenuItem("menu.monitor.cache")
        @OrderedMethod(3) object LinkValidator extends TreeMenuItem("menu.monitor.link_validator")
      }
    }

    @OrderedMethod(4) object Files extends TreeMenuItem("menu.files", Icon.Done16)
  }


  val mainWindow = new Window {
    val hspManagers = new HorizontalSplitPanel with FullSize {
      val menu = new Tree with Immediate
      val content = new VerticalLayout with FullSize with Margin

      setFirstComponent(menu)
      setSecondComponent(content)
      setSplitPosition(15)
    }

    val content = new VerticalSplitPanel |>> { p =>
      p.setFirstComponent(hspManagers)
      p.setSecondComponent(chat)
      p.setSplitPosition(85)
    }

    setContent(content)

    def initManagersMenu() {
      def addMenuItem(parentItem: TreeMenuItem, item: TreeMenuItem) {
        hspManagers.menu.addItem(item)
        hspManagers.menu.setParent(item, parentItem)
        hspManagers.menu.setItemCaption(item, item.id |> I18n.i)
        hspManagers.menu.setItemIcon(item, item.icon)

        item.children |> { children =>
          hspManagers.menu.setChildrenAllowed(item, children.nonEmpty)
          children.foreach(childItem => addMenuItem(item, childItem))
        }
      }

      Menu.children.foreach { item =>
        addMenuItem(Menu, item)
        hspManagers.menu.expandItemsRecursively(item)
      }

      hspManagers.menu.addValueChangeListener { e =>
        hspManagers.content.removeAllComponents()
        hspManagers.content.addComponent(
          e.getProperty.getValue |> {
            case null | Menu.About => labelAbout

            case Menu.System.Monitor.SearchTerms => searchTerms
            case Menu.Documents.Categories => categories
            case Menu.System.Settings.Languages => languages
            case Menu.System.Settings.Properties => settingsProperties
            case Menu.System.Monitor.Session => sessionMonitor
            case Menu.Documents => documents
            case Menu.Permissions.Roles => roles
            case Menu.Permissions.Users => users
            case Menu.Permissions.IP_Access => ipAccess
            case Menu.Documents.Templates => templates
            case Menu.System.Monitor.Cache => systemCacheView
            case Menu.Files => filesystem

            case other => NA(other)
          }
        )
      }

      hspManagers.menu.select(Menu.About)
    } // initManagersMenu
  }





  def init() {
    setTheme("imcms")
    setLocale(new Locale(user().getLanguageIso639_2))
    mainWindow.initManagersMenu()
    setMainWindow(mainWindow)
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

  def NA(id: Any) = new Panel(id.toString) {
    setIcon(Icon.Tab32)

    addComponent(new Label("NOT AVAILABLE"))
  }


  val labelAbout = new Panel("About") {
    getContent |> {
      case c: VerticalLayout =>
        c.setMargin(true)
        c.setSpacing(true)
    }

    addComponent(new Label("""
                   |Welcome to the imCMS new admin UI prototype -
                   | please pick a task from the menu. Note that some views are not (yet) available.
                   |""".stripMargin))
  }


  def systemCacheView = new com.imcode.imcms.admin.system.monitor.cache.View(Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy)


  lazy val languages = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.system.settings.language.LanguageManager(app)
    manager.ui.setMargin(true)
    addTab(manager.ui, "Language", Icon.Tab32)
  }


  lazy val documents = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.doc.DocManager(app)
    manager.ui.setMargin(true)
    addTab(manager.ui, "Document", Icon.Tab32)
  }


  lazy val ipAccess = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.access.ip.IPAccessManager(app)
    manager.ui.setMargin(true)
    addTab(manager.ui, "IP Access ", Icon.Tab32)
  }


  lazy val roles = new TabSheet with FullSize {
    val roleManager = new com.imcode.imcms.admin.access.role.RoleManager(app)
    roleManager.ui.setMargin(true)
    addTab(roleManager.ui, "Roles and their permissions", Icon.Tab32)
  }


  lazy val settingsProperties = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.system.settings.property.PropertyManagerManager(app)
    manager.ui.setMargin(true)
    addTab(manager.ui, "System Properties", Icon.Tab32)
  }


  lazy val sessionMonitor = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.system.monitor.session.counter.SessionCounterManager(app)
    manager.ui.setMargin(true)
    addTab(manager.ui, "Counter", Icon.Tab32)
  }


  lazy val searchTerms = new TabSheet with FullSize {
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

        tblTerms.removeAllItems()
        terms.asScala.foreach { t =>
          val item = Array[AnyRef](t.getTerm, t.getCount.toString)
          tblTerms.addItem(item, item)
        }
      }

      lytBar.btnReload.addClickHandler { reload() }

      reload()
    })
  }


  lazy val filesystem = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.system.file.FileManager(app)
    manager.ui.setMargin(true)
    addTab(manager.ui, "File manager", Icon.Tab32)
  }


  lazy val templates = new TabSheet with FullSize {
    val templateManager = new com.imcode.imcms.admin.doc.template.TemplateManager(app)
    val templateGroupManager = new com.imcode.imcms.admin.doc.template.group.TemplateGroupManager(app)

    addTab(templateManager.ui, "Templates", Icon.Tab32)
    addTab(templateGroupManager.ui, "Template Groups", Icon.Tab32)

    templateManager.ui.setMargin(true)
    templateGroupManager.ui.setMargin(true)
  }


  lazy val categories = new TabSheet with FullSize {
    val categoryManager = new com.imcode.imcms.admin.doc.category.CategoryManager(app)
    val categoryTypeManager = new com.imcode.imcms.admin.doc.category.`type`.CategoryTypeManager(app)

    addTab(categoryManager.ui, "Categories ", Icon.Tab32)
    addTab(categoryTypeManager.ui, "Category types", Icon.Tab32)

    categoryManager.ui.setMargin(true)
    categoryTypeManager.ui.setMargin(true)
  }


  lazy val chat =  new VerticalLayout

  lazy val users = new TabSheet with FullSize {
    val manager = new UserManager(app)
    manager.ui.setMargin(true)
    addTab(manager.ui, "Users and their permissions", Icon.Tab32)
  }
}