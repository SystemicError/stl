(ns stl.paraboloid
  (:require [stl.stl :as stl]
            [stl.spherical :as sph]))

(defn paraboloid-radius [v focus]
  "Takes in a vector, scales it to lie on (circular) paraboloid"
  ; the parabola in question is z = 1/(4f)*r^2 - f ; this guarantees origin at focus
  (let [x (first v)
        y (nth v 1)
        z (last v)
        r2 (+ (* x x) (* y y))
        t (if (= 0.0 r2)
            (* -1.0 focus)
            (/ (* 2.0 focus (- z 1.0)) r2))]
    (map #(* t %) v)))

(defn paraboloid [focus depth]
  "Creates an paraboloid with specified focal length."
  (let [triangles (sph/hemispherical-tiling depth)
        remapper (fn [tri] (map #(paraboloid-radius % focus) tri))]
    (map remapper triangles)))
