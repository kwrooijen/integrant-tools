(ns integrant-tools.edn-test
  (:require [clojure.test :refer [deftest testing is]]
            [integrant-tools.edn :as it.edn]))

(def config-str
  "#:lotr{:quote #it/str [\"Not all those who wander are lost.\"]}")

(def config-meta
  {:entity/legolas ^:elf {:name "Legolas" :age 2931}})

(deftest lazy-read-test
  (testing "Test that reading a config string (with reader tag) and writing it
            back to a string produces the same result."
    (is (= config-str
           (-> config-str
               (it.edn/lazy-read)
               (it.edn/meta-str)))))

  (testing "Test that metadata isn't lost after writing and reading data."
    (is (= {:elf true}
           (-> config-meta
               (it.edn/meta-str)
               (it.edn/lazy-read)
               :entity/legolas
               meta)))))
