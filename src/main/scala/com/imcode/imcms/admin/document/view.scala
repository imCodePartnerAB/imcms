package com.imcode
package imcms
package admin.document

import scala.collection.JavaConversions._
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}

import vaadin.{ImcmsApplication, FullSize}
import com.vaadin.ui.Table.CellStyleGenerator
import com.vaadin.ui._
import imcode.server.Imcms
import imcode.server.document.textdocument.TextDocumentDomainObject
import com.vaadin.terminal.{ExternalResource, Resource}
import imcode.server.document.{LifeCyclePhase, DocumentDomainObject}
import com.vaadin.event.Action
import com.vaadin.data.{Property, Item, Container}
import java.lang.Class
import collection.immutable.{SortedSet, ListMap, ListSet}
import api.Document


class DocSearchableView {
  val basicSearch = new DocBasicSearch
  val advancedSearch = new DocAdvancedSearch
  val advancedSearchPanel = new Panel with Scrollable with UndefinedSize with FullHeight {
    setStyleName(Panel.STYLE_LIGHT)
    setContent(advancedSearch.ui)
  }

  val docTableUI = new DocTableUI with FullSize

  val ui = letret(new GridLayout(1, 2) with Spacing with FullSize) { ui =>
    ui.addComponent(basicSearch.ui)
    ui.addComponent(docTableUI)
    ui.setRowExpandRatio(1, 1f)

    basicSearch.ui.btnAdvanced.addClickHandler {
      val component = ui.getComponent(0, 1) match {
        case `docTableUI` => advancedSearchPanel
        case _ => docTableUI
      }

      ui.removeComponent(0, 1)
      ui.addComponent(component, 0, 1)
    }

    basicSearch.ui.lytButtons.btnSearch.addClickHandler {
      // check
      ui.removeComponent(0, 1)
      ui.addComponent(docTableUI, 0, 1)
    }

    basicSearch.ui.chkAdvanced.addValueChangeHandler {
      if (!basicSearch.ui.chkAdvanced.booleanValue) {
        // check
        ui.removeComponent(0, 1)
        ui.addComponent(docTableUI, 0, 1)
      }
    }
  }
}


case class FunctionProperty[A](valueGetter: Function0[A])(implicit mf: Manifest[A]) extends Property {

  def setReadOnly(newStatus: Boolean) = throw new UnsupportedOperationException

  val isReadOnly = true

  val getType = mf.erasure

  def setValue(newValue: AnyRef) = throw new UnsupportedOperationException

  def getValue = valueGetter().asInstanceOf[AnyRef]

  override def toString = ?(getValue) map { _.toString } getOrElse ""
}

/**
 * Doc table container
 */
class DocTableContainer(private var itemIds: SortedSet[DocId] = SortedSet.empty) extends Container {

  private val propertyIdToType = ListMap(
      "doc.tbl.col.id" -> classOf[DocId],
      "doc.tbl.col.type" -> classOf[JInteger],
      "doc.tbl.col.status" -> classOf[String],
      "doc.tbl.col.alias" -> classOf[String],
      "doc.tbl.col.parents" -> classOf[Component],
      "doc.tbl.col.children" -> classOf[Component])

  private val propertyIds = propertyIdToType.keys.toList

  case class DocItem(docId: DocId) extends Item {

    lazy val doc = Imcms.getServices.getDocumentMapper.getDocument(docId)

    def removeItemProperty(id: AnyRef) = throw new UnsupportedOperationException

    def addItemProperty(id: AnyRef, property: Property) = throw new UnsupportedOperationException

    def getItemPropertyIds = propertyIds

    def getItemProperty(id: AnyRef) = FunctionProperty(id match {
      case "doc.tbl.col.id" => doc.getId
      case "doc.tbl.col.type" => doc.getDocumentTypeId
      case "doc.tbl.col.alias" => doc.getAlias
      case "doc.tbl.col.status" =>
        () => doc.getPublicationStatus match {
          case Document.PublicationStatus.NEW => "New"
          case Document.PublicationStatus.APPROVED => "Approved"
          case Document.PublicationStatus.DISAPPROVED => "Disapproved"
        }

      case "doc.tbl.col.parents" =>
        () => Imcms.getServices.getDocumentMapper.getDocumentMenuPairsContainingDocument(doc).toList match {
          case List() => null
          case List(pair) =>
            letret(new Tree with ItemIdType[DocumentDomainObject] with NotSelectable with DocStatusItemIcon) { tree =>
              val parentDoc = pair.getDocument
              tree.addItem(parentDoc)
              tree.setChildrenAllowed(parentDoc, false)
              tree.setItemCaption(parentDoc, "%s - %s" format (parentDoc.getId, parentDoc.getHeadline))
            }

          case pairs => letret(new Tree with ItemIdType[DocumentDomainObject] with NotSelectable with DocStatusItemIcon) { tree =>
            val root = new {}
            tree.addItem(root)
            tree.setItemCaption(root, pairs.size.toString)
            for (pair <- pairs; parentDoc = pair.getDocument) {
              tree.addItem(parentDoc)
              tree.setChildrenAllowed(parentDoc, false)
              tree.setItemCaption(parentDoc, "%s - %s" format (parentDoc.getId, parentDoc.getHeadline))
              tree.setParent(parentDoc, root)
            }
          }
        }

      case "doc.tbl.col.children" =>
        () => doc match {
          case textDoc: TextDocumentDomainObject =>
            Imcms.getServices.getDocumentMapper.getDocuments(textDoc.getChildDocumentIds).toList match {
              case List() => null
              case List(childDoc) =>
                letret(new Tree with ItemIdType[DocumentDomainObject] with DocStatusItemIcon with NotSelectable) { tree =>
                  tree.addItem(childDoc)
                  tree.setChildrenAllowed(childDoc, false)
                  tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
                }

              case childDocs =>letret(new Tree with ItemIdType[DocumentDomainObject] with DocStatusItemIcon with NotSelectable) { tree =>
                val root = new {}
                tree.addItem(root)
                tree.setItemCaption(root, childDocs.size.toString)
                for (childDoc <- childDocs) {
                  tree.addItem(childDoc)
                  tree.setChildrenAllowed(childDoc, false)
                  tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
                  tree.setParent(childDoc, root)
                  // >>> link to list documents
                }
              }
            }

          case _ => null
        }
    })
  }

  def getContainerPropertyIds = propertyIds

  def addItem() = throw new UnsupportedOperationException

  def addItem(itemId: AnyRef) = let(itemId.asInstanceOf[JInteger]) { id =>
    letret(DocItem(id)) { _ => itemIds += id }
  }

  def getType(propertyId: AnyRef) = propertyIdToType(propertyId.asInstanceOf[String])

  def getItem(itemId: AnyRef) = DocItem(itemId.asInstanceOf[JInteger])

  def getContainerProperty(itemId: AnyRef, propertyId: AnyRef) = getItem(itemId).getItemProperty(propertyId)

  def size = itemIds.size

  def removeItem(itemId: AnyRef) = letret(true) { _ => itemIds -= itemId.asInstanceOf[JInteger] }

  def removeAllItems = letret(true) { _ => itemIds = SortedSet.empty }

  def containsId(itemId: AnyRef) = itemIds.contains(itemId.asInstanceOf[JInteger])

  def addContainerProperty(propertyId: AnyRef, `type` : Class[_], defaultValue: AnyRef) = throw new UnsupportedOperationException

  def removeContainerProperty(propertyId: AnyRef) = throw new UnsupportedOperationException

  def getItemIds = itemIds
}


class DocTableUI(container: DocTableContainer = new DocTableContainer) extends Table(null, container)
    with MultiSelectBehavior[DocId] with DocTableItemIcon with Selectable {

  setColumnCollapsingAllowed(true)
  setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY)
  setColumnHeaders(container.getContainerPropertyIds map (_.i) toArray )
  List("doc.tbl.col.parents", "doc.tbl.col.children") foreach { setColumnCollapsed(_, true) }
}

//object DocTableUI {
//  def apply(fullSize: Boolean = false) = let(new DocTableContainer) { container =>
//    new Table(null, container) with DocTableItemIcon with MultiSelect2[DocId] with Selectable { table =>
//      if (fullSize) table.setSizeFull
//      setColumnCollapsingAllowed(true)
//      setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY)
//      setColumnHeaders(container.getContainerPropertyIds map (_.i) toArray )
//      List("doc.tbl.col.parents", "doc.tbl.col.children") foreach { setColumnCollapsed(_, true) }
//    }
//  }
//
//  def apply2(fullSize: Boolean = false) = new Table with DocStatusItemIcon with MultiSelect2[DocumentDomainObject] with Selectable { table =>
//    addContainerProperties(table,
//      CP[JInteger]("doc.tbl.col.id"),
//      CP[JInteger]("doc.tbl.col.type"),
//      CP[String]("doc.tbl.col.status"),
//      CP[String]("doc.tbl.col.alias"))
//
//
//    if (fullSize) table.setSizeFull
//
////    table.setCellStyleGenerator(new CellStyleGenerator {
////      def getStyle(itemId: AnyRef, propertyId: AnyRef) {
////        if (propertyId == null) {
////            // no propertyId, styling row
////            return (markedRows.contains(itemId) ? "marked" : null);
////        } else if (ExampleUtil.iso3166_PROPERTY_NAME.equals(propertyId)) {
////            return "bold";
////        } else {
////            // no style
////            return null;
////        }
////      }
////    })
//
//    // alias VIEW -> 1003
//    // status EDIT META -> http://imcms.dev.imcode.com/servlet/AdminDoc?meta_id=1003&flags=1
//    // admin: VIWE + ADMIN PANEL 1009 - Start page swe(Copy/Kopia) -> http://imcms.dev.imcode.com/servlet/AdminDoc?meta_id=1009
//    // ref -> DocumentReferences! 3 -> http://imcms.dev.imcode.com/servlet/DocumentReferences?returnurl=ListDocuments%3Fstart%3D1001%26end%3D1031%26showspan%3D%2BLista%2B&id=1001
//    // children LIST DOCS -> 1023 - Testdoc-swe -> http://imcms.dev.imcode.com/servlet/ListDocuments?start=1023&end=1023
//
//    // >>> Html.getLinkedStatusIconTemplate( document, user, request )
//
//    val docMapper = Imcms.getServices.getDocumentMapper
//
//
//    trait TreeActionHandler extends Tree {
//      addActionHandler(new Action.Handler {
//        import Actions._
//
//        def getActions(target: AnyRef, sender: AnyRef) = target match {
//          case doc: DocumentDomainObject => Array(AddToSelection, View)
//          case _ => Array.empty[Action]
//        }
//
//        def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
//          action match {
//            case AddToSelection => //docSelection.ui.tblDocs.addItem(target)
//            case _ =>
//          }
//      })
//    }
//
//    table.addGeneratedColumn("doc.tbl.col.parents", new Table.ColumnGenerator {
//      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) =
//        docMapper.getDocumentMenuPairsContainingDocument(itemId.asInstanceOf[DocumentDomainObject]).toList match {
//          case List() => null
//          case List(pair) =>
//            letret(new Tree with TreeActionHandler with ItemIdType[DocumentDomainObject] with DocStatusItemIcon) { tree =>
//              val parentDoc = pair.getDocument
//              tree.addItem(parentDoc)
//              tree.setChildrenAllowed(parentDoc, false)
//              tree.setItemCaption(parentDoc, "%s - %s" format (parentDoc.getId, parentDoc.getHeadline))
//            }
//
//          case pairs => letret(new Tree with TreeActionHandler with ItemIdType[DocumentDomainObject] with DocStatusItemIcon) { tree =>
//            val root = new {}
//            tree.addItem(root)
//            tree.setItemCaption(root, pairs.size.toString)
//            for (pair <- pairs; parentDoc = pair.getDocument) {
//              tree.addItem(parentDoc)
//              tree.setChildrenAllowed(parentDoc, false)
//              tree.setItemCaption(parentDoc, "%s - %s" format (parentDoc.getId, parentDoc.getHeadline))
//              tree.setParent(parentDoc, root)
//            }
//          }
//        }
//    })
//
//    table.addGeneratedColumn("doc.tbl.col.children", new Table.ColumnGenerator {
//      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) =
//        itemId match {
//          case textDoc: TextDocumentDomainObject =>
//            docMapper.getDocuments(textDoc.getChildDocumentIds).toList match {
//              case List() => null
//              case List(childDoc) =>
//                letret(new Tree with TreeActionHandler with ItemIdType[DocumentDomainObject] with DocStatusItemIcon) { tree =>
//                  tree.addItem(childDoc)
//                  tree.setChildrenAllowed(childDoc, false)
//                  tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
//                }
//
//              case childDocs =>letret(new Tree with TreeActionHandler with ItemIdType[DocumentDomainObject] with DocStatusItemIcon) { tree =>
//                val root = new {}
//                tree.addItem(root)
//                tree.setItemCaption(root, childDocs.size.toString)
//                for (childDoc <- childDocs) {
//                  tree.addItem(childDoc)
//                  tree.setChildrenAllowed(childDoc, false)
//                  tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
//                  tree.setParent(childDoc, root)
//                  // >>> link to list documents
//                }
//              }
//            }
//
//          case _ => null
//        }
//    })
//
//    table.setColumnHeaders(Array("doc.tbl.col.id".i, "doc.tbl.col.type".i, "doc.tbl.col.status".i,
//      "doc.tbl.col.alias".i, "doc.tbl.col.parents".i, "doc.tbl.col.children".i))
//
//    table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY)
//  }
//}

trait DocStatusItemIcon extends AbstractSelect {
  override def getItemIcon(itemId: AnyRef) = itemId match {
    case doc: DocumentDomainObject => new ExternalResource("imcms/eng/images/admin/status/%s.gif" format
      itemId.asInstanceOf[DocumentDomainObject].getLifeCyclePhase.toString)

    case _ => null
  }
}

trait DocTableItemIcon extends AbstractSelect with XSelect[DocId] {
  override def getItemIcon(itemId: AnyRef) = item(itemId.asInstanceOf[DocId]) match {
    case docItem: DocTableContainer#DocItem =>
      new ExternalResource("imcms/eng/images/admin/status/%s.gif" format docItem.doc.getLifeCyclePhase.toString)

    case _ => null
  }
}


class DocBasicSearch {
  val ui = letret(new DocBasicSearchUI) { ui =>
    ui.lytButtons.btnClear.addClickHandler { reset() }

//    ui.chkText.addClickHandler { ui.txtText setEnabled ui.chkText.booleanValue }
//
//    ui.chkRange.addClickHandler { ui.lytRange setEnabled ui.chkRange.booleanValue }
//
//    ui.chkType.addClickHandler { ui.lytType setEnabled ui.chkType.booleanValue }
//
//    ui.chkAdvanced.addClickHandler { ui.btnAdvanced setEnabled ui.chkAdvanced.booleanValue }

    ui.chkText.addValueChangeHandler { ui.txtText setEnabled ui.chkText.booleanValue }

    ui.chkRange.addValueChangeHandler { ui.lytRange setEnabled ui.chkRange.booleanValue }

    ui.chkType.addValueChangeHandler { ui.lytType setEnabled ui.chkType.booleanValue }

    ui.chkAdvanced.addValueChangeHandler { ui.btnAdvanced setEnabled ui.chkAdvanced.booleanValue }
  }

  reset()

  def reset() {
    ui.chkRange.value = true
    ui.chkType.value = true
    ui.chkText.value = true
    ui.chkAdvanced.value = true
    forlet(ui.lytType.chkText, ui.lytType.chkFile, ui.lytType.chkHtml) { _.value = true }
    ui.chkAdvanced.value = false

    ui.lytRange.txtFrom.value = ""
    ui.lytRange.txtTo.value = ""
    ui.txtText.value = ""
  }
}

class DocBasicSearchUI extends CustomLayout("admin/doc/search/basic") {
  setWidth("700px")

  // prompt - real numbers: lower, upper
  val chkRange = new CheckBox("doc.search.basic.frm.lbl.range".i) with Immediate
  val lytRange = new HorizontalLayout with Spacing with UndefinedSize {
    val txtFrom = new TextField { setInputPrompt("doc.search.basic.frm.txt.range.from.prompt".i); setColumns(5) }
    val txtTo = new TextField { setInputPrompt("doc.search.basic.frm.txt.range.to.prompt".i); setColumns(5) }

    addComponents(this, txtFrom, txtTo)
  }
  // prompt - any text
  val chkText = new CheckBox("doc.search.basic.frm.lbl.text".i) with Immediate
  val txtText = new TextField { setInputPrompt("doc.search.basic.frm.txt.text.prompt".i) }

  val chkType = new CheckBox("doc.search.basic.frm.chk.type".i) with Immediate
  val lytType = new HorizontalLayout with UndefinedSize with Spacing {
    val chkText = new CheckBox("doc.search.basic.frm.chk.type.text".i)
    val chkFile = new CheckBox("doc.search.basic.frm.chk.type.file".i)
    val chkHtml = new CheckBox("doc.search.basic.frm.chk.type.html".i)

    addComponents(this, chkText, chkFile, chkHtml)
  }

  val chkAdvanced = new CheckBox("doc.search.basic.frm.chk.advanced".i) with Immediate
  val btnAdvanced = new Button("doc.search.basic.frm.btn.advanced".i) with LinkStyle


  val lytButtons = new HorizontalLayout with UndefinedSize with Spacing {
    val btnClear = new Button("doc.search.basic.frm.btn.clear".i) { setStyleName("small") }
    val btnSearch = new Button("doc.search.basic.frm.btn.search".i) { setStyleName("small") }

    addComponents(this, btnClear, btnSearch)
  }

  addNamedComponents(this,
    "doc.search.basic.frm.chk.range" -> chkRange,
    "doc.search.basic.frm.range" -> lytRange,
    "doc.search.basic.frm.chk.text" -> chkText,
    "doc.search.basic.frm.txt.text" -> txtText,
    "doc.search.basic.frm.chk.type" -> chkType,
    "doc.search.basic.frm.type" -> lytType,
    "doc.search.basic.frm.chk.advanced" -> chkAdvanced,
    "doc.search.basic.frm.btn.advanced" -> btnAdvanced,
    "doc.search.basic.frm.buttons" -> lytButtons
  )
}


class DocAdvancedSearch {
  val ui = new DocAdvancedSearchUI
}

class DocAdvancedSearchUI extends CustomLayout("admin/doc/search/advanced") {
  setWidth("700px")

  val lblPredefined = new Label("doc.search.advanced.frm.lbl.predefined".i) with UndefinedSize
  val cbPredefined = new ComboBox

  val chkStatus = new CheckBox("doc.search.advanced.frm.chk.status".i) with Immediate with UndefinedSize
  val lytStatus = new HorizontalLayout with Spacing with UndefinedSize {
    val chkNew = new CheckBox("doc.search.advanced.frm.ckh.status.new".i)
    val chkPublished = new CheckBox("doc.search.advanced.frm.chk.status.published".i)
    val chkExpired = new CheckBox("doc.search.advanced.frm.chk.status.expired".i)

    addComponents(this, chkNew, chkPublished, chkExpired)
  }

  val chkDates = new CheckBox("doc.search.advanced.frm.chk.dates".i)
  val lytDates = new VerticalLayout with UndefinedSize with Spacing

  val chkCategories = new CheckBox("doc.search.advanced.frm.chk.categories".i)
  val tcsCategories = new TwinColSelect

  val chkRelationship = new CheckBox("doc.search.advanced.frm.chk.relationship".i)
  val lytRelationship = new HorizontalLayout with Spacing with UndefinedSize {
    val cbParents = new ComboBox("doc.search.advanced.frm.chk.relationship.parents".i)
    val cbChildren = new ComboBox("doc.search.advanced.frm.chk.relationship.children".i)

    cbParents.addItem("-not selected-")
    cbParents.addItem("With parents")
    cbParents.addItem("Without parents")

    cbChildren.addItem("-not selected-")
    cbChildren.addItem("With children")
    cbChildren.addItem("Without children")

    cbParents.setNullSelectionItemId("-not selected-")
    cbChildren.setNullSelectionItemId("-not selected-")

    addComponents(this, cbParents, cbChildren)
  }

  val chkMaintainers = new CheckBox("doc.search.advanced.frm.chk.maintainers".i)
  val lytMaintainers = new HorizontalLayout with UndefinedSize {
    val lstCreators = new ListSelect("doc.search.advanced.frm.chk.maintainers.creators".i)

    addComponents(this, lstCreators)
  }

  addNamedComponents(this,
    "doc.search.advanced.frm.chk.status" -> chkStatus,
    "doc.search.advanced.frm.status" -> lytStatus,
    "doc.search.advanced.frm.lbl.predefined" -> lblPredefined,
    "doc.search.advanced.frm.cb.predefined" -> cbPredefined,
    "doc.search.advanced.frm.chk.dates" -> chkDates,
    "doc.search.advanced.frm.lyt.dates" -> lytDates,
    "doc.search.advanced.frm.chk.relationship" -> chkRelationship,
    "doc.search.advanced.frm.lyt.relationship" -> lytRelationship,
    "doc.search.advanced.frm.chk.categories" -> chkCategories,
    "doc.search.advanced.frm.tcs.categories" -> tcsCategories,
    "doc.search.advanced.frm.chk.maintainers" -> chkMaintainers,
    "doc.search.advanced.frm.lyt.maintainers" -> lytMaintainers
  )
}
