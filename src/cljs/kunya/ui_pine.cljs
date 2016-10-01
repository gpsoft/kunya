(ns kunya.ui-pine
  (:require [re-frame.core :as r]))

;; COMPONENT#3 - impure event handling
;;
;; Event handlers mission is to compute next app state
;; from their arguments(current state and an event).
;; However handlers sometimes want to make side effects
;; (such as playing sound, writing files) or need other source
;; (such as file system, clock) to compute next state.
;;
;; After all, we can't live without impureness:
;;   - make side effects
;;   - use side causes(break referential transparency)
;; re-frame calls them "effects" and "coeffects" respectively
;; and abbreviates "effect" to "fx", "coeffects" to "cofx".
;;
;; re-frame provides a way to handle impureness while keeping
;; event handlers pure.
;;   - `reg-fx`   ...define and register an effect
;;   - `reg-cofx` ...define and register a coeffect
;;   - `reg-event-fx`
;;                ...similar to `reg-event-db`, however,
;;                   event handler's arg is not just db, but whole cofx
;;   - interceptors
;;                ...do something before/after event handlers
;;                   "something" include injecting cofx into event handlers'
;;                   arg or making fx happened
;;   - `inject-cofx`
;;                ...generate an interceptor which injects cofx before
;;                   event handler
;;
;; Now event handlers remain pure, can have cofxs injected by interceptors,
;; and their mission is to compute all effects including next app state.
;; Finally re-frame's hidden interceptor `do-fx` takes care of making all
;; effects happened.


;; Generate events periodically.
(defonce time-updater (js/setInterval
                        #(r/dispatch [:ev-timer]) 1000))

;; Define&register fx which consists of id and hander.
;; the fx handler makes side effect.
;; when an event handler returned the fx, the silently registered
;; interceptor(namely `do-fx`) callbacks the fx handler.
(r/reg-fx
  :beep
  (fn [val]
    (println "pi-pi")))

;; Define&register cofx which consists of id and handler.
;; the handler computes cofx value.
;; cofx will be used by `inject-cofx` to generate an interceptor.
;; then the interceptor delivers the cofx value to the event handler.
(r/reg-cofx
  :current-datetime
  (fn [cofxs opt]
    ;; args are a map of cofxs and optional value to which
    ;; `inject-cofx` was applied.
    (assoc cofxs :current-datetime (js/Date.))))

;; Define&register event which consists of id, a vector of
;; interceptors(optional), and event handler.
(r/reg-event-fx
  :ev-timer
  [(r/inject-cofx :current-datetime)]
  (fn [{:keys [db current-datetime] :as cofxs} event-v]
    ;; args are a map of cofxs and event.
    ;; :db is automatically injected by re-frame.
    ;; :current-datetime is done by the interceptor that `inject-cofx`
    ;; generated above.
    (let [fxs {:db (assoc db :clock current-datetime)}
          power (:speaker-power db)]
      (if power (assoc fxs :beep nil) fxs)))
  ;; the handler returns fxs which should be made(by re-frame).
  )

;; Subscriptions.
(r/reg-sub
  :sub-clock
  (fn [db query-v]
    (:clock db)))

(defn- dt->str [dt]
  (-> dt
      .toTimeString
      (clojure.string/split " ")
      first)
  )

;; Components.
(defn- clock []
  (let [datetime (r/subscribe [:sub-clock])]
    (fn []
      (let [time-str (dt->str @datetime)]
        [:h3.text-center.text-danger.clock
         time-str]))))

(defn compo3-impure []
  (r/dispatch [:ev-timer])
  (let [ready? (r/subscribe [:sub-ready?])]
    (when @ready?
      [clock])))
