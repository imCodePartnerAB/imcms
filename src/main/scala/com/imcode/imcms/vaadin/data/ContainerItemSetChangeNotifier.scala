package com.imcode.imcms.vaadin.data

import com.vaadin.data.Container
import com.vaadin.data.Container.ItemSetChangeListener

trait ContainerItemSetChangeNotifier extends Container.ItemSetChangeNotifier { container: Container =>

  @transient
  private var listeners = Set.empty[ItemSetChangeListener]

  override def removeListener(listener: ItemSetChangeListener) {
    listeners -= listener
  }

  override def addListener(listener: ItemSetChangeListener) {
    listeners += listener
  }

  override def addItemSetChangeListener(listener: ItemSetChangeListener) {
    addListener(listener)
  }

  override def removeItemSetChangeListener(listener: ItemSetChangeListener) {
    removeListener(listener)
  }

  protected def notifyItemSetChanged() {
    val event = new Container.ItemSetChangeEvent {
      val getContainer = container
    }

    listeners.foreach(_.containerItemSetChange(event))
  }
}