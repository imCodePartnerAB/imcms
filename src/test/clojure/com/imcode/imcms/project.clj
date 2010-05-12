(ns
  #^{:doc "imCMS project's interaction."}
  com.imcode.imcms.project     
  (:require
    ;[clojure.contrib.logging :as log]
    [clojure.contrib.str-utils :as su]
    ;[clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]
    [com.imcode.cljlib
      [misc-utils :as utils]
      [file-utils :as file-utils]])
  
  (:use
    clojure.contrib.test-is
    clojure.contrib.repl-utils
    clojure.contrib.duck-streams
    clojure.contrib.def
    [clojure.contrib.except :only [throw-if throw-if-not]]
    [com.imcode.cljlib.file-utils :as file-utils :only [throw-if-not-dir throw-if-not-file]])


  (:import
    (java.io File)
    (imcode.server Imcms)
    (org.springframework.context.support FileSystemXmlApplicationContext)))


(def base-dir (atom (.getCanonicalFile (File. "."))))

(def spring-app-context (atom nil))

(defn base-dir-path
  "Returns base dir full path."
  []
  (.getCanonicalPath @base-dir))


(defn ch-base-dir! [new-path]
  (reset! base-dir (.getCanonicalFile (File. new-path))))


(defn- filesystem-node
  [relative-path check-fn]
  (let [node (File. @base-dir relative-path)]
    (if check-fn (check-fn node) node)))


(defn subdir
  "Returns project's subdir - non recursive."
  ([relative-path]
    (subdir relative-path true))

  ([relative-path check]
    (filesystem-node relative-path (if check throw-if-not-dir))))

(defn subdir-path
  "Returns project's subdir canonical path."
  [relative-path]
  (.getCanonicalPath (subdir relative-path)))


(defn file
  "Returns project's file."
  ([relative-path]
    (file relative-path true))

  ([relative-path check]
    (filesystem-node relative-path (if check throw-if-not-file))))


(defn file-path
  "Returns project's file canonical path."
  [relative-path]
  (.getCanonicalPath (file relative-path)))


(defn files
  "Returns files from project's subdir - non recursive."
  [dir-path filenames]
  (let [dir (subdir dir-path)]
    (map #(throw-if-not-file (File. dir %)) filenames)))

(defn get-file-fn
  "Creates function which returns project file."
  [file-path]
  #(file file-path))


(def
  #^{:doc "Function for loading project's build properties from the build.properties file."}
  
  build-properties (file-utils/create-file-watcher
                     (get-file-fn "build.properties")
                     (comp utils/to-keyword-key-map file-utils/load-properties)))


(defn db-schema-name "Default schema name." [] (:db-name (build-properties)))
(defn db-test-schema-name "Test schema name." [] (str (db-schema-name) "_test"))


(defn deploy-maven-jar
  "Deploy jar file to imcms maven's repo."
  [group-id artifact-id version jar-filepath]
  (let [cmd-template "mvn deploy:deploy-file -DrepositoryId=imcode -Durl=scp://garm.imcode.com:/srv/www/apache/sites/repo.imcode.com/maven2 -DgroupId=%s -DartifactId=%s -Dversion=%s -Dfile=%s -Dpackaging=jar"
        cmd (format cmd-template group-id artifact-id version jar-filepath)
        args (su/re-split #"\s" cmd)]
    (apply shell/sh args)))


(defn init-spring-app-context []
  (reset! spring-app-context
    (FileSystemXmlApplicationContext. (str "file:" (file-path "src/main/web/WEB-INF/applicationContext.xml")))))

(defn init-imcms
  "Initializes Imcms for tests."
  []
  (when-not @spring-app-context
    (init-spring-app-context))

  (init-spring-app-context)
  (Imcms/setPath (subdir "src/test/resources") (subdir "src/test/resources"))
  (Imcms/setApplicationContext @spring-app-context)
  (Imcms/setUpgradeDatabaseSchemaOnStart false))



(defmacro sh [& args]
  (let [cmd (map str args)]
    `(shell/sh ~@cmd)))


(defn loc
  "Returns loc in a project's dir."
  ([]
    (loc "."))

  ([#^String dir]
    (file-utils/loc
      (file-utils/files dir #"\.(java|jsp|htm|html|xml|properties|sql|clj)$"))))
