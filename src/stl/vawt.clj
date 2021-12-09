(ns stl.vawt
  (:require [stl.stl :as stl]))

(defn vawt-slice
  "Creates a lantern gore."
  []
  (let [twist-per-slice (stl/deg-to-rad 30.0)
        cos (Math/cos twist-per-slice)
        sin (Math/sin twist-per-slice)
        slice-height 5.0
        ; profile of slice
        a (list 0 0 0)
        b (list 4 3 0)
        c (list 7 3 0)
        d (list 8 0 0)
        twister (fn [v] (list (- (* cos (first v)) (* sin (nth v 1)))
                              (+ (* sin (first v)) (* sin (nth v 1)))
                              (nth v 2)))
        e (twister a)
        f (twister b)
        g (twister c)
        h (twister d)
        ]
    (list (list a b e)
          (list b c f)
          (list c d g)
          (list e b f)
          (list f c g)
          (list g d h))
  ))

(defn -main
  "Writes the specified stl file."
  [& args]
  (stl/write-stl "vawt.stl" (vawt-slice))
  )
