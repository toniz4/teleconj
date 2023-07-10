(ns teleconj.util
  (:require [malli.core :as m]))

(defn match-command
  ([command bot-name]
   (let [command-re (format "(?<=^/)(%s|%s@%s)(?=$|\\s)" command command bot-name)]
     [:map {:closed true}
      [:update_id int?]
      [:message [:map
                 [:text [:re command-re]]
                 [:entities [:fn (fn [val] (some #(= "bot_command" (:type %)) val))]]]]])))

(defn match-message
  ([message-re]
   [:map {:closed true}
    [:update_id int?]
    [:message [:map
               [:text [:re message-re]]]]]))
