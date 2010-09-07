(ns
  #^{:doc "Spring utils."}
  com.imcode.imcms.spring)


(defn get-bean
  "Returns bean from the app context."
  [app-context name]
  (.getBean app-context name))


(defn defbean
  "Defines spring bean.
   app-context is an instance of ApplicationContext."
  ([app-context bean-name]
    (defbean app-context bean-name bean-name))

  ([app-context bean-alias bean-name]
    (intern *ns* (symbol bean-alias) (get-bean app-context bean-name))))