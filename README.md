# Scallion: a configuration tool

`environ` is a great package, but I needed something that would work
pretty much like it, but would consult an `etcd` server for a value
before consulting environment variables, etc.

## Installation

Add the following dependency to your `project.clj`:

```clojure
[scallion "0.2.0"]
```

## Usage

```clojure
(require '[scallion.core :refer [env env*]])

(env :path)
;; Or...
(env* :path1 :path2 :path3 ...)
```

There may be a slight delay the first time your code fetches a value
using the `env` function, because Scallion checks to see if an `etcd`
server is running on either `127.0.0.1` or `172.17.42.1`--or both. The
first is the address on which a local `etcd` server will be running
whereas the latter is the default IP address from which `etcd` is
accessible to processes running inside Docker container under CoreOS.

Scallion will consult an `etcd` instance running on `127.0.0.1` before
one running on `172.17.42.1`, the rationale being that if both are
present, it's running locally for a reason.

Scallion will, if `etcd` is not available, it will look inside
`environ.core/env`. Note that `environ.core/env` is a map, whereas
`scallion.core/env` is a function. Also, because the issue of
embedding security keys and so forth in source code is such a serious
one, there is no two-argument form of the function, making it more
difficult for programmers to embed default values in source code.

Since Scallion consults a web service for each key value, you should
not call it `env` repeatedly for the same key. It is in deference to
the reality of the non-trivial cost of calling `env` that the `env*`
function exists. I recommend invoking `env*` once with all of the keys
containing all of the external configuration options your code
requires--in one, documented place--and consider assigning the
returned value to an atom or a dynamically-scoped variable. The
rationale for doing this is to allow you to easily re-load
configuration information from a REPL during testing.

## License

Copyright © 2013 Edwin Watkeys

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
