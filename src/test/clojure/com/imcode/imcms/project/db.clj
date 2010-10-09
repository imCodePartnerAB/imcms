(ns com.imcode.imcms.project.db

  (:require
    [com.imcode.imcms.project :as p]

    (clojure.contrib
      [sql :as sql])

    (com.imcode.imcms
      [db :as db-lib]
      [fs :as fs-lib])

    (clojure.contrib
      [sql :as sql]))
  
  (:import
    com.imcode.imcms.db.PrepareException
    org.hibernate.SessionFactory
    org.hibernate.cfg.AnnotationConfiguration
    (com.imcode.imcms.db DB Schema)))


;;;;
;;;; Helper fns
;;;;

;;BUG# - recreate on non-existing db throws an error.
;;     reason: datasource must not use DB name 

(def db-name #(p/build-property :db-name))
(def db-host #(p/build-property :db-host))
(def db-port #(p/build-property :db-port))
(def db-user #(p/build-property :db-user))
(def db-pass #(p/build-property :db-pass))
(def db-target #(p/build-property :db-target))
(def db-driver #(p/build-property :db-driver))


(defn create-ds
  ([]
    (create-ds (db-name)))

  ([name]
    (create-ds name true))

  ([name autocomit]
    (let [db-url (db-lib/create-url (db-target) (db-host) (db-port) name)]
      (doto (db-lib/create-ds (db-driver) (db-user) (db-pass) db-url)
        (.setDefaultAutoCommit autocomit)))))


(def
  #^{:doc "Params [autocommit=true]"}
  create-spec
  (comp db-lib/create-spec create-ds))
  

(defn hibernate-properties []
  (let [p (p/build-properties)
        db-url (db-lib/create-url (db-target) (db-host) (db-port) (db-name))]

    {"hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect"
     "hibernate.connection.driver_class", (db-driver )
     "hibernate.connection.url", db-url
     "hibernate.connection.username", (db-user)
     "hibernate.connection.password", (db-pass)
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


(defn prepare
  ([]
    (prepare (db-name)))

  ([name]
    (prepare name true))

  ([name recreate-before-prepare]
    (when recreate-before-prepare
      (recreate name))

    (let [scriptsDir (p/subdir-path "src/main/web/WEB-INF/sql")
          schema (-> (Schema/load (p/file "src/main/resources/schema.xml")) (.changeScriptsDir scriptsDir))]

      (-> (DB. (create-ds name)) (.prepare schema)))))