package com.imcode
package imcms.mapping

import imcode.server.document.FileDocumentDomainObject
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test.{Base}

@RunWith(classOf[JUnitRunner])
class SaveSpec extends WordSpec with ShouldMatchers {

  "A new file document with a single file" should {
    val doc = new FileDocumentDomainObject

    "has default file" in {
    }
  }
}