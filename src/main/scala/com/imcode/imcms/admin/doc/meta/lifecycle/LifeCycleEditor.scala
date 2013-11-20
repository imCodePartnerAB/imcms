package com.imcode
package imcms
package admin.doc.meta.lifecycle

import scala.collection.JavaConverters._
import com.imcode.imcms.admin.access.user.{UserSingleSelectWidget, UserSingleSelect}
import com.imcode.imcms.api.{DocumentVersion, Document, Meta}

import java.util.Date
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import _root_.imcode.server.document.LifeCyclePhase
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.vaadin.Editor


class LifeCycleEditor(meta: Meta) extends Editor with ImcmsServicesSupport {

  case class Data(
    publicationStatus: Document.PublicationStatus,
    publicationStartDt: Date,
    archiveDt: Option[Date],
    publicationEndDt: Option[Date],
    publisher: Option[UserDomainObject],
    versionNo: Int,
    createdDt: Date,
    modifiedDt: Date,
    creator: Option[UserDomainObject],
    modifier: Option[UserDomainObject]
  )


  override val widget = new LifeCycleEditorWidget |>> { w =>
    for (phase <- LifeCyclePhase.ALL) {
      new Label with UndefinedSize |>> { lbl =>
        lbl.setCaption(s"doc_publication_phase.$phase".i)
        lbl.setIcon(Theme.Icon.Doc.phase(phase))
      } |> w.publication.lytPhase.addComponent
    }

    w.publication.chkEnd.addValueChangeHandler { _ =>
      w.publication.calEnd.setEnabled(w.publication.chkEnd.checked)

      updatePhase()
    }

    w.publication.chkArchive.addValueChangeHandler { _ =>
      w.publication.calArchive.setEnabled(w.publication.chkArchive.checked)

      updatePhase()
    }

    w.publication.sltStatus.addValueChangeHandler { _ =>
      updatePhase()
    }

    w.publication.calStart.addValueChangeHandler { _ =>
      updatePhase()
    }

    w.publication.calEnd.addValueChangeHandler { _ =>
      updatePhase()
    }

    w.publication.calArchive.addValueChangeHandler { _ =>
      updatePhase()
    }
  }

  resetValues()

  override def resetValues() {
    // version
    val (versionsNos, defaultVersionNo) = meta.getId match {
      case null =>
        Seq[JInteger](DocumentVersion.WORKING_VERSION_NO) -> DocumentVersion.WORKING_VERSION_NO

      case id =>
        val versionInfo = imcmsServices.getDocumentMapper.getDocumentVersionInfo(id)
        versionInfo.getVersions.asScala.map(_.getNo) -> versionInfo.getDefaultVersion.getNo
    }

    widget.info.ussCreator.selection = meta.getCreatorId.asOption.map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))
    widget.info.ussModifier.selection = None
    widget.publication.ussPublisher.selection = meta.getPublisherId.asOption.map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))

    widget.publication.sltVersion.removeAllItems()
    versionsNos.foreach(no => widget.publication.sltVersion.addItem(no, no.toString))
    widget.publication.sltVersion.setItemCaption(DocumentVersion.WORKING_VERSION_NO, "doc.version.working".i)
    widget.publication.sltVersion.select(defaultVersionNo)

    widget.publication.sltStatus.select(meta.getPublicationStatus)

    widget.publication.calStart.value = meta.getPublicationStartDatetime.asOption.getOrElse(new Date)
    widget.publication.calEnd.value = meta.getPublicationEndDatetime
    widget.publication.calArchive.value = meta.getArchivedDatetime

    widget.publication.chkEnd.checked = meta.getPublicationEndDatetime != null
    widget.publication.chkArchive.checked = meta.getPublicationEndDatetime != null

    updatePhase()
  }

  private def updatePhase() {
    val doc = new TextDocumentDomainObject() |>> { doc =>
      doc.setPublicationStartDatetime(widget.publication.calStart.value)
      doc.setPublicationEndDatetime(if (widget.publication.chkEnd.checked) widget.publication.calEnd.value else null)
      doc.setArchivedDatetime(if (widget.publication.chkArchive.checked) widget.publication.calArchive.value else null)
      doc.setPublicationStatus(widget.publication.sltStatus.value)
    }

    val activePhase = doc.getLifeCyclePhase

    for ((phase, index) <- LifeCyclePhase.ALL.zipWithIndex) {
      widget.publication.lytPhase.getComponent(index).setEnabled(phase == activePhase)
    }
  }

  override def collectValues(): ErrorsOrData = {
    val errors = scala.collection.mutable.Buffer.empty[String]

    if (widget.publication.chkArchive.checked && widget.publication.calArchive.valueOpt.isEmpty) {
      errors.append("Document archive is enabled but date is not specified")
    }

    if (widget.publication.chkEnd.checked && widget.publication.calEnd.valueOpt.isEmpty) {
      errors.append("Document expiration is enabled but date is not specified")
    }

    if (errors.nonEmpty) {
      Left(errors.toSeq)
    } else {
      Right(
        Data(
          widget.publication.sltStatus.value,
          widget.publication.calStart.value,
          when(widget.publication.chkArchive.checked)(widget.publication.calArchive.value),
          when(widget.publication.chkEnd.checked)(widget.publication.calEnd.value),
          widget.info.ussCreator.selection,
          widget.publication.sltVersion.value.intValue,
          widget.info.dCreated.calDate.value,
          widget.info.dModified.calDate.value,
          widget.info.ussCreator.selection,
          widget.info.ussModifier.selection
        )
      )
    }
  }
}
