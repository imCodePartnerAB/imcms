(ns
  #^{:doc "Sandbox."}
  com.imcode.imcms.project.sandbox  
  (:require
    [clojure.contrib.duck-streams :as ds]
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]

    [com.imcode.cljlib.fs :as fs-lib]
    [com.imcode.imcms.db-test :as db]
    [com.imcode.imcms.project :as p]
    [com.imcode.imcms.project.lucene :as l]))


(defn recreate-utvakten-db []
  (db/recreate "utvakten" ["/Users/ajosua/utvakten.sql"]))

(defn recreate-and-upgrade-utvakten-db []
  (recreate-utvakten-db)
  (db/run-scripts "utvakten"
    (p/files "src/main/web/WEB-INF/sql" ["diff/mysql-schema-diff-4.11-6.2.sql"])))


(def m (sorted-map-by 
         (proxy [java.util.Comparator] []
           (compare [s1 s2]
             (- (Integer/valueOf s1) (Integer/valueOf s2))))))


(def m (sorted-map-by
  (comparator #(- (Integer/valueOf %1)
                  (Integer/valueOf %2)))))