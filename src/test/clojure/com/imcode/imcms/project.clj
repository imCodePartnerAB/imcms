(ns com.imcode.imcms.project
  (:require
    ;[clojure.contrib.logging :as log]
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]
    [com.imcode.imcms
      [misc-utils :as utils]
      [db-utils :as db-utils]])
  
  (:use
    clojure.contrib.test-is
    clojure.contrib.repl-utils
    clojure.contrib.duck-streams

    [clojure.contrib.except :only [throw-if throw-if-not]]
    [com.imcode.imcms.file-utils :as file-utils :only [throw-if-not-dir throw-if-not-file]])


  (:import
    (java.io File)
    (org.apache.commons.dbcp BasicDataSource)))


(def PST #(.printStackTrace *e))


(def base-dir (atom (.getCanonicalFile (File. "."))))


(defn change-base-dir [new-path]
  (reset! base-dir (.getCanonicalFile (File. new-path))))


(defn base-dir-path [] (.getCanonicalPath @base-dir))


(defn- filesystem-node
  [relative-path check-fn]
  (let [node (File. @base-dir relative-path)]
    (if check-fn (check-fn node) node)))


(defn subdir
  "Returns project's subdir."
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
  "Returns files from project's subdir."
  [dir-path filenames]
  (let [dir (subdir dir-path)]
    (map #(throw-if-not-file (File. dir %)) filenames)))



(def load-build-properties
  ;"Loads and returns properties from the project build properties file."
  (let [lock (Object.)
        state (atom {:properties nil, :last-modified nil})]

    (fn []
      (locking lock
        (let [prop-file (file "build.properties")
              last-modified (.lastModified prop-file)]

          (if-not (= last-modified (:last-modified @state))
            (reset! state {:properties (utils/to-keyword-key-map(file-utils/load-properties prop-file))
                           :last-modified last-modified}))

          (:properties @state))))))


(defn create-db-datasource
  "Creates database datasource based on build properties."
  []
  (let [properties (load-build-properties)
        db-url (db-utils/create-url (:db-target properties) (:db-host properties) (:db-port properties))]

    (doto (BasicDataSource.)
      (.setUsername (:db-user properties))
      (.setPassword (:db-pass properties))
      (.setDriverClassName (:db-driver properties))
      (.setUrl db-url))))


(defn db-spec []
  {:datasource (create-db-datasource)})


(defn db-metadata []
  (db-utils/get-metadata (db-spec)))


(defn print-db-metadata
  []
  (let [meta-map (bean (db-metadata))
        meta-keys (sort (keys meta-map))]

    (doseq [k meta-keys]
      (println k " -> " (k meta-map)))))


(defn deploy-maven-jar
  "Deploy jar file to imcms maven's repo."
  [group-id artifact-id version jar-filepath]
  (let [cmd-template "mvn deploy:deploy-file -DrepositoryId=imcode -Durl=scp://garm.imcode.com:/srv/www/apache/sites/repo.imcode.com/maven2 -DgroupId=%s -DartifactId=%s -Dversion=%s -Dfile=%s -Dpackaging=jar"
        cmd (format cmd-template group-id artifact-id version jar-filepath)
        args (su/re-split #"\s" cmd)]
    (apply shell/sh args)))


(defmacro sh [& args]
  (let [cmd# (map str args)]
    `(shell/sh ~@cmd#)))