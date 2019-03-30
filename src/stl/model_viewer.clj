(ns stl.model-viewer
  (:require [stl.stl :as stl]
            [quil.core :as q]
            [quil.middleware :as m]))

(defn draw-2d-triangles [triangles]
  (if (> (count triangles) 0)
    (let [t (first triangles)
          x0 (first (first t))
          y0 (last (first t))
          x1 (first (nth t 1))
          y1 (last (nth t 1))
          x2 (first (last t))
          y2 (last (last t))]
      (q/with-translation [(/ (q/width) 2.0) (/ (q/height) 2.0)]
        (q/triangle x0 y0 x1 y1 x2 y2))
      (recur (rest triangles)))))

(defn project-point [point camera]
  "Projects a 3d point to 2d."
  (let [translated (map + point (:translation camera))
        rotated (for [row (:rotation camera)] (map * row translated))]
    (if (< 0.0 (last rotated))
      (map #(/ % (last rotated)) (take 2 rotated)))))

(defn project-triangles [triangles camera]
  "Takes in an array of 3d triangles, returns array of 2d triangles."
  (for [t triangles] (for [vertex t] (project-point vertex camera))))

(defn draw-state [state]
  "Draws the reflections onto xy plane of triangles receiving light from z-infinitiy."
  (let [triangles (:triangles state)]
    (q/background 0)
    (draw-2d-triangles (filter #(not= nil %) (project-triangles triangles state)))
    ))

(defn setup []
  {:rotation [[1.0 0.0 0.0] [0.0 1.0 0.0] [0.0 0.0 1.0]]
   :translation [0.0 1.0 19.0]
   :focal-length 1.0
   :triangles (for [i (range 10)] (for [j (range 3)] (for [k (range 3)] (* (rand) 100))))
   }
  )

(defn update-state [state]
  (assoc state :z (+ 0.1 (:z state)))
  )

(q/defsketch reflection
  :setup setup
  :update update-state
  :size [1024 768]
  :draw draw-state
  :middleware [m/fun-mode])
