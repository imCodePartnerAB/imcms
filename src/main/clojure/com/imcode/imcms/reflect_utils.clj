(ns com.imcode.imcms.reflect-utils)

(defn- get-read-property
  "Returns read property from a propery desciptor as a [:name, method] pair or null
  if desciptor does not have a read method."
  [#^java.beans.PropertyDescriptor pd]
  (when-let [method (.getReadMethod pd)]
    (when empty? (.getParameterTypes method)
      (let [name (keyword (.getName pd))]
        [name method]))))


(defn- get-read-properties
  "Returns a collection of an object's read properties."
  [#^Object obj]
  (let [beanInfo (java.beans.Introspector/getBeanInfo (.getClass obj))
        propertyDescriptors (.getPropertyDescriptors beanInfo)]

    (remove nil? (map get-read-property propertyDescriptors))))


(defn- dump-properties?
  "Returns if object's properties can be dumped."
  [#^Object obj]
  (when-not (some #(% obj) [nil? class? coll? keyword? number? string?])
    (let [clazz (class obj)]
      (not (or
        (.isPrimitive clazz)
        (and
          (some #(instance? % obj) [Enum Boolean CharSequence Character Exception java.util.Date])
          ;; excludes
          (not-any? #(instance? % obj) [java.util.Set])))))))


(defn dump
  "Returns object's dump based on its JavaBean read properties."
  [#^Object obj]
  (if-not (dump-properties? obj)
    obj
    (cond
      (.. obj getClass isArray)            (into [] (map dump obj))
      (instance? java.util.Set obj)        (set (map dump obj))
      (instance? java.util.List obj)       (into [] (map dump obj))
      (instance? java.util.Collection obj) (map dump obj)
      (instance? java.util.Map obj)        (into {} (for [[k v] obj] [k, (dump v)]))

      :else
        (let [read-properties (get-read-properties obj)
              assoc-property-value (fn
                                     ;"Invokes a property method on a obj and assocs name with returned value into obj-map.
                                     ; In case of an invocation exception assocs name with that exception."
                                     [obj-map [name method]]
                                     (let [value-obj (try
                                                       (.invoke method obj nil)
                                                       (catch Exception e e))]

                                       (assoc obj-map name (dump value-obj))))]

          (reduce assoc-property-value {} read-properties)))))
