(ns hostel.core
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [org.httpkit.server :as ohs]
            [hiccup.core :as hc]
            [cheshire.core :as json]
            [hostel.sites :as hs]
            [compojure.route :as route]))

(defn index  [{params :params}]
  {:body  "Hello"
   :status 200})

(defn list-apps [req]
  {:body (-> (hs/read-sites)
             (json/generate-string))
   :status 200 })

(defn upload-app [{form :multipart-params :as req}]
  (let [tmpfile (get-in form ["file" :tempfile])
        plugin-name (get form "app")
        res (hs/upload plugin-name tmpfile)]
    (if (= (:exit res) 0)
      {:status 200
       :body(-> (hs/read-site plugin-name)
                (json/generate-string))}
      {:status 500
       :body (pr-str res)})))

(defn delete-app [{{nm :app} :params :as req}]
  (let [res (hs/rm nm)]
    (if (= (:exit res) 0)
      {:status 200
       :body (-> {:status "removed" :name nm :message (str "app [" nm "] successfully removed")}
                 (json/generate-string))}
      {:status 500
       :body (pr-str res)})))

(defn delete-app [req])


(defroutes app-routes
  (GET "/"  [] #'list-apps)
  (GET "/app"  [] #'list-apps)
  (POST "/app"  [] #'upload-app)
  (DELETE "/post/:app"  [] #'delete-app)
  (route/resources "/")
  (route/not-found "Not Found"))

(defn logger [h]
  (fn [req]
    (println "\n" (:request-method req) " " (:uri req) ": " req)
    (h req)))

(def app
  (handler/site
    (-> app-routes
        logger)))

(defn start  []
  (def stop
    (ohs/run-server #'app  {:port 8080}))
  (println "Server started on port 8080"))

(comment
  (require '[vinyasa.pull :as vp])
  (vp/pull 'cheshire)
  (start)
  (stop))
