(ns teleconj.middleware
  (:require [clojure.string :as str]))

(defn message-middleware
  "Takes a update and adds the key :message-text containing the message text and the key :chat-id with the chat id"
  [update]
  (let [{:keys [message]} update]
       (conj update
             {:message-text (:text message)
              :chat-id (-> message :chat :id)})))

(defn command-middleware
  "Takes a command update and adds the key :command-args containing the arguments to a command"
  [update]
  (let [{:keys [message]} update
        args (rest (str/split (:text message) #" "))]
    (conj update {:command-args args})))
