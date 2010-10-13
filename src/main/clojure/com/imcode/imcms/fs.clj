(ns
  #^{:doc "Filesystem utils."}
  com.imcode.imcms.fs

  (:require
   [clojure.java.io :as io])
  
  (:use
    (clojure.contrib
      [except :only (throw-if throw-if-not)])
    
    (clojure
      [string :only (blank?)]))

  (:import
    (java.io File)
    (java.util Properties)))


;(defmulti exists? class)
;
;(defmulti file? class)
;
;(defmulti dir? class)
;
;(defmethod file? File [fs-node]
;  (when (.isFile fs-node) fs-node))
;
;(defmethod file? String [fs-node-path]
;  (file? (File. fs-node-path)))
;
;(defmethod dir? File [fs-node]
;  (when (.isDirectory fs-node) fs-node))
;
;(defmethod dir? String [fs-node-path]
;  (dir? (File. fs-node-path)))
;
;(defmethod exists? File [fs-node]
;  (when (.exists fs-node) fs-node))
;
;(defmethod exists? String [fs-node-path]
;  (exists? (File. fs-node-path)))
;
;(defmethod exists? nil [fs-node]
;  nil)
;
;(defmethod file? nil [fs-node]
;  nil)
;
;(defmethod dir? nil [fs-node]
;  nil)


(defn throw-if-not-exists
  "Throws an exception if filesystem node does not exists."
  [#^File fs-node]

  (throw-if-not (.exists fs-node)
    (format "File or directory \"%s\" does not exists." (.getCanonicalPath fs-node)))

  fs-node)


(defn throw-if-not-file
  "Throws an exception if provided File object is not a file."
  [#^File file]
  (throw-if-not (.isFile file)
    (format "File \"%s\" does not exists or not a file." (.getCanonicalPath file)))
  
  file)


(defn throw-if-not-dir
  "Throws an exception if provided File object is not a directory."
  [#^File dir]
  (throw-if-not (.isDirectory dir)
    (format "Directory \"%s\" does not exists or not a directory." (.getCanonicalPath dir)))

  dir)


(defn load-properties
  "Returns new Properties object's instance populated with data from provided properties file."
  [#^File file]
  (with-open [r (io/reader file)]
    (doto (Properties.)
      (.load r))))


(defn create-resource-watcher
  "Creates resource watcher function.
   resource-getter is a fn of no args which returns a resource being watched;
   resource-handler is a fn of one arg which is called if resource was modified since its last call, takes a resource as a parameter.;
   resource-state-reader is a fn of one arg intended to read a watched resource's state, takes a resource as a parameter.;
   Return resource-handler fn's call result."
  [resource-getter, resource-handler, resource-state-reader]
  (let [lock (Object.)
        last-access-time-ms (ref nil)
        resource-state (ref nil)
        resource-handler-result (ref nil)
        state-read-delay 1000]

    (fn resource-watcher []
      (locking lock
        (let [time-ms (.getTime (java.util.Date.))]
          (if (and @last-access-time-ms
                   (< (- time-ms @last-access-time-ms) state-read-delay))
            
            @resource-handler-result
            
            (let [resource (resource-getter)
                  new-resource-state (resource-state-reader resource)]
              (when-not (= @resource-state new-resource-state)
                (dosync
                  (ref-set resource-state new-resource-state)
                  (ref-set resource-handler-result (resource-handler resource))))))

          (dosync
            (ref-set last-access-time-ms time-ms))
          
          @resource-handler-result)))))

     
(defn- create-file-getter
  "Creates file getter function which returns file from provided file path.
   A getter function throws an exception if file does not exists."
  [file-path]
  #(throw-if-not-file (io/as-file file-path)))


(defn create-file-watcher
  "Creates and returns a file watcher."
  [file-getter, file-handler]
  (let [file-state-reader  #(.lastModified %)]
    (create-resource-watcher file-getter file-handler file-state-reader)))


(defn create-file-watcher*
  "Creates a file watcher.
   file-path is a relative or absolute file path;
   file-handler is a fn which takes file created from file-path.
   file-handler is invoked if file was modified."
  [file-path, file-handler]
  (let [file-getter (create-file-getter file-path)]
    (create-file-watcher file-getter file-handler)))


(defn files
  "Returns lazy file seq under dir which are match filename re."
  [dir-path, filename-re]
  (filter #(and
             (.isFile %)
             (re-find filename-re (.getName %)))

    (file-seq (io/as-file dir-path))))


(defn loc
  "Returns lines of code in a text file."
  [file]
  (with-open [r (io/reader (io/as-file file))]
    (count
      (remove blank? (line-seq r)))))
