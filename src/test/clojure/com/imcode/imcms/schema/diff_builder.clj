(ns com.imcode.imcms.schema.diff-builder
  (:require
    [com.imcode.imcms.schema-settings :as ss])  
  (:use
    clojure.contrib.test-is)
  (:import
    (com.imcode.imcms.schema Diff DiffBuilder Vendor)))


(deftest build-xpath-expression
  (is (= (format DiffBuilder/XPATH_TEMPLATE__SELECT_SCRIPTS_LOCATIONS 6.11 Vendor/mysql)
         "/schema-upgrade/diff[@version = 6.11]/vendor[@name = 'mysql']/script/@location"))

  (is (= (format DiffBuilder/XPATH_TEMPLATE__SELECT_SCRIPTS_LOCATIONS 6.12 Vendor/mssql)
         "/schema-upgrade/diff[@version = 6.12]/vendor[@name = 'mssql']/script/@location")))


(deftest get-versions-numbers
  (is (= (DiffBuilder/getVersionsNumbers (ss/slurp-xml-conf))  [5.0 5.1 5.2 5.3 6.0 6.1 6.2])))


(deftest get-scripts-locations
  (let [conf (ss/slurp-xml-conf)]
    (is (= (DiffBuilder/getScriptsLocations conf 5.0 Vendor/mysql) ["imcms_5.0_schema.sql" "imcms_5.0_data.sql"]))
    (is (= (DiffBuilder/getScriptsLocations conf 5.0 Vendor/mssql) []))

    (is (= (DiffBuilder/getScriptsLocations conf 6.1 Vendor/mysql) ["diff/mysql-schema-diff-6.0-6.1.sql"]))
    (is (= (DiffBuilder/getScriptsLocations conf 6.1 Vendor/mssql) []))

    (is (= (DiffBuilder/getScriptsLocations conf 6.2 Vendor/mysql) ["diff/mysql-schema-diff-6.1-6.2.sql"]))
    (is (= (DiffBuilder/getScriptsLocations conf 6.2 Vendor/mssql) []))))