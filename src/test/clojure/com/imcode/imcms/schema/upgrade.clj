(ns com.imcode.imcms.schema.upgrade
  (:require
    [clojure.contrib.sql :as sql]
    [com.imcode.imcms.schema.fixture :as fixture])
  
  (:use
    clojure.contrib.test-is
    [com.imcode.imcms.project-utils :only [db-spec]])
  
  (:import
    (com.imcode.imcms.schema SchemaUpgrade Vendor)
    (java.io File)))


(deftest validate-and-read-upgrade-conf
  (SchemaUpgrade/validateAndReadUpgradeConf fixture/xml-conf-file fixture/xsd-conf-file))

(deftest create-instance
  (SchemaUpgrade/validateAndReadUpgradeConf fixture/xml-conf-file fixture/xsd-conf-file))


(deftest upgrade
  (sql/with-connection (db-spec)
    (sql/transaction
      (sql/do-commands
        (format "drop database if exists %s" fixture/schema-name)
        (format "create database %s" fixture/schema-name)
        (format "use %s" fixture/schema-name))

      (doto (SchemaUpgrade/createInstance fixture/xml-conf-file fixture/xsd-conf-file fixture/scripts-dir)
        (.upgrade (sql/connection))))))