(ns com.imcode.imcms.file-utils
  (:use
    clojure.contrib.duck-streams
    [clojure.contrib.except :only (throw-if throw-if-not)])

  (:import
    (java.io File)
    (java.util Properties)))


(defn throw-if-not-file
  "Throws an exception if provided file does not exists or not a file."
  [#^File file]
  (throw-if-not (.isFile file)
    (format "File \"%s\" does not exists or not a file." (.getCanonicalPath file)))
  
  file)


(defn throw-if-not-dir
  "Throws an exception if provided dir does not exists or not a dir."
  [#^File dir]
  (throw-if-not (.isDirectory dir)
    (format "Directory \"%s\" does not exists or not a directory." (.getCanonicalPath dir)))

  dir)


(defn load-properties
  "Load properties from a file."
  [#^File file]
  (with-open [r (reader file)]
    (doto (Properties.) (.load r))))


(defn call-if-modified
  "Creates memoized function that is re-called is a file last modifed date has been changed.
   The file-fn is a function which takes a file as its argument."
  [get-file-fn, file-fn]
  (let [lock (Object.)
        state (atom {:file nil, :file-modified-dt nil, :file-fn-result nil})]

    (fn memoized-file-fn []
      (locking lock
        (let [file (get-file-fn)
              file-modified-dt (.lastModified file)]
          (when-not (and (= file (:file @state))
                         (= file-modified-dt (:file-modified-dt @state)))
            
            (reset! state {:file file
                           :file-modified-dt file-modified-dt
                           :file-fn-result (file-fn file)}))))

          (:file-fn-result @state))))