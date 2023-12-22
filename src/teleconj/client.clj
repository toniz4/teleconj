(ns teleconj.client
  (:require [org.httpkit.client :as client]))

(def base-url "https://api.telegram.org")

(defn- call-api-method [method token params]
  (let [url (str base-url "/bot" token "/" method)]
    (client/post url {:content-type
                      :json
                      :form-params
                      params})))

(defn send-message
  ([token chat-id text]
   (send-message token chat-id text nil))
  ([token chat-id text params]
   (let [url (str base-url "/bot" token "/sendMessage")
         form (conj {:chat_id chat-id
                     :text text}
                     params)]
     (call-api-method "sendMessage" token form))))

(defn forward-message
  ([token chat-id from-chat-id message-id]
   (forward-message token chat-id from-chat-id message-id nil))
  ([token chat-id from-chat-id message-id params]
   (let [url (str base-url "/bot" token "/sendMessage")
         form (conj {:chat_id chat-id
                     :from_chat_id from-chat-id
                     :message_id message-id}
                    params)]
     (call-api-method "forwardMessage" token form))))
