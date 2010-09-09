(ns 
  #^{:doc ""}
  com.imcode.imcms.misc)

(defn to-keyword-key-map
  "Converts keys of a map into keywords."
  [a-map]
  (into {}
    (for [[k v] a-map] [(keyword k) v])))


(defmulti dump class)


(defmethod dump java.util.Map [map]
  (doseq [key  (sort (keys map))]
    (println key " -> " (get map key))))  


(defmethod dump Object [o] 
  (dump (bean o)))


(defmethod dump nil [_] 
  (println nil))

for ((k, v) <- map) yeld Symbol(k) -> v