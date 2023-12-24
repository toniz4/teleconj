(ns teleconj.poller
  (:require [org.httpkit.client :as client]
            [teleconj.client :refer [base-url]]
            [cheshire.core :as json]
            [clojure.core.async :as a]))

(defn- get-updates
  ([token]
   (get-updates token 0 nil))
  ([token offset timeout]
   (let [url (str base-url "/bot" token "/getUpdates")]
     (-> @(client/get url {:query-params {:offset offset
                                          :timout timeout}})
         :body
         (json/decode keyword)
         :result))))

(defn- polling-loop [config chan]
  (let [{:keys [timeout token handler]} config]
    (a/go-loop [offset 0]
      (if (nil? (a/<! chan))
        (println "exited")
        (do
          (a/>! chan :continue)
          (let [updates (get-updates token offset timeout)
                {update-id :update_id :as last-update} (last updates)]
            (run! #(a/go (handler %)) updates)
            (if (some? last-update)
              (recur (inc update-id))
              (recur offset))))))))

(defn poller-service [config]
  (let [chan (a/chan 10)
        {:keys [join]} config
        process (polling-loop config chan)]
    (a/>!! chan :init)
    (if join
      (a/<!! process)
      chan)))

(defn stop! [service]
  (a/close! service))
