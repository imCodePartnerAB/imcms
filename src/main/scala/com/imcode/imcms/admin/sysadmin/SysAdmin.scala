package com.imcode
package imcms
package admin.sysadmin

import com.imcode.imcms.admin.access.user.UserManager
import com.imcode.imcms.vaadin.component.dialog.OkCancelDialog
import com.imcode.imcms.vaadin.component.Theme.Icon
import com.vaadin.ui.themes.Reindeer
import scala.collection.JavaConverters._
import com.imcode._
import com.imcode.imcms.servlet.superadmin.AdminSearchTerms
import java.util.{Locale, Date}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.vaadin.server.{ExternalResource, VaadinRequest}

import _root_.imcode.server.Imcms

import com.vaadin.annotations.PreserveOnRefresh
import com.vaadin.data.Property.ValueChangeEvent
import com.imcode.imcms.I18nMessage
import com.imcode.imcms.vaadin.{Current, MenuItemOrder, TreeMenuItem}
import com.imcode.imcms.admin.doc.manager.DocManager

// todo: rename Theme class - name collision
// todo: enable chat ???
// todo: rename: permissions -> access (members?), IPAccess -> IP Login/Autologin
@PreserveOnRefresh
@com.vaadin.annotations.Theme("imcms")
class SysAdmin extends UI {

  // superadmin access:
  // ------------------
  // categories
  // counter
  // delete doc
  // ipaccess
  // profiles ???
  // roles
  // search terms
  // ~~ sections
  // system info
  // users: !user.isSuperAdmin() && !user.isUserAdminAndCanEditAtLeastOneRole()
  // file
  // link check ???
  // list docs ???
  // templates


  object Menu extends TreeMenuItem {
    @MenuItemOrder(0) object Admin extends TreeMenuItem("mm.admin") {
      @MenuItemOrder(1) object Documents extends TreeMenuItem("mm.docs") {
        @MenuItemOrder(0) object Categories extends TreeMenuItem("mm.docs.categories", Icon.Done16)
        @MenuItemOrder(1) object Templates extends TreeMenuItem("mm.docs.templates", Icon.Done16)
        @MenuItemOrder(2) object Languages extends TreeMenuItem("mm.docs.languages", Icon.Done16)
      }

      @MenuItemOrder(2) object Permissions extends TreeMenuItem("mm.permissions") {
        @MenuItemOrder(0) object Users extends TreeMenuItem("mm.permissions.users", Icon.Done16)
        @MenuItemOrder(1) object Roles extends TreeMenuItem("mm.permissions.roles", Icon.Done16)
        @MenuItemOrder(2) object IP_Access extends TreeMenuItem("mm.permissions.ip_access", Icon.Done16)
      }

      @MenuItemOrder(3) object System extends TreeMenuItem("mm.system") {
        @MenuItemOrder(0) object Settings extends TreeMenuItem("mm.system.settings", Icon.Done16)
        @MenuItemOrder(1) object Monitor extends TreeMenuItem("mm.system.monitor") {
          @MenuItemOrder(0) object Solr extends TreeMenuItem("mm.system.monitor.solr")
          @MenuItemOrder(1) object SearchTerms extends TreeMenuItem("mm.system.monitor.search_terms")
          @MenuItemOrder(2) object Session extends TreeMenuItem("mm.system.monitor.session", Icon.Done16)
          @MenuItemOrder(3) object Cache extends TreeMenuItem("mm.system.monitor.cache")
          @MenuItemOrder(4) object LinkValidator extends TreeMenuItem("mm.system.monitor.link_validator")
        }
      }

      @MenuItemOrder(4) object Files extends TreeMenuItem("mm.files", Icon.Done16)
    }
  }


  val lytContent = new VerticalLayout with FullSize {

    val hspManagers = new HorizontalSplitPanel with FullSize {
      val menu = new Tree with Immediate
      val lytManager = new VerticalLayout with FullSize |>> { lyt =>
        lyt.addStyleName("manager")
      }

      val lytMenu = new VerticalLayout with FullSize |>> { lyt =>
        lyt.addStyleName("sysadmin-menu")
      }

      lytMenu.addComponent(menu)

      setFirstComponent(lytMenu)
      setSecondComponent(lytManager)
      setSplitPosition(15)
      addStyleName(Reindeer.SPLITPANEL_SMALL)
    }

    addComponent(hspManagers)

    def initManagersMenu() {
      def addMenuItem(parentItem: TreeMenuItem, item: TreeMenuItem) {
        hspManagers.menu.addItem(item)
        hspManagers.menu.setParent(item, parentItem)
        hspManagers.menu.setItemCaption(item, item.id |> I18nMessage.i)
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

      hspManagers.menu.addValueChangeHandler { e: ValueChangeEvent =>
        hspManagers.lytManager.removeAllComponents()
        hspManagers.lytManager.addComponent(
          e.getProperty.getValue |> {
            case null | Menu.Admin => admin

            case Menu.Admin.System.Monitor.Solr => searchTerms
            case Menu.Admin.Documents.Categories => categories
            case Menu.Admin.Documents.Languages => languages
            case Menu.Admin.System.Settings => systemSettings
            case Menu.Admin.System.Monitor.Session => sessionMonitor
            case Menu.Admin.Documents => documents
            case Menu.Admin.Permissions.Roles => roles
            case Menu.Admin.Permissions.Users => users
            case Menu.Admin.Permissions.IP_Access => ipAccess
            case Menu.Admin.Documents.Templates => templates
            case Menu.Admin.System.Monitor.Cache => instanceCacheView
            case Menu.Admin.Files => filesystem

            case other => NA(other)
          }
        )
      }

      hspManagers.menu.select(Menu.Admin)
    } // initManagersMenu

    //addStyleName(Reindeer.LAYOUT_WHITE)
  }





  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    lytContent.initManagersMenu()
    setContent(lytContent)

    getLoadingIndicatorConfiguration.setFirstDelay(10)
  }


  def NA(id: Any) = new TabSheet with FullSize |>> { ts =>
    val lblNA = new Label("NOT AVAILABLE") |>> { lbl =>
      lbl.setCaption(id.toString)      
      lbl.addStyleName(Reindeer.LABEL_SMALL)
    }

    ts.addTab(lblNA)
    ts.setStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  val admin = new TabSheet with FullSize |>> { ts =>
    val manager = new Manager
    ts.addTab(manager.view, "imCMS Admin")
    ts.setStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  def instanceCacheView = new com.imcode.imcms.admin.instance.monitor.cache.View(Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy)


  lazy val languages = new TabSheet with FullSize |>> { ts =>
    val manager = new com.imcode.imcms.admin.instance.settings.language.LanguageManager
    ts.addTab(manager.view, "doc.lang.mgr.title".i)
    ts.setStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val documents = new TabSheet with FullSize {
    val manager = new DocManager
    addTab(manager.view, "doc_mgr.title".i)
    setStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val ipAccess = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.access.ip.IPAccessManager
    addTab(manager.view, "IP Access ")
    setStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val roles = new TabSheet with FullSize {
    val roleManager = new com.imcode.imcms.admin.access.role.RoleManager
    addTab(roleManager.view, "Roles and their permissions")
    setStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val systemSettings = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.instance.settings.property.PropertyManager
    addTab(manager.view, "System Properties")
    setStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val sessionMonitor = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.instance.monitor.session.counter.SessionCounterManager
    addTab(manager.view, "Counter")
    setStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val searchTerms = new TabSheet with FullSize {
    addTab(new VerticalLayout with Spacing {
      setCaption("Popular search terms")

      val tblTerms = new Table {
        addContainerProperties(this, PropertyDescriptor[String]("Term"), PropertyDescriptor[String]("Count"))
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

        addComponents(calFrom, calTo, btnReload)
      }

      addComponents(tblTerms, lytBar)

      def reload() {
        val terms = AdminSearchTerms.getTermCounts(lytBar.calFrom.getValue,
          lytBar.calTo.getValue)

        tblTerms.removeAllItems()
        terms.asScala.foreach { t =>
          val item = Array[AnyRef](t.getTerm, t.getCount.toString)
          tblTerms.addItem(item, item)
        }
      }

      lytBar.btnReload.addClickHandler { _ => reload() }

      reload()
    })
    setStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val filesystem = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.instance.file.FileManager
    addTab(manager.view, "File manager")
    setStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val templates = new TabSheet with FullSize {
    val templateManager = new com.imcode.imcms.admin.doc.template.TemplateManager
    val templateGroupManager = new com.imcode.imcms.admin.doc.template.group.TemplateGroupManager

    addTab(templateManager.view, "Templates")
    addTab(templateGroupManager.view, "Template Groups")

    addStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val categories = new TabSheet with FullSize {
    val categoryManager = new com.imcode.imcms.admin.doc.category.CategoryManager
    val categoryTypeManager = new com.imcode.imcms.admin.doc.category.CategoryTypeManager

    addTab(categoryManager.view, "Categories ")
    addTab(categoryTypeManager.view, "Category types")

    categoryManager.view.setSizeFull()
    categoryTypeManager.view.setSizeFull()

    addStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val chat =  new VerticalLayout

  lazy val users = new TabSheet with FullSize {
    val manager = new UserManager
    addTab(manager.view, "Users and their permissions")
    addStyleName(Reindeer.TABSHEET_MINIMAL)
  }
}