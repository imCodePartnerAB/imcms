(ns lucene
  (:import
    (java.io File)
    (org.apache.lucene.store Directory FSDirectory RAMDirectory)
    (org.apache.lucene.document Document Field Field$Store Field$Index DateTools DateTools$Resolution)
    (org.apache.lucene.index IndexReader IndexWriter Term)
    (org.apache.lucene.analysis SimpleAnalyzer)
    (org.apache.lucene.queryParser QueryParser MultiFieldQueryParser)
    (org.apache.lucene.search IndexSearcher TermQuery Hits BooleanClause BooleanClause$Occur Sort))

  (:use
    [clojure.contrib.duck-streams :only (slurp*)]))


(defn new-ramdir []
  (RAMDirectory.))


(defn new-fsdir [path]
  (FSDirectory/getDirectory path))


(defn- create-document [name, path, content, last-modified]
  (doto (Document.)
    (.add (Field. "name" name Field$Store/YES Field$Index/ANALYZED))
    (.add (Field. "path" path Field$Store/YES Field$Index/ANALYZED))
    (.add (Field. "content" content Field$Store/NO Field$Index/ANALYZED))
    (.add (Field. "last-modified" last-modified Field$Store/YES Field$Index/UN_TOKENIZED))))


(defn index
  "Recursively indexes files matches regex under path to the provided lucene dir."
  [#^Directory dir, files]
  (with-open [indexWriter (IndexWriter. dir (SimpleAnalyzer.))]
    (doseq [file files]

      (println "Indexing " (.getCanonicalPath file))
      
      (let [doc (create-document
                  (.getName file)
                  (.getCanonicalPath file)
                  (slurp* file)
                  (DateTools/timeToString (.lastModified file) DateTools$Resolution/MILLISECOND))]
        (doto indexWriter
          (.addDocument doc)
          ;(.commit)
          ))))
  dir)


(defn- search
  "Returns hits"
  [#^Directory dir, query]
  (let [searcher (IndexSearcher. dir)
        hits (.search searcher query (Sort. "last-modified" true))
        docs (iterator-seq (.iterator hits))]
    (for [doc docs]
      (map #(.get doc %) ["name" "path" "last-modified"]))))


(defn term-search [#^Directory dir, value]
  (let [term (Term. "content" value)
        query (TermQuery. term)]
    (search dir query)))


(defn query-search [#^Directory dir, query-str]
  (let [fields (into-array ["name" "content"])
        flags (into-array [BooleanClause$Occur/SHOULD BooleanClause$Occur/SHOULD])
        query (MultiFieldQueryParser/parse query-str fields flags (SimpleAnalyzer.))]
    (search dir query)))


(def q query-search)