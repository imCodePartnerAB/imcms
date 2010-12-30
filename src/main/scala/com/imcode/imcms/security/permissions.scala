package com.imcode.imcms.security

sealed trait Permission
case object PermissionGranted extends Permission
case class PermissionDenied(reason: String) extends Permission