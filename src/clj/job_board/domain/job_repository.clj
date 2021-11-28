(ns job-board.domain.job-repository
  (:require
   [mount.core :as mount]
   [job-board.domain.job :as job]))

(def job-collection (atom {}))

(defn create! [job]
    (swap! job-collection
           assoc (keyword (:uuid job)) job))

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
        (vals @job-collection)))

(defn init! []
  ;;(set-validator! job-collection job/validate)
  (create! {:uuid "uuid"
            :company "my-company"
            :title "title"
            :description "very long description"}))

(mount/defstate ^{:on-reload :noop} job-repository
  :start (init!))