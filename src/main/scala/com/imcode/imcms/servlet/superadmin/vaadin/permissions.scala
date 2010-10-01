package com.imcode.imcms.servlet.superadmin.vaadin.permissions

import com.imcode.imcms.servlet.superadmin.vaadin.filemanager._
import com.imcode.imcms.servlet.superadmin.vaadin.template._
import java.lang.{Class => JClass, Boolean => JBoolean, Integer => JInteger}
import scala.collection.JavaConversions._
import com.imcode._
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcms.api.{CategoryType, SystemProperty, IPAccess, Document}
import imcms.mapping.CategoryMapper
import imcms.servlet.superadmin.AdminSearchTerms
import com.imcode.imcms.api.Document.PublicationStatus
import imcms.servlet.superadmin.vaadin.ChatTopic.Message
import imcode.util.Utility
import imcode.server.user._
import imcode.server.{SystemData, Imcms}
import java.util.{Date, Collection => JCollection}
import com.vaadin.ui.Layout.MarginInfo
import com.imcode.imcms.servlet.superadmin.vaadin.ui._
import com.imcode.imcms.servlet.superadmin.vaadin.ui.AbstractFieldWrapper._
import java.util.concurrent.atomic.AtomicReference
import scala.actors.Actor._
import scala.actors._
import imcode.server.document.textdocument.TextDocumentDomainObject
import imcode.server.document.{TemplateDomainObject, CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import java.io.{ByteArrayInputStream, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{ThemeResource, UserError}


class UserDialogContent extends FormLayout {
  val txtLogin = new TextField
  val txtPassword = new TextField("4-16 characters") { setSecret(true) }
  val txtVerifyPassword = new TextField("4-16 characters (retype)") { setSecret(true) }
  val txtFirstName = new TextField("First")
  val txtLastName = new TextField("Last")
  val chkActivated = new CheckBox("Activated")
  val sltUILanguage = new Select("Interface language") {
    setNullSelectionAllowed(false)
    setRequired(true)
  }
  val lstRoles = new ListSelect("Roles") {
    setMultiSelect(true)
    setNullSelectionAllowed(false)
  }
  val lytPassword = new HorizontalLayoutView("Password") {
    //setSpacing(true)
    //addComponent(new VerticalLayout {
      addComponent(txtPassword)
    //})
    //addComponent(new VerticalLayout {
      addComponent(txtVerifyPassword)
    //})
  }

  val lytName = new HorizontalLayoutView("Name") {
    //addComponent(new VerticalLayout {
      addComponent(txtFirstName)
    //})
    //addComponent(new VerticalLayout {
      addComponent(txtLastName)
    //})
  }

  val lytLogin = new HorizontalLayout {
    setCaption("Login")
    addComponents(this, txtLogin, chkActivated)
    setComponentAlignment(chkActivated, Alignment.MIDDLE_LEFT)
  }

  val btnContacts = new Button("Edit...") {
    setStyleName(Button.STYLE_LINK)
    setIcon(new ThemeResource("icons/16/globe.png"))
  }

  val lytContacts = new HorizontalLayout {
    setCaption("Contacts")
    addComponent(btnContacts)
  }

  forlet(lytLogin, txtPassword, txtVerifyPassword, lstRoles, sltUILanguage) { _ setRequired true }

  addComponents(this, lytLogin, lytPassword, lytName, sltUILanguage, lstRoles, lytContacts)

//    val txtUsername = new TextField("Username")
//    val txtPassword = new TextField("4-16 characters")
//    val txtVerifyPassword = new TextField("4-16 characters (retype)")
//    val txtFirstName = new TextField("Firstn name")
//    val txtLastName = new TextField("Last name")
//
////    val txtTitle = new TextField("Title")
////    val txtCompany = new TextField("Company")
////    val txtAddress = new TextField("Address")
////    val txtZip = new TextField("Zip")
////    val txtCity = new TextField("City")
////
////    val txtEmail = new TextField("Email")
//    val chkActivated = new CheckBox("Activated")
//
//    val lstRoles = new ListSelect("User roles")
//    val lstManagedRoles = new ListSelect("Managed roles")

//    forlet(lstRoles, lstManagedRoles) {_ setColumns 3}
//
//    val lytRoles = new HorizontalLayout {
//      setCaption("Roles")
//      addComponent(new VerticalLayoutView {
//        addComponent(lstRoles)
//      })
//
//      addComponent(new VerticalLayoutView {
//          addComponent(lstManagedRoles)
//      })
//    }

//           val btnAdd = new Button("Add")
//        val btnRemove = new Button("Remove")
//
//    val lytPhoneNumbers = new VerticalLayout {
//      setSizeUndefined
//      setCaption("Phone numbers")
//      val lytButtons = new HorizontalLayout {
//        setSizeUndefined
//        setSpacing(true)
//        addComponents(this, btnAdd, btnRemove)
//      }
//
//      val tblPhoneNumbers = new Table {
//        addContainerProperties(this, ("Kind", classOf[String], null), ("Nr", classOf[String], null))
//        setEditable(true)
//        setImmediate(true)
//        setPageLength(2)
//      }
//
//      addComponents(this, tblPhoneNumbers, lytButtons)

//      btnAdd addListener {
//        val id = 1 + tblPhoneNumbers.getItemIds.map(_.asInstanceOf[Int]).foldLeft(0){_ max _}
//        tblPhoneNumbers.addItem(Array("", ""), Int box id)
//        println("CLICK")
//
//      }
//
//
//      btnRemove addListener {
//        tblPhoneNumbers.getValue match {
//          case null =>
//          case id: JInteger => tblPhoneNumbers removeItem id
//          case ids: Seq[JInteger] => ids foreach { tblPhoneNumbers removeItem _}
//        }
//
//        println("CLICK")
//      }


//    val frmRoles = new Form {
//      setCaption("Roles")
//      getLayout.addComponent(lytRoles)
//    }
}