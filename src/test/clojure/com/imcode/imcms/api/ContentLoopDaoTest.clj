(ns com.imcode.imcms.api.ContentLoopDaoTest
  (:require
    [clojure.contrib.sql :as sql]
    [com.imcode.imcms
      [project :as project]
      [factory :as factory]
      [schema :as schema]
      [spring :as spring]])

  (:use
    clojure.contrib.test-is)

  (:import
    (com.imcode.imcms.api ContentLoop Content)
    (org.springframework.context.support ClassPathXmlApplicationContext FileSystemXmlApplicationContext)
    (com.imcode.imcms.schema SchemaUpgrade Vendor)
    (java.io File)))


(def *text-doc-id* 1001)
(def *text-doc-version-no* 0)
(def *loop-no* 1)

(defn get-loops [] (.getContentLoops spring/contentLoopDao *text-doc-id* *text-doc-version-no*))

(defn get-loop [] (.getContentLoop spring/contentLoopDao *text-doc-id* *text-doc-version-no* *loop-no*))
(defn get-loop-contents [] (.getContents (get-loop)))

(defn get-last-loop []
  (last (get-loops)))


(defn delete-loops []
  (.deleteLoops spring/contentLoopDao *text-doc-id* *text-doc-version-no*))

(defn create-loop
  ([]
    (create-loop *text-doc-id* *text-doc-version-no* *loop-no*))

  ([loop-no]
    (create-loop *text-doc-id* *text-doc-version-no* loop-no))  

  ([doc-id doc-version-no loop-no]
    (let [loop (factory/new-content-loop doc-id doc-version-no loop-no)]

      (.saveContentLoop spring/contentLoopDao loop))))    


(defn get-next-loop-no [] 
  (let [loop (last (get-loops))]
    (if loop
      (inc (.getNo loop))
      0)))

(defn create-next-loop
  ([]
    (create-next-loop 0))

  ([extra-content-count]
    (let [no (get-next-loop-no)
          loop (create-loop no)
          loop-id (.getId loop)]
      
      (when (> extra-content-count 0)
        (dotimes [_ extra-content-count]
          (.addLastContent spring/contentLoopDao loop-id)))

      (get-last-loop))))

(deftest test-create-loop
  (let [loop (create-next-loop)]
        
    (dotimes [_ 10]
      (.addFisrtContent spring/contentLoopDao (.getId loop)))

    (dotimes [_ 10]
      (.addLastContent spring/contentLoopDao (.getId loop)))

    loop))


(deftest test-move-content-up
  (let [loop (create-next-loop 1)
        contents (.getContents loop)
        c1 (first contents)
        c2 (second contents)]
    (is c1 "C1 Exists")
    (is c2 "C2 Exists")

    (let [c1OrdIndex (.getOrderIndex c1)
          c1SeqIndex (.getIndex c1)
          c2OrdIndex (.getOrderIndex c2)
          c2SeqIndex (.getIndex c2)]

      (is (< c1OrdIndex c2OrdIndex) "Ordered")

      (.moveContentUp spring/contentLoopDao (.getId loop) (.getId c2))

      (let [contents (.getContents (get-last-loop))
            c1 (first contents)
            c2 (second contents)]

        (is c1 "C1 Exists")
        (is c2 "C2 Exists")

        (let [c1OrdIndexAfterMove (.getOrderIndex c1)
              c1SeqIndexAfterMove (.getIndex c1)
              c2OrdIndexAfterMove (.getOrderIndex c2)
              c2SeqIndexAfterMove (.getIndex c2)]
          
          (is (< c1OrdIndexAfterMove c2OrdIndexAfterMove) "Ordered")
          (is (= c1SeqIndex c2SeqIndexAfterMove) "Same")
          (is (= c2SeqIndex c1SeqIndexAfterMove) "Same"))))))
          
