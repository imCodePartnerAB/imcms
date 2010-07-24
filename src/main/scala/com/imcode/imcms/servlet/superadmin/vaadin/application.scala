package com.imcode.imcms.servlet.superadmin.vaadin;

import clojure.lang.RT
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._

class App extends com.vaadin.Application {

    def init {
      val window = new Window
      val splitPanel = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL)
      val tree = new Tree

      0 to 10 foreach { n => tree.addItem("Item: " + n) }

      window setContent splitPanel

      splitPanel setFirstComponent tree

      tree addListener (new ItemClickEvent.ItemClickListener {
        def itemClick(e: ItemClickEvent) {
          splitPanel setSecondComponent (new Label(e.getItemId.toString))           
        }
      })

      this setMainWindow window
    }
}