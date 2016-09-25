(ns kunya.ui
  (:require [reagent.core :as reagent]
            [re-frame.core :as r]
            [kunya.ui-apricot :refer [compo1-static]]
            [kunya.ui-bamboo :refer [compo2-dynamic]]
            [kunya.ui-pine :refer [compo3-impure]]))

;; Initial application state.
;; Whole application state is in one place: a map.
;; Think is as an in-memory DB.
(def initial-state
  {
   ;; key-value pairs
   })

;; register a re-frame event
;; here we associate event-id to event-handler.
;; see more on compo2-dynamic.
(r/reg-event-db
  :ev-initialize
  (fn
    [db _]
    (merge db initial-state)))

;; define a reagent component
;; a component can be in various forms,
;; but eventually it creates a hiccup vector which represents a DOM element.
;; more comes in compo1-static.
(defn- container
  []
  [:div.container
   [:div.well
    [compo1-static]
    [compo2-dynamic]
    [compo3-impure]]])

;; - dispatch an event to initialize state.
;; - have reagent render the "container" component
;;   and embed it into DOM(#app).
(defn- run-reagent []
  (r/dispatch-sync [:ev-initialize])
  (reagent/render [container]
                  (js/document.getElementById "app")))

(defn init []
  (enable-console-print!)
  (println "The application has started.")
  (run-reagent))
