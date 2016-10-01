(ns kunya.ui-bamboo
  (:require [re-frame.core :as r]))

;; COMPONENT#2 - dynamic view
;;
;; Forward Flow: DB => DOM
;; View(DOM) reflects app state(DB).
;; When DB changes, DOM also changes.
;; re-frame calls the changes on DB "signals".
;; And components "subscribe" signals to reflect those changes
;; to DOM.
;;
;; Backward Flow: DB <= DOM
;; Events happen on DOM(user operations, timeouts, etc...).
;; Those events will cause changes on DB.
;; How DB should be changed? We declare it by defining event handlers.
;; Changes on DB re-start the forward flow.


;; an event in re-frame is just a vector.
;; the first element of the vector is event-id.
;; here we define an event and register the event handler.
;; note that the event handler is pure function.
(r/reg-event-db
  ;; "-db" implies that this is for DB manipulation.
  :ev-toggle-power
  (fn [db [_ power :as event-v]]
    ;; args are DB and event vector.
    (assoc db :speaker-power power))) ;; returns new DB.

;; when you are interested in changes at a particular part of DB
;; (in other words, signal), you subscribe it.
;; to subscribe a signal, you've got to define it first.
(r/reg-sub
  :sub-speaker-power  ;; signal-id(or subscription-id?)
  (fn [db query-v]    ;; subscription handler
    ;; args are DB and query vector.
    ;; the query vector is the argument to `subscribe`
    ;; which is usually used to retrieve values from DB(like SQL query).
    (:speaker-power db)))

;; Components.
(defn- power-button []
  [:div.checkbox
   [:label
    [:input
     {:type "checkbox"
      :value "1"
      :on-change #(r/dispatch [:ev-toggle-power (.-target.checked %)])}]
    "POWER"]])

;; this is a form-2 component.
(defn- speaker []
  (let [power (r/subscribe [:sub-speaker-power])]
    ;;         here we subscribe a signal.
    ;;         and name it "power".
    ;;         its an atom-like object which you can deref.
    ;;         and when you do, you'll get what the subscription
    ;;         handler returns.
    (fn []
      (let [power-str (str "Speaker is "
                           (if @power "ON" "OFF"))
            class-str (if @power "on" "off")]
        [:div.form-inline
         [:div.form-group.power-status
          {:class class-str}
          power-str]
         [power-button]]))))

(defn compo2-dynamic []
  (r/dispatch [:ev-toggle-power false])
  (let [ready? (r/subscribe [:sub-ready?])]
    (when @ready?
      [speaker])))
