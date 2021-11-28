(ns job-board.application.job-management
  (:require
   [job-board.domain.job-repository :as job-repository]
   [job-board.domain.job :as job]))

(defn list-all-jobs []
  (job-repository/find-all))

(defn add-job! [job]
  (if-let [errors (job/validate job)]
    (throw (ex-info "job is invalid"
                    {:error-id :validation
                     :errors errors}))
    (do (job-repository/create! job)
        1)))

(defn delete-job! [job-id]
  (if-let [errors (job/validate-id job-id)]
    (throw (ex-info "job-id is invalid"
                    {:error-id :validation
                     :errors errors}))
    (do (job-repository/delete! job-id)
        job-id)))