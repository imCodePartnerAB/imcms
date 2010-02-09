(ns com.imcode.imcms.release
  #^{:doc ""}
  (:require
    [com.imcode.imcms
      [svn-utils :as svn]
      [release-utils :as rel]]))


(defonce *SVN-URL-IMCMS-TAGS* "https://repo.imcode.com/imcode/imcms/tags")


(defn get-imcms-tags-names [username password]
  "Returns imcms tags names."
  (let [repo (svn/login *SVN-URL-IMCMS-TAGS* username password)
        releases-entries (filter svn/directory? (svn/dir repo ""))]
    (map #(.getName %) releases-entries)))


;;; TODO:
;;; Next release name generation/check
;;;   --final --v A     -> A.B.(inc revision-no) - generates
;;;   --final --v A.B.C -> A.B.C                 - checks

;;;   --pre --v A                   -> A.B.C-[alpha|beta](inc build-no)
;;;   --pre --v A.B.C-[alpha|beta]D -> A.B.C-[alpha|beta]D

(defn print-usage-and-exit
  "Prints usage and exit with code 1."
  []
  (println)
  (println "Proposes next imCMS release name based on provided major version no.")
  (println "USAGE:")
  (println "  -u `svn login` -p `svn password` -t `release type` -v `major version no`")
  (println "  Where `release type` is 'pre' for pre-relase (alpha or beta) or 'final' for final release.")
  (println "  Examples:" )
  (println "      -u user -p password -r pre -v 4")
  (println "      -u user -p password -t final -v 6")

  (System/exit 1))


(defn- valid-major-version-no? [s]
  (try
    (pos? (Integer/valueOf s))
    (catch Exception e
      false)))

(defn- valid-release-type? [s]
  (#{"pre" "final"} s))


(defmacro p [& args] (let [str-args (map str args)] `(mapcat list ["-u" "-p" "-t" "-v"] [~@str-args])))

(defn main [& args]
  (prn args)
  (when-not (== 8 (count args))
    (print-usage-and-exit))

  (let [args-map (apply assoc {} args)]
    (when-not (and
                (== 4 (count (select-keys args-map ["-u" "-p" "-t" "-v"])))
                (valid-major-version-no? (args-map "-v"))
                (valid-release-type? (args-map "-t")))
      (print-usage-and-exit))

    (let [tags-names (get-imcms-tags-names (args-map "-u") (args-map "-p"))
          releases (rel/create-releases-from-names tags-names)
          major-no (Integer/valueOf (args-map "-v"))
          release-type (args-map "-t")
          next-release-fn (if (= release-type "pre") rel/next-pre-release rel/next-final-release)]
      (rel/release-name (next-release-fn releases major-no)))))




(defmulti m x (fn [x & xs] (class? x ) x)


