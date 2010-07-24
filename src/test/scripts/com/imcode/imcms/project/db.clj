(ns com.imcode.imcms.project.db

  (:require
    [com.imcode.imcms.project :as project]

    (clojure.contrib
      [sql :as sql])
    
;    (com.imcode.imcms
;      [db :as db])

    (com.imcode.cljlib
      [db :as db-lib]
      [fs :as fs-lib])

    (clojure.contrib
      [sql :as sql]))

  
  (:use
    clojure.test
    [clojure.contrib.map-utils :only (safe-get safe-get-in)]
    (clojure.contrib duck-streams))


  (:import
    com.imcode.imcms.db.PrepareException
    org.hibernate.SessionFactory
    org.hibernate.cfg.AnnotationConfiguration))


;;;;
;;;; Helper fns
;;;;

(defn db-name "Database name."
  []
  (:db-name (project/build-properties)))


(defn create-ds
  ([]
    (create-ds (db-name) true))

  ([name]
    (create-ds db-name true))

  ([name autocomit]
    (let [p (project/build-properties)
          db-url (db-lib/create-url (safe-get p :db-target) (safe-get p :db-host) (safe-get p :db-port) name)]

      (doto
        (db-lib/create-ds (safe-get p :db-driver) (safe-get p :db-user) (safe-get p :db-pass) db-url)
        (.setDefaultAutoCommit autocomit)))))


(def
  #^{:doc "Params [autocommit=true]"}
  create-spec
  (comp db-lib/create-spec create-ds))
  

(defn hibernate-properties []
  (let [p (project/build-properties)
        db-url (db-lib/create-url (:db-target p) (:db-host p) (:db-port p) (db-name))]

    {"hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect"
     "hibernate.connection.driver_class", (:db-driver p)
     "hibernate.connection.url", db-url
     "hibernate.connection.username", (:db-user p)
     "hibernate.connection.password", (:db-pass p)
     "hibernate.connection.pool_size", "1"
     "hibernate.connection.autocommit", "true"
     "hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider"
     "hibernate.hbm2ddl.auto", "create-drop"
     "hibernate.show_sql", "true"}))


(defn create-hibernate-sf
  "Creates hibernate session factory."
  [annotatedClasses xmlFiles]
  (let [conf (AnnotationConfiguration.)]
    (doseq [[k v] (hibernate-properties)]
      (.setProperty conf k v))

    (doseq [clazz annotatedClasses]
      (.addAnnotatedClass conf clazz))

    (doseq [xmlFile xmlFiles]
      (.addFile conf (str xmlFile)))

    (.buildSessionFactory conf)))


(defn recreate
  "Recreates datatabse."
  ([]
    (recreate (db-name)))

  ([name]
    (recreate name []))

  ([name scripts]
    (db-lib/recreate (create-spec) name scripts)))


(defn run-scripts
  ([scripts]
    (run-scripts (db-name) scripts))

  ([name scripts]
    (db-lib/run-scripts (create-spec) name scripts)))


;(defn prepare
;  ([]
;    (prepare (db-name)))
;
;  ([name]
;    (prepare name true))
;
;  ([name recreate-before-prepare]
;    (when recreate-before-prepare
;      (recreate name))
;
;      (db/prepare (project/create-conf) (create-spec name false))))