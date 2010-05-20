(ns
  #^{:doc "Databse routines."}
  com.imcode.imcms.db

  (:require
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
      [except :only (throw-if throwf)])))


(defn tables
  "Returns all db tables."
  ([spec]
    (sql/with-connection spec
      (tables)))

  ([]
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
      (get-version)))

  ([]
    (sql/with-query-results rs ["SELECT major, minor FROM database_version"]
      (if (empty? rs)
        (do
          (let [error-msg "Unable to get database version. Table database_version is empty."]
            (log/error error-msg)
            (throwf error-msg)))

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
  [db-conf-diffs from]
  (when-first [diff (select #(= from (:from %)) db-conf-diffs)]
    diff))


(defn required-diffs
  "Returns diffs seq or nil."
  [db-conf-diffs current-version]
  (loop [from current-version, diffs []]
    (if-let [diff (required-diff db-conf-diffs from)]
      (recur (:to diff) (conj diffs diff))
      (seq diffs))))


(defn prepare
  "Prepares database - initializes and/or updates db if necessary.
   app-home - application home.
   conf - configuration map defined in 'conf.clj' file.
   spec - db spec."
  [app-home conf spec]
  (log/info (format "Preparing the databse. app-home: %s, conf: %s." app-home, conf))
  (let [db-conf (:db conf)
        db-conf-scripts-dir (:scripts-dir db-conf)
        db-conf-init (:init db-conf)
        db-conf-diffs (:diffs db-conf)
        
        scripts-home (fs-lib/compose-path app-home db-conf-scripts-dir)]
    
    (sql/with-connection spec
      (sql/transaction
        (when (empty? (tables))
          (log/info "The database is empty and need to be initialized.")
          (let [scripts-paths (fs-lib/extend-paths scripts-home (:scripts db-conf-init))]
            (log/info (format "The following init scripts will be executed: %s" (print-str scripts-paths)))
            (doseq [script-path scripts-paths]
              (db-lib/run-script (sql/connection) script-path))

            (set-version (:version db-conf-init)))

          (log/info (format "The database is initialized. Database version is %s." (get-version))))

        (when-let [diffs (required-diffs db-conf-diffs (get-version))]
          (log/info (format "The database need to be updated. The following diffs will be applied: %s" diffs))
          (doseq [{:keys [to, scripts]} diffs]
            (do
              (doseq [script-path (fs-lib/extend-paths scripts-home scripts)]
                (db-lib/run-script (sql/connection) script-path))
              
            (set-version to))))))

    (log/info (format "The database is prepared. Database version is %s." (get-version spec)))))