(ns
  #^{:doc "Spring utils."}
  com.imcode.imcms.spring-utils)


(defn get-bean
  "Returns bean from the app context."
  [app-context name]
  (.getBean app-context name))


(defmacro defbean
  ""
  ([app-context bean-name]
    (defbean app-context bean-name bean-name))

  ([app-context bean-alias bean-name]
    `(def ~bean-alias (get-bean ~app-context (name '~bean-name)))))


;(defbean metaDao)
;(defbean languageDao)
;(defbean contentLoopDao)

