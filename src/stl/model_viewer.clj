(ns stl.model-viewer
  (:require [stl.stl :as stl]
            [clojure.edn :as edn]
            [quil.core :as q]
            [quil.middleware :as m]))

(defn draw-2d-triangles [triangles]
  (if (> (count triangles) 0)
    (let [tri (first triangles)
          t (:points tri)
          x0 (first (first t))
          y0 (nth (first t) 1)
          x1 (first (nth t 1))
          y1 (nth (nth t 1) 1)
          x2 (first (last t))
          y2 (nth (last t) 1)]
	  ;dummy (println (str "\nt:" (into [] t)))]
      (q/with-translation [(/ (q/width) 2.0) (/ (q/height) 2.0)]
	(apply q/fill (:color tri))
        (q/triangle x0 y0 x1 y1 x2 y2))
      (recur (rest triangles)))))

(defn project-point [point camera]
  "Projects a 3d point to 2d, includes z-buffer as third element."
  (let [translated (map + point (:translation camera))
        rotated (for [row (:rotation camera)] (apply + (map * row translated)))]
      ;  dummy (println (str "point" (into [] point)
      ;                      "\ntr" (into [] translated)
      ;                      "\nrot" (into [] rotated)))]
    {:point (map #(/ (* (camera :focal-length) %) (+ (last rotated) (camera :focal-length))) (take 2 rotated))
     :depth (last rotated)}))

(defn normal-to-color [normal]
  "Converts a unit normal vector to a color."
  (map #(+ (* % 100.0) 155.0) normal))

(defn project-triangles [triangles camera]
  "Takes in an array of 3d triangles, returns array of 2d triangles."
  (for [t triangles] {:points (for [vertex t] (project-point vertex camera))
                      :color (normal-to-color (stl/unit-normal t)) }))

(defn draw-state [state]
  "Draws the reflections onto xy plane of triangles receiving light from z-infinitiy."
  (let [triangles (:triangles state)
        projected (filter #(and (not= nil %)
                                (not= nil (first (:points %)))
                                (not= nil (nth (:points %) 1))
                                (not= nil (last (:points %))))
                          (project-triangles triangles state))
        avg-depth (fn [t] (/ (apply + (map :depth t)) 3.0))
        z-buffered (sort #(< (avg-depth (:points %1)) (avg-depth (:points %2))) projected)]
    (q/background 0)
    (draw-2d-triangles (for [t z-buffered] {:points (for [p (:points t)] (:point p))
                                            :color (:color t)}))
    ))

(defn setup []
  {:rotation [[1.0 0.0 0.0] [0.0 1.0 0.0] [0.0 0.0 1.0]]
   :translation [0.0 0.0 0.0]
   :focal-length 2048.0
   :triangles (edn/read-string (slurp "model.triangles"))
   }
  )

(defn update-state [state]
  (let [pressed (q/key-as-keyword)
        dangle 0.02
        c (Math/cos dangle)
        s (Math/sin dangle)
        rot (:rotation state)
        drot (case pressed
               :j [[1.0 0.0 0.0] [0.0 c (* -1.0 s)] [0.0 s c]]
               :k [[1.0 0.0 0.0] [0.0 c s] [0.0 (* -1.0 s) c]]
               :h [[c 0.0 (* -1.0 s)] [0.0 1.0 0.0] [s 0.0 c]]
               :l [[c 0.0 s] [0.0 1.0 0.0] [(* -1.0 s) 0.0 c]]
               [[1.0 0.0 0.0]
                [0.0 1.0 0.0]
                [0.0 0.0 1.0]])]
    (assoc state :rotation (for [row (range 3)]
                             (for [col (range 3)]
                               (apply + (map * (nth drot row)
                                               (map #(nth % col) rot))))))))

(q/defsketch reflection
  :setup setup
  :update update-state
  :size [1024 768]
  :draw draw-state
  :middleware [m/fun-mode])
