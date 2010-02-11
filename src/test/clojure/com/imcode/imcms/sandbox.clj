(ns
  #^{:doc "Sandbox."}
  com.imcode.imcms.sandbox  
  (:require
    [clojure.contrib.duck-streams :as ds]
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]))


(letfn [(local-even? [n]
          (if (zero? n) true #(local-odd? (dec n))))

        (local-odd? [n]
          (if (zero? n) false #(local-even? (dec n))))]

  (defn letfn-test [n]
    {:pre [(>= n 0)]}
    (if (trampoline local-even? n) :even :odd)))
