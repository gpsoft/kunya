(ns kunya.main
  "Electron main process")

(def electron (js/require "electron"))
(def app (.-app electron))
(def browser-window (.-BrowserWindow electron))

;; Debug switch
;; Can be overridden by :closure-defines at compile time.
(goog-define dev? false)

(def main-window (atom nil))

(defn- index-url
  "Url of index.html which varies
  according to optimization compiler option."
  []
  (let [p (if dev? "/../.." "")]
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
