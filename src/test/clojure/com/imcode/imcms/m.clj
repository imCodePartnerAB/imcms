(ns com.imcode.imcms.m)

(use 'clojure.contrib.monads)


(defn ff [] (domonad identity-m
  [a  1
   b  (inc a)]
  (* a b)))


(defn >>= [x f]
  (if (= x :nothing)
    (do (println "<fall>") :nothing)
    (f x)))

(defn =<< [f x]
  (if (= x :nothing)
    (do (println "<fall>") :nothing)
    (f x)))

(defn f [x]
  (println "f " x)
  (if (< x 10)
    (inc x)
    nil))

(defn t [n]
  (>>= n     (fn [a]
    (>>= (f a) (fn [b]
      (>>= (f b) (fn [c]
        (>>= (f c) (fn [d] (+ a b c d))))))))))


(defn t2 [n]
  (=<< f (=<< f (=<< f (=<< f n)))))

(defn t3 [n]
  (>>= (>>= (>>= (>>= n f) f) f) f))

(def *args-count* 2)

(defmacro liftm* [f]
  `(let [c# (eval *args-count*)]
     (println "c=" c)
     (with-monad maybe-m (m-lift c# ~f))))
 
(defn liftf* [f]
  (fn [& args]
    (binding [*args-count* (count args)]
      (let [lifted-f (liftm* f)]
        (lifted-f args)))))