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
         (cond
           (= vendor-name "mysql") "jdbc:mysql://%s:%s"
           (= vendor-name "mssql") "jdbc:jtds:sqlserver://%s:%s"
           :else (throw (IllegalArgumentException.
               (format "Invalid vendor: '%s'. Valid values: 'mysql', 'mssql'." vendor-name))))]

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


(defn recreate-schema
  "Recreates schema"
  [db-spec schema-name scripts]
  (sql/with-connection db-spec
    (sql/transaction
      (sql/do-commands
        (format "drop database if exists %s" schema-name)
        (format "create database %s" schema-name)
        (format "use %s" schema-name))

      (doseq [script scripts]
        (run-script (sql/connection) script)))))


(defn get-schema-tables
  [db-spec schema-name]
  (sql/with-connection db-spec
    (sql/do-commands
      (format "use %s" schema-name))

    (sql/with-query-results rs ["show tables"] (doall rs))))


(defn delete-schema
  "Deletes schema."
  [db-spec schema-name]
  (sql/with-connection db-spec
    (sql/do-commands
      (format "drop database if exists %s" schema-name))))

(defn empty-schema?
  [db-spec schema-name]
  (empty? (get-schema-tables db-spec schema-name)))


(defn empty-schema
  [db-spec schema-name]
  (recreate-schema db-spec schema-name []))


