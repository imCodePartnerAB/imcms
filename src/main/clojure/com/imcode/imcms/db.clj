(ns
  #^{:doc "Databse routines."}
  com.imcode.imcms.db

  (:require
    (com.imcode.cljlib
      [db :as db-lib])

    (clojure.contrib
      [sql :as sql]))

  (:use
    (clojure.contrib
      [except :only (throw-if)]
      [seq-utils :only (includes?)]
      [str-utils :only (str-join)])))


(defn tables
  "Returns all db tables."
  [spec]
  (sql/with-connection spec
    (sql/with-query-results rs ["SHOW TABLES"]
      (mapcat vals (doall rs)))))


(defn new? [spec]
  (empty? (tables spec)))


(defn get-version-rec
  "Retrns current db version as record {:major x, :minor y} or {:major 0, :minor 0} if database is new."
  [spec]
  (if (new? spec)
    {:major 0, :minor 0}
    (sql/with-connection spec
      (sql/with-query-results rs ["SELECT major, minor FROM database_version"]
        (throw-if (empty? rs) "Can not retrieve database version. Table database_version is empty.")
        (first rs)))))


(defn version-rec-to-double [{:keys [major minor]}]
  (Double/valueOf (format "%d.%d" major minor)))


(defn double-to-version-rec [version]
  (let [[major minor] (re-seq #"\d" (str version))]
    {:major major,
     :minor (if-not minor 0 minor)
    }))


(defn get-version [spec]
  (version-rec-to-double (get-version-rec spec)))


(defn set-version
  ([spec version]
    (sql/with-connection spec
      (set-version version)))

  ([version]
    (sql/update-or-insert-values
      "database_version" ["major IS NOT NULL"]
      (double-to-version-rec version))))



