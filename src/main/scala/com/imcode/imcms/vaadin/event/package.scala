package com.imcode.imcms.vaadin

import com.vaadin.event.ItemClickEvent

package object event {

  implicit def wrapItemClickNotifier(notifier: ItemClickEvent.ItemClickNotifier) = new {
    def addItemClickListener(listener: ItemClickEvent => Unit): Unit =
      notifier.addListener(new ItemClickEvent.ItemClickListener {
        def itemClick(event: ItemClickEvent): Unit = listener(event)
      })
  }
}
