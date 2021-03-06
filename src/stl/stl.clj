(ns stl.stl
  (:require [clojure.string :as str]))

(def pi 3.14159265358979)

(defn deg-to-rad
  "Degrees to radians."
  [deg]
  (* deg (/ pi 180.0))
  )

(defn rad-to-deg
  "Radians to degrees."
  [rad]
  (* rad (/ 180.0 pi))
  )

(defn cross-product
  "Cross product."
  [a b]
  (let [ax (first a)
        ay (first (rest a))
        az (last a)
        bx (first b)
        by (first (rest b))
        bz (last b)]
  [(- (* ay bz) (* by az))
   (- (* bx az) (* ax bz))
   (- (* ax by) (* bx ay))]))

(defn dot-product
  "Dot product."
  [a b]
  (apply + (map * a b)))

(defn magnitude
  "Magnitude of a given vector."
  [x y z]
  (Math/sqrt (+ (* x x) (* y y ) (* z z))))

(defn unit-vector
  "Unit vector of a given vector."
  [x y z]
  (let [r (magnitude x y z)]
    [(/ x r)
     (/ y r)
     (/ z r)]))

(defn unit-normal
  "Computes the normal vector to a triangle (counter-clockwise is assumed)."
  [triangle]
  (let [v0 (first triangle)
        v1 (first (rest triangle))
        v2 (last triangle)
        a (map - v1 v0)
        b (map - v2 v0)]
    (apply unit-vector (cross-product a b))))


(defn half-plane-crossing [p1 p2 h-plane]
  "Finds the intersection between the line segment p1 to p2 and the half-plane normal to h-plane.  Returns nil if none exists."
  ;; ((p2 - p1)t + p1)*h-plane = 0
  ;; ((p2 - p1)*h-plane)t + p1*h-plane = 0
  ;; where 0 < t < 1
  (let [u (map - p2 p1)
        u-dot-h (dot-product u h-plane)
        p1-dot-h (dot-product p1 h-plane)]
    (if (= 0.0 u-dot-h)
      nil
      (let [t (/ p1-dot-h u-dot-h -1.0)]
        (if (or (<= t 0.0) (>= t 1.0))
          nil
          (map + p1 (map #(* t %) u)))))))

(defn translate-triangle [tri v]
  "Translates triangle by vector v."
  (map #(map + % v) tri))

(defn clip-triangle-to-half-plane
  "Clips a triangle in arbitrary dimensions to a half-plane running through origin, specified by normal vector.  Returns a list of triangles representing clipped polygon.  Preserves orientation.  May optionally include offset of half-plane as third argument."
  ([triangle h-plane offset]
   (let [translated (translate-triangle triangle (map #(* -1.0 %) offset))
         clipped (clip-triangle-to-half-plane translated h-plane)]
     (map #(translate-triangle % offset) clipped)))
  ([triangle h-plane]
   (let [a (first triangle)
         b (nth triangle 1)
         c (last triangle)
         ab (half-plane-crossing a b h-plane)
         bc (half-plane-crossing b c h-plane)
         ca (half-plane-crossing c a h-plane)
         vertices (filter #(and (not= nil %)
                                (<= 0 (dot-product % h-plane))) [a ab b bc c ca])]
     (case (count vertices)
       0 []
       1 []
       2 []
       3 [vertices]
       4 (let [p (first vertices)
               q (nth vertices 1)
               r (nth vertices 2)
               s (last vertices)]
           [[p q r] [r s p]])
       (println "Error!")))))

(defn clip-triangles-to-2d-triangle [triangles clipper]
  "Clips triangle to within clipper, returns collection of triangles."
  (let [a (first clipper)
        b (nth clipper 1)
        c (last clipper)
        ab (map - b a)
        bc (map - c b)
        ca (map - a c)
        counter-90 (fn [v] [(* -1.0 (last v)) (first v)])
        clipped-once (apply concat (for [t triangles]
                                     (clip-triangle-to-half-plane t
                                                                  (counter-90 ab)
                                                                  a)))
        clipped-twice (apply concat (for [t clipped-once]
                                      (clip-triangle-to-half-plane t
                                                                   (counter-90 bc)
                                                                   b)))]
    (apply concat (for [t clipped-twice]
                    (clip-triangle-to-half-plane t
                                                 (counter-90 ca)
                                                 c)))))

(defn intersect-2d-triangles
  "Returns a collection of triangles representing planar intersection."
  ([triangles]
   (case (count triangles)
     0 []
     1 triangles
     (intersect-2d-triangles (rest triangles) [(first triangles)])))
  ([triangles result]
   (if (empty? triangles)
     result
     (recur (rest triangles) (clip-triangles-to-2d-triangle result (first triangles))))))

; possible additions

; binary operations on 2d/3d triangles
; transformations
;   rotation
;   arbitrary matrix operations
;   projection
; triangulation/avoiding sliver triangles
; extrusion of solids, binary operations on solids

; stl file functions

; possibly add (binary) stl reader?

(defn facet
  "Creates a facet entry from a triangle."
  [triangle]
  (let [normal (unit-normal triangle)
        nx (nth normal 0)
        ny (nth normal 1)
        nz (nth normal 2)
        a (first triangle)
        b (first (rest triangle))
        c (last triangle)
        x0 (first a)
        x1 (first b)
        x2 (first c)
        y0 (first (rest a))
        y1 (first (rest b))
        y2 (first (rest c))
        z0 (last a)
        z1 (last b)
        z2 (last c)]
    (str "  facet normal " nx " " ny " " nz "\n"
         "    outer loop\n"
         "      vertex " x0 " " y0 " " z0 "\n"
         "      vertex " x1 " " y1 " " z1 "\n"
         "      vertex " x2 " " y2 " " z2 "\n"
         "    endloop\n"
         "  endfacet\n"))
  )

(defn write-stl
  "Writes an ASCII stl file."
  [path triangles]
  (let [header "solid STL generated by Clojure\n"
        footer "endsolid vcg"
        facets (map facet triangles)]
    (spit path (str header
                    (apply str facets)
                    footer)))
  )

(defn read-ascii-stl
  "Reads an ASCII stl file."
  ;TODO ensure normal is in correct direction.
  [path]
  (let [contents (slurp path)
        lines (str/split contents #"[\n\r]")
        vertex-lines (filter #(re-find #"vertex" %) lines)
        line-to-vertices (fn [line] 
                           (map read-string
                             (filter #(and (not= "" %) (not= "vertex" %))
                                     (str/split line #" "))))
        vertices (map line-to-vertices vertex-lines)
        triangles (into [] vertices)
        ]
    triangles))
