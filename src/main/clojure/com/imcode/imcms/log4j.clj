(ns
  #^{:doc "Log4j loggers access."}
  
  com.imcode.imcms.log4j
  (:import
    (org.apache.log4j Level LogManager Logger)
    (org.apache.log4j.xml DOMConfigurator)))


(def levels (into {}
                  (map #(vector (str %) %)
                        [Level/ALL Level/DEBUG Level/ERROR Level/FATAL Level/INFO Level/OFF Level/TRACE Level/WARN])))


(defmulti to-level class)

(defmethod to-level Level [l] l)
(defmethod to-level String [l] (get levels (.toUpperCase l)))
(defmethod to-level clojure.lang.Keyword [l] (to-level (name l)))

(defn logger [name-or-class]
  (Logger/getLogger name-or-class))


(defn loggers
  "Returns seq of loggers sodter by name which level is not null."
  []
  (sort-by #(.getName %)
           (filter #(.getLevel %) (enumeration-seq (LogManager/getCurrentLoggers)))))


(defn loggers-names []
  (map #(.getName %) (loggers)))


(defn set-level [name-or-class level]
  (doto (logger name-or-class)
        (.setLevel (to-level level))))


(defn reload []
  (LogManager/shutdown)
  (let [url (ClassLoader/getSystemResource "log4j.xml")]
    (DOMConfigurator/configure url)))


(defn print-loggers []
  (doseq [l (loggers)]
    (printf "%s, %s.%n" (.getName l), (.getLevel l))))