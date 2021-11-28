(ns job-board.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [job-board.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[job-board started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[job-board has shut down successfully]=-"))
   :middleware wrap-dev})
