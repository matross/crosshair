(ns matross.config-map-test
  (:require [clojure.test :refer :all]
            [matross.config-map :refer :all])
  (:import clojure.lang.MapEntry))

(deftest config-resolver-behaves
  (testing "I can retrieve values"
    (let [cr (config-map {:ns {:kwd "foo"}})
          kwd :ns/kwd]
      (is (= (kwd cr) "foo"))
      (is (not (= (kwd cr) (:kwd cr))))))

  (testing "can assoc values"
    (let [cr (config-map {:ns {:kwd "foo"}})
          kwd :ns/kwd]
      (is (= cr (assoc cr kwd "foo")))
      (is (= "bar" (kwd (assoc cr kwd "bar"))))))

  (testing "can dissoc values"
    (let [cr (config-map {:ns {:kwd "foo" :foo "kwd"}})
          or (config-map {:ns {:kwd "foo"}})]
      (is (= or (dissoc cr :ns/foo)))))

  (testing "can check contains"
    (let [cr (config-map {:ns {:kwd "foo"}} "/" "ns")]
      (is (contains? cr :ns/kwd))
      (is (contains? cr :kwd))
      (is (not (contains? cr :ns)))))

  (testing "can retrieve entries"
    (let [cr (config-map {:ns {:kwd "foo"}} "/" "ns")]
      (is (instance? MapEntry (.entryAt cr :ns/kwd)))
      (is (nil? (.entryAt cr :missing)))))

  (testing "Templating occurs properly"
    (let [cr (config-map {:test {:foo "{{fact/bar}}" :baz "{{foo}}"}
                          :fact {:bar "herp"}} "/" "test")]
      (is (= (:foo cr) (:fact/bar cr)))
      (is (= (:foo cr) (:baz cr)))))

  (testing "Config resolver is iterable"
    (let [cr (config-map {:user {:foo "bar"}})
          s (seq cr)]
      (is (= (count s) 1)))))
