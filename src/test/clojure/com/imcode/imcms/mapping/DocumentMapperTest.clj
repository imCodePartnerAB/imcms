(ns com.imcode.imcms.mapping.DocumentMapperTest
  (:require
    [com.imcode.imcms
      [reflect-utils :as ru]
      [project :as project]
      [spring :as spring]
      [boot :as boot]
      [factory :as factory]
      [runtime :as rt]])

  (:use
    clojure.contrib.test-is)  

  (:import
    (imcode.server Imcms)
    (imcode.server.user UserDomainObject)
    (imcode.server.document.textdocument TextDomainObject MenuDomainObject)
    (com.imcode.imcms.api ContentLoop Content)))


(def *text-doc-id* 1001)
(def *lang* :en)
(def *text-no* 1000)
(def *content-loop-no* 1000)
(def *working-version-no* 0)
(def *doc-mapper*)

(def #^{:tag UserDomainObject :doc "Imcms superuser. Bind in fixture"} *superadmin*)


(defn imcms-fixture
  "Initializes and starts Imcms."
  [f]
  (boot/init-imcms)
  (Imcms/start)
  (binding [*doc-mapper* (rt/get-doc-mapper)]
    (f))
  (Imcms/stop))


(defn users-fixture
  "Binds imcms users (*superadmin*)."
  [f]
  (binding [*superadmin* (rt/login :admin :admin)]
    (f)))

(use-fixtures :once imcms-fixture users-fixture)


(comment deftest test-save-text
  (testing "insert"
    (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
          text (factory/new-text *text-doc-id* *working-version-no* *text-no* "test")]

      (.setLanguage text (.getLanguage text-doc))
      (.saveText *doc-mapper* text-doc text *superadmin*)))

  (testing "update"
    (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
          text (.getText text-doc *text-no*)]

      (.setText text "new text")
      (.saveText *doc-mapper* text-doc text *superadmin*)))

  (testing "insert with enclosing unsaved content loop"
    (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
          loop (factory/new-content-loop *text-doc-id* *working-version-no* *content-loop-no*)
          text (factory/new-text *text-doc-id* *working-version-no* *text-no* "test")]

      (-> (.getContentLoops text-doc) (.put *content-loop-no* loop))

      (doto text
        (.setLanguage (.getLanguage text-doc))
        (.setLoopNo *content-loop-no*)
        (.setContentIndex 0))

      (.saveText *doc-mapper* text-doc text *superadmin*))))


(deftest test-copy-document
  (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
        new-text-doc (.copyDocument *doc-mapper* text-doc *superadmin*)]
    (is new-text-doc)))


;(deftest test-change-menu
;  (testing "add menu"
;    (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
;          new-text-doc (.createDocumentOfTypeFromParent *doc-mapper* 2 text-doc *user*)
;          menus (.getMenus text-doc)
;          menu-no (inc (apply max 0 (keys menus)))
;          menu (.setMenu text-doc menu-no (MenuDomainObject.))
;
;      (if (seq menus)
;
;          new-text-doc (.copyDocument *doc-mapper* text-doc *superadmin*)]
;      (is new-text-doc))))




;(defn test-insert-text-with-existing-loop []
;  (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
;        text (create-text *text-doc-id* *text-no* "test")
;        user (rt/login :admin :admin)]
;
;    (.setLanguage text (.getLanguage text-doc))
;    (.setLoopNo text *content-loop-no*)                                          
;    (.saveText *doc-mapper* text-doc text user)))


;(defmacro do-test [ns-name test-name]
;  `(binding [test-ns-hook #(~test-name)]
;     (run-tests ~ns-name)))
;
;
;(defmacro def-test-ns-hook []
;  (let [syms (for [[s v] (ns-publics *ns*) :when (and (.isBound v) (fn? @v) (:test ^v))] `(~s))]
;    `(defn test-ns-hook [] ~@syms)))
;
;
;(defn test-ns-hook []
;  (println "Test ns hook")
;  (test-copy-document))
  