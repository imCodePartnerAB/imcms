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
      [except :only (throw-if throw-if-not throwf)]))

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
  "Returns a diff required to update db or nil if no corresponding diff exists.
   diffs-set - a set of diffs; see conf.clj and tests to learn more about diffs set definition.
   current-version - current db version as double."
  [diffs-set current-version]
  (when-first [diff (select #(= current-version (:from %)) diffs-set)]
    diff))


(defn required-diffs
  "Returns a seq of diffs beginning from a current version or nil.
   Chaines diffs by matching a previous diff ':to' field with a sucveeding diff ':from' field. 
   diffs-set - a set of diffs; see conf.clj and tests to learn more about diffs set definition.
   current-version - current db version as double."  
  [diffs-set current-version]
  (loop [version current-version, diffs []]
    (if-let [diff (required-diff diffs-set version)]
      (let [new-current-version (:to diff)
            collected-diffs (conj diffs diff)]
        (recur new-current-version collected-diffs))
      (seq diffs))))


;;;;
;;;; Fns with side-effect
;;;;

(defn empty-db?
  "WARNING! Works properly ONLY with MySQL. 
   Returns true if current databse is empty or false otherwise."
  ([spec]
    (sql/with-connection spec
      (empty-db?)))

  ([]
    (sql/with-query-results rs ["SHOW TABLES"]
      (empty? rs))))


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
   db-conf-init - db init record. See :db/:init definition in conf.clj.
   scripts-home - init scripts parent directory path."
  [db-conf-init scripts-home]
  (let [scripts-paths (fs-lib/extend-paths scripts-home (:scripts db-conf-init))
        version (:version db-conf-init)]
    
    (log/info (format "The following init scripts will be executed: %s." (print-str scripts-paths)))
    (doseq [script-path scripts-paths]
      (db-lib/run-script (sql/connection) script-path))

    (log/info (format "Updating db version to: %s." version))
    (set-version version)))


(defn upgrade
  "Upgrades databse.
   diffs - seq of diff records.
   scripts-home - diff scripts parent directory path."
  [diffs scripts-home]
  (doseq [{:keys [to, scripts]} diffs]
    (do
      ;; log xxx
      (doseq [script-path (fs-lib/extend-paths scripts-home scripts)]
        (db-lib/run-script (sql/connection) script-path)))

    (set-version to)))  
  

(defn prepare
  "Prepares database - initializes and/or upgrades db if necessary.
   app-home - application home.
   conf - configuration map defined in 'conf.clj' file.
   spec - db spec."
  [app-home conf spec]
  (log/info (format "Preparing the databse. app-home: %s, conf: %s." app-home, conf))
  (let [db-conf (:db conf)
        db-conf-version (:version db-conf)
        db-conf-scripts-dir (:scripts-dir db-conf)
        db-conf-init (:init db-conf)
        db-conf-diffs (:diffs db-conf)
        
        scripts-home (fs-lib/compose-path app-home db-conf-scripts-dir)]

    (sql/with-connection spec
      (sql/transaction
        (when (empty-db?)
          (log/info (format "The database is empty and need to be initialized. The following init will be applied: %s."
                            db-conf-init))
          
          (init db-conf-init scripts-home) 

          (log/info (format "The database is initialized. Database version is %s." (get-version))))

        (let [current-version (get-version)]
          (when-let [diffs (required-diffs db-conf-diffs current-version)]
            (let [last-diff-version (:to (last diffs))]
              ;; check against required version
              (log/info (format "The database need to be upgraded from %s to %s. The following diffs will be applied: %s."
                                current-version, last-diff-version, diffs))

              (upgrade diffs scripts-home))))))

    (let [current-version (get-version spec)]
      (when-not (= current-version db-conf-version)
        (let [msg (format "Database version %s is not eqaul to required version %s."
                           current-version, db-conf-version)]

          (log/error msg)
          (throwf PrepareException msg)))

      (log/info (format "The database is prepared. Database version is %s." (get-version spec))))))


;;;;
;;;; Tests
;;;;

(def
  #^{:doc "db-diffs - diffs set, see ':db/:db-diffs' definition in conf.clj file."
     :private true}

  db-conf-diffs #{
      {
          :from 4.11
          :to 4.12
          :scripts ["a.sql" "b.sql"]
      }

      {
          :from 4.12
          :to 4.13
          :scripts ["c.sql" "d.sql"]
      }

      {
          :from 4.13
          :to 6.2
          :scripts ["e.sql" "f.sql"]
      }
  })


(deftest test-version-rec-to-double
  (is (= 4 (version-rec-to-double {:major 4, :minor 0})))
  (is (= 4.1 (version-rec-to-double {:major 4, :minor 1}))))


(deftest test-double-to-version-rec
  (is (= {:major "4", :minor "0"} (double-to-version-rec 4)))
  (is (= {:major "4", :minor "0"} (double-to-version-rec 4.0)))
  (is (= {:major "4", :minor "1"} (double-to-version-rec 4.1))))


(deftest test-required-diff
  (is (nil? (required-diff db-conf-diffs 4.10)))

  (is (= (required-diff db-conf-diffs 4.11)
         {
            :from 4.11
            :to 4.12
            :scripts ["a.sql" "b.sql"]
         }))

  (is (= (required-diff db-conf-diffs 4.13)
         {
             :from 4.13
             :to 6.2
             :scripts ["e.sql" "f.sql"]
         })))


(deftest test-required-diffs
  (is (nil? (required-diffs db-conf-diffs 4.10)))

  (is (= (set (required-diffs db-conf-diffs 4.11))
         db-conf-diffs))

  (is (= (set (required-diffs db-conf-diffs 4.12))
         #{
              {
                  :from 4.12
                  :to 4.13
                  :scripts ["c.sql" "d.sql"]
              }

              {
                  :from 4.13
                  :to 6.2
                  :scripts ["e.sql" "f.sql"]
              }
         }))


  (is (= (set (required-diffs db-conf-diffs 4.13))
         #{
              {
                  :from 4.13
                  :to 6.2
                  :scripts ["e.sql" "f.sql"]
              }
         })))
