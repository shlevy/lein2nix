(defproject lein2nix "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [leiningen-core "2.5.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.eclipse.aether/aether-api "1.0.2.v20150114"]]
  :main ^:skip-aot lein2nix.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
