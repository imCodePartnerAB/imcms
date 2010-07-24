(ns
  #^{:doc "Bootstrap configuration."}
  com.imcode.imcms.boot)

(System/setProperty "log4j.configuration" "file:src/test/resources/log4j.xml")


(def m-require (memoize require))

(defn m-ns-resolve [ns-sym sym]
  (m-require ns-sym)
  (or (ns-resolve ns-sym sym)
      (throw (RuntimeException. (format "No such var %s/%s." ns-sym sym)))))