(ns
  #^{:doc "Project's databse schema fns."}
  com.imcode.imcms.test.project.db.upgrade
  
  (:use
    (clojure.contrib duck-streams test-is))

  (:require
    (com.imcode.imcms
      [project :as project])

    (com.imcode.imcms.project
      [db :as db])    

    (com.imcode.cljlib
      [file-utils :as file-utils])

    (clojure.contrib [sql :as sql])))

(def xml-conf-file (project/get-file-fn "src/main/resources-conf/schema-upgrade.xml"))
(def xsd-conf-file (project/get-file-fn "src/main/resources-conf/schema-upgrade.xsd"))


(defn xml-conf-file-path [] (.getCanonicalPath (xml-conf-file)))
(defn xsd-conf-file-path [] (.getCanonicalPath (xsd-conf-file)))


(def slurp-xml-conf (file-utils/create-file-watcher xml-conf-file slurp*))
(def slurp-xsd-conf (file-utils/create-file-watcher xsd-conf-file slurp*))


(deftest test-settings
  (testing "Files/Dirs"
    (println "xml-conf-file: " (xml-conf-file))
    (println "xsd-conf-file: " (xsd-conf-file)))

  (testing "Paths"
    (println "xml-conf-file-path: " (xml-conf-file-path))
    (println "xsd-conf-file-path: " (xsd-conf-file-path)))

  (testing "Contents"
    (println "xml-conf-file content length: " (count (slurp-xml-conf)))
    (println "xsd-conf-file content length: " (count (slurp-xsd-conf)))))

