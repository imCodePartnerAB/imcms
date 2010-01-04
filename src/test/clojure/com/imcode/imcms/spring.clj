(ns com.imcode.imcms.spring
  (:require
    [com.imcode.imcms
      [project :as project]
      [runtime :as rt]])

  (:import
    (org.springframework.context.support FileSystemXmlApplicationContext)))


(def spring-app-context
  (FileSystemXmlApplicationContext. (str "file:" (project/file-path "src/test/resources/testApplicationContext.xml"))))


(defn get-spring-bean [name]
  (.getBean spring-app-context name))


(defmacro defbean
  ([bean-name]
    `(defbean ~bean-name ~bean-name))

  ([alias-name bean-name]
    (let [bean-name-str (str bean-name)]
      `(def ~alias-name (get-spring-bean ~bean-name-str)))))


(defbean metaDao)
(defbean languageDao)
(defbean contentLoopDao)

