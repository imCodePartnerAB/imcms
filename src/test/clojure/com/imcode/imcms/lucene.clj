(ns com.imcode.imcms.lucene
  (:import
    (java.io File)
    (org.apache.lucene.store RAMDirectory)
    (org.apache.lucene.document Document Field Field$Store Field$Index)
    (org.apache.lucene.index IndexReader IndexWriter Term)
    (org.apache.lucene.analysis SimpleAnalyzer)
    (org.apache.lucene.queryParser QueryParser MultiFieldQueryParser)
    (org.apache.lucene.search IndexSearcher TermQuery Hits BooleanClause BooleanClause$Occur))
  (:use clojure.contrib.duck-streams))


(def directory (atom (RAMDirectory.)))

(defn index
  "Indexes project's clojure files"
  []
  (let [dir (RAMDirectory.)
        w (IndexWriter. dir (SimpleAnalyzer.))]
    (doseq [file (file-seq (File. "src"))]
      (when (and (.isFile file) (.endsWith (.getName file) ".clj"))
        (println "Indexing " (.getCanonicalPath file))
        (let [name (.getName file)
              content (slurp* file)
              doc (Document.)]
          (.add doc (Field. "name" name Field$Store/YES Field$Index/ANALYZED))
          (.add doc (Field. "content" content Field$Store/NO Field$Index/ANALYZED))
          (.addDocument w doc))))
    (.close w)
    (reset! directory dir)))


(defn- search
  "Returns hits"
  [query]
  (let [searcher (IndexSearcher. @directory)
        hits (.search searcher query)
        docs (iterator-seq (.iterator hits))]
    (doseq [d docs] (println (.get d "name")))))


(defn term-search [value]
  (let [term (Term. "content" value)
        query (TermQuery. term)]
    (search query)))


(defn query-search [query-str]
  (let [fields (into-array ["name" "content"])
        flags (into-array [BooleanClause$Occur/SHOULD BooleanClause$Occur/SHOULD])
        query (MultiFieldQueryParser/parse query-str fields flags (SimpleAnalyzer.))]
    (search query)))


(def q query-search)
