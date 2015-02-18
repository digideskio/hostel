(ns hostel.main
  (:require [hostel.core :as hc])
  (:gen-class))

(defn -main  [& args]
    (hc/start))
