[![Clojars Project](https://img.shields.io/clojars/v/integrant-tools.svg)](https://clojars.org/integrant-tools)

# integrant-tools

A library with helper functions, reader tags, and init-keys for
[Integrant](https://github.com/weavejester/integrant). These are things which I
didn't think would fit in core.

## Index

### Core

+ [integrant-tools.core/readers](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#readers)
+ [integrant-tools.core/derive-unknown](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#derive-unknown)
+ [integrant-tools.core/derive-hierarchy](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#derive-hierarchy)
+ [integrant-tools.core/derive-composite](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#derive-composite)
+ [integrant-tools.core/underive-all](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#underive-all)
+ [integrant-tools.core/find-derived-keys](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#find-derived-keys)
+ [integrant-tools.core/meta-init](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#meta-init)
+ [integrant-tools.core/find-derived-key](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#find-derived-key)
+ [integrant-tools.core/find-derived-values](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#find-derived-values)
+ [integrant-tools.core/find-derived-value](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#find-derived-value)
+ [integrant-tools.core/select-keys](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.core#select-keys)

### EDN

+ [integrant-tools.edn/meta-str](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.edn#meta-str)
+ [integrant-tools.edn/lazy-read](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.edn#lazy-read)

### Keyword

* [integrant-tools.keyword/ancestor?](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#ancestor?)
* [integrant-tools.keyword/descendant?](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#descendant?)
* [integrant-tools.keyword/parent?](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#parent?)
* [integrant-tools.keyword/child?](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#child?)
* [integrant-tools.keyword/child](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#child)
* [integrant-tools.keyword/parent](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#parent)
* [integrant-tools.keyword/ancestor](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#ancestor)
* [integrant-tools.keyword/descendant](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#descendant)
* [integrant-tools.keyword/children](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#children)
* [integrant-tools.keyword/underive-ancestors](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#underive-ancestors)
* [integrant-tools.keyword/underive-parents](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#underive-parents)
* [integrant-tools.keyword/underive-descendants](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#underive-descendants)
* [integrant-tools.keyword/underive-children](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#underive-children)
* [integrant-tools.keyword/make-child](https://cljdoc.org/d/integrant-tools/integrant-tools/0.3.3/api/integrant-tools.keyword#make-child)

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
