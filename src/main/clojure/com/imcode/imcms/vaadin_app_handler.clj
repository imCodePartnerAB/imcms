(ns com.imcode.imcms.vaadin-app-handler
  (:require
   (clojure.java [io :as io]))

  (:import
   com.vaadin.Application
   (scala Some None$)
   (com.vaadin.ui Window SplitPanel Button Panel Label Button$ClickListener)
   (com.imcode.imcms.servlet.superadmin.vaadin.filemanager FileBrowser)))


(def None None$/MODULE$)


(defn add-components [container & components]
  (doseq [c components] (.addComponent container c))
  container)


(defn mk-click-listener [click-handler-fn]
  (reify Button$ClickListener
    (buttonClick [this, event] (click-handler-fn event))))


(defn add-click-listener [button click-handler-fn]
  (.addListener button (mk-click-listener click-handler-fn)))


(defn mk-click-listener* [click-handler-fn-zero-arity]
  (mk-click-listener (fn [_] (click-handler-fn-zero-arity))))


(defn add-click-listener* [button, click-handler-fn-zero-arity]
  (add-click-listener button  (fn [_] (click-handler-fn-zero-arity))))


(defn mk-file-browser []
  (doto (FileBrowser.)
    (.addDirectoryTree "Home" (io/file "/Users/ajosua") None)
    (.addDirectoryTree "Projects" (io/file "/Users/ajosua/projects") None)
    (.addDirectoryTree "imCMS trunk" (io/file "/Users/ajosua/projects/imcode/imcms/trunk") None)

    (.setWidth "100%")
    (.setHeight "500px")))


(defn mk-main-wnd-content [wnd]
  (let [content (Panel.)
        button (Button. "Click me")]
    (add-click-listener* button #(.addComponent content (mk-file-browser)))

    (add-components content
      button, (Button. "New"), (Button. "Edit"), (Button. "Delete"), (Label. "Test"))))


(defn mk-main-wnd []
  (let [wnd (Window. "Application main window")
        content (mk-main-wnd-content wnd)]
    (doto wnd (.setContent content))))


(defn init-app[^com.vaadin.Application app]
  (.setMainWindow app (mk-main-wnd)))