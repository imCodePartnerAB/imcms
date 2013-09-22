package com.imcode
package imcms
package admin.sysadmin

import scala.collection.JavaConverters._
import com.imcode._
import com.imcode.imcms.servlet.superadmin.AdminSearchTerms
import java.util.{Locale, Date}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.vaadin.server.VaadinRequest
import imcms.admin.access.user.UserManager
import imcode.server.Imcms

import Theme.Icon
import com.vaadin.annotations.PreserveOnRefresh
import com.vaadin.data.Property.ValueChangeEvent
import com.imcode.imcms.I18nMessage
import com.imcode.imcms.vaadin.{MenuItemOrder, TreeMenuItem}

// todo: rename Theme class - name collision
@PreserveOnRefresh
@com.vaadin.annotations.Theme("imcms")
class SysAdmin extends com.vaadin.ui.UI { app =>

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
    @MenuItemOrder(0) object About extends TreeMenuItem("mm.about", Icon.About16)

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


  val pnlUIContent = new Panel with FullSize {
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

    setContent(hspManagers)

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
        hspManagers.content.removeAllComponents()
        hspManagers.content.addComponent(
          e.getProperty.getValue |> {
            case null | Menu.About => labelAbout

            case Menu.System.Monitor.Solr => searchTerms
            case Menu.Documents.Categories => categories
            case Menu.Documents.Languages => languages
            case Menu.System.Settings => systemSesttings
            case Menu.System.Monitor.Session => sessionMonitor
            case Menu.Documents => documents
            case Menu.Permissions.Roles => roles
            case Menu.Permissions.Users => users
            case Menu.Permissions.IP_Access => ipAccess
            case Menu.Documents.Templates => templates
            case Menu.System.Monitor.Cache => instanceCacheView
            case Menu.Files => filesystem

            case other => NA(other)
          }
        )
      }

      hspManagers.menu.select(Menu.About)
    } // initManagersMenu
  }





  override def init(request: VaadinRequest) {
    setLocale(new Locale(UI.getCurrent.imcmsUser.getLanguageIso639_2))
    pnlUIContent.initManagersMenu()
    setContent(pnlUIContent)
  }


  def NA(id: Any) = new Panel(id.toString) {
    setIcon(Icon.Tab32)

    setContent(new Label("NOT AVAILABLE"))
  }


  val labelAbout = new Panel("About") {
    private val lyt = new VerticalLayout with Spacing with Margin

    setContent(lyt)

    lyt.addComponent(new Label("""
                   |Welcome to the imCMS new admin UI prototype -
                   | please pick a task from the menu. Note that some views are not (yet) available.
                   |""".stripMargin))
  }


  def instanceCacheView = new com.imcode.imcms.admin.instance.monitor.cache.View(Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy)


  lazy val languages = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.instance.settings.language.LanguageManager(app)
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


  lazy val systemSesttings = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.instance.settings.property.PropertyManagerManager(app)
    manager.ui.setMargin(true)
    addTab(manager.ui, "System Properties", Icon.Tab32)
  }


  lazy val sessionMonitor = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.instance.monitor.session.counter.SessionCounterManager(app)
    manager.ui.setMargin(true)
    addTab(manager.ui, "Counter", Icon.Tab32)
  }


  lazy val searchTerms = new TabSheet with FullSize {
    addTab(new VerticalLayout with Spacing with Margin {
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

        this.addComponents(calFrom, calTo, btnReload)
      }

      this.addComponents(tblTerms, lytBar)

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
  }


  lazy val filesystem = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.instance.file.FileManager(app)
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