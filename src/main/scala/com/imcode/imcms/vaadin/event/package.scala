package com.imcode.imcms.vaadin

import com.vaadin.event.ItemClickEvent

package object event {

  implicit def fnToItemClickListener(fn: (ItemClickEvent => Any)): ItemClickEvent.ItemClickListener = {
    new ItemClickEvent.ItemClickListener {
        def itemClick(event: ItemClickEvent): Unit = fn(event)
    }
  }
}
