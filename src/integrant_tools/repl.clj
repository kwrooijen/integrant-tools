(ns integrant-tools.repl
  (:require
   [clojure.tools.namespace.repl :as repl]
   [integrant-tools.core :as it]
   [integrant.core :as ig]
   [integrant.repl]
   [integrant.repl.state :as state]))

(defn- build-system [build wrap-ex]
  (try
    (build)
    (catch clojure.lang.ExceptionInfo ex
      (when-let [system (:system (ex-data ex))]
        (try
          (ig/halt! system)
          (catch clojure.lang.ExceptionInfo halt-ex
            (throw (wrap-ex ex halt-ex)))))
      (throw ex))))

(defn- meta-opts-init-system [config keys]
  (build-system
   (if keys
     #(it/meta-opts-init config keys)
     #(it/meta-opts-init config))
   #(ex-info "Config failed to init; also failed to halt failed system"
             {:init-exception %1}
             %2)))

(defn- meta-opts-resume-system [config system]
  (build-system
   #(it/meta-opts-resume config system)
   #(ex-info "Config failed to resume; also failed to halt failed system"
             {:resume-exception %1}
             %2)))

(defn meta-opts-resume []
  (if-let [prep state/preparer]
    (let [cfg (prep)]
      (alter-var-root #'state/config (constantly cfg))
      (alter-var-root #'state/system (fn [sys]
                                       (if sys
                                         (meta-opts-resume-system cfg sys)
                                         (meta-opts-init-system cfg nil))))
      :resumed)
    (throw (#'integrant.repl/prep-error))))

(defn meta-opts-init
  ([] (meta-opts-init nil))
  ([keys]
   (alter-var-root #'state/system
                   (fn [sys]
                     (#'integrant.repl/halt-system sys)
                     (meta-opts-init-system state/config keys)))
   :initiated))

(defn meta-opts-go
  ([] (meta-opts-go nil))
  ([keys]
   (integrant.repl/prep)
   (meta-opts-init keys)))

(defn meta-opts-reset []
  (integrant.repl/suspend)
  (repl/refresh :after 'integrant-tools.repl/meta-opts-resume))
