(ns
  #^{:doc "Project's databse schema fns."}
  com.imcode.imcms.project.db.schema
  
  (:use
    (clojure.contrib duck-streams test-is))

  (:require
    (com.imcode.imcms
      [project :as project])

    (com.imcode.imcms.project
      [db :as db])    

    (com.imcode.cljlib
      [file-utils :as file-utils])

    (com.imcode.cljlib.db
      [schema :as schema-lib])

    (clojure.contrib [sql :as sql]))

  (:import
    com.imcode.imcms.schema.SchemaUpgrade))

(def xml-conf-file (project/get-file-fn "src/main/resources-conf/schema-upgrade.xml"))
(def xsd-conf-file (project/get-file-fn "src/main/resources-conf/schema-upgrade.xsd"))
(defn scripts-dir [] (project/subdir "src/main/sql"))


(defn xml-conf-file-path [] (.getCanonicalPath (xml-conf-file)))
(defn xsd-conf-file-path [] (.getCanonicalPath (xsd-conf-file)))
(defn scripts-dir-path [] (.getCanonicalPath (scripts-dir)))


(def slurp-xml-conf (file-utils/create-file-watcher xml-conf-file slurp*))
(def slurp-xsd-conf (file-utils/create-file-watcher xsd-conf-file slurp*))


(defn init-script-files
  []
  ;(project/files "src/main/sql" ["imcms_6.1_schema.sql" "imcms_6.1_data.sql"]))
  ;(project/files "src/main/web/WEB-INF/sql" ["imcms_5.3_schema.sql" "imcms_5.3_data.sql" "diff/mysql-schema-diff-5.3-6.2.sql"]))
  (project/files "src/main/web/WEB-INF/sql" ["imcms_rb4.sql" "diff/mysql-schema-diff-4.11-6.2.sql"]))

(defn version-d
  "Returns imCMS schema version."
  [db-spec db-name]
  (sql/with-connection db-spec
    (sql/do-commands
      (format "use %s" db-name))

    (SchemaUpgrade/getSchemaVersion (sql/connection))))

(defn version
  ([]
    (version (project/db-schema-name)))

  ([name]
    (version-d (db/create-spec) name)))


(defn tables
  ([]
    (tables (project/db-schema-name)))

  ([schema-name]
    (schema-lib/tables (db/create-spec) schema-name)))


(defn delete
  ([]
    (delete (project/db-schema-name)))

  ([schema-name]
    (schema-lib/delete (db/create-spec) schema-name)))


(defn- recreate-custom
  "Recreates datatabse schema."
  ([db-spec-fn schema-name-fn]
    (recreate-custom db-spec-fn schema-name-fn (init-script-files)))

  ([db-spec-fn schema-name-fn scripts]
    (schema-lib/recreate (db-spec-fn) (schema-name-fn) scripts)))


;"Recreates default schema."
;[scripts=(init-script-files)]
(def recreate
  (partial recreate-custom db/create-spec project/db-schema-name))


;"Recreates test schema."
;[scripts=(init-script-files)]
(def recreate-test
  (partial recreate-custom db/create-test-spec project/db-test-schema-name))


;(defn recreate-empty
;  "Recreates empty datatabse schema."
;  ([]
;    (recreate (empty-schema-name) []))
;
;  ([schema-name]
;    (recreate schema-name [])))



;(defn recreate-sandbox
;  []
;  "Recreates SANDBOX datatabse schema."
;  (let [script-files (project/files "src/test/sql" ["sandbox.sql"])]
;    (recreate "sandbox" script-files)))



(defn upgrade-d
  "Upgrades imCMS schema."
  [db-spec schema-name xml-conf-file xsd-conf-file scripts-dir]
  (sql/with-connection db-spec
    (sql/transaction
      (sql/do-commands
        (format "use %s" schema-name))

      (doto (SchemaUpgrade/createInstance xml-conf-file xsd-conf-file scripts-dir)
        (.upgrade (sql/connection))))))

(defn upgrade
  ([]
    (upgrade (project/db-schema-name)))

  ([schema-name]
    (upgrade schema-name (xml-conf-file) (xsd-conf-file) (scripts-dir)))

  ([schema-name p_xml-conf-file p_xsd-conf-file p_scripts-dir]
    (upgrade-d (db/create-spec false) schema-name p_xml-conf-file p_xsd-conf-file p_scripts-dir)))


;(defn recreate-empty-upgrade
;  "Recreates empty schema and runs upgrade."
;  ([]
;    (recreate-empty-upgrade (schema-name)))
;
;  ([schema-name]
;    (recreate-empty schema-name)
;    (upgrade schema-name)))


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


