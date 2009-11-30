(ns com.imcode.imcms.spring
  (:require
    [com.imcode.imcms.project :as project])

  (:import
    (org.springframework.context.support ClassPathXmlApplicationContext FileSystemXmlApplicationContext)))


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

