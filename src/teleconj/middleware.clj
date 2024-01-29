(ns teleconj.middleware
  (:require [clojure.string :as str]))

(defn message-middleware
  "Takes a update and adds the key :message-text containing the
  message text and the key :chat-id with the chat id"
  [handler]
  (fn [up]
    (let [{:keys [message]} up]
      (handler (conj up
                     {:message-text (:text message)
                      :chat-id (-> message :chat :id)})))))

(defn command-middleware
  "Takes a command update and adds the key :command-args
  containing the arguments to a command"
  [handler]
  (fn [up]
    (let [{:keys [message]} up
          args (rest (str/split (:text message) #" "))]
      (handler (conj up {:command-args args})))))

(defn inline-middleware
  "Takes a inline update and adds the key
  :query containing the inline query"
  [handler]
  (fn [up]
    (let [{:keys [inline_query]} up]
      (conj up {:query (:query inline_query)}))))

(defn loging-middleware
  "Logs the current update with logfn"
  [handler logfn]
  (fn [up]
    (logfn up)
    (handler up)))
