(ns integrant-tools.keyword)

(defn- set-or-nil [s]
  (when (seq s)
    (set s)))

(defn- child-keyword [tag separator]
  (let [prefix (str (name tag) separator)]
    (keyword (namespace tag) (str (gensym prefix)))))

(defn ancestor?
  "Return `true` if `tag1` is an ancestor of `tag2`, otherwise return false."
  [tag1 tag2]
  (if-let [tags (ancestors tag2)]
    (tags tag1)
    false))

(defn descendant?
  "Return `true` if `tag1` is an descendant of `tag2`, otherwise return false."
  [tag1 tag2]
  (if-let [tags (descendants tag2)]
    (tags tag1)
    false))

(defn parent?
  "Return `true` if `tag1` is an parent of `tag2`, otherwise return false."
  [tag1 tag2]
  (if-let [tags (parents tag2)]
    (tags tag1)
    false))

(defn children
  "Returns the immediate children of `tag`, either via a Java(Script) type
  inheritance relationship or a relationship established via derive. `h`
  must be a hierarchy obtained from make-hierarchy, if not supplied
  defaults to the global hierarchy"
  ([tag]
   (->> (descendants tag)
        (filter (partial parent? tag))
        (set-or-nil)))
  ([tag h]
   (->> (descendants h tag)
        (filter (partial parent? tag))
        (set-or-nil))))

(defn child?
  "Return `true` of `tag1` is an child of `tag2`, otherwise return false."
  [tag1 tag2]
  (if-let [tags (children tag2)]
    (tags tag1)
    false))

(defn parent
  "Return the first parent of `tag`."
  [tag]
  (first (parents tag)))

(defn ancestor
  "Return the first ancestor of `tag`."
  [tag]
  (first (ancestor tag)))

(defn child
  "Return the first child of `tag`."
  [tag]
  (first (children tag)))

(defn descendant
  "Return the first descendant of `tag`."
  [tag]
  (first (descendants tag)))

(defn underive-ancestors
  "Underive all `tag` of all its ancestors."
  [tag]
  (doseq [ancestor (ancestors tag)]
    (underive tag ancestor)))

(defn underive-parents
  "Underive all `tag` of all its parents."
  [tag]
  (doseq [parent (parents tag)]
    (underive tag parent)))

(defn underive-descendants
  "Underive all descendants of `tag`."
  [tag]
  (doseq [descendant (descendants tag)]
    (underive descendant tag)))

(defn underive-children
  "Underive all children of `tag`."
  [tag]
  (doseq [child (children tag)]
    (underive child tag)))

(defn make-child
  "Return a new unique keyword that derived from `tag`. This new keyword will
  have the same name as `tag` with a number affixed. An optional `separator`
  argument can be supplied, defaults to `+`"
  ([tag] (make-child tag "+"))
  ([tag separator]
   (doto (child-keyword tag separator)
     (derive tag))))
