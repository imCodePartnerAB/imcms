package com.imcode.imcms.api

import _root_.java.util
import com.imcode._
import scala.collection.JavaConverters._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, WordSpec}
import org.junit.Assert._
import org.hamcrest.CoreMatchers._
import com.imcode.imcms.test.fixtures.{DocItemFX, DocRefFX}

@RunWith(classOf[JUnitRunner])
class ContentLoopSpec extends WordSpec with BeforeAndAfterEach {

  val LoopFx = new {
    val FirstContentIndex = 0
    val LastContentIndex = 9
    val VacantContentIndex = 10
    val ContentsCount = 10

    val NextContentNo = 10
  }

  def mkContentLoop(contentsCount: Int = LoopFx.ContentsCount): Loop = {
    val items = (1 to contentsCount).map(no => LoopEntry.of(no))
    val nextContentNo = contentsCount + 1

    new Loop(nextContentNo, util.Arrays.asList(items : _*))
  }


  "constructor" should {
    "throw IllegalArgumentException if nextContentNo parameter is less than 1" in {
      intercept[IllegalArgumentException] {
        new Loop(0)
      }

      intercept[IllegalArgumentException] {
        new Loop(-1)
      }
    }

    "throw IllegalArgumentException if an item content.no parameter not in range [1..nextContentNo - 1] less than 1" in {
      intercept[IllegalArgumentException] {
        new Loop(1, util.Arrays.asList(LoopEntry.of(1)))
      }

      intercept[IllegalArgumentException] {
        new Loop(3, util.Arrays.asList(LoopEntry.of(1), LoopEntry.of(2), LoopEntry.of(3)))
      }
    }
  }

  "ContentLoop.ops.addContentFirst" should {
    "return a new ContentLoop with a new Content as a first item" in {
      val loop = mkContentLoop()
      val newLoop = loop.ops.addEntryFirst()

      assertThat("new loop has a new item", newLoop.getItems.size(), is(loop.getItems.size() + 1))
      assertThat("new item was added first", newLoop.findEntryByNo(loop.getNextContentNo).get.getIndex, is(0))
      assertThat("added content was assigned loop.nextContentNo value", newLoop.getItems.get(0).getNo, is(loop.getNextContentNo))
      assertThat("new loop nextContentNo was incremented", newLoop.getNextContentNo, is(loop.getNextContentNo + 1))
    }
  }


//  "Add last content" should {
//    "return pair of updated loop with added content with highest index and highest no" in {
//      val loop = mkContentLoop()
//      val ops = new ContentLoopOps(loop)
//
//      ops.addContentLast() |> { loopAndContent =>
//        val updatedLoop = loopAndContent.getLoop()
//        val newContent = loopAndContent.getContent()
//        val expectedNewContentNo = LoopFx.NextContentNo
//        val expectedNewContentIndex = 10
//
//        assertEquals(LoopFx.ContentsCount, loop.getItems.size)
//        assertEquals(LoopFx.ContentsCount, loop.getItems.asScala.count(_.isEnabled))
//
//        assertNotSame(loop, updatedLoop)
//
//        assertEquals(LoopFx.ContentsCount + 1, updatedLoop.getItems.size)
//        assertEquals(LoopFx.ContentsCount + 1, updatedLoop.getItems.asScala.count(_.isEnabled))
//
//        assertEquals(expectedNewContentNo, newContent.getNo)
//
//        updatedLoop.getItems.get(expectedNewContentIndex) |> { lastContent =>
//          assertSame(newContent, lastContent)
//        }
//      }
//    }
//  }
//
//
//  "Add content before other content" which {
//    "does not exist" should {
//      "throw IndexOutOfBoundsException" in {
//        val loop = mkContentLoop()
//        val loopOps = new ContentLoopOps(loop)
//
//        intercept[IndexOutOfBoundsException] {
//          loopOps.addContentBefore(LoopFx.VacantContentIndex)
//        }
//      }
//    }
//
//    "exists" should {
//      "return updated content and inserted content" in {
//        mkContentLoop() |> { loop =>
//          val ops = new ContentLoopOps(loop)
//
//          ops.addContentBefore(LoopFx.LastContentIndex) |> { loopAndContent =>
//            val contents = loopAndContent.getLoop().getItems
//            val content = loopAndContent.getContent()
//
//            assertEquals(LoopFx.ContentsCount + 1, contents.size)
//            assertEquals(LoopFx.NextContentNo, content.getNo)
//            assertSame(content, contents.get(LoopFx.LastContentIndex))
//          }
//        }
//      }
//    }
//  }
//
//
//  "Delete content" which {
//    "does not exist" should {
//      "throw IndexOutOfBoundsException" in {
//        val loop = mkContentLoop()
//        val loopOps = new ContentLoopOps(loop)
//
//        intercept[IndexOutOfBoundsException] {
//          loopOps.disableContent(LoopFx.VacantContentIndex)
//        }
//      }
//    }
//
//    "exists" should {
//      "return updated loop" in {
//        mkContentLoop() |> { loop =>
//          assertTrue("content exists", new ContentLoopOps(loop).findContent(5).isPresent)
//
//          val ops = new ContentLoopOps(loop)
//
//          ops.deleteContent(5) |> { updatedLoop =>
//            val contents = updatedLoop.getItems.asScala
//            assertEquals(LoopFx.ContentsCount - 1, contents.size)
//            assertFalse("content exists", updatedLoop.ops.findContent(5).isPresent)
//          }
//        }
//      }
//    }
//  }
//
//
//  "Add content after other content" which {
//    "does not exist" should {
//      "throw IndexOutOfBoundsException" in {
//        val loop = mkContentLoop()
//        val loopOps = new ContentLoopOps(loop)
//
//        intercept[IndexOutOfBoundsException] {
//          loopOps.addContentAfter(LoopFx.VacantContentIndex)
//        }
//      }
//    }
//
//    "exists" should {
//      "return updated content and inserted content" in {
//        mkContentLoop() |> { loop =>
//          val ops = new ContentLoopOps(loop)
//
//          ops.addContentAfter(LoopFx.FirstContentIndex) |> { loopAndContent =>
//            val contents = loopAndContent.getLoop().getItems
//            val content = loopAndContent.getContent()
//
//            assertEquals(LoopFx.ContentsCount + 1, contents.size)
//            assertEquals(LoopFx.NextContentNo, content.getNo)
//            assertSame(content, contents.get(LoopFx.FirstContentIndex + 1))
//          }
//        }
//      }
//    }
//  }
//
//
//  "Disable content" which {
//    "does not exist" should {
//      "throw IndexOutOfBoundsException" in {
//        val loop = mkContentLoop()
//        val loopOps = new ContentLoopOps(loop)
//
//        intercept[IndexOutOfBoundsException] {
//          loopOps.disableContent(LoopFx.VacantContentIndex)
//        }
//      }
//    }
//
//    "exist" should {
//      "return updated loop" in {
//        mkContentLoop() |> { loop =>
//          val ops = new ContentLoopOps(loop)
//
//          ops.disableContent(5) |> { updatedLoop =>
//            val contents = updatedLoop.getItems.asScala
//            assertEquals(LoopFx.ContentsCount - 1, contents.count(_.isEnabled))
//
//            contents.zipWithIndex.filter { case (content, _) => !content.isEnabled } |> { disabledContents =>
//              assertEquals(1, disabledContents.size)
//              assertEquals(5, disabledContents.head._2)
//            }
//          }
//        }
//      }
//    }
//  }
//
//
//  "Enable content" which {
//    "does not exist" should {
//      "throw IndexOutOfBoundsException" in {
//        val loop = mkContentLoop()
//        val loopOps = new ContentLoopOps(loop)
//
//        intercept[IndexOutOfBoundsException] {
//          loopOps.enableContent(LoopFx.VacantContentIndex)
//        }
//      }
//    }
//
//    "exist" should {
//      "return updated loop" in {
//        ContentLoopService.builder().addContent(0).addContent(1).addContent(2).disableContent(1).build() |> { loop =>
//          loop.getContents |> { contents =>
//            assertTrue(contents.get(0).isEnabled)
//            assertTrue(contents.get(1).isDisabled)
//            assertTrue(contents.get(2).isEnabled)
//          }
//
//          assertEquals(3, loop.getContents.size)
//          assertEquals(2, loop.getContents.asScala.count(_.isEnabled))
//
//          val ops = new ContentLoopOps(loop)
//
//          ops.enableContent(1) |> { updatedLoop =>
//            assertEquals(3, updatedLoop.getItems.size)
//            assertEquals(3, updatedLoop.getItems.asScala.count(_.isEnabled))
//          }
//        }
//      }
//    }
//  }
//
//
//  "Move non existing content forward" should {
//    "throw IndexOutOfBoundsException" in {
//      val loop = mkContentLoop()
//      val loopOps = new ContentLoopOps(loop)
//
//      intercept[IndexOutOfBoundsException] {
//        loopOps.moveContentForward(LoopFx.VacantContentIndex)
//      }
//    }
//  }
//
//
//  "Move existing content forward" should {
//    "return the loop unchanged" when {
//      "the content is the last" in {
//        mkContentLoop() |> { loop =>
//          assertEquals(loop, new ContentLoopOps(loop).moveContentForward(LoopFx.LastContentIndex))
//        }
//      }
//
//      "all following contents are disabled" in {
//        ContentLoopService.builder(mkContentLoop()) |> { builder =>
//          6 to 9 foreach builder.disableContent
//          builder.build()
//        } |> { loop =>
//          assertEquals(loop, loop.ops.moveContentForward(5))
//        }
//      }
//    }
//  }
//
//
//  "Move existing content forward" should {
//    "swap the content with a next content and return updated loop" in {
//      mkContentLoop() |> { loop =>
//        val contentAt5 = loop.getItems.get(5)
//        val contentAt6 = loop.getItems.get(6)
//
//        val ops = new ContentLoopOps(loop)
//
//        ops.moveContentForward(5) |> { updateLoop =>
//          assertSame(contentAt5, updateLoop.getItems.get(6))
//          assertSame(contentAt6, updateLoop.getItems.get(5))
//        }
//      }
//    }
//
//    "place the content next after a nearest enabled content with greater index and return updated loop" in {
//      ContentLoopService.builder(mkContentLoop()) |> { builder =>
//        5 to 7 foreach builder.disableContent
//        builder.build()
//      } |> { loop =>
//        val contentAt4 = loop.getContents.get(4)
//        val contentAt8 = loop.getContents.get(8)
//
//        val ops = new ContentLoopOps(loop)
//
//        ops.moveContentForward(4) |> { updateLoop =>
//          assertSame(contentAt4, updateLoop.getItems.get(8))
//          assertSame(contentAt8, updateLoop.getItems.get(7))
//        }
//      }
//    }
//  }
//
//
//  "Move non existing content backward" should {
//    "throw IndexOutOfBoundsException" in {
//      val loop = mkContentLoop()
//      val loopOps = new ContentLoopOps(loop)
//
//      intercept[IndexOutOfBoundsException] {
//        loopOps.moveContentBackward(LoopFx.VacantContentIndex)
//      }
//    }
//  }
//
//
//  "Move existing content backward" should {
//    "return the loop unchanged" when {
//      "the content is the first" in {
//        val loop = mkContentLoop()
//        val updatedLoop = loop.ops.moveContentBackward(LoopFx.FirstContentIndex)
//
//        assertEquals(loop, updatedLoop)
//      }
//
//      "all previous contents are disabled" in {
//        ContentLoopService.builder(mkContentLoop()) |> { builder =>
//          0 to 5 foreach builder.disableContent
//          builder.build()
//        } |> { loop =>
//          assertEquals(loop, loop.ops.moveContentBackward(6))
//        }
//      }
//    }
//  }
//
//
//  "Move existing content backward" should {
//    "swap the content with a prev content and return updated loop" in {
//      mkContentLoop() |> { loop =>
//        val contentAt5 = loop.getItems.get(5)
//        val contentAt6 = loop.getItems.get(6)
//
//        val ops = new ContentLoopOps(loop)
//
//        ops.moveContentBackward(6) |> { updateLoop =>
//          assertSame(contentAt5, updateLoop.getItems.get(6))
//          assertSame(contentAt6, updateLoop.getItems.get(5))
//        }
//      }
//    }
//
//    "place the content before nearest enabled content with lower index and return updated loop" in {
//      ContentLoopService.builder(mkContentLoop()) |> { builder =>
//        5 to 7 foreach builder.disableContent
//        builder.build()
//      } |> { loop =>
//        val contentAt4 = loop.getContents.get(4)
//        val contentAt8 = loop.getContents.get(8)
//
//        val ops = new ContentLoopOps(loop)
//
//        ops.moveContentBackward(8) |> { updateLoop =>
//          assertSame(contentAt4, updateLoop.getItems.get(5))
//          assertSame(contentAt8, updateLoop.getItems.get(4))
//        }
//      }
//    }
//  }
//
//
//  "Move non existing content top" should {
//    "throw IndexOfBoundsException" in {
//      val loop = mkContentLoop()
//      val loopOps = new ContentLoopOps(loop)
//
//      intercept[IndexOutOfBoundsException] {
//        loopOps.moveContentFirst(LoopFx.VacantContentIndex)
//      }
//    }
//  }
//
//
//  "Move existing content top" should {
//    "return the loop unchanged" when {
//      "the content is allready at the top" in {
//        val loop = mkContentLoop()
//        val updatedLoop = new ContentLoopOps(loop).moveContentFirst(0)
//
//        assertEquals(loop, updatedLoop)
//      }
//    }
//
//
//    "return updated loop with the content at the top" in {
//      val loop = mkContentLoop()
//      val updatedLoop = loop.ops.moveContentFirst(5)
//
//      assertEquals(loop.getItems.get(5), updatedLoop.getItems.get(0))
//      assertEquals(loop.getItems.get(0), updatedLoop.getItems.get(1))
//    }
//  }
//
//
//  "Move non existing content bottom" should {
//    "throw IndexOfBoundsException" in {
//      val loop = mkContentLoop()
//      val loopOps = new ContentLoopOps(loop)
//
//      intercept[IndexOutOfBoundsException] {
//        loopOps.moveContentLast(LoopFx.VacantContentIndex)
//      }
//    }
//  }
//
//
//  "Move existing content bottom" should {
//    "return a loop unchanged" when {
//      "the content is already at the bottom" in {
//        val loop = mkContentLoop()
//        val loopOps = new ContentLoopOps(loop)
//        val updatedLoop = loopOps.moveContentLast(LoopFx.LastContentIndex)
//
//        assertEquals(loop, updatedLoop)
//      }
//    }
//
//
//    "return updated loop with the content at the bottom" in {
//      val loop = mkContentLoop()
//      val updatedLoop = loop.ops.moveContentLast(5)
//
//      assertEquals(loop.getItems.get(5), updatedLoop.getItems.get(LoopFx.LastContentIndex))
//      assertEquals(loop.getItems.get(LoopFx.LastContentIndex), updatedLoop.getItems.get(LoopFx.LastContentIndex - 1))
//    }
//  }
}