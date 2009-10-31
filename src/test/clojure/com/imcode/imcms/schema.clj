(ns com.imcode.imcms.schema
  (:use
    clojure.contrib.test-is)

  (:require
    [com.imcode.imcms
      [project :as project]
      [schema-utils :as schema-utils]]))


(def schema-name "imcms_trunk")  
(def test-schema-name "imcms_trunk_test")


(defn xml-conf-file [] (project/file "src/main/resources-conf/schema-upgrade.xml"))
(defn xsd-conf-file [] (project/file "src/main/resources-conf/schema-upgrade.xsd"))
(defn scripts-dir [] (project/subdir "src/main/sql"))


(defn xml-conf-file-path [] (.getCanonicalPath (xml-conf-file)))
(defn xsd-conf-file-path [] (.getCanonicalPath (xsd-conf-file)))
(defn scripts-dir-path [] (.getCanonicalPath (scripts-dir)))


(defn slurp-xml-conf [] (slurp (xml-conf-file-path)))
(defn slurp-xsd-conf [] (slurp (xsd-conf-file-path)))


(defn init-script-files
  []
  (project/files "src/main/sql" ["imcms_6.1_schema.sql" "imcms_6.1_data.sql"]))


(defn schema-version
  ([]
    (schema-version (:db-name (project/load-build-properties))))

  ([schema-name]
    (schema-utils/get-version (project/db-spec) schema-name)))


(defn tables
  ([]
    (tables (:db-name (project/load-build-properties))))

  ([schema-name]
    (schema-utils/tables (project/db-spec) schema-name)))


(defn delete-tables
  ([]
    (delete-tables (:db-name (project/load-build-properties))))

  ([schema-name]
    (schema-utils/delete-tables (project/db-spec) schema-name)))


(defn has-tables?
  ([]
    (has-tables? (:db-name (project/load-build-properties))))

  ([schema-name]
    (schema-utils/has-tables? (project/db-spec) schema-name)))


(defn delete
  ([]
    (delete (:db-name (project/load-build-properties))))

  ([schema-name]
    (schema-utils/delete (project/db-spec) schema-name)))


(defn recreate
  "Recreates datatabse schema or PROJECT schema if schema name is not given."
  ([]
    (recreate (:db-name (project/load-build-properties)) (init-script-files)))

  ([schema-name scripts]
    (schema-utils/recreate (project/db-spec) schema-name scripts)))


(defn recreate-sandbox
  []
  "Recreates SANDBOX datatabse schema."
  (let [script-files (project/files "src/test/sql" ["sandbox.sql"])]
    (recreate "sandbox" script-files)))


(deftest test-settings
  (testing "Files/Dirs"
    (println "xml-conf-file: " (xml-conf-file))
    (println "xsd-conf-file: " (xsd-conf-file))
    (println "scripts-dir: " (scripts-dir)))

  (testing "Paths"
    (println "xml-conf-file-path: " (xml-conf-file-path))
    (println "xsd-conf-file-path: " (xsd-conf-file-path))
    (println "scripts-dir-path: " (scripts-dir-path)))

  (testing "Contents"
    (println "xml-conf-file content length: " (count (slurp-xml-conf)))
    (println "xsd-conf-file content length: " (count (slurp-xsd-conf))))

  (is (= 2 (count (init-script-files)))))


