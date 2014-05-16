# crosshair

templated & namespaced immutable maps, for configuration

## Usage

```clj
(require '[matross.crosshair :refer [crosshair]])

(def config (crosshair {
  :system {
    :hostname "box"
    :home "/home/user"
  }
  :default {
    :bin "{{ system/home }}/bin"
    :script "{{ bin }}/script.sh"
  }
}))

(def other-config
  (assoc config :system/home     "/home/other"
                :system/hostname "other-box"))

;; top level keys become configuration namespaces

(:script other-config)          ; => "/home/other/bin/script.sh"
(:system/hostname other-config) ; => "other-box"

;; immutable maps under the hood, original reference remains unchanged

(:script config)          ; => "/home/user/bin/script.sh"
(:system/hostname config) ; => "box"
```

All top level keys become namespaces, and namespaced values can be referenced by `:ns/key`

Things in the `default` namespace can be referenced without an explicit namespace.

Copyright © 2014 FIXME
