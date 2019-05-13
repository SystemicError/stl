(ns stl.flying-wing
  (:require [stl.stl :as stl]
            [clojure.string :as str]))

(defn read-aerofoil [path]
  "Reads in a path to an aerofoil file and returns two sorted lists, one for upper and one for lower surface."
  (let [text (slurp path)
        lines (str/split text #"[\n\r]")
        coords (for [line lines] (map read-string (str/split line #" ")))
        upper (sort #(< (first %1) (first %2)) (filter #(<= 0.0 (last %)) coords))
        lower (sort #(< (first %1) (first %2)) (filter #(>= 0.0 (last %)) coords))]
    [upper lower])
  )

(defn transform-aerofoil [foil offset chord]
  "Translates a set of points in a foil by offset, scales by chord."
  (map 
    (fn [coords] [(+ (* chord (first coords)) offset) (* chord (last coords))])
    foil))

(defn flying-wing [wingspan backsweep max-chord min-chord lateral-step path]
  "Creates a flying wing with given properties."
  (let [wingspan-steps (/ wingspan 2.0 lateral-step)
        xs (range 0.0 (/ wingspan 2.0) lateral-step)
        y-offsets (map #(* (Math/tan backsweep) 1.0 %) xs)
        chords (range max-chord min-chord (/ (- min-chord max-chord) (count xs)))
        surfaces (read-aerofoil path)
        upper (first surfaces)
        lower (last surfaces)
        dummy (println (str "xs: " xs "\n"
                            "wsteps" wingspan-steps "\n"
                            "yoff" (into [] y-offsets) "\n"))
        foil-slice (fn [wing-index foil] (apply concat
                                           (for [i (range (dec (count foil)))]
                                             (let [inner (transform-aerofoil foil (nth y-offsets wing-index) (nth chords wing-index))
                                                   outer (transform-aerofoil foil (nth y-offsets (inc wing-index)) (nth chords (inc wing-index)))
                                                   a (cons (nth xs wing-index) (nth inner i))
                                                   b (cons (nth xs wing-index) (nth inner (inc i)))
                                                   c (cons (nth xs (inc wing-index)) (nth outer (inc i)))
                                                   d (cons (nth xs (inc wing-index)) (nth outer i))]
                                               [[a b c] [c d a]]))))
        wing (apply concat (for [wing-index (range (dec (count xs)))]
                             (concat (foil-slice wing-index upper)
                                     (foil-slice wing-index lower))))
        mirror-wing (map reverse (map (fn [triangle] (for [vertex triangle] (map * vertex [-1.0 1.0 1.0]))) wing))]
    (concat wing mirror-wing)))
