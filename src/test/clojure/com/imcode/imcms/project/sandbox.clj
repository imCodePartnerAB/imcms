(ns
  #^{:doc "Sandbox."}
  com.imcode.imcms.project.sandbox  
  (:require
    [clojure.contrib.duck-streams :as ds]
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]

    [com.imcode.cljlib.fs :as fs-lib]
    [com.imcode.imcms.db-test :as db]
    [com.imcode.imcms.project :as p]
    [com.imcode.imcms.project.lucene :as l]))


(defn recreate-utvakten-db []
  (db/recreate "utvakten" ["/Users/ajosua/utvakten.sql"]))

(defn recreate-and-upgrade-utvakten-db []
  (recreate-utvakten-db)
  (db/run-scripts "utvakten"
    (p/files "src/main/web/WEB-INF/sql" ["diff/mysql-schema-diff-4.11-6.2.sql"])))


(def BOM-UTF-8 '(239 187 191))
(def BOM-UTF-16_BE '(254 255))
(def BOM-UTF-16_LE '(255 254))


(def *bom-pattern* (map byte BOM-UTF-8))


(defn delete
  "Deletes subsequences which eqaul to a pattern from a data.
   Args:
    data - input seq.
    pattern - a finit seq to be removed from a data."
  [data pattern]
  (let [pattern-seq (seq pattern)
        chunk-size (count pattern)]

    (if (zero? chunk-size)
      data
      (loop [acc (transient []), data data]
        (let [chunk (take chunk-size data)]
          (if (< (count chunk) chunk-size)
            (into (persistent! acc) chunk)

            (if (= chunk pattern-seq)
              (recur acc (drop chunk-size data))
              (recur (conj! acc (first data)), (rest data)))))))))


(defn contains-boms [file-data]
  (some #(= *bom-pattern* %)
        (partition (count *bom-pattern*) 1 file-data)))


(defn files-with-boms [#^String dir]
  (doseq [file (fs-lib/files dir #"\.(vm|jsp|htm|html|xml|java|tag|frag|txt|properties|tld)$") :when (contains-boms (ds/to-byte-array file))]
    (println file)))


(defn rewrite-file [file byte-seq]
  (with-open [out (java.io.FileOutputStream. file)]
    (.write out (into-array Byte/TYPE byte-seq))))


(defn fix-files [#^String dir]
  (doseq [file (fs-lib/files dir #"\.(vm|jsp|htm|html|xml|java|tag|frag|txt|properties|tld)$")]
    (let [file-bytes (ds/to-byte-array file)]
      (when (contains-boms file-bytes)
        (rewrite-file file (del file-bytes *bom-pattern*))))))