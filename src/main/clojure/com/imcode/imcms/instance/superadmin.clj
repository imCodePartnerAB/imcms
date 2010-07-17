(ns
  #^{:doc "Vaadin superadmin app."}
  com.imcode.imcms.instance.superadmin

  (:import com.vaadin.Application
    (com.vaadin.ui Window Tree SplitPanel Label)
    (com.vaadin.event ItemClickEvent ItemClickEvent$ItemClickListener)))


(defn init [#^Application app]
  (let [window (Window. "Main window")
        splitPanel (SplitPanel. SplitPanel/ORIENTATION_HORIZONTAL)
        tree (Tree.)]

    (dotimes [n 10]
      (.addItem tree (str "Item " n)))

    (.setMainWindow app window)
    (.setContent window splitPanel)
    (.setFirstComponent splitPanel tree)
    (.addListener tree (proxy [ItemClickEvent$ItemClickListener] []
                         (itemClick [#^ItemClickEvent e]
                           (.setSecondComponent splitPanel (Label. (.getItemId e))))))))
