(ns
  #^{:doc "Databse routines."}
  com.imcode.imcms.db

  (:require
    settings
    
    (com.imcode.cljlib
      [fs :as fs-lib]
      [db :as db-lib])

    (clojure.contrib
      [sql :as sql]
      [str-utils :as su]
      [logging :as log]))

  (:use
    [clojure.set :only (select)]
    
    (clojure.contrib
      [except :only (throw-if)])))


(defn tables
  "Returns all db tables."
  [spec]
  (sql/with-connection spec
    (sql/with-query-results rs ["SHOW TABLES"]
      (mapcat vals (doall rs)))))


(defn double-to-version-rec [version]
  (let [[major minor] (su/re-split #"\." (str version))]
    {:major major,
     :minor (if-not minor 0 minor)
    }))


(defn version-rec-to-double [{:keys [major minor]}]
  (Double/valueOf (format "%s.%s" major (if minor minor 0))))


(defn get-version
  "Retrns db version."
  ([spec]
    (sql/with-connection spec
      (sql/transaction
        (get-version))))

  ([]
    (sql/with-query-results rs ["SELECT major, minor FROM database_version"]
      (if (empty? rs)
        (do
          (let [error-msg "Unable to get database version. Table database_version is empty."]
            (log/error error-msg)
            (throw error-msg)))

        (version-rec-to-double (first rs))))))


(defn set-version
  ([spec version]
    (sql/with-connection spec
      (set-version version)))

  ([version]
    (log/info (format "Setting database version to %s." version))
    (sql/update-or-insert-values
      "database_version" ["major IS NOT NULL"]
      (double-to-version-rec version))))


(defn required-diff
  [diffs from]
  (when-first [diff (select #(= from (:from %)) diffs)]
    diff))


(defn required-diffs
  "Returns diffs seq or nil."
  [diffs current-version]
  (loop [from current-version, diffs2 []]
    (if-let [diff (required-diff diffs from)]
      (recur (:to diff) (conj diffs2 diff))
      (seq diffs2))))


(defn prepare
  "Prepares database - initializes and/or updates db if necessary."
  [app-home spec]
  (log/info (format "Preparing the databse. app-home: %s" app-home))
  (let [scripts-home (fs-lib/compose-path app-home settings/db-scripts-dir)]
    (sql/with-connection spec
      (sql/transaction
        (when (empty? (tables spec))
          (log/info "The database is empty and need to be initialized.")
          (let [scripts-paths (fs-lib/extend-paths scripts-home (:scripts settings/db-init))]
            (log/info (format "The following init scripts will be executed: %s" (print-str scripts-paths)))
            (doseq [script-path scripts-paths]
              (db-lib/run-script (sql/connection) script-path))

            (set-version (:version settings/db-init)))

          (log/info (format "The database is initialized. Database version is %s." (get-version))))

        (when-let [diffs (required-diffs
                           settings/db-diffs
                           (get-version))]

          (log/info (format "The database need to be updated. The following diffs will be applied: %s" diffs))
          (doseq [{:keys [to, scripts]} diffs]
            (do
              (doseq [script-path (fs-lib/extend-paths scripts-home scripts)]
                (db-lib/run-script (sql/connection) script-path))
              
            (set-version to))))))

    (log/info (format "The database is prepared. Database version is %s." (get-version spec)))))