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


(defn version-d
  "Returns imCMS schema version."
  [db-spec db-name]
  (sql/with-connection db-spec
    (sql/do-commands
      (format "use %s" db-name))

    (SchemaUpgrade/getSchemaVersion (sql/connection))))

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
    (upgrade (db/db-name)))

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

  (is (= 3 (count (db/init-script-files)))))


