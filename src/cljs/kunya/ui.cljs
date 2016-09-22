(ns kunya.ui
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as r]))

;; アプリの初期状態。
;; すべての状態を1つのマップで管理する。
;; In-memory DBのイメージ。
(def initial-state
  {:timer (js/Date.)})

;; 定期的にイベントを発生させる。
;; re-frameのイベントは、単なるベクタ。
;; [:clicked]とか[:timer xxx]とか。
;; イベントハンドラへdispatchする。
(defonce time-updater (js/setInterval
                        #(r/dispatch [:ev-timer (js/Date.)]) 1000))

;; イベントとイベントハンドラの対応付け。
;; イベントハンドラの第1引数はアプリの状態(DB)。
;; 第2引数がイベント。
(r/reg-event-db
  :ev-initialize
  (fn
    [db _]
    (merge db initial-state)))

(r/reg-event-db
  :ev-timer
  (fn
    [db [_ value]]
    (assoc db :timer value)))

;; サブスクリプションIDとクエリの対応付け。
;; ある種の状態変化通知を購読(監視登録)するのがサブスクリプション。
;; その変化に対応してDOMを変化させる。
;; それに必要な情報をDBから取り出すのがクエリ。
;; クエリ関数の第1引数はアプリの状態(DB)。
;; 第2引数以降には、subscribe時に指定した引数が来る。
(r/reg-sub
  :sub-timer
  (fn
    [db & args]  ;; args[0] should be :sub-timer
    (:timer db)))

;; View(Reagentのコンポーネント)。
;; Reagentのコンポーネントは、
;; form-1: hiccupベクタを返す関数(greeting)
;; form-2: hiccupベクタを返す関数を返す関数(clock)
;; form-3: xxx
;; の3形態。
;; hiccupベクタの先頭要素に別のコンポーネントを指定してもいい。
(defn- greeting
  [message]
  [:p message])

(defn- clock
  []
  (let [timer (r/subscribe [:sub-timer :hoge :fuga])]
    ;;  ↑ここでsubscribeしてる。
    ;;  timerはatomライクなオブジェクト。
    ;;  derefすれば、:sub-timerに対応するクエリが返す値が手に入る。
    (fn clock-render
      []
      (let [time-str (-> @timer
                         .toTimeString
                         (clojure.string/split " ")
                         first)
            style {:style {:color "#f88"}}]
        [:h3.text-center.text-danger style time-str]))))

(defn- container
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
       ^{:key n} [:li n])]
    [greeting "Hey, it's late. Go to bed."]
    [clock]]])

;; ページの#appへ
;; Reagentのレンダリング結果を埋め込む。
(defn- run-reagent []
  (r/dispatch-sync [:ev-initialize])
  (reagent/render [container]
                  (js/document.getElementById "app")))

(defn init []
  (enable-console-print!)
  (println "The application has started.")
  (run-reagent))
