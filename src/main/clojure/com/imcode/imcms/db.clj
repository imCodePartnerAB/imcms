(ns 
  #^{:doc "Database utils."}
  com.imcode.imcms.db
  (:require
    (clojure.contrib [sql :as sql]))
  
  (:use
    [clojure.contrib.except :only (throw-if)]
    [clojure.contrib.def :only (defvar)]
    [clojure.string :only (join lower-case)]
    [com.imcode.imcms.misc :only (dump)]
    [clojure.java.io :only (reader)])
  
  (:import
    (org.apache.commons.dbcp BasicDataSource) 
    (com.ibatis.common.jdbc ScriptRunner)))


(defvar *execute-script-statements* true
  "Controls if script's statement will be executed by run-script fn.")


(defn create-url
  "Creates database vendor specific url string."
  ([vendor-name host port]
    (let [url-template (condp = (lower-case vendor-name)
                         "mysql" "jdbc:mysql://%s:%s"
                         "mssql" "jdbc:jtds:sqlserver://%s:%s"
                         (throw (IllegalArgumentException.
                           (format "Unsupported vendor: '%s'. Supported vendors: 'mysql', 'mssql'." vendor-name))))]

      (format url-template host port)))

  ([vendor-name host port schema-name]
    (str (create-url vendor-name host port) "/" schema-name)))


(defn create-ds
  "Creates DBCP datasource."
  [driver-class-name username password url]
  (doto (BasicDataSource.)
    (.setDriverClassName driver-class-name)
    (.setUsername username)
    (.setPassword password)
    (.setUrl url)))


(defn create-mysql-ds
  "Creates MySQL DBCP datasource."
  ([username password]
     (create-mysql-ds "localhost" 3306 username password))

  ([host port username password]
     (create-ds "com.mysql.jdbc.Driver" username password (create-url "mysql" host port))))


(defn create-spec
  "Creates db-spec from datasource which is used by clojure.contrib.sql fns."
  [ds]
  {:datasource ds})


(defn create-h2-mem-spec 
  ([]
     (create-h2-mem-spec ""))

  ([name & params]
    {:classname "org.h2.Driver"
     :subprotocol "h2"
     :subname (str "mem:" name (join ";" params))}))


(defn- print-script-info
  "Prints SQL script name and lines count.
  This fn is called when *execute-script-statements* is set to false."
  [script script-lc]
  (println (format
    "WARNING! Script [%s, %s lines] statements will not be executed: %s is set to false ."
    script script-lc #'*execute-script-statements*)))


(defn run-script
  "Runs sql script using existing connection."
  ([connection script]
    (run-script connection script false true))

  ([connection script autocommit stop-on-error]
    (with-open [script-reader (reader script)]
      (if *execute-script-statements*
        (doto (ScriptRunner. connection autocommit stop-on-error)
          (.runScript script-reader))

        (print-script-info script (count (line-seq script-reader)))))))


(defn run-scripts
  "Run sql scripts."
  ([spec scripts]
    (run-scripts spec nil scripts))

  ([spec schema-name scripts]
    (sql/with-connection spec
      (sql/transaction
        (when schema-name
          (sql/do-commands
            (format "use %s" schema-name)))

        (doseq [script scripts]
          (run-script (sql/connection) script))))))

  
(defn recreate
  "Recreates databse."
  [spec schema-name scripts]
  (sql/with-connection spec
    (sql/transaction
      (sql/do-commands
        (format "drop database if exists %s" schema-name)
        (format "create database %s" schema-name)
        (format "use %s" schema-name))

      (doseq [script scripts]
        (run-script (sql/connection) script)))))


(defn delete
  "Deletes database."
  [spec schema-name]
  (sql/with-connection spec
    (sql/do-commands
      (format "drop database if exists %s" schema-name))))


(defn metadata 
  "Returns database metadata"
  [spec]
  (sql/with-connection spec
    (.getMetaData (sql/connection))))


(defn print-metadata [spec]
  (dump (metadata spec)))