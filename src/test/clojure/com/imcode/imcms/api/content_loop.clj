(ns com.imcode.imcms.api.content_loop
  (:require
    [clojure.contrib.sql :as sql]
    [com.imcode.imcms.project :as project]
    [com.imcode.imcms.schema :as schema])

  (:use
    clojure.contrib.test-is)

  (:import
    (com.imcode.imcms.api ContentLoop ContentIndexes)
    (org.springframework.context.support ClassPathXmlApplicationContext FileSystemXmlApplicationContext)
    (com.imcode.imcms.schema SchemaUpgrade Vendor)
    (java.io File)))


(def spring-app-context
  (FileSystemXmlApplicationContext. (str "file:" (project/file-path "src/test/resources/testApplicationContext.xml"))))

(defn get-spring-bean [name]
  (.getBean spring-app-context name))


(defmacro defbean
  ([bean-name]
    `(defbean ~bean-name ~bean-name))

  ([name bean-name]
    (let [bean-name-str (str bean-name)]
      `(def ~name (get-spring-bean ~bean-name-str)))))

(defbean meta-dao metaDao)
(defbean language-dao languageDao)
(defbean loop-dao contentLoopDao)

(defn get-loop [] (last (.getContentLoops loop-dao 1001 2)))
(defn get-loop-contents [] (.getContents (get-loop)))


(deftest test-create-loop
  ;(schema/recreate-empty-upgrade (schema/test-db-schema))
  (let [loops (.getContentLoops loop-dao 1001 2)
        lastLoop (last loops)
        loopNo (inc (if lastLoop (.getNo lastLoop) 0))
        loop (doto (ContentLoop.) (.setNo loopNo) (.setBaseIndex 1) (.setMetaId 1001) (.setMetaVersion 2))
        indexes (doto (ContentIndexes.) (.setSequence 1) (.setLowerOrder -3) (.setHigherOrder 3))]
    (.setContentIndexes loop indexes)
    ;(.deleteAllLoops loopDao)
    (println "loop no" loopNo)
    (.saveContentLoop loop-dao 1001 loop)
    (dotimes [_ 10]
      (.addFisrtContent loop-dao (.getId loop)))

    (dotimes [_ 10]
      (.addLastContent loop-dao (.getId loop)))))


(deftest test-move-content-up
  (let [contents (get-loop-contents)
        c1 (first contents)
        c2 (second contents)]
    (is c1 "C1 Exists")
    (is c2 "C2 Exists")

    (let [c1OrdIndex (.getOrderIndex c1)
          c1SeqIndex (.getSequenceIndex c1)
          c2OrdIndex (.getOrderIndex c2)
          c2SeqIndex (.getSequenceIndex c2)]

      (is (< c1OrdIndex c2OrdIndex) "Ordered")

      (.moveContentUp loop-dao (get-loop) (.getId c2))

      (let [contents (get-loop-contents)
            c1 (first contents)
            c2 (second contents)]

        (is c1 "C1 Exists")
        (is c2 "C2 Exists")

        (let [c1OrdIndexAfterMove (.getOrderIndex c1)
              c1SeqIndexAfterMove (.getSequenceIndex c1)
              c2OrdIndexAfterMove (.getOrderIndex c2)
              c2SeqIndexAfterMove (.getSequenceIndex c2)]
          
          (is (< c1OrdIndexAfterMove c2OrdIndexAfterMove) "Ordered")
          (is (= c1SeqIndex c2SeqIndexAfterMove) "Same")
          (is (= c2SeqIndex c1SeqIndexAfterMove) "Same"))))))
          
