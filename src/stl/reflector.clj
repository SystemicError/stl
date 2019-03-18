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
