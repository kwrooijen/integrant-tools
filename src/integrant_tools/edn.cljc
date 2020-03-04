(ns integrant-tools.edn
  (:require
   #?(:clj  [clojure.edn :as edn]
      :cljs [clojure.tools.reader.edn :as edn])
   [clojure.walk :as walk]))

(defn- tag->map [t v]
  {:reader/tag t :reader/value v})

(defn format-string [string]
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
  "TODO"
  [config]
  (binding [*print-meta* true]
    (->> config
         (walk/postwalk parse-reader)
         (pr-str))))

(defn lazy-read
  "TODO"
  ([config]
   (lazy-read {} config))
  ([readers config]
   (edn/read-string {:readers readers :default tag->map} config)))
