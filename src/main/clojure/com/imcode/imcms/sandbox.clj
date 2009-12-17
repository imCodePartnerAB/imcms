(ns com.imcode.imcms.sandbox)

(defn f [xs]
  (loop [xs xs, evens [], odds []]
    (let [x (first xs)]
      (if-not x
        [evens, odds]
        (if (even? x)
          (recur (rest xs) (conj evens x) odds)
          (recur (rest xs) evens (conj odds x)))))))