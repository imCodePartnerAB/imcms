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


(defn get-version
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


(defn tables
  [db-spec schema-name]
  (sql/with-connection db-spec
    (sql/do-commands
      (format "use %s" schema-name))

    (sql/with-query-results rs ["show tables"]
      (map #(str %) (doall rs)))))


(defn has-tables?
  [db-spec schema-name]
  (empty? (tables db-spec schema-name)))


(defn delete-tables
  [db-spec schema-name]
  (recreate db-spec schema-name []))


(defn delete
  "Deletes schema."
  [db-spec schema-name]
  (sql/with-connection db-spec
    (sql/do-commands
      (format "drop database if exists %s" schema-name))))
