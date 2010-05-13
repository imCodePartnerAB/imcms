(ns
  #^{:doc "Project's databse fns."}
  com.imcode.imcms.project.db

  (:use
    (clojure.contrib duck-streams test-is))

  (:require
    (clojure.contrib
      [sql :as sql])
      
    (com.imcode.imcms
      [project :as project])

    (com.imcode.cljlib
      [db :as db-lib])

    (clojure.contrib [sql :as sql]))

  (:import
    org.hibernate.SessionFactory
    org.hibernate.cfg.AnnotationConfiguration))


(defn db-name "Database name."
  []
  (:db-name (project/build-properties)))


(defn init-script-files
  []
  (project/files "src/main/web/WEB-INF/sql" ["imcms_rb4.sql" "diff/mysql-schema-diff-4.11-6.2.sql"]))


(defn create-ds
  ([]
    (create-ds true))

  ([autocomit]
    (let [p (project/build-properties)
          db-url (db-lib/create-url (:db-target p) (:db-host p) (:db-port p) (db-name))]

      (doto
        (db-lib/create-ds (:db-driver p) (:db-user p) (:db-pass p) db-url)
        (.setDefaultAutoCommit autocomit)))))




; Creates db spec
; [autocommit=true]
(def create-spec
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
     ;"hibernate.hbm2ddl.auto", "create-drop"
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
      (.addFile conf xmlFile))

    (.buildSessionFactory conf)))


(defn recreate
  "Recreates datatabse."
  [scripts]
  (db-lib/recreate (create-spec) (db-name) scripts))


(defn recreate-empty
  "Recreates empty datatabse."
  []
  (recreate []))


(defn run-scripts
  [scripts]
  (db-lib/run-scripts (create-spec) scripts))







;(defn version
;  ([]
;    (version (db-name)))
;
;  ([name]
;    (version-d (create-spec) name)))


(defn metadata []
  (db-lib/metadata (create-spec)))


(defn print-metadata []
  (db-lib/print-metadata (metadata)))