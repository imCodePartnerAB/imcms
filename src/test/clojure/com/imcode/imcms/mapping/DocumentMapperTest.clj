(ns com.imcode.imcms.mapping.DocumentMapperTest
  (:require
    [com.imcode.imcms
      [reflect-utils :as ru]
      [project :as project]
      [spring :as spring]
      [factory :as factory]
      [runtime :as rt]])

  (:use
    clojure.contrib.test-is)  

  (:import
    (imcode.server Imcms)
    (imcode.server.document.textdocument TextDomainObject)
    (com.imcode.imcms.api ContentLoop Content)))

(defn init-imcms-fixture
  "Initializes and starts Imcms."
  [f]
  (Imcms/setPath (project/subdir "src/test/resources"))
  (Imcms/setPrefsConfigPath ".")
  (Imcms/setApplicationContext spring/spring-app-context)
  (Imcms/setUpgradeDatabaseSchemaOnStart false)
  (Imcms/start)

  (f)

  (Imcms/stop))

(def *text-doc-id* 1001)
(def *lang* :en)
(def *text-no* 1000)
(def *content-loop-no* 1000)
(def *working-version-no* 0)
(def *superadmin*)


(defn bind-users-fixture
  "Binds imcms users (*superadmin*)."
  [f]
  (binding [*superadmin* (rt/login :admin :admin)]
    (f)))

(use-fixtures :once init-imcms-fixture bind-users-fixture)


(deftest test-save-text
  (testing "with new text"
    (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
          text (factory/new-text *text-doc-id* *working-version-no* *text-no* "test")]

      (.setLanguage text (.getLanguage text-doc))
      (.saveText (rt/get-doc-mapper) text-doc text *superadmin*)))

  (testing "with existings text"
    (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
          text (.getText text-doc *text-no*)]

      (.setText text "new text")
      (.saveText (rt/get-doc-mapper) text-doc text *superadmin*))))


;(defn test-insert-text-with-create-loop
;  "Tests DocumentMapper.saveText."
;  []
;  (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
;        loop (create-loop *text-doc-id* *working-version-no* *content-loop-no*)
;        text (create-text *text-doc-id* *working-version-no* *text-no* "test")
;        user (rt/login :admin :admin)]
;
;    (-> (.getContentLoops text-doc) (.put *content-loop-no* loop))
;
;    (doto text
;      (.setLanguage (.getLanguage text-doc))
;      (.setLoopNo *content-loop-no*)
;      (.setContentIndex 0))
;
;    (.saveText (rt/get-doc-mapper) text-doc text user)))



;(defn test-insert-text-with-existing-loop []
;  (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
;        text (create-text *text-doc-id* *text-no* "test")
;        user (rt/login :admin :admin)]
;
;    (.setLanguage text (.getLanguage text-doc))
;    (.setLoopNo text *content-loop-no*)
;    (.saveText (rt/get-doc-mapper) text-doc text user)))