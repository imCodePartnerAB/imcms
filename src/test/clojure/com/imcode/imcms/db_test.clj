(ns
  #^{:doc "Database tests."}
  com.imcode.imcms.db-test

  (:require
    (clojure.contrib
      [sql :as sql])
      
    (com.imcode.imcms
      [db :as db]
      [project :as project])

    (com.imcode.cljlib
      [db :as db-lib]
      [fs :as fs-lib])

    (clojure.contrib
      [sql :as sql]))

  
  (:use
    com.imcode.imcms.conf-utils
    
    clojure.test
    (clojure.contrib duck-streams))


  (:import
    com.imcode.imcms.db.PrepareException
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
  ([]
    (recreate (db-name)))

  ([name]
    (recreate name (init-script-files)))

  ([name scripts]
    (db-lib/recreate (create-spec) name scripts)))


(defn recreate-empty
  "Recreates empty datatabse."
  ([]
    (recreate-empty (db-name)))

  ([name]
    (db-lib/recreate (create-spec) name [])))


(defn run-scripts
  ([scripts]
    (run-scripts (db-name) scripts))

  ([name scripts]
    (db-lib/run-scripts (create-spec) name scripts)))


;;;;
;;;; Tests
;;;; todo: refactor

(deftest test-set-and-get-version
  (let [spec (db-lib/create-h2-mem-spec)]
    (sql/with-connection spec
      (sql/do-commands "CREATE TABLE database_version(major INT NOT NULL, minor INT NOT NULL)")

      (is (thrown? PrepareException (db/get-version)))

      (db/set-version 4.11)
      (is (= 4.11 (db/get-version)))

      (db/set-version 6.12)
      (is (= 6.12 (db/get-version))))))


(deftest test-empty-db?
  (recreate-empty)
  (is (db/empty-db? (create-spec))))


(deftest test-init
  (recreate-empty)

  (let [basedir (project/subdir-path "src/main/web")
        conf (create-conf (project/file-path "src/main/resources/conf.clj") basedir)
        db-conf-init (:init (:db conf))  
        spec (create-spec false)]

    (sql/with-connection spec
      (sql/transaction
        (db/init db-conf-init)))))


(deftest test-upgrade
  (test-init)

  (let [basedir (project/subdir-path "src/main/web")
        conf (create-conf (project/file-path "src/main/resources/conf.clj") basedir)
        db-conf-diffs (:diffs (:db conf))
        spec (create-spec false)]

    (sql/with-connection spec
      (sql/transaction
        (db/upgrade db-conf-diffs)))))



(deftest test-prepare-empty
  (recreate-empty)
  
  (let [basedir (project/subdir-path "src/main/web")
        conf (create-conf (project/file-path "src/main/resources/conf.clj") basedir)
        spec (create-spec false)]

    (db/prepare conf spec)))




;(deftest test-prepare
;  (let [spec (db-lib/create-h2-mem-spec "test;DB_CLOSE_DELAY=-1")
;        app-home "/Users/ajosua/projects/imcode/imcms/trunk/src/main/web"
;        conf (read-string (slurp "/Users/ajosua/projects/imcode/imcms/trunk/src/main/resources/conf.clj"))]
;
;    (binding [db-lib/*execute-script-statements* false
;              empty-db? (fn []
;                          ;; 'prepare' calls empty-db? only once to determine is db need to be updated.
;                          ;; Real init scrip(s) creates database_version table and populates it with initial data.
;                          (sql/do-commands
;                            "DROP TABLE IF EXISTS database_version"
;                            "CREATE TABLE database_version (major INT NOT NULL, minor INT NOT NULL)")
;                          true)]
;
;      (try
;        (prepare app-home conf spec)
;        ;; Ensure database is disposed
;        (finally
;          (sql/with-connection spec
;            (sql/do-commands "SET DB_CLOSE_DELAY 0")))))))