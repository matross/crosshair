# config-map

templated & namespaced immutable maps, for configuration

## Usage

```clj
(require '[matross.config-map :refer [config-map]])

(def config (config-map {
  :system {
    :hostname "box"
    :home "/home/user"
  }
  :default {
    ;; string values get templated across namespaces
    :bin "{{ system/home }}/bin"
    :script "{{ bin }}/script.sh"
  }
}))

(def other-config (assoc config :system/home "/home/other"
                                :system/hostname "other-box"))

;; top level keys become configuration namespaces
(:script other-config) ; => "/home/other/bin/script.sh"
(:script config)       ; => "/home/user/bin/script.sh"

(:system/hostname config)       ; => "box"
(:system/hostname other-config) ; => "other-box"
```

Copyright © 2014 FIXME
