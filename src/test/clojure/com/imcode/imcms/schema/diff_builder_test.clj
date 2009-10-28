(ns com.imcode.imcms.schema.diff-builder-test
  (:use
    clojure.contrib.test-is)
  (:import
    (com.imcode.imcms.schema Diff DiffBuilder Vendor)))

(defn slurp-conf [] (slurp "src/test/resources/schema-upgrade.xml"))

(deftest test-create-diff-filenames-expression
  (is (= (format DiffBuilder/XPATH_TEMPLATE__SELECT_SCRIPTS_LOCATIONS 6.11 Vendor/mysql)
         "/schema-upgrade/diff[@version = 6.11]/vendor[@name = 'mysql']/script/@location"))

  (is (= (format DiffBuilder/XPATH_TEMPLATE__SELECT_SCRIPTS_LOCATIONS 6.12 Vendor/mssql)
         "/schema-upgrade/diff[@version = 6.12]/vendor[@name = 'mssql']/script/@location")))


(deftest test-get-versions
  (is (= (DiffBuilder/getVersionsNumbers (slurp-conf))  [5.0 5.1 5.2 5.3 6.0 6.1 6.2])))


(deftest test-get-diff-pathnames
  (let [conf (slurp-conf)]
    (is (= (DiffBuilder/getScriptsLocations conf 5.0 Vendor/mysql) ["imcms_5.0_schema.sql" "imcms_5.0_data.sql"]))
    (is (= (DiffBuilder/getScriptsLocations conf 5.0 Vendor/mssql) []))

    (is (= (DiffBuilder/getScriptsLocations conf 6.1 Vendor/mysql) ["diff/mysql-schema-diff-6.0-6.1.sql"]))
    (is (= (DiffBuilder/getScriptsLocations conf 6.1 Vendor/mssql) []))

    (is (= (DiffBuilder/getScriptsLocations conf 6.2 Vendor/mysql) ["diff/mysql-schema-diff-6.1-6.2.sql"]))
    (is (= (DiffBuilder/getScriptsLocations conf 6.2 Vendor/mssql) []))))