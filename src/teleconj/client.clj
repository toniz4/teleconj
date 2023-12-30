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

(defmacro defn-api-method [method-sym doc args]
  (let [method-name (kebab-case->camel-case (str method-sym))
        form (reduce #(conj %1 {} {(keyword (hyphen->underscore %2)) %2}) {} args)]
    `(defn ~method-sym
       ~doc
       [~'token ~@args & ~'params]
       (let [form# (conj ~form (args->map ~'params))]
         (call-api-method ~method-name ~'token form#)))))

(defn-api-method get-me
  "A simple method for testing your bot's authentication token. Requires no
   parameters.  Returns basic information about the bot in form of a User
   object."
  [])

(defn-api-method log-out
  "Use this method to log out from the cloud Bot API server before launching the
  bot locally. You must log out the bot before running it locally, otherwise
  there is no guarantee that the bot will receive updates. After a successful
  call, you can immediately log in on a local server, but will not be able to
  log in back to the cloud Bot API server for 10 minutes. Returns True on
  success. Requires no parameters."
  [])

(defn-api-method close
  "Use this method to close the bot instance before moving it from one local
  server to another. You need to delete the webhook before calling this method
  to ensure that the bot isn't launched again after server restart. The method
  will return error 429 in the first 10 minutes after the bot is
  launched. Returns True on success. Requires no parameters."
  [])

(defn-api-method send-message
  "Use this method to send text messages. On success, the sent Message is
  returned."
  [chat-id text])

(defn-api-method forward-message
  "Use this method to forward messages of any kind. Service messages can't be
  forwarded. On success, the sent Message is returned."
  [chat-id from-chat-id message-id])

(defn-api-method copy-message
  "Use this method to copy messages of any kind. Service messages and invoice
  messages can't be copied. A quiz poll can be copied only if the value of the
  field correct_option_id is known to the bot. The method is analogous to the
  method forwardMessage, but the copied message doesn't have a link to the
  original message. Returns the MessageId of the sent message on success."
  [chat-id from-chat-id message-id])

(defn-api-method send-photo
  "Use this method to send photos. On success, the sent Message is returned."
  [chat-id photo])

(defn-api-method send-audio
  "Use this method to send audio files, if you want Telegram clients to display
  them in the music player. Your audio must be in the .MP3 or .M4A format. On
  success, the sent Message is returned. Bots can currently send audio files of
  up to 50 MB in size, this limit may be changed in the future.

  For sending voice messages, use the sendVoice method instead."
  [chat-id photo])

(defn-api-method send-document
  "Use this method to send general files. On success, the sent Message is
  returned. Bots can currently send files of any type of up to 50 MB in size,
  this limit may be changed in the future."
  [chat-id document])

(defn-api-method send-video
  "Use this method to send video files, Telegram clients support MPEG4 videos
  (other formats may be sent as Document). On success, the sent Message is
  returned. Bots can currently send video files of up to 50 MB in size, this
  limit may be changed in the future."
  [chat-id video])

(defn-api-method send-animation
  "Use this method to send animation files (GIF or H.264/MPEG-4 AVC video
  without sound). On success, the sent Message is returned. Bots can currently
  send animation files of up to 50 MB in size, this limit may be changed in the
  future."
  [chat-id animation])

(defn-api-method send-voice
  "Use this method to send audio files, if you want Telegram clients to display
  the file as a playable voice message. For this to work, your audio must be in
  an .OGG file encoded with OPUS (other formats may be sent as Audio or
  Document). On success, the sent Message is returned. Bots can currently send
  voice messages of up to 50 MB in size, this limit may be changed in the
  future."
  [chat-id voice])

(defn-api-method send-video-note
  "Use this method to send video messages. On success, the sent Message is
  returned."
  [chat-id video-note])

(defn-api-method send-media-group
  "Use this method to send a group of photos, videos, documents or audios as an
  album. Documents and audio files can be only grouped in an album with messages
  of the same type. On success, an array of Messages that were sent is returned."
  [chat-id media])

(defn-api-method send-location
  "Use this method to send point on the map. On success, the sent Message is
  returned."
  [chat-id latitude longitude])

(defn-api-method send-venue
  "Use this method to send information about a venue. On success, the sent
  Message is returned."
  [chat-id latitude longitude title address])

(defn-api-method send-contact
  "Use this method to send phone contacts. On success, the sent Message is
  returned."
  [chat-id phone-number first-name])

(defn-api-method send-poll
  "Use this method to send a native poll. On success, the sent Message is
  returned."
  [chat-id question options])

(defn-api-method send-dice
  "Use this method to send an animated emoji that will display a random
  value. On success, the sent Message is returned."
  [chat-id])

(defn-api-method send-chat-action
  "Use this method when you need to tell the user that something is happening on
  the bot's side. The status is set for 5 seconds or less (when a message
  arrives from your bot, Telegram clients clear its typing status). Returns True
  on success."
  [chat-id action])

(defn-api-method get-user-profile-photos
  "Use this method to get a list of profile pictures for a user. Returns a
  UserProfilePhotos object."
  [user-id])

(defn-api-method get-file
  "Use this method to get basic information about a file and prepare it for
  downloading. For the moment, bots can download files of up to 20MB in size. On
  success, a File object is returned. The file can then be downloaded via the
  link https://api.telegram.org/file/bot<token>/<file_path>, where <file_path>
  is taken from the response. It is guaranteed that the link will be valid for
  at least 1 hour. When the link expires, a new one can be requested by calling
  getFile again."
  [file-id])

(defn-api-method ban-chat-member
  "Use this method to ban a user in a group, a supergroup or a channel. In the
  case of supergroups and channels, the user will not be able to return to the
  chat on their own using invite links, etc., unless unbanned first. The bot
  must be an administrator in the chat for this to work and must have the
  appropriate administrator rights. Returns True on success."
  [chat-id user-id])

(defn-api-method unban-chat-member
  "Use this method to unban a previously banned user in a supergroup or
  channel. The user will not return to the group or channel automatically, but
  will be able to join via link, etc. The bot must be an administrator for this
  to work. By default, this method guarantees that after the call the user is
  not a member of the chat, but will be able to join it. So if the user is a
  member of the chat they will also be removed from the chat. If you don't want
  this, use the parameter only_if_banned. Returns True on success."
  [chat-id user-id])

(defn-api-method restrict-chat-member
  "Use this method to restrict a user in a supergroup. The bot must be an
  administrator in the supergroup for this to work and must have the appropriate
  administrator rights. Pass True for all permissions to lift restrictions from
  a user. Returns True on success."
  [chat-id user-id permissions])

(defn-api-method promote-chat-member
  "Use this method to promote or demote a user in a supergroup or a channel. The
  bot must be an administrator in the chat for this to work and must have the
  appropriate administrator rights. Pass False for all boolean parameters to
  demote a user. Returns True on success."
  [chat-id user-id])

(defn-api-method set-chat-administrator-custom-title
  "Use this method to set a custom title for an administrator in a supergroup
  promoted by the bot. Returns True on success."
  [chat-id user-id custom-title])

(defn-api-method ban-chat-sender-chat
  "Use this method to ban a channel chat in a supergroup or a channel. Until the
  chat is unbanned, the owner of the banned chat won't be able to send messages
  on behalf of any of their channels. The bot must be an administrator in the
  supergroup or channel for this to work and must have the appropriate
  administrator rights. Returns True on success."
  [chat-id sender-chat-id])

(defn-api-method unban-chat-sender-chat
  "Use this method to unban a previously banned channel chat in a supergroup or
  channel. The bot must be an administrator for this to work and must have the
  appropriate administrator rights. Returns True on success."
  [chat-id sender-chat-id])

(defn-api-method set-chat-permissions
  "Use this method to unban a previously banned channel chat in a supergroup or
  channel. The bot must be an administrator for this to work and must have the
  appropriate administrator rights. Returns True on success."
  [chat-id permissions])

(defn-api-method export-chat-invite-link
  "Use this method to generate a new primary invite link for a chat; any
  previously generated primary link is revoked. The bot must be an administrator
  in the chat for this to work and must have the appropriate administrator
  rights. Returns the new invite link as String on success."
  [chat-id])

(defn-api-method create-chat-invite-link
  "Use this method to create an additional invite link for a chat. The bot must
  be an administrator in the chat for this to work and must have the appropriate
  administrator rights. The link can be revoked using the method
  revokeChatInviteLink. Returns the new invite link as ChatInviteLink object."
  [chat-id])

(defn-api-method edit-chat-invite-link
  "Use this method to edit a non-primary invite link created by the bot. The bot
  must be an administrator in the chat for this to work and must have the
  appropriate administrator rights. Returns the edited invite link as a
  ChatInviteLink object."
  [chat-id invite-link])

(defn-api-method revoke-chat-invite-link
  "Use this method to revoke an invite link created by the bot. If the primary
  link is revoked, a new link is automatically generated. The bot must be an
  administrator in the chat for this to work and must have the appropriate
  administrator rights. Returns the revoked invite link as ChatInviteLink
  object."
  [chat-id invite-link])

(defn-api-method approve-chat-join-request
  "Use this method to approve a chat join request. The bot must be an
  administrator in the chat for this to work and must have the can_invite_users
  administrator right. Returns True on success"
  [chat-id user-id])

(defn-api-method decline-chat-join-request
  "Use this method to decline a chat join request. The bot must be an
  administrator in the chat for this to work and must have the can_invite_users
  administrator right. Returns True on success."
  [chat-id user-id])

(defn-api-method set-chat-photo
  "Use this method to set a new profile photo for the chat. Photos can't be
  changed for private chats. The bot must be an administrator in the chat for
  this to work and must have the appropriate administrator rights. Returns True
  on success."
  [chat-id photo])

(defn-api-method delete-chat-photo
  "Use this method to delete a chat photo. Photos can't be changed for private
  chats. The bot must be an administrator in the chat for this to work and must
  have the appropriate administrator rights. Returns True on success."
  [chat-id])

(defn-api-method set-chat-title
  "Use this method to change the title of a chat. Titles can't be changed for
  private chats. The bot must be an administrator in the chat for this to work
  and must have the appropriate administrator rights. Returns True on success."
  [chat-id title])

(defn-api-method set-chat-description
  "Use this method to change the description of a group, a supergroup or a
  channel. The bot must be an administrator in the chat for this to work and
  must have the appropriate administrator rights. Returns True on success."
  [chat-id description])

(defn-api-method pin-chat-message
  "Use this method to add a message to the list of pinned messages in a chat. If
  the chat is not a private chat, the bot must be an administrator in the chat
  for this to work and must have the 'can_pin_messages' administrator right in a
  supergroup or 'can_edit_messages' administrator right in a channel. Returns
  True on success."
  [chat-id message-id])

(defn-api-method unpin-chat-message
  "Use this method to remove a message from the list of pinned messages in a
  chat. If the chat is not a private chat, the bot must be an administrator in
  the chat for this to work and must have the 'can_pin_messages' administrator
  right in a supergroup or 'can_edit_messages' administrator right in a
  channel. Returns True on success."
  [chat-id message-id])

(defn-api-method unpin-all-chat-messages
  "Use this method to clear the list of pinned messages in a chat. If the chat
  is not a private chat, the bot must be an administrator in the chat for this
  to work and must have the 'can_pin_messages' administrator right in a
  supergroup or 'can_edit_messages' administrator right in a channel. Returns
  True on success."
  [chat-id])

(defn-api-method leave-chat
  "Use this method for your bot to leave a group, supergroup or channel. Returns
  True on success."
  [chat-id])

(defn-api-method get-chat
  "Use this method to get up to date information about the chat (current name of
  the user for one-on-one conversations, current username of a user, group or
  channel, etc.). Returns a Chat object on success."
  [chat-id])

(defn-api-method get-chat-administrators
  "Use this method to get up to date information about the chat (current name of
  the user for one-on-one conversations, current username of a user, group or
  channel, etc.). Returns a Chat object on success."
  [chat-id])

(defn-api-method get-chat-member-count
  "Use this method to get the number of members in a chat. Returns Int on
  success."
  [chat-id])

(defn-api-method get-chat-member
  "Use this method to get information about a member of a chat. The method is
  only guaranteed to work for other users if the bot is an administrator in the
  chat. Returns a ChatMember object on success."
  [chat-id user-id])

(defn-api-method set-chat-sticker-set
  "Use this method to set a new group sticker set for a supergroup. The bot must
  be an administrator in the chat for this to work and must have the appropriate
  administrator rights. Use the field can_set_sticker_set optionally returned in
  getChat requests to check if the bot can use this method. Returns True on
  success."
  [chat-id sticker-set-name])

(defn-api-method delete-chat-sticker-set
  "Use this method to delete a group sticker set from a supergroup. The bot must
  be an administrator in the chat for this to work and must have the appropriate
  administrator rights. Use the field can_set_sticker_set optionally returned in
  getChat requests to check if the bot can use this method. Returns True on
  success."
  [chat-id])

(defn-api-method get-forum-topic-icon-stickers
  "Use this method to get custom emoji stickers, which can be used as a forum
  topic icon by any user. Requires no parameters. Returns an Array of Sticker
  objects."
  [])

(defn-api-method create-forum-topic
  "Use this method to create a topic in a forum supergroup chat. The bot must be
  an administrator in the chat for this to work and must have the
  can_manage_topics administrator rights. Returns information about the created
  topic as a ForumTopic object."
  [chat-id name])

(defn-api-method edit-forum-topic
  "Use this method to edit name and icon of a topic in a forum supergroup
  chat. The bot must be an administrator in the chat for this to work and must
  have can_manage_topics administrator rights, unless it is the creator of the
  topic. Returns True on success."
  [chat-id message-thread-id])

(defn-api-method close-forum-topic
  "Use this method to close an open topic in a forum supergroup chat. The bot
  must be an administrator in the chat for this to work and must have the
  can_manage_topics administrator rights, unless it is the creator of the
  topic. Returns True on success."
  [chat-id message-thread-id])

(defn-api-method reopen-forum-topic
  "Use this method to reopen a closed topic in a forum supergroup chat. The bot
  must be an administrator in the chat for this to work and must have the
  can_manage_topics administrator rights, unless it is the creator of the
  topic. Returns True on success."
  [chat-id message-thread-id])

(defn-api-method delete-forum-topic
  "Use this method to delete a forum topic along with all its messages in a
  forum supergroup chat. The bot must be an administrator in the chat for this
  to work and must have the can_delete_messages administrator rights. Returns
  True on success."
  [chat-id message-thread-id])

(defn-api-method unpin-all-forum-topic-messages
  "Use this method to clear the list of pinned messages in a forum topic. The
  bot must be an administrator in the chat for this to work and must have the
  can_pin_messages administrator right in the supergroup. Returns True on
  success."
  [chat-id message-thread-id])

(defn-api-method edit-general-forum-topic
  "Use this method to edit the name of the 'General' topic in a forum supergroup
  chat. The bot must be an administrator in the chat for this to work and must
  have can_manage_topics administrator rights. Returns True on success."
  [chat-id name])

(defn-api-method edit-general-forum-topic
  "Use this method to edit the name of the 'General' topic in a forum supergroup
  chat. The bot must be an administrator in the chat for this to work and must
  have can_manage_topics administrator rights. Returns True on success."
  [chat-id name])

(defn-api-method close-general-forum-topic
  "Use this method to close an open 'General' topic in a forum supergroup
  chat. The bot must be an administrator in the chat for this to work and must
  have the can_manage_topics administrator rights. Returns True on success."
  [chat-id])

(defn-api-method reopen-general-forum-topic
  "Use this method to reopen a closed 'General' topic in a forum supergroup
  chat. The bot must be an administrator in the chat for this to work and must
  have the can_manage_topics administrator rights. The topic will be
  automatically unhidden if it was hidden. Returns True on success."
  [chat-id])

(defn-api-method hide-general-forum-topic
  "Use this method to hide the 'General' topic in a forum supergroup chat. The
  bot must be an administrator in the chat for this to work and must have the
  can_manage_topics administrator rights. The topic will be automatically closed
  if it was open. Returns True on success."
  [chat-id])

(defn-api-method unhide-general-forum-topic
  "Use this method to unhide the 'General' topic in a forum supergroup chat. The
  bot must be an administrator in the chat for this to work and must have the
  can_manage_topics administrator rights. Returns True on success."
  [chat-id])

(defn-api-method unpin-all-general-forum-topic-messages
  "Use this method to clear the list of pinned messages in a General forum
  topic. The bot must be an administrator in the chat for this to work and must
  have the can_pin_messages administrator right in the supergroup. Returns True
  on success."
  [chat-id])

(defn-api-method answer-callback-query
  "Use this method to send answers to callback queries sent from inline
  keyboards. The answer will be displayed to the user as a notification at the
  top of the chat screen or as an alert. On success, True is returned."
  [callback-query-id])

(defn-api-method set-my-commands
  "Use this method to change the list of the bot's commands. See this manual for
  more details about bot commands. Returns True on success."
  [commands])

(defn-api-method delete-my-commands
  "Use this method to delete the list of the bot's commands for the given scope
  and user language. After deletion, higher level commands will be shown to
  affected users. Returns True on success."
  [])

(defn-api-method get-my-commands
  "Use this method to get the current list of the bot's commands for the given
  scope and user language. Returns an Array of BotCommand objects. If commands
  aren't set, an empty list is returned."
  [])

(defn-api-method set-my-name
  "Use this method to change the bot's name. Returns True on success."
  [])

(defn-api-method get-my-name
  "Use this method to get the current bot name for the given user
  language. Returns BotName on success."
  [])

(defn-api-method set-my-description
  "Use this method to change the bot's description, which is shown in the chat
  with the bot if the chat is empty. Returns True on success."
  [])

(defn-api-method get-my-description
  "Use this method to get the current bot description for the given user
  language. Returns BotDescription on success."
  [])

(defn-api-method set-my-short-description
  "Use this method to change the bot's short description, which is shown on the
  bot's profile page and is sent together with the link when users share the
  bot. Returns True on success."
  [])

(defn-api-method get-my-short-description
  "Use this method to get the current bot short description for the given user
  language. Returns BotShortDescription on success."
  [])

(defn-api-method set-chat-menu-button
  "Use this method to change the bot's menu button in a private chat, or the
  default menu button. Returns True on success."
  [])

(defn-api-method get-chat-menu-button
  "Use this method to get the current value of the bot's menu button in a
  private chat, or the default menu button. Returns MenuButton on success."
  [])

(defn-api-method set-my-default-administrator-rights
  "Use this method to change the default administrator rights requested by the
  bot when it's added as an administrator to groups or channels. These rights
  will be suggested to users, but they are free to modify the list before adding
  the bot. Returns True on success."
  [])

(defn-api-method get-my-default-administrator-rights
  "Use this method to get the current default administrator rights of the
  bot. Returns ChatAdministratorRights on success."
  [])

(defn-api-method edit-message-text
  "Use this method to edit text and game messages. On success, if the edited
  message is not an inline message, the edited Message is returned, otherwise
  True is returned."
  [text])

(defn-api-method edit-message-caption
  "Use this method to edit captions of messages. On success, if the edited
  message is not an inline message, the edited Message is returned, otherwise
  True is returned."
  [])

(defn-api-method edit-message-media
  "Use this method to edit animation, audio, document, photo, or video
  messages. If a message is part of a message album, then it can be edited only
  to an audio for audio albums, only to a document for document albums and to a
  photo or a video otherwise. When an inline message is edited, a new file can't
  be uploaded; use a previously uploaded file via its file_id or specify a
  URL. On success, if the edited message is not an inline message, the edited
  Message is returned, otherwise True is returned."
  [media])

(defn-api-method edit-message-live-location
  "Use this method to edit live location messages. A location can be edited
  until its live_period expires or editing is explicitly disabled by a call to
  stopMessageLiveLocation. On success, if the edited message is not an inline
  message, the edited Message is returned, otherwise True is returned."
  [latitude longitude])

(defn-api-method stop-message-live-location
  "Use this method to stop updating a live location message before live_period
  expires. On success, if the message is not an inline message, the edited
  Message is returned, otherwise True is returned."
  [])

(defn-api-method edit-message-reply-markup
  "Use this method to edit only the reply markup of messages. On success, if the
  edited message is not an inline message, the edited Message is returned,
  otherwise True is returned."
  [])

(defn-api-method stop-poll
  "Use this method to stop a poll which was sent by the bot. On success, the
  stopped Poll is returned."
  [chat-id message-id])

(defn-api-method delete-message
  "Use this method to delete a message, including service messages, with the
   following limitations:

- A message can only be deleted if it was sent less than
48 hours ago.
- Service messages about a supergroup, channel, or forum topic
creation can't be deleted.
- A dice message in a private chat can only be
deleted if it was sent more than 24 hours ago.
- Bots can delete outgoing messages in private chats, groups,
and supergroups.
- Bots can delete incoming messages in private chats.
- Bots granted can_post_messages permissions can delete outgoing messages
in channels.
- If the bot is an administrator of a group, it can delete any message
there.
- If the bot has can_delete_messages permission in a supergroup or a
channel, it can delete any message there. Returns True on success."
  [chat-id message-id])

(defn-api-method delete-messages
  "Use this method to delete multiple messages simultaneously. If some of the
  specified messages can't be found, they are skipped. Returns True on success."
  [chat-id message-ids])

(defn-api-method send-sticker
  "Use this method to send static .WEBP, animated .TGS, or video .WEBM
  stickers. On success, the sent Message is returned."
  [chat-id sticker])

(defn-api-method get-sticker-set
  "Use this method to get a sticker set. On success, a StickerSet object is
  returned."
  [name])

(defn-api-method get-custom-emoji-stickers
  "Use this method to get information about custom emoji stickers by their
  identifiers. Returns an Array of Sticker objects."
  [custom-emoji-ids])

(defn-api-method upload-sticker-file
  "Use this method to upload a file with a sticker for later use in the
  createNewStickerSet and addStickerToSet methods (the file can be used multiple
  times). Returns the uploaded File on success."
  [user-id sticker sticker-format])

(defn-api-method create-new-sticker-set
  "Use this method to create a new sticker set owned by a user. The bot will be
  able to edit the sticker set thus created. Returns True on success."
  [user-id name title stickers sticker-format])

(defn-api-method add-sticker-to-set
  "Use this method to add a new sticker to a set created by the bot. The format
  of the added sticker must match the format of the other stickers in the
  set. Emoji sticker sets can have up to 200 stickers. Animated and video
  sticker sets can have up to 50 stickers. Static sticker sets can have up to
  120 stickers. Returns True on success."
  [user-id name sticker])

(defn-api-method set-sticker-position-in-set
  "Use this method to move a sticker in a set created by the bot to a specific
  position. Returns True on success."
  [sticker position])

(defn-api-method delete-sticker-from-set
  "Use this method to delete a sticker from a set created by the bot. Returns
  True on success."
  [sticker])

(defn-api-method set-sticker-emoji-list
  "Use this method to change the list of emoji assigned to a regular or custom
  emoji sticker. The sticker must belong to a sticker set created by the
  bot. Returns True on success."
  [sticker emoji-list])


(defn-api-method set-sticker-keywords
  "Use this method to change search keywords assigned to a regular or custom
  emoji sticker. The sticker must belong to a sticker set created by the
  bot. Returns True on success."
  [sticker keywords])

(defn-api-method set-sticker-mask-position
  "Use this method to change the mask position of a mask sticker. The sticker
  must belong to a sticker set that was created by the bot. Returns True on
  success."
  [sticker mask-position])

(defn-api-method set-sticker-mask-position
  "Use this method to change the mask position of a mask sticker. The sticker
  must belong to a sticker set that was created by the bot. Returns True on
  success."
  [sticker keywords])

(defn-api-method set-sticker-set-title
  "Use this method to set the title of a created sticker set. Returns True on
  success."
  [name title])

(defn-api-method set-sticker-set-thumbnail
  "Use this method to set the thumbnail of a regular or mask sticker set. The
  format of the thumbnail file must match the format of the stickers in the
  set. Returns True on success."
  [name user-id])

(defn-api-method set-sticker-set-thumbnail
  "Use this method to set the thumbnail of a regular or mask sticker set. The
  format of the thumbnail file must match the format of the stickers in the
  set. Returns True on success."
  [name user-id])

(defn-api-method set-custom-emoji-sticker-set-thumbnail
  "Use this method to set the thumbnail of a custom emoji sticker set. Returns
  True on success."
  [name custom-emoji-id])

(defn-api-method delete-sticker-set
  "Use this method to delete a sticker set that was created by the bot. Returns
  True on success."
  [name])

(defn-api-method answer-inline-query
  "Use this method to send answers to an inline query. On success, True is
  returned."
  [inline-query-id results])

(defn-api-method answer-web-app-query
  "Use this method to set the result of an interaction with a Web App and send a
  corresponding message on behalf of the user to the chat from which the query
  originated. On success, a SentWebAppMessage object is returned."
  [web-app-query-id result])

(defn-api-method send-invoice
  "Use this method to send invoices. On success, the sent Message is returned."
  [chat-id title description payload provider-token currency prices])

(defn-api-method create-invoice-link
  "Use this method to create a link for an invoice. Returns the created invoice
  link as String on success."
  [title description payload provider-token currency prices])

(defn-api-method answer-shipping-query
  "If you sent an invoice requesting a shipping address and the parameter
  is_flexible was specified, the Bot API will send an Update with a
  shipping_query field to the bot. Use this method to reply to shipping
  queries. On success, True is returned."
  [shipping-query-id ok])

(defn-api-method answer-pre-checkout-query
  "Once the user has confirmed their payment and shipping details, the Bot API
  sends the final confirmation in the form of an Update with the field
  pre_checkout_query. Use this method to respond to such pre-checkout
  queries. On success, True is returned. Note: The Bot API must receive an
  answer within 10 seconds after the pre-checkout query was sent."
  [pre-checkout-query-id ok])

(defn-api-method send-game
  "Use this method to send a game. On success, the sent Message is returned."
  [chat-id game-short-name])

(defn-api-method set-game-score
  "Use this method to set the score of the specified user in a game message. On
  success, if the message is not an inline message, the Message is returned,
  otherwise True is returned. Returns an error, if the new score is not greater
  than the user's current score in the chat and force is False."
  [user-id score])

(defn-api-method get-game-high-scores
  "Use this method to get data for high score tables. Will return the score of
  the specified user and several of their neighbors in a game. Returns an Array
  of GameHighScore objects."
  [user-id])
