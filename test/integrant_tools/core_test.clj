(ns integrant-tools.core-test
  (:require [clojure.test :refer :all]
            [integrant-tools.core :as it]
            [integrant.core :as ig]
            [clojure.edn :as edn]))

(defmethod ig/init-key :entity/legolas [_ opts] opts)

(defmethod ig/init-key :entity/thranduil [_ opts] opts)

(defmethod ig/init-key :entity/aragorn [_ opts] opts)

(defmethod ig/init-key :component/html [_ opts]
  (fn []
    (:return/value opts)))

(defmethod ig/init-key :component/number [_ opts]
  (:return/value opts))

(def config-1
  {:entity/legolas
   {:name (ig/ref :legolas/name)
    :age (ig/ref :legolas/age)}
   [:entity/name :legolas/name] "Legolas"
   [:entity/age :legolas/age] 2931

   :entity/thranduil
   {:name (ig/ref :thranduil/name)
    :age (ig/ref :thranduil/age)}
   [:entity/name :thranduil/name] "Thranduil"
   [:entity/age :thranduil/age] 6000

   :entity/aragorn
   {:name (ig/ref :aragorn/name)
    :age (ig/ref :aragorn/age)}

   [:entity/name :aragorn/name] "Aragorn"
   [:entity/age :aragorn/age] 87})

(def config-2
  {[:component/html :component/with-meta]
   ^{:type :reagent}
   {:return/value [:div "Html!"]}

   [:component/html :component/without-meta]
   {:return/value [:div "Html!"]}

   [:component/number :component/non-IMeta]
   ^{:type :reagent}
   {:return/value 1}})

(def config-hierarchy-1
  {:entity/name [:it/const]
   :entity/age [:it/const]})

(def config-hierarchy-2
  {:entity/thranduil [:race/elf]
   :entity/legolas [:race/elf]
   :entity/aragorn [:race/human]
   :entity/name [:it/const]
   :entity/age [:it/const]})

(def it-str-reader-config
  "{:lotr/quote #it/str [\"It's a dangerous business, \"
                         \"walking out one's front door.\"]}")

(def it-regex-reader-config
  "{:lotr/quote #it/regex \"^Legolas$\" }")

(deftest readers-test
  (testing "Test #it/str"
    (is (= {:lotr/quote "It's a dangerous business, walking out one's front door."}
           (edn/read-string {:readers it/readers} it-str-reader-config))))

  (testing "Test #it/regex"
    (is (-> (edn/read-string {:readers it/readers} it-regex-reader-config)
            (get :lotr/quote)
            (re-matches "Legolas")))))

(deftest derive-unknown-test
  (testing "Derive from const to always return opts"
    (it/derive-unknown config-1 ig/init-key :it/const)
    (is (= 2931 (-> config-1 (ig/init) :entity/legolas :age)))
    (underive :entity/name :it/const)
    (underive :entity/age :it/const)))

(deftest derive-hierarchy-test
  (testing "Use derive-hierarchy! to specify which keys should derive from const"
    (it/derive-hierarchy! config-hierarchy-1)
    (is (= 2931 (-> config-1 (ig/init [:entity/legolas]) :entity/legolas :age))))

  (testing "Use derive-hierarchy! to create groups"
    (it/derive-hierarchy! config-hierarchy-2)
    (is (some? (-> config-1 (ig/init [:race/elf]) :entity/legolas)))
    (is (some? (-> config-1 (ig/init [:race/elf]) :entity/thranduil)))
    (is (nil? (-> config-1 (ig/init [:race/elf]) :entity/aragorn)))

    (is (nil? (-> config-1 (ig/init [:race/human]) :entity/legolas)))
    (is (nil? (-> config-1 (ig/init [:race/human]) :entity/thranduil)))
    (is (some? (-> config-1 (ig/init [:race/human]) :entity/aragorn)))))

(deftest meta-init-test
  (testing "Check that the output inherits metadata"
    (is (= {:type :reagent} (-> config-2 (it/meta-init) (get [:component/html :component/with-meta]) meta)))
    (is (fn? (-> config-2 (it/meta-init) (get [:component/html :component/with-meta])))))

  (testing "Check that the output doesn't need metadata"
    (is (nil? (-> config-2 (it/meta-init) (get [:component/html :component/without-meta]) meta)))
    (is (fn? (-> config-2 (it/meta-init) (get [:component/html :component/without-meta])))))

  (testing "Ignore metadata if a non IMeta value is returned"
    (is (nil? (-> config-2 (it/meta-init) (get [:component/number :component/non-IMeta]) meta)))
    (is (integer? (-> config-2 (it/meta-init) (get [:component/number :component/non-IMeta]))))))
