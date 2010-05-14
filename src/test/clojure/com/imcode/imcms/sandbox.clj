(ns
  #^{:doc "Sandbox."}
  com.imcode.imcms.sandbox  
  (:require
    [clojure.contrib.duck-streams :as ds]
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]
    
    [com.imcode.imcms.project :as p]
    [com.imcode.imcms.lucene :as l]))


;(run-tests
;  'com.imcode.imcms.schema.DiffBuilderTest
;  'com.imcode.imcms.schema.SchemaUpgradeTest)


;(deftest test-get-versions-numbers
;  (is (= (DiffBuilder/getVersionsNumbers (schema/slurp-xml-conf))  [5.0 5.1 5.2 5.3 6.0 6.1 6.2])))
