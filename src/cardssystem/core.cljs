(ns cardssystem.core
  (:require
   #_[om.core :as om :include-macros true]
   [sablono.core :as sab :include-macros true]
   [goog.dom :as gdom]
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defcard first-card
  (sab/html [:div
             [:h1 "This is your first devcard!"]]))

(defcard second-card
         (sab/html [:div
                    [:h1 "This is my second devcard!"]]))

(defui HelloWorld
       Object
       (render [this]
               (dom/div nil "Hello, world!")))

(def hello (om/factory HelloWorld))

;(js/ReactDOM.render (hello) (gdom/getElement "app"))

(defcard third-card (hello))

(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

