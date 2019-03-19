(ns stl.stl-test
  (:require [clojure.test :refer :all]
            [stl.stl :refer :all]))

(deftest deg-to-rad-test
  (testing "Deg-to-rad fail."
    (is (= (deg-to-rad 90.0 ) (/ pi 2.0)))
    ))

(deftest rad-to-deg-test
  (testing "Rad-to-deg fail."
    (is (= (rad-to-deg (/ pi 2.0)) 90.0))
    ))

(deftest cross-product-test
  (testing "Cross product fail."
    (is (= (cross-product [1.0 0.0 0.0] [0.0 2.0 0.0]) [0.0 0.0 2.0]))
    (is (= (cross-product [0.0 1.0 0.0] [0.0 0.0 2.0]) [2.0 0.0 0.0]))
    (is (= (cross-product [0.0 0.0 1.0] [2.0 0.0 0.0]) [0.0 2.0 0.0]))
    ))

(deftest dot-product-test
  (testing "Dot product fail."
    (is (= (dot-product [1.0 2.0 3.0] [2.0 3.0 4.0]) 20.0))
    ))

(deftest magnitude-test
  (testing "Magnitude test."
    (is (= (magnitude 9.0 12.0 20.0) 25.0))
    ))

(deftest unit-vector-test
  (testing "Unit vector fail."
    (is (= (unit-vector 9.0 0.0 0.0) [1.0 0.0 0.0]))
    (is (= (unit-vector 0.0 9.0 0.0) [0.0 1.0 0.0]))
    (is (= (unit-vector 0.0 0.0 9.0) [0.0 0.0 1.0]))
    ))

(deftest unit-normal-test
  (testing "Unit normal fail."
    (is (= (unit-normal [[1.0 1.0 1.0] [2.0 1.0 1.0] [1.0 3.0 1.0]]) [0.0 0.0 1.0]))
    ))

(deftest half-plane-crossing-test
  (testing "Half plane crossing fail."
    (is (= (half-plane-crossing [0.0 1.0] [1.0 -1.0] [0.0 1.0]) [0.5 0.0]))
    (is (= (half-plane-crossing [0.0 1.0 1.0] [1.0 -1.0 0.0] [0.0 1.0 0.0]) [0.5 0.0 0.5]))
    (is (= (half-plane-crossing [0.0 1.0 1.0] [1.0 1.0 0.0] [0.0 1.0 0.0]) nil))
    ))

(deftest clip-triangle-to-half-plane-test
  (testing "Clip triangle to half plane fail."
    (is (= (clip-triangle-to-half-plane [[0.0 0.0] [1.0 1.0] [1.0 -1.0]] [-1.0 0.0])
           []))
    (is (= (clip-triangle-to-half-plane [[0.0 0.0] [1.0 1.0] [1.0 -1.0]] [1.0 0.0])
           [[[0.0 0.0] [1.0 1.0] [1.0 -1.0]]]))
    (is (= (clip-triangle-to-half-plane [[0.0 0.0] [1.0 1.0] [1.0 -1.0]] [0.0 1.0])
           [[[0.0 0.0] [1.0 1.0] [1.0 0.0]]]))
    (is (= (clip-triangle-to-half-plane [[0.0 -1.0] [1.0 1.0] [0.0 1.0]] [0.0 1.0])
           [[[0.5 0.0] [1.0 1.0] [0.0 1.0]]
            [[0.0 1.0] [0.0 0.0] [0.5 0.0]]]))
    ))

(deftest facet-test
  (testing "Facet fail."
    (is (= (facet [[0.0 0.0 0.0] [1.0 0.0 0.0] [0.0 1.0 0.0]])
           (str "  facet normal 0.0 0.0 1.0\n"
                "    outer loop\n"
                "      vertex 0.0 0.0 0.0\n"
                "      vertex 1.0 0.0 0.0\n"
                "      vertex 0.0 1.0 0.0\n"
                "    endloop\n"
                "  endfacet\n")))
    ))
