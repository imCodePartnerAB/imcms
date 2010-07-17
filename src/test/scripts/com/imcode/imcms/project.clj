(ns
  #^{:doc "imCMS project."}
  com.imcode.imcms.project
  
  (:require
    com.imcode.imcms.boot
    
    [com.imcode.imcms.configurator :as configurator]

    (clojure.contrib
      [logging :as log]
      [str-utils :as su]
      [str-utils2 :as su2]
      [shell-out :as shell])

    (com.imcode.cljlib
      [misc :as misc-lib]
      [fs :as fs-lib]))
  
  (:use
    (clojure.contrib
      repl-utils
      duck-streams
      def
      [except :only (throw-if throw-if-not)])

    [com.imcode.cljlib.fs :only (throw-if-not-dir throw-if-not-file)])

  (:import
    (java.io File)
    (imcode.server Imcms)
    (org.springframework.context.support FileSystemXmlApplicationContext)))


(defonce basedir (.getCanonicalPath (File. ".")))

(defonce spring-app-context nil)


(defn ch-basedir! [new-path]
  (alter-var-root #'basedir (fn [_] (.getCanonicalPath (File. new-path)))))


(defn- fs-node
  [relative-path check-fn]
  (let [node (File. basedir relative-path)]
    (if check-fn (check-fn node) node)))


(defn subdir
  "Returns project's subdir - non recursive."
  ([relative-path]
    (subdir relative-path true))

  ([relative-path check]
    (fs-node relative-path (if check throw-if-not-dir))))

(defn subdir-path
  "Returns project's subdir canonical path."
  [relative-path]
  (.getCanonicalPath (subdir relative-path)))


(defn file
  "Returns project's file."
  ([relative-path]
    (file relative-path true))

  ([relative-path check]
    (fs-node relative-path (if check throw-if-not-file))))


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
  
  build-properties (fs-lib/create-file-watcher
                     (get-file-fn "build.properties")
                     (comp misc-lib/to-keyword-key-map fs-lib/load-properties)))


(defn deploy-maven-jar
  "Deploy jar file to imcms maven's repo."
  [group-id artifact-id version jar-filepath]
  (let [cmd-template "mvn deploy:deploy-file -DrepositoryId=imcode -Durl=scp://garm.imcode.com:/srv/www/apache/sites/repo.imcode.com/maven2 -DgroupId=%s -DartifactId=%s -Dversion=%s -Dfile=%s -Dpackaging=jar"
        cmd (format cmd-template group-id artifact-id version jar-filepath)
        args (su/re-split #"\s" cmd)]
    (apply shell/sh args)))


(defn init-spring-app-context! []
  (alter-var-root #'spring-app-context
    (fn [_] (FileSystemXmlApplicationContext. (str "file:" (file-path "src/main/web/WEB-INF/applicationContext.xml"))))))


(defn init-imcms
  "Initializes Imcms for tests."
  ([]
    (init-imcms false))

  ([prepare-db-on-start]
    (when-not spring-app-context
      (init-spring-app-context!))

    (Imcms/setPath (subdir "src/main/webapp"))
    (Imcms/setApplicationContext spring-app-context)
    (Imcms/setPrepareDatabaseOnStart prepare-db-on-start)))


(defn loc
  "Returns loc in path. By default returns project's loc."
  ([]
    (loc basedir))

  ([#^String dir]
    (fs-lib/loc
      (fs-lib/files dir #"\.(java|jsp|htm|html|xml|properties|sql|clj)$"))))


(defn create-conf []
  (let [basedir (subdir-path "src/main/web")]
    (configurator/create-conf (file-path "src/main/resources/conf.clj")
                              {:base.dir basedir})))
