(ns teleconj.handler
  (:require [malli.core :as m]))

(def ^:private message?
  (m/validator
   [:map {:closed true}
    [:update_id int?]
    [:message [:map]]]))

(def ^:private inline-query?
  (m/validator
   [:map {:closed true}
    [:update_id int?]
    [:inline_query [:map]]]))

(def ^:private command?
  (m/validator
   [:map {:closed true}
    [:update_id int?]
    [:message [:map
               [:entities [:fn (fn [val] (some #(= "bot_command" (:type %)) val))]]]]]))

(defn- get-update-type [up]
  (cond
    (command? up) :command
    (message? up) :message
    (inline-query? up) :inline-query
    :else :unhandled))

(defn- apply-middleware [middleware handler]
  (if middleware
    (reduce (fn [acc f]
              (if (sequential? f)
                (apply (first f) acc (rest f))
                (f acc)))
            handler middleware)
    identity))

(defn- handler [botspec up]
  (loop [handle botspec]
    (if (nil? (seq handle))
      (:update_id up)
      (let [current (first handle)
            update-type (get-update-type up)
            {:keys [handler schema exclusive middleware]} (update-type current)
            match (if schema (m/validate schema up) false)]
        (when match
          ((apply-middleware middleware handler) up))
        (recur (next (if-not (and exclusive match) handle nil)))))))

(defn make-handler [botspec]
  (partial handler botspec))

