(ns stl.reflection-sketch
  (:require [stl.stl :as stl]
            [stl.paraboloid :as para]
            [stl.reflector :as refl]
            [quil.core :as q]
            [quil.middleware :as m]))

(defn draw-triangles [triangles]
  (if (> (count triangles) 0)
    (let [t (first triangles)
          image (:image t)
          x0 (first (first image))
          y0 (last (first image))
          x1 (first (nth image 1))
          y1 (last (nth image 1))
          x2 (first (last image))
          y2 (last (last image))]
      (q/with-translation [(/ (q/width) 2.0) (/ (q/height) 2.0)]
        (q/scale 2.0)
        (q/triangle x0 y0 x1 y1 x2 y2)
        (q/scale 0.5))
      (recur (rest triangles)))))

(defn draw-state [state]
  "Draws the reflections onto xy plane of triangles receiving light from z-infinitiy."
  (let [path (:path state)
        triangles (:triangles state)]
    (q/background 0)
    (q/fill 255 255 255 10)
    (draw-triangles triangles)
    ;(q/save path)
    ))

(defn setup []
  (q/no-stroke)
  {:time 0}
  )

(defn update-state [state]
  {:time (+ 1 (:time state))
   :path "reflection.png"
   :triangles (map refl/reflected-ray (para/paraboloid 40 2))}
  )

(q/defsketch reflection
  :setup setup
  :update update-state
  :size [768 768]
  :draw draw-state
  :middleware [m/fun-mode])
