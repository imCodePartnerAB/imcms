(ns
  #^{:doc "Upgrades imcms databse."}
  com.imcode.imcms.db.upgrade

  (:require
    settings
    
    (com.imcode.cljlib
      [db :as db-lib]
      [fs :as fs-lib])

    (com.imcode.imcms
      [db :as db])

    (clojure.contrib
      [sql :as sql]))

  (:use
    (clojure.contrib
      [except :only (throw-if)]
      [seq-utils :only (includes?)]
      [str-utils :only (str-join)])))


(defn reload-settings []
  (require 'settings :reload))


(defn required-diffs
  "Returns seq of diffs in the form of [version, scripts-names]."
  [diffs current-version]
  (for [[version, _ :as diff] diffs :when (> version current-version)] diff))


(defn upgrade [app-home spec]
  (reload-settings)
  (let [current-version (db/get-version spec)
        diffs (required-diffs settings/db-diffs current-version)]

    (sql/with-connection spec
      (sql/transaction
        (let [connection (sql/connection)]
          (doseq [[version scripts-names] diffs,
                   script-paths (map #(fs-lib/compose-path app-home, settings/sql-scripts-home, %) scripts-names)]
            (do
              (db-lib/run-script connection script-paths)
              (db/set-version version))))))))