package com.imcode.imcms.admin.doc.projection.filter

// Last search result:

sealed trait FilterParametersKind

case object CreatedByCurrentUser extends FilterParametersKind
case object ApprovedByCurrentUser extends FilterParametersKind

case object CreatedInPastWeek extends FilterParametersKind
case object ModifiedButNotCreatedInPastWeek extends FilterParametersKind

case object ApprovedAndPublishedToBeArchivedWithinOneWeek extends FilterParametersKind
case object ApprovedPublishedAndArchivedToBeUnpublishedWithinOneWeek extends FilterParametersKind
case object NewApprovedPublishedAndArchivedNotModifiedInLastSixMonth extends FilterParametersKind

//Recent Changes:
//-----------------------------------
//Documents created in the past week
//Documents modified, but not created in the past week
//
//
//Reminders:
//-----------------------------------
//Approved and published documents to be archived within one week
//Approved, published and archived documents to be unpublished within one week
//New, approved, published and archived documents not modified in the last six months
