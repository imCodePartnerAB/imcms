(ns com.imcode.imcms.api.content_loop
  (:require
    [clojure.contrib.sql :as sql]
    [com.imcode.imcms.project :as project]
    [com.imcode.imcms.schema :as schema]
    [com.imcode.imcms.spring :as spring])

  (:use
    clojure.contrib.test-is)

  (:import
    (com.imcode.imcms.api ContentLoop ContentIndexes)
    (org.springframework.context.support ClassPathXmlApplicationContext FileSystemXmlApplicationContext)
    (com.imcode.imcms.schema SchemaUpgrade Vendor)
    (java.io File)))


(def *meta-id* 1001)
(def *doc-version* 2)
(def *loop-no* 1)

(defn get-loops [] (.getContentLoops spring/loop-dao *meta-id* *doc-version*))

(defn get-loop [] (.getContentLoop spring/loop-dao *meta-id* *doc-version* *loop-no*))
(defn get-loop-contents [] (.getContents (get-loop)))


(defn delete-loops []
  (.deleteLoops spring/loop-dao *meta-id* *doc-version*))

(defn create-loop
  ([]
    (create-loop *meta-id* *doc-version* *loop-no*))

  ([loop-no]
    (create-loop *meta-id* *doc-version* loop-no))  

  ([meta-id doc-version loop-no]
    (let [indexes (doto (ContentIndexes.) (.setSequence 0) (.setLowerOrder 0) (.setHigherOrder 0))
          loop (doto (ContentLoop.)
                 (.setNo loop-no) (.setBaseIndex 0) (.setMetaId meta-id)
                 (.setMetaVersion doc-version) (.setContentIndexes indexes))]

      (.saveContentLoop spring/loop-dao meta-id loop)

      loop)))    


(deftest test-create-loop
  (let [last-loop (last (get-loops))
        no (if last-loop (inc (.getNo last-loop)) 0)
        loop (create-loop no)]
        
    (dotimes [_ 10]
      (.addFisrtContent spring/loop-dao (.getId loop)))

    (dotimes [_ 10]
      (.addLastContent spring/loop-dao (.getId loop)))

    loop))


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

      (.moveContentUp spring/loop-dao (get-loop) (.getId c2))

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
          
