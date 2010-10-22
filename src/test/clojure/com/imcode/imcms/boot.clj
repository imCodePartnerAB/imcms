(ns
  #^{:doc "Bootstrap configuration."}
  com.imcode.imcms.boot)


(System/setProperty "log4j.configuration" "file:src/test/resources/log4j.xml")


(defn ns-resolve*
  "Same as clojure.core/ns-resolve but throws an exception if symbol can not be resolved."
  [ns sym]
  (require ns)
  (or (ns-resolve ns sym)
      (throw (RuntimeException. (format "No such var %s/%s." ns sym)))))