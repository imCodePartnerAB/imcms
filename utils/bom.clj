(ns
  #^{:doc "Routines for finding and removing BOM (byte-order marks) from files."}
  bom
  (:require
    [clojure.contrib.duck-streams :as ds]
    [com.imcode.cljlib.fs :as fs-lib]))


;;;; Representations of byte order marks by encoding

(def BOM-UTF-8 [0xEF 0xBB 0xBF])
(def BOM-UTF-16_BE [0xFE 0xFF])
(def BOM-UTF-16_LE [0xFF 0xFE])


;;;; Default BOM as Byte seq
(def *bom-pattern* (map byte BOM-UTF-8))


(defn delete
  "Deletes all sequences in a coll which matches to a pattern."
  [coll pattern]
  (let [pattern-seq (seq pattern)
        chunk-size (count pattern)]

    (if (zero? chunk-size)
      coll
      (loop [acc (transient []), data coll]
        (let [chunk (take chunk-size data)]
          (if (< (count chunk) chunk-size)
            (into (persistent! acc) chunk)

            (if (= chunk pattern-seq)
              (recur acc (drop chunk-size data))
              (recur (conj! acc (first data)), (rest data)))))))))


(defn contains-bom?
  "Converts argument (a String, File, InputStream, or Reader) into
   a Java byte array (if its not allready a byte array) and returns if it contains the *bom-pattern*."
  [arg]
  (let [array (ds/to-byte-array arg)]
    (some #(= *bom-pattern* %)
          (partition (count *bom-pattern*) 1 array))))


(defn overwrite-file
  "Overwrites a file."
  [file byte-seq]
  (with-open [out (java.io.FileOutputStream. file)]
    (.write out (into-array Byte/TYPE byte-seq))))


(defn files-with-boms
  "Returls seq of files in a dir that contains BOM."
  [#^String dir]
  (filter #(contains-bom? %)
          (fs-lib/files dir #"\.(vm|jsp|htm|html|xml|java|tag|frag|txt|properties|tld)$")))


(defn fix-file [file]
  (let [file-bytes (ds/to-byte-array file)
       fixed-file-bytes (delete file-bytes *bom-pattern*)]

    (overwrite-file file fixed-file-bytes)))


(defn fix-files [#^String dir]
  (doseq [file (files-with-boms dir)] (fix-file file)))
