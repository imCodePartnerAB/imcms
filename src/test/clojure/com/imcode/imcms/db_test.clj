(ns
  #^{:doc "Database tests."}
  com.imcode.imcms.db-test

  (:require
    [com.imcode.imcms.conf-utils :as conf]

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

    [clojure.contrib.map-utils :only (safe-get safe-get-in)]
    clojure.test
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
      (.addFile conf xmlFile))

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


(defn create-conf []
  (let [basedir (project/subdir-path "src/main/web")]
    (conf/create-conf basedir (project/file-path "src/main/resources/conf.clj"))))


(defn prepare
  ([]
    (prepare (db-name)))

  ([name]
    (prepare name true))

  ([name recreate-before-prepare]
    (when recreate-before-prepare
      (recreate name))

      (db/prepare (create-conf) (create-spec name false))))


;(defn tables-ddls
;  "Returns a map of table-name -> table ddl."
;  []
;  (let [create-table-key (keyword "create table")]
;    (into {}
;      (sql/with-connection (create-spec)
;        (doall
;          (for [table (db/tables)]
;            (sql/with-query-results rs [(str "SHOW CREATE TABLE " table)]
;              [table (get (first rs) create-table-key)])))))))
;
;
;
;(defn create-tables
;  "Creates tables in a db from ddls in an order they apper in the tables-names coll. "
;  [name ddls tables-names]
;  (sql/with-connection (create-spec)
;    (sql/do-commands
;      (format "USE %s" name))
;
;    (doseq [table-name tables-names]
;      (sql/do-commands
;        (safe-get ddls table-name)))))



;;;;
;;;; Tests
;;;; todo: refactor

(deftest test-set-and-get-version
  (let [spec (db-lib/create-h2-mem-spec)]
    (sql/with-connection spec
      (sql/do-commands "CREATE TABLE database_version(major INT NOT NULL, minor INT NOT NULL)")

      (testing "When database_version table is empty, get-version must throw a PrepareException."
        (is (thrown? PrepareException (db/get-version))))

      (db/set-version 4.11)
      (is (= 4.11 (db/get-version)))

      (db/set-version 6.12)
      (is (= 6.12 (db/get-version))))))


(deftest test-empty-db?
  (recreate)
  (is (db/empty-db? (create-spec))))


(deftest test-init
  (recreate)

  (let [conf (create-conf)
        db-conf-init (:init (:db conf))
        spec (create-spec)]

    (sql/with-connection spec
      (sql/transaction
        (db/init db-conf-init)))))


(deftest test-upgrade
  (test-init)

  (let [conf (create-conf)
        db-conf-diffs (:diffs (:db conf))
        spec (create-spec)]

    (sql/with-connection spec
      (sql/transaction
        (db/upgrade db-conf-diffs)))))



(deftest test-prepare-empty
  (recreate)
  
  (db/prepare (create-conf) (create-spec)))




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