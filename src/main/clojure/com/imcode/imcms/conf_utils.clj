(ns
  #^{:doc "Configuration is a plain clojure map stored into a file."} 
  com.imcode.imcms.conf-utils

  (:use
    clojure.test    
    [clojure.walk :only (postwalk)]))  


;;;;
;;;; Pure fns.
;;;;

(defn rewrite-expression
  "Replaces parameters placeholders - ${xxx} in a expression with their values.
   Args:
     expression - a string which possibly contains parameters placeholders.
     params - parameters map in the form of :name -> value.

   Throws an exception if an expression contains an unknown parameter."
  ; (re-find #"\$\{([\w\.-]+?)\}" "/aaa/${xxx.yyy.zzz}/bbb") => ["${xxx.yyy.zzz}" "xxx.yyy.zzz"]
  [#^String expression, params]
  (if-let [[clause, param-name] (re-find #"\$\{([\w\.-]+?)\}" expression)]
    (if-let [param-value (get params (keyword param-name))]
      (recur (.replace expression clause param-value) params)
      (throw (RuntimeException. (format  "Can not rewrite expression %s. Parameter %s does not exists in params %s.",
                                         expression, param-name, params))))
    expression))

                      
(defn rewrite-params
  "Replaces params placeholders - ${xxx} in a conf with their values."
  [conf]
  (postwalk #(if (string? %) (rewrite-expression % conf) %)
            conf))


;;;;
;;;; Side effect fns.
;;;;

(defn read-conf
  "Reads and returns conf map from a conf file."
  [conf-file-path]
  (read-string (slurp conf-file-path)))


(defn create-conf
  "Args
    conf-file-path configuration file path.
    params - additional and/or overloaded parameters merged with a conf."
  [conf-file-path params]
  (let [conf (read-conf conf-file-path)]
    (rewrite-params (merge conf params))))