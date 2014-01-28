package com.imcode
package imcms
package admin.uber

import com.imcode.imcms.admin.access.ip.IPAccessManager
import com.imcode.imcms.admin.access.role.RoleManager
import com.imcode.imcms.admin.access.user.UserManager
import com.imcode.imcms.admin.doc.category.{CategoryTypeManager, CategoryManager}
import com.imcode.imcms.admin.doc.template.group.TemplateGroupManager
import com.imcode.imcms.admin.doc.template.TemplateManager
import com.imcode.imcms.admin.instance.file.FileManager
import com.imcode.imcms.admin.instance.monitor.session.counter.SessionCounterManager
import com.imcode.imcms.admin.instance.settings.language.LanguageManager
import com.imcode.imcms.admin.instance.settings.property.PropertyManager
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
class UberAdmin extends UI {

  private class MenuItem(val caption: String, val iconOpt: Option[ThemeResource] = None,
                         viewOpt: => Option[Component] = None, val children: Seq[MenuItem]) {

    def view = viewOpt.getOrElse(createNotImplementedStubView(caption))
  }

  private object MenuItem {
    def apply(caption: String, children: MenuItem*) =
      new MenuItem(caption, children = children)

    def apply(caption: String, view: => Component, children: MenuItem*) =
      new MenuItem(caption, viewOpt = Some(view), children = children)

    def apply(caption: String, icon: ThemeResource, view: => Component, children: MenuItem*) =
      new MenuItem(caption, Some(icon), Some(view), children)
  }


  private val menuRoot =
    MenuItem("mm.admin", adminView,
      MenuItem("mm.docs", documentsView,
        MenuItem("mm.docs.categories", Icon.Done16, categoriesView),
        MenuItem("mm.docs.templates", Icon.Done16, templatesView),
        MenuItem("mm.docs.languages", Icon.Done16, languagesView)
      ),

      MenuItem("mm.permissions",
        MenuItem("mm.permissions.users", Icon.Done16, usersView),
        MenuItem("mm.permissions.roles", Icon.Done16, rolesView),
        MenuItem("mm.permissions.ip_access", Icon.Done16, ipAccessView)
      ),

      MenuItem("mm.system",
        MenuItem("mm.system.settings", Icon.Done16, systemSettingsView,
          MenuItem("mm.system.monitor"),
          MenuItem("mm.system.monitor.solr"),
          MenuItem("mm.system.monitor.search_terms", searchTermsView),
          MenuItem("mm.system.monitor.session", Icon.Done16, sessionMonitorView),
          MenuItem("mm.system.monitor.cache", instanceCacheView),
          MenuItem("mm.system.monitor.link_validator")
        )
      ),

      MenuItem("mm.files", Icon.Done16, filesystemView)
    )


  private lazy val menu = new Tree with SingleSelect[MenuItem] with Immediate with NoNullSelection |>> { tree =>
    def addMenuItem(item: MenuItem) {
      tree.addItem(item)
      tree.setItemCaption(item, item.caption.i)

      item.iconOpt.foreach(icon => tree.setItemIcon(item, icon))
      item.children |> { children =>
        tree.setChildrenAllowed(item, children.nonEmpty)
        for (child <- children) {
          addMenuItem(child)
          tree.setParent(child, item)
        }
      }
    }

    addMenuItem(menuRoot)

    tree.addValueChangeHandler { _ =>
      content.setSecondComponent(
        tree.firstSelectedOpt match {
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


  private def createNotImplementedStubView(caption: String) =
    new VerticalLayout with MiddleCenterAlignment with FullSize |>> {
      _.addComponent(new Label("NOT AVAILABLE") with UndefinedSize)
    } |> { view =>
      createTabSheet(caption -> view)
    }

  private def createTabSheet(tabs: (String, Component)*) =
    new TabSheet with MinimalStyle with FullSize |>> { ts =>
      for ((caption, component) <- tabs) ts.addTab(component, caption)
    }

  private lazy val adminView = createTabSheet("imCMS Admin" -> new UberAdminManager().view)

  private lazy val languagesView = createTabSheet("doc.lang.mgr.title".i -> new LanguageManager().view)

  private lazy val documentsView = createTabSheet("doc_mgr.title".i -> new DocManager().view)

  private lazy val ipAccessView = createTabSheet("IP Access" -> new IPAccessManager().view)

  private lazy val rolesView = createTabSheet("Roles and their permissions" -> new RoleManager().view)

  private lazy val systemSettingsView = createTabSheet("System Properties" -> new PropertyManager().view)

  private lazy val sessionMonitorView = createTabSheet("Counter" -> new SessionCounterManager().view)

  private lazy val filesystemView = createTabSheet("File manager" -> new FileManager().view)

  private lazy val usersView = createTabSheet("Users and their permissions" -> new UserManager().view)

  private lazy val templatesView = createTabSheet(
    "Templates" -> new TemplateManager().view,
    "Template Groups" -> new TemplateGroupManager().view
  )

  private lazy val categoriesView = createTabSheet(
    "Categories" -> new CategoryManager().view,
    "Category types" -> new CategoryTypeManager().view
  )

  private lazy val instanceCacheView = createTabSheet(
    "Cache" -> new com.imcode.imcms.admin.instance.monitor.cache.View(Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy)
  )

  private lazy val searchTermsView = createTabSheet("Popular search terms" ->
    new VerticalLayout with Spacing {
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

        calFrom.value = new Date
        //calFrom.setStyle("calendar")
        calFrom.setResolution(DateField.RESOLUTION_DAY)

        calTo.value = new Date
        //calTo.setStyle("calendar")
        calTo.setResolution(DateField.RESOLUTION_DAY)

        addComponents(calFrom, calTo, btnReload)
      }

      addComponents(tblTerms, lytBar)

      def reload() {
        val terms = AdminSearchTerms.getTermCounts(lytBar.calFrom.getValue, lytBar.calTo.getValue)

        tblTerms.removeAllItems()
        terms.asScala.foreach { t =>
          val item = Array[AnyRef](t.getTerm, t.getCount.toString)
          tblTerms.addItem(item, item)
        }
      }

      lytBar.btnReload.addClickHandler { _ => reload() }

      reload()
    }
  )
}