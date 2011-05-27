package com.imcode
package imcms
package admin.doc

import scala.collection.JavaConversions._
import com.imcode.imcms.vaadin._

import vaadin.{FullSize}
import imcode.server.document.textdocument.TextDocumentDomainObject
import com.vaadin.terminal.{ExternalResource, Resource}
import com.vaadin.event.Action
import com.vaadin.data.{Property, Item, Container}
import java.lang.Class
import collection.immutable.{SortedSet, ListMap}
import imcode.server.document.{DocumentTypeDomainObject, DocumentDomainObject}
import PartialFunction.condOpt
import admin.access.user.UserSearchDialog
import java.util.{Calendar, Date}
import api.{LuceneParsedQuery, Document}
import imcode.server.user.UserDomainObject
import imcode.server.document.index.SimpleDocumentQuery
import com.vaadin.ui.ComponentContainer.{ComponentAttachEvent, ComponentAttachListener}
import web.admin.DateRange
import com.vaadin.ui._
import javax.management.remote.rmi._RMIConnection_Stub

//    // alias VIEW -> 1003
//    // status EDIT META -> http://imcms.dev.imcode.com/servlet/AdminDoc?meta_id=1003&flags=1
//    // admin: VIWE + ADMIN PANEL 1009 - Start page swe(Copy/Kopia) -> http://imcms.dev.imcode.com/servlet/AdminDoc?meta_id=1009
//    // ref -> DocumentReferences! 3 -> http://imcms.dev.imcode.com/servlet/DocumentReferences?returnurl=ListDocuments%3Fstart%3D1001%26end%3D1031%26showspan%3D%2BLista%2B&id=1001
//    // children LIST DOCS -> 1023 - Testdoc-swe -> http://imcms.dev.imcode.com/servlet/ListDocuments?start=1023&end=1023
// >>> Html.getLinkedStatusIconTemplate( document, user, request )

/**
 * Doc search consists of two forms (basic and advanced)
 * and a table that displays search result.
 */
class DocSearch(val docsContainer: DocsContainer) {
  val basicSearchForm = new DocBasicSearchForm
  val advancedSearchForm = new DocAdvancedSearchForm
  val docsUI = new DocsUI(docsContainer) with FullSize

  val ui = letret(new DocSearchUI(basicSearchForm.ui, advancedSearchForm.ui, docsUI)) { ui =>
    val basicFormUI = basicSearchForm.ui

    basicFormUI.lytAdvanced.btnCustomize.addClickHandler { ui.toggleAdvancedSearchForm() }
    basicFormUI.chkAdvanced.addValueChangeHandler {
      if (!basicFormUI.chkAdvanced.booleanValue) ui.advancedSearchFormVisible = false
    }

    basicFormUI.lytButtons.btnSearch.addClickHandler { submit() }
    basicFormUI.lytButtons.btnReset.addClickHandler { reset() }
  }


  def reset() {
    basicSearchForm.reset()
    advancedSearchForm.reset()
    update()
    submit()
  }

  def update() {
    basicSearchForm.setRangeInputPrompt(docsContainer.range)
  }

  def submit() {
    createQuery() match {
      case Left(throwable) =>
        ui.getApplication.show(new ErrorDialog(throwable.getMessage.i))

      case Right(solrQueryOpt) =>
        println(solrQueryOpt)

        ui.removeComponent(0, 1)
        ui.addComponent(docsUI, 0, 1)

        docsContainer.search(solrQueryOpt, ui.getApplication.user)
    }
  }


  /**
   * Creates and returns query string.
   *
   * @return query string.
   */
  def createQuery(): Throwable Either Option[String] = EX.allCatch.either {
    val basicFormUI = basicSearchForm.ui
    val advancedFormUI = advancedSearchForm.ui

    val rangeOpt =
      if (basicFormUI.chkRange.isUnchecked) None
      else {
        val start = condOpt(basicFormUI.lytRange.txtStart.trim) {
          case value if value.nonEmpty => value match {
            case IntNumber(start) => start
            case _ => error("doc.search.dlg_param_validation_err.msg.illegal_range_value")
          }
        }

        val end = condOpt(basicFormUI.lytRange.txtStart.trim) {
          case value if value.nonEmpty => value match {
            case IntNumber(end) => end
            case _ => error("doc.search.dlg_param_validation_err.msg.illegal_range_value")
          }
        }

        whenOpt(start.isDefined || end.isDefined) {
          DocSearchRange(start, end)
        }
      }


    val textOpt: Option[String] =
      if (basicFormUI.chkText.isUnchecked) None
      else condOpt(basicFormUI.txtText.trim) {
        case value if value.nonEmpty => value
      }


    val typesOpt: Option[List[String]] = whenOpt(basicFormUI.chkType.isChecked) {
      import basicFormUI.lytType._

      Map(chkFile -> "file",
          chkText -> "text",
          chkHtml -> "html"
      ).filterKeys(_.isChecked).values.toList match {
        case Nil => error("doc.search.dlg_param_validation_err.msg.no_type_selected")
        case values => values
      }
    }

    val statusesOpt: Option[List[String]] = whenOpt(advancedFormUI.chkStatus.isChecked) {
      import advancedFormUI.lytStatus._

      Map(chkNew -> "new",
          chkPublished -> "published",
          chkUnpublished -> "unpublished",
          chkApproved -> "approved",
          chkDisapproved -> "disapproved",
          chkExpired -> "expired"
      ).filterKeys(_.isChecked).values.toList match {
        case Nil => error("doc.search.dlg_param_validation_err.msg.no_status_selected")
        case values => values
      }
    }

    val datesOpt: Option[Map[String, DateSearchRange]] =
      if (advancedFormUI.chkDates.isUnchecked) None
      else {
        import advancedFormUI.lytDates._

        val datesMap =
          for {
            (name, dr) <- Map("created" -> drCreated, "modified" -> drModified, "published" -> drPublished, "expired" -> drExpired)
            if dr.cbRangeType.value != DocRangeType.Undefined
            start = ?(dr.dtFrom.value)
            end = ?(dr.dtTo.value)
            if start.isDefined || end.isDefined

            // todo: check start/end value
          } yield
            name -> DateSearchRange(start, end)

        if (datesMap.isEmpty) None else Some(datesMap.toMap)
      }

//    // Not yet defined how to make such query
//    val relationshipsOpt =
//      if (advancedFormUI.chkRelationships.isUnchecked) None
//      else {
//        val parentsOpt = advancedFormUI.lytRelationships.cbParents.value
//        val chidrenOpt = advancedFormUI.lytRelationships.cbChildren.value
//      }

    val categoriesOpt: Option[List[String]] = whenOpt(advancedFormUI.chkCategories.isChecked) {
      advancedFormUI.tcsCategories.getItemIds.asInstanceOf[JCollection[String]].toList match {
        case Nil => error("doc.search.dlg_param_validation_err.msg.no_category_selected")
        case values => values
      }
    }

    val creatorsOpt: Option[List[String]] = None
    val publishersOpt: Option[List[String]] = None

    List(
      rangeOpt.map(range => "range:[%s TO %s]".format(range.start.getOrElse("*"), range.end.getOrElse("*"))),
      textOpt.map("text:" + _),
      typesOpt.map(_.mkString("type:(", " OR ", ")")),
      statusesOpt.map(_.mkString("status:(", " OR ", ")"))
    ).flatten match {
      case Nil => None
      case terms => Some(terms.mkString(" "))
    }
  } // def createQuery()
}


class DocSearchUI(
    basicSearchFormUI: DocBasicFormSearchUI,
    advancedSearchFormUI: DocAdvancedSearchFormUI,
    docsUI: DocsUI) extends GridLayout(1, 2) with Spacing with FullSize {

  private val pnlAdvancedSearchForm = new Panel with Scrollable with UndefinedSize with FullHeight {
    setStyleName(Panel.STYLE_LIGHT)
    setContent(advancedSearchFormUI)
  }

  addComponent(basicSearchFormUI)
  addComponent(docsUI)
  setRowExpandRatio(1, 1f)

  def toggleAdvancedSearchForm() =
    advancedSearchFormVisible = !advancedSearchFormVisible

  def advancedSearchFormVisible =
    getComponent(0, 1) == pnlAdvancedSearchForm

  def advancedSearchFormVisible_=(visible: Boolean) {
    removeComponent(0, 1)
    addComponent(if (visible) pnlAdvancedSearchForm else docsUI, 0, 1)
  }
}




/**
 * Docs container.
 */
abstract class DocsContainer extends Container
    with ContainerItemSetChangeNotifier
    with Container.Ordered
    with ItemIdType[DocId]
    with ImcmsServicesSupport {

  private val propertyIdToType = ListMap(
      "doc.tbl.col.id" -> classOf[DocId],
      "doc.tbl.col.type" -> classOf[JInteger],
      "doc.tbl.col.status" -> classOf[String],
      "doc.tbl.col.alias" -> classOf[String],
      "doc.tbl.col.parents" -> classOf[Component],
      "doc.tbl.col.children" -> classOf[Component])

  private val propertyIds = propertyIdToType.keys.toList

  case class DocItem(docId: DocId) extends Item {

    lazy val doc = imcmsServices.getDocumentMapper.getDocument(docId)

    def removeItemProperty(id: AnyRef) = throw new UnsupportedOperationException

    def addItemProperty(id: AnyRef, property: Property) = throw new UnsupportedOperationException

    def getItemPropertyIds = propertyIds

    def getItemProperty(id: AnyRef) = FunctionProperty(id match {
      case "doc.tbl.col.id" => doc.getId
      case "doc.tbl.col.type" => doc.getDocumentTypeId
      case "doc.tbl.col.alias" => doc.getAlias
      case "doc.tbl.col.status" =>
        () => doc.getPublicationStatus match {
          case Document.PublicationStatus.NEW => "doc.publication_status.new".i
          case Document.PublicationStatus.APPROVED => "doc.publication_status.approved".i
          case Document.PublicationStatus.DISAPPROVED => "doc.publication_status.disapproved".i
        }

      case "doc.tbl.col.parents" =>
        () => imcmsServices.getDocumentMapper.getDocumentMenuPairsContainingDocument(doc).toList match {
          case Nil => null
          case pair :: Nil =>
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
            imcmsServices.getDocumentMapper.getDocuments(textDoc.getChildDocumentIds).toList match {
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

  /**
   * Search docs in this container using SOLr query.
   *
   * @param Some(query) to restrict accessible docs set in this container or None to access all docs.
   */
  def search(solrQuery: Option[String], user: UserDomainObject) {
    innerSearch(solrQuery, user)
    notifyItemSetChanged()
  }

  protected def innerSearch(solrQuery: Option[String], user: UserDomainObject): Unit

  /**
   * Returns full (non filtered) inclusive docs range of this container.
   *
   * @return Some(range) or None if there is no docs in this container.
   */
  def range: Option[(DocId, DocId)]

  def getContainerPropertyIds = propertyIds

  def addItem() = throw new UnsupportedOperationException

  def getType(propertyId: AnyRef) = propertyIdToType(propertyId.asInstanceOf[String])

  def getItem(itemId: AnyRef) = DocItem(itemId.asInstanceOf[DocId])

  def getContainerProperty(itemId: AnyRef, propertyId: AnyRef) = getItem(itemId).getItemProperty(propertyId)

  def containsId(itemId: AnyRef) = getItemIds.contains(itemId)

  def addContainerProperty(propertyId: AnyRef, `type` : Class[_], defaultValue: AnyRef) = throw new UnsupportedOperationException

  def removeContainerProperty(propertyId: AnyRef) = throw new UnsupportedOperationException

  def size = getItemIds.size

  def addItemAfter(previousItemId: AnyRef, newItemId: AnyRef) = null

  def addItemAfter(previousItemId: AnyRef) = null

  def isLastId(itemId: AnyRef) = itemId == lastItemId

  def isFirstId(itemId: AnyRef) = itemId == firstItemId

  def lastItemId = itemIds.lastOption.orNull

  def firstItemId = itemIds.headOption.orNull

  // extremely ineffective prototype
  def prevItemId(itemId: AnyRef) = let(itemIds.toIndexedSeq) { seq =>
    seq.indexOf(itemId.asInstanceOf[DocId]) match {
      case index if index > 0 => seq(index - 1)
      case _ => null
    }
  }

  // extremely ineffective prototype
  def nextItemId(itemId: AnyRef) = let(itemIds.toIndexedSeq) { seq =>
    seq.indexOf(itemId.asInstanceOf[DocId]) match {
      case index if index < (size - 1) => seq(index + 1)
      case _ => null
    }
  }
}


/**
 * Read only container which provides access to all docs.
 */
class AllDocsContainer extends DocsContainer {

  private val docMapper = imcmsServices.getDocumentMapper

  private var filteredDocIds = Seq.empty[DocId]

  protected def innerSearch(solrQuery: Option[String], user: UserDomainObject) {
    filteredDocIds = docMapper.getAllDocumentIds.toSeq

//    filteredDocIds = solrQuery match {
//      case None => docMapper.getAllDocumentIds.toSeq
//      case Some(query) => docMapper.getDocumentIndex.search(new SimpleDocumentQuery(LuceneParsedQuery.parse(query)), user)
//                                   .map(_.getMeta.getId)
//    }
  }

  def removeItem(itemId: AnyRef) = throw new UnsupportedOperationException

  def addItem(itemId: AnyRef) = throw new UnsupportedOperationException

  def removeAllItems() = throw new UnsupportedOperationException

  def range = let(docMapper.getDocumentIdRange) { idsRange =>
    Some(Int box idsRange.getMinimumInteger, Int box idsRange.getMaximumInteger)
  }

  def getItemIds = filteredDocIds
}


/**
 * Provides access to fully customizable set of docs.
 */
class CustomDocsContainer extends DocsContainer {

  private var docIds = Seq.empty[DocId]

  private var filteredDocIds = Seq.empty[DocId]

  def range = condOpt(docIds) { case ids if ids.nonEmpty => (ids.min, ids.max) }

  protected def innerSearch(solrQuery: Option[String], user: UserDomainObject) {
    filteredDocIds = docIds
  }

  def removeItem(itemId: AnyRef) = docIds.remove(itemId.asInstanceOf[DocId])

  def addItem(itemId: AnyRef) = letret(new DocItem(itemId.asInstanceOf[DocId])) { docItem =>
    docIds :+= docItem.docId
  }

  def removeAllItems() = letret(true) { _ =>
    docIds = Seq.empty
    notifyItemSetChanged()
  }

  def getItemIds = filteredDocIds
}



class DocsUI(container: DocsContainer) extends Table(null, container)
    with MultiSelectBehavior[DocId] with DocTableItemIcon with Selectable {

  setColumnCollapsingAllowed(true)
  setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY)

  setColumnHeaders(container.getContainerPropertyIds map (_.toString.i) toArray )
  Seq("doc.tbl.col.parents", "doc.tbl.col.children") foreach { setColumnCollapsed(_, true) }
}



trait DocStatusItemIcon extends AbstractSelect {
  override def getItemIcon(itemId: AnyRef) = itemId match {
    case doc: DocumentDomainObject => new ExternalResource("imcms/eng/images/admin/status/%s.gif" format
      itemId.asInstanceOf[DocumentDomainObject].getLifeCyclePhase.toString)

    case _ => null
  }
}

trait DocTableItemIcon extends AbstractSelect with XSelect[DocId] {
  override def getItemIcon(itemId: AnyRef) = item(itemId.asInstanceOf[DocId]) match {
    case docItem: DocsContainer#DocItem =>
      new ExternalResource("imcms/eng/images/admin/status/%s.gif" format docItem.doc.getLifeCyclePhase.toString)

    case _ => null
  }
}


class DocBasicSearchForm {
  private var state = DocBasicSearchFormState()

  val ui: DocBasicFormSearchUI = letret(new DocBasicFormSearchUI) { ui =>
    ui.chkRange.addValueChangeHandler { state = updateRangeState(state) }

    ui.chkText.addValueChangeHandler { state = updateTextState(state) }

    ui.chkType.addValueChangeHandler { state = updateTypeState(state) }

    ui.chkAdvanced.addValueChangeHandler {
      ui.lytAdvanced.setEnabled(ui.chkAdvanced.isChecked)
    }

    Seq("doc.search.basic.frm.fld.cb_advanced_type.custom", "doc.search.basic.frm.fld.cb_advanced_type.last_xxx", "doc.search.basic.frm.fld.cb_advanced_type.last_zzz").foreach(itemId => ui.lytAdvanced.cbTypes.addItem(itemId, itemId.i))
  }

  def setRangeInputPrompt(range: Option[(DocId, DocId)]) {
    let(range map { case (start, end) => (start.toString, end.toString) } getOrElse ("", "")) {
      case (start, end) =>
        ui.lytRange.txtStart.setInputPrompt(start)
        ui.lytRange.txtEnd.setInputPrompt(end)
    }
  }


  def reset() {
    ui.chkRange.checked = true
    ui.chkText.checked = true
    ui.chkType.checked = false
    ui.chkAdvanced.checked = false

    forlet(ui.chkRange, ui.chkText, ui.chkType, ui.chkAdvanced)(_ fireValueChange true)

    setState(DocBasicSearchFormState())

    ui.lytAdvanced.cbTypes.value = "doc.search.basic.frm.fld.cb_advanced_type.custom"
  }

  def setState(newState: DocBasicSearchFormState) {
    updateRangeState(newState)
    updateTextState(newState)
    updateTypeState(newState)

    state = newState
  }

  def getState() = state

  private def updateRangeState(currentState: DocBasicSearchFormState) = {
    if (ui.chkRange.isChecked) {
      ui.lytRange.setEnabled(true)
      ui.lytRange.txtStart.value = currentState.range.flatMap(_.start).map(_.toString).getOrElse("")
      ui.lytRange.txtEnd.value = currentState.range.flatMap(_.end).map(_.toString).getOrElse("")
      currentState
    } else {
      val start = condOpt(ui.lytRange.txtStart.value.trim) { case value if value.nonEmpty => value.toInt }
      val end = condOpt(ui.lytRange.txtEnd.value.trim) { case value if value.nonEmpty => value.toInt }
      val newState = currentState.copy(
        range = if (start.isEmpty && end.isEmpty) None else Some(DocSearchRange(start, end))
      )

      ui.lytRange.setEnabled(true)
      ui.lytRange.txtStart.value = ""
      ui.lytRange.txtEnd.value = ""
      ui.lytRange.setEnabled(false)
      newState
    }
  }

  private def updateTextState(currentState: DocBasicSearchFormState) = {
    if (ui.chkText.isChecked) {
      ui.txtText.setEnabled(true)
      ui.txtText.value = currentState.text.getOrElse("")
      currentState
    } else {
      val newState = currentState.copy(
        text = condOpt(ui.txtText.value.trim) { case value if value.nonEmpty => value }
      )

      ui.txtText.setEnabled(true)
      ui.txtText.value = ""
      ui.txtText.setEnabled(false)
      newState
    }
  }

  private def updateTypeState(currentState: DocBasicSearchFormState) = {
    if (ui.chkType.isChecked) {
      ui.lytType.setEnabled(true)
      ui.lytType.chkText.checked = currentState.docType.map(_(DocumentTypeDomainObject.TEXT)).getOrElse(false)
      ui.lytType.chkFile.checked = currentState.docType.map(_(DocumentTypeDomainObject.FILE)).getOrElse(false)
      ui.lytType.chkHtml.checked = currentState.docType.map(_(DocumentTypeDomainObject.HTML)).getOrElse(false)
      currentState
    } else {
      val types =
        Set(
          whenOpt(ui.lytType.chkText.isChecked) { DocumentTypeDomainObject.TEXT },
          whenOpt(ui.lytType.chkFile.isChecked) { DocumentTypeDomainObject.FILE },
          whenOpt(ui.lytType.chkHtml.isChecked) { DocumentTypeDomainObject.HTML }
        ).flatten

      val newState = currentState.copy(
        docType = if (types.size == 3) None else Some(types)
      )

      ui.lytType.setEnabled(true)
      forlet(ui.lytType.chkText, ui.lytType.chkFile, ui.lytType.chkHtml) { _.check }
      ui.lytType.setEnabled(false)
      newState
    }
  }
}

case class DocSearchRange(start: Option[Int] = None, end: Option[Int] = None)
case class DateSearchRange(start: Option[Date] = None, end: Option[Date] = None)

case class DocBasicSearchFormState(
  range: Option[DocSearchRange] = None,
  text: Option[String] = None,
  docType: Option[Set[DocumentTypeDomainObject]] = None
)


class DocBasicFormSearchUI extends CustomLayout("admin/doc/search/basic_form") with FullWidth {

  val chkRange = new CheckBox("doc.search.basic.frm.fld.chk_range".i) with Immediate with ExposeValueChange
  val lytRange = new HorizontalLayout with Spacing with UndefinedSize {
    val txtStart = new TextField { setColumns(5) }
    val txtEnd = new TextField { setColumns(5) }

    addComponents(this, txtStart, txtEnd)
  }

  val chkText = new CheckBox("doc.search.basic.frm.fld.chk_text".i) with Immediate with ExposeValueChange
  val txtText = new TextField { setInputPrompt("doc.search.basic.frm.fld.txt_text.prompt".i) }

  val chkType = new CheckBox("doc.search.basic.frm.fld.chk_type".i) with Immediate with ExposeValueChange
  val lytType = new HorizontalLayout with UndefinedSize with Spacing {
    val chkText = new CheckBox("doc.search.basic.frm.fld.chk_type_text".i)
    val chkFile = new CheckBox("doc.search.basic.frm.fld.chk_type_file".i)
    val chkHtml = new CheckBox("doc.search.basic.frm.fld.chk_type_html".i)

    addComponents(this, chkText, chkFile, chkHtml)
  }

  val chkAdvanced = new CheckBox("doc.search.basic.frm.fld.chk_advanced".i) with Immediate with ExposeValueChange
  //val btnAdvanced = new Button("doc.search.basic.frm.fld.btn_advanced".i) with LinkStyle

  val lytAdvanced = new HorizontalLayout with UndefinedSize with Spacing {
    val cbTypes = new ComboBox with NoNullSelection with SingleSelect2[String] with Immediate
    val btnCustomize = new Button("...") with SmallStyle
    val btnSave = new Button("doc.search.basic.frm.fld.btn_advanced_save".i) with SmallStyle with Disabled

    forlet(cbTypes, btnCustomize, btnSave) { component =>
      addComponent(component)
      setComponentAlignment(component, Alignment.MIDDLE_LEFT)
    }
  }


  val lytButtons = new HorizontalLayout with UndefinedSize with Spacing {
    val btnReset = new Button("btn_reset".i) with SmallStyle
    val btnSearch = new Button("btn_search".i) with SmallStyle

    addComponents(this, btnReset, btnSearch)
  }

  addNamedComponents(this,
    "doc.search.basic.frm.fld.chk_range" -> chkRange,
    "doc.search.basic.frm.fld.lyt_range" -> lytRange,
    "doc.search.basic.frm.fld.chk_text" -> chkText,
    "doc.search.basic.frm.fld.txt_text" -> txtText,
    "doc.search.basic.frm.fld.chk_type" -> chkType,
    "doc.search.basic.frm.fld.lyt_type" -> lytType,
    "doc.search.basic.frm.fld.chk_advanced" -> chkAdvanced,
    "doc.search.basic.frm.fld.lyt_advanced" -> lytAdvanced,
    "doc.search.basic.frm.fld.lyt_buttons" -> lytButtons
  )
}


class DocAdvancedSearchForm extends ImcmsServicesSupport {
  val ui = new DocAdvancedSearchFormUI

  ui.chkCategories.addClickHandler { toggleCategories() }
  ui.chkDates.addClickHandler { toggleDates() }
  ui.chkRelationships.addClickHandler { toggleRelationships() }
  ui.chkMaintainers.addClickHandler { toggleMaintainers() }
  ui.chkStatus.addClickHandler { toggleStatus() }

  def reset() {
    forlet(ui.chkCategories, ui.chkDates, ui.chkRelationships, ui.chkMaintainers, ui.chkStatus)(_.uncheck)
    forlet(ui.lytStatus.chkNew, ui.lytStatus.chkPublished, ui.lytStatus.chkUnpublished, ui.lytStatus.chkApproved, ui.lytStatus.chkDisapproved, ui.lytStatus.chkExpired)(_.uncheck)

    forlet(ui.lytDates.drCreated, ui.lytDates.drModified, ui.lytDates.drPublished, ui.lytDates.drExpired) { dr =>
      dr.cbRangeType.value = DocRangeType.Undefined
    }

    forlet(ui.lytMaintainers.ulCreators, ui.lytMaintainers.ulPublishers) { ul =>
      ul.chkEnabled.uncheck
      ul.chkEnabled.fireValueChange(true)
      ul.lstUsers.removeAllItems
    }

    toggleCategories()
    toggleMaintainers()
    toggleRelationships()
    toggleDates()
    toggleStatus()

    for {
      categoryType <- imcmsServices.getCategoryMapper.getAllCategoryTypes
      category <- imcmsServices.getCategoryMapper.getAllCategoriesOfType(categoryType)
    } {
      ui.tcsCategories.addItem(category)
      ui.tcsCategories.setItemCaption(category, categoryType.getName + ":" + category.getName)
      ?(category.getImageUrl).foreach(url => ui.tcsCategories.setItemIcon(category, new ExternalResource(url)))
    }

    ui.lytRelationships.cbParents.value = "doc.search.advanced.frm.fld.cb_relationships_parents.item.undefined"
    ui.lytRelationships.cbChildren.value = "doc.search.advanced.frm.fld.cb_relationships_children.item.undefined"
  }

  private def toggle(checkBox: CheckBox, component: Component, name: String) {
    ui.addComponent(
      if (checkBox.checked) component else new Label("doc.search.advanced.frm.fld.lbl_undefined".i) with UndefinedSize,
      name)
  }

  private def toggleCategories() = toggle(ui.chkCategories, ui.tcsCategories, "doc.search.advanced.frm.fld.tcs_categories")
  private def toggleMaintainers() = toggle(ui.chkMaintainers, ui.lytMaintainers, "doc.search.advanced.frm.fld.lyt_maintainers")
  private def toggleRelationships() = toggle(ui.chkRelationships, ui.lytRelationships, "doc.search.advanced.frm.fld.lyt_relationships")
  private def toggleDates() = toggle(ui.chkDates, ui.lytDates, "doc.search.advanced.frm.fld.lyt_dates")
  private def toggleStatus() = toggle(ui.chkStatus, ui.lytStatus, "doc.search.advanced.frm.fld.lyt_status")
}


class DocAdvancedSearchFormUI extends CustomLayout("admin/doc/search/advanced_form") with FullWidth {
  val chkStatus = new CheckBox("doc.search.advanced.frm.fld.chk_status".i) with Immediate
  val lytStatus = new HorizontalLayout with Spacing with UndefinedSize {
    val chkNew = new CheckBox("doc.search.advanced.frm.fld.chk_status_new".i)
    val chkPublished = new CheckBox("doc.search.advanced.frm.fld.chk_status_published".i)
    val chkUnpublished = new CheckBox("doc.search.advanced.frm.fld.chk_status_unpublished".i)
    val chkApproved = new CheckBox("doc.search.advanced.frm.fld.chk_status_approved".i)
    val chkDisapproved = new CheckBox("doc.search.advanced.frm.fld.chk_status_disapproved".i)
    val chkExpired = new CheckBox("doc.search.advanced.frm.fld.chk_status_expired".i)

    addComponents(this, chkNew, chkPublished, chkUnpublished, chkApproved, chkDisapproved, chkExpired)
  }

  val chkDates = new CheckBox("doc.search.advanced.frm.fld.chk_dates".i) with Immediate
  val lytDates = new FormLayout with UndefinedSize {
    val drCreated = new DocDateRangeUI("doc.search.advanced.frm.fld.dr_created".i) with DocDateRangeUISetup
    val drModified = new DocDateRangeUI("doc.search.advanced.frm.fld.dr_modified".i) with DocDateRangeUISetup
    val drPublished = new DocDateRangeUI("doc.search.advanced.frm.fld.dr_published".i) with DocDateRangeUISetup
    val drExpired = new DocDateRangeUI("doc.search.advanced.frm.fld.dr_expired".i) with DocDateRangeUISetup

    addComponents(this, drCreated, drModified, drPublished, drExpired)
  }

  val chkCategories = new CheckBox("doc.search.advanced.frm.fld.chk_categories".i) with Immediate
  val tcsCategories = new TwinColSelect

  val chkRelationships = new CheckBox("doc.search.advanced.frm.fld.chk_relationships".i) with Immediate
  val lytRelationships = new HorizontalLayout with Spacing with UndefinedSize {
    val cbParents = new ComboBox("doc.search.advanced.frm.fld.chk_relationships_parents".i) with SingleSelect2[String] with NoNullSelection
    val cbChildren = new ComboBox("doc.search.advanced.frm.fld.chk_relationships_children".i) with SingleSelect2[String] with NoNullSelection

    Seq("doc.search.advanced.frm.fld.cb_relationships_parents.item.undefined",
        "doc.search.advanced.frm.fld.cb_relationships_parents.item.has_parents",
        "doc.search.advanced.frm.fld.cb_relationships_parents.item.no_parents"
    ).foreach(itemId => cbParents.addItem(itemId, itemId.i))

    Seq("doc.search.advanced.frm.fld.cb_relationships_children.item.undefined",
        "doc.search.advanced.frm.fld.cb_relationships_children.item.has_children",
        "doc.search.advanced.frm.fld.cb_relationships_children.item.no_children"
    ).foreach(itemId => cbChildren.addItem(itemId, itemId.i))

    addComponents(this, cbParents, cbChildren)
  }

  val chkMaintainers = new CheckBox("doc.search.advanced.frm.fld.chk_maintainers".i) with Immediate
  val lytMaintainers = new HorizontalLayout with Spacing with UndefinedSize{
    val ulCreators = new UserListUI("doc.search.advanced.frm.fld.chk_maintainers_creators".i) with UserListUISetup {
      val searchDialogCaption = "doc.search.advanced.dlg_select_creators.caption".i
    }

    val ulPublishers = new UserListUI("doc.search.advanced.frm.fld.chk_maintainers_publishers".i) with UserListUISetup {
      val searchDialogCaption = "doc.search.advanced.dlg_select_publishers.caption".i
    }

    addComponents(this, ulCreators, ulPublishers)
  }

  addNamedComponents(this,
    "doc.search.advanced.frm.fld.chk_status" -> chkStatus,
    "doc.search.advanced.frm.fld.lyt_status" -> lytStatus,
    "doc.search.advanced.frm.fld.chk_dates" -> chkDates,
    "doc.search.advanced.frm.fld.lyt_dates" -> lytDates,
    "doc.search.advanced.frm.fld.chk_relationships" -> chkRelationships,
    "doc.search.advanced.frm.fld.lyt_relationships" -> lytRelationships,
    "doc.search.advanced.frm.fld.chk_categories" -> chkCategories,
    "doc.search.advanced.frm.fld.tcs_categories" -> tcsCategories,
    "doc.search.advanced.frm.fld.chk_maintainers" -> chkMaintainers,
    "doc.search.advanced.frm.fld.lyt_maintainers" -> lytMaintainers
  )
}


trait UserListUISetup { this: UserListUI =>
  val searchDialogCaption: String

  chkEnabled.addValueChangeHandler {
    forlet(lstUsers, lytButtons)(_ setEnabled chkEnabled.booleanValue)
  }

  btnAdd.addClickHandler {
    getApplication.initAndShow(new OkCancelDialog(searchDialogCaption) with UserSearchDialog) { dlg =>
      dlg.wrapOkHandler {
        for (user <- dlg.search.selection) lstUsers.addItem(Int box user.getId, "#" + user.getLoginName)
      }
    }
  }

  btnRemove.addClickHandler {
    lstUsers.value.foreach(lstUsers.removeItem)
  }
}


/**
 * Component for managing list of users.
 */
class UserListUI(caption: String = "") extends GridLayout(2, 2) {
  val chkEnabled = new CheckBox(caption) with Immediate with ExposeValueChange
  val lstUsers = new ListSelect with MultiSelectBehavior[UserId] with NoNullSelection {
    setColumns(20)
  }
  val btnAdd = new Button("+") with SmallStyle
  val btnRemove = new Button("-") with SmallStyle
  val lytButtons = new VerticalLayout with UndefinedSize

  addComponents(lytButtons, btnRemove, btnAdd)
  addComponent(chkEnabled, 0, 0, 1, 0)
  addComponents(this, lstUsers, lytButtons)

  setComponentAlignment(lytButtons, Alignment.BOTTOM_LEFT)
}

// I18n os not properly implemented - not dynamic
object DocRangeType extends Enumeration {
  val Undefined = Value("dr.cb_type.item.undefined".i)
  val Custom = Value("dr.cb_type.item.custom".i)
  val Day = Value("dr.cb_type.item.day".i)
  val Week = Value("dr.cb_type.item.week".i)
  val Month = Value("dr.cb_type.item.month".i)
  val Quarter = Value("dr.cb_type.item.quarter".i)
  val Year = Value("dr.cb_type.item.year".i)
}

class DocDateRangeUI(caption: String = "") extends HorizontalLayout with Spacing with UndefinedSize {
  val cbRangeType = new ComboBox with ValueType[DocRangeType.Value] with NoNullSelection with Immediate
  val dtFrom = new PopupDateField with DayResolution
  val dtTo = new PopupDateField with DayResolution

  dtFrom.setInputPrompt("dr.dt_from.prompt".i)
  dtTo.setInputPrompt("dr.dt_to.prompt".i)

  setCaption(caption)

  addComponents(this, cbRangeType, dtFrom, dtTo)
}


trait DocDateRangeUISetup { this: DocDateRangeUI =>
  import DocRangeType._

  cbRangeType.addValueChangeHandler {
    forlet(dtFrom, dtTo) { _ setEnabled false }
    val now = new Date
    val calendar = Calendar.getInstance

    cbRangeType.value match {
      case Undefined =>
        dtFrom.setValue(null)
        dtTo.setValue(null)

      case Custom =>
        forlet(dtFrom, dtTo) { dt => dt setEnabled true; dt.value = now }

      case Day =>
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        dtFrom.value = calendar.getTime
        dtTo.value = now

      case Week =>
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        dtFrom.value = calendar.getTime
        dtTo.value = now

      case Month =>
        calendar.add(Calendar.MONTH, -1)
        dtFrom.value = calendar.getTime
        dtTo.value = now

      case Quarter =>
        calendar.add(Calendar.MONTH, -3)
        dtFrom.value = calendar.getTime
        dtTo.value = now

      case Year =>
        calendar.add(Calendar.YEAR, -1)
        dtFrom.value = calendar.getTime
        dtTo.value = now
    }
  }

  DocRangeType.values foreach (cbRangeType addItem _)
  cbRangeType.value = Undefined
}
