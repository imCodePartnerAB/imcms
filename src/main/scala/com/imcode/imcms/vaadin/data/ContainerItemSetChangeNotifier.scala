package com.imcode.imcms.vaadin.data

import com.vaadin.data.Container
import com.vaadin.data.Container.ItemSetChangeListener

trait ContainerItemSetChangeNotifier extends Container.ItemSetChangeNotifier { container: Container =>

  private var listeners = Set.empty[ItemSetChangeListener]

  def removeListener(listener: ItemSetChangeListener) {
    listeners -= listener
  }

  def addListener(listener: ItemSetChangeListener) {
    listeners += listener
  }

  protected def notifyItemSetChanged() {
    val event = new Container.ItemSetChangeEvent {
      val getContainer = container
    }

    listeners.foreach(_.containerItemSetChange(event))
  }
}