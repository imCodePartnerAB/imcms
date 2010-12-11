(ns
  ^{:doc "Vaadin test/prototype application handler."}
  com.imcode.imcms.vaadin-app-handler
  
  (:require
    (clojure.java [io :as io]))

  (:import
    (java.net URL)
    com.vaadin.Application
    (scala Some None$)
    (com.vaadin.ui Window SplitPanel Button Panel Label Button$ClickListener Embedded GridLayout HorizontalLayout
                   FormLayout VerticalLayout Alignment TextField CheckBox MenuBar MenuBar$MenuItem MenuBar$Command
                   Select ListSelect TabSheet)

   ; (com.vaadin.ui.themes BaseTheme)

    (com.vaadin.terminal ExternalResource ClassResource FileResource ThemeResource)
    (com.vaadin.data Property Property$ValueChangeListener)
    (com.imcode.imcms.vaadin OkCancelDialog)
    (com.imcode.imcms.sysadmin.filemanager FileBrowser FileBrowserWithImagePreview)
    com.vaadin.data.util.ObjectProperty))


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


(defn with
  "Sets properties to an object and returns it."
  [obj keyword & keywords]
  (doseq [kw (cons keyword keywords)]
    (condp = kw
      :scrollable (.setScrollable obj true)
      :unscrollable (.setScrollable obj false)
      :checked (.setValue obj true)
      :unchecked (.setValue obj false)
      :enabled (.setEnabled obj true)
      :disabled (.setEnabled obj false)
      :spacing (.setSpacing obj true)
      :no-spacing (.setSpacing obj false)
      :margin (.setMargin obj true)
      :no-margin (.setMargin obj false)
      :multiselect (.setMultiSelect obj true)
      :no-multiselect (.setMultiSelect obj false)
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


(defn mk-components-value-exchange-demo
  "A componnent can be assigned as a datasource of another component.
   In that case any value change throug API or UI controller automatically used by enother.
   There is no difference between source and target component - any changes to target also reflects source component."
  []
  (let [lyt (-> (VerticalLayout.) (with :spacing :margin))
        txt-src (-> (TextField. "source") (with :immediate))
        txt-target (-> (TextField. "target" txt-src) (with :immediate))
        btn-clear-src (Button. "clear source")
        btn-clear-target (Button. "clear target")]
    (add-btn-click-listener btn-clear-src _
      (.setValue txt-src ""))
    (add-btn-click-listener btn-clear-target _
      (.setValue txt-target ""))

    (add-components lyt
      txt-src txt-target btn-clear-src btn-clear-target)))


;        lst (doto (ListSelect. "list") (with :immediate :no-multiselect :undefined-size :null-selection)
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
  (let [ts (-> (TabSheet.) (with :enabled))
        lyt1 (doto (FormLayout.) (with :margin :undefined-size)
               (.setCaption "t1") (add-components ,, (TextField. "text1"), (TextField. "text2"), (Button. "button")))
        lyt2 (doto (FormLayout.) (with :margin :undefined-size)
               (.setCaption "t2") (add-components ,, (TextField. "text1"), (TextField. "text2"), (Button. "button") (Button. "button")))
        tab1 (.addTab ts lyt1)
        tab2 (.addTab ts lyt2)]

    ;;(doto lyt2 (.setWidth "800px") (.setHeight "800px"))
    ;;(doto ts (.setWidth "500px") (.setHeight "300px"))
    ts))

(defn mk-chk-box-handler-demo
  "Check box does not send event to click listeners while holding focus unless its set immediate."
  []
  (let [chk-default (with (CheckBox. "Check box with default behaviour - related button will become enabled/disabled only after focus is lost.")
                      :checked)
        btn-default (Button. "Button tied to check box with default behavior.")
        chk-immediate (with (CheckBox. "Check box with imediate behaviour - related button become enabled/disabled immediately.")
                        :checked :immediate)
        btn-immediate (Button. "Button tied to check box with immediate behavior.")]

    (add-btn-click-listener chk-default e
      (.setEnabled btn-default (. chk-default getValue)))

    (add-btn-click-listener chk-immediate e
      (.setEnabled btn-immediate (. chk-immediate getValue)))

    (doto (VerticalLayout.) (with :spacing :margin)
      (add-components ,, chk-default btn-default chk-immediate btn-immediate))))


(defn mk-panel-test []
  (let [content (doto (HorizontalLayout.) (with :undefined-size))
        panel (doto (Panel.) (with :scrollable :full-size)
                (.setContent content))]

    (dotimes [i 10] (.addComponent content (Button. (str "button " i))))

    (set-size panel 900 900)

    (doto (VerticalLayout.)
      (with :full-size)
      (.addComponent panel))))


(defn mk-main-wnd-content [app]
  (let [content (-> (VerticalLayout.) (with :spacing :margin))]
    (add-components content
      (mk-panel-test))))


(defn init[^com.vaadin.Application app]
  (let [wnd (Window. "Main app window")
        content (mk-main-wnd-content app)]

    (.setContent wnd content)

    (doto app
      ;(.setTheme "runo")
      (.setMainWindow wnd))))



