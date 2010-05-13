(ns
  #^{:doc "Project's databse fns."}
  com.imcode.imcms.project.db

  (:use
    (clojure.contrib duck-streams test-is))

  (:require
    (com.imcode.imcms
      [project :as project])

    (com.imcode.cljlib
      [file-utils :as file-utils]
      [db :as db-lib])

    (clojure.contrib [sql :as sql])))


(defn- create-custom-ds
  ([schema-name-fn]
    (create-custom-ds schema-name-fn true))

  ([schema-name-fn autocomit]
    (let [p (project/build-properties)
          db-url (db-lib/create-url (:db-target p) (:db-host p) (:db-port p) (schema-name-fn))]

      (doto
        (db-lib/create-ds (:db-driver p) (:db-user p) (:db-pass p) db-url)
        (.setDefaultAutoCommit autocomit)))))


; [autocommit=true]
(def create-ds
  (partial create-custom-ds project/db-schema-name))


; [autocommit=true]
(def create-test-ds
  (partial create-custom-ds project/db-test-schema-name))


; [autocommit=true]
(def create-spec
  (comp db-lib/create-spec create-ds))


; [autocommit=true]
(def create-test-spec
  (comp db-lib/create-spec create-ds))


(defn run-scripts-on-test
  [scripts]
  (db-lib/run-scripts (create-test-spec) scripts))


(defn metadata []
  (db-lib/metadata (create-spec)))


(defn print-metadata []
  (db-lib/print-metadata (metadata)))