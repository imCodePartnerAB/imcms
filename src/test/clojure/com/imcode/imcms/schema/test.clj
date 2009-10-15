(ns com.imcode.imcms.schema.test
  (:use
    clojure.contrib.test-is)
  (:import
    (com.imcode.imcms.schema Diff Upgrade Vendor)))

(defn upgrade-conf [] (slurp "src/test/resources/schema-upgrade.xml"))

(deftest test-create-diff-filenames-expression
  (is (= (format Upgrade/XPATH_TEMPLATE__SELECT_DIFF_LOCATIONS 6.11 Vendor/mysql)
         "/schema-upgrade/diff[@version = 6.11]/vendor[@name = 'mysql']/script/@location"))

  (is (= (format Upgrade/XPATH_TEMPLATE__SELECT_DIFF_LOCATIONS 6.12 Vendor/mssql)
         "/schema-upgrade/diff[@version = 6.12]/vendor[@name = 'mssql']/script/@location")))


(deftest test-get-versions
  (is (= (Upgrade/getVersions (upgrade-conf))  [6.11 6.12 6.13])))


(deftest test-get-diff-pathnames
  (let [conf (upgrade-conf)]
    (is (= (Upgrade/getDiffLocations conf 6.11 Vendor/mysql) ["A.sql" "B.sql"]))
    (is (= (Upgrade/getDiffLocations conf 6.11 Vendor/mysql) ["A.sql" "B.sql"]))
    (is (= (Upgrade/getDiffLocations conf 6.11 Vendor/mssql) ["A$.sql" "B$.sql"]))

    (is (= (Upgrade/getDiffLocations conf 6.12 Vendor/mysql) ["C.sql" "D.sql"]))
    (is (= (Upgrade/getDiffLocations conf 6.12 Vendor/mssql) ["C$.sql" "D$.sql"]))

    (is (= (Upgrade/getDiffLocations conf 6.13 Vendor/mysql) ["E.sql" "F.sql"]))
    (is (= (Upgrade/getDiffLocations conf 6.13 Vendor/mssql) ["E$.sql" "F$.sql"]))))