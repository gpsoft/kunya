(set-env!
  :source-paths #{"src/cljs"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [org.clojure/clojurescript "1.9.229"]
                  [adzerk/boot-cljs "1.7.228-1"]
                  [adzerk/boot-reload "0.4.12"]
                  [adzerk/boot-cljs-repl "0.3.3"]
                  [org.clojure/tools.nrepl "0.2.11"]
                  [com.cemerick/piggieback "0.2.1"]
                  [weasel "0.7.0"]
                  [reagent "0.6.0"]
                  [re-frame "0.8.0"]])

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
  '[adzerk.boot-reload :refer [reload]])

(deftask release []
  (comp (cljs :ids #{"main"}
              :optimizations :simple)
        (cljs :ids #{"ui"}
              :optimizations :advanced)
        (target :dir #{"release"})))

(deftask dev []
  (comp 
        (cljs :ids #{"main"}
              :compiler-options {:asset-path "target/main.out"
                                 :closure-defines {'kunya.main/dev? true}})
        (watch)
        (cljs-repl :ids #{"ui"}
                   ;; if you want to fix the port...
                   ;; :nrepl-opts {:port 34567}
                   )
        (reload :ids #{"ui"}
                :ws-host  "127.0.0.1"
                :on-jsload 'kunya.ui/init)
        (cljs :ids #{"ui"})
        (target :dir #{"target"})))
