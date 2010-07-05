(ns
  #^{:doc "Tests."}
  com.imcode.imcms.db-test

  (:require
    [com.imcode.imcms.project :as project]

    [com.imcode.imcms.project.db :as project-db]

    [com.imcode.imcms.db :as db]
    
    (clojure.contrib
      [sql :as sql])

    (com.imcode.cljlib
      [db :as db-lib]))
  
  (:use
    clojure.test
    [clojure.contrib.map-utils :only (safe-get safe-get-in)]
    (clojure.contrib duck-streams))

  (:import
    com.imcode.imcms.db.PrepareException
    org.hibernate.SessionFactory
    org.hibernate.cfg.AnnotationConfiguration))


;;;;
;;;; Tests
;;;; todo: refactor

(deftest test-set-and-get-version
  (let [spec (db-lib/create-h2-mem-spec)]
    (sql/with-connection spec
      (sql/do-commands "CREATE TABLE database_version (major INT NOT NULL, minor INT NOT NULL)")

      (testing "When database_version table is empty, get-version must throw PrepareException."
        (is (thrown? PrepareException (db/get-version))))

      (db/set-version 4.11)
      (is (= 4.11 (db/get-version)))

      (db/set-version 6.12)
      (is (= 6.12 (db/get-version))))))


(deftest test-empty-db?
  (project-db/recreate)
  (is (db/empty-db? (project-db/create-spec))))


(deftest test-init
  (project-db/recreate)

  (let [conf (project/create-conf)
        db-conf-init (:init (:db conf))
        spec (project-db/create-spec)]

    (sql/with-connection spec
      (sql/transaction
        (db/init db-conf-init)))))


(deftest test-upgrade
  (test-init)

  (let [conf (project/create-conf)
        db-conf-diffs (:diffs (:db conf))
        spec (project-db/create-spec)]

    (sql/with-connection spec
      (sql/transaction
        (db/upgrade db-conf-diffs)))))



(deftest test-prepare-empty
  (project-db/recreate)
  
  (db/prepare (project/create-conf) (project-db/create-spec)))




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


(def
  #^{:doc "db-diffs - diffs set, see ':db/:db-diffs' definition in conf.clj file."
     :private true}

  db-conf-diffs #{
      {
          :from 4.11
          :to 4.12
          :scripts ["a.sql" "b.sql"]
      }

      {
          :from 4.12
          :to 4.13
          :scripts ["c.sql" "d.sql"]
      }

      {
          :from 4.13
          :to 6.2
          :scripts ["e.sql" "f.sql"]
      }
  })


(deftest test-version-rec-to-double
  (is (= 4 (@#'db/version-rec-to-double {:major 4, :minor 0})))
  (is (= 4.1 (@#'db/version-rec-to-double {:major 4, :minor 1}))))


(deftest test-double-to-version-rec
  (is (= {:major "4", :minor "0"} (@#'db/double-to-version-rec 4)))
  (is (= {:major "4", :minor "0"} (@#'db/double-to-version-rec 4.0)))
  (is (= {:major "4", :minor "1"} (@#'db/double-to-version-rec 4.1))))


(deftest test-required-diff
  (is (nil? (db/required-diff db-conf-diffs 4.10)))

  (is (= (db/required-diff db-conf-diffs 4.11)
         {
            :from 4.11
            :to 4.12
            :scripts ["a.sql" "b.sql"]
         }))

  (is (= (db/required-diff db-conf-diffs 4.13)
         {
             :from 4.13
             :to 6.2
             :scripts ["e.sql" "f.sql"]
         })))


(deftest test-required-diffs
  (is (nil? (db/required-diffs db-conf-diffs 4.10)))

  (is (= (set (db/required-diffs db-conf-diffs 4.11))
         db-conf-diffs))

  (is (= (set (db/required-diffs db-conf-diffs 4.12))
         #{
              {
                  :from 4.12
                  :to 4.13
                  :scripts ["c.sql" "d.sql"]
              }

              {
                  :from 4.13
                  :to 6.2
                  :scripts ["e.sql" "f.sql"]
              }
         }))


  (is (= (set (db/required-diffs db-conf-diffs 4.13))
         #{
              {
                  :from 4.13
                  :to 6.2
                  :scripts ["e.sql" "f.sql"]
              }
         })))