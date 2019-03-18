(ns stl.reflector-test
  (:require [clojure.test :refer :all]
            [stl.reflector :refer :all]))

(deftest xy-intersection-test
  (testing "xy-intesrection fail."
    (is (= (xy-intersection [0.0 0.0 1.0] [1.0 2.0 -3.0]) [1.0 2.0]))
    (is (= (xy-intersection [1.0 0.0 1.0] [1.0 2.0 -3.0]) [4.0 2.0]))
    (is (= (xy-intersection [1.0 1.0 1.0] [1.0 2.0 -3.0]) [4.0 5.0]))
    (is (= (xy-intersection [1.0 -1.0 1.0] [1.0 2.0 -3.0]) [4.0 -1.0]))
    ))

(deftest reflected-ray-test
  (testing "reflected-ray fail."
    (is (= (reflected-ray [[0.0 0.0 0.0] [1.0 0.0 0.0] [0.0 1.0 0.0]])
           {:image [[0.0 0.0] [1.0 0.0] [0.0 1.0]] :light-coeff 1.0}))
    (is (= (reflected-ray [[0.0 0.0 0.0] [1.0 0.0 0.0] [0.0 1.0 -0.5]])
           {:image [[0.0 0.0] [1.0 0.0] [0.0 (/ 5.0 3.0)]]
            :light-coeff 0.6}))
    ))
