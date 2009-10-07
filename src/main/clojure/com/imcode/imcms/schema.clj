(ns com.imcode.imcms.schema
  (:require
    [com.imcode.imcms
      [db-utils :as db-utils]]
    
    [clojure.contrib [sql :as sql]])

  (:use
    [clojure.contrib.except :only (throw-if)]
    clojure.contrib.test-is)

  (:import
        (java.io File)))


(defn get-version
  "Returns schema version as a vector pair in format [major minor].
   Ex: [6 1]"
  [db-spec db-name]
  (sql/with-connection db-spec
    (sql/do-commands
      (format "use %s" db-name))

    (sql/with-query-results rs ["SELECT major, minor FROM database_version"]
      (let [version (first rs)]
        (throw-if (empty? version) "No schema version present in table database_version.")
        [(:major version), (:minor version)]))))


(defn run-diff-bundle
  "Runs diff bundle."
  ([db-spec db-name bundle]
    (sql/with-connection db-spec
      (sql/transaction
        (sql/do-commands
          (format "use %s" db-name))

        (run-diff-bundle (sql/connection) bundle))))

  ([connection bundle]
    (let [[version scripts] bundle
          [major minor] (.split version "\\.")]

      (doseq [script scripts]
        (db-utils/run-script connection script)))))


(defn run-diff-bundles
  "Runs diff bundles."
  ([db-spec db-name bundles]
    (sql/with-connection db-spec
      (sql/transaction
        (sql/do-commands
          (format "use %s" db-name))
        
        (run-diff-bundles (sql/connection) bundles))))

   ([connection bundles]
     (doseq [bundle bundles]
       (run-diff-bundle connection bundle))))


(defn expand-diff-bundles-paths [diffs-dir bundles]
  (for [[version scripts] bundles]
    [version (map #(.getCanonicalPath (File. diffs-dir %)) scripts)]))

; The same - for testing purposes
(defn expand-diff-bundles-paths [diffs-dir bundles]
  (for [[version scripts] bundles]
    [version (map (fn [_] (.getCanonicalPath (File. diffs-dir "sandbox.sql"))) scripts)]))
