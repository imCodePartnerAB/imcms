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
    (clojure
      test
      [set :only (select)])
    
    (clojure.contrib
      [except :only (throw-if throw-if-not throwf)]
      [map-utils :only (safe-get safe-get-in)]))

  (:import
    com.imcode.imcms.db.PrepareException))

;;;;
;;;; Pure fns
;;;;

(defn- version-rec-to-double
  "Creates a double from database_version table row."
  [{:keys [major minor]}]
  (Double/valueOf (format "%s.%s" major minor)))


(defn- double-to-version-rec
  "Creates a record intended to insert/update row in database_version table."
  [version]
  (let [[major minor] (su/re-split #"\." (str version))]
    {
      :major major,
      :minor (if minor minor "0")
    }))


(defn required-diff
  "Returns a diff required to update db from current to next version or nil if no corresponding diff exists.
   diffs-set - a set of diffs; see conf.clj and tests to learn more about diffs set definition.
   current-version - current db version as double."
  [diffs-set current-version]
  (when-first [diff (select #(= current-version (safe-get % :from)) diffs-set)]
    diff))


(defn required-diffs
  "Returns a seq of diffs beginning from a current version or nil.
   Chaines diffs by matching a previous diff ':to' field with a sucveeding diff ':from' field. 
   diffs-set - a set of diffs; see conf.clj and tests to learn more about diffs set definition.
   current-version - current db version as double."  
  [diffs-set current-version]
  (loop [version current-version, diffs []]
    (if-let [diff (required-diff diffs-set version)]
      (let [new-current-version (safe-get diff :to)
            collected-diffs (conj diffs diff)]
        (recur new-current-version collected-diffs))
      (seq diffs))))


;;;;
;;;; Fns with side-effect
;;;;
(defn tables
  "WARNING! Works properly ONLY with MySQL.
   Returns database tables."
  ([spec]
    (sql/with-connection spec
      (tables)))

  ([]
    (sql/with-query-results rs ["SHOW TABLES"]
      (mapcat #(vals %) (doall rs)))))


(def
  #^{:doc "WARNING! Works properly ONLY with MySQL.
           Returns true if current databse is empty or false otherwise.
           See tables fn for params description."}
  empty-db? (comp empty? tables))


(defn get-version
  ([spec]
    (sql/with-connection spec
      (get-version)))

  ([]
    (sql/with-query-results rs ["SELECT major, minor FROM database_version"]
      (if (empty? rs)
        (let [error-msg "Unable to get database version. Table database_version is empty."]
          (log/error error-msg)
          (throwf PrepareException error-msg))

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


(defn init
  "Initializes empty database.
   db-conf-init - db init record. See :db/:init definition in conf.clj."
  [db-conf-init]
  (let [scripts (safe-get db-conf-init :scripts)
        version (safe-get db-conf-init :version)]
    
    (log/info (format "The following init scripts will be executed: %s." (print-str scripts)))
    (doseq [script scripts]
      (db-lib/run-script (sql/connection) script))

    (log/info (format "Updating db version to: %s." version))
    (set-version version)))


(defn upgrade
  "Upgrades databse.
   diffs - seq of diff records."
  [diffs]
  (doseq [{:keys [to, scripts]} diffs]
    (do
      ;; log xxx
      (doseq [script scripts]
        (db-lib/run-script (sql/connection) script))

      (set-version to))))  
  

(defn prepare
  "Prepares database - initializes and/or upgrades db if necessary.
   conf - configuration map defined in 'conf.clj' file.
   spec - db spec."
  [conf spec]
  (let [db-conf (safe-get conf :db)
        db-conf-version (safe-get db-conf :version)
        db-conf-init (safe-get db-conf :init)
        db-conf-diffs (safe-get db-conf :diffs)]

    (sql/with-connection spec
      (sql/transaction
        (when (empty-db?)
          (log/info (format "The database is empty and need to be initialized. The following init will be applied: %s."
                            db-conf-init))
          
          (init db-conf-init) 

          (log/info (format "The database is initialized. Database version is %s." (get-version))))

        (let [current-version (get-version)]
          (when-let [diffs (required-diffs db-conf-diffs current-version)]
            (let [last-diff-version (:to (last diffs))]
              ;; check against required version
              (log/info (format "The database need to be upgraded from %s to %s. The following diffs will be applied: %s."
                                current-version, last-diff-version, diffs))

              (upgrade diffs))))))

    (let [current-version (get-version spec)]
      (when-not (= current-version db-conf-version)
        (let [msg (format "Database version %s is not eqaul to required version %s."
                           current-version, db-conf-version)]

          (log/error msg)
          (throwf PrepareException msg)))

      (log/info (format "The database is prepared. Database version is %s." (get-version spec))))))