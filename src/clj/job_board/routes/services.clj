(ns job-board.routes.services
  (:require
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.coercion :as coercion]
   [reitit.coercion.spec :as spec-coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [job-board.middleware.formats :as formats]
   [ring.util.http-response :as response]
   [job-board.application.job-management :as app]))

(defn jobs-get [_]
  (response/ok (app/list-all-jobs)))

(defn jobs-post [{{params :body} :parameters}]
  (try
    (let [count (app/add-job! params)]
      (response/ok {:status :ok
                    :created count}))
    (catch Exception e
      (let [{id     :error-id
             errors :errors} (ex-data e)]
        (case id
          :validation
          (response/bad-request {:errors errors})
          (response/internal-server-error
           {:errors
            {:server-error ["Failed to save job!"]}}))))))

(defn jobs-delete [input]
  (try
    (let [{{{:keys [id]} :path} :parameters} input
          id (app/delete-job! id)]
      (response/ok {:status :ok
                    :deleted id}))
    (catch Exception e
      (let [{id     :error-id
             errors :errors} (ex-data e)]
        (case id
          :validation
          (response/bad-request {:input input
                                 :errors errors})
          :illegal-argument
          (response/bad-request {:input input
                                 :errors errors})
          (response/internal-server-error
           {:errors
            {:server-error ["Failed to delete job!"]}}))))))

(defn service-routes []
   ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 coercion/coerce-exceptions-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "job-board"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
            {:url "/api/swagger.json"
             :config {:validator-url nil}})}]]

   ["/jobs"
    {:get {:summary "Returns a map of open positions in the job board."
           :responses {200 {:body [{:uuid string?
                                    :company string?
                                    :title string?
                                    :description string?}]}}
           :handler jobs-get}
     :post {:summary "Inserts a new open position in the job board and returns the updated map."
            :parameters {:body {:uuid string?
                                :company string?
                                :title string?
                                :description string?}}
            :responses {200 {:body map?}
                        400 {:body map?}
                        500 {:errors map?}}
            :handler jobs-post}
     }]
     ["/jobs/:id"
      {:delete {:summary "Removes an open position from the job board and returns the updated map"
                :parameters {:path {:id string?}}
                :responses {200 {:body map?}
                            400 {:body map?}
                            500 {:errors map?}}
                :handler jobs-delete}}]])
