(set-env!
  :source-paths #{"src/cljs"}    ;; compiled by cljs task
  :resource-paths #{"resources"} ;; copied to the target dir by target task
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
  '[adzerk.boot-cljs-repl :refer [cljs-repl]]
  '[adzerk.boot-reload :refer [reload]])

;; ABOUT :ids OPTION AND MULTIPLE BUILD
;; :ids option chooses *.cljs.edn files for multiple build.
;; for example:
;;  - edn file => resources/js/ui.cljs.edn
;;  - option => :ids #{"js/ui"}
;;  - then compiler creates js/ui.js(entry point) and js/ui.out/*
;; we need at least two entry points:
;;  - one for electron main process kicked by nodejs
;;  - one for main window loaded from index.html
;; also depending on the location of entry point, we need to adjust:
;;  - :asset-path compiler option ... for debug build only
;;  - package.json ... relative path for main.js
;;  - index.html ... relative path for ui.js
;;  - main.cljs ... url for index.html
(deftask release
  "Release build."
  []
  (comp (cljs :ids #{"js/main"}
              :optimizations :simple)
        (cljs :ids #{"js/ui"}
              :optimizations :advanced)
        (target :dir #{"release"})))

(deftask build-main
  "Build main entry point for debug."
  []
  (comp (cljs :ids #{"js/main"}
              :compiler-options {:closure-defines {'kunya.main/dev? true}
                                 :asset-path "target/js/main.out"})))

(deftask build-ui
  "Build ui entry point for debug."
  []
  (comp (cljs :ids #{"js/ui"}
              :compiler-options {:asset-path "js/ui.out"})))

(deftask debug
  "Debug build."
  []
  (comp (build-main)
        (build-ui)
        (target :dir #{"target"})))

(deftask dev
  "Build and setup development environment."
  []
  (comp (build-main)
        (watch)
        (cljs-repl :ids #{"js/ui"}
                   ;; if you want to fix the port...
                   ;; :nrepl-opts {:port 34567}
                   )
        (reload :ids #{"js/ui"}
                :ws-host  "127.0.0.1"
                :on-jsload 'kunya.ui/init)
        (build-ui)
        (target :dir #{"target"})))
