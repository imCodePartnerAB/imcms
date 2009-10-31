(ns com.imcode.imcms.schema.upgrade
  (:require
    [clojure.contrib.sql :as sql]
    [com.imcode.imcms.schema :as schema])
  
  (:use
    clojure.contrib.test-is)
  
  (:import
    (com.imcode.imcms.schema SchemaUpgrade Vendor)
    (java.io File)))


(deftest test-validate-and-read-upgrade-conf
  (SchemaUpgrade/validateAndReadUpgradeConf (schema/xml-conf-file) (schema/xsd-conf-file)))


(deftest test-create-instance
  (SchemaUpgrade/createInstance (schema/xml-conf-file) (schema/xsd-conf-file) (schema/scripts-dir)))


(deftest test-upgrade
  (schema/recreate (schema/test-db-schema-name))
  (schema/upgrade (schema/test-db-schema-name)))


(deftest test-upgrade-empty
  (let [schema-name (schema/test-db-schema-name)]
    (schema/recreate-empty schema-name)

    (is (empty? (schema/tables schema-name)))
    
    (schema/upgrade schema-name)))