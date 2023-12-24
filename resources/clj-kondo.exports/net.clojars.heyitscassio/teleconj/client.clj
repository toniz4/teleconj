(ns client)

(defmacro defn-api-method [method-sym args]
  `(defn ~method-sym [~'token ~@args & ~'params]
     [~'token ~@args ~'params]))
