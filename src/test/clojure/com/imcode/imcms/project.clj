(ns
  #^{:doc "imCMS project."}
  com.imcode.imcms.project
  
  (:require
    (com.imcode.imcms
      boot ;must always appear first
      [misc :as misc-lib]
      [fs :as fs-lib])
    
    [clojure.java.io :as io]
    [clojure.string :as str]

    [clojure.contrib.shell-out :as shell])
  
  (:use
    (clojure.contrib
      repl-utils
      def
      [except :only (throw-if throw-if-not)])
    
    [clojure.contrib.map-utils :only (safe-get safe-get-in)]
    [com.imcode.imcms.fs :only (throw-if-not-dir throw-if-not-file)])

  (:import
    (imcode.server Imcms)
    (org.springframework.context.support FileSystemXmlApplicationContext)))


(defonce basedir (.getCanonicalPath (io/file ".")))

(defonce spring-app-context nil)


(defn ch-basedir! [new-path]
  (alter-var-root #'basedir (fn [_] (.getCanonicalPath (io/file new-path)))))


(defn- fs-node
  [relative-path check-fn]
  (let [node (io/file basedir relative-path)]
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
    (map #(throw-if-not-file (io/file dir %)) filenames)))


(defn get-file-fn
  "Creates function which returns project file."
  [file-path]
  #(file file-path))


(def
  #^{:doc "Function of zero arity.
           Loads and returns project's build properties from the build.properties file as a map.
           Map keys are converted into keywords."}
  
  build-properties (fs-lib/create-file-watcher
                     (get-file-fn "build.properties")
                     (comp misc-lib/to-keyword-key-map fs-lib/load-properties)))


(defn safe-select-keys [map key & keys]
  (into {}
    (for [k (cons key keys)]
      [k, (safe-get map k)]))) 


;(defn build-properties*
;  "Returns build properties as a map.
;  Throws an exception if property does not exists."
;  [key & keys]
;  (let [bp (build-properties)]
;    (into {}
;      (for [k (cons key keys)]
;        [k, (safe-get bp k)]))))


(defn build-property [name] (safe-get (build-properties) name))


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

(def *file-exts* "java|jsp|htm|html|xml|properties|sql|clj|scala")


(defn loc
  "Returns loc in path. By default returns project's loc."
  ([]
    (loc *file-exts*))

  ([file-exts]
    (loc basedir file-exts))

  ([^String dir file-exts]
    (reduce +
      (map fs-lib/loc
      (fs-lib/files dir (re-pattern (str "\\.(" file-exts ")$")))))))


(defn deploy-maven-jar
  "Deploy jar file to imcms maven's repo."
  [group-id artifact-id version jar-filepath]
  (let [cmd-template "mvn deploy:deploy-file -DrepositoryId=imcode -Durl=scp://garm.imcode.com:/srv/www/apache/sites/repo.imcode.com/maven2 -DgroupId=%s -DartifactId=%s -Dversion=%s -Dfile=%s -Dpackaging=jar"
        cmd (format cmd-template group-id artifact-id version jar-filepath)
        args (str/split cmd #"\s")]
    (apply shell/sh args)))