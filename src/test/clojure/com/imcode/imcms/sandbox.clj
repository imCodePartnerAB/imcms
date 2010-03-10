(ns
  #^{:doc "Sandbox."}
  com.imcode.imcms.sandbox  
  (:require
    [clojure.contrib.duck-streams :as ds]
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]
    [com.imcode.imcms.project :as p]
    [com.imcode.imcms.lucene :as l]
    [com.imcode.imcms.file-utils :as f]))

;
;(letfn [(local-even? [n]
;          (if (zero? n) true #(local-odd? (dec n))))
;
;        (local-odd? [n]
;          (if (zero? n) false #(local-even? (dec n))))]
;
;  (defn letfn-test [n]
;    {:pre [(>= n 0)]}
;    (if (trampoline local-even? n) :even :odd)))


(defn setup []
  (def clj-test-files (f/files "src/main" #"\.clj$"))
  (def clj-main-files (f/files "src/test" #"\.clj$"))
  
  ;(def project-files (f/files "src" #"\.(java|jsp|htm|html|xml|properties|sql|clj)$"))
  (def project-files (f/files "src" #"\.(java|jsp|htm|html|xml|properties|clj)$"))
  (def all-files (f/files "." #"\.(java|jsp|htm|html|xml|properties|sql|clj)$"))

  (def dbdir (l/new-dbdir (p/create-db-datasource) "lucene"))

  (require '[com.imcode.imcms.lucene :as l])
  (require '[com.imcode.imcms.file-utils :as f])

  (def r1 (l/index (l/new-ramdir) clj-test-files))

  (def r2 (l/index (l/new-ramdir) clj-main-files))

  (import 'org.apache.lucene.store.Directory)
  (import 'org.apache.lucene.index.IndexReader)
  (import 'org.apache.lucene.index.IndexWriter)
  (import 'org.apache.lucene.analysis.SimpleAnalyzer)


  nil)

(use 'com.imcode.imcms.spring-utils)
(import '(com.imcode.imcms.api Content ContentLoop))

(defn s []

  (defbean p/spring-app-context dao contentLoopDao)
  (def l (ContentLoop.))
  (-> l .getContents (.add (Content.)))
  (doto (.getFirst (.getContents l)) (.setNo 0))
  (doto (.getFirst (.getContents l)) (.setOrderNo 0))

  (.setNo l 1)
  (.setDocId l 1001)
  (.setDocVersionNo l 0)

  )

;(def files (f/files "src" #"\.clj$"))

;(def dbdir (l/new-dbdir (p/create-db-datasource) "lucene"))