(ns matross.crosshair
  (:import clojure.lang.ILookup
           clojure.lang.Seqable
           clojure.lang.SeqIterator
           clojure.lang.IPersistentCollection
           clojure.lang.IPersistentMap
           clojure.lang.Associative
           clojure.lang.MapEntry))

(declare crosshair)

(defn name-ns-key [k]
  (->> k str (drop 1) (apply str)))

(defn- internal-key [k ns-sep default-ns]
  (let [ks (name-ns-key k)
        ns-pos (. ks indexOf ns-sep)]
    (if (= ns-pos -1)
      (map keyword [default-ns k])
      (map keyword (clojure.string/split ks (re-pattern ns-sep) 2)))))

(defn- external-key [k ns-sep]
  (->> (map name-ns-key k)
       (clojure.string/join ns-sep)
       keyword))

(deftype Crosshair [value ns-sep default-ns]
  ILookup
  (valAt [this k] (. this valAt k nil))
  (valAt [this k not-found]
    (let [[ns-key target-key :as full-key] (internal-key k ns-sep default-ns)]
      (get-in value full-key not-found)))

  IPersistentCollection
  (cons [this o]
    (let [[ns-key target-key] (internal-key (key o) ns-sep default-ns)]
      (Crosshair. (update-in value [ns-key] conj (MapEntry. target-key (val o)))
                       ns-sep
                       default-ns)))

  (empty [this] (Crosshair. {} ns-sep default-ns))
  (equiv [this o] (= (seq this) (seq o)))
  (count [this] (apply + (map #(count (val %1)) value)))

  IPersistentMap
  (assoc [this k v]
    (let [full-key (internal-key k ns-sep default-ns)
          new-value (assoc-in value full-key v)]
      (crosshair new-value ns-sep default-ns)))

  (assocEx [this k v]
    (let [[ns _] (internal-key k ns-sep default-ns)]
      (if (contains? k (ns value))
        (throw (IllegalArgumentException. (str "Already contains key: " k)))
        (.assoc this k v))))

  (without [this k]
    (let [[ns k] (internal-key k ns-sep default-ns)
          without-k (update-in value [ns] dissoc k)]
      (crosshair without-k ns-sep default-ns)))

  Associative
  (containsKey [this k]
    (let [[ns-key target-key :as full-key] (internal-key k ns-sep default-ns)]
      (contains? (ns-key value) target-key)))

  (entryAt [this k]
    (if (.containsKey this k)
      (let [full-key (internal-key k ns-sep default-ns)]
        (MapEntry.
         (external-key full-key ns-sep)
         (get-in value full-key)))))

  Seqable
  (seq [this]
    (letfn [(ns-entry [ns-key [curr-key curr-val]]
              (let [ns-curr-key (external-key [ns-key curr-key] ns-sep)]
                (MapEntry. ns-curr-key curr-val)))
            (ns-maps [[ns-key m]] (map (partial ns-entry ns-key) m))]
      (mapcat ns-maps value)))

  Iterable
  (iterator [this] (SeqIterator. (. this seq))))

(defn crosshair
  ([m] (crosshair m "/" "default"))
  ([m ns-sep default-ns]
     (Crosshair. m ns-sep default-ns)))
