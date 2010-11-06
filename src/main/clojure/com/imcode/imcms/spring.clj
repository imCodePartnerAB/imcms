(ns
  ^{:doc "Spring utils."}
  com.imcode.imcms.spring

  (:import
    (org.springframework.orm.hibernate3 HibernateCallback HibernateTemplate)))


(defn get-bean
  "Returns bean from the app context.
   bean-name must evaluate into a string or a symbol."
  [app-context bean-name]
  (.getBean app-context (if (symbol? bean-name) (name bean-name) bean-name)))


(defmacro defbean
  "Defines spring bean var in the current namespace."
  ([app-context bean-name-sym]
    (defbean app-context bean-name-sym bean-name-sym))

  ([app-context bean-name-sym bean-alias-sym]
    `(def ~bean-alias-sym (get-bean ~app-context (symbol '~bean-name-sym)))))


(defn do-in-hibernate [^HibernateTemplate template callback-fn]
  (let [callback (reify HibernateCallback
                   (doInHibernate [this, session]
                     (callback-fn session)))]

    (.execute template callback-fn))) 