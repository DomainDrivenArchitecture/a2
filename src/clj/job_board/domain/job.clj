(ns job-board.domain.job
  (:require
    [struct.core :as st]))

(def job-id-schema
  [[:uuid st/required st/string]])

(def job-schema
  (into
   job-id-schema
   [[:company st/required st/string]
    [:title st/required st/string]
    [:description st/required st/string
     {:message "message must contain at least 10 characters"
      :validate (fn [msg] (>= (count msg) 10))}]]))

(defn validate-id [id]
  (first (st/validate {:uuid id} job-id-schema)))

(defn validate [job]
  (first (st/validate job job-schema)))
