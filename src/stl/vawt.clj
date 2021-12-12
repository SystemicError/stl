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
                              (+ (* sin (first v)) (* cos (nth v 1)))
                              slice-height))
        e (twister a)
        f (twister b)
        g (twister c)
        h (twister d)
        mirror (fn [v] (list (* -1 (first v)) (* -1 (nth v 1)) (nth v 2)))
        ]
    (list (list a b f)
          (list b c g)
          (list c d h)
          (list e a f)
          (list f b g)
          (list g c h)
          (list a (mirror b) (mirror f))
          (list (mirror b) (mirror c) (mirror g))
          (list (mirror c) (mirror d) (mirror h))
          (list e a (mirror f))
          (list (mirror f) (mirror b) (mirror g))
          (list (mirror g) (mirror c) (mirror h))
          )
  ))

(defn -main
  "Writes the specified stl file."
  [& args]
  (stl/write-stl "vawt.stl" (vawt-slice))
  )
