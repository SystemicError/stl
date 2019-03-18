(ns stl.ellipsoid
  (:require [stl.stl :as stl]
            [stl.spherical :as sph]))

(defn ellipsoid-radius [v a b c]
  "Takes in a vector, scales it to lie on ellipsoid"
  (let [x (first v)
        y (nth v 1)
        z (last v)
        t (Math/pow (+ (/ (* x x) (* a a))
                       (/ (* y y) (* b b))
                       (/ (* z z) (* c c))) -0.5)]
    (map #(* t %) v)))

(defn get-axes [focus slr]
  "Calculates the semimajor/minor axes bases on focus and semilatus rectum."
  (let [a (/ (+ slr (Math/sqrt (+ (* slr slr) (* 4 focus focus)))) 2.0)
        b (Math/sqrt (- (* a a) (* focus focus)))]
    [a b]))

(defn ellipsoid [a b c depth]
  "Creates an ellipsoid with specified semi-minor/major/medi axes."
  (let [triangles (sph/spherical-tiling depth)
        remapper (fn [tri] (map #(ellipsoid-radius % a b c) tri))]
    (map remapper triangles)))
