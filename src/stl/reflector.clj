(ns stl.reflector
  (:require [stl.stl :as stl]
            [stl.ellipsoid :as ell]))

(defn write-elliptical-reflector [focus slr depth]
  "Writes an ellipsoid stl file."
  (let [axes (ell/get-axes focus slr)
        a (first axes)
        b (last axes)
        triangles (ell/ellipsoid b b a depth)
        triangles (filter #(< focus (apply max (map last %))) triangles)]
    (stl/write-stl "elliptical_reflector.stl" triangles)))
