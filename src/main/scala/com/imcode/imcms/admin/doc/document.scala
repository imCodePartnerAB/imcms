package com.imcode
package imcms.admin.doc

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import com.imcode.imcms.api._
import imcode.server.user._
import imcode.server.{Imcms}
import imcode.server.document._
import com.imcode.imcms.vaadin._

import java.net.{MalformedURLException, URL}
import com.imcode.imcms.vaadin.flow.{Flow, FlowPage, FlowUI}
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile
import imcode.util.io.InputStreamSource
import java.io.ByteArrayInputStream
import textdocument.TextDocumentDomainObject
import java.util.{EnumSet}
import imcms.mapping.DocumentMapper.SaveDirectives
import imcms.mapping.{DocumentMapper, DocumentSaver}


import collection.mutable.{Map => MMap}

///////////////////////////////////////////////////////////
// @deprecated
///////////////////////////////////////////////////////////


//todo: type Component = UI ??

case class MimeType(name: String, displayName: String)


/**
 * Document editors factory - creates and initializes document editors.
 */
class EditorsFactory(app: ImcmsApplication, user: UserDomainObject) {
  
  import scala.util.control.{Exception => E}
  
  def newURLDocFlow(parentDoc: DocumentDomainObject): Flow[UrlDocumentDomainObject] = {
    val docUI = new URLDocEditorUI
    val docValidator = () => E.allCatch.either(new URL(docUI.txtURL.value)) fold (ex => Some(ex.getMessage), url => None)
    val page0 = new FlowPage(() => docUI, docValidator)

    val metaModel = MetaModel(DocumentTypeDomainObject.URL_ID, parentDoc)
    val metaEditor = new MetaEditorDepricated(app, metaModel)
    val metaValidator = () => Some("meta is invalid, please fix the following errors..")
    val page1 = new FlowPage(() => metaEditor.ui, metaValidator)

    val commit = () => Left("Not implemented")

    new Flow[UrlDocumentDomainObject](commit, page0, page1)
  }

  
  def newFileDocFlow(parentDoc: DocumentDomainObject): Flow[FileDocumentDomainObject] = {
    val doc = Imcms.getServices.getDocumentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.FILE_ID, parentDoc, user).asInstanceOf[FileDocumentDomainObject]
    val mimeTypes = for {
      Array(name, description) <- Imcms.getServices.getDocumentMapper.getAllMimeTypesWithDescriptions(user).toSeq
    } yield  MimeType(name, description)

    val docEditor = new FileDocEditor(app, doc, mimeTypes)
    val docValidator = () => None
    val page0 = new FlowPage(() => docEditor.ui, docValidator)

    val metaModel = MetaModel(DocumentTypeDomainObject.URL_ID, parentDoc)
    val metaEditor = new MetaEditorDepricated(app, metaModel)
    val metaValidator = () => Some("meta is invalid, please fix the following errors..")
    val page1 = new FlowPage(() => metaEditor.ui, metaValidator)

    val commit = () =>  // : Function0[String Either FileDocumentDomainObject]
      E.allCatch[FileDocumentDomainObject].either {
        doc.setMeta(metaModel.meta)
        Imcms.getServices.getDocumentMapper.saveNewDocument(doc, metaModel.i18nMetas, user).asInstanceOf[FileDocumentDomainObject]
      } match {
        case Left(ex) => Left(ex.getMessage)
        case Right(doc) => Right(doc)
      }

    new Flow[FileDocumentDomainObject](commit, page0, page1)
  }

  def newTextDocFlow(parentDoc: DocumentDomainObject): Flow[TextDocumentDomainObject] = {
    val doc = Imcms.getServices.getDocumentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, user).asInstanceOf[TextDocumentDomainObject]
    val metaModel = MetaModel(DocumentTypeDomainObject.URL_ID, parentDoc)
    val metaEditor = new MetaEditorDepricated(app, metaModel)

    val copyTextEditor = new NewTextDocumentFlowPage2(metaModel)
    
    // page0 - welcome?
    val page1 = new FlowPage(() => metaEditor.ui, metaEditor.validate)
    val page2 = new FlowPage(() => {copyTextEditor.reload(); copyTextEditor.ui})

    val commit = () =>  // : Function0[String Either TextDocumentDomainObject]
      E.allCatch[TextDocumentDomainObject].either {
        doc.setMeta(metaModel.meta)

        val parameters = if (copyTextEditor.isCopyI18nMetaTextsToTextFields)
          EnumSet.of(DocumentMapper.SaveDirectives.CopyI18nMetaTextsIntoTextFields)
        else
          EnumSet.noneOf(classOf[DocumentMapper.SaveDirectives])

        Imcms.getServices.getDocumentMapper.saveNewDocument(doc, metaModel.i18nMetas, parameters, user)//.asInstanceOf[TextDocumentDomainObject]
      } match {
        case Left(ex) => Left(ex.getMessage)
        case Right(doc) => Right(doc)
      }

    new Flow[TextDocumentDomainObject](commit, page1, page2)
  }

  def editURLDocument = new URLDocEditorUI
  def editFileDocument = new FileDocEditorUI
  def editTextDocument {}
}


/**
 * URL document editor UI
 */
// todo: escape URL text, validate???
class URLDocEditorUI extends FormLayout {
  val lblTodo = new Label("#TODO: SUPPORTED PROTOCOLS: HTTPS, FTP?; VALIDATE?")
  val txtURL = new TextField("Content URL") with ValueType[String] {
    setInternalValue("http://")
  }

  addComponents(this, lblTodo, txtURL)
}

/**
 * File document editor UI
 * File document is `container` which may contain one or more related or unrelated files.
 * If there is more than one file then one of them must be set as default.
 * Default file content is returned when an user clicks on a doc link in a browser. 
 */
class FileDocEditorUI extends VerticalLayout with UndefinedSize {
  val menuBar = new MenuBar
  val miNew = menuBar.addItem("Add", null)
  val miEdit = menuBar.addItem("Edit", null)
  val miDelete = menuBar.addItem("Delete", null)
  val miSetDefault = menuBar.addItem("Set default", null)

  type FileId = String
  val tblFiles = new Table with ValueType[FileId] with Selectable with Immediate with Reloadable {
    addContainerProperties(this,
      ContainerProperty[FileId]("File Id"),
      ContainerProperty[String]("File name"),
      ContainerProperty[String]("Size"),
      ContainerProperty[String]("Mime type"),
      ContainerProperty[String]("Default"))
  }

  addComponents(this, menuBar, tblFiles)
}

/** Add/Edit file doc's file */
class FileDocFileDialogContent extends FormLayout with UndefinedSize {
  // model
  val uploadReceiver = new MemoryUploadReceiver

  // ui
  val sltMimeType = new Select("Mime type") with ValueType[String]
  val lblUploadStatus = new Label with UndefinedSize
  val txtFileId = new TextField("File id")
  val upload = new Upload(null, uploadReceiver) with UploadEventHandler {
    setImmediate(true)
    setButtonCaption("Select")

    def handleEvent(e: com.vaadin.ui.Component.Event) = e match {
      case e: Upload.SucceededEvent =>
        alterNameTextField()
      case e: Upload.FailedEvent =>
        uploadReceiver.uploadRef.set(None)
        alterNameTextField()
      case _ =>
    }
  }

  addComponents(this, lblUploadStatus, upload, sltMimeType, txtFileId)
  alterNameTextField()

  def alterNameTextField() = let(uploadReceiver.uploadRef.get) { uploadOpt =>
    lblUploadStatus setValue (uploadOpt match {
      case Some(upload) => upload.filename
      case _ => "No file selected"
    })
  }
}

class FileDocEditor(app: ImcmsApplication, doc: FileDocumentDomainObject, mimeTypes: Seq[MimeType]) {
  val ui = letret(new FileDocEditorUI) { ui =>
    ui.tblFiles.itemsProvider = () =>
      doc.getFiles.toSeq collect {
        case (fileId, fdf) =>
          fileId -> List(fileId, fdf.getId, fdf.getMimeType, fdf.getInputStreamSource.getSize.toString, (fileId == doc.getDefaultFileId).toString)
      }

    ui.tblFiles.reload()

    ui.miNew setCommandHandler {
      app.initAndShow(new OkCancelDialog("Add file")) { w =>
        let(w setMainContent new FileDocFileDialogContent) { c =>
          for (MimeType(name, displayName) <- mimeTypes) {
            c.sltMimeType.addItem(name)  
          }

          w wrapOkHandler {
            c.uploadReceiver.uploadRef.get match {
              case Some(upload) =>
                val file = new FileDocumentFile
                val source = new InputStreamSource {
                  def getInputStream = new ByteArrayInputStream(upload.content)
                  def getSize = upload.content.length
                }

                file.setInputStreamSource(source)
                file.setFilename(c.txtFileId.value)
                file.setMimeType(c.sltMimeType.value)

                doc.addFile(c.txtFileId.value, file)
                ui.tblFiles.reload()
              case _ =>
            }
          }
        }
      }
    }

    // todo: replace old file - delete from storage
    ui.miEdit setCommandHandler {
      whenSelected(ui.tblFiles) { fileId =>
        app.initAndShow(new OkCancelDialog("Edit file")) { dlg =>
          let(dlg.setMainContent(new FileDocFileDialogContent)) { c =>
            val fdf = doc.getFile(fileId)
            
            c.txtFileId.value = fileId
            //c.sltMimeType.value = "" // todo: set
            c.lblUploadStatus.value = fdf.getFilename

            dlg wrapOkHandler {
              c.uploadReceiver.uploadRef.get match {
                case Some(upload) => // relace fdf
                  val newFdf = new FileDocumentFile
                  val source = new InputStreamSource {
                    def getInputStream = new ByteArrayInputStream(upload.content)
                    def getSize = upload.content.length
                  }

                  newFdf.setInputStreamSource(source)
                  newFdf.setFilename(c.txtFileId.value)

                  doc.addFile(c.txtFileId.value, newFdf)
                  doc.removeFile(fileId)

                case _ => // update fdf
                  fdf.setId(c.txtFileId.value)
                  // todo: fdf.setMimeType()
              }

              ui.tblFiles.reload()
            }
          }
        }
      }
    }

    ui.miDelete setCommandHandler {
      whenSelected(ui.tblFiles) { fileId =>
        doc.removeFile(fileId)

        ui.tblFiles.reload()
      }
    }

    ui.miSetDefault setCommandHandler {
      whenSelected(ui.tblFiles) { fileId =>
        doc.setDefaultFileId(fileId)

        ui.tblFiles.reload()
      }
    }
  }
}


/**
 * This page is shown as a second page in the flow - next after meta.
 * User may choose whether copy link texts (filled in meta page) into the text fields no 1 and 2.
 * Every language's texts is shown in its tab.
 */
class NewTextDocumentFlowPage2UI extends VerticalLayout with FullSize with Spacing with Margin {
  class TextsUI extends FormLayout with FullSize {
    val txtText1 = new TextField("No 1")
    val txtText2 = new TextField("No 2")

    addComponents(this, txtText1, txtText2)
  }

  val chkCopyI18nMetaTextsToTextFields = new CheckBox("Copy link heading & subheading to text 1 & text 2 in page")
                                           with Immediate 
  val tsTexts = new TabSheet with UndefinedSize with FullSize

  addComponents(this, chkCopyI18nMetaTextsToTextFields, tsTexts)
  setExpandRatio(tsTexts, 1.0f)
}

// todo: prototype, refactor
class NewTextDocumentFlowPage2(metaModel: MetaModel) {
  val ui = new NewTextDocumentFlowPage2UI

  ui.chkCopyI18nMetaTextsToTextFields addClickHandler { reload() }
  reload()

  def isCopyI18nMetaTextsToTextFields = ui.chkCopyI18nMetaTextsToTextFields.booleanValue
  
  def reload() {
    val visible = isCopyI18nMetaTextsToTextFields
    
    ui.tsTexts.removeAllComponents

    for ((language, i18nMeta) <- metaModel.i18nMetas) {
      val textsUI = new ui.TextsUI
      textsUI.txtText1.value = i18nMeta.getHeadline
      textsUI.txtText2.value = i18nMeta.getMenuText

      ui.tsTexts.addTab(textsUI, language.getName, null)

      forlet(textsUI.txtText1, textsUI.txtText2) { t=>
        t setVisible visible
        t setEnabled false
      }
    }    
  }
}











class MetaEditorDepricated(val application: ImcmsApplication, val model: MetaModel) {

  val ui = letret(new MetaUI) { ui =>
    // affects model
//    ui.lytPublication.btnChoosePublisher addClickHandler {
//      application.initAndShow(new OkCancelDialog("Choose publisher") with UserSearchDialog) { dlg =>
//        dlg.wrapOkHandler {
//          dlg.search.selection match {
//            case Seq(user) =>
//              model.meta.setPublisherId(user.getId)
//              ui.lytPublication.lblPublisherName.value = user.getLoginName
//
//            case _ =>
//              model.meta.setPublisherId(null)
//              ui.lytPublication.lblPublisherName.value = "No publisher selected"
//          }
//        }
//      }
//    }

    // does NOT alter meta - only reads its values
//    let(ui.lytPublication) { lyt =>
//      lyt.chkEnd addClickHandler {
//        lyt.chkEnd.booleanValue match {
//          case true =>
//            lyt.calEnd.setEnabled(true)
//            lyt.calEnd.value = model.meta.getPublicationEndDatetime
//          case false =>
//            lyt.calEnd.value = null
//            lyt.calEnd.setEnabled(false)
//        }
//      }
//
//      // fire event
//      lyt.chkEnd.fireClick()
//    }
  }

  /**
   * Validates data and populates model with values.
   * @returns Some(error) in case of a validation error or None.
   */
  def validate(): Option[String] = {
//    ui.lytI18n.tsI18nMetas.getComponentIterator foreach {
//      case i18nMetaUI: I18nMetaLyt with DataType[I18nLanguage] =>
//        let(model.i18nMetas(i18nMetaUI.data)) { i18nMeta =>
//          i18nMeta.setHeadline(i18nMetaUI.txtTitle.value)
//          i18nMeta.setMenuText(i18nMetaUI.txtMenuText.value)
//          i18nMeta.setMenuImageURL(i18nMetaUI.embLinkImage.value)
//        }
//    }

    ui.lytIdentity.txtAlias.value.trim match {
      case "" => model.meta.removeAlis
      case alias =>
        // todo: check alias
        model.meta.setAlias(alias)
    }

    //model.meta.setPublicationStatus(ui.lytPublication.sltStatus.value)

    let(model.meta.getLanguages) { metaLanguages =>
      metaLanguages.clear
      for ((language, enabled) <- model.languages if enabled) metaLanguages.add(language)
    }

//    model.meta.setPublicationStartDatetime(ui.lytPublication.calStart.value)
//    model.meta.setPublicationEndDatetime(
//      if (ui.lytPublication.chkEnd.booleanValue) ui.lytPublication.calEnd.value
//      else null
//    )
//
//    model.meta.setSearchDisabled(ui.lytSearch.chkExclude.value)
//    model.meta.setLinkedForUnauthorizedUsers(ui.lytLink.chkShowToUnauthorizedUser.value)
//    model.meta.setTarget(if (ui.lytLink.chkOpenInNewWindow.booleanValue) "_top" else "_self")

    None
  }
}



/**
 * Meta (doc info) ui.
 */
class MetaUI extends FormLayout /*with UndefinedSize*/ with Margin {

  val lytIdentity = new HorizontalLayout with UndefinedSize with Spacing {
    val txtId = new TextField("Document Id") with Disabled
    val txtName = new TextField("Name")
    val txtAlias = new TextField("Alias")

    setCaption("Identity")
    addComponents(this, txtId, txtName, txtAlias)
  }

  val lytI18n = new VerticalLayout {//with UndefinedSize {
    val tsI18nMetas = new TabSheet// with UndefinedSize
    val btnSettings = new Button("Configure...") with LinkStyle

    setCaption("Appearence")
    addComponents(this, tsI18nMetas, btnSettings)
  }

  val lytLink = new VerticalLayout with UndefinedSize with Spacing {
    val chkOpenInNewWindow = new CheckBox("Open in new window")
    val chkShowToUnauthorizedUser = new CheckBox("Show to unauthorized user")

    setCaption("Link/menu item")
    addComponents(this, chkOpenInNewWindow, chkShowToUnauthorizedUser)
  }

  val lytSearch = new VerticalLayout with UndefinedSize with Spacing {
    val chkExclude = new CheckBox("Exclude this page from internal search")
    val lytKeywords = new HorizontalLayout with Spacing {
      val lblKeywords = new Label("Keywords")
      val txtKeywords = new TextField with Disabled { setColumns(30) }
      val btnEdit = new Button("Edit...") with LinkStyle

      addComponents(this, lblKeywords, txtKeywords, btnEdit)
    }

    setCaption("Search")
    addComponents(this, lytKeywords, chkExclude)
  }

  val lytCategories = new HorizontalLayout with UndefinedSize with Spacing {
    val lblCategories = new Label("Categories")
    val txtCategories = new TextField with Disabled { setColumns(30) }
    val btnEdit = new Button("Edit...") with LinkStyle

    addComponents(this, lblCategories, txtCategories, btnEdit)
  }

  forlet(lytIdentity, lytI18n, lytLink, lytSearch, lytCategories) { c =>
    c.setMargin(true)
    addComponent(c)
  }
}



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/** Meta model */
class MetaModel(val meta: Meta,
                val defaultLanguage: I18nLanguage,
                val languages: MMap[I18nLanguage, Boolean],
                val i18nMetas: Map[I18nLanguage, I18nMeta],
                val versionInfo: Option[DocumentVersionInfo] = Option.empty) {

  val isNewDoc = versionInfo.isEmpty
}


object MetaModel {

  /** Creates meta model for existing document. */
  def apply(id: JInteger): MetaModel = {
    val meta = Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy.getMeta(id).clone
    val versionInfo = Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy.getDocVersionInfo(id)
    val defaultLanguage = Imcms.getI18nSupport.getDefaultLanguage
    val languagesMap = MMap[I18nLanguage, Boolean]()

    Imcms.getI18nSupport.getLanguages foreach { language =>
      languagesMap.put(language, meta.getLanguages.contains(language))
    }
    languagesMap.put(defaultLanguage, true)

    val i18nMetas = Imcms.getServices.getDocumentMapper.getI18nMetas(id) map { i18nMeta =>
      i18nMeta.getLanguage -> i18nMeta
    } toMap

    new MetaModel(meta, defaultLanguage, languagesMap, i18nMetas, Some(versionInfo))
  }

  /** Creates meta model for new document. */
  def apply(docType: Int, parentDoc: DocumentDomainObject): MetaModel = {
    val doc = Imcms.getServices.getDocumentMapper.createDocumentOfTypeFromParent(docType, parentDoc, Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(UserDomainObject.DEFAULT_USER_ID))
    val defaultLanguage = Imcms.getI18nSupport.getDefaultLanguage
    val availableLanguages = Imcms.getI18nSupport.getLanguages
    val languages = availableLanguages.zip(Stream.continually(false)).toMap.updated(defaultLanguage, true)
    val i18nMetas = availableLanguages map { language =>
      let(new I18nMeta) { i18nMeta =>
        i18nMeta.setHeadline("")
        i18nMeta.setMenuText("")
        i18nMeta.setMenuImageURL("")
        i18nMeta.setLanguage(language)

        language -> i18nMeta
      }
    } toMap

    new MetaModel(
      doc.getMeta,
      Imcms.getI18nSupport.getDefaultLanguage,
      MMap(languages.toSeq : _*),
      i18nMetas
    )
  }
}