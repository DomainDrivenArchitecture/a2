(ns job-board.routes.home
  (:require
   [job-board.layout :as layout]
   [clojure.java.io :as io]
   [job-board.middleware :as middleware]
   [ring.util.response]
   [job-board.application.job-management :as app]))

(defn home-page [{:keys [flash] :as request}]
  (layout/render 
   request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)
                        :jobs (app/list-all-jobs)}))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]])

