(ns kunya.ui-apricot)

;; COMPONENT#1 - static view
;;
;; This is a static view. No dynamic parts here.
;;
;; In reagent, we build DOM from components.
;; A component can be made from other sub components.
;;
;; There are three forms for reagent components.
;;   form-1: function which returns a hiccup vector
;;   form-2: function which returns a form-1-like function
;;   form-3: out of scope for this application
;;
;; When you compose a hiccup vector, you can place a component
;; at the first element of the vector.


;; here we define two form-1 components.
(defn- para
  [message]
  [:p {:style {:margin-top "8px"}} message])

(defn compo1-static []
  [:div
   [:h1.text-uppercase "have fun"]
   [:ul
    ;; feel free to use Clojure's ability
    ;; to compose hiccup vectors programmatically.
    (for [n ["ClojureScript"
             "Electron"
             "Boot"
             "Reagent"
             "re-frame"
             "Vim"
             "vim-fireplace"]]
      ^{:key n} [:li n])]
   ;;   :key metadata helps reagent identify a list item.

   [para "Hey, it's late."]  ;; notice it's a vector
   [para "Go to bed."]])      ;; not a function application.
