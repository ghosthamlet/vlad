# vlad

Vlad is an attempt at providing convenient and simple validations. Vlad is
purely functional. It makes no assumptions about your data. It can be used for
validating html post data just as well as it can be used to validate your
csv about cats.

Those coming from a Ruby on Rails background will be famliar with validations
poluted with conditional statements and models poluted with virtual attributes.
Vlad aims to avoid this mess.

To use vlad add the following to your `project.clj` dependencies:

    [vlad "1.0.0"]

API Docs: <http://logaan.github.io/vlad/vlad.html>

## Basics

```clojure
(ns vlad.test.readme
  (:require [vlad :refer :all]))

(midje/fact
  (validate (present [:age]) {:name "Logan"})
  => [{:type :vlad.validations/present
       :selector [:age]}])
```

## Composition

Vlad lets you build complex validations through composition. `join` will return
a validation that checks each of it's arguments. `chain` will stop checking
once the first validation fails. This helps avoid overwhelming your users with
redundant error messages.

```clojure
(def common
  (join (present [:name])
        (present [:email])))

(def password
  (chain (present [:password])
         (join (length-in 6 128 [:password])
               (matches #"[a-zA-Z]" [:password])
               (matches #"[0-9]" [:password]))
         (equals-field [:password] [:confirmation])))

(def signup
  (join common password))

(def update
  common)
```

And of course all these validations could be run over any data. Whether you're
pulling it in from a web service, a database or a csv file somewhere.

## Translation

Vlad is an exercise in extreme simpicity. This means you can use validations in
any number of ways. Because errors are not coupled to messages vlad is well
suited for localisation. Default english translations are provided for your
convenience.

```clojure
(def english-field-names
  {[:name]         "Full Name"
   [:email]        "Email Address"
   [:password]     "Password"
   [:confirmation] "Password Confirmation"})

(midje/fact
  (-> (validate signup {:password "!"})
      (assign-name english-field-names)
      (translate-errors english-translation))

  => {[:password] ["Password must be over 6 characters long."
                   "Password must match the pattern [a-zA-Z]."
                   "Password must match the pattern [0-9]."],
      [:email]    ["Email Address is required."],
      [:name]     ["Full Name is required."]})

(def chinese-field-names
  {[:name]         "姓名"
   [:email]        "邮箱"
   [:password]     "密码"
   [:confirmation] "确认密码"})

(defmulti chinese-translation :type)

(defmethod chinese-translation :vlad.validations/present
  [{:keys [name]}]
  (format "请输入%s" name))

; Other validation translations go here.

(midje/fact
  (-> (validate update {:name "Rich"})
      (assign-name chinese-field-names)
      (translate-errors chinese-translation))

  => {[:email] ["请输入邮箱"]})
```

## Alternatives

There are 7 validation libraries up on [Clojure Toolbox]
(http://www.clojure-toolbox.com/). I believe vlad to be the simplest of the
lot. It differs from most other's in the following ways:

* Validations can be composed. Fail fast composition (`chain` in vlad) is
  missing from all libraries I saw. This runs the risk of presenting your users
  with a page full of redundant errors.
* Validations are not tied to any particular data type. They just need to
  implement the `vlad.validation-types/Validation` protocol. A default
  implementation has been made for `clojure.lang.IFn`.
* Neither field names nor error messages are tied to validations. You can
  define your validation rules once and then translate them in many different
  ways. Or not at all if you choose.
* Vlad is not tied to the web. It is suitable for more than just form data
  processing. It is also not tied to any web framework.

## License

Copyright © 2012 Logan Campbell

Distributed under the Eclipse Public License, the same as Clojure.

