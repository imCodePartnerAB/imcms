(ns
  #^{:doc "Routines for finding and removing BOM (byte-order marks) from files.
           Usage:
              (print-bom-files dir) - recursively print files in a dir that contain BOM's.
              (fix-files dir) - recursively removes BOMs from files in a dir."}
  bom
  (:require
    [clojure.contrib.duck-streams :as ds]
    [com.imcode.cljlib.fs :as fs-lib])

  (:use
    clojure.test
    [clojure.contrib.seq-utils :only (includes?)]))


;;; Representations of byte order marks by encoding

(def BOM-UTF-8 [0xEF 0xBB 0xBF])
(def BOM-UTF-16_BE [0xFE 0xFF])
(def BOM-UTF-16_LE [0xFF 0xFE])


;;; Default BOM as a Byte seq
(def *bom-pattern* (map byte BOM-UTF-8))

;;; Checked files re
(def *checked-files-re* #"\.(vm|jsp|htm|html|xml|java|tag|frag|txt|properties|tld)$")

;;;;
;;;; Pure fns
;;;;

;;; Ineffective
(defn delete
  "Deletes all subsequences in a coll which matches to a pattern."
  [coll pattern]
  (let [pattern-seq (seq pattern)
        chunk-size (count pattern)]
    (seq
      (if (zero? chunk-size)
        coll
        (loop [acc (transient []), data coll]
          (let [chunk (take chunk-size data)]
            (if (< (count chunk) chunk-size)
              (into (persistent! acc) chunk)

              (if (= chunk pattern-seq)
                (recur acc (drop chunk-size data))
                (recur (conj! acc (first data)), (rest data))))))))))


;;; Ineffective
(defn subseq?
  "Returns true if ys is a subsequence of xs."
  [ys xs]
  (includes? (partition (count ys) 1 xs)
             (seq ys)))


;;;;
;;;; Side effect fns
;;;;

(defn contains-bom?
  "Converts argument (a String, File, InputStream, or Reader) into
   a Java byte array (if its not allready a byte array) and returns if it contains the *bom-pattern*."
  [arg]
  (subseq? *bom-pattern* (ds/to-byte-array arg)))


(defn overwrite-file
  "Overwrites a file."
  [file byte-seq]
  (with-open [out (java.io.FileOutputStream. file)]
    (.write out (into-array Byte/TYPE byte-seq))))


(defn files-with-boms
  "Returns seq of files in a dir that contains BOMs."
  [#^String dir]
  (filter #(contains-bom? %)
          (fs-lib/files dir  *checked-files-re*)))


(defn print-bom-files [#^String dir]
  (doseq [file (files-with-boms dir)]
    (println (.getCanonicalPath file))))


(defn fix-file [file]
  (let [file-bytes (ds/to-byte-array file)
        fixed-file-bytes (delete file-bytes *bom-pattern*)]

    (overwrite-file file fixed-file-bytes)))


(defn fix-files
  "Recursively remove BOMs from files found in a dir."
  [#^String dir]
  (doseq [file (files-with-boms dir)] (fix-file file)))


;;;; Tests

(deftest test-delete
  (is (nil?
         (delete nil nil)))

  (is (nil?
         (delete nil (range 10))))

  (is (= [1 2 3]
         (delete [1 2 3] nil)))  

  (is (= '(0 4 5 6)
         (delete [0 1 2 3 4 5 6] [1 2 3])))


  (is (= '(a b c)
         (delete '[a 0 1 2 3 b 0 1 2 3 c 0 1 2 3] [0 1 2 3]))))


(deftest test-subseq?
  (is (subseq? [0] (range 10)))
  (is (subseq? [1 2 3] (range 10)))
  (is (subseq? (range 9) (range 10)))
  (is (subseq? (range 10) (range 10)))

  (is (not (subseq? nil nil)))
  (is (not (subseq? nil (range 10))))
  (is (not (subseq? (range 10) nil)))
  
  (is (not (subseq? [11 12 13] (range 10))))
  (is (not (subseq? (range 11) (range 10)))))