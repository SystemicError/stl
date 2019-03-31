(ns stl.model-viewer
  (:require [stl.stl :as stl]
            [quil.core :as q]
            [quil.middleware :as m]))

(defn draw-2d-triangles [triangles]
  (if (> (count triangles) 0)
    (let [t (first triangles)
          x0 (first (first t))
          y0 (nth (first t) 1)
          x1 (first (nth t 1))
          y1 (nth (nth t 1) 1)
          x2 (first (last t))
          y2 (nth (last t) 1)
	  dummy (println (str "\nt:" (into [] t)))]
      (q/with-translation [(/ (q/width) 2.0) (/ (q/height) 2.0)]
	(q/fill 255 255 255)
        (q/triangle x0 y0 x1 y1 x2 y2))
      (recur (rest triangles)))))

(defn project-point [point camera]
  "Projects a 3d point to 2d, includes z-buffer as third element."
  (let [translated (map + point (:translation camera))
        rotated (for [row (:rotation camera)] (apply + (map * row translated)))]
      ;  dummy (println (str "point" (into [] point)
      ;                      "\ntr" (into [] translated)
      ;                      "\nrot" (into [] rotated)))]
    (if (< 0.0 (last rotated))
      {:point (map #(/ (* (camera :focal-length) %) (last rotated)) (take 2 rotated))
       :depth (last rotated)})))

(defn project-triangles [triangles camera]
  "Takes in an array of 3d triangles, returns array of 2d triangles."
  (for [t triangles] (for [vertex t] (project-point vertex camera))))

(defn draw-state [state]
  "Draws the reflections onto xy plane of triangles receiving light from z-infinitiy."
  (let [triangles (:triangles state)
        projected (filter #(not= nil %) (project-triangles triangles state))
        z-buffered (sort #(< (:depth (first %1)) (:depth (first %2))) projected)]
    (q/background 0)
    ;(println (str (into [] z-buffered)))
    (draw-2d-triangles (for [t z-buffered] (for [p t] (:point p))))
    ))

(defn setup []
  {:rotation [[1.0 0.0 0.0] [0.0 1.0 0.0] [0.0 0.0 1.0]]
   :translation [0.0 1.0 1.0]
   :focal-length 100.0
   :triangles (for [i (range 16)] (for [j (range 3)] [(- (* (rand) 1000) 500) (- (* (rand) 1000) 500) (* (rand) 1000)]))
   :time 0.0
   }
  )

(defn update-state [state]
  (assoc state :translation (map + [(Math/cos (:time state)) (Math/sin (:time state)) 0.0] (:translation state))
               :time (+ (:time state) 0.01))
  )

(q/defsketch reflection
  :setup setup
  :update update-state
  :size [1024 768]
  :draw draw-state
  :middleware [m/fun-mode])
