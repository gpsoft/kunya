(ns kunya.ui
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as r]))

(defonce time-updater (js/setInterval
                        #(r/dispatch [:timer (js/Date.)]) 1000))

(def initial-state
  {:timer (js/Date.)})

(r/reg-event-db
  :initialize
  (fn
    [db _]
    (merge db initial-state)))

(r/reg-event-db
  :timer
  (fn
    [db [_ value]]
    (assoc db :timer value)))

(r/reg-sub
  :timer
  (fn
    [db _]
    (:timer db)))

(defn greeting
  [message]
  [:p message])

(defn clock
  []
  (let [timer (r/subscribe [:timer])]
    (fn clock-render
      []
      (let [time-str (-> @timer
                         .toTimeString
                         (clojure.string/split " ")
                         first)
            style {:style {:color "#f88"}}]
        [:h3.text-center.text-danger style time-str]))))

(defn container
  []
  [:div.container
   [:div.well
    [:h1.text-uppercase "have fun"]
    [:ul
     (for [n ["ClojureScript"
              "Electron"
              "Boot"
              "Reagent"
              "re-frame"
              "Vim"
              "vim-fireplace"]]
       [:li n])]
    [greeting "Hey, it's late. Go to bed."]
    [clock]]])

(defn- run-reagent []
  (r/dispatch-sync [:initialize])
  (reagent/render [container]
                  (js/document.getElementById "app")))

(defn init []
  (enable-console-print!)
  (println "The application has started.")
  (run-reagent))
