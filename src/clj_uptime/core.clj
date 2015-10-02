(ns clj-uptime.core
  (:require [cemerick.url :refer (url url-encode)]
            [clj-http.client :as client]
            [cheshire.core :as cheshire]))

(def base-api-uri (atom ""))

(defn checks-uri [] (str (url @base-api-uri (url-encode"checks"))))

(defn check-uri [id] (str (url @base-api-uri (url-encode"checks") (url-encode id))))

(defn setup-client [api-uri]
  (if (.endsWith api-uri "/api")
    (reset! base-api-uri api-uri)
    (throw (Exception. "You must call setup with a valid uri. Example: http://domain.com/api"))))

(defn get-checks []
  (cheshire/parse-string (:body (client/get (checks-uri)))))

(defn get-check-by-id [id]
  (cheshire/parse-string (:body (client/get (check-uri id)))))

(defn get-check [check]
  (let [id (get check "_id")]
    (get-check-by-id id)))

(defn delete-check-by-id [id]
  (cheshire/parse-string (:body (client/delete (check-uri id)))))

(defn delete-check [check]
  (let [id (get check "_id")]
    (delete-check-by-id id)))

(defn add-check
  ;; Add a check to uptime, please call with a hash-map with these keys
  ;; url : (required) Url of the check
  ;; name : (optional) Name of the check - if empty, url will be set as check name
  ;; interval : (optional) Interval of polling
  ;; maxTime : (optional) Slow threshold
  ;; isPaused : (optional) Status of polling
  ;; alertTreshold : (optional) set the threshold of failed pings that will create an alert
  ;; tags : (optional) list of tags (comma-separated values)
  ;; type : (optional) type of check (auto|http|https|udp)
  [check]
  (cheshire/parse-string (:body (client/put (checks-uri) {:content-type :json
                                                          :body (cheshire/generate-string check)}))))

(defn update-check
  ;; Update a check in uptime, please call with a hash-map with these keys
  ;; url : (required) Url of the check
  ;; name : (optional) Name of the check - if empty, url will be set as check name
  ;; interval : (optional) Interval of polling
  ;; maxTime : (optional) Slow threshold
  ;; isPaused : (optional) Status of polling
  ;; alertTreshold : (optional) set the threshold of failed pings that will create an alert
  ;; tags : (optional) list of tags (comma-separated values)
  ;; type : (optional) type of check (auto|http|https|udp)
  [check]
  (let [id (get check "_id")]
    (cheshire/parse-string (:body (client/post (check-uri id) {:content-type :json
                                                               :body (cheshire/generate-string check)})))))
