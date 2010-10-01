(ns
  ^{:doc "Vaadin test/prototype application handler."}
  com.imcode.imcms.vaadin-app-handler
  
  (:require
    (clojure.java [io :as io]))

  (:import
    com.vaadin.Application
    (scala Some None$)
    (com.vaadin.ui Window SplitPanel Button Panel Label Button$ClickListener Embedded GridLayout HorizontalLayout
                   VerticalLayout Alignment TextField CheckBox MenuBar MenuBar$MenuItem MenuBar$Command)
    (com.vaadin.terminal ExternalResource ClassResource FileResource ThemeResource)
    (com.vaadin.data Property Property$ValueChangeListener)
    (com.imcode.imcms.servlet.superadmin.vaadin.ui OkCancelDialog)
    (com.imcode.imcms.servlet.superadmin.vaadin.filemanager FileBrowser FileBrowserWithImagePreview)))


(def None None$/MODULE$)


(defn add-components [container & components]
  (doseq [c components] (.addComponent container c))
  container)


(defmacro add-btn-click-listener [button event & body]
  `(let [btn# ~button]
     (. btn# ~'addListener
       (reify Button$ClickListener
         (~'buttonClick [~'_, ~event] ~@body)))
     btn#))


(defn mk-file-browser []
  (doto (FileBrowser.)
    (-> ,, .tblDirContent (.setSelectable true))

    (.addDirectoryTree "Home" (io/file "/Users/ajosua") None)
    (.addDirectoryTree "Projects" (io/file "/Users/ajosua/projects") None)
    (.addDirectoryTree "imCMS trunk" (io/file "/Users/ajosua/projects/imcode/imcms/trunk") None)

    (.setWidth "100%")
    (.setHeight "500px")))



(defn mk-file-browser-with-img-preview []
  (let [browser-with-img-preview (FileBrowserWithImagePreview. 100 100)]

    (doto (. browser-with-img-preview browser)
      (-> ,, .tblDirContent (.setSelectable true))

      (.addDirectoryTree "Home" (io/file "/Users/ajosua") None)
      (.addDirectoryTree "Projects" (io/file "/Users/ajosua/projects") None)
      (.addDirectoryTree "imCMS trunk" (io/file "/Users/ajosua/projects/imcode/imcms/trunk") None)
      (.addDirectoryTree "Images" (io/file "/Users/ajosua/projects/imcode/imcms/trunk/src/main/web/images") None))

    (doto browser-with-img-preview
      (.setWidth "650px")
      (.setHeight "400px"))))


(defn mk-select-img-dlg []
  (let [dlg (OkCancelDialog. "Select image - *.gif, *.png, *.jpg *.jpeg")
        file-browser-with-preview (mk-file-browser-with-img-preview)]
    (doto dlg
      (.setMainContent file-browser-with-preview)
      (-> ,, .lytArea (.setComponentAlignment file-browser-with-preview Alignment/TOP_LEFT))
      (-> ,, .lytArea (.setMargin false))
      (-> ,, .lytArea .setSizeFull)
      (-> ,, .lytArea (.setColumnExpandRatio 0 1.0))
      (-> ,, .lytArea (.setRowExpandRatio 0 1.0)))

    (.setSizeFull file-browser-with-preview)

    (doto dlg
      (.setWidth "650px")
      (.setHeight "400px"))))


(defn check-box-value-test
  "Check box value is in instance of Boolean."
  []
  (let [checkBox (CheckBox. "Check me!")
        value-off (.getValue checkBox)]

    (println "Value off: " value-off ", value-off class " (class value-off))
    (.setValue checkBox true)
    (let [value-on (.getValue checkBox)]
      (println "Value on: " value-on ", value-on class " (class value-on)))))


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

(defn mk-layout-with-button-in-center-demo
  [^String width, ^String height]
  (let [layout (VerticalLayout.) ; might be Horizontal or Grid as well 
        button (Button. "CENTER")]
    (doto layout
      (.addComponent button)
      (.setComponentAlignment button Alignment/MIDDLE_CENTER)
      (.setWidth width)
      (.setHeight height))))


(defn mk-panel-with-button-in-center-demo
  "Discovered so far:
   If a component (a button in this case) is added directly to the panel (layout) then
   aligned with Alignment/MIDDLE_CENTER then it anyway remains in the top left position.

   In order to put a component in the center of a panel:
   1.create component holder (a layout) with fixed size
     (setting holder size to undefined and panel size to fixed size also does not work)
   2.add the holder to the panel
   3.set panel size to undefined - so it will wrap the holder."
  [^String width, ^String height]
  (let [panel (Panel.)
        button (Button. "CENTER")
        lytButtonHolder (HorizontalLayout.)]

    (doto lytButtonHolder
      (.addComponent button)
      (.setComponentAlignment button Alignment/MIDDLE_CENTER)
      (.setWidth width)
      (.setHeight height))
    (doto panel
      (.addComponent lytButtonHolder)
      .setSizeUndefined)))


(defn mk-panel-with-label-in-center-demo
  "See comments on mk-panel-with-button-in-center-demo
   However, a label differs from a button - by default its width 100%.
   Should label apperar in center its width (size) must be set to undefined."
  [^String width, ^String height]
  (let [panel (Panel.)
        label (Label. "CENTER")
        lytLabelHolder (HorizontalLayout.)]

    (.setSizeUndefined label)

    (doto lytLabelHolder
      (.addComponent label)
      (.setComponentAlignment label Alignment/MIDDLE_CENTER)
      (.setWidth width)
      (.setHeight height))
    (doto panel
      (.addComponent lytLabelHolder)
      .setSizeUndefined)))


;; Grid Layout demos
(defn mk-grid-lyt-demo-1
  "Returns a 4x4 grid layout with given width and height.
   Assigns different expands ratio to cells."
  [^String width, ^String height]
  (let [lyt (GridLayout. 4 4)]
    (doseq [x (range 4), y (range 4)]
      (.addComponent lyt (doto (Button. "") .setSizeFull))
      (.setColumnExpandRatio lyt x (if (< 0 x 3) 2 1))
      (.setRowExpandRatio    lyt y (if (< 0 y 3) 2 1)))

    (doto lyt
      (.setSpacing true)
      (.setWidth width)
      (.setHeight height))))


(defn mk-grid-lyt-demo-2
  "Returns a grid layout with a single cell.
   Replaces comonent in a grid. 
   Details to remember:
   If a cell is allready occupied by a comonent then it must be removed first.
   Otherwise a com.vaadin.ui.GridLayout$OverlapsException will be thrown."
  []
  (let [lyt (GridLayout. 1 1)
        btn1 (Button. "1 >> 2")
        btn2 (Button. "2 >> 1")
        switch (fn [component]
                 (.removeComponent lyt 0 0)
                 (.addComponent lyt component 0 0))]

    (add-btn-click-listener btn1 _ (switch btn2))
    (add-btn-click-listener btn2 _ (switch btn1))

    (switch btn1)

    lyt))

(defn mk-vertical-layout-demo
  "Three button with expand ratio 1.0 inside a vertical layout with *defined* size.
   Details:
   Expand ratio does not affect a component width unlsess layout size is defined and component's wdth is 100%.
   "
  [^String width]
  (let [btn1 (Button. "1--")
        btn2 (Button. "2----")
        btn3 (Button. "3--------")]

    (doseq [btn [btn1 btn2 btn3]] (.setWidth btn "100%"))

    (doto (VerticalLayout.)
      (.setWidth width)
      (add-components btn1 btn2 btn3)
      (.setExpandRatio btn1 1.0)
      (.setExpandRatio btn2 1.0)
      (.setExpandRatio btn3 1.0))))


(defn mk-horizontal-layout-demo
  "Horizontal layout containing some components with captions.
   By default caption is shown above a compoment."
  []
  (doto (HorizontalLayout.)
    (add-components (TextField. "Text 1") (TextField. "Text 2") (Panel. "Panel 1"))))


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
    
    (add-btn-click-listener btn-file-img _
      (.addComponent content
                     (embedded "FILE RESOURCE IMAGE" (file-resource app
                                "/Users/ajosua/projects/imcode/imcms/trunk/src/main/web/images/imCMSpower.gif"))))

    (add-btn-click-listener btn-cls-img _
      (.addComponent content
                     (embedded "CLASS RESOURCE IMAGE" (class-resource app
                                "src/main/web/images/imCMSpower.gif"))))

    (add-btn-click-listener btn-ext-img _
      (.addComponent content
                     (embedded "EXTERNAL RESOURCE IMAGE" (external-resource
                                (str (.. app getURL toString) "images/imCMSpower.gif")))))

    (add-btn-click-listener btn-app-info _
      (println (bean app)))

    (add-btn-click-listener btn-select-img-dlg _
      (let [dlg (mk-select-img-dlg)]
        (doto dlg
          (.setModal true)
          (.setResizable true)
          (.setDraggable true))
        
      (.addWindow wnd dlg)))

    (add-components content

      (mk-vertical-layout-demo "250px")
      (mk-horizontal-layout-demo)
      btn-select-img-dlg
      (mk-layout-with-button-in-center-demo "250px", "250px")
      (mk-panel-with-button-in-center-demo "250px", "250px")
      (mk-panel-with-label-in-center-demo "250px", "250px")
      ;btn-select-img-dlg, file-browser-with-img-preview, 
      file-browser, btn-app-info, btn-file-img, btn-cls-img, btn-ext-img)

    (add-components content
      (add-btn-click-listener (Button. "Checkbox test") _ (check-box-value-test)))

    (let [txtReadOnly (TextField. "ReadOnly")]
      (.setEnabled txtReadOnly false)
      (add-components content txtReadOnly
        (add-btn-click-listener (Button. "Test read-only") _ (.setValue txtReadOnly "???"))))

    (let [chkBox (CheckBox. "Check box")]
      (.setImmediate chkBox true)
      (add-btn-click-listener chkBox _ (println "checked: " (.booleanValue chkBox)))

      (add-components content chkBox))

    (add-components content
      (mk-grid-lyt-demo-1 "250px", "250px")
      (mk-grid-lyt-demo-2))

    (add-components content
      (let [lytHorisontal (VerticalLayout.)
            lblMsg (Label. "Default message")
            txtMsg (TextField. "")
            btnOk (Button. "Ok")]
        (.setWidth lblMsg "50px")
        (doto lytHorisontal (.setSpacing true) (.setMargin true))

        (add-btn-click-listener btnOk _ (.setValue lblMsg (.getValue txtMsg)))
        (add-components lytHorisontal lblMsg txtMsg btnOk)))

    (add-components content
      (let [mb (MenuBar.)
            lt (VerticalLayout.)
            hl (HorizontalLayout.)]
        (.addItem mb "Add new" (ThemeResource. "icons/16/document-add.png")
                     (reify MenuBar$Command
                       (menuSelected [this item] (println "ADD NEW"))))

        (.addItem mb "Edit" (ThemeResource. "icons/16/document-txt.png")
                     (reify MenuBar$Command
                       (menuSelected [this item] (println "EDIT"))))

        (.addItem mb "Delete" (ThemeResource. "icons/16/document-delete.png")
                     (reify MenuBar$Command
                       (menuSelected [this item] (println "DELETE"))))

        
        (.setWidth hl "100%")
        (.setWidth mb "100%")
        (doto hl
          (.addComponent mb)
          (.setExpandRatio mb 1.0)
          (.addComponent (doto (Button. "Reload")
                           (.setStyleName Button/STYLE_LINK)
                           (.setIcon (ThemeResource. "icons/16/reload.png")))))))
    ; let
    ))


(defn init[^com.vaadin.Application app]
  (let [wnd (Window. "Application main window")]
    (.setTheme app "runo")
    (.setMainWindow app wnd)
    (.setContent wnd (mk-main-wnd-content wnd))))



