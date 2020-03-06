(ns integrant-tools.edn
  (:require
   #?(:clj  [clojure.edn :as edn]
      :cljs [clojure.tools.reader.edn :as edn])
   [clojure.walk :as walk]))

(defn- tag->map [t v]
  {:reader/tag t :reader/value v})

(defn- format-string [string]
  (str "\"" string "\""))

(defn- format-value [v]
  (cond-> v
    (string? v) format-string))

(defn- reader-map->tag [{:reader/keys [tag value]}]
  (->> value
       (format-value)
       (str "#" tag " ")
       (symbol)))

(defn- reader-map? [{:reader/keys [tag value]}]
  (and tag value))

(defn- parse-reader [v]
  (cond-> v
    (reader-map? v) reader-map->tag))

(defn meta-str
  "Convert a lazily read EDN structure into a string, adding the meta data to
  the string as well."
  [config]
  (binding [*print-meta* true]
    (->> config
         (walk/postwalk parse-reader)
         (pr-str))))

(defn lazy-read
  "Reads the EDN string `s`, but doesn't evaluate any readers tags except the
  ones supplied in `readers`. Instead of evaluating them they are converted to
  a map. This is useful if you want read multiple config files, merge them, and
  write them back to a string, without losing the reader tags.

  For example:

  ```clojure
  (it.edn/lazy-read \"{:lotr/quote #it/str [...]}\")
  ```

  Is read to:

  ```clojure
  {:lotr/quote {:reader/tag 'it/str :reader/value [...]}}
  ```

  Which can then later be written to a string using `meta-str`."
  ([s]
   (lazy-read {} s))
  ([readers s]
   (edn/read-string {:readers readers :default tag->map} s)))
