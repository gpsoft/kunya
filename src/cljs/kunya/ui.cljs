(ns kunya.ui
  (:require [reagent.core :as r]))

(defonce app-state (r/atom ""))

(comment
  (reset! app-state "World!")
  (reset! app-state "Clojure!")
  (reset! app-state "ClojureScript!"))

(defn- para []
  [:p (str "Hello " @app-state)])

(defn- run-reagent []
  (r/render [para]
            (js/document.getElementById "app")))

(defn init []
  (enable-console-print!)
  (println "The application has started.")
  (run-reagent))
