(ns job-board.domain.job-repository
  (:require
   [mount.core :as mount]
   [job-board.domain.job :as job]))

(def job-collection (atom {}))

(defn validate-job [job]
  (if-let [errors (job/validate job)]
    (throw (ex-info (str "job is invalid" job)
                    {:error-id :validation
                     :errors errors}))
    job))

(defn create! [job]
  (let [valid-job (validate-job job)]
    (swap! job-collection
           assoc (keyword (:uuid valid-job)) valid-job)))

(defn delete! [id]
  (swap! job-collection
         (fn [collection]
           (let [k (keyword id)]
             (if (contains? collection k)
               (dissoc collection k)
               (throw (ex-info "id does not exist"
                               {:error-id :illegal-argument
                                :id id})))))))

(defn find-all []
  (into []
        (->> (vals @job-collection)
             (map validate-job))))


(defn init! []
  (create! {:uuid "uuid"
            :company "my-company"
            :title "title"
            :description "very long description"})
  )

(mount/defstate ^{:on-reload :noop} job-repository
  :start (init!))