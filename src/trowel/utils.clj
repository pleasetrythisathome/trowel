(ns trowel.utils
  (:refer-clojure :exclude [+ - * /])
  (:require [clojure.string :as str]
            [garden.units :refer [px]]
            [garden.color :refer [hsl rgb]]
            [garden.arithmetic :refer [+ - * /]]
            [garden.stylesheet :refer [at-media]]
            [me.raynes.fs :as fs]))

(defn join-keys
  "joins a seq of keywords into a single keyword"
  [separator & keywords]
  (keyword (str/join separator (map (fn [k]
                                      (name (if (keyword? k)
                                              k
                                              (keyword (str k))))) keywords))))

(defn make-kv
  "turns a vector or sequence into a map. idempotent"
  [s]
  (cond-> s
          (sequential? s) (zipmap s)))

(defn combo
  "creates a map that has a vector of the values of each left to right combination of the passed in maps, returning a {[k k k k] [v v v v]} map"
  [& colls]
  (if-not (seq (rest colls))
    (into {} (mapv (partial mapv vector) (first colls)))
    (reduce (fn [out coll]
              (into {} (let [is-first (= out (first colls))]
                         (for [[k1 v1] out
                               [k2 v2] coll]
                           (mapv (fn [[p1 p2]]
                                   (if is-first
                                     [p1 p2]
                                     (conj p1 p2)))
                                 [[k1 k2]
                                  [v1 v2]])))))
            colls)))

(defn selectors
  [f & colls]
  (->> colls
       (map make-kv)
       (apply combo)
       (map (fn [[ks vs]]
              (let [sel (apply join-keys "" ks)
                    rules (apply f vs)]
                (if (sequential? rules)
                  (into [] (concat [sel] rules))
                  [sel rules]))))))

(defn grid
  "creates a grid with :1 being the base value
  1-sub being (/ base sub)
  up to (* base octaves)"
  ([base sub octaves] (grid base sub octaves identity))
  ([base sub octaves unit]
     (let [step (/ base sub)
           pairs (for [i (range (* octaves sub))]
                   (let [part (mod i sub)
                         key (cond-> ""
                                     (<= sub i) (str (int (Math/floor (/ i sub))))
                                     (and (< sub i)
                                          (not (zero? part))) (str "-")
                                          (not (zero? part)) (str part "-" sub)
                                          true keyword)
                         value (* step i)]
                     [key (unit value)]))]
       (drop 1 pairs))))


(defn media
  [break val body]
  (at-media {break val}
            [:&
             body]))

(defn break [platform]
  (condp = platform
    :mobile 568
    :tablet 768
    :desktop 1280))

(defn smaller
  [platform body]
  (media :max-width (px (break platform)) body))

(defn larger
  [platform body]
  (media :min-width (px (break platform)) body))

(defn filterm [f m]
  (into {} (filter (comp f second) m)))
