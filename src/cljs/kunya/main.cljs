(ns kunya.main
  "Electron main process")

(def electron (js/require "electron"))
(def app (.-app electron))
(def browser-window (.-BrowserWindow electron))

;; Debug switch.
;; can be overridden by :closure-defines at compile time.
(goog-define dev? false)

(comment
  ;; an alternative to dev? switch is goog.DEBUG defined by
  ;; Google Closure Library.
  ;; however it's value is true by default and you have to
  ;; change it manually for release build.
  ;; David Nohlen said :closure-defines woks only when :optimizations
  ;; is :none. So we can't use it to change goog.DEBUG to false.
  ;; hmm... let's stick to dev? then.
  (.log js/console (if dev? "debug" "release"))
  (.log js/console (if ^boolean js/goog.DEBUG "DEBUG" "RELEASE")))

(def main-window (atom nil))

(defn- index-url
  "Url of index.html.
  relative path to the file varies
  according to optimization compiler option."
  []
  (let [p (if dev? "/../../.." "/..")]
    (str "file://" js/__dirname p "/index.html")))

(defn- new-window
  [width height & opts]
  (->> (apply hash-map opts)
       (merge {:width width :height height})
       clj->js
       browser-window.))

(defn- init-browser []
  (reset! main-window
          (new-window 800 600
                      :autoHideMenuBar true
                      :webPreferences {:nodeIntegration false}))
  ;;                  disabling nodeIntegration is for jQuery.
  ;;                  drawback: you can't use "require" on a renderer process.
  (.loadURL @main-window (index-url))
  (when dev?
    (.openDevTools @main-window #js {:mode "undocked"}))
  (.on @main-window "closed" #(reset! main-window nil)))

(defn init []
  (.on app "window-all-closed"
       #(when-not (= js/process.platform "darwin")
          (.quit app)))
  (.on app "ready" init-browser)
  (set! *main-cli-fn* (fn [] nil))) ;; see cljs.core/*main-cli-fn*
