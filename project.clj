(defproject hostel "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.9"]]
                   :plugins [[lein-kibit "0.0.8"]] }}
  :dependencies [[compojure "1.3.1"]
                 [hiccup "1.0.5"]
                 [http-kit "2.1.19"]
                 [cheshire "5.4.0"]
                 [im.chit/vinyasa "0.3.3"]
                 [ring/ring-core "1.3.2"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojure/clojure "1.6.0"]])
