(ns com.imcode.imcms.vaadin-app-handler
  (:import
   com.vaadin.Application
   (com.vaadin.ui Window SplitPanel Button Panel)))


(defn create-main-window []
  (let [split-panel (SplitPanel. SplitPanel/ORIENTATION_HORIZONTAL)]
    (doto (Window. "Window")
      (.setContent split-panel))))


(defn init-app[^com.vaadin.Application app]
  (.setMainWindow app (create-main-window)))
