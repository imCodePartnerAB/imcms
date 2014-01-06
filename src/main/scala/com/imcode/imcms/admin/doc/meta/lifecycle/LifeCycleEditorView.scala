package com.imcode
package imcms
package admin.doc.meta.lifecycle

import com.imcode.imcms.admin.access.user.{UserSingleSelectView, UserSingleSelect}
import com.imcode.imcms.api.Document
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class LifeCycleEditorView extends VerticalLayout with Spacing with FullWidth {

  class DateComponent(caption: String, ussUI: UserSingleSelectView) extends HorizontalLayoutUI(caption, margin = false) {
    val calDate = new PopupDateField with MinuteResolution with Now
    val lblBy = new Label("by") with UndefinedSize

    this.addComponents(calDate, lblBy, ussUI)
  }

  object info {
    val ussCreator = new UserSingleSelect
    val ussModifier = new UserSingleSelect

    val dCreated = new DateComponent("Created", ussCreator.view)
    val dModified = new DateComponent("Modified", ussModifier.view)
  }

  object publication {
    val ussPublisher = new UserSingleSelect

    val sltStatus = new Select("doc_publication_status".i) with SingleSelect[Document.PublicationStatus] with NoNullSelection with Immediate {
      addItem(Document.PublicationStatus.NEW, "doc_publication_status.new".i)
      addItem(Document.PublicationStatus.DISAPPROVED, "doc_publication_status.disapproved".i)
      addItem(Document.PublicationStatus.APPROVED, "doc_publication_status.approved".i)
    }

    val lytPhase = new HorizontalLayout with Spacing with UndefinedSize |>> { lyt =>
      lyt.setCaption("Status")
      lyt.setStyleName("im-border-top-doc_lifecycle")
    }

    val sltVersion = new Select("Version") with SingleSelect[DocVersionNo] with NoNullSelection

    val calStart = new PopupDateField with MinuteResolution with Immediate with Now
    val calArchive = new PopupDateField with MinuteResolution with Immediate
    val calEnd = new PopupDateField with MinuteResolution with Immediate
    val chkStart = new CheckBox("start") with Checked with ReadOnly // decoration, always read-only
    val chkArchive = new CheckBox("archive") with Immediate with AlwaysFireValueChange[JBoolean]
    val chkEnd = new CheckBox("end") with Immediate with AlwaysFireValueChange[JBoolean]

    ussPublisher.view.setCaption("Publisher")
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

    content.addComponents(publication.sltStatus, publication.sltVersion, lytDate, publication.ussPublisher.view, publication.lytPhase)
  }

  this.addComponents(pnlInfo, pnlPublication)
}