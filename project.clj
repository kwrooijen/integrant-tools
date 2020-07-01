(defproject kwrooijen/integrant-tools "0.3.7"
  :description "A library with helper functions, reader tags, and init-keys for Integrant"
  :url "https://github.com/kwrooijen/integrant-tools"
  :license {:name "MIT"}
  :dependencies [[integrant "0.8.0"]]
  :plugins [[lein-cloverage "1.0.13"]
            [lein-shell "0.5.0"]
            [lein-ancient "0.6.15"]
            [lein-changelog "0.3.2"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0"]]}}
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :username :env/clojars_user
                                    :password :env/clojars_pass
                                    :sign-releases false}]]
  :aliases {"update-readme-version" ["shell" "sed" "-i" "s/\\\\[integrant-tools \"[0-9.]*\"\\\\]/[integrant-tools \"${:version}\"]/" "README.md"]}
  :release-tasks [["shell" "git" "diff" "--exit-code"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["changelog" "release"]
                  ["update-readme-version"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]
                  ["vcs" "push"]])
