(ns com.imcode.imcms.project-utils
  (:require
    ;[clojure.contrib.logging :as log]
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]
    [com.imcode.imcms
      [db-utils :as db-utils]
      [schema :as schema]
      [schema-diff :as schema-diff]])
  
  (:use
    [com.imcode.imcms
      [file-utils :only (throw-if-not-a-file throw-if-not-a-dir load-properties)]]
    [clojure.contrib
      [sql :as sql :only ()]]
    clojure.contrib.test-is
    clojure.contrib.repl-utils
    clojure.contrib.duck-streams
    [clojure.contrib.except :only (throw-if throw-if-not)])

  (:import
    (java.io File)
    (java.util Properties)
    (org.apache.commons.dbcp BasicDataSource)))


(defn root-dir
  []
  (File. "."))


(defn root-dir-path
  []
  (.getCanonicalPath (root-dir)))


(defn- to-keyword-key-map
  [a-map]
  (apply conj {} (for [[k v] a-map] [(keyword k) v])))


(defn- filesystem-node
  [relative-path check-fn]
  (let [node (File. (root-dir) relative-path)]
    (if check-fn (check-fn node) node)))


(defn subdir
  ([relative-path]
    (subdir relative-path true))

  ([relative-path check]
    (filesystem-node relative-path (if check throw-if-not-a-dir))))

(defn subdir-path
  [relative-path]
  (.getCanonicalPath (subdir relative-path)))


(defn file
  ([relative-path]
    (file relative-path true))

  ([relative-path check]
    (filesystem-node relative-path (if check throw-if-not-a-file))))


(defn file-path
  [relative-path]
  (.getCanonicalPath (file relative-path)))


(defn files
  [parent-dir-relative-path filenames]
  (let [dir (subdir parent-dir-relative-path)]
    (map #(throw-if-not-a-file (File. dir %)) filenames)))


(defn db-schema-script-files
  []
  (files "sql" ["imcms_6.1_schema.sql" "imcms_6.1_data.sql"]))


(defn load-build-properties
  []
  (to-keyword-key-map (load-properties (file "build.properties"))))


(defn create-db-datasource
  ([]
    (create-db-datasource (load-build-properties)))
  
  ([bp]
    (let [db-url (db-utils/create-url (:db-target bp) (:db-host bp) (:db-port bp))]
      (doto (BasicDataSource.)
        (.setUsername (:db-user bp))
        (.setPassword (:db-pass bp))
        (.setDriverClassName (:db-driver bp))
        (.setUrl db-url)))))


(defn db-env
  []
  (let [p (load-build-properties)]
    {:vendor-name (:db-target p)
     :schema-name (:db-name p)
     :db-spec {:datasource (create-db-datasource p)}}))


(defn db-metadata
  []
  (db-utils/get-metadata (:db-spec (db-env))))


(defn print-db-metadata
  []
  (let [meta-map (bean (db-metadata))
        meta-keys (sort (keys meta-map))]

    (doseq [k meta-keys] (println k " -> " (k meta-map)))))


(defn db-schema-version
  ([]
    (db-schema-version (:schema-name (db-env))))

  ([schema-name]
    (schema/get-version (:db-spec (db-env)) schema-name)))


(defn empty-db-schema
  ([]
    (empty-db-schema (:schema-name (db-env))))

  ([schema-name]
    (db-utils/empty-schema schema-name [])))


(defn empty-db-schema?
  ([]
    (empty-db-schema? (:db-spec (db-env))))

  ([schema-name]
    (db-utils/empty-schema? (:db-spec (db-env)) schema-name)))


(defn delete-db-schema
  ([]
    (delete-db-schema (:schema-name (db-env))))

  ([schema-name]
    (db-utils/delete-schema (:db-spec (db-env)) schema-name)))


(defn recreate-db-schema
  "Recreates PROJECT datatabse schema."
  []
  (let [env (db-env)]
    (db-utils/recreate-schema (:db-spec env) (:schema-name env) (db-schema-script-files))))


(defn recreate-sandbox-db-schema
  []
  "Recreates SANDBOX datatabse schema."
  (let [schema-script-files (files "sql" ["sandbox.sql"])]
    (db-utils/recreate-schema (:db-spec (db-env)) "sandbox" schema-script-files)))


(defn upgrade-db-schema
  "Runs sql diff scripts on PROJECT database schema."
  [execute-script-statements]
  (binding [db-utils/*execute-script-statements* execute-script-statements]
    (let [schema-version (db-schema-version)
          env (db-env)
          upgrade-conf-file (file-path "sql/schema-upgrade.xml")
          bundles (schema-diff/get-diff-bundles upgrade-conf-file (:vendor-name env))
          diffs-dir (subdir "sql")
          expanded-paths-bundles (schema/expand-diff-bundles-paths diffs-dir bundles)]

      (schema/run-diff-bundles (:db-spec env) (:schema-name env) expanded-paths-bundles))))


(defn deploy-maven-jar
  "Deploy jar file to imcms maven's repo."
  [group-id artifact-id version jar-filepath]
  (let [cmd-template "mvn deploy:deploy-file -DrepositoryId=imcode -Durl=scp://garm.imcode.com:/srv/www/apache/sites/repo.imcode.com/maven2 -DgroupId=%s -DartifactId=%s -Dversion=%s -Dfile=%s -Dpackaging=jar"
        cmd (format cmd-template group-id artifact-id version jar-filepath)
        args (su/re-split #"\s" cmd)]
    (apply shell/sh args)))


(defmacro sh [& args]
  (let [cmd# (map str args)]
    `(shell/sh ~@cmd#)))