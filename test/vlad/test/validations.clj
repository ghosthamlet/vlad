(ns vlad.test.validations
  (:use midje.sweet
        [vlad validations validation_types]))

(def data {:name "Chris"})

(tabular
  (fact (validate ?validator {:name "Chris"}) => ?errors)
    ?validator ?errors

    (present "Name" [:name])
       []                   

    (present "Age"  [:age])
       [{:type :vlad.validations/present
         :name "Age"
         :selector [:age]}] 

    (length-over 4 "Name" [:name])
       []                   

    (length-over 9 "Name" [:name])
       [{:type :vlad.validations/length-over
         :size 9
         :name "Name"
         :selector [:name]}] 

    (length-under 9 "Name" [:name])
       []                   

    (length-under 4 "Name" [:name])
       [{:type :vlad.validations/length-under
         :size 4
         :name "Name"
         :selector [:name]}] 

    (length-in 4 9 "Name" [:name])
       []                   

    (length-in 9 4 "Name" [:name])
       [{:type :vlad.validations/length-over
         :size 9
         :name "Name"
         :selector [:name]}
        {:type :vlad.validations/length-under
         :size 4
         :name "Name"
         :selector [:name]}])
