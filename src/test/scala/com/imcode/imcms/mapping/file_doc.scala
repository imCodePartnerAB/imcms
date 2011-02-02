package com.imcode
package imcms.mapping

import scala.collection.JavaConversions._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{WordSpec}
import DocumentStoringVisitor.getFilenameForFileDocumentFile
import imcms.api.DocumentVersion.WORKING_VERSION_NO

@RunWith(classOf[JUnitRunner])
class FilenameSpeck extends WordSpec {

  implicit def unbox(value: JInteger) = value.intValue

  "the result of getFilenameForFileDocumentFile invocation" when {
    "doc version is a working version and file id is a blank" should {
      "be 'docId'" in {
        expect("1001") {
          getFilenameForFileDocumentFile(1001, WORKING_VERSION_NO, null)
        }

        expect("1111") {
          getFilenameForFileDocumentFile(1111, WORKING_VERSION_NO, "")
        }
      }
    }

    "doc version is a working version and file id is *not* a blank" should {
      "be 'docId.fileId'" in {
        expect("1001.10") {
          getFilenameForFileDocumentFile(1001, WORKING_VERSION_NO, 10.toString)
        }

        expect("1234.56") {
          getFilenameForFileDocumentFile(1234, WORKING_VERSION_NO, 56.toString)
        }

        expect("1212.ok") {
          getFilenameForFileDocumentFile(1212, WORKING_VERSION_NO, "ok")
        }
      }
    }

    "doc version is not a working version and file id is a blank" should {
      "be 'docId_docVersionNo'" in {
        expect("1001_3") {
          getFilenameForFileDocumentFile(1001, 3, null)
        }

        expect("1111_5") {
          getFilenameForFileDocumentFile(1111, 5, "")
        }
      }
    }

    "doc version is not a working version and file id is *not* a blank" should {
      "be 'docId_docVersionNo.fileId'" in {
        expect("1001_4.2") {
          getFilenameForFileDocumentFile(1001, 4, 2.toString)
        }

        expect("3210_6.11") {
          getFilenameForFileDocumentFile(3210, 6, 11.toString)
        }

        expect("3412_2.abc") {
          getFilenameForFileDocumentFile(3412, 2, "abc")
        }
      }
    }
  }
}