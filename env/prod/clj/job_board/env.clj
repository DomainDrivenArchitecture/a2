(ns job-board.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[job-board started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[job-board has shut down successfully]=-"))
   :middleware identity})
