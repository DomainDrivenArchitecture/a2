(ns job-board.handler-test
  (:require
    [clojure.test :refer :all]
    [ring.mock.request :refer :all]
    [job-board.handler :refer :all]
    [job-board.middleware.formats :as formats]
    [muuntaja.core :as m]
    [mount.core :as mount]))

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'job-board.config/env
                 #'job-board.handler/app-routes)
    (f)))

(deftest test-web
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response))))))

(deftest test-api
  (testing "add job"
    (let [response ((app) (-> (request :post "/jobs")
                                (json-body {:uuid "it-5"
                                            :company "inttest"
                                            :title "inttest"
                                            :description "very long description"})))]
        (is (= 200 (:status response)))))
  (testing "one job should be there"
    (let [response ((app) (-> (request :get "/jobs")))]
      (is (= 200 (:status response)))
      (is (= 1 (count (m/decode-response-body response))))))
  (testing "one job should be there"
    (let [response ((app) (-> (request :delete "/jobs/it-5")))]
      (is (= 200 (:status response)))))
  (testing "one job should be there"
    (let [response ((app) (-> (request :get "/jobs")))]
      (is (= 200 (:status response)))
      (is (= 0 (count (m/decode-response-body response))))))
)
