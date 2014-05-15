# configuration-map

namespaced, templated map, for configuration

## Usage

```clj
(use '[matross.config-map :only [config-map]])

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

;; top level keys become configuration namespaces
(:script config) ; => "/home/user/bin/script.sh"
(:system/hostname config) ; => "box"
```

Copyright © 2014 FIXME
