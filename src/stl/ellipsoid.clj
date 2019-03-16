(ns stl.ellipsoid
  (:require [stl.stl :as stl]))

(defn subdivide-spherical-triangle [triangle]
  "Subdivides exactly one triangle into exactly four partitions."
  (let [a (first triangle)
        b (first (rest triangle))
        c (last triangle)
        ab (apply stl/unit-vector (map + a b))
        bc (apply stl/unit-vector (map + b c))
        ca (apply stl/unit-vector (map + c a))]
    [[a ab ca]
     [b bc ab]
     [c ca bc]
     [ab bc ca]]))

(defn subdivide-spherical-tiles [depth triangles]
  "Subdivides triangles into 4^depth partitions."
  (if (>= 0 depth)
    triangles
    (subdivide-spherical-tiles (dec depth)
                               (apply concat (map subdivide-spherical-triangle triangles)))))

(defn spherical-tiling [depth]
  "Returns a spherical tiling based on recursive partitions of the regular octahedron."
  (let [pi [1.0 0.0 0.0]
        pj [0.0 1.0 0.0]
        pk [0.0 0.0 1.0]
        ni [-1.0 0.0 0.0]
        nj [0.0 -1.0 0.0]
        nk [0.0 0.0 -1.0]
        triangles [[pi pj pk]
                   [pi pk nj]
                   [pi nj nk]
                   [pi nk pj]
                   [ni nk nj]
                   [ni nj pk]
                   [ni pk pj]
                   [ni pj nk]]]
    (subdivide-spherical-tiles depth triangles)))

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
  (let [triangles (spherical-tiling depth)
        remapper (fn [tri] (map #(ellipsoid-radius % a b c) tri))]
    (map remapper triangles)))

(defn write-elliptical-reflector [focus slr depth]
  "Writes an ellipsoid stl file."
  (let [axes (get-axes focus slr)
        a (first axes)
        b (last axes)
        triangles (ellipsoid b b a depth)
        triangles (filter #(< focus (apply max (map last %))) triangles)]
    (stl/write-stl "elliptical_reflector.stl" triangles)))
