(ns hostel.sites
  (:require
    [clojure.java.io :as io]
    [clojure.java.shell :as cjs]
    [clojure.string :as cs]
    [hostel.shell :as fs]
    [cheshire.core :as json])
  (:import [java.net.URL]))

(defn read-json [file]
 (json/parse-string (slurp file) keyword))

(defn sites [acc file]
  (let [plug (read-json file)]
    (assoc acc (:name plug) (merge plug {:dir (.getName (.getParentFile file))}))))

(def base-path (.getPath (io/resource "public")))

(defn site-path [nm]
  (-> (str base-path "/" nm)
      (io/file)
      (.getPath)))

(defn read-site [nm]
  (let [manifest-file (-> (site-path nm) (str "/fhir.json") (io/file))]
    (->
      (if (.exists manifest-file)
        (try
          (read-json  manifest-file)
          (catch Exception e
            {:name nm :title nm :description (str "Error while reading manifest " e)}))
        {:name nm :title nm :description "site without manifest"})
      (merge {:url (str "/" nm "/index.html")}))))

(defn read-sites []
  (->> (io/resource "public")
       (io/file)
       (.listFiles)
       (filter #(.isDirectory %))
       (map #(.getName  %))
       (map #(read-site %))))

(defn url [s] (java.net.URL. s))

(defn rm [nm]
  (let [site-path (site-path nm)
        cmd (fs/shell [:rm :-rf site-path])]
    (println cmd)
    (cjs/sh "bash" "-c" cmd)))

;; TODO support for zip
(defn upload [nm tmpfile]
  (let [tar-path (.getPath tmpfile)
        site-path (site-path nm)
        cmd (fs/shell
              [:and
               [:rm :-rf site-path]
               [:mkdir :-p site-path]
               [:cd site-path]
               [:tar :-xzf tar-path]
               [:ls :-lah]])]
    (println cmd)
    (cjs/sh "bash" "-c" cmd)))

(comment
  (read-sites)
  (read-site "test")

  (def tmpfile (io/file "/home/devel/fhirplace-empty-site/arch.tar.gz"))
  (rm "test")
  (println (upload "test" tmpfile)))
