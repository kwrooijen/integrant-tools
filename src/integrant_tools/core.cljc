(ns integrant-tools.core
  (:require
   [integrant.core :as ig]))

(defn- ->coll [k]
  (if (coll? k) k [k]))

(defn- implemented-method?
  [multi-method k]
  (contains? (methods multi-method) k))

(defn- derive-unknown* [multi-method new-key acc ks]
  (if (some (partial implemented-method? multi-method) ks)
    acc
    (do (derive (first ks) new-key)
        (conj acc (first ks)))))

(defn- meta-value?
  [v]
  #?(:clj  (instance? clojure.lang.IObj v)
     :cljs (satisfies? IMeta v)))

(defn- meta-init-key [k opts]
  (let [v (ig/init-key k opts)]
    (if (meta-value? v)
      (vary-meta v merge (meta opts))
      v)))

(def ^{:doc "TODO"}
  readers
  {'it/regex re-pattern
   'it/str (partial apply str)
   'it/fn #(fn [] %)})

(defmethod ig/init-key :it/const [_ opts] opts)

(defn derive-unknown
  "TODO"
  [config multi-method new-key]
  (->> config
       (keys)
       (map ->coll)
       (reduce (partial derive-unknown* multi-method new-key) [])))

(defn derive-hierarchy!
  "TODO"
  [hierarchy]
  (doseq [[tag parents] hierarchy
          parent parents]
    (derive tag parent)))

(defn meta-init
  "TODO"
  ([config]
   (meta-init config (keys config)))
  ([config keys]
   {:pre [(map? config)]}
   (ig/build config keys meta-init-key #'ig/assert-pre-init-spec)))
