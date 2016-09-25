(ns kunya.ui-pine
  (:require [re-frame.core :as r]))

;; COMPONENT#3 - impure event handling
;;
;; Event handlers mission is to compute next app state
;; from their arguments(current state and a event).
;; However handler sometimes want to make side effects
;; (such as playing sound, writing files) or need other source
;; (such as random value, system clock) to compute next state.
;;
;; After all we can't live without impureness:
;;   - make side effects
;;   - break referential transparency
;; re-frame calls them "effects" and "coeffects" respectively
;; and abbreviates "effect" to "fx".
;;
;; re-frame provides a way to handle impureness while keeping
;; event handlers pure.
;;   - interceptors ...do something before/after event handlers
;;   - reg-event-fx ...event handler's arg is not just db, but whole cofx
;;   - reg-fx ...define and register an effect
;;   - reg-cofx ...define and register a coeffect
;;   - inject-cofx ...generate an interceptor which injects cofx before event handler
;;
;; Now event handlers remain pure, can have cofxs injected by interceptors,
;; and their mission is to compute all effects including next app state.


;; generate events periodically
(defonce time-updater (js/setInterval
                        #(r/dispatch [:ev-timer]) 1000))

;; define&register fx which consists of id and hander.
;; the fx handler makes side effect.
;; when an event handler returned the fx, the silently registered
;; interceptor(namely "do-fx") callbacks the fx handler.
(r/reg-fx
  :beep
  (fn [val]
    (println "pi-pi")))

;; define&register cofx which consists of id and handler.
;; the handler computes cofx value.
;; cofx will be used by inject-cofx to generate an interceptor.
;; then the interceptor delivers the cofx value to the event handler.
(r/reg-cofx
  :current-datetime
  (fn [cofxs opt]
    ;; args are a map of cofxs and optional value to which inject-cofx was applied.
    (assoc cofxs :current-datetime (js/Date.))))

;; define&register event which consists of id, a vector of interceptors(optional),
;; and event handler.
(r/reg-event-fx
  :ev-timer
  [(r/inject-cofx :current-datetime)]
  (fn [{:keys [db current-datetime] :as cofxs} event-v]
    ;; args are a map of cofxs and event.
    ;; :db is automatically injected by re-frame.
    ;; :current-datetime is done by the interceptor that inject-cofx generated above.
    (let [fxs {:db (assoc db :clock current-datetime)}
          power (:speaker-power db)]
      (if power (assoc fxs :beep nil) fxs)))) ;; returns fxs to be made(by re-frame).

;; subscription
(r/reg-sub
  :sub-clock
  (fn [db query-v]
    (:clock db)))

;; component
(defn- clock []
  (let [datetime (r/subscribe [:sub-clock])]
    (fn []
      (when (and datetime @datetime)
        (let [time-str (-> @datetime
                           .toTimeString
                           (clojure.string/split " ")
                           first)
              style {:style {:color "#f88"}}]
          [:h3.text-center.text-danger style time-str])))))

(defn compo3-impure []
    [clock])
