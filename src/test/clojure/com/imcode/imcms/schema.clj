(ns com.imcode.imcms.schema
  #^{:doc "Project's schema manipulation routines."}
  
  (:use
    [clojure.contrib duck-streams test-is])

  (:require
    [com.imcode.imcms
      [project :as project]
      [file-utils :as file-utils]
      [schema-utils :as schema-utils]]))


(defn db-schema-name [] (:db-name (project/build-properties)))
(defn test-db-schema-name [] (str (db-schema-name) "_test"))


(def xml-conf-file (project/get-file-fn "src/main/resources-conf/schema-upgrade.xml"))
(def xsd-conf-file (project/get-file-fn "src/main/resources-conf/schema-upgrade.xsd"))
(defn scripts-dir [] (project/subdir "src/main/sql"))


(defn xml-conf-file-path [] (.getCanonicalPath (xml-conf-file)))
(defn xsd-conf-file-path [] (.getCanonicalPath (xsd-conf-file)))
(defn scripts-dir-path [] (.getCanonicalPath (scripts-dir)))


(def slurp-xml-conf (file-utils/call-if-modified xml-conf-file slurp*))
(def slurp-xsd-conf (file-utils/call-if-modified xsd-conf-file slurp*))


(defn init-script-files
  []
  ;(project/files "src/main/sql" ["imcms_6.1_schema.sql" "imcms_6.1_data.sql"]))
  (project/files "src/main/sql" ["imcms_5.3_schema.sql" "imcms_5.3_data.sql" "diff/mysql-schema-diff-5.3-6.2.sql"]))


(defn version
  ([]
    (version (db-schema-name)))

  ([name]
    (schema-utils/version (project/db-spec) name)))


(defn tables
  ([]
    (tables (db-schema-name)))

  ([schema-name]
    (schema-utils/tables (project/db-spec) schema-name)))


(defn delete
  ([]
    (delete (db-schema-name)))

  ([schema-name]
    (schema-utils/delete (project/db-spec) schema-name)))


(defn recreate
  "Recreates datatabse schema or PROJECT schema if schema name is not given."
  ([]
    (recreate (db-schema-name)))

  ([schema-name]
    (recreate schema-name (init-script-files)))  

  ([schema-name scripts]
    (schema-utils/recreate (project/db-spec) schema-name scripts)))


(defn recreate-empty
  "Recreates empty datatabse schema or PROJECT schema if schema name is not given."
  ([]
    (recreate (db-schema-name) []))

  ([schema-name]
    (recreate schema-name [])))


(defn recreate-sandbox
  []
  "Recreates SANDBOX datatabse schema."
  (let [script-files (project/files "src/test/sql" ["sandbox.sql"])]
    (recreate "sandbox" script-files)))



(defn upgrade
  ([]
    (upgrade (db-schema-name)))

  ([schema-name]
    (upgrade schema-name (xml-conf-file) (xsd-conf-file) (scripts-dir)))

  ([schema-name p_xml-conf-file p_xsd-conf-file p_scripts-dir]
    (schema-utils/upgrade (project/db-spec) schema-name p_xml-conf-file p_xsd-conf-file p_scripts-dir)))


(defn recreate-empty-upgrade
  "Recreates empty schema and runs upgrade."
  ([]
    (recreate-empty-upgrade (db-schema-name)))

  ([schema-name]
    (recreate-empty schema-name)
    (upgrade schema-name)))


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

  (is (= 3 (count (init-script-files)))))


