(ns com.imcode.imcms.m)

(use 'clojure.contrib.monads)

(domonad identity-m
  [a  1
   b  (inc a)]
  (* a b))
