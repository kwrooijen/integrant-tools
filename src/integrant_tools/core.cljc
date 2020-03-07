(ns integrant-tools.core
  (:refer-clojure :exclude [select-keys])
  (:require
   [clojure.walk :as walk]
   [integrant.core :as ig]
   [integrant-tools.keyword :as it.keyword]))

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

(defmulti prep-meta-ref
  "TODO"
  (comp first keys meta))

(defmulti prep-meta-config
  "TODO"
  (fn [_config ref]
    (-> ref meta keys first)))

(defmulti prep-fn
  "TODO"
  identity)

(defmulti init-fn
  "TODO"
  identity)

(defn child-ref
  "TODO"
  [key]
  (with-meta (ig/ref key) {:ref/child true}))



(def ^:private meta-ref?
  (every-pred ig/ref? meta))

(def ^:private modify-meta-refs
  (partial walk/postwalk
           #(cond-> % (meta-ref? %) prep-meta-ref)))

(defn- reduce-meta-config [config]
  (reduce prep-meta-config
          config
          (#'ig/depth-search meta-ref? config)))

(defn- reduce-init-fns [init-fns k opts]
  (reduce #(init-fn %2 k %1) opts init-fns))

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
   'it/str (partial apply str)
   'it/child-ref child-ref})

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
  ```
  Is equivalent to calling:

  ```clojure
  (derive :entity/thranduil :race/elf)
  (derive :entity/legolas   :race/elf)
  (derive :entity/aragorn   :race/human)
  ```"
  [hierarchy]
  (doseq [[tag parents] hierarchy
          parent parents]
    (derive tag parent)))

(defn derive-composite
  "Derives a the keys of the composite key `k` from left to right.

  For exmaple:

  ```clojure
  (it/derive-composite [:race/human :entity/aragorn :aragorn/age])
  ```

  is equivalent to calling:

  ```clojure
  (derive :entity/aragorn :race/human)
  (derive :aragorn/age :entity/aragorn)
  ```"
  [k]
  (reduce #(do (derive %2 %1) %2) k))

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
       (ffirst)))

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

(defn select-keys
  "Select all keys from `config` that are a dependency if `keys`"
  [config keys]
  (->> (#'ig/dependent-keys config keys)
       #?(:cljs (cljs.core/select-keys config)
          :clj  (clojure.core/select-keys config))))

(defn prep
  "TODO"
  ([config prep-fns] (prep config prep-fns (keys config)))
  ([config prep-fns keys]
   (reduce #(prep-fn %2 %1) (select-keys config keys) prep-fns)))

(defmethod prep-fn :it/prep-meta [_ config]
   (-> config
       (modify-meta-refs)
       (reduce-meta-config)))

(defmethod prep-fn :ig/prep [_ config]
  (ig/prep config))

(defn init
  "TODO"
  ([config init-fns]
   (init config init-fns (keys config)))
  ([config init-fns keys]
   {:pre [(map? config)]}
   (ig/build config keys
             (partial reduce-init-fns init-fns)
             #'ig/assert-pre-init-spec)))

(defmethod init-fn :it/meta-init [_ k opts]
  (let [v (ig/init-key k opts)]
    (if (meta-value? v)
      (vary-meta v merge (meta opts))
      v)))

(defmethod init-fn :ig/init [_ k opts]
  (ig/init-key k opts))

(defmethod prep-meta-ref :ref/child [ref]
  (update ref :key it.keyword/make-child))

(defmethod prep-meta-config :ref/child [config {:keys [key]}]
  (let [[[child-key child-value]] (ig/find-derived config (it.keyword/parent key))
        child-key (conj child-key key)]
    (assoc config child-key child-value)))
