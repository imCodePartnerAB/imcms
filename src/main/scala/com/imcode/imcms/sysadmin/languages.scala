//import scala.collection.JavaConversions._
//import com.vaadin.event.ItemClickEvent
//import com.vaadin.terminal.gwt.server.WebApplicationContext
//import com.vaadin.ui._
//import com.vaadin.data.Property
//import com.vaadin.data.Property._
//import imcode.server.Imcms
//import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao}
//import imcode.server.document.DocumentDomainObject
//import com.imcode.imcms.api.{Document}
//import com.imcode.imcms.api.Document.PublicationStatus
//import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper
//import com.vaadin.terminal.UserError
//
////
//// Languages panel
////
//
//class LanguagesPanel {
//
//  val languageDao = Imcms.getSpringBean("languageDao").asInstanceOf[LanguageDao]
//  val systemDao = Imcms.getSpringBean("systemDao").asInstanceOf[SystemDao]
//
//  class ModalWindow(caption: String) extends Window(caption, new FormLayout) {
//    setModal(true)
//  }
//
//  class LanguageModalWindow(caption: String) extends ModalWindow(caption) {
//    val txtId = new TextField("Id")
//    val txtCode = new TextField("Code")
//    val txtName = new TextField("Name")
//    val txtNativeName = new TextField("Native name")
//    val chkEnabled = new CheckBox("Enabled")
//
//    addComponent(txtId)
//    addComponent(txtCode)
//    addComponent(txtName)
//    addComponent(txtNativeName)
//    addComponent(chkEnabled)
//
//    val lytControls = new HorizontalLayout
//    val btnOk = new Button("Save")
//    val btnCancel = new Button("Cancel")
//
//    lytControls addComponent btnOk
//    lytControls addComponent btnCancel
//
//    addComponent(lytControls)
//
//    btnCancel addListener new Button.ClickListener {
//      def buttonClick(clickEvent: Button#ClickEvent) = close
//    }
//
//    getContent.asInstanceOf[FormLayout].setMargin(true)
//  }
//
//  class ConfirmationModalWindow(caption: String, msg: String) extends Window(caption) {
//    val lblMsg = new Label(msg)
//    val lytControls = new HorizontalLayout
//    val btnOk = new Button("Ok")
//    val btnCancel = new Button("Cancel")
//
//    lytControls addComponent btnOk
//    lytControls addComponent btnCancel
//
//    addComponent(lblMsg)
//    addComponent(lytControls)
//
//    btnCancel addListener new Button.ClickListener {
//      def buttonClick(clickEvent: Button#ClickEvent) = close
//    }
//
//    setModal(true)
//  }
//
//  val pnlLanguages = new Panel
//
//
//  val table = new Table
//
//  table setPageLength 10
//  table setSelectable true
//  table setImmediate true
//
//  table.addContainerProperty("Id", classOf[java.lang.Integer],  null)
//  table.addContainerProperty("Code", classOf[String],  null)
//  table.addContainerProperty("Name", classOf[String],  null)
//  table.addContainerProperty("Native name", classOf[String],  null)
//  table.addContainerProperty("Enabled", classOf[java.lang.Boolean],  null)
//  table.addContainerProperty("Default", classOf[java.lang.Boolean],  null)
//
//  val pnlControls = new Panel with Button.ClickListener {
//    val btnNew = new Button("New")
//    val btnEdit = new Button("Edit")
//    val btnSetDefault = new Button("Set default")
//    val btnDelete = new Button("Delete")
//
//    setContent(new HorizontalLayout)
//
//    List(btnNew, btnEdit, btnSetDefault, btnDelete).foreach { btn =>
//      this addComponent btn
//      btn addListener this
//    }
//
//    def buttonClick(clickEvent: Button#ClickEvent) {
//      val defaultLanguageId = Int box systemDao.getProperty("DefaultLanguageId").getValue.toInt
//
//      clickEvent.getButton match {
//
//        case `btnNew` =>
//          val wndEditLanguage = new LanguageModalWindow("New language")
//          val language = new com.imcode.imcms.api.I18nLanguage
//
//          wndEditLanguage.btnOk.addListener(new Button.ClickListener {
//            def isInt(x:Any) = x match {
//              case n: Int => true
//              case s: String => s.nonEmpty && s.forall(_.isDigit)
//              case _ => false
//            }
//
//            def buttonClick(clickEvent: Button#ClickEvent) {
//              if (!isInt(wndEditLanguage.txtId.getValue)) {
//                wndEditLanguage.txtId.setComponentError(new UserError("Id must be an Int"))
//              } else {
//                language.setId(Int box wndEditLanguage.txtId.getValue.asInstanceOf[String].toInt)
//                language.setCode(wndEditLanguage.txtCode.getValue.asInstanceOf[String])
//                language.setName(wndEditLanguage.txtName.getValue.asInstanceOf[String])
//                language.setNativeName(wndEditLanguage.txtNativeName.getValue.asInstanceOf[String])
//                language.setEnabled(wndEditLanguage.chkEnabled.getValue.asInstanceOf[java.lang.Boolean])
//
//                languageDao.saveLanguage(language)
//                wndMain removeWindow wndEditLanguage
//              }
//            }
//          })
//
//          wndMain addWindow wndEditLanguage
//
//        case `btnEdit` =>
//          val languageId = table.getValue.asInstanceOf[java.lang.Integer]
//          val language = languageDao.getById(languageId)
//
//          val wndEditLanguage = new LanguageModalWindow("Edit language")
//
//          wndEditLanguage.txtId.setValue(language.getId)
//          wndEditLanguage.txtId.setEnabled(false)
//          wndEditLanguage.txtCode.setValue(language.getCode)
//          wndEditLanguage.txtName.setValue(language.getName)
//          wndEditLanguage.txtNativeName.setValue(language.getNativeName)
//          wndEditLanguage.chkEnabled.setValue(language.isEnabled)
//          wndEditLanguage.btnOk.addListener(new Button.ClickListener {
//
//
//            def buttonClick(clickEvent: Button#ClickEvent) {
//              language.setCode(wndEditLanguage.txtCode.getValue.asInstanceOf[String])
//              language.setName(wndEditLanguage.txtName.getValue.asInstanceOf[String])
//              language.setNativeName(wndEditLanguage.txtNativeName.getValue.asInstanceOf[String])
//              language.setEnabled(wndEditLanguage.chkEnabled.getValue.asInstanceOf[java.lang.Boolean])
//
//              languageDao.saveLanguage(language)
//              wndMain removeWindow wndEditLanguage
//            }
//          })
//
//          wndMain addWindow wndEditLanguage
//
//        case `btnSetDefault` =>
//          val wndConfirmation = new ConfirmationModalWindow("Confirmation", "Change default language?")
//          wndConfirmation.btnOk.addListener(new Button.ClickListener {
//            def buttonClick(clickEvent: Button#ClickEvent) {
//              val languageId = table.getValue.asInstanceOf[java.lang.Integer]
//              val property = systemDao.getProperty("DefaultLanguageId")
//
//              property.setValue(languageId.toString)
//              systemDao.saveProperty(property)
//            }
//          })
//
//          wndMain addWindow wndConfirmation
//
//        case `btnDelete` =>
//          val wndConfirmation = new ConfirmationModalWindow("Confirmation", "Delete language from the system?")
//          wndConfirmation.btnOk.addListener(new Button.ClickListener {
//            def buttonClick(clickEvent: Button#ClickEvent) {
//              val languageId = table.getValue.asInstanceOf[java.lang.Integer]
//              languageDao.deleteLanguage(languageId)
//            }
//          })
//          wndMain addWindow wndConfirmation
//      }
//    }
//  }
//
//  def resetControls = {
//    val languageId = table.getValue.asInstanceOf[java.lang.Integer]
//
//    if (languageId == null) {
//      pnlControls.btnDelete.setEnabled(false)
//      pnlControls.btnEdit.setEnabled(false)
//      pnlControls.btnSetDefault.setEnabled(false)
//    } else {
//      val defaultLanguageId = Int box systemDao.getProperty("DefaultLanguageId").getValue.toInt
//
//      pnlControls.btnEdit.setEnabled(true)
//      pnlControls.btnDelete.setEnabled(languageId != defaultLanguageId)
//      pnlControls.btnSetDefault.setEnabled(languageId != defaultLanguageId)
//    }
//  }
//
//  table.addListener(new Property.ValueChangeListener {
//      def valueChange(e: ValueChangeEvent) {
//        resetControls
//      }
//  })
//
//  val defaultLanguageId = Int box systemDao.getProperty("DefaultLanguageId").getValue.toInt
//
//  languageDao.getAllLanguages.toList foreach { language =>
//    table.addItem(Array(language.getId, language.getCode, language.getName,
//                        language.getNativeName, language.isEnabled,
//                        Boolean box (language.getId == defaultLanguageId)),
//                  language.getId)
//  }
//
//  pnlLanguages addComponent pnlControls
//  pnlLanguages addComponent table
//
//  resetControls
//  pnlLanguages
//}