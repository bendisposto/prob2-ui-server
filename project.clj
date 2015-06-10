(defproject de.prob2 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :repositories [["cobra" "http://cobra.cs.uni-duesseldorf.de/artifactory/repo"]]

  :source-paths ["src/clj" "src/cljs"]

  :dependencies [[org.clojure/clojure "1.7.0-beta2" :scope "provided"]
                 [ring/ring-json "0.3.1"]
                 [com.stuartsierra/component "0.2.2"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-servlet "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [com.taoensso/sente "1.3.0"]
                 [http-kit "2.1.19"]
                 [prone "0.8.0"]
                 [compojure "1.3.1"]
                 [environ "1.0.0"]
                 [com.cognitect/transit-clj "0.8.259"]
                 [prismatic/schema "0.4.0"]
                 [org.clojure/test.check "0.7.0"]
                 [schema-gen "0.1.4"]]

  :plugins [
            [lein-environ "1.0.0"]
            [com.keminglabs/cljx "0.6.0"]


  ;;  :ring {:handler de.prob2.handler/app
  ;;         :uberwar-name "de.prob2.war"}

  :prep-tasks [["cljx" "once"] "javac" "compile"]

  :min-lein-version "2.5.0"

  :uberjar-name "de.prob2.jar"

  :main de.prob2.server

  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path "src/clj"
                   :rules :clj}]}

  :aliases {"autocjlx" ["with-profile", "+dev", "cljx", "auto"]}

  :profiles {:dev {:repl-options {:init-ns user
                                  :timeout 120000}
                   :jvm-opts ^:replace []

                   :dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.3.2"]
                                  [leiningen "2.5.1"]
                                  [org.clojure/test.check "0.7.0"]
                                  [pjstadig/humane-test-output "0.6.0"]
                                  [org.clojure/test.check "0.7.0"]
                                  [com.gfredericks/test.chuck "0.1.16"]
                                  [schema-gen "0.1.4"]]
                   :resource-paths ["kernel/de.prob2.kernel/build/libs/*.jar"]
                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-expand-resource-paths "0.0.1"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev? true}}

             :uberjar {:env {:production true}
                       :aot :all
                       :dependencies [[de.prob2/de.prob2.kernel "2.0.0-milestone-25-SNAPSHOT"]]
                       :omit-source true}

             :production {:ring {:open-browser? false
                                 :stacktraces?  false
                                 :auto-reload?  false}}})
