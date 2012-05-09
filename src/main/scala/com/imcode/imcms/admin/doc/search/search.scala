package com.imcode
package imcms
package admin.doc.search

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
import admin.access.user.UserSelectDialog
import java.util.{Calendar, Date}
import api.{LuceneParsedQuery, Document}
import imcode.server.user.UserDomainObject
import imcode.server.document.index.SimpleDocumentQuery
import com.vaadin.ui.ComponentContainer.{ComponentAttachEvent, ComponentAttachListener}
import com.vaadin.ui._
import scala.Some

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
  val searchResultUI = new DocsUI(docsContainer) with FullSize

  val ui = new DocSearchUI(basicSearchForm.ui, advancedSearchForm.ui, searchResultUI) { ui =>
    val basicFormUI = basicSearchForm.ui

    basicFormUI.lytAdvanced.btnCustomize.addClickHandler { ui.toggleAdvancedSearchForm() }
    basicFormUI.chkAdvanced.addValueChangeHandler {
      if (!basicFormUI.chkAdvanced.booleanValue) ui.advancedSearchFormVisible = false
    }

    basicFormUI.lytButtons.btnSearch.addClickHandler { search() }
    basicFormUI.lytButtons.btnReset.addClickHandler { reset() }

    override def attach() {
      super.attach()
      reset()
    }
  }


  def reset() {
    basicSearchForm.reset()
    advancedSearchForm.reset()
    update()
    search()
  }

  def update() {
    basicSearchForm.setRangeInputPrompt(docsContainer.range)
  }

  def search() {
    createQuery() match {
      case Left(throwable) =>
        ui.getApplication.show(new ErrorDialog(throwable.getMessage.i))

      case Right(solrQueryOpt) =>
        println("Doc search query: " + solrQueryOpt)

        ui.removeComponent(0, 1)
        ui.addComponent(searchResultUI, 0, 1)

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
            case _ => sys.error("doc.search.dlg_param_validation_err.msg.illegal_range_value")
          }
        }

        val end = condOpt(basicFormUI.lytRange.txtStart.trim) {
          case value if value.nonEmpty => value match {
            case IntNumber(end) => end
            case _ => sys.error("doc.search.dlg_param_validation_err.msg.illegal_range_value")
          }
        }

        when(start.isDefined || end.isDefined) {
          DocSearchRange(start, end)
        }
      }


    val textOpt: Option[String] =
      if (basicFormUI.chkText.isUnchecked) None
      else condOpt(basicFormUI.txtText.trim) {
        case value if value.nonEmpty => value
      }


    val typesOpt: Option[List[String]] =
      if (basicFormUI.chkType.isUnchecked) None
      else {
        import basicFormUI.lytType._

        Map(chkFile -> "file",
            chkText -> "text",
            chkHtml -> "html"
        ).filterKeys(_.isChecked).values.toList match {
          case Nil => None
          case values => Some(values)
        }
    }

    val statusesOpt: Option[List[String]] =
      if (advancedFormUI.chkStatus.isUnchecked) None
      else {
      import advancedFormUI.lytStatus._

        Map(chkNew -> "new",
            chkPublished -> "published",
            chkUnpublished -> "unpublished",
            chkApproved -> "approved",
            chkDisapproved -> "disapproved",
            chkExpired -> "expired"
        ).filterKeys(_.isChecked).values.toList match {
          case Nil => None
          case values => Some(values)
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
            start = Option(dr.dtFrom.value)
            end = Option(dr.dtTo.value)
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

    val categoriesOpt: Option[List[String]] =
      if (advancedFormUI.chkCategories.isUnchecked) None
      else {
        advancedFormUI.tcsCategories.getItemIds.asInstanceOf[JCollection[String]].toList match {
          case Nil => None
          case values => Some(values)
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
    docsUI: DocsUI) extends GridLayout(1, 2) with FullSize {

  private val pnlAdvancedSearchForm = new Panel with Scrollable with FullSize {
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
            new Tree with ItemIdType[DocumentDomainObject] with NotSelectable with DocStatusItemIcon |>> { tree =>
              val parentDoc = pair.getDocument
              tree.addItem(parentDoc)
              tree.setChildrenAllowed(parentDoc, false)
              tree.setItemCaption(parentDoc, "%s - %s" format (parentDoc.getId, parentDoc.getHeadline))
            }

          case pairs => new Tree with ItemIdType[DocumentDomainObject] with NotSelectable with DocStatusItemIcon |>> { tree =>
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
                new Tree with ItemIdType[DocumentDomainObject] with DocStatusItemIcon with NotSelectable |>> { tree =>
                  tree.addItem(childDoc)
                  tree.setChildrenAllowed(childDoc, false)
                  tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
                }

              case childDocs => new Tree with ItemIdType[DocumentDomainObject] with DocStatusItemIcon with NotSelectable |>> { tree =>
                val root = new {}
                tree.addItem(root)
                tree.setItemCaption(root, childDocs.size.toString)
                for (childDoc <- childDocs) {
                  tree.addItem(childDoc)
                  tree.setChildrenAllowed(childDoc, false)
                  tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
                  tree.setParent(childDoc, root)
                  // >>> link to listByNamedParams documents
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
  def prevItemId(itemId: AnyRef) = itemIds.toIndexedSeq |> { seq =>
    seq.indexOf(itemId.asInstanceOf[DocId]) match {
      case index if index > 0 => seq(index - 1)
      case _ => null
    }
  }

  // extremely ineffective prototype
  def nextItemId(itemId: AnyRef) = itemIds.toIndexedSeq |> { seq =>
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

  def range = docMapper.getDocumentIdRange |> { idsRange =>
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

  def addItem(itemId: AnyRef) = new DocItem(itemId.asInstanceOf[DocId]) |>> { docItem =>
    docIds :+= docItem.docId
  }

  def removeAllItems() = true |>> { _ =>
    docIds = Seq.empty
    notifyItemSetChanged()
  }

  def getItemIds = filteredDocIds
}



class DocsUI(container: DocsContainer) extends Table(null, container)
    with MultiSelectBehavior[DocId] with DocTableItemIcon with Selectable {

  setColumnCollapsingAllowed(true)
  setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY)

  setColumnHeaders(container.getContainerPropertyIds.map(_.toString.i).toArray)
  Seq("doc.tbl.col.parents", "doc.tbl.col.children") foreach { setColumnCollapsed(_, true) }
}



trait DocStatusItemIcon extends AbstractSelect {
  override def getItemIcon(itemId: AnyRef) = itemId match {
    case doc: DocumentDomainObject => new ExternalResource("imcms/eng/images/admin/status/%s.gif" format
      itemId.asInstanceOf[DocumentDomainObject].getLifeCyclePhase.toString)

    case _ => null
  }
}

trait DocTableItemIcon extends AbstractSelect with ItemIdType[DocId] {
  override def getItemIcon(itemId: AnyRef) = item(itemId.asInstanceOf[DocId]) match {
    case docItem: DocsContainer#DocItem =>
      new ExternalResource("imcms/eng/images/admin/status/%s.gif" format docItem.doc.getLifeCyclePhase.toString)

    case _ => null
  }
}


class DocBasicSearchForm {

  val ui: DocBasicFormSearchUI = new DocBasicFormSearchUI |>> { ui =>
    ui.chkRange.addValueChangeHandler {
       SearchFormUtil.toggle(ui, "doc.search.basic.frm.fld.range", ui.chkRange, ui.lytRange,
                             new Label("%s - %s".format(Option(ui.lytRange.txtStart.getInputPrompt).getOrElse(""), Option(ui.lytRange.txtEnd.getInputPrompt).getOrElse(""))))
    }

    ui.chkText.addValueChangeHandler {
       SearchFormUtil.toggle(ui, "doc.search.basic.frm.fld.text", ui.chkText, ui.txtText)
    }

    ui.chkType.addValueChangeHandler {
      SearchFormUtil.toggle(ui, "doc.search.basic.frm.fld.type", ui.chkType, ui.lytType)
    }

    ui.chkAdvanced.addValueChangeHandler {
      ui.lytAdvanced.setEnabled(ui.chkAdvanced.isChecked)
    }
  }

  def setRangeInputPrompt(range: Option[(DocId, DocId)]) {
    range.map { case (start, end) => (start.toString, end.toString) }.getOrElse ("", "") |> {
      case (start, end) =>
        ui.lytRange.txtStart.setInputPrompt(start)
        ui.lytRange.txtEnd.setInputPrompt(end)
    }
  }


  def reset() = setState(DocBasicSearchFormState())

  def setState(state: DocBasicSearchFormState) {
    ui.chkRange.checked = state.range.isDefined
    ui.chkText.checked = state.text.isDefined
    ui.chkType.checked = state.docType.isDefined
    ui.chkAdvanced.checked = state.advanced.isDefined
    doall(ui.chkRange, ui.chkText, ui.chkType, ui.chkAdvanced)(_ fireValueChange true)

    ui.txtText.value = state.text.getOrElse("")

    state.range.collect {
      case DocSearchRange(start, end) => (start.map(_.toString).getOrElse(""), end.map(_.toString).getOrElse(""))
    } getOrElse ("", "") match {
      case (start, end) =>
        ui.lytRange.txtStart.value = start
        ui.lytRange.txtEnd.value = end
    }

    ui.lytType.chkText.checked = state.docType.map(_(DocumentTypeDomainObject.TEXT)).getOrElse(false)
    ui.lytType.chkFile.checked = state.docType.map(_(DocumentTypeDomainObject.FILE)).getOrElse(false)
    ui.lytType.chkHtml.checked = state.docType.map(_(DocumentTypeDomainObject.HTML)).getOrElse(false)

    // todo: DEMO, replace with real values when spec is complete
    ui.lytAdvanced.cbTypes.removeAllItems()
    Seq("doc.search.basic.frm.fld.cb_advanced_type.custom", "doc.search.basic.frm.fld.cb_advanced_type.last_xxx", "doc.search.basic.frm.fld.cb_advanced_type.last_zzz").foreach(itemId => ui.lytAdvanced.cbTypes.addItem(itemId, itemId.i))
    ui.lytAdvanced.cbTypes.value = state.advanced.getOrElse("doc.search.basic.frm.fld.cb_advanced_type.custom")
  }

  // todo: return Error Either State
  def getState() = DocBasicSearchFormState(
    range = when(ui.chkRange.isChecked) {
      DocSearchRange(
        condOpt(ui.lytRange.txtStart.trim) { case value if value.nonEmpty => value.toInt },
        condOpt(ui.lytRange.txtEnd.trim) { case value if value.nonEmpty => value.toInt }
      )
    },

    text = when(ui.chkText.isChecked)(ui.txtText.trim),

    docType = when(ui.chkType.isChecked) {
      Set(
        when(ui.lytType.chkText.isChecked) { DocumentTypeDomainObject.TEXT },
        when(ui.lytType.chkFile.isChecked) { DocumentTypeDomainObject.FILE },
        when(ui.lytType.chkHtml.isChecked) { DocumentTypeDomainObject.HTML }
      ).flatten
    },

    advanced = when(ui.chkAdvanced.isChecked)(ui.lytAdvanced.cbTypes.value)
  )
}

case class DocSearchRange(start: Option[Int] = None, end: Option[Int] = None)
case class DateSearchRange(start: Option[Date] = None, end: Option[Date] = None)

case class DocBasicSearchFormState(
  range: Option[DocSearchRange] = Some(DocSearchRange(None, None)),
  text: Option[String] = Some(""),
  docType: Option[Set[DocumentTypeDomainObject]] = Some(Set.empty),
  profile: Boolean = false, // Set[String]
  advanced: Option[String] = None // value in drop-down
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
    val chkURL = new CheckBox("doc.search.basic.frm.fld.chk_type_url".i)
    val chkProfile = new CheckBox("doc.search.basic.frm.fld.chk_type_profile".i)

    addComponents(this, chkText, chkFile, chkHtml, chkURL, new Label(" | ") with UndefinedSize,  chkProfile)
  }

  val chkAdvanced = new CheckBox("doc.search.basic.frm.fld.chk_advanced".i) with Immediate with ExposeValueChange

  val lytAdvanced = new HorizontalLayout with UndefinedSize with Spacing {
    val cbTypes = new ComboBox with NoNullSelection with SingleSelect[String] with Immediate
    val btnCustomize = new Button("...") with SmallStyle
    val btnSaveAs = new Button("doc.search.basic.frm.fld.btn_advanced_save_as".i) with SmallStyle with Disabled
    val btnDelete = new Button("doc.search.basic.frm.fld.btn_advanced_delete".i) with SmallStyle with Disabled

    doall(cbTypes, btnCustomize, btnSaveAs, btnDelete) { component =>
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
    "doc.search.basic.frm.fld.range" -> lytRange,
    "doc.search.basic.frm.fld.chk_text" -> chkText,
    "doc.search.basic.frm.fld.text" -> txtText,
    "doc.search.basic.frm.fld.chk_type" -> chkType,
    "doc.search.basic.frm.fld.type" -> lytType,
    "doc.search.basic.frm.fld.chk_advanced" -> chkAdvanced,
    "doc.search.basic.frm.fld.advanced" -> lytAdvanced,
    "doc.search.basic.frm.fld.buttons" -> lytButtons
  )
}


class DocAdvancedSearchForm extends ImcmsServicesSupport {
  val ui = new DocAdvancedSearchFormUI

  ui.chkCategories.addValueChangeHandler { toggleCategories() }
  ui.chkDates.addValueChangeHandler { toggleDates() }
  ui.chkRelationships.addValueChangeHandler { toggleRelationships() }
  ui.chkMaintainers.addValueChangeHandler { toggleMaintainers() }
  ui.chkStatus.addValueChangeHandler { toggleStatus() }

  def reset() {
    doall(ui.chkCategories, ui.chkDates, ui.chkRelationships, ui.chkMaintainers, ui.chkStatus)(_.uncheck)
    doall(ui.lytStatus.chkNew, ui.lytStatus.chkPublished, ui.lytStatus.chkUnpublished, ui.lytStatus.chkApproved, ui.lytStatus.chkDisapproved, ui.lytStatus.chkExpired)(_.uncheck)

    doall(ui.lytDates.drCreated, ui.lytDates.drModified, ui.lytDates.drPublished, ui.lytDates.drExpired) { dr =>
      dr.cbRangeType.value = DocRangeType.Undefined
    }

    doall(ui.lytMaintainers.ulCreators, ui.lytMaintainers.ulPublishers) { ul =>
      ul.chkEnabled.check
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
      Option(category.getImageUrl).foreach(url => ui.tcsCategories.setItemIcon(category, new ExternalResource(url)))
    }

    ui.lytRelationships.cbParents.value = "doc.search.advanced.frm.fld.cb_relationships_parents.item.undefined"
    ui.lytRelationships.cbChildren.value = "doc.search.advanced.frm.fld.cb_relationships_children.item.undefined"
  }

  private def toggleCategories() = SearchFormUtil.toggle(ui, "doc.search.advanced.frm.fld.categories", ui.chkCategories, ui.tcsCategories)
  private def toggleMaintainers() = SearchFormUtil.toggle(ui, "doc.search.advanced.frm.fld.maintainers", ui.chkMaintainers, ui.lytMaintainers)
  private def toggleRelationships() = SearchFormUtil.toggle(ui, "doc.search.advanced.frm.fld.relationships", ui.chkRelationships, ui.lytRelationships)
  private def toggleDates() = SearchFormUtil.toggle(ui, "doc.search.advanced.frm.fld.dates", ui.chkDates, ui.lytDates)
  private def toggleStatus() = SearchFormUtil.toggle(ui, "doc.search.advanced.frm.fld.status", ui.chkStatus, ui.lytStatus)
}


class DocAdvancedSearchFormUI extends CustomLayout("admin/doc/search/advanced_form") with UndefinedSize {
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
  val tcsCategories = new TwinColSelect with TCSDefaultI18n

  val chkRelationships = new CheckBox("doc.search.advanced.frm.fld.chk_relationships".i) with Immediate
  val lytRelationships = new HorizontalLayout with Spacing with UndefinedSize {
    val cbParents = new ComboBox("doc.search.advanced.frm.fld.chk_relationships_parents".i) with SingleSelect[String] with NoNullSelection
    val cbChildren = new ComboBox("doc.search.advanced.frm.fld.chk_relationships_children".i) with SingleSelect[String] with NoNullSelection

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
    "doc.search.advanced.frm.fld.status" -> lytStatus,
    "doc.search.advanced.frm.fld.chk_dates" -> chkDates,
    "doc.search.advanced.frm.fld.dates" -> lytDates,
    "doc.search.advanced.frm.fld.chk_relationships" -> chkRelationships,
    "doc.search.advanced.frm.fld.relationships" -> lytRelationships,
    "doc.search.advanced.frm.fld.chk_categories" -> chkCategories,
    "doc.search.advanced.frm.fld.categories" -> tcsCategories,
    "doc.search.advanced.frm.fld.chk_maintainers" -> chkMaintainers,
    "doc.search.advanced.frm.fld.maintainers" -> lytMaintainers
  )
}


trait UserListUISetup { this: UserListUI =>
  val searchDialogCaption: String

  chkEnabled.addValueChangeHandler {
    doall(lstUsers, lytButtons)(_ setEnabled chkEnabled.booleanValue)
  }

  btnAdd.addClickHandler {
    getApplication.initAndShow(new OkCancelDialog(searchDialogCaption) with UserSelectDialog) { dlg =>
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
 * Component for managing listByNamedParams of users.
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
    doall(dtFrom, dtTo) { _ setEnabled false }
    val now = new Date
    val calendar = Calendar.getInstance

    cbRangeType.value match {
      case Undefined =>
        dtFrom.setValue(null)
        dtTo.setValue(null)

      case Custom =>
        doall(dtFrom, dtTo) { dt => dt setEnabled true; dt.value = now }

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
