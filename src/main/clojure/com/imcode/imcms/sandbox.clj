(ns com.imcode.imcms.sandbox)

(defmulti area :type)

(defmethod area :circle [{r :r}] (* r 3.14))
(defmethod area :square [{n :n}] (* n n))
(defmethod area :box [{x :x, y :y :or {:x 1, :y 2}}] (* x y))