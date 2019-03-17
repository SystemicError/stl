(ns stl.crown
  (:require [stl.stl :as stl]
            [stl.ellipsoid :as ell]))

(defn write-crown []
  "Writes a crown stl file."
  (let [a 80
        b 110
        c 50
        depth 2
        triangles (ell/ellipsoid a b c depth)
        z-check (fn [tri] (> 0 (apply min (map last tri))))
        triangles (filter z-check triangles)]
    (stl/write-stl "crown.stl" triangles)))
