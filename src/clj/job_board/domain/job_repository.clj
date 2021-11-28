(ns job-board.domain.job-repository
  (:require
   [job-board.config :refer [env]]
    [job-board.domain.job :as job]))

(def job-collection :job-collection)

(defn job-collection [] 
  (get job-collection env))

(defn create! [job]
    (swap! (job-collection)
           assoc (keyword (:uuid job)) job))

(defn delete! [id]
    (swap! (job-collection)
           (fn [collection]
             (let [k (keyword id)]
               (if (contains? collection k)
                 (dissoc collection k)
                 (throw (ex-info "id does not exist"
                                 {:error-id :illegal-argument
                                  :id id})))))))

(defn find-all []
  (vals @(job-collection)))

(defn init! []
  (set-validator! (job-collection) job/validate)
  (create! {:uuid "uuid"
            :company "my-company"
            :title "title"
            :description "description"}))