(ns
  #^{:doc "Sandbox."}
  sandbox  
  (:require
    [clojure.contrib.shell-out :as shell]

    [com.imcode.imcms.fs :as fs-lib]
    [com.imcode.imcms.project :as p]
    [com.imcode.imcms.project.db :as db]))

(defn recreate-utvakten-db []
  (db/recreate "utvakten" ["/Users/ajosua/utvakten.sql"]))

(defn recreate-and-upgrade-utvakten-db []
  (recreate-utvakten-db)
  (db/run-scripts "utvakten"
    (p/files "src/main/web/WEB-INF/sql" ["diff/mysql-schema-diff-4.11-6.2.sql"])))

    