(ns stl.lantern
  (:require [stl.stl :as stl]))

(defn profile-r
  "Latitude to cylindrical radius."
  [lat]
  ;(* 30.0 (Math/pow (Math/cos lat) (if (< lat 0) 1.0 0.4))) ; floating lantern
  (* 50.0 (Math/cos lat)) ; simple oblate spheroid/灯笼
  )

(defn profile-h
  "Latitude to cylindrical height/z."
  [lat]
  (* 30.0 1.4 (Math/sin lat))
  )

(defn sphere-panel
  "Creates a quadrilateral as a pair of triangles, unless the panel is at the pole, in which case it's a single triangle."
  [fr fh lat dlat dlon]
  (let [r0 (fr lat)
        r1 (fr (+ lat dlat))
        h0 (fh lat)
        h1 (fh (+ lat dlat))
        p [r0 0.0 h0]
        q [(* r0 (Math/cos dlon)) (* r0 (Math/sin dlon)) h0]
        r [(* r1 (Math/cos dlon)) (* r1 (Math/sin dlon)) h1]
        s [r1 0.0 h1]]
    (if (< (stl/deg-to-rad 90.0) (+ lat dlat))
      ; only one triangle
      [[p q [0.0 0.0 (fh (stl/deg-to-rad 90.0))]]]
      ; two triangles
      [[p q r] [p r s]]
    ))
  )

(defn lantern-gore
  "Creates a lantern gore."
  []
  (let [dlat (stl/deg-to-rad 15.0)]
    (apply concat (for [lat (range (stl/deg-to-rad -65.0)
                                   (stl/deg-to-rad 90.0)
                                   dlat)]
                    (sphere-panel profile-r
                                  profile-h
                                  lat
                                  dlat
                                  (stl/deg-to-rad 60.0)))))
  )

(defn -main
  "Writes the specified stl file."
  [& args]
  (stl/write-stl "lantern.stl" (lantern-gore))
  )
