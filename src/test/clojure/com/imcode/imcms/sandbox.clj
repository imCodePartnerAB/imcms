(ns com.imcode.imcms.sandbox
  #^{:doc "Sandbox."}
  (:require
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]
    [com.imcode.imcms
      [reflect-utils :as ru]
      [project :as project]
      [spring :as spring]
      [runtime :as rt]])

  (:import
    (imcode.server Imcms)
    (imcode.server.document.textdocument TextDomainObject)
    (com.imcode.imcms.api ContentLoop Content)))

(defn init-imcms []
  (Imcms/setPath (project/subdir "src/test/resources"))
  (Imcms/setPrefsConfigPath ".")
  (Imcms/setApplicationContext spring/spring-app-context)
  (Imcms/setUpgradeDatabaseSchemaOnStart false)
  (Imcms/start))

(def *text-doc-id* 1001)
(def *lang* :en)
(def *text-no* 1000)
(def *content-loop-no* 1000)
(def *working-version-no* 0)


(defn create-text [doc-id no text]
  (doto (TextDomainObject.)
    (.setDocId doc-id)
    (.setNo no)
    (.setText text)
    (.setDocVersionNo *working-version-no*)))


(defn create-loop [doc-id no]
  (let [loop (doto (ContentLoop.)
               (.setDocId doc-id)
               (.setNo no)
               (.setDocVersionNo *working-version-no*))

        content (doto (Content.)
                  (.setIndex 0)
                  (.setOrderIndex 0))]

    (-> (.getContents loop) (.add content))

    loop))



(defn test-insert-text []
  (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
        text (create-text *text-doc-id* *text-no* "test")
        user (rt/login :admin :admin)]

    (.setLanguage text (.getLanguage text-doc))
    (.saveText (rt/get-doc-mapper) text-doc text user)))


(defn test-update-text []
  (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
        text (.getText text-doc *text-no*)
        user (rt/login :admin :admin)]

    (.setText text-doc "new text")
    (.saveText (rt/get-doc-mapper) text-doc text user)))


(defn test-insert-text-with-create-loop []
  (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
        loop (create-loop *text-doc-id* *content-loop-no*)
        text (create-text *text-doc-id* *text-no* "test")
        user (rt/login :admin :admin)]

    (-> (.getContentLoops text-doc) (.put *content-loop-no* loop))

    (doto text
      (.setLanguage (.getLanguage text-doc))
      (.setLoopNo *content-loop-no*)
      (.setContentIndex 0))
      
    (.saveText (rt/get-doc-mapper) text-doc text user)))



;(defn test-insert-text-with-existing-loop []
;  (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
;        text (create-text *text-doc-id* *text-no* "test")
;        user (rt/login :admin :admin)]
;
;    (.setLanguage text (.getLanguage text-doc))
;    (.setLoopNo text *content-loop-no*)
;    (.saveText (rt/get-doc-mapper) text-doc text user)))

(defn e [] (.printStackTrace *e))

