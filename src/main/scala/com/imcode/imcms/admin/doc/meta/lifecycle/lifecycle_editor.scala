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

    // publication status
    ui.publication.sltStatus.select(meta.getPublicationStatus)

    // dates
    ui.publication.calStart.value = meta.getPublicationStartDatetime |> opt getOrElse new Date
    ui.publication.calEnd.value = meta.getPublicationEndDatetime
    ui.publication.chkEnd.checked = meta.getPublicationEndDatetime != null
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

    addComponentsTo(this, calDate, lblBy, ussUI)
  }

  object info {
    val ussCreator = new UserSingleSelect
    val ussModifier = new UserSingleSelect

    val dCreated = new DateUI("Created", ussCreator.ui)
    val dModified = new DateUI("Modified", ussModifier.ui)
  }

  object publication {
    val ussPublisher = new UserSingleSelect

    val sltStatus = new Select("Status") with SingleSelect[Document.PublicationStatus] with NoNullSelection {
      addItem(Document.PublicationStatus.NEW, "New")
      addItem(Document.PublicationStatus.APPROVED, "Approved")
      addItem(Document.PublicationStatus.DISAPPROVED, "Disapproved")
    }

    val sltVersion = new Select("Version") with SingleSelect[DocVersionNo] with NoNullSelection

    val calStart = new PopupDateField with MinuteResolution with Now
    val calEnd = new PopupDateField with MinuteResolution
    val chkStart = new CheckBox("start") with Checked with ReadOnly // decoration, always read-only
    val chkEnd = new CheckBox("end") with Immediate with AlwaysFireValueChange

    ussPublisher.ui.setCaption("Publisher")
  }

  private val pnlInfo = new Panel("Info") with FullWidth {
    val content = new FormLayout with Margin with FullWidth
    setContent(content)

    addComponentsTo(content, info.dCreated, info.dModified)
  }

  private val pnlPublication = new Panel("Publication") with FullWidth {
    val content = new FormLayout with Margin with FullWidth
    setContent(content)

    val lytDate = new GridLayout(2, 2) with Spacing {
      setCaption("Date")

      addComponentsTo(this, publication.chkStart, publication.calStart, publication.chkEnd, publication.calEnd)
    }

    addComponentsTo(content, publication.sltStatus, publication.sltVersion, lytDate, publication.ussPublisher.ui)
  }

  addComponentsTo(this, pnlInfo, pnlPublication)
}