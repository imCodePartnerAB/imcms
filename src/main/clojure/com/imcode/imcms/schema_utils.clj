(ns com.imcode.imcms.schema-utils
  (:require
    [com.imcode.imcms
      [db-utils :as db-utils]]
    
    [clojure.contrib [sql :as sql]])

  (:use
    [clojure.contrib.except :only (throw-if)]
    clojure.contrib.test-is)

  (:import
    (java.io File)
    (com.imcode.imcms.schema Diff DiffBuilder Vendor SchemaUpgrade)))


(defn upgrade
  [db-spec schema-name xml-conf-file xsd-conf-file scripts-dir]
  (sql/with-connection db-spec
    (sql/transaction
      (sql/do-commands
        (format "use %s" schema-name))
      
      (doto (SchemaUpgrade/createInstance xml-conf-file xsd-conf-file scripts-dir)
        (.upgrade (sql/connection))))))


(defn tables
  "Returns sequence of schema tables names."
  [db-spec schema-name]
  (sql/with-connection db-spec
    (sql/do-commands
      (format "use %s" schema-name))

    (sql/with-query-results rs ["show tables"]
      (map #(first (vals %)) (doall rs)))))


(defn version
  "Returns schema version."
  [db-spec db-name]
  (sql/with-connection db-spec
    (sql/do-commands
      (format "use %s" db-name))

    (SchemaUpgrade/getSchemaVersion (sql/connection))))


(defn recreate
  "Recreates schema"
  [db-spec schema-name scripts]
  (sql/with-connection db-spec
    (sql/transaction
      (sql/do-commands
        (format "drop database if exists %s" schema-name)
        (format "create database %s" schema-name)
        (format "use %s" schema-name))

      (doseq [script scripts]
        (db-utils/run-script (sql/connection) script)))))


(defn delete
  "Deletes schema."
  [db-spec schema-name]
  (sql/with-connection db-spec
    (sql/do-commands
      (format "drop database if exists %s" schema-name))))
