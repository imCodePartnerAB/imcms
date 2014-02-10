package com.imcode
package imcms
package admin.docadmin.menu

import com.imcode.imcms.vaadin.component.dialog.Dialog
import com.imcode.imcms.vaadin.Current
import com.vaadin.ui._

import _root_.imcode.server.document.textdocument.{MenuItemDomainObject, MenuDomainObject, TextDocumentDomainObject}
import _root_.imcode.server.document.DocumentDomainObject

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.vaadin.Editor
import com.vaadin.ui.AbstractSelect.{VerticalLocationIs, ItemDescriptionGenerator}
import com.imcode.imcms.vaadin.data.PropertyDescriptor
import com.imcode.imcms.admin.doc.{DocEditorDialog, DocOpener, DocSelectDialog}
import scala.annotation.tailrec
import com.vaadin.data.util.HierarchicalContainer
import com.vaadin.event.dd.{DragAndDropEvent, DropHandler}
import com.vaadin.shared.ui.dd.VerticalDropLocation
import com.vaadin.server.Page
import com.vaadin.event.dd.acceptcriteria.{AcceptAll, Not, AcceptCriterion}
import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.data.PropertyDescriptor


// refers to check:
// Deleted legacy "Menu Editing files"
// [+] MenuEditPage
// [-] DocumentCreator +
//   [-] CreateTextDocumentPageFlow
//   ... relatives
// [+] GetExistingDoc - permissions checks
// [+] change_menu.jsp
// [+] ChangeMenu
// [-]inc_adminlinks.jsp
// [-]NoPermissionsToAddToDocumentException
class MenuEditor(doc: TextDocumentDomainObject, menu: MenuDomainObject) extends Editor with ImcmsServicesSupport {

  override type Data = MenuDomainObject

  private var state = menu.clone()

  override val view = new MenuEditorView |>> { w =>
    w.ttMenu.setItemDescriptionGenerator(new ItemDescriptionGenerator {
      def generateDescription(source: Component, itemId: AnyRef, propertyId: AnyRef) = "n/a" // column title tooltip
    })

    w.ttMenu.addValueChangeHandler { _ =>
      Seq(w.miNewDoc, w.miEditSelectedDoc, w.miRemoveSelectedDocs, w.miShowSelectedDoc, w.miCopySelectedDoc).foreach { mi =>
        mi.setEnabled(w.ttMenu.isSelected)
      }
    }

    addContainerProperties(w.ttMenu,
      PropertyDescriptor[DocId]("docs_projection.container_property.meta_id".i),
      PropertyDescriptor[String]("docs_projection.container_property.headline".i),
      PropertyDescriptor[String]("docs_projection.container_property.alias".i),
      PropertyDescriptor[String]("docs_projection.container_property.type".i),
      PropertyDescriptor[String]("docs_projection.container_property.status".i)
    )

    // todo: ??? search for current language + default version ???
    w.miAddExistingDocs.setCommandHandler { _ =>
      new DocSelectDialog("menu_editor_dlg.select_docs.title".i, Current.imcmsUser) |>> { dlg =>
        dlg.setOkButtonHandler {
          for {
            ref <- dlg.projection.selection
            docId = ref.getDocId()
            if !state.getItemsMap.containsKey(docId)
            doc <- imcmsServices.getDocumentMapper.getDefaultDocument[DocumentDomainObject](docId).asOption
          } {
            val docIdentity = imcmsServices.getDocumentMapper.getDocumentReference(doc)
            val menuItem = new MenuItemDomainObject(docIdentity)
            state.addMenuItemUnchecked(menuItem)
          }

          updateMenuView()
          dlg.close()
        }
      } |> Current.ui.addWindow
    }

    w.miRemoveSelectedDocs.setCommandHandler { _ =>
      for (docId <- w.ttMenu.firstSelectedOpt) {
        state.removeMenuItemByDocumentId(docId)
        updateMenuView()
      }
    }

    w.miEditSelectedDoc.setCommandHandler { _ =>
      for (docId <- w.ttMenu.firstSelectedOpt) {
        imcmsServices.getDocumentMapper.getDocument[DocumentDomainObject](docId) match {
          case null =>
            Current.page.showWarningNotification("notification.doc.unable_to_find".i)
            state.removeMenuItemByDocumentId(docId)
            updateMenuView()

          case selectedDoc =>
            val dialog = new DocEditorDialog("doc.edit_properties.title".f(docId), selectedDoc)
            Dialog.bind(dialog) { case (modifiedDoc, i18nMetas) =>
              imcmsServices.getDocumentMapper.saveDocument(modifiedDoc, i18nMetas.values.to[Set].asJava, Current.imcmsUser)
              updateMenuView()
            }

            dialog.show()
        }
      }
    }

    w.miCopySelectedDoc.setCommandHandler { _ =>
      for (docId <- w.ttMenu.firstSelectedOpt) {
        imcmsServices.getDocumentMapper.getDocument[DocumentDomainObject](docId) match {
          case null =>
            Current.page.showWarningNotification("notification.doc.unable_to_find".i)
            state.removeMenuItemByDocumentId(docId)
            updateMenuView()

          case doc =>
            val newDoc: DocumentDomainObject = imcmsServices.getDocumentMapper.copyDocument(doc, Current.imcmsUser)
            val newDocRef = imcmsServices.getDocumentMapper.getDocumentReference(newDoc)
            val newMenuItem = new MenuItemDomainObject(newDocRef)
            state.addMenuItemUnchecked(newMenuItem)
            updateMenuView()
        }
      }
    }

    w.miShowSelectedDoc.setCommandHandler { _ =>
      for (docId <- w.ttMenu.firstSelectedOpt) {
        DocOpener.openDoc(docId)
      }
    }

    w.cbSortOrder.addValueChangeHandler { _ =>
      state.setSortOrder(w.cbSortOrder.firstSelected)
      updateMenuView()
    }
  }

  resetValues()

  private object MenuDropHandlers {
    private val container = view.ttMenu.getContainerDataSource.asInstanceOf[HierarchicalContainer]

    private abstract class AbstractDropHandler extends DropHandler {
      def drop(event: DragAndDropEvent) {
        val transferable = event.getTransferable.asInstanceOf[Table#TableTransferable]
        val target = event.getTargetDetails.asInstanceOf[AbstractSelect#AbstractSelectTargetDetails]

        val targetItemId = target.getItemIdOver
        val sourceItemId = transferable.getItemId

        target.getDropLocation match {
          case VerticalDropLocation.TOP =>
            val parentId = container.getParent(targetItemId)
            container.setParent(sourceItemId, parentId)
            container.moveAfterSibling(sourceItemId, targetItemId)
            container.moveAfterSibling(targetItemId, sourceItemId)

          case VerticalDropLocation.BOTTOM =>
            val parentId = container.getParent(targetItemId)
            container.setParent(sourceItemId, parentId)
            container.moveAfterSibling(sourceItemId, targetItemId)

          case VerticalDropLocation.MIDDLE =>
            container.setParent(sourceItemId, targetItemId)
        }

        updateItemsSortIndex()
      }

      protected def updateItemsSortIndex()
    }

    val singleLevel: DropHandler = new AbstractDropHandler {
      val getAcceptCriterion: AcceptCriterion = new Not(VerticalLocationIs.MIDDLE)

      protected def updateItemsSortIndex() {
        val menuItems = state.getItemsMap

        for {
          nodes <- container.rootItemIds().asOption
          (docId, index) <- nodes.asInstanceOf[JCollection[DocId]].asScala.zipWithIndex
        } {
          menuItems.get(docId) |> { _.setSortKey(index + 1) }
        }
      }
    }

    val multilevel: DropHandler = new AbstractDropHandler {
      val getAcceptCriterion: AcceptCriterion = AcceptAll.get()

      protected def updateItemsSortIndex() {
        def updateItemsTreeSortIndex(parentSortIndex: Option[String], nodes: JCollection[_]) {
          if (nodes != null) {
            for ((docId, index) <- nodes.asInstanceOf[JCollection[DocId]].asScala.zipWithIndex) {
              state.getItemsMap.get(docId) |> { menuItem =>
                val sortIndex = parentSortIndex.map(_ + ".").mkString + (index + 1)

                menuItem.setTreeSortIndex(sortIndex)
                updateItemsTreeSortIndex(Some(sortIndex), container.getChildren(docId))
              }
            }
          }
        }

        updateItemsTreeSortIndex(None, container.rootItemIds())
      }
    }
  }


  private def updateMenuView() {
    val sortOrder = state.getSortOrder
    val isMultilevel = sortOrder == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER
    val isManualSort = Set(
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER,
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED
    ).contains(sortOrder)


    view.ttMenu.removeAllItems()
    view.ttMenu.setDragMode(if (isManualSort) Table.TableDragMode.ROW else Table.TableDragMode.NONE)
    view.ttMenu.setDropHandler(sortOrder match {
      case MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER => MenuDropHandlers.multilevel
      case MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED => MenuDropHandlers.singleLevel
      case _ => null
    })


    val menuItems = state.getMenuItems
    for (menuItem <- menuItems) {
      val doc = menuItem.getDocument
      val docId = doc.getId
      // doc.getDocumentType.getName.toLocalizedString(ui.getApplication.imcmsUser)
      view.ttMenu.addRow(docId, docId: JInteger, doc.getHeadline, doc.getAlias, doc.getDocumentType.getName.toLocalizedString("eng"), doc.getLifeCyclePhase.toString)
      view.ttMenu.setChildrenAllowed(docId, isMultilevel)
      view.ttMenu.setCollapsed(docId, false)
    }

    if (isMultilevel) {
      @tailrec
      def findParentMenuItem(menuIndex: String): Option[MenuItemDomainObject] = {
        menuIndex.lastIndexOf('.') match {
          case -1 => None
          case  n =>
            val parentMenuIndex = menuIndex.substring(0, n)
            val parentMenuItemOpt = menuItems.find(_.getTreeSortIndex == parentMenuIndex)

            if (parentMenuItemOpt.isDefined) parentMenuItemOpt else findParentMenuItem(parentMenuIndex)
        }
      }

      for {
        menuItem <- menuItems
        parentMenuItem <- findParentMenuItem(menuItem.getTreeSortIndex)
      } {
        view.ttMenu.setParent(menuItem.getDocumentId, parentMenuItem.getDocumentId)
      }
    }
  }

  override def resetValues() {
    state = menu.clone()
    view.cbSortOrder.selection = state.getSortOrder
    view.ttMenu.clearSelection()
  }

  override def collectValues(): ErrorsOrData = Right(state.clone())
}
