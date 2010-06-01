(ns
  #^{:doc "Configuration utils."}
  com.imcode.imcms.conf-utils

  (:require
    (com.imcode.cljlib
      [fs :as fs-lib]))

  (:use
    [clojure.walk :only (postwalk)]
    [clojure.contrib.map-utils :only (safe-get-in)]))  


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
  "All paths in conf are directly or recursively relative to baseidir.
   Substitute all relative paths in configuration with absolute path."
  [conf base-dir]

  (let [conf-db-scripts-dir (safe-get-in conf [:db :scripts-dir])
        real-db-scripts-dir (fs-lib/compose-path base-dir conf-db-scripts-dir)]

    (-> conf (assoc-in [:base-dir] base-dir)
             (assoc-in [:db :scripts-dir] real-db-scripts-dir)

             (substitute-db-scripts-paths real-db-scripts-dir))))



;;;;
;;;; Side effect fns.
;;;;

(defn read-conf
  "Reads and returns conf map from a conf file."
  [conf-file-path]
  (read-string (slurp conf-file-path)))


(defn create-conf [conf-file-path base-dir]
  (substitute-paths (read-conf conf-file-path) base-dir))


;;;;
;;;; Tests
;;;;