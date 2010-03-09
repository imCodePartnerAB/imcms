(ns
  #^{:doc "File utils."}
  com.imcode.imcms.file-utils
  (:use
    [clojure.contrib
      [duck-streams :only (reader)]
      [except :only (throw-if throw-if-not)]
      [str-utils2 :only (blank?)]])

  (:import
    (java.io File)
    (clojure.lang ISeq)
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
  (with-open [r (reader file)]
    (doto (Properties.)
      (.load r))))


(defn create-resource-watcher
  "Creates resource watcher function.
   resource-getter is a fn of no args which returns a resource being watched;
   resource-handler is a fn of one arg which is called if resource was modified since its last call, takes a resource as a parameter.;
   resource-state-creator is a fn of one arg intended to create a watched resource's state snapshot, takes a resource as a parameter.;
   Return resource-handler fn's call result."
  [resource-getter, resource-handler, resource-state-creator]
  (let [lock (Object.)
        resource-state (ref nil)
        resource-handler-result (ref nil)]

    (fn resource-watcher []
      (locking lock
        (let [resource (resource-getter)
              new-resource-state (resource-state-creator resource)]
          (when-not (= @resource-state new-resource-state)
            (dosync
              (ref-set resource-state new-resource-state)
              (ref-set resource-handler-result (resource-handler resource))))


          @resource-handler-result)))))


(defn create-file-watcher
  "Creates and returns a file wathcher."
  [file-getter, file-handler]
  (create-resource-watcher file-getter file-handler #(.lastModified %)))



(defn call-if-modified
  "Deprecated, use create-file-watcher instead."
  [get-file-fn, file-fn]
  (create-file-watcher get-file-fn, file-fn))


(defn files
  "Returns lazy file seq under dir which are match filename re."
  [#^String dir, filename-re]
  (filter #(and
             (.isFile %)
             (re-find filename-re (.getName %)))

    (file-seq (File. dir))))


(defmulti loc class)

;"Returns lines of code in a text file."
(defmethod loc String [file-path]
  (loc (File. file-path)))

  
;"Returns lines of code in a text file."
(defmethod loc File [file]
  (with-open [r (reader file)]
    (count
      (remove blank? (line-seq r)))))


;"Returns lines of code in text files."
(defmethod loc ISeq [files]
  (reduce +
    (map loc files)))