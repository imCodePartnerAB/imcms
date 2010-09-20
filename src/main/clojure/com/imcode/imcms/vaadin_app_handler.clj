(ns com.imcode.imcms.vaadin-app-handler
  (:import
   com.vaadin.Application
   (com.vaadin.ui Window SplitPanel Button Panel Label Button$ClickListener)))


(defn add-components [container & components]
  (doseq [c components] (.addComponent container c))
  container)


(defn mk-click-listener [click-handler-fn]
  (reify Button$ClickListener
    (buttonClick [this, e] (click-handler-fn))))


(defn add-click-listener [button click-handler-fn]
  (.addListener button click-handler-fn)) 


(defn mk-main-wnd-content []
  (let [panel (Panel. "This is a panel")
        button (Button. "Click me")]
    (add-click-listener button #(.addComponent panel (Label. "Clicked")))

    (add-components panel
      button, (Button. "New"), (Button. "Edit"), (Button. "Delete"), (Label. "Test"))))


(defn mk-main-wnd []
  (let [split-panel (SplitPanel. SplitPanel/ORIENTATION_HORIZONTAL)]
    (doto (Window. "Window")
      (.setContent (create-main-window-content)))))


(defn init-app[^com.vaadin.Application app]
  (.setMainWindow app (mk-main-wnd)))
