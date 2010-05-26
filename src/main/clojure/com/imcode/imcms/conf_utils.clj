(ns
  #^{:doc "Configuration utils."}
  com.imcode.imcms.conf-utils

  (:require
    (com.imcode.cljlib
      [fs :as fs-lib]))

  (:use
    [clojure.walk :only (postwalk)]))  


;;;;
;;;; Pure fns.
;;;;

(defn- substitute-db-scripts-paths [conf db-scripts-dir]
  (postwalk
    (fn [node]
      (if (and (vector? node)
               (= :scripts (first node)))

        (let [scripts (second node)]
          [:scripts (apply vector (fs-lib/extend-paths db-scripts-dir scripts))])

        node))

    conf))


(defn- substitute-paths
  "All paths in conf are directly or recursively relative to basedir.
   Substitute all relative paths in configuration with absolute path."
  [conf basedir]

  (let [conf-db-scripts-dir (get-in conf [:db :scripts-dir])
        real-db-scripts-dir (fs-lib/compose-path basedir conf-db-scripts-dir)]

    (-> conf (assoc-in [:basedir] basedir)
             (assoc-in [:db :scripts-dir] real-db-scripts-dir)

             (substitute-db-scripts-paths real-db-scripts-dir))))



;;;;
;;;; Side effect fns.
;;;;

(defn read-conf
  "Reads and returns conf map from conf file.
   Throws an exception if conf file can not be found."
  [conf-file-path]
  (read-string (slurp conf-file-path)))


(defn create-conf [conf-file-path basedir]
  (substitute-paths (read-conf conf-file-path) basedir))


;;;;
;;;; Tests
;;;;