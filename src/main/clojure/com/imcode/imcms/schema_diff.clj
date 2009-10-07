(ns com.imcode.imcms.schema-diff
  (:use
    [clojure.xml :only (parse)])
  (:import
    (org.xml.sax InputSource)
    (javax.xml.xpath XPath XPathFactory XPathConstants)))


(def xpath (.. XPathFactory newInstance newXPath))


(defn- get-nodes
  "Evaluate an XPath expression in the context of the specified InputSource and return nodes seq."
  [expression input-source]
  (let [nodes-list (.evaluate xpath expression input-source XPathConstants/NODESET)
        nodes-count (.getLength nodes-list)]
    (loop [i 0, nodes []]
      (if (= i nodes-count)
        nodes
        (recur (inc i) (conj nodes (.item nodes-list i)))))))


(defn- get-nodes-values
  "Evaluates an XPath expression in the context of the specified InputSource and return nodes values seq."
  [expression input-source]
  (let [nodes (get-nodes expression input-source)]
    (map #(.getNodeValue %) nodes)))


(defn get-diff-versions
  "Returns all diff versions found in input source."
  [input-source]
  (get-nodes-values "/schema-upgrade/diff/@version" input-source))


(defn get-diff-bundle
  "Returns upgrade bundle as a pair (vector) where first item is a version number string
   and second is a collection of script filenames.
   Ex: [\"4.10\" [script1.sql script2.sql scriptN.sql]]."
  [input-source version vendor-name]
  (let [expression-template "/schema-upgrade/diff[@version = %s]/vendor[@name='%s']/script/@location"
        expression (format expression-template version vendor-name)]
    
    (get-nodes-values expression input-source)))


(defn get-diff-bundles [conf-file-path vendor-name]
  (let [input-source (InputSource. conf-file-path)
        versions (get-diff-versions input-source)]

    (for [version versions]
      [version (get-diff-bundle input-source version vendor-name)])))