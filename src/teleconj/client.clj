(ns teleconj.client
  (:require [org.httpkit.client :as client]
            [clojure.string :as str]))

(def base-url "https://api.telegram.org")

(defn- kebab-case->camel-case [s]
  (reduce #(if (= (last %1) \-)
             (str (apply str (butlast %1)) (str/upper-case %2))
             (str %1 %2)) "" s))

(defn- hyphen->underscore [s]
  (str/replace s "-" "_"))

(defn- args->map [args]
  (->> args
       (map #(if (keyword? %)
               (keyword (hyphen->underscore (name %)))
               %))
       (apply hash-map)))

(defn- call-api-method [method token params]
  (let [url (str base-url "/bot" token "/" method)]
    (client/post url {:content-type :json
                      :form-params params})))

(defmacro defn-api-method [method-sym args]
  (let [method-name (kebab-case->camel-case (str method-sym))
        form (reduce #(conj %1 {} {(keyword (hyphen->underscore %2)) %2}) {} args)]
    `(defn ~method-sym [~'token ~@args & ~'params]
       (let [form# (conj ~form (args->map ~'params))]
         (call-api-method ~method-name ~'token form#)))))

(defn-api-method send-message [chat-id text])

;; (send-message )

;; TODO: redefine api functions

;; @(send-message "6258987071:AAHliVLweKcDX6qSAR8lkMkaFjz4dzX-l5o" 592698126 "teste")

;; (defn send-message [token chat-id text & params]
;;   (let [form (conj {:chat_id chat-id
;;                     :text text}
;;                    (args->map params))]
;;     (call-api-method "sendMessage" token form)))

;; (defn forward-message
;;   ([token chat-id from-chat-id message-id]
;;    (forward-message token chat-id from-chat-id message-id {}))
;;   ([token chat-id from-chat-id message-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :from_chat_id from-chat-id
;;                      :message_id message-id}
;;                     params)]
;;      (call-api-method "forwardMessage" token form))))

;; (defn copy-message
;;   ([token chat-id from-chat-id message-id]
;;    (copy-message token chat-id from-chat-id message-id {}))
;;   ([token chat-id from-chat-id message-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :from_chat_id from-chat-id
;;                      :message_id message-id}
;;                      params)]
;;      (call-api-method "copyMessage" token form))))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ;; Adjust functions in this section to work when receiving files by multipart
;; ;; requests

;; (defn get-file
;;   ([token file-id]
;;    (get-file token file-id {}))
;;   ([token file-id params]
;;    (let [form (conj {:file_id file-id}
;;                     params)]
;;      (call-api-method "getFile" token form))))

;; (defn send-photo
;;   ([token chat-id photo]
;;    (send-photo token chat-id photo {}))
;;   ([token chat-id photo params]
;;    (let [form (conj {:chat_id chat-id
;;                      :photo photo}
;;                      params)]
;;      (call-api-method "sendPhoto" token form))))

;; (defn send-audio
;;   ([token chat-id audio]
;;    (send-audio token chat-id audio {}))
;;   ([token chat-id audio params]
;;    (let [form (conj {:chat_id chat-id
;;                      :audio audio}
;;                      params)]
;;      (call-api-method "sendAudio" token form))))

;; (defn send-document
;;   ([token chat-id document]
;;    (send-document token chat-id document {}))
;;   ([token chat-id document params]
;;    (let [form (conj {:chat_id chat-id
;;                      :document document}
;;                      params)]
;;      (call-api-method "sendDocument" token form))))

;; (defn send-video
;;   ([token chat-id video]
;;    (send-video token chat-id video {}))
;;   ([token chat-id video params]
;;    (let [form (conj {:chat_id chat-id
;;                      :video video}
;;                      params)]
;;      (call-api-method "sendVideo" token form))))

;; (defn send-animation
;;   ([token chat-id animation]
;;    (send-animation token chat-id animation {}))
;;   ([token chat-id animation params]
;;    (let [form (conj {:chat_id chat-id
;;                      :animation animation}
;;                      params)]
;;      (call-api-method "sendAnimation" token form))))

;; (defn send-voice
;;   ([token chat-id voice]
;;    (send-voice token chat-id voice {}))
;;   ([token chat-id voice params]
;;    (let [form (conj {:chat_id chat-id
;;                      :voice voice}
;;                      params)]
;;      (call-api-method "sendVoice" token form))))

;; (defn send-video-note
;;   ([token chat-id video-note]
;;    (send-video-note token chat-id video-note {}))
;;   ([token chat-id video-note params]
;;    (let [form (conj {:chat_id chat-id
;;                      :video_note video-note}
;;                      params)]
;;      (call-api-method "sendVideoNote" token form))))

;; ;; TODO send-media-group
;; (defn send-media-group
;;   ([token chat-id media-group]
;;    (send-media-group token chat-id media-group {}))
;;   ([token chat-id media-group params]
;;    nil))

;; (defn send-location
;;   ([token chat-id location]
;;    (send-location token chat-id location {}))
;;   ([token chat-id location params]
;;    (let [form (conj {:chat_id chat-id
;;                      :location location}
;;                      params)]
;;      (call-api-method "sendLocation" token form))))

;; (defn send-venue
;;   ([token chat-id location title address]
;;    (send-venue token chat-id location title address {}))
;;   ([token chat-id location title address params]
;;    (let [form (conj {:chat_id chat-id
;;                      :location location
;;                      :title title
;;                      :address address}
;;                      params)]
;;      (call-api-method "sendVenue" token form))))

;; (defn send-contact
;;   ([token chat-id phone-number first-name]
;;    (send-contact chat-id phone-number first-name {}))
;;   ([token chat-id phone-number first-name params]
;;    (let [form (conj {:chat_id chat-id
;;                      :phone_number phone-number
;;                      :first_name first-name}
;;                     params)]
;;      (call-api-method "sendContact" token form))))

;; (defn send-poll
;;   ([token chat-id question options]
;;    (send-poll token chat-id question options {}))
;;   ([token chat-id question options]
;;    (let [form (conj {:chat_id chat-id
;;                      :question question
;;                      :options options}
;;                      params)]
;;      (call-api-method "sendPoll" token form))))

;; (defn send-dice
;;   ([token chat-id]
;;    (send-dice token chat-id {}))
;;   ([token chat-id params]
;;    (let [form (conj {:chat_id chat-id} params)]
;;      (call-api-method "sendPoll" token form))))

;; (defn send-chat-action
;;   ([token chat-id action]
;;    (send-chat-action token chat-id action {}))
;;   ([token chat-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :action action}
;;                     params)]
;;      (call-api-method "sendChatAction" token form))))

;; (defn get-user-profile-photos
;;   ([token chat-id]
;;    (get-user-profile-photos token chat-id {}))
;;   ([token chat-id params]
;;    (let [form (conj {:chat_id chat-id}
;;                     params)]
;;      (call-api-method "getUserProfilePhotos" token form))))

;; (defn ban-chat-member
;;   ([token chat-id user-id]
;;    (ban-chat-member token chat-id user-id {}))
;;   ([token chat-id user-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :user_id user-id}
;;                     params)]
;;      (call-api-method "banChatMember" token form))))

;; (defn unban-chat-member
;;   ([token chat-id user-id]
;;    (unban-chat-member token chat-id user-id {}))
;;   ([token chat-id user-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :user_id user-id}
;;                     params)]
;;      (call-api-method "unbanChatMember" token form))))

;; (defn restrict-chat-member
;;   ([token chat-id user-id]
;;    (restrict-chat-member token chat-id user-id {}))
;;   ([token chat-id user-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :user_id user-id
;;                      :permissions permissions}
;;                     params)]
;;      (call-api-method "restictChatMember" token form))))

;; (defn promote-chat-member
;;   ([token chat-id user-id]
;;    (promote-chat-member token chat-id user-id {}))
;;   ([token chat-id user-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :user_id user-id}
;;                     params)]
;;      (call-api-method "promoteChatMember" token form))))

;; (defn set-chat-administrator-custom-title
;;   [token chat-id user-id custom-title]
;;   (let [form {:chat_id chat-id
;;               :user_id user-id
;;               :custom_title custom-title}]
;;     (call-api-method "setChatAdministratorCustomTitle" token form)))

;; (defn ban-chat-sender-chat
;;   [token chat-id sender-chat-id]
;;   (let [form {:chat_id chat-id
;;               :sender_chat_id sender-chat-id}]
;;     (call-api-method "banChatSenderChat" token form)))

;; (defn unban-chat-sender-chat
;;   [token chat-id sender-chat-id]
;;   (let [form {:chat_id chat-id
;;                     :sender_chat_id sender-chat-id}]
;;     (call-api-method "unbanChatSenderChat" token form)))

;; (defn set-chat-permissions
;;   ([token chat-id permissions]
;;    (set-chat-permissions token chat-id permissions {}))
;;   ([token chat-id permissions params]
;;    (let [form (conj {:chat_id chat-id
;;                      :user_id user-id
;;                      :permissions permissions}
;;                     params)]
;;      (call-api-method "setChatPermissions" token form))))

;; (defn export-chat-invite-link [token chat-id]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "exportChatInviteLink" token form)))

;; (defn create-chat-invite-link [token chat-id]
;;   ([token chat-id]
;;    (create-chat-invite-link chat-id {}))
;;   ([token chat-id params]
;;    (let [form (conj {:chat_id chat-id} params)]
;;      (call-api-method "createChatInviteLink" token form))))

;; (defn edit-chat-invite-link [token chat-id]
;;   ([token chat-id invite-link]
;;    (edit-chat-invite-link chat-id invite-link {}))
;;   ([token chat-id invite-link params]
;;    (let [form (conj {:chat_id chat-id} params)]
;;      (call-api-method "editChatInviteLink" token form))))

;; (defn revoke-chat-invite-link [token chat-id invite-link]
;;   (let [form {:chat_id chat-id
;;               :invite-link invite-link}]
;;     (call-api-method "revokeChatInviteLink" token form)))

;; (defn approve-chat-join-request [token chat-id invite-link]
;;   (let [form {:chat_id chat-id
;;               :user_id user-id}]
;;     (call-api-method "approveChatJoinRequest" token form)))

;; (defn decline-chat-join-request [token chat-id invite-link]
;;   (let [form {:chat_id chat-id
;;               :user_id user-id}]
;;     (call-api-method "declineChatJoinRequest" token form)))

;; (defn set-chat-photo [token chat-id invite-link]
;;   (let [form {:chat_id chat-id
;;               :photo photo}]
;;     (call-api-method "setChatPhoto" token form)))

;; (defn delete-chat-photo [token chat-id invite-link]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "deleteChatPhoto" token form)))

;; (defn set-chat-title [token chat-id invite-link]
;;   (let [form {:chat_id chat-id
;;               :title title}]
;;     (call-api-method "setChatTitle" token form)))

;; (defn set-chat-description [token chat-id invite-link]
;;   (let [form {:chat_id chat-id
;;               :description description}]
;;     (call-api-method "setChatDescription" token form)))

;; (defn pin-chat-message
;;   ([token chat-id message-id]
;;    (pin-chat-message chat-id message-id {}))
;;   ([token chat-id message-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :message_id message-id}
;;                     params)]
;;      (call-api-method "pinChatMessage" token form))))

;; (defn unpin-chat-message
;;   ([token chat-id message-id]
;;    (unpin-chat-message chat-id message-id {}))
;;   ([token chat-id message-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :message_id message-id}
;;                     params)]
;;      (call-api-method "unpinChatMessage" token form))))

;; (defn unpin-all-chat-messages [token chat-id]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "unpinAllChatMessages" token form)))

;; (defn leave-chat [token chat-id]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "leaveChat" token form)))

;; (defn get-chat [token chat-id]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "getChat" token form)))

;; (defn get-chat-administrators [token chat-id]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "getChatAdministrators" token form)))

;; (defn get-chat-member-count [token chat-id]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "getChatMemberCount" token form)))

;; (defn get-chat-member [token chat-id user-id]
;;   (let [form {:chat_id chat-id
;;               :user_id user-id}]
;;     (call-api-method "getChatMember" token form)))

;; (defn get-chat-member [token chat-id user-id]
;;   (let [form {:chat_id chat-id
;;               :user_id user-id}]
;;     (call-api-method "getChatMember" token form)))

;; (defn set-chat-sticker-set [token chat-id stciker-set-name]
;;   (let [form {:chat_id chat-id
;;               :sticker_set_name sticker-set-name}]
;;     (call-api-method "setChatStickerSet" token form)))

;; (defn delete-chat-sticker-set [token chat-id]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "deleteChatStickerSet" token form)))

;; (defn delete-chat-sticker-set [token]
;;   (call-api-method "deleteChatStickerSet" token))

;; (defn create-forum-topic
;;   ([token chat-id message-id]
;;    (create-forum-topic chat-id name {}))
;;   ([token chat-id message-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :name name}
;;                     params)]
;;      (call-api-method "createForumTropic" token form))))

;; (defn edit-forum-topic
;;   ([token chat-id message-id]
;;    (edit-forum-topic chat-id name {}))
;;   ([token chat-id message-id params]
;;    (let [form (conj {:chat_id chat-id
;;                      :name name}
;;                     params)]
;;      (call-api-method "editForumTropic" token form))))

;; (defn close-forum-topic
;;   [token chat-id message-thread-id params]
;;   (let [form {:chat_id chat-id
;;               :message_thread_id message-thread-id}]
;;     (call-api-method "closeForumTropic" token form)))

;; (defn reopen-forum-topic
;;   [token chat-id message-thread-id]
;;   (let [form {:chat_id chat-id
;;               :message_thread_id message-thread-id}]
;;     (call-api-method "reopenForumTropic" token form)))

;; (defn delete-forum-topic
;;   ([token chat-id message-thread-id]
;;    (delete-forum-topic chat-id message-thread-id {}))
;;   ([token chat-id message-thread-id params]
;;    (let [form {:chat_id chat-id
;;                :message_thread_id message-thread-id}]
;;      (call-api-method "deleteForumTropic" token form))))

;; (defn unpin-all-forum-topic-messages
;;   [token chat-id message-thread-id params]
;;   (let [form {:chat_id chat-id
;;               :message_thread_id message-thread-id}]
;;     (call-api-method "unpinAllForumTopicMessages" token form)))

;; (defn edit-general-forum-topic [token chat-id name]
;;   (let [form {:chat_id chat-id
;;               :name name}]
;;     (call-api-method "editGeneralForumTopic" token form)))

;; (defn close-general-forum-topic [token chat-id name]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "closeGeneralForumTopic" token form)))

;; (defn reopen-general-forum-topic [token chat-id name]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "reopenGeneralForumTopic" token form)))

;; (defn hide-general-forum-topic [token chat-id name]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "hideGeneralForumTopic" token form)))

;; (defn unhide-general-forum-topic [token chat-id name]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "unhideGeneralForumTopic" token form)))

;; (defn unpin-all-general-forum-topic-messages [token chat-id name]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "unpinAllGeneralForumTopicMessages" token form)))

;; (defn answer-callback-query [token chat-id name]
;;   (let [form {:chat_id chat-id}]
;;     (call-api-method "answerCallbackQuery" token form)))

;; (defn set-my-commands [token chat-id name]
;;   (let [form {:commands commands}]
;;     (call-api-method "setMyCommands" token form)))

;; (defn delete-my-commands
;;  ([token] (delete-my-commands {}))
;;  ([token params]
;;   (call-api-method "deleteMyCommands" token params)))

;; (defn get-my-commands
;;  ([token] (get-my-commands {}))
;;  ([token params]
;;   (call-api-method "getMyCommands" token params)))

;; (defn set-my-name
;;  ([token] (set-my-name {}))
;;  ([token params]
;;   (call-api-method "setMyName" token params)))

;; (defn get-my-name
;;  ([token] (get-my-name {}))
;;  ([token params]
;;   (call-api-method "getMyName" token params)))

;; (defn set-my-description
;;  ([token] (set-my-description {}))
;;  ([token params]
;;   (call-api-method "setMyDescription" token params)))

;; (defn get-my-description
;;  ([token] (get-my-description {}))
;;  ([token params]
;;   (call-api-method "getMyDescription" token params)))

;; (defn set-my-short-description
;;  ([token] (set-my-short-description {}))
;;  ([token params]
;;   (call-api-method "setMyShortDescription" token params)))

;; (defn get-my-short-description
;;  ([token] (get-my-short-description {}))
;;  ([token params]
;;   (call-api-method "getMyShortDescription" token params)))

;; (defn get-chat-menu-button
;;  ([token] (get-chat-menu-button {}))
;;  ([token params]
;;   (call-api-method "getChatMenuButton" token params)))

;; (defn set-my-default-administrator-rights
;;  ([token] (set-my-default-administrator-rights {}))
;;  ([token params]
;;   (call-api-method "setMyAdministratorRights" token params)))

;; (defn get-my-default-administrator-rights
;;  ([token] (get-my-default-administrator-rights {}))
;;  ([token params]
;;   (call-api-method "getMyAdministratorRights" token params)))

;; (defn edit-message-text
;;  ([token] (edit-message-text {}))
;;  ([token text params]
;;   (let [form (conj {:text text} params)]
;;     (call-api-method "editMessageText" token form))))

;; (defn edit-message-caption
;;  ([token] (edit-message-caption {}))
;;  ([token params]
;;   (call-api-method "editMessageCaption" token params)))

;; (defn edit-message-media
;;  ([token] (edit-message-media {}))
;;  ([token media params]
;;   (let [form (conj {:media media} params)]
;;     (call-api-method "editMessageMedia" token form))))

;; (defn edit-message-live-location
;;  ([token] (edit-message-live-location {}))
;;  ([token location params]
;;   (let [form (conj {:location location} params)]
;;     (call-api-method "editMessageMedia" token form))))

;; (defn stop-message-live-location
;;  ([token] (stop-message-live-location {}))
;;  ([token params]
;;   (call-api-method "stopMessageLiveLocation" token params)))

;; (defn edit-message-reply-markup
;;  ([token] (edit-message-reply-markup {}))
;;  ([token params]
;;   (call-api-method "editMessageReplyMarkup" token params)))

;; (defn stop-poll
;;   ([token chat-id message_id]
;;    (stop-poll token chat-id message-id {}))
;;   ([token chat-id message-id]
;;    (let [form (conj {:chat_id chat-id
;;                      :message-id message-id}
;;                      params)]
;;      (call-api-method "stopPoll" token form))))

;; (defn delete-message [token chat-id message-id]
;;   (let [form {:chat_id chat-id
;;               :message-id message-id}]
;;     (call-api-method "deleteMessage" token form)))

;; ;; TODO: create a sticker record
;; (defn send-sticker
;;   ([token chat-id sticker]
;;    (send-sticker token chat-id sticker {}))
;;   ([token chat-id sticker]
;;    (let [form (conj {:chat_id chat-id
;;                      :sticker sticker}
;;                     params)]
;;      (call-api-method "sendSticker" token form))))

;; (defn get-sticker-set [token name]
;;   (let [form {:name name}]
;;     (call-api-method "getStickerSet" token form)))

;; (defn get-custom-emoji-stickers
;;   [token custom-emoji-ids]
;;   (let [form {:custom_emoji_ids custom-emoji-ids}]
;;     (call-api-method "getCustomEmojiStickers" token form)))

;; (defn upload-sticker-file [token user-id sticker sticker-format]
;;   (let [form {:user_id user-id
;;               :sticker sticker
;;               :sticker-format sticker-format}]
;;     (call-api-method "getStickerSet" token form)))

;; (defn create-new-sticker-set
;;   ([token chat-id name title stickers sticker-format]
;;    (create-new-sticker-set
;;     token chat-id name title stickers sticker-format {}))
;;   ([token chat-id name title stickers sticker-format params]
;;    (let [form (conj {:chat_id chat-id
;;                      :name name
;;                      :title title
;;                      :stickers stickers
;;                      :sticker_format sticker-format}
;;                     params)]
;;      (call-api-method "createNewStickerSet" token form))))

;; (defn add-sticker-to-set [token user-id name sticker]
;;   (let [form {:user_id user-id
;;               :name name
;;               :sticker sticker}]
;;     (call-api-method "addStickerToSet" token form)))

;; (defn set-sticker-position-in-set [token sticker position]
;;   (let [form {:sticker sticker
;;               :position position}]
;;     (call-api-method "setStickerPositionInSet" token form)))

;; (defn delete-sticker-from-set [token sticker position]
;;   (let [form {:sticker sticker}]
;;     (call-api-method "deleteStickerFromSet" token form)))

;; (defn set-sticker-emoji-list [token sticker emoji-list]
;;   (let [form {:sticker sticker
;;               :emoji_list emoji-list}]
;;     (call-api-method "setStickerEmojiList" token form)))

;; (defn set-sticker-keywords [token sticker keywords]
;;   (let [form {:sticker sticker
;;               :keywords keywords}]
;;     (call-api-method "setStickerKeywords" token form)))

;; (defn set-sticker-set-title [token name title]
;;   (let [form {:name name
;;               :title title}]
;;     (call-api-method "setStickerSetTitle" token form)))

;; (defn setStickerMaskPosition
;;   ([token sticker]
;;    (setStickerMaskPosition token sticker {}))
;;   ([token sticker params]
;;    (let [form (conj {:sticker sticker} params)]
;;      (call-api-method "setStickerMaskPosition" token form))))

;; (defn set-sticker-set-thumbnail
;;   ([token name user_id]
;;    (set-sticker-set-thumbnail token name user_id {}))
;;   ([token name user_id params]
;;    (let [form (conj {:sticker sticker} params)]
;;      (call-api-method "setStickerSetThumbnail" token form))))

;; (defn set-custom-emoji-sticker-set-thumbnail
;;   ([token name]
;;    (set-custom-emoji-sticker-set-thumbnail token name {}))
;;   ([token name params]
;;    (let [form (conj {:name name} params)]
;;      (call-api-method "setCustomEmojiStickerSetThumbnail" token form))))

;; (defn delete-sticker-set [token name]
;;   (let [form {:name name}]
;;     (call-api-method "deleteStickerSet" token form)))

;; (defn answer-inline-query
;;   ([token inline-query-id results]
;;    (answer-inline-query token inline-query-id results {}))
;;   ([token inline-query-id results]
;;    (let [form (conj {:inline_query_id inline-query-id
;;                      :results results}
;;                     params)]
;;      (call-api-method "answerInlineQuery" token form))))

;; (defn answer-inline-query
;;   ([token inline-query-id results]
;;    (answer-inline-query token inline-query-id results {}))
;;   ([token inline-query-id results]
;;    (let [form (conj {:inline_query_id inline-query-id
;;                      :results results}
;;                     params)]
;;      (call-api-method "answerInlineQuery" token form))))

;; ;; TODO rewrite it using keyword arguments
;; (defn send-invoice
;;   ([token chat-id title description payload provider-token currency prices]
;;    (send-invoice
;;     token chat-id title description payload provider-token currency prices {}))
;;   ([token chat-id title description payload provider-token currency prices params]
;;    (let [form (conj {:chat_id chat-id
;;                      :title title
;;                      :description description
;;                      :payload payload
;;                      :provider_token provider-token
;;                      :currrency currency
;;                      :prices prices}
;;                     params)]
;;      (call-api-method "sendInvoice" token form))))

;; (defn send-invoice-link
;;   ([token chat-id title description payload provider-token currency prices]
;;    (send-invoice-link
;;     token chat-id title description payload provider-token currency prices {}))
;;   ([token chat-id title description payload provider-token currency prices params]
;;    (let [form (conj {:chat_id chat-id
;;                      :title title
;;                      :description description
;;                      :payload payload
;;                      :provider_token provider-token
;;                      :currrency currency
;;                      :prices prices}
;;                     params)]
;;      (call-api-method "sendInvoiceLink" token form))))

;; (defn answer-shipping-query
;;   ([token shipping-query-id ok]
;;    (answer-shipping-query token shipping-query-id ok))
;;   ([token shipping-query-id ok params]
;;    (let [form (conj {:shipping_query_id shipping-query-id
;;                      :ok ok}
;;                     params)]
;;      (call-api-method "answerShippingQuery" token form))))

;; (defn answer-pre-checkout-query
;;   ([pre-checkout-query-id ok]
;;    (answer-pre-checkout-query token pre-checkout-query-id ok))
;;   ([token pre-checkout-query-id ok params]
;;    (let [form (conj {:pre_checkout_query_id pre-checkout-query-id
;;                      :ok ok}
;;                     params)]
;;      (call-api-method "answerPreCheckoutQuery" token form))))

;; (defn send-game
;;   ([token chat-id game-short-name]
;;    (send-game token chat-id game-short-name {}))
;;   ([token chat-id game-short-name params]
;;    (let [form (conj {:chat_id chat-id
;;                      :game_short_name game-short-name}
;;                     params)]
;;      (call-api-method "sendGame" token form))))

;; (defn set-game-score
;;   ([token chat-id user-id]
;;    (set-game chat-id user-id))
;;   ([token chat-id user-id params]
;;    (let [form (conj {:chat_id game-short-id
;;                      :user_id user-id}
;;                     params)]
;;      (call-api-method "answerPreCheckoutQuery" token form))))
