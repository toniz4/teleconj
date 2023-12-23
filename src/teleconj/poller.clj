(ns teleconj.poller
  (:require [org.httpkit.client :as client]
            [teleconj.client :refer [base-url]]
            [teleconj.handler :as handler]
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

(defn poller-service
  ([config]
   (poller-service 0 config))
  ([offset config]
   (let [chan (a/chan 10)
         {:keys [timeout token handler]} config]
     (a/>!! chan :init)
     (a/go-loop [offset offset]
       (if (nil? (a/<! chan))
         (println "exited")
         (do
           (a/>! chan :continue)
           (let [update (->> (get-updates token offset timeout)
                             (reduce #(a/go (handler %2)) ())]
             (recur (if (some? update)
                      (inc (a/<! update))
                      offset))))))
     chan)))

(defn stop! [service]
  (a/close! service))
