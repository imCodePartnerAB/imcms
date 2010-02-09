(ns com.imcode.imcms.sandbox
  #^{:doc "Sandbox."}
  (:require
    [clojure.contrib.str-utils :as su]
    [clojure.contrib.str-utils2 :as su2]
    [clojure.contrib.shell-out :as shell]))

(defn for_ [seq f] (mapcat f seq))

(for_ [1 2 3] (fn [n]
  (for_ [:a :b :c] (fn [x]
    (repeat n x)))))


;;; Tree walker
;(defn render [])
;
;(defn html [content]
;  (render [:html content]))
;
;
;(html
;  [:body {:bg-color "red"}
;    [:table {:border 1}
;      [:tr
;        [:th "First name"]
;        [:th "Snd name"]
;      ]
;      [:tr
;        [:td {:colspan 2} "SPACE"]
;      ]
;    ]
;  ])


(defn file-loc [file]
  (with-open [r (java.io.BufferedReader. (java.io.FileReader. file))]
    (count (for [l (line-seq r) :when (= (count l) 0)] l))))

(defn loc [path]
  (reduce +
    (for [f (file-seq (java.io.File. path)) :when (and
                                                    (.isFile f)
                                                    (-> f .getName (.endsWith ,, ".clj")))]
      (file-loc f))))