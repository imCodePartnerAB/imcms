(ns com.imcode.imcms.schema.SchemaUpgradeTest
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


(deftest test-upgrade-existing
  (let [schema-name (schema/test-db-schema-name)]
    (schema/recreate schema-name)
    (schema/upgrade schema-name)))


(deftest test-upgrade-empty
  (let [schema-name (schema/test-db-schema-name)]
    (schema/recreate-empty schema-name)

    (is (empty? (schema/tables schema-name)))
    
    (schema/upgrade schema-name)))


