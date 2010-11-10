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


;; dev (build) properties
;(def dev-db-name #(p/build-property :db-name))
;(def dev-db-host #(p/build-property :db-host))
;(def dev-db-port #(p/build-property :db-port))
;(def dev-db-user #(p/build-property :db-user))
;(def dev-db-pass #(p/build-property :db-pass))
;(def dev-db-target #(p/build-property :db-target))
;(def dev-db-driver #(p/build-property :db-driver))

;; test properties
(def db-user #(p/test-property :User))
(def db-pass #(p/test-property :Password))
(def db-driver #(p/test-property :JdbcDriver))
(def db-url #(p/test-property :JdbcUrl))
(def db-name #(p/test-property :__DBName__))
(def db-url-without-db-name #(p/test-property :__JdbcUrlWithoutDBName__))


(defn- create-ds*
  [& {:keys [without-db-name, autocommit, env] :or {without-db-name false, autocommit true, env :test}}]
    (let [url (if without-db-name (db-url-without-db-name) (db-url))]
      (doto (db-lib/create-ds (db-driver) url (db-user) (db-pass))
        (.setDefaultAutoCommit autocommit))))

(defn create-ds [autocommit]
  (create-ds* :without-db-name false :autocommit autocommit))

(defn create-ds-without-db-name [autocommit]
  (create-ds* :without-db-name true :autocommit autocommit))


(def create-spec
  (comp db-lib/create-spec create-ds))

(def create-spec-without-db-name
  (comp db-lib/create-spec create-ds-without-db-name))
  

(defn hibernate-properties []
  {"hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect"
   "hibernate.connection.driver_class", (db-driver)
   "hibernate.connection.url", (db-url)
   "hibernate.connection.username", (db-user)
   "hibernate.connection.password", (db-pass)
   "hibernate.connection.pool_size", "1"
   "hibernate.connection.autocommit", "true"
   "hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider"
   "hibernate.hbm2ddl.auto", "create-drop"
   "hibernate.show_sql", "true"})


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
    (db-lib/recreate (create-spec-without-db-name true) name scripts)))


(defn run-scripts
  [scripts]
  (db-lib/run-scripts (create-spec true) scripts))


(defn- prepare*
  "Prepares datbase."
  ([ds name]
    (prepare* ds name true))

  ([ds name recreate-before-prepare]
    (when recreate-before-prepare
      (recreate name))

    (let [scriptsDir (p/subdir-path "src/main/web/WEB-INF/sql")
          schema (-> (Schema/load (p/file "src/main/resources/schema.xml")) (.changeScriptsDir scriptsDir))]

      (-> (DB. (create-ds false)) (.prepare schema)))))


(def
  ^{:doc "[recreate-before-prepare = true]"}
  prepare
  (partial prepare* (create-ds-without-db-name true) (db-name)))

;; prepare-dev??

