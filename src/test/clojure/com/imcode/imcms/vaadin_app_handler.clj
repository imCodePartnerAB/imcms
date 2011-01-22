(ns
  ^{:doc "Vaadin test/prototype application handler."}
  com.imcode.imcms.vaadin-app-handler
  
  (:require
    (clojure.java [io :as io])
    [clojure.string :as str])

  (:import
    (java.net URL)
    com.vaadin.Application
    (scala Some None$)
    (com.vaadin.ui Window SplitPanel Button Panel Label Button$ClickListener Embedded GridLayout HorizontalLayout
                   FormLayout VerticalLayout Alignment TextField CheckBox MenuBar MenuBar$MenuItem MenuBar$Command
                   Select ListSelect TabSheet Table Tree CustomLayout Accordion AbstractSelect)

   ; (com.vaadin.ui.themes BaseTheme)

    (com.vaadin.terminal ExternalResource ClassResource FileResource ThemeResource)
    (com.vaadin.data Property Property$ValueChangeListener)
    ;(com.imcode.imcms.vaadin OkCancelDialog)
    ;(com.imcode.imcms.sysadmin.filemanager FileBrowser FileBrowserWithImagePreview)
    ;(com.imcode.imcms.admin.file FileBrowser)

    (com.vaadin.data.util ObjectProperty FilesystemContainer)))


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

;(defmacro when-selected [[sym field] & forms]
;  `(when-let [~sym (.getValue ~field)] ~@forms))


(defmacro when-selected
  "Allows multiple bindings if this AbstractSelect is multiselect.
   The form (when-selected selectable value &body) must be used in single select and might be used in multiselect mode.
   The form (when-selected selectable [v1 v2 & rest :as vals] &body) can be used in multiselect mode to defstruct bind,
   however, the order of returned items may differ from selection order.
   Returns nil if no item is selected or body evaluation result."   
  [selectable binding & body]
  `(when-let [value# (.getValue ~selectable)]
     (if (instance? Iterable value#)
       (when-let [value-seq# (seq value#)]
         (let [~binding value-seq#]
           ~@body))

       (let [~binding value#]
         ~@body))))                                    

(defn find-resource [key] (str "#" key "#"))

(defn setup
  "Sets properties to an object and returns it."
  [obj keyword & keywords]
  (doseq [kw (cons keyword keywords)]
    (condp = kw
      :resource-caption (.setCaption obj (find-resource (.getCaption obj)))
      :selectable (.setSelectable obj true)
      :multiselect (-> (setup obj :selectable) (.setMultiSelect true))
      :single-select (-> (setup obj :selectable) (.setMultiSelect false))      
      :scrollable (.setScrollable obj true)
      :non-scrollable (.setScrollable obj false)
      :checked (.setValue obj true)
      :unchecked (.setValue obj false)
      :enabled (.setEnabled obj true)
      :disabled (.setEnabled obj false)
      :spacing (.setSpacing obj true)
      :no-spacing (.setSpacing obj false)
      :margin (.setMargin obj true)
      :no-margin (.setMargin obj false)
      :immediate (.setImmediate obj true)
      :full-size (.setSizeFull obj)
      :undefined-size (.setSizeUndefined obj)
      :null-selection (.setNullSelectionAllowed obj true)
      :no-null-selection (.setNullSelectionAllowed obj false)
      (throw (Exception. (format "Undefined property %s for object %s." kw obj)))))
  obj)


(defn set-size
  "Sets component size and returns component."
  ([component width height]
    (set-size component width height :px))

  ([component width height kw-unit]
    (condp = kw-unit
      ;pixels
      :px (doto component
             (.setWidth (str width "px"))
             (.setHeight (str height "px")))
      ;percentage
      :pg (doto component
             (.setWidth (str width "%"))
             (.setHeight (str height "%")))

      ;points
      ;todo (:pt ...
      (throw (Exception. (format "Unable to set component %s size - undefined unit %s." component kw-unit))))))

;; requires IMCMS classes

;(defn mk-file-browser []
;  (doto (FileBrowser.)
;    (-> ,, .tblDirContent (.setSelectable true))
;
;    (.addDirectoryTree "Home" (io/file "/Users/ajosua") None)
;    (.addDirectoryTree "Projects" (io/file "/Users/ajosua/projects") None)
;    (.addDirectoryTree "imCMS trunk" (io/file "/Users/ajosua/projects/imcode/imcms/trunk") None)
;
;    (.setWidth "100%")
;    (.setHeight "500px")))
;
;
;
;(defn mk-file-browser-setup-img-preview []
;  (let [browser-setup-img-preview (FileBrowsersetupImagePreview. 100 100)]
;
;    (doto (. browser-setup-img-preview browser)
;      (-> ,, .tblDirContent (.setSelectable true))
;
;      (.addDirectoryTree "Home" (io/file "/Users/ajosua") None)
;      (.addDirectoryTree "Projects" (io/file "/Users/ajosua/projects") None)
;      (.addDirectoryTree "imCMS trunk" (io/file "/Users/ajosua/projects/imcode/imcms/trunk") None)
;      (.addDirectoryTree "Images" (io/file "/Users/ajosua/projects/imcode/imcms/trunk/src/main/web/images") None))
;
;    (doto browser-setup-img-preview
;      (.setWidth "650px")
;      (.setHeight "400px"))))


;; requires IMCMS classes

;(defn mk-select-img-dlg []
;  (let [dlg (OkCancelDialog. "Select image - *.gif, *.png, *.jpg *.jpeg")
;        file-browser-setup-preview (mk-file-browser-setup-img-preview)]
;    (doto dlg
;      (.setMainContent file-browser-setup-preview)
;      (.setWidth "650px")
;      (.setHeight "400px"))))


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

(defn MB [n] (* n 1024 1024))

(defn can-preview? [file] (-> file .getLength (< ,, (MB 2))))

(defn mk-layout-setup-button-in-center-demo
  [^String width, ^String height]
  (let [layout (VerticalLayout.) ; might be Horizontal or Grid as well 
        button (Button. "CENTER")]
    (doto layout
      (.addComponent button)
      (.setComponentAlignment button Alignment/MIDDLE_CENTER)
      (.setWidth width)
      (.setHeight height))))


(defn mk-panel-setup-button-in-center-demo
  "Discovered so far:
   If a component (a button in this case) is added directly to the panel (layout) then
   aligned setup Alignment/MIDDLE_CENTER then it anyway remains in the top left position.

   In order to put a component in the center of a panel:
   1.create component holder (a layout) setup fixed size
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


(defn mk-panel-setup-label-in-center-demo
  "See comments on mk-panel-setup-button-in-center-demo
   However, a label differs from a button - by default its width is set to 100%.
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
  "Returns a 4x4 grid layout setup given width and height.
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
  "Returns a grid layout setup a single cell.
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
  "Three button setup expand ratio 1.0 inside a vertical layout setup *defined* size.
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
  "Horizontal layout containing some components setup captions.
   By default caption is shown above a compoment."
  []
  (doto (HorizontalLayout.)
    (add-components (TextField. "Text 1") (TextField. "Text 2") (Panel. "Panel 1"))))


(defn mk-embedded-demo [app]
  (let [content (VerticalLayout.)
        btn-file-img (Button. "File image")
        btn-cls-img (Button. "Cls image")
        btn-ext-img (Button. "External image")]
    
    (add-components content
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
                                  (str (.. app getURL toString) "images/imCMSpower.gif"))))))))


;(defn mk-components-value-exchange-demo
;  "A componnent can be assigned as a datasource of another component.
;   In that case any value change throug API or UI controller automatically used by enother.
;   There is no difference between source and target component - any changes to target also reflects source component."
;  []
;  (let [lyt (-> (VerticalLayout.) (setup :spacing :margin))
;        txt-src (-> (TextField. "source") (setup :immediate))
;        txt-target (-> (TextField. "target" txt-src) (setup :immediate))
;        btn-clear-src (Button. "clear source")
;        btn-clear-target (Button. "clear target")]
;    (add-btn-click-listener btn-clear-src _
;      (.setValue txt-src ""))
;    (add-btn-click-listener btn-clear-target _
;      (.setValue txt-target ""))
;
;    (add-components lyt
;      txt-src txt-target btn-clear-src btn-clear-target)))


;        lst (doto (ListSelect. "list") (setup :immediate :single-select :undefined-size :null-selection)
;              (.addItem "1")
;              (.addItem "2"))]
;
;    (add-btn-click-listener btn _
;      (when-selected lst [v1 v2 & rest :as vals]
;                         (println v1 v2 vals)
;        (.setValue txtRef val)))

(defn mk-tab-sheet-size-demo
  "Tab sheet does not automatically ajust its width according to its content like for ex. window does when tab content(s)
   size is undefined.
   By default tab sheet width is related (100%) and height is undefined.
   Recipes:
     -Tabsheet and its tab contents size are udefined:
      Tabsheet's width equals to tab's caption widths; each tab's height is ajusted to content's height
     -Tabsheet size is undefied and its tab contents size are defined:
      Each tabsheet's tab size equals to contained component size.
     -Tabsheet has defined size
      Each tabsheet's tab size equals to tabsheet size.
     In every case tab provides scrollbars is its content does not fit."
  []
  (let [ts (-> (TabSheet.) (setup :enabled))
        lyt1 (doto (FormLayout.) (setup :margin :undefined-size)
               (.setCaption "t1") (add-components ,, (TextField. "text1"), (TextField. "text2"), (Button. "button")))
        lyt2 (doto (FormLayout.) (setup :margin :undefined-size)
               (.setCaption "t2") (add-components ,, (TextField. "text1"), (TextField. "text2"), (Button. "button") (Button. "button")))
        tab1 (.addTab ts lyt1)
        tab2 (.addTab ts lyt2)]

    ;;(doto lyt2 (.setWidth "800px") (.setHeight "800px"))
    ;;(doto ts (.setWidth "500px") (.setHeight "300px"))
    ts))

(defn mk-chk-box-handler-demo
  "Check box does not send event to click listeners while holding focus unless its set immediate."
  []
  (let [chk-default (setup (CheckBox. "Check box setup default behaviour - related button will become enabled/disabled only after focus is lost.")
                      :checked)
        btn-default (Button. "Button tied to check box setup default behavior.")
        chk-immediate (setup (CheckBox. "Check box setup imediate behaviour - related button become enabled/disabled immediately.")
                        :checked :immediate)
        btn-immediate (Button. "Button tied to check box setup immediate behavior.")]

    (add-btn-click-listener chk-default e
      (.setEnabled btn-default (. chk-default getValue)))

    (add-btn-click-listener chk-immediate e
      (.setEnabled btn-immediate (. chk-immediate getValue)))

    (doto (VerticalLayout.) (setup :spacing :margin)
      (add-components ,, chk-default btn-default chk-immediate btn-immediate))))


(defn mk-panel-test []
  (let [content (doto (HorizontalLayout.) (setup :undefined-size))
        panel (doto (Panel.) (setup :scrollable :full-size)
                (.setContent content))]

    (dotimes [i 10] (.addComponent content (Button. (str "button " i))))

    (set-size panel 400 500)
    (set-size content 500 200)

    (doto (VerticalLayout.)
      (setup :full-size)
      (.addComponent panel))))

(defn mk-table-test []
  (let [table (Table. "table")
        button (Button. "unselect")]

    (add-btn-click-listener button _
                                (println "NS:" (.getNullSelectionItemId table))
                                (.select table nil))

    (doto table
      (setup :single-select :no-null-selection)
      (.setNullSelectionItemId "NL!")
      (.addContainerProperty 1 Character nil)
      (.addContainerProperty 2 Character nil)
      (.addContainerProperty 3 Character nil)

      (.addItem  (into-array "abc") "1")
      (.addItem  (into-array "def") "2")
      (.addItem  (into-array "ghi") "3"))

    (doto (VerticalLayout.) (add-components button table))))


(defn mk-multi-list-select-unselection-test
 "If ListSelect is set to multiselect it can not have NullSelectionItemId,
  however .getNullSelectionItemId still returns nil.
  To unselect all items in multiselect list unselect should be call for every selected item."
 []
(let [list (-> (ListSelect. "ls") (setup :null-selection :multiselect))
      button (Button. "unselect")]

  (add-btn-click-listener button _
    (doseq [v (.getValue list)]
      (println "unselecting" v)
      (.unselect list v)))

  (doto list
    (.addItem   "1")
    (.addItem   "2")
    (.addItem   "3"))

  (doto (VerticalLayout.)
    (add-components button list))))

                            ;/Users/ajosua/projects/imcode/imcms/imcms/src/main/clojure/VAADIN/themes/imcms/layouts/test.html
(defn custom-layout-demo []
  (let [panel (setup (Panel. "Login") :undefined-size)
        custom (setup (CustomLayout.
                 (java.io.FileInputStream. "/Users/ajosua/projects/imcode/imcms/imcms/src/main/clojure/VAADIN/themes/imcms/layouts/test.html")) ;custom.addStyleName("customlayoutexample")
                 :undefined-size)                                                                                                                               
        txtPassword (TextField.)
        button (Button. "Login")]
    (.setContent panel custom)
    (doto custom
      (.addComponent (TextField.) "username")
      (.addComponent txtPassword "password")
      (.addComponent button "okbutton"))

    (add-btn-click-listener button _
      (.setVisible txtPassword (not (.isVisible txtPassword))))

    panel))


(defn value [component]
  (.getValue component))


(defprotocol Functor
  (fmap [this f]))

(defprotocol Either
  (left? [this])
  (right? [this])
  (either-value [this]))

(deftype Left [value]
  Object
    (toString [_] (format "Left(%s)" value))               
  Either
    (left? [_] true)
    (right? [_] false)
    (either-value [this] value))

(deftype Right [value]
  Object
    (toString [_] (format "Right(%s)" value))                
  Either
    (left? [_] false)
    (right? [_] true)
    (either-value [this] value))

(defprotocol Maybe
  (nothing? [this])
  (just? [this])
  (maybe-value [this]))

(deftype Just[value]
  Object
    (toString [_] (format "Just(%s)" value))             
  Maybe
    (nothing? [_] false)
    (just? [_] true)
    (maybe-value [this] value)
  Functor
    (fmap [this f] (Just. (f value))))

(def Nothing
  (reify
    Object
      (toString [_] "Nothing")
    Maybe
      (nothing? [_] true)
      (just? [_] false)
      (maybe-value [_] (throw (Exception. "Nothing does not have a value.")))
    Functor
      (fmap [this f] this)))




(defn maybe[v] (if v (Just. v) Nothing))


;(defmulti >>= class)
;(defmethod >>= Just [m f & fs])
;(defmethod >>= Nothing [m f & fs])
;
;(defmethod >> Just [m ms])
;(defmethod >> Nothing [m ms])


(defn validate
  "Returns Just error or Nothing if there is no errors."
  [editor]
  (let [ui-components (get-in editor [:ui :components])]
    (if-let [error (or (when (str/blank? (value (:txt-fname ui-components))) "First name can not be blank")
                       (when (str/blank? (value (:txt-sname ui-components))) "Second name can not be blank"))]
      (Just. error)
      Nothing)))


(defn update-model
  "Returns left error or right updated state of the editor's model."
  [editor]
  (let [invalid (validate editor)]
    (if (just? invalid)
      (Left. (maybe-value invalid))
      (let [ui-components (get-in editor [:ui :components])
            model (swap! (:model editor) assoc
                                         :fname (value (:txt-fname ui-components))
                                         :sname (value (:txt-sname ui-components)))]
        (Right. model)))))


;; test
(def i18n-resources {
  :editor-menu-ui {
    :btn-new {:caption "New user" :tooltip "Create new user" :icon nil}
    :btn-edit {:caption "Edit user" :tooltip "Edit selected user" :icon nil}
    :btn-delete {:caption "Delete user" :tooltip "Delete selected user" :icon nil}
  }

  :user-editor-ui {
    :txt-fname {:caption "First name" :tooltip nil}
    :txt-sname {:caption "Second name" :tooltip nil}
    :btn-submit {:caption "Create!" :tooltip nil :icon nil}
  }
})


;; id - UI identity - namespace keyword - used for generic customization (for ex. i18n). Might be nil.
;; content - vaadin container or component
;; components - exposed ui components map
(defrecord UI [^clojure.lang.Keyword id, ^com.vaadin.ui.Component content, ^clojure.lang.IPersistentMap components])

;; ui - UI record instance
;; model - an atomic reference to a model
(defrecord Editor [^UI ui, model])

(defn i18n-ui [^UI ui]
  ;; todo: extract namespace..
  (when-let [id (:id ui)]
    (let [resource-root-id (keyword (namespace id))
          resource-id (keyword (name id))]
      (when-let [resources (get i18n-resources resource-id)]
        (let [components (:components ui)]
          (doseq [[component-id, properties] resources, :let [component (get components component-id)] :when component]
            (when-let [caption (:caption properties)]
              (.setCaption component caption))))))))


(defn editor-menu-ui []
  (let [content (setup (HorizontalLayout.) :spacing)
        btn-new (Button. "New")
        btn-edit (Button. "Edit")
        btn-delete (Button. "Delete")]
    (add-components content btn-new btn-edit btn-delete)
    (UI. ::editor-menu-ui
         content
         {:btn-new btn-new
          :btn-edit btn-edit
          :btn-delete btn-delete})))


(defn user-editor-ui [menu-ui]
  (let [txt-fname (setup (TextField. "Fist name") :resource-caption)
        txt-sname (setup (TextField. "Second name") :resource-caption)
        btn-submit (setup (Button. "Submit") :resource-caption)
        content (setup (GridLayout. 1 3) :spacing)]
    (add-components content (:content menu-ui) txt-fname txt-sname btn-submit)
    (UI. ::user-editor-ui
         content
         {:txt-fname txt-fname,
          :txt-sname txt-sname,
          :btn-submit btn-submit})))


(defn user-editor [model]
  (let [menu-ui (editor-menu-ui)
        menu-ui-components (:components menu-ui)
        ui (user-editor-ui menu-ui)
        ui-components (:components ui)
        editor (Editor. ui model)]

    (doseq [ui [menu-ui menu-ui-components]] (i18n-ui ui))

    (add-btn-click-listener (:btn-new menu-ui-components) _
      (println "Creating new user..."))

    (add-btn-click-listener (:btn-edit menu-ui-components) _
      (println "Editing selected user..."))

    (add-btn-click-listener (:btn-delete menu-ui-components) _
      (println "Deleting selected user..."))

    (add-btn-click-listener (:btn-submit ui-components) _
      (let [update (update-model editor)
            update-value (either-value update)]
        
        (if (left? update)
          (println "Please fix the following error(s):" update-value)
          (println "Model has been updated:" update-value))))

    editor))


(defn mk-main-wnd-content [app]
  (let [content (setup (VerticalLayout.) (:full-size :spacing :margin))
        model (atom {:fname "anton" :sname "josua"})
        editor (user-editor model)
        editor-ui (:ui editor)]

    (add-btn-click-listener (get-in editor-ui [:components :btn-submit]) _
      (println "another handler"))  

    (add-components content
      (get editor-ui :content))))


(defn init[^com.vaadin.Application app]
  (let [wnd (Window. "Main app window")
        content (mk-main-wnd-content app)]

    (.setContent wnd
      (doto (Panel.)
        (.addComponent
          (doto (Embedded.)
            (.setStandby "WAIT!")
            (set-size 100 100)))))

;    (doseq [clazz [Label Button TextField TabSheet Table Tree SplitPanel VerticalLayout HorizontalLayout GridLayout CustomLayout]]
;      (let [obj (.newInstance clazz)]
;        (println (format "class: %s: width: %s, height: %s" (class obj) (.getWidth obj) (.getHeight obj)))))


    (doto app
      (.setTheme "imcms")
      (.setMainWindow wnd))))



