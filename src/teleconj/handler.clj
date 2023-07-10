(ns teleconj.handler
  (:require [malli.core :as m]
            [clojure.core.async :as a]))

(def ^:private message?
  (m/validator
   [:map {:closed true}
    [:update_id int?]
    [:message [:map]]]))

(def ^:private command?
  (m/validator
   [:map {:closed true}
    [:update_id int?]
    [:message [:map
               [:entities [:fn (fn [val] (some #(= "bot_command" (:type %)) val))]]]]]))

(defn- get-update-type [update]
  (cond
    (command? update) :command
    (message? update) :message
    :else :unhandled))

(defn- apply-middleware [middleware update]
  (if middleware
    (reduce #(%2 %1) update middleware)
    identity))

(defn- handler [botspec update]
  (loop [handle botspec]
    (if (nil? (seq handle))
      (:update_id update)
      (let [current (first handle)
            update-type (get-update-type update)
            {:keys [callback schema exclusive middleware]} (update-type current)
            match (if schema (m/validate schema update) false)]
        (when match
          (callback (apply-middleware middleware update)))
        (recur (next (if-not (and exclusive match) handle nil)))))))

(defn make-handler [botspec]
  (partial handler botspec))

