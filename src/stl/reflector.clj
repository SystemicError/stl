(ns stl.reflector
  (:require [stl.stl :as stl]
            [stl.ellipsoid :as ell]
            [stl.paraboloid :as para]))

(defn write-elliptical-reflector [focus slr depth]
  "Writes an elliptical reflector stl file."
  (let [axes (ell/get-axes focus slr)
        a (first axes)
        b (last axes)
        triangles (ell/ellipsoid b b a depth)
        triangles (filter #(< focus (apply max (map last %))) triangles)]
    (stl/write-stl "elliptical_reflector.stl" triangles)))

(defn write-parabolic-reflector [focus depth]
  "Writes a parabolic reflector stl file."
  (let [triangles (para/paraboloid focus depth)]
    (stl/write-stl "parabolic_reflector.stl" triangles)))

(defn xy-intersection [beam point]
  "Finds the intersection with the x-y plane of a line through point parallel to unit vector beam."
  (let [bx (first beam)
        by (nth beam 1)
        bz (last beam)
        px (first point)
        py (nth point 1)
        pz (last point)
        t (/ (* -1.0 pz) bz)
        x (+ px (* t bx))
        y (+ py (* t by))]
    [x y]))

(defn reflected-ray [triangle]
  "Returns a 2d triangle representing where this triangle would reflect a light from the positive z direction onto the x-y plane, and a lighting coefficient."
  (let [n (stl/unit-normal triangle)
        nx (first n)
        ny (nth n 1)
        nz (last n)
        beam-x (* 2.0 nx nz)
        beam-y (* 2.0 ny nz)
        beam-z (- (* nz nz) (* nx nx) (* ny ny))
        beam [beam-x beam-y beam-z]
        image (map #(xy-intersection beam %) triangle)
        light-coeff beam-z
        dummy (println (str "nx ny nz" nx ", " ny ", " nz "\n"
                            "bx by bz" beam-x ", " beam-y ", " beam-z "\n"))
                         ]
    {:image image :light-coeff light-coeff}))

