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
  (is (= (DiffBuilder/getVersionsNumbers (slurp-conf))  [6.11 6.12 6.13])))


(deftest test-get-diff-pathnames
  (let [conf (slurp-conf)]
    (is (= (DiffBuilder/getScriptsLocations conf 6.11 Vendor/mysql) ["A.sql" "B.sql"]))
    (is (= (DiffBuilder/getScriptsLocations conf 6.11 Vendor/mysql) ["A.sql" "B.sql"]))
    (is (= (DiffBuilder/getScriptsLocations conf 6.11 Vendor/mssql) ["A$.sql" "B$.sql"]))

    (is (= (DiffBuilder/getScriptsLocations conf 6.12 Vendor/mysql) ["C.sql" "D.sql"]))
    (is (= (DiffBuilder/getScriptsLocations conf 6.12 Vendor/mssql) ["C$.sql" "D$.sql"]))

    (is (= (DiffBuilder/getScriptsLocations conf 6.13 Vendor/mysql) ["E.sql" "F.sql"]))
    (is (= (DiffBuilder/getScriptsLocations conf 6.13 Vendor/mssql) ["E$.sql" "F$.sql"]))))