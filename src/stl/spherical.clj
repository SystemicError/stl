(ns stl.spherical
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

(defn hemispherical-tiling [depth]
  "Returns a hemispherical tiling based on recursive partitions of the regular octahedron."
  (let [pi [1.0 0.0 0.0]
        pj [0.0 1.0 0.0]
        pk [0.0 0.0 1.0]
        ni [-1.0 0.0 0.0]
        nj [0.0 -1.0 0.0]
        triangles [[pi pj pk]
                   [pi pk nj]
                   [ni nj pk]
                   [ni pk pj]]]
    (subdivide-spherical-tiles depth triangles)))

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
