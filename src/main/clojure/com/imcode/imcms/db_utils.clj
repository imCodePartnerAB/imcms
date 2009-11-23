(ns com.imcode.imcms.db-utils
  (:require
    [clojure.contrib.sql :as sql])
  
  (:use
    [clojure.contrib.except :only (throw-if)]
    clojure.contrib.test-is
    clojure.contrib.duck-streams)
  
  (:import
    (com.ibatis.common.jdbc ScriptRunner)))


(def *execute-script-statements* true)


(defn create-url
  "Returns database vendor specific url string."
  [vendor-name host port]
  (let [url-template
         (condp = vendor-name
           "mysql" "jdbc:mysql://%s:%s"
           "mssql" "jdbc:jtds:sqlserver://%s:%s"
           (throw (IllegalArgumentException.
             (format "Unsupported vendor: '%s'. Supported vendors: 'mysql', 'mssql'." vendor-name))))]

    (format url-template host port)))


(defn get-metadata 
  "Returns database metadata"
  [db-spec]
  (sql/with-connection db-spec
    (.getMetaData (sql/connection))))


(defn- println-script-lc
  "Prints script lines count."
  [script script-lc]
  (println (format
    "WARNING! Script [%s, %s lines] statements will not be executed: %s is set to false ."
    script script-lc #'*execute-script-statements* )))


(defn run-script
  "Runs sql script using ScriptRunner"
  ([connection script]
    (run-script connection script false true))

  ([connection script autocommit stop-on-error]
    (with-open [script-reader (reader script)]
      (if *execute-script-statements*
        (doto (ScriptRunner. connection autocommit stop-on-error)
          (.runScript script-reader))

        (println-script-lc script (count (line-seq script-reader)))))))