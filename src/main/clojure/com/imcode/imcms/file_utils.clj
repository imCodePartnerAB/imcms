(ns com.imcode.imcms.file-utils
  (:use
    clojure.contrib.duck-streams
    [clojure.contrib.except :only (throw-if throw-if-not)])

  (:import
    (java.io File)
    (java.util Properties)))


(defn throw-if-not-file
  [file]
  (throw-if-not (.isFile file)
    (format "File \"%s\" does not exists." (.getCanonicalPath file)))
  
  file)


(defn throw-if-not-dir
  [dir]
  (throw-if-not (.isDirectory dir)
    (format "Directory \"%s\" does not exists." (.getCanonicalPath dir)))

  dir)


(defn load-properties
  "Load properties from a file."
  [file]
  (with-open [r (reader file)]
    (doto (Properties.) (.load r))))