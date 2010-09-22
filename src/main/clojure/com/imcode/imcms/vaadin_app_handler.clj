(ns com.imcode.imcms.vaadin-app-handler
  (:require
   (clojure.java [io :as io]))

  (:import
   com.vaadin.Application
   (scala Some None$)
   (com.vaadin.ui Window SplitPanel Button Panel Label Button$ClickListener Embedded)
   (com.vaadin.terminal ExternalResource ClassResource FileResource)
   (com.vaadin.data Property Property$ValueChangeListener)
   (com.imcode.imcms.servlet.superadmin.vaadin.ui OkCancelDialog)
   (com.imcode.imcms.servlet.superadmin.vaadin.filemanager FileBrowser FileBrowserWithImagePreview)))


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
    (-> ,, .tblDirContent (.setSelectable true))

    (.addDirectoryTree "Home" (io/file "/Users/ajosua") None)
    (.addDirectoryTree "Projects" (io/file "/Users/ajosua/projects") None)
    (.addDirectoryTree "imCMS trunk" (io/file "/Users/ajosua/projects/imcode/imcms/trunk") None)

    (.setWidth "100%")
    (.setHeight "500px")))



(defn mk-file-browser-with-img-preview []
  (let [browser-with-img-preview (FileBrowserWithImagePreview.)]

    (doto (. browser-with-img-preview browser)
      (-> ,, .tblDirContent (.setSelectable true))

      (.addDirectoryTree "Home" (io/file "/Users/ajosua") None)
      (.addDirectoryTree "Projects" (io/file "/Users/ajosua/projects") None)
      (.addDirectoryTree "imCMS trunk" (io/file "/Users/ajosua/projects/imcode/imcms/trunk") None))

    (doto (. browser-with-img-preview preview)
      (.setWidth "150px")
      (.setHeight "150px"))      

    (doto browser-with-img-preview
      (.setWidth "650px")
      (.setHeight "400px"))))


(defn mk-select-img-dlg []
  (let [dlg (OkCancelDialog. "Select image - *.gif, *.png, *.jpg *.jpeg")
        file-browser-with-preview (mk-file-browser-with-img-preview)]

    (.setMainAreaContent dlg file-browser-with-preview)
    (doto file-browser-with-preview (.setWidth "650px") (.setHeight "400px"))
    dlg))


(defn file-resource [app resource-name]
  (FileResource. (io/file resource-name) app))

(defn class-resource [app resource-name]
  (ClassResource. resource-name app))

(defn external-resource [resource-name]
  (ExternalResource. resource-name))

(defn embedded [name resource]
  (Embedded. name, resource))

(defn image? [file]
  (boolean
    (re-matches #".*\.(?:gif|png|jpg)$" (str file))))

(def MB (* 1024 1024))

(defn can-preview? [file] (-> file .getLength (< ,, (* 2 MB))))


(defn mk-main-wnd-content [wnd]
  (let [app (.getApplication wnd)
        content (Panel.)
        file-browser (mk-file-browser)
        file-browser-with-img-preview (mk-file-browser-with-img-preview)
        btn-select-img-dlg (Button. "Select image")
        btn-app-info (Button. "Print app info")
        btn-file-img (Button. "File image")
        btn-cls-img (Button. "Cls image")
        btn-ext-img (Button. "External image")]

    (.addListener (.tblDirContent file-browser)
      (reify Property$ValueChangeListener
        (valueChange [this event]
          (when-let [file (.. event getProperty getValue)]
            (when (image? file)
              (let [img (embedded "" (file-resource app file))]
                (doto img (.setWidth "100px") (.setHeight "100px"))
                (.addComponent content img)))))))
    
    (add-click-listener* btn-file-img
      #(.addComponent content
                      (embedded "FILE RESOURCE IMAGE" (file-resource app
                                "/Users/ajosua/projects/imcode/imcms/trunk/src/main/web/images/imCMSpower.gif"))))

    (add-click-listener* btn-cls-img
      #(.addComponent content
                      (embedded "CLASS RESOURCE IMAGE" (class-resource app
                                "src/main/web/images/imCMSpower.gif"))))

    (add-click-listener* btn-ext-img
      #(.addComponent content
                      (embedded "EXTERNAL RESOURCE IMAGE" (external-resource
                                (str (.. app getURL toString) "images/imCMSpower.gif")))))

    (add-click-listener* btn-app-info
      #(println (bean app)))

    (add-click-listener* btn-select-img-dlg
      (let [dlg (mk-select-img-dlg)]
        (doto dlg
          (.setModal true)
          (.setResizable true)
          (.setDraggable true))
        
      #(.addWindow wnd dlg)))
    
;    window setModal modal
;    window setResizable resizable
;    window setDraggable draggable

    (add-components content
      btn-select-img-dlg, file-browser-with-img-preview, file-browser, btn-app-info, btn-file-img, btn-cls-img, btn-ext-img)))


(defn init-app[^com.vaadin.Application app]
  (let [wnd (Window. "Application main window")]
    (.setMainWindow app wnd)
    (.setContent wnd (mk-main-wnd-content wnd))))