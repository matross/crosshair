(ns matross.crosshair
  (:require [potemkin :refer [def-map-type]]))

(defn name-ns-key
  "Retrieve the string version of the keyword, sans :"
  [k]
  (-> k str (subs 1)))

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

(defprotocol ICrosshair
  (add-ns [this ns] [this ns m]))

(def-map-type Crosshair [value ns-sep default-ns]
  (assoc [_ k v]
         (let [full-key (internal-key k ns-sep default-ns)
               new-value (assoc-in value full-key v)]
           (Crosshair. new-value ns-sep default-ns)))

  (dissoc [_ k]
          (let [[ns k] (internal-key k ns-sep default-ns)
                without-k (update-in value [ns] dissoc k)]
            (Crosshair. without-k ns-sep default-ns)))

  (get [_ k not-found]
       (let [[ns-key target-key :as full-key] (internal-key k ns-sep default-ns)]
         (get-in value full-key not-found)))

  (keys [_]
        (let [ns-ks (keys value)
              ks (mapcat (fn [ns-k]
                           (map #(external-key [ns-k %] ns-sep) (keys (get value ns-k))))
                         ns-ks)]
          ks))

  (containsKey [_ k] (not (nil? (get-in value (internal-key k ns-sep default-ns)))))

  ICrosshair
  (add-ns [this n] (add-ns this n {})) 
  (add-ns [this n m] (Crosshair. (assoc value n m) ns-sep default-ns)))

(defn crosshair
  ([m] (crosshair m "/" "default"))
  ([m ns-sep default-ns]
     (Crosshair. m ns-sep default-ns)))
