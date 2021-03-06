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
          y2 (last (last image))
          alpha (* 128 (:light-coeff t))]
      (q/with-translation [(/ (q/width) 2.0) (/ (q/height) 2.0)]
        (q/fill 255 255 255 alpha)
        (q/triangle x0 y0 x1 y1 x2 y2))
      (recur (rest triangles)))))

(defn draw-state [state]
  "Draws the reflections onto xy plane of triangles receiving light from z-infinitiy."
  (let [path (:path state)
        triangles (:triangles state)]
    (q/background 0)
    (draw-triangles triangles)
    ;(q/save (str path (:time state) ".png"))
    (q/save (str path ".png"))
    ))

(defn setup []
  (q/no-stroke)
  {:time 0}
  )

(defn update-state [state]
  (let [paraboloid (para/paraboloid 80 2);(int (Math/floor (state :time))))
        ;offset (* 80.0 (Math/sin (/ (:time state) 100.0)))
        offset 0
        point-shifter (fn [p] [(first p) (nth p 1) (+ (last p) offset)])
        tri-shifter (fn [tri] (map point-shifter tri))
        paraboloid (map tri-shifter paraboloid)
        ]
    {:time (+ 1 (:time state))
     :path "reflection"
     :triangles (map refl/reflected-ray paraboloid)}
    ))

(q/defsketch reflection
  :setup setup
  :update update-state
  :size [1024 1024]
  :draw draw-state
  :middleware [m/fun-mode])
