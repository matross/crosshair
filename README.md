# Crosshair

Namespaced immutable maps, for configuration.

## Usage
Crosshair is available on Clojars. To include in your project, simply add the following to your dependencies:

```clojure
[matross/crosshair "0.1.1"]
```

```clj
(require '[matross.crosshair :refer [crosshair]])

(def config (crosshair {
  :ns {:key 23}
  :default {:key 42}
}))

(:ns/key config)
; => 23

(:key other-config)
; => 42

;; behaves like a map, with special namespaced keys
(def other-config (assoc config :ns/key 1234))

(:ns/key other-config)
; => 1234
```

All top level keys become namespaces, and namespaced values can be referenced by `:ns/key`

Things in the `default` namespace can be referenced without an explicit namespace.

Copyright © 2014 FIXME
