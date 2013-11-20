package com.imcode.imcms.vaadin.event

import com.vaadin.event.ItemClickEvent

// implicit
class ItemClickNotifierWrapper(notifier: ItemClickEvent.ItemClickNotifier) {
  def addItemClickHandler(handler: ItemClickEvent => Unit) {
    notifier.addItemClickListener(
      new ItemClickEvent.ItemClickListener {
        def itemClick(event: ItemClickEvent) {
          handler(event)
        }
      }
    )
  }
}
