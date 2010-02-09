(ns com.imcode.imcms.lucene
  (:import
    (java.io File)
    (org.apache.lucene.store RAMDirectory)
    (org.apache.lucene.document Document Field Field$Store Field$Index DateTools DateTools$Resolution)
    (org.apache.lucene.index IndexReader IndexWriter Term)
    (org.apache.lucene.analysis SimpleAnalyzer)
    (org.apache.lucene.queryParser QueryParser MultiFieldQueryParser)
    (org.apache.lucene.search IndexSearcher TermQuery Hits BooleanClause BooleanClause$Occur Sort))
  (:use clojure.contrib.duck-streams))


(defn mk-ramdir []
  (RAMDirectory.))

(defn mk-dbdir []
  (RAMDirectory.))

(defn index
  "Indexes project's clojure files to the provided lucene dir."
  [dir]
  (with-open [w (IndexWriter. dir (SimpleAnalyzer.))]
    (doseq [file (file-seq (File. "src")) :when (and
                                                  (.isFile file)
                                                  ;)]
                                                  (-> file .getName (.endsWith ,, ".clj")))]

      (println "Indexing " (.getCanonicalPath file))
      
      (let [doc (Document.)
            last-modified-ms (DateTools/timeToString (.lastModified file) DateTools$Resolution/MILLISECOND)]
        (.add doc (Field. "name" (.getName file) Field$Store/YES Field$Index/ANALYZED))
        (.add doc (Field. "path" (.getCanonicalPath file) Field$Store/YES Field$Index/ANALYZED))
        (.add doc (Field. "content" (slurp* file) Field$Store/NO Field$Index/ANALYZED))        
        (.add doc (Field. "last-modified" last-modified-ms Field$Store/YES Field$Index/UN_TOKENIZED))
        (.addDocument w doc))))
  dir)


(defn- search
  "Returns hits"
  [dir, query]
  (let [searcher (IndexSearcher. dir)
        hits (.search searcher query (Sort. "last-modified" true))
        docs (iterator-seq (.iterator hits))]
    (for [d docs] [(.get d "name"), (.get d "path"), (.get d "last-modified")])))               


(defn term-search [dir, value]
  (let [term (Term. "content" value)
        query (TermQuery. term)]
    (search dir query)))


(defn query-search [dir, query-str]
  (let [fields (into-array ["name" "content"])
        flags (into-array [BooleanClause$Occur/SHOULD BooleanClause$Occur/SHOULD])
        query (MultiFieldQueryParser/parse query-str fields flags (SimpleAnalyzer.))]
    (search dir query)))


(def q query-search)