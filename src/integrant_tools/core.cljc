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
    (do (derive (last ks) new-key)
        (conj acc (last ks)))))

(defn- meta-value?
  [v]
  #?(:clj  (instance? clojure.lang.IObj v)
     :cljs (satisfies? IMeta v)))

(defn- meta-init-key [k opts]
  (let [v (ig/init-key k opts)]
    (if (meta-value? v)
      (vary-meta v merge (meta opts))
      v)))

(def ^{:doc "Useful Integrant readers

## `it/regex`
  Convert a string to a regex

```clojure
{:regex/email? #it/regex \".+\\@.+\\..+\"
 ...}
```

## `it/str`
  Convert a collection of strings into a single string

```clojure
{:lotr/quote #it/str
 [\"One ring to rule them all,\"
  \"One ring to find them,\"
  \"One ring to bring them all and in the darkness bind them\"]
 ...}
```"}
  readers
  {'it/regex re-pattern
   'it/str (partial apply str)})

(defmethod ig/init-key :it/const [_ opts] opts)

(defn derive-unknown
  "Derives any keys in `config` that aren't implemented in `multi-method` with
  `new-key`. Any keys that are derived using this function will be returned in
  a vector."
  [config multi-method new-key]
  (->> config
       (keys)
       (map ->coll)
       (reduce (partial derive-unknown* multi-method new-key) [])))

(defn derive-hierarchy
  "Derive keys using a hierarchy structure.

  For example:

  ```clojure
  (it/derive-hierarchy
   {:entity/thranduil [:race/elf]
    :entity/legolas   [:race/elf]
    :entity/aragorn   [:race/human]})

  Is equivalent to calling:

  (derive :entity/thranduil :race/elf)
  (derive :entity/legolas   :race/elf)
  (derive :entity/aragorn   :race/human)
  ```"
  [hierarchy]
  (doseq [[tag parents] hierarchy
          parent parents]
    (derive tag parent)))

(defn underive-all
  "Underives all keys from `config` of their parents. This is useful if you've
  manually used `derive` on keys within your config and need to remove them to
  prevent ambiguous init-keys."
  [config]
  (doseq [key (-> config keys flatten distinct)
          parent (parents key)]
    (underive key parent)))

(defn find-derived-keys
  "Return all keys in `config` that are derived from `k`."
  [config k]
  (->> (ig/find-derived config k)
       (mapv first)))

(defn find-derived-key
  "Return the first key in `config` that is derived from `k`."
  [config k]
  (->> (ig/find-derived config k)
       (mapv first)))

(defn find-derived-values
  "Return all values of keys in `config` that are derived from `k`."
  [config k]
  (->> (ig/find-derived config k)
       (mapv last)))

(defn find-derived-value
  "Return the value of the first key in `config` that is derived from `k`."
  [config k]
  (->> (ig/find-derived config k)
       (first)
       (last)))

(defn meta-init
  "Same as ig/init, but any metadata in a key's `opts` are merged into the
  resulting value after initialization. This is useful if your init-key returns
  a function, but you want to add extra context to it."
  ([config]
   (meta-init config (keys config)))
  ([config keys]
   {:pre [(map? config)]}
   (ig/build config keys meta-init-key #'ig/assert-pre-init-spec)))
