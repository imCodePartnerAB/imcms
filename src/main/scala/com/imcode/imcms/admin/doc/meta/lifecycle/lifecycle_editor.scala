package com.imcode
package imcms
package admin.doc.meta.lifecycle

import scala.collection.JavaConverters._
import com.imcode.imcms.admin.access.user.{UserSingleSelectUI, UserSingleSelect}
import com.imcode.imcms.api.{DocumentVersion, Document, Meta}
import com.imcode.imcms.vaadin._
import _root_.imcode.server.user.UserDomainObject
import java.util.Date
import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import imcode.server.document.textdocument.TextDocumentDomainObject
import com.vaadin.terminal.ThemeResource

// todo: ??? remember lytDate.chkEnd date when uncheked ???
class LifeCycleEditor(meta: Meta) extends Editor with ImcmsServicesSupport {

  case class Data(
    publicationStatus: Document.PublicationStatus,
    publicationStart: Date,
    publicationEnd: Option[Date],
    publisher: Option[UserDomainObject],
    versionNo: Int,
    created: Date,
    modified: Date,
    creator: Option[UserDomainObject],
    modifier: Option[UserDomainObject]
  )

  val ui = new LifeCycleEditorUI |>> { ui =>
    ui.publication.chkEnd.addValueChangeHandler {
      ui.publication.calEnd.setEnabled(ui.publication.chkEnd.checked)
      updatePhase()
    }

    ui.publication.chkArchive.addValueChangeHandler {
      ui.publication.calArchive.setEnabled(ui.publication.chkArchive.checked)
      updatePhase()
    }

    ui.publication.sltStatus.addValueChangeHandler {
      updatePhase()
    }

    ui.publication.calStart.addValueChangeHandler {
      updatePhase()
    }

    ui.publication.calEnd.addValueChangeHandler {
      updatePhase()
    }
    ui.publication.calArchive.addValueChangeHandler {
      updatePhase()
    }
  }

  def resetValues() {
    // version
    val (versionsNos, defaultVersionNo) = meta.getId match {
      case null =>
        Seq[JInteger](DocumentVersion.WORKING_VERSION_NO) -> DocumentVersion.WORKING_VERSION_NO

      case id =>
        val versionInfo = imcmsServices.getDocumentMapper.getDocumentVersionInfo(id)
        versionInfo.getVersions.asScala.map(_.getNo) -> versionInfo.getDefaultVersion.getNo
    }

    ui.info.ussCreator.selection = Option(meta.getCreatorId).map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))
    ui.info.ussModifier.selection = None
    ui.publication.ussPublisher.selection = Option(meta.getPublisherId).map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))

    ui.publication.sltVersion.removeAllItems()
    versionsNos.foreach(no => ui.publication.sltVersion.addItem(no, no.toString))
    ui.publication.sltVersion.setItemCaption(DocumentVersion.WORKING_VERSION_NO, "doc.version.working".i)
    ui.publication.sltVersion.select(defaultVersionNo)

    ui.publication.sltStatus.select(meta.getPublicationStatus)

    ui.publication.calStart.value = meta.getPublicationStartDatetime |> opt getOrElse new Date
    ui.publication.calEnd.value = meta.getPublicationEndDatetime
    ui.publication.calArchive.value = meta.getArchivedDatetime

    ui.publication.chkEnd.checked = meta.getPublicationEndDatetime != null
    ui.publication.chkArchive.checked = meta.getPublicationEndDatetime != null

    updatePhase()
  }

  private def updatePhase() {
    val doc = new TextDocumentDomainObject() |>> { doc =>
      doc.setPublicationStartDatetime(ui.publication.calStart.value)
      doc.setPublicationEndDatetime(if (ui.publication.chkEnd.checked) ui.publication.calEnd.value else null)
      doc.setArchivedDatetime(if (ui.publication.chkArchive.checked) ui.publication.calArchive.value else null)
      doc.setPublicationStatus(ui.publication.sltStatus.value)
    }

    val phase = doc.getLifeCyclePhase

    ui.publication.lblPhase.setCaption("doc.publication.phase.id.%s".format(phase).i)
    ui.publication.lblPhase.setIcon(new ThemeResource("icons/docstatus/%s.gif".format(phase)))
  }

  def collectValues(): ErrorsOrData = Right(
    Data(
      ui.publication.sltStatus.value,
      ui.publication.calStart.value,
      ui.publication.calEnd.valueOpt,
      ui.info.ussCreator.selection,
      ui.publication.sltVersion.value.intValue,
      ui.info.dCreated.calDate.value,
      ui.info.dModified.calDate.value,
      ui.info.ussCreator.selection,
      ui.info.ussModifier.selection
    )
  )

  resetValues()
}


class LifeCycleEditorUI extends VerticalLayout with Spacing with FullWidth {

  class DateUI(caption: String, ussUI: UserSingleSelectUI) extends HorizontalLayoutUI(caption, margin = false) {
    val calDate = new PopupDateField with MinuteResolution with Now
    val lblBy = new Label("by") with UndefinedSize

    this.addComponents(calDate, lblBy, ussUI)
  }

  object info {
    val ussCreator = new UserSingleSelect
    val ussModifier = new UserSingleSelect

    val dCreated = new DateUI("Created", ussCreator.ui)
    val dModified = new DateUI("Modified", ussModifier.ui)
  }

  object publication {
    val lblPhase = new Label
    val ussPublisher = new UserSingleSelect

    val sltStatus = new Select("doc.publication.status.select".i) with SingleSelect[Document.PublicationStatus] with NoNullSelection with Immediate {
      addItem(Document.PublicationStatus.NEW, "doc.publication.status.id.new".i, new ThemeResource("icons/docstatus/new.gif"))
      addItem(Document.PublicationStatus.APPROVED, "doc.publication.status.id.approved".i, new ThemeResource("icons/docstatus/approved.gif"))
      addItem(Document.PublicationStatus.DISAPPROVED, "doc.publication.status.id.disapproved".i, new ThemeResource("icons/docstatus/disapproved.gif"))
//
//      override def getItemIcon(itemId: AnyRef): ThemeResource = itemId.asInstanceOf[Document.PublicationStatus] |> { status =>
//        new ThemeResource("icons/docstatus/%s.gif".format(status))
//      }
//
//      override def getItemCaption(itemId: AnyRef): String = itemId.asInstanceOf[Document.PublicationStatus] |> { status =>
//        "doc.publication.status.id.%s".format(status).i
//      }
    }

    val sltVersion = new Select("Version") with SingleSelect[DocVersionNo] with NoNullSelection

    val calStart = new PopupDateField with MinuteResolution with Immediate with Now
    val calArchive = new PopupDateField with MinuteResolution with Immediate
    val calEnd = new PopupDateField with MinuteResolution with Immediate
    val chkStart = new CheckBox("start") with Checked with ReadOnly // decoration, always read-only
    val chkArchive = new CheckBox("archive") with Immediate with AlwaysFireValueChange
    val chkEnd = new CheckBox("end") with Immediate with AlwaysFireValueChange

    ussPublisher.ui.setCaption("Publisher")
  }

  private val pnlInfo = new Panel("Info") with FullWidth {
    val content = new FormLayout with Margin with FullWidth
    setContent(content)

    content.addComponents(info.dCreated, info.dModified)
  }

  private val pnlPublication = new Panel("Publication") with FullWidth {
    val content = new FormLayout with Margin with FullWidth
    setContent(content)

    val lytDate = new GridLayout(2, 2) with Spacing {
      setCaption("Date")

      this.addComponents(
        publication.chkStart, publication.calStart,
        publication.chkArchive, publication.calArchive,
        publication.chkEnd, publication.calEnd
      )
    }

    private val lytPhase = new HorizontalLayout |>> { lyt =>
      lyt.setCaption("Stage")
      lyt.addComponent(publication.lblPhase)
    }

    content.addComponents(lytPhase, publication.sltStatus, publication.sltVersion, lytDate, publication.ussPublisher.ui)
  }

  this.addComponents(pnlInfo, pnlPublication)
}