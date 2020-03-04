[![Clojars Project](https://img.shields.io/clojars/v/integrant-tools.svg)](https://clojars.org/integrant-tools)

# integrant-tools

A library with helper functions, reader tags, and init-keys for
[Integrant](https://github.com/weavejester/integrant). These are things which I
didn't think would fit in core.

## Index

...[integrant-tools.core/readers](#integrant-tools.corereaders)
...[integrant-tools.core/derive-unknown](#integrant-tools.corederive-unknown)
...[integrant-tools.core/derive-hierarchy!](#integrant-tools.corederive-hierarchy!)
...[integrant-tools.core/meta-init](#integrant-tools.coremeta-init)
...[integrant-tools.edn/meta-str](#integrant-tools.ednmeta-str)
...[integrant-tools.edn/lazy-read](#integrant-tools.edn/lazy-read)

## integrant-tools.core/readers


### `it/regex`
  Convert a string to a regex

```clojure
{:regex/email? #ig/regex ".+\@.+\..+"
 ...}
```

### `it/str`
  Convert a collection of strings into a single string

```clojure
{:lotr/quote #ig/str
 ["One ring to rule them all,"
  "One ring to find them,"
  "One ring to bring them all and in the darkness bind them"]
 ...}
```

## integrant-tools.core/derive-unknown

([config multi-method new-key])

  Derives any keys in `config` that aren't implemented in `multi-method` with
  `new-key`. Any keys that are derived using this function will be returned in
  a vector.


## integrant-tools.core/derive-hierarchy!
([hierarchy])

  Derive keys using a hierarchy structure.

  For example:

  ```clojure
  (it/derive-hierarchy!
   {:entity/thranduil [:race/elf]
    :entity/legolas   [:race/elf]
    :entity/aragorn   [:race/human]})

  Is equivalent to calling:

  (derive :entity/thranduil :race/elf)
  (derive :entity/legolas   :race/elf)
  (derive :entity/aragorn   :race/human)
  ```


## integrant-tools.core/meta-init
([config] [config keys])

  Same as ig/init, but any metadata in a key's `opts` are merged into the
  resulting value after initialization. This is useful if your init-key returns
  a function, but you want to add extra context to it.

## integrant-tools.edn/meta-str
([config])

  Convert a lazily read EDN structure into a string, adding the meta data to
  the string as well.

## integrant-tools.edn/lazy-read
([config] [readers config])

  Reads an EDN string, but doesn't evaluate any readers tags except the ones
  supplied in `readers`. Instead of evaluating them they are converted to a map.
  This is useful if you want read multiple config files, merge them, and write
  them back to a string, without losing the reader tags.

  For example:

  ```clojure
  (it.edn/lazy-read "{:lotr/quote #it/str [...]}")
  ```
  Is read to:

  ```clojure
  {:lotr/quote {:reader/tag 'it/str :reader/value [...]}}
  ```

  Which can then later be written to a string using `meta-str`.

## License

Copyright © 2020 Kevin William van Rooijen

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
