package com.imcode
package imcms
package vaadin
package ui

import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin._
import com.imcode._
import com.vaadin.ui._

class TwinSelect[T <: AnyRef](caption: String = "") extends Panel(caption) with LightStyle {
    private val content = new GridLayout(3, 1)

    setContent(content)

    val btnAdd = new Button("<<")
    val btnRemove = new Button(">>")
    val lstAvailable = new ListSelect("Available") with MultiSelect[T] with Immediate
    val lstChosen = new ListSelect("Chosen") with MultiSelect[T] with Immediate
    val lytButtons = new VerticalLayout |>> { lyt =>
      addComponentsTo(lyt, btnAdd, btnRemove)
    }

    addComponentsTo(content, lstChosen, lytButtons, lstAvailable)
    content.setComponentAlignment(lytButtons, Alignment.MIDDLE_CENTER)

    doto(lstAvailable, lstChosen) { l =>
      l.setColumns(10)
      l.setRows(5)
      l.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT)
    }

    btnAdd.addClickHandler { move(lstAvailable, lstChosen) }
    btnRemove.addClickHandler { move(lstChosen, lstAvailable) }

    lstAvailable.addValueChangeHandler { reset() }
    lstChosen.addValueChangeHandler { reset() }

    reset()

    def reset() {
      btnAdd.setEnabled(lstAvailable.value.size > 0)
      btnRemove.setEnabled(lstChosen.value.size > 0)
    }

    private[this] def move(src: ListSelect with MultiSelect[T], dest: ListSelect with MultiSelect[T]) {
      src.value.asScala.foreach { itemId =>
        src.getItemCaption(itemId) |> { itemCaption =>
          addItem(dest, itemId, itemCaption)
        }

        src.removeItem(itemId)
      }
    }

    def availableItemIds: List[T] = lstAvailable.itemIds.asScala.toList
    def chosenItemIds: List[T] = lstChosen.itemIds.asScala.toList

    def addAvailableItem(itemId: T)(implicit ev: T =:= String): Unit = addAvailableItem(itemId, itemId)

    def addAvailableItem(itemId: T, caption: String): Unit = addItem(lstAvailable, itemId, caption)

    def addChosenItem(itemId: T)(implicit ev: T =:= String): Unit = addChosenItem(itemId, itemId)

    def addChosenItem(itemId: T, caption: String): Unit = addItem(lstChosen, itemId, caption)

    private[this] def addItem(listSelect: ListSelect, itemId: T, caption: String) {
      listSelect.addItem(itemId)
      listSelect.setItemCaption(itemId, caption)
    }

    def setRows(count: Int): Unit = doto(lstAvailable, lstChosen) { _ setRows count }
    def setColumns(count: Int): Unit = doto(lstAvailable, lstChosen) { _ setColumns count }
  }
