(ns com.imcode.imcms.api.content_loop
  (:require
    [clojure.contrib.sql :as sql]
    [com.imcode.imcms.project :as project]
    [com.imcode.imcms.schema :as schema])

  (:use
    clojure.contrib.test-is)

  (:import
    com.imcode.imcms.api.ContentLoop
    (org.springframework.context.support ClassPathXmlApplicationContext FileSystemXmlApplicationContext)
    (com.imcode.imcms.schema SchemaUpgrade Vendor)
    (java.io File)))


(def spring-app-context
  (FileSystemXmlApplicationContext. (str "/" (project/file-path "src/test/resources/testApplicationContext.xml"))))

(defn get-spring-bean [name]
  (.getBean spring-app-context name))


(deftest test-create-loop
  ;(schema/recreate-empty-upgrade (schema/test-db-schema))
  (let [loopDao (get-spring-bean "contentLoopDao")
        loop (doto (ContentLoop.) (.setNo 1) (.setBaseIndex 1) (.setMetaId 1001) (.setMetaVersion 2))]))



