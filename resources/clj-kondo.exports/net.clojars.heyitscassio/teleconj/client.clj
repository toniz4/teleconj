(ns client)

(defmacro defn-api-method [method-sym doc args]
  `(defn ~method-sym
     ~doc
     [~'token ~@args & ~'params]
     [~'token ~@args ~'params]))
