(ns com.imcode.imcms.release-utils)
  ;#^{:doc ""})

;;; Release name regular expression.
;;; All automatically generated releases names match this format.
;;;
;;; Examples of release tags:
;;;   "3.2.1", "4.0.1-beta1", "6.0.0-alpha15"
;;; When matched against this re the following vectors are created:
;;;   ["3.2.1" "3" "2" "1" nil nil], ["4.0.1-beta1" "4" "0" "1" "beta" "1"], ["6.0.0-alpha15" "6" "0" "0" "alpha" "15"]
(def release-re #"(\d+)\.(\d+)\.(\d)(?:-(alpha|beta)(\d+))?")


;;; Release structure.
;;; A release can be 'pre' of 'final'.
;;; In case of final release build-name and build-no are set to nil.
;;; In case of pre-release build name is either 'alpha' or 'beta' and build-no is a pre-release sequence number.
(defstruct release-struct :major-no :minor-no :revision-no :build-name :build-no)


(defn final-release?
  "Return if a given release struct represents final release."
  [release]
  (nil? (:build-name release)))


(defn pre-release?
  "Return if a given release struct represents pre release."
  [release]
  (not (final-release? release)))


(defn release-name
  "Builds and returns release name from s release struct fields."
  [{:keys [major-no minor-no revision-no build-name build-no]}]
  (let [name (format "%d.%d.%d" major-no minor-no revision-no)]
    (if-not build-name
      name
      (str name (format "-%s%d" build-name build-no)))))


;;; release-struct comparator
;;; Please not that releases without build name are greater that releases with build name.
;;; Thus when applied then the (ascending) sort order will be following:
;;;   "3.2.1-alphaN", ..., "3.2.1-betaN", ..., "3.2.1", "4.0.0-alphaN", ..., "4.0.0"
(def release-comparator
  (proxy [java.util.Comparator] []
    (compare [r1 r2]
      (let [cmp-version-seq (map #(.compareTo (r1 %) (r2 %)) [:major-no :minor-no :revision-no])]
        ;;looking for the first non-zero value
        (if-let [cmp-version-res (first (drop-while zero? cmp-version-seq))]
          cmp-version-res
          ;;release with build-name = nil is greater
          (let [build-name-1 (:build-name r1)
                build-name-2 (:build-name r2)
                cmp-build-name-res (cond
                                     (nil? build-name-1) 1
                                     (nil? build-name-2) -1
                                     :else (.compareTo build-name-1 build-name-2))]
            (if-not (zero? cmp-build-name-res)
              cmp-build-name-res
              (.compareTo (:build-no r1) (:build-no r2)))))))))


(defn sort-releases
  "Sorts releases using release-struct comparator."
  [releases]
  (sort release-comparator releases))


(defn create-release
  "Creates release struct from release values."
  ([major-no minor-no revision-no]
    (create-release major-no minor-no revision-no nil nil))

  ([major-no minor-no revision-no build-name build-no]
  (let [release (struct release-struct major-no minor-no revision-no build-name build-no)]
    (reduce
      (fn to-int [map key]
        (if-let [val (get map key)]
          (assoc map key (Integer/valueOf val))
          map))
      release
      [:major-no, :minor-no, :revision-no, :build-no]))))


(defn create-releases-from-names
  "Creates releases from their names.
  Takes a sequence of releases names and returns a sequence of releases structs.
  Names which are not conform to relase name format are skipped."
  [names]
  (for [release-vals (map #(re-matches release-re %) names) :when release-vals]
    ;; the first val in release-vals is a name itself, so take the rest 
    (apply create-release (rest release-vals))))


(defn select-releases
  "Selects and returns releases with specified major-no(s) from releases seq."
  [releases major-no & major-nos]
  (let [major-nos-set (set (cons major-no major-nos))]
    (filter #(major-nos-set (:major-no %)) releases)))


(defn last-final-release
  "Returns last final release from releases seq."
  [releases]
  (last (filter final-release? (sort-releases releases))))


(defn last-release
  "Returns last (pre of final) release from releases seq."
  [releases]
  (last (sort-releases releases)))


(defn- inc-release-field [release field]
  (assoc release field (inc (release field))))

(defn- inc-revision-no [release]
  (inc-release-field release :revision-no))

(defn- inc-build-no [release]
  (inc-release-field release :build-no))

(defn- set-build-data-to-nil [release]
  (assoc release :build-name nil, :build-no nil))


(defn next-final-release
  "Creates next final release based on provided major-no."
  [releases major-no]
  (if-let [release (last-release (select-releases releases major-no))]
    (if (final-release? release)
      (inc-revision-no release)
      (set-build-data-to-nil release))

    (create-release major-no, 0, 0)))


(defn next-pre-release
  "Creates next pre-release based on provided major-no."
  [releases major-no]
  (if-let [release (last-release (select-releases releases major-no))]
    (if (pre-release? release)
      (inc-build-no release)
      (merge (inc-revision-no release) {:build-name "alpha", :build-no 1}))

    (create-release major-no, 0, 0, "alpha", 1)))


;;; tests
(def *TEST-RELEASES-NAMES*
  ["4.0.1" "RB-5-ImageArchive-pre" "2.0.0" "4.0.0" "4.0.1-beta1" "4.0.2-alpha1" "4.0.1-alpha99" "2a.1.0-beta1" "RB-4-ImageArchive-image-handling-merge-pre" "2a.1.0-beta2" "3.2.0" "1.6.0" "2a.0.0" "rb4-image-handling-merge-pre" "4.0.0-beta29-img-beta10" "4.0.0-beta29-img-beta11" "4.0.0-beta29-img-beta12" "4.0.0-beta29-img-beta13" "4.0.0-alpha10" "4.0.0-alpha11" "6.0.0-alpha10" "4.0.0-alpha12" "6.0.0-alpha11" "4.0.0-alpha13" "4.0.0-beta1" "6.0.0-alpha12" "4.0.0-alpha14" "4.0.0-beta2" "6.0.0-alpha13" "4.0.0-alpha15" "4.0.0-beta3" "6.0.0-alpha14" "4.0.0-alpha16" "4.0.0-beta4" "6.0.0-alpha15" "4.0.0-alpha17" "4.0.0-beta5" "6.0.0-alpha16" "4.0.0-alpha18" "4.0.0-beta6" "6.0.0-alpha17" "4.0.0-alpha19" "4.0.0-beta7" "6.0.0-alpha18" "4.0.0-beta8" "3.2.0-beta10" "6.0.0-alpha19" "4.0.0-beta9" "3.2.0-beta11" "3.2.0-beta12" "3.2.0-beta13" "5.0.0-alpha1" "5.0.0-alpha2" "3.1.0" "5.0.0-alpha3" "3.1.1" "5.0.0-alpha4" "1.5.0" "3.1.2" "5.0.0-alpha5" "5.0.0-alpha6" "5.0.0-alpha7" "5.0.0-alpha8" "1.9.0" "5.0.0-alpha9" "1.9.1" "1.9.2" "1.9.3" "4.0.0-alpha20" "4.0.0-alpha21" "4.0.0-alpha22" "6.0.0-alpha20" "4.0.0-alpha23" "6.0.0-alpha21" "4.0.0-alpha24" "6.0.0-alpha22" "4.0.0-alpha25" "6.0.0-alpha23" "4.0.0-alpha26" "6.0.0-alpha24" "4.0.0-alpha27" "6.0.0-alpha25" "4.0.0-alpha28" "6.0.0-alpha26" "4.0.0-alpha29" "6.0.0-alpha27" "6.0.0-alpha28" "6.0.0-alpha29" "4.0.0-beta10" "4.0.0-beta11" "4.0.0-beta12" "4.0.0-beta13" "4.0.0-beta14" "4.0.0-beta15" "4.0.0-beta16" "4.0.0-beta17" "4.0.0-beta18" "4.0.0-beta19" "4.0.0-alpha30" "4.0.0-alpha31" "4.0.0-alpha32" "6.0.0-alpha30" "4.0.0-alpha33" "6.0.0-alpha31" "4.0.0-alpha34" "6.0.0-alpha32" "4.0.0-alpha35" "6.0.0-alpha33" "4.0.0-alpha36" "6.0.0-alpha34" "3.2.0-beta1" "4.0.0-alpha37" "6.0.0-alpha35" "3.2.0-beta2" "4.0.0-alpha38" "6.0.0-alpha36" "5.0.0-versioning-frozen" "3.2.0-beta3" "4.0.0-alpha39" "6.0.0-alpha37" "2.2.3-beta2" "3.2.0-beta4" "2.2.3-beta3" "3.2.0-beta5" "3.2.0-beta6" "4.0.0-beta20" "3.2.0-beta7" "4.0.0-beta21" "3.2.0-beta8" "4.0.0-beta22" "3.2.0-beta9" "1.3" "4.0.0-beta23" "3.0.0" "4.0.0-beta24" "3.0.1" "2.2.0" "4.0.0-beta25" "2.2.1" "4.0.0-beta26" "2.2.2" "4.0.0-beta27" "4.0.0-beta28" "4.0.0-beta29" "1.8.0" "1.8.1" "1.8.2" "1.8.3" "1.8.4" "4.0.0-alpha40" "4.0.0-alpha41" "4.0.0-alpha42" "4.0.0-alpha43" "4.0.0-alpha44" "4.0.0-alpha45" "4.0.0-alpha46" "4.0.0-alpha47" "5.0.0-test3" "4.0.0-alpha48" "5.0.0-test4" "4.0.0-alpha49" "1.11.0" "5.0.0-test5" "1.11.1" "5.0.0-test6" "5.0.0-test7" "5.0.0-test8" "4.0.0-beta30" "new_ui_v1__RB-4-alpha-1" "5.0.0-test9" "4.0.0-beta31" "issue_9013_pre" "4.0.0-beta32" "4.0.0-beta33" "6.0.0-alpha1" "4.0.0-beta34" "6.0.0-alpha2" "4.0.0-beta35" "6.0.0-alpha3" "4.0.0-beta36" "6.0.0-alpha4" "4.0.0-beta37" "4.0.0-beta38" "4.0.0-beta39" "6.0.0-alpha7" "6.0.0-alpha8" "6.0.0-alpha9" "4.0.0-alpha50" "4.0.0-alpha51" "4.0.0-alpha52" "4.0.0-alpha53" "4.0.0-beta29-img-beta2" "4.0.0-alpha54" "4.0.0-beta29-img-beta3" "5.0.0-alpha10" "4.0.0-alpha55" "4.0.0-beta29-img-beta4" "5.0.0-alpha11" "4.0.0-beta29-img-beta5" "5.0.0-alpha12" "4.0.0-beta29-img-beta6" "5.0.0-alpha13" "4.0.0-beta29-img-beta7" "5.0.0-alpha14" "4.0.0-beta29-img-beta8" "5.0.0-alpha15" "4.0.0-beta29-img-beta9" "5.0.0-alpha16" "5.0.0-alpha17" "5.0.0-alpha18" "4.0.0_new_ui_v1-alpha2" "4.0.0-beta40" "5.0.0-alpha19" "4.0.0-beta41" "2.1.0" "2.1.1" "1.3.3" "1.7.0" "1.7.1" "1.7.2" "1.7.3" "1.7.4" "1.7.5" "5.0.0-alpha20" "5.0.0-alpha21" "5.0.0-alpha22" "5.0.0-alpha23" "6.0.0-apha35" "1.10.0" "1.10.1" "1.10.2" "1.10.3" "5.0.0-test10" "5.0.0-test11" "5.0.0-test12" "5.0.0-test13" "4.0.0-alpha1" "5.0.0-test14" "4.0.0-alpha2" "5.0.0-test15" "4.0.0-alpha3" "4.0.0-alpha4" "4.0.0-alpha5" "4.0.0-alpha6" "4.0.0-alpha7" "4.0.0-alpha8" "4.0.0-alpha9"])

(defn- test-releases []
  (create-releases-from-names *TEST-RELEASES-NAMES*))
