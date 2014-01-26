package com.imcode
package imcms
package admin.sysadmin

import com.imcode.imcms.admin.access.user.UserManager
import com.imcode.imcms.vaadin.component.Theme.Icon
import com.vaadin.ui.themes.Reindeer
import scala.collection.JavaConverters._
import com.imcode.imcms.servlet.superadmin.AdminSearchTerms
import java.util.{Locale, Date}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.vaadin.server.{ThemeResource, VaadinRequest}

import _root_.imcode.server.Imcms

import com.vaadin.annotations.PreserveOnRefresh
import com.imcode.imcms.I18nMessage
import com.imcode.imcms.vaadin.Current
import com.imcode.imcms.admin.doc.manager.DocManager

// todo: rename Theme class - name collision
// todo: rename: permissions -> access (members?), IPAccess -> IP Login/Autologin

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

@PreserveOnRefresh
@com.vaadin.annotations.Theme("imcms")
class SysAdmin extends UI {

  private class MenuItem(val id: AnyRef, viewOpt: => Option[Component] = None,
                         val iconOpt: Option[ThemeResource] = None, val children: Seq[MenuItem]) {

    def view = viewOpt.getOrElse(NA(id))
  }

  private object MenuItem {
    def apply(id: AnyRef, children: MenuItem*) = new MenuItem(id, children = children)

    def apply(id: AnyRef, view: => Component, children: MenuItem*) = new MenuItem(id, Some(view), children = children)

    def apply(id: AnyRef, view: => Component, icon: ThemeResource, children: MenuItem*) =
      new MenuItem(id, Some(view), Some(icon), children)
  }


  private val menuRoot =
    MenuItem("mm.admin", admin,
      MenuItem("mm.docs", documents,
        MenuItem("mm.docs.categories", categories, Icon.Done16),
        MenuItem("mm.docs.templates", templates, Icon.Done16),
        MenuItem("mm.docs.languages", languages, Icon.Done16)
      ),

      MenuItem("mm.permissions",
        MenuItem("mm.permissions.users", users , Icon.Done16),
        MenuItem("mm.permissions.roles", roles , Icon.Done16),
        MenuItem("mm.permissions.ip_access", ipAccess, Icon.Done16)
      ),

      MenuItem("mm.system",
        MenuItem("mm.system.settings", systemSettings, Icon.Done16,
          MenuItem("mm.system.monitor"),
          MenuItem("mm.system.monitor.solr"),
          MenuItem("mm.system.monitor.search_terms", searchTerms),
          MenuItem("mm.system.monitor.session", sessionMonitor, Icon.Done16),
          MenuItem("mm.system.monitor.cache"),
          MenuItem("mm.system.monitor.link_validator")
          )
      ),

      MenuItem("mm.files", filesystem, Icon.Done16)
    )


  private lazy val menu = new Tree with SingleSelect[MenuItem] with Immediate with NoNullSelection |>> { tree =>
    def addMenuItem(item: MenuItem, parentItemOpt: Option[MenuItem] = None) {
      tree.addItem(item)
      tree.setItemCaption(item, item.id.toString |> I18nMessage.i)

      item.iconOpt.foreach(icon => tree.setItemIcon(item, icon))
      parentItemOpt.foreach(parentItem => tree.setParent(item, parentItem))

      item.children |> { children =>
        tree.setChildrenAllowed(item, children.nonEmpty)
        children.foreach(childItem => addMenuItem(childItem, Some(item)))
      }
    }

    addMenuItem(menuRoot)

    tree.addValueChangeHandler { _ =>
      content.setSecondComponent(
        tree.valueOpt match {
          case Some(menuItem) => menuItem.view
          case _ => null
        }
      )
    }
  }


  private lazy val content: HorizontalSplitPanel = new HorizontalSplitPanel with FullSize |>> { hsp =>
    hsp.setFirstComponent(menu)
    hsp.setSplitPosition(15)
    hsp.addStyleName(Reindeer.SPLITPANEL_SMALL)
  }


  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    setContent(content)

    menu.expandItemsRecursively(menuRoot)
    menu.select(menuRoot)

    getLoadingIndicatorConfiguration.setFirstDelay(10)
    getLoadingIndicatorConfiguration.setSecondDelay(100)
    getLoadingIndicatorConfiguration.setThirdDelay(1000)

    Current.page.setTitle("imCMS Admin")
  }


  def NA(id: Any) = new TabSheet with MinimalStyle with FullSize |>> { ts =>

    new VerticalLayout with MiddleCenterAlignment with FullSize |>> {
      _.addComponent(new Label("NOT AVAILABLE") with UndefinedSize)
    } |>> {
      ts.addTab(_, id.toString |> I18nMessage.i)
    }
  }


  lazy val admin = new TabSheet with MinimalStyle with FullSize |>> { ts =>
    val manager = new Manager
    ts.addTab(manager.view, "imCMS Admin")
  }


  lazy val instanceCacheView = new com.imcode.imcms.admin.instance.monitor.cache.View(Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy)


  lazy val languages = new TabSheet with MinimalStyle with FullSize |>> { ts =>
    val manager = new com.imcode.imcms.admin.instance.settings.language.LanguageManager
    ts.addTab(manager.view, "doc.lang.mgr.title".i)
  }


  lazy val documents = new TabSheet with MinimalStyle with FullSize {
    val manager = new DocManager
    addTab(manager.view, "doc_mgr.title".i)
  }


  lazy val ipAccess = new TabSheet with FullSize {
    val manager = new com.imcode.imcms.admin.access.ip.IPAccessManager
    addTab(manager.view, "IP Access ")
    addStyleName(Reindeer.TABSHEET_MINIMAL)
  }


  lazy val roles = new TabSheet with MinimalStyle with FullSize {
    val roleManager = new com.imcode.imcms.admin.access.role.RoleManager
    addTab(roleManager.view, "Roles and their permissions")
  }


  lazy val systemSettings = new TabSheet with MinimalStyle with FullSize {
    val manager = new com.imcode.imcms.admin.instance.settings.property.PropertyManager
    addTab(manager.view, "System Properties")
  }


  lazy val sessionMonitor = new TabSheet with MinimalStyle with FullSize {
    val manager = new com.imcode.imcms.admin.instance.monitor.session.counter.SessionCounterManager
    addTab(manager.view, "Counter")
  }


  lazy val searchTerms = new TabSheet with MinimalStyle with FullSize {
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
  }


  lazy val filesystem = new TabSheet with MinimalStyle with FullSize {
    val manager = new com.imcode.imcms.admin.instance.file.FileManager
    addTab(manager.view, "File manager")
  }


  lazy val templates = new TabSheet with MinimalStyle with FullSize {
    val templateManager = new com.imcode.imcms.admin.doc.template.TemplateManager
    val templateGroupManager = new com.imcode.imcms.admin.doc.template.group.TemplateGroupManager

    addTab(templateManager.view, "Templates")
    addTab(templateGroupManager.view, "Template Groups")
  }


  lazy val categories = new TabSheet with MinimalStyle with FullSize {
    val categoryManager = new com.imcode.imcms.admin.doc.category.CategoryManager
    val categoryTypeManager = new com.imcode.imcms.admin.doc.category.CategoryTypeManager

    addTab(categoryManager.view, "Categories ")
    addTab(categoryTypeManager.view, "Category types")

    categoryManager.view.setSizeFull()
    categoryTypeManager.view.setSizeFull()
  }


  lazy val users = new TabSheet with MinimalStyle with FullSize {
    val manager = new UserManager
    addTab(manager.view, "Users and their permissions")
  }
}