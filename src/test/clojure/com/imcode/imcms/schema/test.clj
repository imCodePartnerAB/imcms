(ns com.imcode.imcms.schema.test
  (:use
    clojure.contrib.test-is)
  (:import
    (com.imcode.imcms.schema Diff Upgrade Vendor Version)))

(defn upgrade-conf [] (slurp "src/test/resources/schema-upgrade.xml"))


(deftest test-create-diff-filenames-expression
  (is (= (Upgrade/createDiffFilenamesExpression "6.11" "mysql") "/schema-upgrade/diff[@version=6.11]/vendor[@name='mysql']/script/@location"))
  (is (= (Upgrade/createDiffFilenamesExpression "6.12" "mssql") "/schema-upgrade/diff[@version=6.12]/vendor[@name='mssql']/script/@location")))


(deftest test-get-versions
  (is (= (Upgrade/getVersions (upgrade-conf)) ["6.11" "6.12" "6.13"])))


(deftest test-get-diff-pathnames
  (let [conf (upgrade-conf)]
    (is (= (Upgrade/getDiffFilenames conf "6.11" "mysql") ["A.sql" "B.sql"]))
    (is (= (Upgrade/getDiffFilenames conf "6.11" "mssql") ["A$.sql" "B$.sql"]))

    (is (= (Upgrade/getDiffFilenames conf "6.12" "mysql") ["C.sql" "D.sql"]))
    (is (= (Upgrade/getDiffFilenames conf "6.12" "mssql") ["C$.sql" "D$.sql"]))

    (is (= (Upgrade/getDiffFilenames conf "6.13" "mysql") ["E.sql" "F.sql"]))
    (is (= (Upgrade/getDiffFilenames conf "6.13" "mssql") ["E$.sql" "F$.sql"]))))