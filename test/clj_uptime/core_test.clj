(ns clj-uptime.core-test
  (:require [clojure.test :refer :all]
            [clj-uptime.core :refer :all]))

(defn delete-all-checks []
  (map delete-check (get-checks)))

(deftest test-setup-client
  (testing "Calling setup with wrong uri should throw"
    (is (thrown? Exception (setup-client "kehe.com/"))))
  (testing "Calling setup with correct url should set the base-api-uri and return the uri"
    (is (= (setup-client "kehe.com/api") "kehe.com/api"))
    (is (= @base-api-uri "kehe.com/api"))))

(deftest test-setup-client
  (testing "Calling setup with wrong uri should throw"
    (is (thrown? Exception (setup-client "kehe.com/"))))
  (testing "Calling setup with correct url should set the base-api-uri and return the uri"
    (is (= (setup-client "kehe.com/api") "kehe.com/api"))
    (is (= @base-api-uri "kehe.com/api"))))

(deftest test-uris
  (setup-client "http://kehe.com/api")
  (testing "checks-uri"
    (is (= (checks-uri) "http://kehe.com/api/checks")))
  (testing "check-uri"
    (is (= (check-uri "coolID") "http://kehe.com/api/checks/coolID"))))


;; Integration tests. Run with git@github.com:simonjohansson/uptime.git
(deftest test-scenario1
  (setup-client "http://192.168.99.100:8082/api")
  (delete-all-checks)
  (testing "Adding and deleting check."
    (let [check (add-check {:url "http://google.com"})
          id (get check "_id")]

      ;; Make sure the check is there
      (is (= check (get-check-by-id id)))

      ;; Delete the check
      (delete-check check)

      ;; Make sure the check is not there
      (is (thrown? Exception (get-check-by-id id))))))

(deftest test-scenario2
  (setup-client "http://192.168.99.100:8082/api")
  (delete-all-checks)
  (testing "Adding and updating a check."
    (let [check (add-check {:url "http://google.com"})
          updated-check (assoc check :url "http://simonjohansson.com")
          id (get check "_id")]

      ;; Make sure the check is there
      (is (= check (get-check-by-id id)))

      ;; Update the check
      (update-check updated-check)

      ;; Make sure the check got updated
      (is (= (get (get-check-by-id id) "url") "http://simonjohansson.com"))

      ;; Delete the check
      (delete-check check))))
