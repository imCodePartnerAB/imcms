package com.imcode.imcms.api

import com.imcode._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{OneInstancePerTest, BeforeAndAfter, WordSpec, FunSuite}
import org.junit.Assert._
import imcode.server.document.textdocument.DocRef
import com.imcode.imcms.test.fixtures.{DocItemFX, DocRefFX}

@RunWith(classOf[JUnitRunner])
class ContentLoopTest extends WordSpec with BeforeAndAfter {

  def mkContentLoop(id: JLong = null, no: JInteger = DocItemFX.DefaultNo, docRef: DocRef = DocRefFX.Default, contentsCount: Int = 0) =
    ContentLoop.builder().id(id).no(no).docRef(docRef) |>> { builder =>
      0 until contentsCount foreach builder.insertContent
    } build()


  "ContentLoop.builder(...)" when {
    "no params are provided" should {
      "create new ContentLoop.Builder instance with default values" in {
        val builder = ContentLoop.builder()
        val contentLoop = builder.build()

        assertNull(contentLoop.getId)
        assertNull(contentLoop.getNo)
        assertNull(contentLoop.getDocRef)

        assertNotNull(contentLoop.getAllContents)
        assertNotNull(contentLoop.getEnabledContents)

        assertTrue(contentLoop.getAllContents.isEmpty)
        assertTrue(contentLoop.getEnabledContents.isEmpty)
      }
    }

    "an esxisting ContentLoop provided as a parameted" should {
      "create new ContentLoop.Builder instance with values base on provided ContentLoop" in {
        val contentLoopTemplate = ContentLoop.builder()
          .id(1)
          .no(2)
          .docRef(DocRef.of(1001, 0))
          .insertContent(0)
          .insertContent(1)
          .insertContent(2)
          .disableContent(0)
          .build()

        val contentLoop = ContentLoop.builder(contentLoopTemplate).build()

        assertEquals(1l, contentLoop.getId)
        assertEquals(2, contentLoop.getNo)
        assertEquals(DocRef.of(1001, 0), contentLoop.getDocRef)

        assertEquals(3, contentLoop.getAllContents.size)
        assertEquals(2, contentLoop.getEnabledContents.size)
      }
    }
  }


  "ContentLoop" should {
    "add first content" in {
      val loop = mkContentLoop(contentsCount = 10)

      loop.addFirstContent() |> { loopAndContent =>
        val updatedLoop = loopAndContent._1
        val content = loopAndContent._2

        assertEquals(10, loop.getAllContents.size)
        assertEquals(10, loop.getEnabledContents.size)

        assertNotSame(updatedLoop, loop)

        assertEquals(11, updatedLoop.getAllContents.size)
        assertEquals(11, updatedLoop.getEnabledContents.size)

        assertEquals(10, content.getNo)
        assertSame(content, updatedLoop.getAllContents.get(0))

        updatedLoop.findContentWithIndexByNo(10) |> { contentAndIndex =>
          assertTrue(contentAndIndex.isPresent)
          assertSame(content, contentAndIndex.get._1)
          assertSame(0, contentAndIndex.get._2)
        }
      }
    }


    "add last content" in {
      val loop = mkContentLoop(contentsCount = 10)

      loop.addLastContent() |> { loopAndContent =>
        val updatedLoop = loopAndContent._1
        val content = loopAndContent._2

        assertEquals(10, loop.getAllContents.size)
        assertEquals(10, loop.getEnabledContents.size)

        assertNotSame(updatedLoop, loop)

        assertEquals(11, updatedLoop.getAllContents.size)
        assertEquals(11, updatedLoop.getEnabledContents.size)

        assertEquals(10, content.getNo)
        assertSame(content, updatedLoop.getAllContents.get(10))

        updatedLoop.findContentWithIndexByNo(10) |> { contentAndIndex =>
          assertTrue(contentAndIndex.isPresent)
          assertSame(content, contentAndIndex.get._1)
          assertSame(10, contentAndIndex.get._2)
        }
      }
    }
  }

  "ContentLoop" when {
    "add (insert) content before non-existing content" should {
      "return Absent" in {
        val loop = mkContentLoop(contentsCount = 10)
        val content = Content.builder().no(DocItemFX.VacantNo).build()

        assertFalse(loop.addContentBefore(content).isPresent)
      }
    }

    "add (insert) content after non-existing content" should {
      "return Absent" in {
        val loop = mkContentLoop(contentsCount = 10)
        val content = Content.builder().no(DocItemFX.VacantNo).build()

        assertFalse(loop.addContentAfter(content).isPresent)
      }
    }

    "move non-existing content backward" should {
      "return Absent" in {
        val loop = mkContentLoop(contentsCount = 10)
        val content = Content.builder().no(DocItemFX.VacantNo).build()

        assertFalse(loop.moveContentBackward(content).isPresent)
      }
    }

    "move non-existing content forward" should {
      "return Absent" in {
        val loop = mkContentLoop(contentsCount = 10)
        val content = Content.builder().no(DocItemFX.VacantNo).build()

        assertFalse(loop.moveContentForward(content).isPresent)
      }
    }


    "disable non-existing content" should {
      "return Absent" in {
        val loop = mkContentLoop(contentsCount = 10)
        val content = Content.builder().no(DocItemFX.VacantNo).build()

        assertEquals(10, loop.getAllContents.size)
        assertEquals(10, loop.getEnabledContents.size)

        assertFalse(loop.disableContent(content).isPresent)

        assertEquals(10, loop.getAllContents.size)
        assertEquals(10, loop.getEnabledContents.size)
      }
    }

    "enable non-existing content" should {
      "return Absent" in {
        val loop = mkContentLoop(contentsCount = 10)
        val content = Content.builder().no(DocItemFX.VacantNo).build()

        assertEquals(10, loop.getAllContents.size)
        assertEquals(10, loop.getEnabledContents.size)

        assertFalse(loop.enableContent(content).isPresent)

        assertEquals(10, loop.getAllContents.size)
        assertEquals(10, loop.getEnabledContents.size)
      }
    }


    "disabling existing content" should {
      "return Present<ContentLoop>" in {
        val loop = mkContentLoop(contentsCount = 10)
        val updatedLoop =
          4.to(7).foldLeft(loop) { case (currentLoop, contentNo) =>
            currentLoop.findContentByNo(contentNo).get |> currentLoop.disableContent |> { updatedLoopOpt =>
              assertTrue(updatedLoopOpt.isPresent)
              assertNotSame(currentLoop, updatedLoopOpt.get)

              updatedLoopOpt.get
            }
          }

        assertEquals(10, loop.getAllContents.size)
        assertEquals(10, loop.getEnabledContents.size)

        assertEquals(10, updatedLoop.getAllContents.size)
        assertEquals(6, updatedLoop.getEnabledContents.size)

        4 to 7 foreach { contentNo =>
          assertFalse(updatedLoop.findContentByNo(contentNo).get.isEnabled)
        }

        0.until(10).toSet.diff(4 to 7 toSet) foreach { contentNo =>
          assertTrue(updatedLoop.findContentByNo(contentNo).get.isEnabled)
        }

        4.to(7).foldLeft(updatedLoop) { case (currentLoop, contentNo) =>
          currentLoop.findContentByNo(contentNo).get |> currentLoop.enableContent |> { updatedLoopOpt =>
            assertTrue(updatedLoopOpt.isPresent)
            assertNotSame(currentLoop, updatedLoopOpt.get)

            updatedLoopOpt.get
          }
        } |> { restoredLoop =>
          assertEquals(10, restoredLoop.getAllContents.size)
          assertEquals(10, restoredLoop.getEnabledContents.size)

          0 until 10 foreach { contentNo =>
            assertTrue(restoredLoop.findContentByNo(contentNo).get.isEnabled)
          }
        }
      }
    }


    "add (insert) content before existing content" should {
      "return updated loop and inserted content packed in 'Present'" in {
        val loop = mkContentLoop(contentsCount = 10)
        val content = loop.findContentByNo(5).get

        loop.addContentBefore(content) |> { loopAndContent =>
          assertTrue(loopAndContent.isPresent)

          val updatedLoop = loopAndContent.get._1
          val newContent = loopAndContent.get._2

          assertEquals(10, loop.getAllContents.size)
          assertEquals(10, loop.getEnabledContents.size)

          assertNotSame(updatedLoop, loop)

          assertEquals(11, updatedLoop.getAllContents.size)
          assertEquals(11, updatedLoop.getEnabledContents.size)

          assertEquals(10, newContent.getNo)
          assertSame(newContent, updatedLoop.getAllContents.get(5))
          assertSame(content, updatedLoop.getAllContents.get(6))

          updatedLoop.findContentWithIndexByNo(10) |> { contentAndIndex =>
            assertTrue(contentAndIndex.isPresent)
            assertSame(newContent, contentAndIndex.get._1)
            assertSame(5, contentAndIndex.get._2)
          }
        }
      }
    }

    "add (insert) content after existing content" should {
      "return updated loop and inserted content packed in 'Present'" in {
        val loop = mkContentLoop(contentsCount = 10)
        val content = loop.findContentByNo(5).get

        loop.addContentAfter(content) |> { loopAndContent =>
          assertTrue(loopAndContent.isPresent)

          val updatedLoop = loopAndContent.get._1
          val newContent = loopAndContent.get._2

          assertEquals(10, loop.getAllContents.size)
          assertEquals(10, loop.getEnabledContents.size)

          assertNotSame(updatedLoop, loop)

          assertEquals(11, updatedLoop.getAllContents.size)
          assertEquals(11, updatedLoop.getEnabledContents.size)

          assertEquals(10, newContent.getNo)
          assertSame(newContent, updatedLoop.getAllContents.get(6))
          assertSame(content, updatedLoop.getAllContents.get(5))

          updatedLoop.findContentWithIndexByNo(10) |> { contentAndIndex =>
            assertTrue(contentAndIndex.isPresent)
            assertSame(newContent, contentAndIndex.get._1)
            assertSame(6, contentAndIndex.get._2)
          }
        }
      }
    }
  }
}

//  "ContentLoop" should {
//    "provide a result" which {
//      "mathces blah-1" in {}
//      "mathces blah-2" in {}
//    }
//  }